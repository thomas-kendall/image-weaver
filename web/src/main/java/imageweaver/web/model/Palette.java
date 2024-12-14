package imageweaver.web.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Palette {
	private final int width;
	private final int height;
	private final PalettePixel[][] pixels;

	public Palette(int width, int height, int lineFactor) {
		this.width = width;
		this.height = height;
		this.pixels = new PalettePixel[width][height];
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				this.pixels[x][y] = new PalettePixel(lineFactor);
			}
		}
	}

	public void drawLine(Line line) {
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
			plot(x, y);

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
	}

	public int getGrayscale(int x, int y) {
		return pixels[x][y].getGrayscaleValue();
	}

	/**
	 * Returns the impact of drawing the line on the palette without actually
	 * drawing the line.
	 *
	 * @param line - the line that would be drawn
	 * @return - the list of PixelValues representing the pixels that would change
	 *         due to the line being drawn.
	 */
	public List<PixelValueChange> getLineImpact(Line line) {
		List<PixelValueChange> pixelValues = new ArrayList<>();

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
			pixelValues.add(pixels[x][y].getLineImpact(x, y));

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

	public int getRGB(int x, int y) {
		return pixels[x][y].getRGB();
	}

	private void plot(int x, int y) {
		pixels[x][y].crossWithLine();
	}

	public BufferedImage toBufferedImage() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, getRGB(x, y));
			}
		}
		return image;
	}
}
