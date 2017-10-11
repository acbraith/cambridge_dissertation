package demolitionEntities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Iterator;

import demolition.DemolitionGame;
import demolitionUtilities.Line2D;
import demolitionUtilities.Vector2D;

/**
 * Class to manage Lasers in the Demolition Game.
 * 
 * @author Alex Braithwaite
 *
 */
public class Laser extends Entity {

	private Line2D line;
	private double length;
	private Entity hit;
	private Agent shooter;
	private DemolitionGame game;

	/**
	 * Create a Laser from the given start location, in the given direction,
	 * fired by the given Agent in the given Demolition Game.
	 * 
	 * @param shooter
	 * @param start
	 * @param dir
	 * @param game
	 */
	public Laser(Agent shooter, Vector2D start, Vector2D dir, DemolitionGame game) {
		super();
		this.shooter = shooter;
		this.game = game;
		line = new Line2D(start, dir);
		length = DemolitionGame.laserMaxRange;
		hit = null;
		checkForHit();
	}

	public Line2D getLine() {
		return line;
	}

	public double getLength() {
		return length;
	}

	public Entity getHit() {
		return hit;
	}

	/**
	 * Find the agent, if any, hit by this laser.
	 */
	private void checkForHit() {
		Iterator<Entity> it = game.entities.listIterator();
		while (it.hasNext()) {
			Entity e = it.next();
			if (e instanceof Wall) {
				Wall w = (Wall) e;
				// check collision distance to wall
				Double[] d = line.intersect(w.getLine());
				if (d[0] != null && d[1] > 0 && d[1] < w.getLength() && d[0] > 0 && d[0] < length) {
					length = d[0];
					hit = w;
				}
			} else if (e != shooter && e instanceof Agent) {
				Agent a = (Agent) e;
				// check collision distance to agent
				Double[] ds = a.getPosition().intersect(line);
				for (Double d : ds) {
					if (d != null && d > 0 && d < length) {
						length = d;
						hit = a;
					}
				}
			}
		}
	}

	@Override
	public void draw(Graphics2D g2d) {
		g2d.setColor(new Color(shooter.getTeam() == 1 ? 1f : 0f, 0f, shooter.getTeam() == 0 ? 1f : 0f, 1f));
		g2d.setStroke(new BasicStroke(2));
		int x1 = (int) line.getStart().getX();
		int y1 = (int) line.getStart().getY();
		int x2 = (int) (line.getStart().getX() + length * line.getDirection().getX());
		int y2 = (int) (line.getStart().getY() + length * line.getDirection().getY());
		g2d.drawLine(x1, y1, x2, y2);
	}

}
