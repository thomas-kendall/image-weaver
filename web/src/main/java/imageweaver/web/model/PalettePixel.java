package imageweaver.web.model;

import lombok.Getter;

@Getter
public class PalettePixel {
	private final int lineFactor;
	private int grayscaleValue;
	private int numberOfLines;

	public PalettePixel(int lineFactor) {
		this.lineFactor = lineFactor;
		this.grayscaleValue = 255;
		this.numberOfLines = 0;
	}

	public void crossWithLine() {
		grayscaleValue -= lineFactor;
		if (grayscaleValue < 0) {
			grayscaleValue = 0;
		}
	}

	public PixelValueChange getLineImpact(int x, int y) {
		int previousValue = grayscaleValue;
		int value = previousValue - lineFactor;
		if (value < 0) {
			value = 0;
		}
		return new PixelValueChange(x, y, previousValue, value);
	}

	public int getRGB() {
		return grayscaleValue << 16 | grayscaleValue << 8 | grayscaleValue;
	}
}
