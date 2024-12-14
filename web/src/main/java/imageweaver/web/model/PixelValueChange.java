package imageweaver.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PixelValueChange {
	private final int x;
	private final int y;
	private final int previousGrayscaleValue;
	private final int grayscaleValue;
}
