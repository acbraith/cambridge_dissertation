package learner;

import java.util.List;

/**
 * Interface for a Reinforcement Learning algorithm.
 * 
 * @author Alex Braithwaite
 *
 */
public interface Learner {

	/**
	 * Let the Q-function evaluate a given state without updating anything.
	 * 
	 * @param state
	 * @return List of all outputs.
	 */
	public List<Double> evaluateState(List<Double> state);

	/**
	 * Get action for current time step and learn from action in previous time
	 * step.
	 * 
	 * @param state
	 *            Inputs to use to decide action for current time step.
	 * @param reward
	 *            Reward from action in previous time step.
	 * @return Action to perform in current time step.
	 */
	public int getAction(List<Double> state, double reward);

	/**
	 * Get action for current time step and learn from action in previous time
	 * step.
	 * 
	 * @param state
	 *            Inputs to use to decide action for current time step.
	 * @param reward
	 *            Reward from action in previous time step.
	 * @param agentNum
	 *            Agent to perform time step.
	 * @return Action to perform in current time step.
	 */
	public int getAction(List<Double> state, double reward, int agentNum);

	/**
	 * Analogous to the getAction method, but with the action
	 * pre-determined.
	 * 
	 * @param state
	 *            State in which the action will be taken.
	 * @param reward
	 *            Reward from previous action.
	 * @param agentNum
	 *            Agent to perform time step.
	 * @param action
	 *            Action the agent will take next.
	 */
	public void forceAction(List<Double> state, double reward, int agentNum, int action);

	/**
	 * Reset every agent using this learner.
	 */
	void reset();

}
