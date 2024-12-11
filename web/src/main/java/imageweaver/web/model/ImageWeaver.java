package imageweaver.web.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import imageweaver.web.service.ImageService;
import lombok.Getter;

public class ImageWeaver {
	@Getter
	private List<Post> posts;

	@Getter
	private Pallette pallette;

	@Getter
	private List<Post> postSequence;

	private BufferedImage targetImage;

	private final int diameter;

	public ImageWeaver(int numberOfPosts, BufferedImage image, int diameter) {
		super();
		this.diameter = diameter;
		this.postSequence = new ArrayList<>();

		// Create the target image
		this.targetImage = createTargetImage(image);

		// Calculate the frame bounds
		double imageRadius = ((double) this.targetImage.getWidth()) / 2;
		double frameRadius = imageRadius - 20;

		// Create the pallette
		this.pallette = new Pallette(this.targetImage.getWidth(), this.targetImage.getHeight());

		// Create the posts
		this.posts = new ArrayList<>();
		double sliceDegrees = 360.0 / numberOfPosts;
		for (int i = 0; i < numberOfPosts; i++) {
			double degrees = sliceDegrees * i;
			double radians = Math.toRadians(degrees);

			// Find the point relative to the center
			double x = frameRadius * Math.cos(radians);
			double y = frameRadius * Math.sin(radians);

			// Translate to a point relative to top-left
			x += imageRadius;
			y += imageRadius;

			posts.add(new Post((int) Math.round(x), (int) Math.round(y)));
		}
	}

	/**
	 * Calculates the accuracy of the change of pixel values. The higher the number,
	 * the more accurate.
	 *
	 * @param pixelValues - the list of new pixel values
	 * @return the accuracy change
	 */
	private int calculateAccuracyChange(List<PixelValue> pixelValues) {
		int accuracy = 0;
		for (PixelValue pixelValue : pixelValues) {
			int targetGrayscaleValue = targetImage.getRGB(pixelValue.getX(), pixelValue.getY()) & 0xFF;
			accuracy += Math.abs(pixelValue.getGrayscaleValue() - targetGrayscaleValue);
		}
		return accuracy;
	}

	private BufferedImage createTargetImage(BufferedImage image) {
		// Crop the image to be square
		BufferedImage croppedImage = ImageService.cropToSquare(image);

		// Convert to grayscale
		BufferedImage result = ImageService.convertToGrayscale(croppedImage, diameter, diameter);

		return result;
	}

	public BufferedImage getWeavedImage() {
		BufferedImage image = new BufferedImage(targetImage.getWidth(), targetImage.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		// White background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, targetImage.getWidth(), targetImage.getHeight());

		// Draw posts
		g.setColor(Color.RED);
		for (Post post : posts) {
			g.fillOval(post.getLocation().x - 5, post.getLocation().y - 5, 10, 10);
		}

		// Draw lines
		g.setColor(Color.BLACK);
		for (int i = 1; i < postSequence.size(); i++) {
			Post fromPost = postSequence.get(i - 1);
			Post toPost = postSequence.get(i);
			g.drawLine(fromPost.getLocation().x, fromPost.getLocation().y, toPost.getLocation().x,
					toPost.getLocation().y);
		}

		g.dispose();
		return image;
	}

	public void weave(int maxNumberOfLines) {
		// Clear if necessary
		if (!this.postSequence.isEmpty()) {
			this.postSequence.clear();
			this.pallette = new Pallette(this.targetImage.getWidth(), this.targetImage.getHeight());
		}

		// Start at the first post
		postSequence.add(posts.get(0));

		while (true) {
			System.out.println(postSequence.size());
			int bestAccuracyChange = Integer.MIN_VALUE;
			Post bestPost = null;
			Pallette bestPallette = null;
			for (Post post : posts.stream().filter(p -> !p.equals(postSequence.getLast())).toList()) {
				Pallette testPallette = this.pallette.clone();

				// Draw a line on the testPallette
				Line line = new Line(postSequence.getLast().getLocation(), post.getLocation());
				List<PixelValue> pixelValues = testPallette.drawLine(line);
				int testAccuracyChange = calculateAccuracyChange(pixelValues);
				if (testAccuracyChange > 0 && testAccuracyChange > bestAccuracyChange) {
					bestAccuracyChange = testAccuracyChange;
					bestPost = post;
					bestPallette = testPallette;
				}
			}

			if (bestPost == null) {
				// We could not improve the accuracy
				break;
			}

			// Commit the post to the sequence
			postSequence.add(bestPost);
			this.pallette = bestPallette;

			if (postSequence.size() == maxNumberOfLines) {
				break;
			}
		}
	}
}
