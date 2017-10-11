package demolitionEntities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;

import demolition.DemolitionGame;
import demolitionUtilities.Circle;
import demolitionUtilities.Vector2D;

/**
 * Agent entity, contains all information and methods for using an Agent in the
 * Demolition Game.
 * 
 * @author Alex Braithwaite
 *
 */
public class Agent extends Entity {

	private Vector2D direction;
	private Circle position;
	private int team;
	private int health;
	private double reward;

	public int deaths = 0;

	public DemolitionGame game;

	public boolean crashed = false;
	public boolean killed = false;
	public boolean damaged = false;
	private int laserHeat = 0;
	private int fatigue = 0;
	public boolean playerControlled = false;
	public int experience = 0;

	private double[] sensorDistances = new double[DemolitionGame.sensorsNumber];
	private double[] sensorAgent = new double[DemolitionGame.sensorsNumber];
	private double[] sensorEnemy = new double[DemolitionGame.sensorsNumber];
	private double[] sensorBombCarrier = new double[DemolitionGame.sensorsNumber];
	public double sensorBombDistance;
	private boolean laserOverheated = false;
	private boolean exhausted = false;

	/**
	 * Create an agent in the DemolitionGame provided.
	 * 
	 * @param pos
	 * @param direction
	 * @param team
	 * @param game
	 */
	public Agent(Vector2D pos, Vector2D direction, int team, DemolitionGame game) {
		super();
		position = new Circle(pos, DemolitionGame.agentRadius);
		this.direction = direction.normalise();
		this.team = team;
		this.health = DemolitionGame.agentMaxHealth;
		this.reward = 0;
		this.game = game;
	}

	/**
	 * Create an agent in the DemolitionGame provided.
	 * 
	 * @param pos
	 * @param direction
	 * @param team
	 * @param game
	 */
	public Agent(Vector2D pos, double direction, int team, DemolitionGame game) {
		super();
		position = new Circle(pos, DemolitionGame.agentRadius);
		this.direction = new Vector2D(direction);
		this.team = team;
		this.health = DemolitionGame.agentMaxHealth;
		this.reward = 0;
		this.game = game;
	}

	/**
	 * Get the circle representing the agent's position.
	 * 
	 * @return
	 */
	public Circle getPosition() {
		return position;
	}

	/**
	 * Get the direction of the agent.
	 * 
	 * @return
	 */
	public Vector2D getDirection() {
		return direction;
	}

	/**
	 * Get the team of the agent.
	 * 
	 * @return
	 */
	public int getTeam() {
		return team;
	}

	/**
	 * Move this agent in a given direction.
	 * 
	 * @param distance
	 * @param direction
	 */
	public void move(double distance, Vector2D direction) {
		if (exhausted) {
			distance /= 3;
		}
		if (fatigue < 90) {
			fatigue += (exhausted ? 4 : 5);
		}
		forceMove(distance, direction);
	}

	/**
	 * Move this agent in a given direction without fatiguing it.
	 * 
	 * @param distance
	 * @param direction
	 */
	public void forceMove(double distance, Vector2D direction) {
		position = position.move(direction.normalise().scale(distance));
	}

	/**
	 * Turn this agent.
	 * 
	 * @param angle
	 */
	public void turn(double angle) {
		if (exhausted) {
			angle /= 2;
		}
		angle *= 0.95 + 0.1 * Math.random();
		direction = direction.rotate(angle);
	}

	/**
	 * Shoot a laser.
	 * 
	 * @return Laser shot.
	 */
	public Laser shoot() {
		double spread = DemolitionGame.agentFireSpread;
		if (exhausted) {
			spread *= 2;
		}
		if (!laserOverheated) {
			laserHeat += 10;
			double angle = Math.random() * (spread * 2) - spread;
			Vector2D dir = this.getDirection().rotate(angle);
			return new Laser(this, this.getPosition().getCentre(), dir, game);
		}
		return null;
	}

	/**
	 * Update agent's sensors for the current time step.
	 */
	public void updateInputs() {
		Vector2D dir = this.getDirection().rotate(-DemolitionGame.sensorAngle / 2.);
		for (int i = 0; i < DemolitionGame.sensorsNumber; i++) {
			Sensor[] s = new Sensor[3];
			s[0] = new Sensor(this, this.getPosition().getCentre(), dir, game);
			// multiple sensors for each reported sensor (not currently being used)
			s[1] = s[0];// new Sensor(this, this.getPosition().getCentre(),
						// dir.rotate(DemolitionGame.agentFireSpread), game);
			s[2] = s[0];// new Sensor(this, this.getPosition().getCentre(),
						// dir.rotate(-DemolitionGame.agentFireSpread), game);
			int min = 0;
			double minDist = DemolitionGame.sensorMaxRange;
			for (int j = 0; j < 3; j++) {
				if (s[j].getLength() < minDist) {
					minDist = s[j].getLength();
					min = j;
				}
			}
			sensorDistances[i] = minDist;
			sensorAgent[i] = -10;
			sensorEnemy[i] = -10;
			sensorBombCarrier[i] = -10;
			Entity hit = s[min].getHit();
			if (hit instanceof Agent) {
				Agent a = (Agent) hit;
				sensorAgent[i] = 10;
				if (a.team != this.team) {
					sensorEnemy[i] = 10;
				}
				if (a == game.bomb.getCarrier()) {
					sensorBombCarrier[i] = 10;
				}
			}
			// rotate non-linear amount
			double rotationScalar = i == 0 ? 5
					: i == 1 ? 5 * (4. / 3.)
							: i == 2 ? 5 * (4. / 2.)
									: i == 3 ? 5 * (4. / 1.)
											: i == 4 ? 5 * (4. / 1.)
													: i == 5 ? 5 * (4. / 2.) : i == 6 ? 5 * (4. / 3.) : 5;
			dir = dir.rotate(DemolitionGame.sensorAngle * 1. / rotationScalar);
		}
	}

	/**
	 * Return an array containing all the sensory inputs to this agent.
	 * 
	 * @return Array of agent's observation of the game world.
	 */
	public ArrayList<Double> getInputs() {
		ArrayList<Double> sensors = new ArrayList<Double>(DemolitionGame.sensorsTotal);
		double rangeShort = 2 * DemolitionGame.agentRadius;
		double rangeLong = DemolitionGame.sensorMaxRange * .6;
		double rangeMed = (rangeShort + rangeLong) / 2;
		int j = 0;
		for (int i = 0; i < DemolitionGame.sensorsNumber; i++) {
			// shift sensors by a different amount each, to make input flip at different times
			sensors.add(sensorDistances[j] - rangeShort);
			sensors.add(sensorDistances[j] - rangeMed);
			sensors.add(sensorDistances[j] - rangeLong);
			sensors.add(sensorAgent[j]);
			sensors.add(sensorEnemy[j]);
			sensors.add(sensorBombCarrier[j]);
			j++;
		}

		// health
		sensors.add(health - (DemolitionGame.agentMaxHealth - DemolitionGame.laserDamage * 1.5));
		sensors.add(health - DemolitionGame.agentMaxHealth / 2.);
		sensors.add(health - DemolitionGame.laserDamage * 1.5);

		// laser heat
		sensors.add(laserHeat - 70.);
		sensors.add(laserHeat - 30.);

		// laser overheated
		sensors.add(laserOverheated ? 10. : -10.);

		// fatigue
		sensors.add(fatigue - 70.);
		sensors.add(fatigue - 30.);

		// exhausted
		sensors.add(exhausted ? 10. : -10.);

		// team
		sensors.add(this.team == 0 ? 10. : -10.);

		// carrying bomb
		sensors.add(game.bomb.getCarrier() == this ? 10. : -10.);

		// arming/disarming bomb
		sensors.add((game.bomb.getArmer() == this || game.bomb.getDefuser() == this) ? 10. : -10.);

		// bomb carried
		sensors.add(team == 0 ? 10. : game.bomb.getCarrier() != null ? 10. : -10.);

		// bomb armed
		sensors.add(game.bomb.isArmed() ? 10. : -10.);

		// bomb sensors
		{
			Vector2D toBomb = new Vector2D(0, 0);
			double bombDistance = 0;
			// bomb only visible to team 1
			if (team == 1) {
				toBomb = this.getPosition().getCentre().sub(game.bomb.getPosition().getCentre());
				bombDistance = Math.sqrt(toBomb.getLengthSquared());
			}
			// bomb distance
			sensors.add(bombDistance - rangeShort);
			sensors.add(bombDistance - rangeMed);
			sensors.add(bombDistance - rangeLong);

			// bomb bearing
			Vector2D dir = this.getDirection().scale(-1);
			// flip point when straight ahead
			int bearingMinMax = 20;
			double BombBearingFront = dir.getAngleTo(toBomb) * bearingMinMax / Math.PI;
			sensors.add(BombBearingFront);

			// flip point when at 90 degrees
			double bombBearingSide1 = dir.rotate(Math.PI / 2).getAngleTo(toBomb) * bearingMinMax / Math.PI;
			sensors.add(bombBearingSide1);

			// flip point when at 180 degrees
			double bombBearingRear = dir.rotate(Math.PI).getAngleTo(toBomb) * bearingMinMax / Math.PI;
			sensors.add(bombBearingRear);

			// flip point when at 270 degrees
			double bombBearingSide2 = dir.rotate(3 * Math.PI / 2).getAngleTo(toBomb) * bearingMinMax / Math.PI;
			sensors.add(bombBearingSide2);
		}
		// bomb site sensors
		{
			Vector2D toBombSite = this.getPosition().getCentre().sub(game.bombSite.getPosition().getCentre());
			double bombSiteDistance = Math.sqrt(toBombSite.getLengthSquared());
			// bomb site distance
			sensors.add(bombSiteDistance - rangeShort);
			sensors.add(bombSiteDistance - rangeMed);
			sensors.add(bombSiteDistance - rangeLong);

			// bomb site bearing
			Vector2D dir = this.getDirection().scale(-1);
			// flip point when straight ahead
			int bearingMinMax = 20;
			double BombBearingFront = dir.getAngleTo(toBombSite) * bearingMinMax / Math.PI;
			sensors.add(BombBearingFront);

			// flip point when at 90 degrees
			double bombBearingSide1 = dir.rotate(Math.PI / 2).getAngleTo(toBombSite) * bearingMinMax / Math.PI;
			sensors.add(bombBearingSide1);

			// flip point when at 180 degrees
			double bombBearingRear = dir.rotate(Math.PI).getAngleTo(toBombSite) * bearingMinMax / Math.PI;
			sensors.add(bombBearingRear);

			// flip point when at 270 degrees
			double bombBearingSide2 = dir.rotate(3 * Math.PI / 2).getAngleTo(toBombSite) * bearingMinMax / Math.PI;
			sensors.add(bombBearingSide2);
		}
		return sensors;
	}

	/**
	 * Get the reward this agent received from it's previous action.
	 * 
	 * @return
	 */
	public double getReward() {
		return this.reward;
	}

	/**
	 * Damage this agent, returning whether it was killed or not.
	 * 
	 * @param damage
	 * @return True if this damage killed the agent.
	 */
	public boolean damage(int damage) {
		health -= damage;
		if (health <= 0) {
			respawn();
			killed = true;
		}
		damaged = true;
		return killed;
	}

	/**
	 * Do what an agent does every time step (ie heal, laser cool down etc).
	 */
	public void runGameStep() {
		if (health < DemolitionGame.agentMaxHealth) {
			health += DemolitionGame.agentHealRate;
		}

		if (laserHeat > 90) {
			laserOverheated = true;
		}
		if (laserHeat > 5) {
			laserHeat -= (laserOverheated ? 1 : 3);
		} else {
			laserOverheated = false;
		}

		if (fatigue > 90) {
			exhausted = true;
		}
		if (fatigue > 5) {
			fatigue -= (exhausted ? 4 : 2);
		} else {
			exhausted = false;
		}

		crashed = false;
		killed = false;
		damaged = false;
	}

	/**
	 * Set the reward this agent received from it's previous action.
	 * 
	 * @param reward
	 */
	public void setReward(double reward) {
		this.reward = reward;
	}

	/**
	 * Respawn this agent in it's team's respawn area.
	 */
	public void respawn() {
		boolean firstPass = true;

		this.health = DemolitionGame.agentMaxHealth;
		this.laserHeat = 0;
		this.fatigue = 0;
		experience = 0;

		while (firstPass) {
			this.deaths++;
			game.bomb.drop(this);
			long x, y;
			if (team == 0) {
				x = Math.round(DemolitionGame.agentRadius + Math.random() * DemolitionGame.agentRespawnAreaWidth);
				y = Math.round(DemolitionGame.agentRadius + Math.random() * DemolitionGame.agentRespawnAreaWidth);
			} else {
				x = Math.round(DemolitionGame.worldX - DemolitionGame.agentRadius
						- Math.random() * DemolitionGame.agentRespawnAreaWidth);
				y = Math.round(DemolitionGame.worldY - DemolitionGame.agentRadius
						- Math.random() * DemolitionGame.agentRespawnAreaWidth);
			}

			this.position = this.position.move(new Vector2D(x, y).sub(this.position.getCentre()));
			this.direction = this.direction.rotate(Math.random() * 2 * Math.PI);
			firstPass = false;
		}
	}

	/**
	 * Check for collision with a wall.
	 * 
	 * @param w
	 *            Wall of check collision with
	 * @return True if there is a collision.
	 */
	public boolean collide(Wall w) {
		Double[] ds = position.intersect(w.getLine());
		for (Double d : ds) {
			if (d != null && d > 0 && d < w.getLength())
				return true;
		}
		return false;
	}

	/**
	 * Check for collision with an agent.
	 * 
	 * @param a
	 *            Agent of check collision with
	 * @return True if there is a collision.
	 */
	public boolean collide(Agent a) {
		return position.intersect(a.getPosition());
	}

	/**
	 * Check for collision with a wall or agent.
	 * 
	 * @param e
	 *            Entity of check collision with
	 * @return True if there is a collision.
	 */
	public boolean collide(Entity e) {
		if (e instanceof Wall) {
			Wall w = (Wall) e;
			return this.collide(w);
		} else if (e instanceof Agent) {
			Agent a = (Agent) e;
			return this.collide(a);
		}
		return false;
	}

	@Override
	public void draw(Graphics2D g2d) {

		int d = (int) (position.getRadius() * 2);
		int x = (int) position.getCentre().getX();
		int y = (int) position.getCentre().getY();

		// experience circle
		{
			if (playerControlled) {
				g2d.setColor(new Color(1f, 1f, 0f, 0.2f + 0.5f * (float) (1 - Math.exp(-experience / 5.))));
			} else {
				g2d.setColor(new Color(1f, 1f, 1f, 0.5f * (float) (1 - Math.exp(-experience / 5.))));
			}
			g2d.setStroke(new BasicStroke(DemolitionGame.agentRadius / 2));
			g2d.drawOval(x - d / 2, y - d / 2, d, d);
		}

		// main circle
		{
			float fractionHealth = (float) this.health / DemolitionGame.agentMaxHealth;
			g2d.setColor(new Color(.5f * (team == 1 ? 1 : 0) + .3f * (team == 0 ? (1 - fractionHealth) : 0),
					.3f * (1 - fractionHealth),
					.5f * (team == 0 ? 1 : 0) + .3f * (team == 1 ? (1 - fractionHealth) : 0), 1f));

			g2d.setStroke(new BasicStroke(1));
			g2d.fillOval(x - d / 2, y - d / 2, d, d);
		}
		// gun
		{
			Vector2D dir = direction.scale(position.getRadius());
			int x2 = (int) (x + dir.getX());
			int y2 = (int) (y + dir.getY());

			g2d.setStroke(new BasicStroke(3));
			float b;
			if (laserOverheated) {
				b = (200 - laserHeat) / 200f - 0.5f;

			} else {
				b = (200 - laserHeat) / 200f;
			}
			g2d.setColor(new Color(b, b, b, 1f));
			g2d.drawLine(x, y, x2, y2);
		}

		// sensor pointer

		{
			Vector2D dir = this.getDirection().rotate(-DemolitionGame.sensorAngle / 2.);
			for (int i = 0; i < DemolitionGame.sensorsNumber; i++) {
				Vector2D dir2 = dir.scale(sensorDistances[i]);
				int x2 = (int) (x + dir2.getX());
				int y2 = (int) (y + dir2.getY());
				float r = .5f * (sensorEnemy[i] > 0 ? 1 : 0) * (team == 0 ? 1 : 0)
						+ ((sensorAgent[i] + sensorEnemy[i]) == 0 ? 1 : 0) * (team == 1 ? 1 : 0);
				float g = 0f;
				float b = .5f * (sensorEnemy[i] > 0 ? 1 : 0) * (team == 1 ? 1 : 0)
						+ ((sensorAgent[i] + sensorEnemy[i]) == 0 ? 1 : 0) * (team == 0 ? 1 : 0);
				g2d.setColor(new Color(r, g, b, 0.03f + (playerControlled ? 0.05f : 0f)));
				g2d.drawLine(x, y, x2, y2);
				double rotationScalar = i == 0 ? 5
						: i == 1 ? 5 * (4. / 3.)
								: i == 2 ? 5 * (4. / 2.)
										: i == 3 ? 5 * (4. / 1.)
												: i == 4 ? 5 * (4. / 1.)
														: i == 5 ? 5 * (4. / 2.) : i == 6 ? 5 * (4. / 3.) : 5;
				dir = dir.rotate(DemolitionGame.sensorAngle * 1. / rotationScalar);
			}
		}

		/*
		 * { Vector2D dir =
		 * direction.scale(sensorDistances[DemolitionGame.sensorsNumber / 2]);
		 * int x2 = (int) (x + dir.getX()); int y2 = (int) (y + dir.getY());
		 * float r = .5f * (sensorEnemy[DemolitionGame.sensorsNumber / 2] > 0 ?
		 * 1 : 0) * (team == 0 ? 1 : 0) +
		 * ((sensorAgent[DemolitionGame.sensorsNumber / 2] +
		 * sensorEnemy[DemolitionGame.sensorsNumber / 2]) == 0 ? 1 : 0) * (team
		 * == 1 ? 1 : 0); float g = 0f; float b = .5f *
		 * (sensorEnemy[DemolitionGame.sensorsNumber / 2] > 0 ? 1 : 0) * (team
		 * == 1 ? 1 : 0) + ((sensorAgent[DemolitionGame.sensorsNumber / 2] +
		 * sensorEnemy[DemolitionGame.sensorsNumber / 2]) == 0 ? 1 : 0) * (team
		 * == 0 ? 1 : 0); g2d.setColor(new Color(r, g, b, 0.05f +
		 * (playerControlled ? 0.05f : 0f))); g2d.drawLine(x, y, x2, y2); }
		 */

		// fatigue circle
		{
			float b;

			b = (100 - fatigue) / 200f;
			g2d.setColor(new Color(b, b, b, 1f));
			g2d.setStroke(new BasicStroke(1));
			g2d.fillOval(x - d / 4, y - d / 4, d / 2, d / 2);

			b = exhausted ? 0 : 1;
			g2d.setColor(new Color(b, b, b, 1f));
			g2d.drawOval(x - d / 4, y - d / 4, d / 2, d / 2);
		}
	}
}
