package imageweaver.web.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import imageweaver.web.model.ImageWeaver;
import imageweaver.web.service.ImageService;

@RestController
public class ImageController {

	@Value("classpath:images/obama.jpg")
	private Resource resource;

	@RequestMapping(value = "/images/obama", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getObama() throws IOException {
		InputStream inputStream = resource.getInputStream();
		// BufferedImage image = ImageIO.read(inputStream);
		return IOUtils.toByteArray(inputStream);
	}

	@RequestMapping(value = "/images/obama-grayscale", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getObamaGrayscale() throws IOException {
		InputStream inputStream = resource.getInputStream();
		BufferedImage image = ImageIO.read(inputStream);
		BufferedImage grayscaleImage = ImageService.convertToGrayscale(image, image.getWidth(), image.getHeight());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(grayscaleImage, "jpg", outputStream);
		return outputStream.toByteArray();
	}

	@RequestMapping(value = "/images/obama-monochrome", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getObamaMonochrome() throws IOException {
		InputStream inputStream = resource.getInputStream();
		BufferedImage image = ImageIO.read(inputStream);
		BufferedImage monochromeImage = ImageService.convertToMonochrome(image, image.getWidth(), image.getHeight());
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(monochromeImage, "jpg", outputStream);
		return outputStream.toByteArray();
	}

	@RequestMapping(value = "/images/obama/weaved", produces = MediaType.IMAGE_JPEG_VALUE)
	public byte[] getObamaWeaved() throws IOException {
		InputStream inputStream = resource.getInputStream();
		BufferedImage image = ImageIO.read(inputStream);
		BufferedImage grayscaleImage = ImageService.convertToGrayscale(image, image.getWidth(), image.getHeight());

		ImageWeaver weaver = new ImageWeaver(50, grayscaleImage, 600);
		weaver.weave(10000);
		BufferedImage weavedImage = weaver.getWeavedImage();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(weavedImage, "jpg", outputStream);
		return outputStream.toByteArray();
	}

}
