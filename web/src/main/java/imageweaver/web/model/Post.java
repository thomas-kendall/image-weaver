package imageweaver.web.model;

import java.awt.Point;

import lombok.Data;

@Data
public class Post {
	private Point location;

	public Post(int x, int y) {
		this.location = new Point(x, y);
	}
}
