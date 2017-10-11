package demolitionUtilities;

/**
 * An immutable Vector Class
 * 
 * @author Alex Braithwaite
 * 
 */
final public class Vector2D {

	final private double x, y;

	/**
	 * Create a unit vector at an angle to the x axis.
	 * 
	 * @param a
	 *            Angle to x axis.
	 */
	public Vector2D(double a) {
		this.x = Math.cos(a);
		this.y = Math.sin(a);
	}

	/**
	 * Create a vector.
	 * 
	 * @param x
	 * @param y
	 */
	public Vector2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Copy a vector.
	 * 
	 * @param v
	 *            Vector to copy.
	 */
	public Vector2D(Vector2D v) {
		this.x = v.x;
		this.y = v.y;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	/**
	 * Get the length of this vector squared.
	 * 
	 * @return Length of this vector squared.
	 */
	public double getLengthSquared() {
		return (x * x + y * y);
	}

	/**
	 * Get the length of this vector.
	 * 
	 * @return Length of this vector.
	 */
	public double getLength() {
		return Math.sqrt(x * x + y * y);
	}

	/**
	 * Get the angle this vector makes to the x axis.
	 * 
	 * @return Angle this vector makes to the x axis.
	 */
	public double getAngle() {
		return (Math.atan(y / x));
	}

	/**
	 * Get the distance between this vector and another squared.
	 * 
	 * @param v
	 *            Vector to measure distance from.
	 * @return Distance between vectors squared.
	 */
	public double getDistanceToSquared(Vector2D v) {
		double dx = this.x - v.x;
		double dy = this.y - v.y;
		return (dx * dx + dy * dy);
	}

	/**
	 * Get the distance between this vector and another.
	 * 
	 * @param v
	 *            Vector to measure distance from.
	 * @return Distance between vectors.
	 */
	public double getDistanceTo(Vector2D v) {
		double dx = this.x - v.x;
		double dy = this.y - v.y;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Get the angle (in radians) between this vector and another, measured from
	 * this vector in the counterclockwise direction.
	 * 
	 * @param v1
	 *            Vector to measure angle to.
	 * @return Angle between vectors.
	 */
	public double getAngleTo(Vector2D v) {
		if (this.getLengthSquared() == 0 || v.getLengthSquared() == 0)
			return 0;
		double a = Math.acos(this.dotProduct(v) / (this.getLength() * v.getLength()));
		if ((this.x * v.y - this.y * v.x) > 0)
			a *= -1;
		return a;
	}

	/**
	 * Get the dot product of this vector with another.
	 * 
	 * @param v1
	 *            Vector to dot product with.
	 * @return Dot product of these 2 vectors.
	 */
	public double dotProduct(Vector2D v) {
		return this.x * v.x + this.y * v.y;
	}

	/**
	 * Add another vector to this vector.
	 * 
	 * @param v
	 *            Vector to add.
	 * @return New vector equal to this + v.
	 */
	public Vector2D add(Vector2D v) {
		double x = this.x + v.x;
		double y = this.y + v.y;
		return new Vector2D(x, y);
	}

	/**
	 * Subtract a vector from this vector.
	 * 
	 * @param v
	 *            Vector to subtract.
	 * @return New vector equal to this - v.
	 */
	public Vector2D sub(Vector2D v) {
		double x = this.x - v.x;
		double y = this.y - v.y;
		return new Vector2D(x, y);
	}

	/**
	 * Scale a vector by a scalar.
	 * 
	 * @param s
	 *            Scalar.
	 * @return New vector equal to s * this.
	 */
	public Vector2D scale(double s) {
		double x = this.x * s;
		double y = this.y * s;
		return new Vector2D(x, y);
	}

	/**
	 * Rotate vector by an angle.
	 * 
	 * @param a
	 *            Angle to rotate (in radians).
	 * @return New vector formed by rotating this by a radians
	 *         counter-clockwise.
	 */
	public Vector2D rotate(double a) {
		double x = this.x * Math.cos(a) - this.y * Math.sin(a);
		double y = this.x * Math.sin(a) + this.y * Math.cos(a);
		return new Vector2D(x, y);
	}

	/**
	 * Normalise this vector.
	 * 
	 * @return New vector formed by normalising this.
	 */
	public Vector2D normalise() {
		double lenSquared = this.getLengthSquared();
		double x = this.x, y = this.y;
		if (lenSquared != 0.0f && lenSquared != 1.0f) {
			double len = Math.sqrt(lenSquared);
			x /= len;
			y /= len;
		}
		return new Vector2D(x, y);
	}

	/**
	 * Test whether this vector and another are equal.
	 * 
	 * @param v
	 *            Vector to test against.
	 * @return True if vectors are equal.
	 */
	public boolean equals(Vector2D v) {
		return this.x == v.x && this.y == v.y;
	}
}
