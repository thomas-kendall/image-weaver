package imageweaver.web.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.springframework.stereotype.Service;

@Service
public class ImageService {
	public BufferedImage convertToMonochrome(BufferedImage sourceImage, int targetWidth, int targetHeight) {
		// TODO: Validate input

		BufferedImage result = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_BYTE_BINARY);

		Graphics2D graphics = result.createGraphics();
		graphics.drawImage(sourceImage, 0, 0, Color.WHITE, null);
		graphics.dispose();

		return result;
	}

}
