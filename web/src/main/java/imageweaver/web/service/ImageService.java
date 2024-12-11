package imageweaver.web.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageService {
	public static BufferedImage convertToGrayscale(BufferedImage sourceImage, int targetWidth, int targetHeight) {
		BufferedImage result = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_GRAY);

		Graphics2D graphics = result.createGraphics();
		graphics.drawImage(sourceImage, 0, 0, null);
		graphics.dispose();

		return result;
	}

	public static BufferedImage convertToMonochrome(BufferedImage sourceImage, int targetWidth, int targetHeight) {
		BufferedImage result = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_BINARY);

		Graphics2D graphics = result.createGraphics();
		graphics.drawImage(sourceImage, 0, 0, Color.WHITE, null);
		graphics.dispose();

		return result;
	}

	public static BufferedImage cropToSquare(BufferedImage image) {
		// Determine the size of the square crop
		int cropSize = Math.min(image.getWidth(), image.getHeight());

		// Calculate the coordinates for the crop
		int x = (image.getWidth() - cropSize) / 2;
		int y = (image.getHeight() - cropSize) / 2;

		// Crop the image
		BufferedImage croppedImage = image.getSubimage(x, y, cropSize, cropSize);

		return croppedImage;
	}

}
