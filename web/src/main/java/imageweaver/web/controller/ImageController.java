package imageweaver.web.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import imageweaver.web.service.ImageService;

@RestController
public class ImageController {

	@Value("classpath:images/obama.jpg")
	private Resource resource;

	@Autowired
	private ImageService imageService;

	@RequestMapping(value = "/images/obama", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getFlood() throws IOException {
		InputStream inputStream = resource.getInputStream();
		// BufferedImage image = ImageIO.read(inputStream);
		return IOUtils.toByteArray(inputStream);
	}

	@RequestMapping(value = "/images/obama-monochrome", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getFloodMonochrome() throws IOException {
		InputStream inputStream = resource.getInputStream();
		BufferedImage image = ImageIO.read(inputStream);
		BufferedImage monochromeImage = imageService.convertToMonochrome(image, image.getWidth(), image.getHeight());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(monochromeImage, "jpg", outputStream);
		return outputStream.toByteArray();
	}

}
