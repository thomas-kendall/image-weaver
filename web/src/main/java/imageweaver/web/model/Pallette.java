package imageweaver.web.model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Pallette {
	private static final int LINE_PIXEL_FACTOR = 20;
	private int width;
	private int height;
	private int[][] pixels;

	public Pallette(int width, int height) {
		this.width = width;
		this.height = height;
		this.pixels = new int[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				this.pixels[x][y] = 255;
			}
		}
	}

	@Override
	public Pallette clone() {
		Pallette copy = new Pallette(width, height);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				copy.pixels[x][y] = pixels[x][y];
			}
		}
		return copy;
	}

	/**
	 * Draws the line on the pallette.
	 *
	 * @param line to draw
	 * @return the list of pixel values that have been changed as a result of the
	 *         line
	 */
	public List<PixelValue> drawLine(Line line) {
		List<PixelValue> pixelValues = new ArrayList<>();

		int x1 = line.getP1().x;
		int y1 = line.getP1().y;
		int x2 = line.getP2().x;
		int y2 = line.getP2().y;

		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int sx = (x1 < x2) ? 1 : -1;
		int sy = (y1 < y2) ? 1 : -1;
		int err = dx - dy;

		int x = x1;
		int y = y1;

		while (true) {
			PixelValue pixelValue = plot(x, y);
			if (pixelValue != null) {
				pixelValues.add(pixelValue);
			}

			if (x == x2 && y == y2) {
				break;
			}

			int e2 = 2 * err;
			if (e2 > -dy) {
				err -= dy;
				x += sx;
			}
			if (e2 < dx) {
				err += dx;
				y += sy;
			}
		}

		return pixelValues;
	}

	public int getPixel(int x, int y) {
		return pixels[x][y];
	}

	private PixelValue plot(int x, int y) {
		PixelValue pixelValue = null;
		if (x >= 0 && x < width && y >= 0 && y < height) {
			pixels[x][y] -= LINE_PIXEL_FACTOR;
			if (pixels[x][y] < 0) {
				pixels[x][y] = 0;
			}
			pixelValue = new PixelValue(x, y, pixels[x][y]);
		}
		return pixelValue;
	}

	public BufferedImage toBufferedImage() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, new Color(pixels[x][y], pixels[x][y], pixels[x][y]).getRGB());
			}
		}
		return image;
	}
}
