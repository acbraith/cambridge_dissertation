package demolitionEntities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import demolition.DemolitionGame;
import demolitionUtilities.Circle;
import demolitionUtilities.Vector2D;

/**
 * Class to manage the Bomb Site in the Demolition Game.
 * 
 * @author Alex Braithwaite
 *
 */
public class BombSite extends Entity {

	private Circle position;

	public Circle getPosition() {
		return position;
	}

	private static final int radius = DemolitionGame.bombRadius * 2;

	/**
	 * Create a bomb site, to be used in any Demolition Game.
	 * 
	 * @param position
	 */
	public BombSite(Vector2D position) {
		this.position = new Circle(position, radius);
	}

	@Override
	public void draw(Graphics2D g2d) {
		g2d.setColor(Color.lightGray);
		g2d.setStroke(new BasicStroke(1));
		int x = (int) (position.getCentre().getX() - radius / 2.);
		int y = (int) (position.getCentre().getY() - radius / 2.);
		g2d.fillOval(x - radius / 2, y - radius / 2, radius * 2, radius * 2);
		g2d.drawOval(x - radius / 2, y - radius / 2, radius * 2, radius * 2);
	}

}
