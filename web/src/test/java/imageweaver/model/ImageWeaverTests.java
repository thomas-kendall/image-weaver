package imageweaver.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import imageweaver.web.model.ImageWeaver;

public class ImageWeaverTests {

	@Test
	public void testImageWeaver() throws IOException {
		InputStream inputStream = getClass().getClassLoader().getResource("images/obama-headshot.jpg").openStream();
		BufferedImage image = ImageIO.read(inputStream);

		ImageWeaver weaver = new ImageWeaver(100, image, 600);
		weaver.weave(10000);
		BufferedImage weavedImage = weaver.getWeavedImage();

		ImageIO.write(weavedImage, "jpg",
				new File("output/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm")) + ".jpg"));
	}
}
