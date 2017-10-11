package demolitionUtilities;

/**
 * Immutable class for a line in 2D.
 * 
 * @author Alex Braithwaite
 *
 */
public class Line2D {

	final private Vector2D start;
	final private Vector2D direction;

	/**
	 * Create a line from a position in a direction.
	 * 
	 * @param start
	 *            Vector specifying start position.
	 * @param direction
	 *            Vector specifying direction of line.
	 */
	public Line2D(Vector2D start, Vector2D direction) {
		this.start = start;
		this.direction = direction.normalise();
	}

	/**
	 * Create a line from a position in a direction.
	 * 
	 * @param start
	 *            Vector specifying start position.
	 * @param direction
	 *            Angle line makes to x axis.
	 */
	public Line2D(Vector2D start, double direction) {
		this.start = start;
		this.direction = new Vector2D(direction);
	}

	public Vector2D getStart() {
		return start;
	}

	public Vector2D getDirection() {
		return direction;
	}

	public double getAngle() {
		return direction.getAngle();
	}

	/**
	 * Check and give position of intersection of this line and another line.
	 * 
	 * @param l
	 *            Line to intersect.
	 * @return Distance from this line and l's start points to intersection
	 *         point. Return [null, null] if lines are parallel.
	 */
	public Double[] intersect(Line2D l) {
		Double d1 = null, d2 = null;

		// parallel lines do not intersect
		if (!this.getDirection().equals(l.getDirection())) {
			double x1 = this.getStart().getX(), x2 = l.getStart().getX();
			double y1 = this.getStart().getY(), y2 = l.getStart().getY();
			double dx1 = this.getDirection().getX();
			double dy1 = this.getDirection().getY();
			double dx2 = l.getDirection().getX();
			double dy2 = l.getDirection().getY();

			d1 = (y2 * dx2 + x1 * dy2 - y1 * dx2 - x2 * dy2)
					/ (dy1 * dx2 - dx1 * dy2);
			d2 = (y1 * dx1 + x2 * dy1 - y2 * dx1 - x1 * dy1)
					/ (dy2 * dx1 - dx2 * dy1);
		}

		Double[] ret = { d1, d2 };
		return ret;
	}

	/**
	 * Check and give position of intersection of this line and a circle.
	 * 
	 * @param cir
	 *            Circle to intersect.
	 * @return Distance from this line's start to intersection point(s). [null,
	 *         null] if there are no intersections.
	 */
	public Double[] intersect(Circle cir) {
		Double d1 = null, d2 = null;

		// this requires solving a quadratic
		double r = cir.getRadius();
		double cx = cir.getCentre().getX(), cy = cir.getCentre().getY();
		double lx = this.getStart().getX(), ly = this.getStart().getY();
		double dx = this.getDirection().getX(), dy = this.getDirection().getY();

		double a = dx * dx + dy * dy;
		double b = 2 * (lx * dx - dx * cx + ly * dy - dy * cy);
		double c = lx * lx + ly * ly + cx * cx + cy * cy - 2 * cx * lx - 2 * cy
				* ly - r * r;
		double det = b * b - 4 * a * c;

		// if determinant is below zero there is no intersection
		if (det >= 0) {
			d1 = (-b + Math.sqrt(det)) / (2 * a);
			d2 = (-b - Math.sqrt(det)) / (2 * a);
		}

		Double[] ret = { d1, d2 };
		return ret;
	}
}
