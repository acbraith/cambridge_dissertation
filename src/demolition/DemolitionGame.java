package demolition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import demolitionEntities.Agent;
import demolitionEntities.Bomb;
import demolitionEntities.BombSite;
import demolitionEntities.Entity;
import demolitionEntities.Laser;
import demolitionEntities.Wall;
import demolitionUtilities.Vector2D;
import learner.Learner;
import learner.SarsaLambda;
import simulator.Game;

/**
 * The Demolition Game class, manages playing of full Demolition Games and
 * higher level in game logic.
 * 
 * @author Alex Braithwaite
 *
 */
public class DemolitionGame implements Game {

	public static final double degToRad = Math.PI / 180.;

	/*
	 * World dimensions.
	 */
	public static final int worldX = 1200;
	public static final int worldY = 800;

	/*
	 * Game settings.
	 */
	public static final int numAgents = 24;
	public static final int numWalls = 20;
	public static final int wallMaxLength = 200;
	public static final int wallMinLength = 60;
	public static final int numBuildings = 0;
	public static final int buildingMaxLength = 200;
	public static final int buildingMinLength = 50;
	public static final int buildingDoorSize = 30;
	public static final int buildingMaxDoors = 4;
	public static final int buildingMinDoors = 1;

	public static final int FPS = 30;
	public static int currentFPS = FPS * 0;

	public static final int gameLength = 5000;
	
	/*
	 * Agent settings.
	 */
	public static final int agentMaxHealth = 1000;
	public static final int agentHealRate = 1;
	public static final int agentRadius = 20;
	public static final int agentForwardMoveRate = 10;
	public static final double agentTurnRate = 15 * degToRad;
	public static final double agentFireSpread = 5 * degToRad;

	public static final int agentRespawnAreaWidth = worldX / 10;

	/*
	 * Sensor settings.
	 */
	public static final int sensorMaxRange = worldX;
	public static final int sensorsEachSide = 4;
	public static final int sensorsNumber = 1 + 2 * sensorsEachSide;
	// 6 for each distance sensor (3 for distance, 3 for type)
	// 3 for health
	// 2 for laser heat
	// 1 for laser overheated
	// 2 for stamina
	// 1 for exhausted
	// 1 for team
	// 1 for carrying bomb
	// 1 for arming/disarming bomb
	// 1 for bomb armed
	// 1 for bomb carried
	// 3 for bomb distance
	// 4 for bomb bearing
	// 3 for bomb site distance
	// 4 for bomb site bearing
	public static final int sensorsTotal = 6 * sensorsNumber + 3 + 2 + 4 + 1 + 1 + 1 + 1 + 1 + 3 + 4 + 3 + 3 + 1;
	public static final double sensorAngle = 180 * degToRad;

	public static final int laserMaxRange = worldX;
	public static final int laserDamage = 150;
	public static final int crashDamage = 2;

	public static final int respawnFrames = 1;
	public static final int offMapDamage = agentMaxHealth / respawnFrames;

	/*
	 * Bomb settings.
	 */
	public static final int bombRadius = 7;
	public static final int bombFramesToPickup = 1;
	public static final int bombFramesToArm = 90;
	public static final int bombFramesToDefuse = 90;
	public static final int bombFramesToExplode = gameLength / 3;

	/*
	 * Reward settings.
	 */
	public static final double rewardDamageEnemy = 1; // RAW INPUT
	public static final double rewardDamageAlly = -rewardDamageEnemy / 2;
	public static final double rewardKillEnemy = rewardDamageEnemy * 10;
	public static final double rewardKillAlly = -rewardKillEnemy / 2;
	public static final double rewardKillBombCarrier = rewardKillEnemy * 2;
	public static final double rewardGetHit = 0;
	public static final double rewardDie = -rewardKillEnemy;
	public static final double rewardMiss = rewardDamageAlly / 10;
	public static final double rewardMove = 0;
	public static final double rewardCrash = rewardDamageAlly / 10;

	public static final double rewardApproachBomb = rewardDamageEnemy / 5;
	public static final double rewardAttackerApproachBombSite = rewardApproachBomb * 2;
	public static final double rewardDefenderApproachBombSite = rewardApproachBomb;
	public static final double rewardCarryingBomb = rewardApproachBomb * numAgents / 2.;
	public static final double rewardArmingBomb = rewardDefenderApproachBombSite * numAgents / 2.;
	public static final double rewardDefusingBomb = rewardAttackerApproachBombSite * numAgents / 2.;
	public static final double rewardArmedBomb = rewardArmingBomb * bombFramesToArm * numAgents / 2.;
	public static final double rewardDefusedBomb = rewardDefusingBomb * bombFramesToDefuse * numAgents / 2.;
	public static final double rewardWinGame = 0;

	private double meanReward = 0.;

	public List<Entity> entities;
	public List<Wall> walls;
	public List<Agent> agents;
	public List<Laser> lasers;
	public Bomb bomb;
	double r = Math.random();
	public BombSite bombSite = new BombSite(new Vector2D(worldX - worldX * r, worldY * r));

	public Window window;

	public Canvas world;

	private Learner brain;
	private ArrayList<Learner> brains;

	private int turn;

	private boolean playerOn = false;

	public enum Action {
		moveForward, turnLeft, turnRight, shoot, moveBackward
	}

	/**
	 * Constructor method. Creates a window and canvas to draw on, and adds a
	 * key listener to that window.
	 */
	public DemolitionGame(boolean toDraw) {
		if (toDraw) {
			window = new Window(this);
		}
	}

	@Override
	public void setLeaner(Learner brain) {
		this.brain = brain;

		brains = new ArrayList<Learner>(numAgents);
		for (int i = 0; i < numAgents; i++) {
			brains.add(new SarsaLambda((SarsaLambda) brain));
		}
	}

	@Override
	public void teardown() {
		if (this.window != null)
			this.window.setVisible(false);
	}

	/**
	 * Setup a new game. Clears everything from the previous game, adds map
	 * edges, bomb and agents.
	 */
	public void setupGame() {
		entities = new LinkedList<Entity>();
		walls = new LinkedList<Wall>();
		agents = new LinkedList<Agent>();
		lasers = new LinkedList<Laser>();

		// bomb site
		double r = Math.random();
		bombSite = new BombSite(new Vector2D(worldX - worldX * r, worldY * r));
		entities.add(bombSite);

		// map edges
		entities.add(new Wall(new Vector2D(0, 0), new Vector2D(worldX, 0)));
		entities.add(new Wall(new Vector2D(0, 0), new Vector2D(0, worldY)));
		entities.add(new Wall(new Vector2D(worldX, 0), new Vector2D(worldX, worldY)));
		entities.add(new Wall(new Vector2D(0, worldY), new Vector2D(worldX, worldY)));

		// bomb
		bomb = new Bomb(new Vector2D(0, 0));
		entities.add(bomb);

		// agents
		for (int i = 0; i < numAgents; i++) {
			Agent a = new Agent(new Vector2D(0, 0), 0, i % 2, this);
			a.respawn();
			entities.add(a);
			agents.add(a);
		}
	}

	/**
	 * Add walls and buildings to the map.
	 */
	public void createMap() {
		// walls
		for (int i = 0; i < numWalls; i++) {
			double x = new Random().nextInt(worldX - 2 * agentRespawnAreaWidth) + agentRespawnAreaWidth;
			double y = new Random().nextInt(worldY);
			double len = wallMinLength + new Random().nextInt(wallMaxLength - wallMinLength);
			double angle = new Random().nextFloat() * 2 * Math.PI;
			Wall w = new Wall(new Vector2D(x, y), angle, len);
			entities.add(w);
			walls.add(w);
		}

		// buildings
		for (int i = 0; i < numBuildings; i++) {
			double x = new Random().nextInt(worldX);
			double y = new Random().nextInt(worldY);
			double length = buildingMinLength + new Random().nextInt(buildingMaxLength - buildingMinLength);
			double width = buildingMinLength + new Random().nextInt(buildingMaxLength - buildingMinLength);
			double angle = Math.random() * Math.PI * 2;

			double x2 = x + length * Math.cos(angle) + width * Math.cos(Math.PI / 2 - angle);
			double y2 = y;
			ArrayList<Wall> ws = new ArrayList<Wall>(4);
			ws.add(new Wall(new Vector2D(x, y), angle, length));
			ws.add(new Wall(new Vector2D(x, y), angle + Math.PI / 2, width));
			ws.add(new Wall(new Vector2D(x2, y2), angle, length));
			ws.add(new Wall(new Vector2D(x2, y2), angle + Math.PI / 2, width));
			for (Wall w : ws) {
				entities.add(w);
				walls.add(w);
			}
		}
	}

	/**
	 * Clear walls from map, add new walls, and respawn all agents.
	 */
	public void resetMap() {
		Iterator<Wall> itw = walls.listIterator();
		while (itw.hasNext()) {
			Wall w = itw.next();
			entities.remove(w);
		}
		walls = new LinkedList<Wall>();

		Iterator<Agent> ita = agents.listIterator();
		while (ita.hasNext()) {
			Agent a = ita.next();
			a.respawn();
		}

		entities.remove(bomb);
		bomb = new Bomb(new Vector2D(worldX - Math.random() * agentRespawnAreaWidth,
				worldY - Math.random() * agentRespawnAreaWidth));
		entities.add(bomb);

		entities.remove(bombSite);
		double r = Math.random();
		bombSite = new BombSite(new Vector2D(worldX - worldX * r, worldY * r));
		((LinkedList<Entity>) entities).addFirst(bombSite);

		createMap();
	}

	/**
	 * Listen for player inputs to move player controlled agent.
	 * 
	 * @return Action requested by player.
	 */
	public int getInputAction(Agent a) {
		int action = 0;

		// turn towards the mouse
		Vector2D mousePos = new Vector2D(window.mouseX, window.mouseY);
		Vector2D myPos = a.getPosition().getCentre();
		Vector2D myDir = a.getDirection();
		Vector2D meToMouse = mousePos.sub(myPos);
		if (myDir.getAngleTo(meToMouse) > DemolitionGame.agentFireSpread) {
			action += 1 << 3;
		} else if (myDir.getAngleTo(meToMouse) < -DemolitionGame.agentFireSpread) {
			action += 1 << 2;
		}

		// move and shoot (also depreiciated turn)
		if (window != null) {
			if (window.pressW)
				action += 1 << 0;
			if (window.pressA)
				action += 1 << 2;
			if (window.pressD)
				action += 1 << 3;
			if (window.mousePress)
				action += 1 << 1;
		}
		return action;
	}

	/**
	 * Clear all lasers.
	 */
	public void removeLasers() {
		Iterator<Laser> it = lasers.listIterator();
		while (it.hasNext()) {
			Laser l = it.next();
			entities.remove(l);
		}
		lasers = new LinkedList<Laser>();
	}

	/**
	 * Perform an agents action.
	 * 
	 * @param a
	 *            Agent performing action.
	 * @param action
	 *            Action to perform.
	 * @return Reward received by performing action.
	 */
	public double performAction(Agent a, int action) {

		double reward = 0;

		// used to calculate distance travelled over this time step, to give rewards
		double bombDistBefore = a.getPosition().getCentre().getDistanceTo(bomb.getPosition().getCentre());
		double bombSiteDistBefore = a.getPosition().getCentre().getDistanceTo(bombSite.getPosition().getCentre());

		int toMove;
		Vector2D direction;

		ArrayList<Action> actions = new ArrayList<Action>(Action.values().length);

		// multiple actions possible at once
		if ((action & (1 << 0)) != 0) {
			actions.add(Action.moveForward);
		}
		if ((action & (1 << 1)) != 0) {
			actions.add(Action.shoot);
		}
		if ((action & (1 << 2)) != 0) {
			actions.add(Action.turnRight);
		}
		if ((action & (1 << 3)) != 0) {
			actions.add(Action.turnLeft);
		}

		for (Action act : actions) {
			switch (act) {
			case moveForward:
				toMove = agentForwardMoveRate;
				direction = a.getDirection();
				a.move(toMove, direction);
				reward += rewardMove;
				break;
			case moveBackward:
				toMove = -agentForwardMoveRate / 3;
				direction = a.getDirection();
				a.move(toMove, direction);
				reward += rewardMove / 3;
				break;
			case turnLeft:
				a.turn(-agentTurnRate);
				break;
			case turnRight:
				a.turn(agentTurnRate);
				break;
			case shoot:
				if (bomb.getCarrier() != a) {
					// create a laser
					Laser l = a.shoot();
					if (l != null) {
						entities.add(l);
						lasers.add(l);
						// check for what the laser hit
						if (l.getHit() instanceof Agent) {
							// calculate damage
							Agent agentHit = (Agent) l.getHit();
							boolean hitBombCarrier = agentHit == bomb.getCarrier();
							boolean kill = false;
							int damage = (int) (laserDamage * (1 - Math.exp(-a.experience / 5.) / 3.));

							// assign rewards for damage done
							if (agentHit.getTeam() != a.getTeam() && agentHit.damage(damage))
								kill = true;
							if (((Agent) l.getHit()).getTeam() == a.getTeam()) {
								reward += rewardDamageAlly;
								if (kill)
									reward += rewardKillAlly;
							} else {
								reward += rewardDamageEnemy;
								if (kill) {
									reward += rewardKillEnemy;
									a.experience++;
									if (hitBombCarrier) {
										reward += rewardKillBombCarrier;
									}
								}
							}
						} else {
							reward += rewardMiss;
						}
					} else {
						reward += rewardMiss;
					}
				}
				break;
			default:
				break;
			}
		}

		// collision avoidance
		if (avoidCollision(a)) {
			a.crashed = true;
			reward += rewardCrash;
			a.damage(crashDamage);
		}

		if (a.killed) {
			reward += rewardDie;
		}
		if (a.damaged) {
			reward += rewardGetHit;
		}

		double bombDistAfter = a.getPosition().getCentre().getDistanceTo(bomb.getPosition().getCentre());
		double bombSiteDistAfter = a.getPosition().getCentre().getDistanceTo(bombSite.getPosition().getCentre());

		// bomb related rewards
		if (bomb.isCarried() && bomb.getCarrier() == a) {
			double delta = rewardAttackerApproachBombSite * (bombSiteDistBefore - bombSiteDistAfter)
					/ agentForwardMoveRate;
			delta *= (delta < 0 ? 1.1 : 1);
			reward += delta;
		} else if (bomb.getArmer() == a) {
			reward += rewardArmingBomb;
		} else if (bomb.getDefuser() == a) {
			reward += rewardDefusingBomb;
		}
		if (a.getTeam() == 1 && !bomb.isCarried()) {
			double delta = rewardApproachBomb * (bombDistBefore - bombDistAfter) / agentForwardMoveRate;
			delta *= (delta < 0 ? 1.1 : 1);
			reward += delta;
		} else if (a.getTeam() == 0 && bomb.isArmed()) {
			double delta = rewardDefenderApproachBombSite * (bombSiteDistBefore - bombSiteDistAfter)
					/ agentForwardMoveRate;
			delta *= (delta < 0 ? 1.1 : 1);
			reward += delta;
		}

		// reward += ((distBefore - distAfter) / 50.) / agentForwardMoveRate;

		return reward;
	}

	/**
	 * Check if an agent is colliding with a wall or another agent, and move
	 * then such that they are no longer colliding with the object.
	 * 
	 * @param a
	 *            Agent to check.
	 * @return True if collision occurred, else false.
	 */
	public boolean avoidCollision(Agent a) {
		boolean hit = false;
		for (Entity e2 : entities) {
			// collision with walls
			if (e2 instanceof Wall) {
				Wall w = (Wall) e2;
				Double[] ds = a.getPosition().intersect(w.getLine());
				if (ds[0] != null) {
					// check for collision
					boolean[] bs = { (ds[0] > 0 && ds[0] < w.getLength()), (ds[1] > 0 && ds[1] < w.getLength()) };
					if (bs[0] || bs[1]) {
						double lambda, distance;
						Vector2D collisionPoint, direction;
						if (bs[0] && bs[1]) {
							lambda = (ds[0] + ds[1]) / 2;
							collisionPoint = w.getLine().getStart().add(w.getLine().getDirection().scale(lambda));
						} else {
							lambda = bs[0] ? ds[0] : ds[1];
							if (lambda < w.getLength() / 2) {
								lambda /= 2;
							} else {
								lambda = (w.getLength() + lambda) / 2;
							}
							collisionPoint = w.getLine().getStart().add(w.getLine().getDirection().scale(lambda));
						}
						direction = a.getPosition().getCentre().sub(collisionPoint);
						distance = Math.sqrt(collisionPoint.getDistanceToSquared(a.getPosition().getCentre()));
						// move away from collision point
						a.forceMove(agentRadius - distance + 1, direction);
						hit = true;
					}
				}
			} else if (e2 instanceof Agent && e2 != a) {
				// collision with agents
				Agent a2 = (Agent) e2;
				if (a.collide(a2)) {
					Vector2D direction = a.getPosition().getCentre().sub(a2.getPosition().getCentre());
					@SuppressWarnings("unused")
					double distance = Math
							.sqrt(a.getPosition().getCentre().getDistanceToSquared(a2.getPosition().getCentre()));
					// move away from other agent
					a.forceMove(agentForwardMoveRate / 3, direction);
					hit = true;
				}
			}
		}
		return hit;
	}

	/**
	 * Check whether an agent is off the map.
	 * 
	 * @param a
	 *            Agent to check.
	 * @return True if agent is off the map, else false.
	 */
	public static boolean agentOffMap(Agent a) {
		double x = a.getPosition().getCentre().getX();
		double y = a.getPosition().getCentre().getY();
		return (x < 0 || x > worldX || y < 0 || y > worldY);
	}

	/**
	 * Check whether end-game conditions reached.
	 * 
	 * @return -1 if game not over, 1 if attackers win, 0 if defenders win.
	 */
	public int gameOver() {
		if (turn > gameLength || bomb.getDefused() || (turn > gameLength - bombFramesToExplode && !bomb.isArmed()))
			return 0;
		else if (bomb.getExploded())
			return 1;

		return -1;
	}

	/**
	 * Run one game step.
	 * 
	 * @return Total reward received by all agents this game step.
	 */
	public double runTimeStep() {

		double totalReward = 0;

		// Get the time that the frame started
		long start = System.nanoTime();

		// bomb arming
		bomb.doTimeStep(this);

		// team 0 = defenders
		// team 1 = attackers
		int randomTeam = -1;// XXX team to take random actions

		// give each agent inputs and get outputs
		int agentNum = 0;
		Iterator<Agent> it = agents.listIterator();
		List<Integer> actions = new LinkedList<Integer>();
		while (it.hasNext()) {
			Agent a = it.next();

			a.runGameStep();

			a.updateInputs();

			// if agent off map, move back onto map
			double x = a.getPosition().getCentre().getX();
			double y = a.getPosition().getCentre().getY();
			double x2 = x < 0 ? 1 : x > worldX ? worldX - 1 : x;
			double y2 = y < 0 ? 1 : y > worldY ? worldY - 1 : y;
			a.forceMove(x2 - x, new Vector2D(1, 0));
			a.forceMove(y2 - y, new Vector2D(0, 1));

			int action = 0;

			// XXX ACTION SELECTION
			if (a.getTeam() == randomTeam) { // default: 0 (ie defenders do
												// random actions)
				action = (int) (Math.random() * 12);
			} else {
				// XXX learn together or learn separately
				action = brain.getAction(a.getInputs(), a.getReward(), agentNum);

				// action = brains.get(agentNum).getAction(a.getInputs(),
				// a.getReward(), agentNum);
			}

			// inputs for player one
			if (agentNum == 1) {
				a.playerControlled = false;
				if (playerOn) {
					action = getInputAction(a);
					a.playerControlled = true;
				}
				if (window != null && window.pressInt == 1)
					playerOn = true;
				else if (window != null && window.pressInt != 1)
					playerOn = false;
			}

			actions.add(action);

			agentNum++;
		}

		// if we're drawing
		if (window != null) {

			// update FPS
			DemolitionGame.currentFPS = (int) Math.round(DemolitionGame.FPS * Math.pow(2, window.pressInt - 1));

			// Redraw the world
			if (window.pressInt != 0) {

				world.repaint();

				// sleep for necessary time
				long delta = (long) ((1000000000.0 / currentFPS) - (System.nanoTime() - start));
				if (delta > 0) {
					try {
						Thread.sleep(delta / 1000000L, (int) delta % 1000000);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		removeLasers();

		// perform agent outputs
		it = agents.listIterator();
		Iterator<Integer> actionIterator = actions.listIterator();
		while (it.hasNext()) {
			Agent a = it.next();
			int action = actionIterator.next();

			double reward = 0;
			reward = performAction(a, action);

			if (a.getTeam() != randomTeam) // only measure attacker rewards
				totalReward += reward;

			a.setReward(reward);
		}

		return totalReward;
	}

	/**
	 * Run a full game.
	 * 
	 * @return Total reward received by all agents over game.
	 */
	public double runTrial() {

		// reset everything
		brain.reset();
		for (Learner l : brains)
			l.reset();
		resetMap();
		turn = 0;
		double reward = 0;

		// run game
		while (gameOver() == -1) {
			reward += runTimeStep();
			turn++;
		}

		// get final reward
		for (int agentNum = 0; agentNum < numAgents; agentNum++) {
			Agent a = agents.get(agentNum);

			double finalReward = 0;
			if (a.getTeam() == gameOver()) {
				finalReward = rewardWinGame;
			}

			// give final reward to learner
			brain.getAction(a.getInputs(), finalReward, agentNum);
			reward += finalReward;

		}

		// reward processing
		double newReward = reward / turn;
		// attacker win rate
		// newReward = gameOver();
		int movingAverageRewardPeriod = 5;
		meanReward += (newReward - meanReward) / movingAverageRewardPeriod;

		return meanReward;
	}

}
