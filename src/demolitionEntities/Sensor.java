package demolitionEntities;

import java.util.Iterator;

import demolition.DemolitionGame;
import demolitionUtilities.Line2D;
import demolitionUtilities.Vector2D;

/**
 * Class to manage obtaining Sensory inputs for the Agents in the Demolition
 * Game.
 * 
 * @author Alex Braithwaite
 *
 */
public class Sensor {

	private Line2D line;
	private double length;
	private Entity hit;
	private Agent agent;
	private DemolitionGame game;

	/**
	 * Create a sensor for the given agent, in the given direction from the
	 * start point, in the given Demolition Game.
	 * 
	 * @param a
	 * @param start
	 * @param dir
	 * @param game
	 */
	public Sensor(Agent a, Vector2D start, Vector2D dir, DemolitionGame game) {
		this.agent = a;
		this.game = game;
		line = new Line2D(start, dir);
		length = DemolitionGame.sensorMaxRange;
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
	 * Find what the sensor hits and how far away it is.
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
			} else if (e != agent && e instanceof Agent) {
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

}
