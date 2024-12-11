package imageweaver.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PixelValue {
	private final int x;
	private final int y;
	private final int grayscaleValue;
}
