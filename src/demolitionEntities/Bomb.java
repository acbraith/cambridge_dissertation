package demolitionEntities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import demolition.DemolitionGame;
import demolitionUtilities.Circle;
import demolitionUtilities.Vector2D;

/**
 * Class to contain the Bomb in the Demolition Game.
 * 
 * @author Alex Braithwaite
 *
 */
public class Bomb extends Entity {

	private Circle position;
	private Agent carrier;
	private Agent armer;
	private Agent defuser;
	private boolean carried;
	private boolean arming;
	private boolean defusing;
	private boolean armed;
	private boolean defused;
	private int timeToCarry;
	private int timeToArm;
	private int timeToDefuse;
	private int bombTimer;

	private static final int radius = DemolitionGame.bombRadius;

	/**
	 * Create a bomb at the given position. This can be used in any Demolition
	 * Game, but use in multiple at once will not give good behaviour.
	 * 
	 * @param position
	 */
	public Bomb(Vector2D position) {
		this.position = new Circle(position, radius);

		this.carrier = null;
		this.defused = false;
		drop(carrier);
		bombTimer = DemolitionGame.bombFramesToExplode;
	}

	/**
	 * Check whether an Agent is sufficiently close to interact with the bomb.
	 * 
	 * @param a
	 * @return
	 */
	private boolean closeToBomb(Agent a) {
		return a.getPosition().getCentre().getDistanceToSquared(this.getPosition().getCentre()) < Math
				.pow(DemolitionGame.bombRadius * 4, 2);
	}

	/**
	 * Check whether an Agent is sufficiently close to interact with the bomb
	 * site.
	 * 
	 * @param g
	 * @return
	 */
	private boolean closeToBombSite(DemolitionGame g) {
		return getPosition().getCentre().getDistanceToSquared(g.bombSite.getPosition().getCentre()) < Math
				.pow(DemolitionGame.bombRadius * 4, 2);
	}

	/**
	 * Manage picking up or moving the Bomb.
	 */
	private void carry() {
		Agent a = carrier;
		if (timeToCarry > 0) {
			if (a.getTeam() == 1 && closeToBomb(a)) {
				timeToCarry--;
			} else {
				drop(a);
			}
		} else {
			Vector2D carrierPos = a.getPosition().getCentre();
			Vector2D bombPos = position.getCentre();
			Vector2D toMove = carrierPos.sub(bombPos);
			position = position.move(toMove);
		}
	}

	/**
	 * Manage arming the bomb.
	 * 
	 * @param game
	 */
	private void arm(DemolitionGame game) {
		if (!closeToBombSite(game)) {
			arming = false;
			armer = null;
			timeToArm = DemolitionGame.bombFramesToArm;
			return;
		}
		Agent a = armer;
		if (a.getTeam() == 1 && closeToBomb(a)) {
			timeToArm--;
			if (timeToArm < 0) {
				drop(a);
				armed = true;
			}
		} else {
			drop(a);
		}
	}

	/**
	 * Manage defusing the bomb.
	 */
	private void defuse() {
		Agent a = defuser;
		if (a.getTeam() == 0 && closeToBomb(a)) {
			timeToDefuse--;
			if (timeToDefuse < 0) {
				armed = false;
				bombTimer = DemolitionGame.bombFramesToExplode;
				defused = true;
			}
		} else {
			drop(a);
		}
	}

	/**
	 * Manage all interactions with the Bomb in the given Demolition Game.
	 * 
	 * @param game
	 */
	public void doTimeStep(DemolitionGame game) {
		if (carried && closeToBombSite(game)) {
			arming = true;
		}
		if (armed) {
			position = new Circle(game.bombSite.getPosition().getCentre(), radius);
		}

		if (isArmed()) {
			bombTimer--;
		}
		if (carried) {
			carry();
		}
		if (arming) {
			arm(game);
		}
		if (defusing) {
			defuse();
		}
		pickUp(game);

	}

	/**
	 * Check for agents to interact with the bomb.
	 * 
	 * @param game
	 */
	private void pickUp(DemolitionGame game) {
		for (Agent a : game.agents) {
			if (carrier == null) {
				// bomb can only be picked up by team 1
				if (!armed && a.getTeam() == 1 && closeToBomb(a)) {
					carrier = a;
					carried = true;
				}
			}
			if (armer == null) {
				if (!armed && a.getTeam() == 1 && closeToBomb(a) && closeToBombSite(game)) {
					armer = a;
					arming = true;
				}
			}
			if (defuser == null) {
				if (armed && a.getTeam() == 0 && closeToBomb(a)) {
					defuser = a;
					defusing = true;
				}
			}
		}
	}

	/**
	 * To be used when the agent carrying the bomb dies.
	 * 
	 * @param a
	 */
	public void drop(Agent a) {
		if (carrier == a || armer == a || defuser == a) {
			carrier = null;
			carried = false;
			armer = null;
			arming = false;
			defuser = null;
			defusing = false;

			timeToCarry = DemolitionGame.bombFramesToPickup;
			timeToArm = DemolitionGame.bombFramesToArm;
			timeToDefuse = DemolitionGame.bombFramesToDefuse;
		}
	}

	@Override
	public void draw(Graphics2D g2d) {
		if (!armed)
			g2d.setColor(Color.darkGray);
		else
			g2d.setColor(Color.getHSBColor(0f, 1f, 1 - bombTimer / (1f * DemolitionGame.bombFramesToExplode)));
		g2d.setStroke(new BasicStroke(1));
		int x = (int) (position.getCentre().getX() - radius / 2.);
		int y = (int) (position.getCentre().getY() - radius / 2.);
		g2d.fillOval(x - radius / 2, y - radius / 2, radius * 2, radius * 2);
		if (armed)
			g2d.setColor(Color.getHSBColor(0f, 1f, timeToDefuse / (1f * DemolitionGame.bombFramesToDefuse)));
		else if (arming)
			g2d.setColor(Color.getHSBColor(0f, 1f, timeToArm / (1f * DemolitionGame.bombFramesToArm)));
		else
			g2d.setColor(Color.white);
		g2d.drawOval(x - radius / 2, y - radius / 2, radius * 2, radius * 2);
	}

	public boolean isArmed() {
		return armed;
	}

	public Circle getPosition() {
		return position;
	}

	public Agent getCarrier() {
		return carrier;
	}

	public Agent getArmer() {
		return armer;
	}

	public Agent getDefuser() {
		return defuser;
	}

	public boolean isCarried() {
		return carried;
	}

	public boolean getExploded() {
		return bombTimer < 0;
	}

	public boolean getDefused() {
		return defused;
	}

}
