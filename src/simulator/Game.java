package simulator;

import learner.Learner;

/**
 * Game interface to be used for games to be run in the Simulator.
 * 
 * @author Alex Braithwaite
 *
 */
public interface Game {

	/**
	 * Set the learner to be used for this game
	 */
	void setLeaner(Learner brain);

	/**
	 * Method to initialise the game.
	 */
	void setupGame();
	
	/**
	 * Remove any windows etc initialised by game.
	 */
	void teardown();

	/**
	 * Method to run a full length game.
	 * 
	 * @return Total reward accumulated.
	 */

	double runTrial();

}
