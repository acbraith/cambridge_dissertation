package demolition;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import demolitionEntities.Entity;

/**
 * Canvas to draw Demolition Game to.
 * 
 * @author Alex Braithwaite
 *
 */
public class Canvas extends JPanel {

	private static final long serialVersionUID = 6503451076688400110L;

	private final int canvasX;
	private final int canvasY;

	public int i = 10;

	private DemolitionGame game;

	/**
	 * Initialise the canvas to given world dimensions.
	 * 
	 * @param g
	 * @param worldX
	 * @param worldY
	 */
	public Canvas(DemolitionGame g, int worldX, int worldY) {
		this.game = g;
		this.canvasX = worldX;
		this.canvasY = worldY;

		// setup the window
		setPreferredSize(new Dimension(canvasX, canvasY));
		setBackground(Color.white);
	}

	@Override
	public void paintComponent(Graphics g) {
		// required
		super.paintComponent(g);

		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Draw all entities in game
		Object[] es = game.entities.toArray();
		for (Object o : es) {
			if (o != null) {
				((Entity) o).draw(g2d);
			}
		}
	}

}
