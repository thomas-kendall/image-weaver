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
	private List<Post> postSequence;

	private BufferedImage targetImage;

	private final int diameter;

	private final int maxNumberOfLines;
	private final int lineFactor;

	@Getter
	private BufferedImage weavedImage;

	public ImageWeaver(BufferedImage image, int numberOfPosts, int diameter, int maxNumberOfLines, int lineFactor) {
		super();
		this.diameter = diameter;
		this.maxNumberOfLines = maxNumberOfLines;
		this.lineFactor = lineFactor;
		this.postSequence = new ArrayList<>();

		// Create the target image
		this.targetImage = createTargetImage(image);

		// Calculate the frame bounds
		double imageRadius = ((double) this.targetImage.getWidth()) / 2;
		double frameRadius = imageRadius - 20;

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
	 * @param pixelValueChanges - the list of new pixel values
	 * @return the accuracy change
	 */
	private int calculateAccuracyChange(List<PixelValueChange> pixelValueChanges) {
		int accuracy = 0;
		for (PixelValueChange pixelValueChange : pixelValueChanges) {
			int targetGrayscaleValue = targetImage.getRGB(pixelValueChange.getX(), pixelValueChange.getY()) & 0xFF;
			int previousDifference = Math.abs(pixelValueChange.getPreviousGrayscaleValue() - targetGrayscaleValue);
			int newDifference = Math.abs(pixelValueChange.getGrayscaleValue() - targetGrayscaleValue);
			int improvement = previousDifference - newDifference;
			accuracy += improvement;
		}
		return accuracy;
	}

	private BufferedImage createTargetImage(BufferedImage image) {
		// Crop the image to be square
		BufferedImage croppedImage = ImageService.cropToSquare(image);
//		try {
//			ImageIO.write(croppedImage, "jpg", new File("output/cropped-"
//					+ LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm")) + ".jpg"));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// Resize to diameter
		BufferedImage resizedImage = ImageService.resize(croppedImage, diameter, diameter);

		// Convert to grayscale
		BufferedImage result = ImageService.convertToGrayscale(resizedImage);

		return result;
	}

	private void generateWeavedImage(Palette palette) {
		BufferedImage image = new BufferedImage(targetImage.getWidth(), targetImage.getHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		// Draw the palette lines
		g.drawImage(palette.toBufferedImage(), 0, 0, image.getWidth(), image.getHeight(), null);

		// Draw posts
		g.setColor(Color.RED);
		for (Post post : posts) {
			g.fillOval(post.getLocation().x - 5, post.getLocation().y - 5, 10, 10);
		}

		g.dispose();

		this.weavedImage = image;
	}

	public void weave() {
		// Initialize
		this.postSequence.clear();
		Palette palette = new Palette(this.targetImage.getWidth(), this.targetImage.getHeight(), lineFactor);

		// Start at the first post
		postSequence.add(posts.get(0));

		while (true) {
			System.out.println(postSequence.size());
			int bestAccuracyChange = Integer.MIN_VALUE;
			Post bestPost = null;
			for (Post post : posts.stream().filter(p -> !p.equals(postSequence.getLast())).toList()) {
				// Get the impact of drawing a line on the palette
				Line line = new Line(postSequence.getLast().getLocation(), post.getLocation());
				List<PixelValueChange> pixelValueChanges = palette.getLineImpact(line);
				int testAccuracyChange = calculateAccuracyChange(pixelValueChanges);
				if (testAccuracyChange > 0 && testAccuracyChange > bestAccuracyChange) {
					bestAccuracyChange = testAccuracyChange;
					bestPost = post;
				}
			}

			if (bestPost == null) {
				// We could not improve the accuracy
				break;
			}

			// Commit the post to the sequence
			Line lineToDraw = new Line(postSequence.getLast().getLocation(), bestPost.getLocation());
			postSequence.add(bestPost);
			palette.drawLine(lineToDraw);

			if (postSequence.size() == maxNumberOfLines) {
				break;
			}
		}

		generateWeavedImage(palette);
	}
}
