package learner;

/**
 * Generic function approximator for a Q function, allowing use by multiple
 * agents.
 * 
 * @author Alex Braithwaite
 * 
 */
public interface FunctionApproximator {

	/**
	 * Update function to try and give SAPair given a value closer to Q, using
	 * agentNum's eligibilities.
	 * 
	 * @param agentNum
	 * @param alpha
	 * @param delta
	 */
	public void update(int agentNum, double alpha, double delta);

	/**
	 * Get the Q value for a given SAPair.
	 * 
	 * @param saPair
	 * @return
	 */
	public double get(SAPair<Double> saPair);

	/**
	 * Increase an agent's eligibility for a given agent using this function
	 * approximator.
	 * 
	 * @param agentNum
	 *            Agent's eligibility to change.
	 */
	public void accumulateEligibilities(int agentNum, SAPair<Double> saPair);

	/**
	 * Degrade eligibility for a given agent using this function approximator.
	 * 
	 * @param agentNum
	 *            Agent's eligibility to change.
	 */
	public void degradeEligibilities(int agentNum, double gamma, double lambda);

	/**
	 * Reset eligibility for a given agent using this function approximator.
	 * 
	 * @param agentNum
	 *            Agent's eligibility to change.
	 */
	public void resetEligibilities(int agentNum);

}
