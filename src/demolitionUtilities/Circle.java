package demolitionUtilities;

/**
 * Immutable class for a circle in 2D.
 * 
 * @author Alex Braithwaite
 * 
 */
public class Circle {

	final private Vector2D centre;
	final private double radius;

	/**
	 * Create a circle with a given centre and radius.
	 * 
	 * @param centre
	 *            Centre of circle.
	 * @param radius
	 *            Radius of circle.
	 */
	public Circle(Vector2D centre, double radius) {
		this.centre = centre;
		this.radius = radius;
	}

	public Vector2D getCentre() {
		return centre;
	}

	public double getRadius() {
		return radius;
	}

	/**
	 * Move circle by a vector.
	 * 
	 * @param v
	 *            Vector to move circle by.
	 * @return New circle with centre moved by v.
	 */
	public Circle move(Vector2D v) {
		Vector2D centre = this.centre.add(v);
		return new Circle(centre, this.radius);
	}

	/**
	 * Check and give position of intersection of this circle and a line.
	 * 
	 * @param l
	 *            Line to intersect.
	 * @return Distance from l's start to intersection point(s). [null, null] if
	 *         there are no intersections.
	 */
	public Double[] intersect(Line2D l) {
		return l.intersect(this);
	}

	/**
	 * Check for intersection of this circle and another circle.
	 * 
	 * @param l
	 *            Circle to intersect.
	 * @return True if the two circles intersect. False otherwise.
	 */
	public boolean intersect(Circle c) {
		if (this.centre.getDistanceTo(c.centre) <= 2 * this.radius) {
			return true;
		}
		return false;
	}
}
