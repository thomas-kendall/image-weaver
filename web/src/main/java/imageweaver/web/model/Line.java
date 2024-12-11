package imageweaver.web.model;

import java.awt.Point;
import java.awt.geom.Line2D;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Line {
	private final Point p1;
	private final Point p2;

	public Line2D toLine2D() {
		return new Line2D.Double(p1, p2);
	}
}
