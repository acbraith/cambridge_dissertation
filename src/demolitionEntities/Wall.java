package demolitionEntities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import demolitionUtilities.Line2D;
import demolitionUtilities.Vector2D;

/**
 * Class representing the walls in the Demolition Game.
 * 
 * @author Alex Braithwaite
 *
 */
public class Wall extends Entity {

	private Line2D line;
	private double length;

	/**
	 * Create a Wall between two points.
	 * 
	 * @param start
	 * @param end
	 */
	public Wall(Vector2D start, Vector2D end) {
		super();
		Vector2D dir = end.sub(start);
		line = new Line2D(start, dir);
		length = Math.sqrt(end.getDistanceToSquared(start));
	}

	/**
	 * Create a wall from a start point, in a given direction for a given
	 * distance.
	 * 
	 * @param start
	 * @param dir
	 * @param length
	 */
	public Wall(Vector2D start, Vector2D dir, double length) {
		super();
		line = new Line2D(start, dir);
		this.length = length;
	}

	/**
	 * Create a wall from a start point, in a given direction for a given
	 * distance.
	 * 
	 * @param start
	 * @param dir
	 * @param length
	 */
	public Wall(Vector2D start, double dir, double length) {
		super();
		line = new Line2D(start, dir);
		this.length = length;
	}

	public Line2D getLine() {
		return line;
	}

	public double getLength() {
		return length;
	}

	@Override
	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(5));
		int x1 = (int) line.getStart().getX();
		int y1 = (int) line.getStart().getY();
		int x2 = (int) (line.getStart().getX() + length * line.getDirection().getX());
		int y2 = (int) (line.getStart().getY() + length * line.getDirection().getY());
		g2d.drawLine(x1, y1, x2, y2);
	}

}
