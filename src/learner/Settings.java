package learner;

/**
 * Class to store possible settings for a SARSALearner.
 * 
 * @author Alex Braithwaite
 *
 */
public class Settings {

	public int hiddenNodes;
	public double alpha;
	public double gamma;
	public double lambda;
	public double T;
	public double epsilon;
	public double alphaDecayRate;
	public double lambdaDecayRate;
	public double TDecayRate;
	public double epsilonDecayRate;
	public int numAgents;
	public int numInputs;
	public int numOutputs;
	public int memoryLength;
	public int memoryInterval;

	/**
	 * 
	 * @param hiddenNodes
	 *            Hidden nodes in neural network, 0 < hiddenNodes.
	 * @param alpha
	 *            Learning rate, 0 <= alpha.
	 * @param gamma
	 *            Discount factor, 0 <= gamma <= 1. Determines importance of
	 *            future rewards. 0 makes the agent short sighted, whilst when
	 *            gamma approaches 1 the agent strives for a long term reward.
	 * @param lambda
	 *            Temporal difference, 0 <= lambda <= 1. Determines how an error
	 *            at a given time step feeds back to previous estimates. A value
	 *            of 0 leads to no feedback beyond the current time step, whilst
	 *            approaching 1 leads to a longer decay.
	 * @param T
	 *            Randomness associated with action selection, used for
	 *            Boltzmann randomness. Values close to 0 indicate little
	 *            randomness, whilst higher values lead to more likelihood of
	 *            choosing actions which are apparently non-optimal.
	 * @param epsilon
	 *            Randomness associated with action selection, epsilon greedy
	 *            strategy. Chance epsilon of choosing some random action.
	 * @param alphaDecayRate
	 *            Rate at which alpha decays per time step. Rate of 0 gives no
	 *            decay.
	 * @param lambdaDecayRate
	 *            Rate at which lambda decays per time step. Rate of 0 gives no
	 *            decay.
	 * @param TDecayRate
	 *            Rate at which T decays per time step. Rate of 0 gives no
	 *            decay.
	 * @param epsilonDecayRate
	 *            Rate at which epsilon decays per time step. Rate of 0 gives no
	 *            decay.
	 * @param numAgents
	 *            Number of agents using this learner.
	 * @param numInputs
	 *            Number of inputs to the learner.
	 * @param numOutputs
	 *            Number of outputs for the learner.
	 * @param memoryLength
	 *            Total length of memory stored. 0 means the learner only knows
	 *            about it's current state.
	 * @param memoryInterval
	 *            Interval at which inputs are recorded to memory.
	 */
	public Settings(int hiddenNodes, double alpha, double gamma, double lambda, double T, double epsilon,
			double alphaDecayRate, double lambdaDecayRate, double TDecayRate, double epsilonDecayRate, int numAgents,
			int numInputs, int numOutputs, int memoryInterval, int memoryLength) {

		/*
		 * hiddenNodes = hiddenNodes < 1 ? 1 : hiddenNodes; alpha = alpha < 0 ?
		 * 0 : alpha; gamma = gamma < 0 ? 0 : gamma > 1 ? 1 : gamma; lambda =
		 * lambda < 0 ? 0 : lambda > 1 ? 1 : lambda; T = T < 0 ? 0 : T; epsilon
		 * = epsilon < 0 ? 0 : epsilon > 1 ? 1 : epsilon;
		 */

		this.hiddenNodes = hiddenNodes;
		this.alpha = alpha;
		this.gamma = gamma;
		this.lambda = lambda;
		this.T = T;
		this.epsilon = epsilon;
		this.alphaDecayRate = alphaDecayRate;
		this.lambdaDecayRate = lambdaDecayRate;
		this.TDecayRate = TDecayRate;
		this.epsilonDecayRate = epsilonDecayRate;
		this.numAgents = numAgents;
		this.numInputs = numInputs;
		this.numOutputs = numOutputs;
		this.memoryInterval = memoryInterval;
		this.memoryLength = memoryLength;
	}

	/**
	 * Compare two Settings.
	 * 
	 * @param s
	 * @return
	 */
	public boolean magnitudeLessThanEqualTo(Settings s) {
		return Math.abs(this.hiddenNodes) <= Math.abs(s.hiddenNodes) && Math.abs(this.alpha) <= Math.abs(s.alpha)
				&& Math.abs(this.gamma) <= Math.abs(s.gamma) && Math.abs(this.lambda) <= Math.abs(s.lambda)
				&& Math.abs(this.T) <= Math.abs(s.T) && Math.abs(this.epsilon) <= Math.abs(s.epsilon)
				&& Math.abs(this.alphaDecayRate) <= Math.abs(s.alphaDecayRate)
				&& Math.abs(this.lambdaDecayRate) <= Math.abs(s.lambdaDecayRate)
				&& Math.abs(this.TDecayRate) <= Math.abs(s.TDecayRate)
				&& Math.abs(this.epsilonDecayRate) <= Math.abs(s.epsilonDecayRate)
				&& Math.abs(this.numAgents) <= Math.abs(s.numAgents)
				&& Math.abs(this.numInputs) <= Math.abs(s.numInputs)
				&& Math.abs(this.numOutputs) <= Math.abs(s.numOutputs)
				&& Math.abs(this.memoryInterval) <= Math.abs(s.memoryInterval)
				&& Math.abs(this.memoryLength) <= Math.abs(s.memoryLength);
	}

	/**
	 * Add all Settings together.
	 * 
	 * @param s
	 * @return
	 */
	public Settings add(Settings s) {
		int hiddenNodes = this.hiddenNodes + s.hiddenNodes;
		double alpha = this.alpha + s.alpha;
		double gamma = this.gamma + s.gamma;
		double lambda = this.lambda + s.lambda;
		double T = this.T + s.T;
		double epsilon = this.epsilon + s.epsilon;
		double alphaDecayRate = this.alphaDecayRate + s.alphaDecayRate;
		double lambdaDecayRate = this.lambdaDecayRate + s.lambdaDecayRate;
		double TDecayRate = this.TDecayRate + s.TDecayRate;
		double epsilonDecayRate = this.epsilonDecayRate + s.epsilonDecayRate;
		int numAgents = this.numAgents + s.numAgents;
		int numInputs = this.numInputs + s.numInputs;
		int numOutputs = this.numOutputs + s.numOutputs;
		int memoryInterval = this.memoryInterval + s.memoryInterval;
		int memoryLength = this.memoryLength + s.memoryLength;
		return new Settings(hiddenNodes, alpha, gamma, lambda, T, epsilon, alphaDecayRate, lambdaDecayRate, TDecayRate,
				epsilonDecayRate, numAgents, numInputs, numOutputs, memoryInterval, memoryLength);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param s
	 * @return
	 */
	public Settings sub(Settings s) {
		int hiddenNodes = this.hiddenNodes - s.hiddenNodes;
		double alpha = this.alpha - s.alpha;
		double gamma = this.gamma - s.gamma;
		double lambda = this.lambda - s.lambda;
		double T = this.T - s.T;
		double epsilon = this.epsilon - s.epsilon;
		double alphaDecayRate = this.alphaDecayRate - s.alphaDecayRate;
		double lambdaDecayRate = this.lambdaDecayRate - s.lambdaDecayRate;
		double TDecayRate = this.TDecayRate - s.TDecayRate;
		double epsilonDecayRate = this.epsilonDecayRate - s.epsilonDecayRate;
		int numAgents = this.numAgents - s.numAgents;
		int numInputs = this.numInputs - s.numInputs;
		int numOutputs = this.numOutputs - s.numOutputs;
		int memoryInterval = this.memoryInterval - s.memoryInterval;
		int memoryLength = this.memoryLength - s.memoryLength;
		return new Settings(hiddenNodes, alpha, gamma, lambda, T, epsilon, alphaDecayRate, lambdaDecayRate, TDecayRate,
				epsilonDecayRate, numAgents, numInputs, numOutputs, memoryInterval, memoryLength);
	}

	/**
	 * Scale all settings by a constant scalar.
	 * 
	 * @param s
	 * @return
	 */
	public Settings scale(double s) {
		int hiddenNodes = (int) Math.round(this.hiddenNodes * s);
		double alpha = this.alpha * s;
		double gamma = this.gamma * s;
		double lambda = this.lambda * s;
		double T = this.T * s;
		double epsilon = this.epsilon * s;
		double alphaDecayRate = this.alphaDecayRate * s;
		double lambdaDecayRate = this.lambdaDecayRate * s;
		double TDecayRate = this.TDecayRate * s;
		double epsilonDecayRate = this.epsilonDecayRate * s;
		int numAgents = (int) Math.round(this.numAgents * s);
		int numInputs = (int) Math.round(this.numInputs * s);
		int numOutputs = (int) Math.round(this.numOutputs * s);
		int memoryInterval = (int) Math.round(this.memoryInterval * s);
		int memoryLength = (int) Math.round(this.memoryLength * s);
		return new Settings(hiddenNodes, alpha, gamma, lambda, T, epsilon, alphaDecayRate, lambdaDecayRate, TDecayRate,
				epsilonDecayRate, numAgents, numInputs, numOutputs, memoryInterval, memoryLength);
	}

	/**
	 * Scale each setting by a unique scalar.
	 * @param h Hidden Nodes scalar
	 * @param a alpha scalar
	 * @param g gamma scalar
	 * @param l lambda scalar
	 * @param t T scalar
	 * @param e epsilon scalar
	 * @param ad alpha decay scalar
	 * @param ld lambda decay scalar
	 * @param td T decay scalar
	 * @param ed epsilon decay scalar
	 * @param na number agents scalar
	 * @param ni number inputs scalar
	 * @param no number outputs scalar
	 * @param mi memory interval scalar
	 * @param ml memory length scalar
	 * @return
	 */
	public Settings scaleElements(double h, double a, double g, double l, double t, double e, double ad, double ld,
			double td, double ed, double na, double ni, double no, double mi, double ml) {
		int hiddenNodes = (int) Math.round(this.hiddenNodes * h);
		double alpha = this.alpha * a;
		double gamma = this.gamma * g;
		double lambda = this.lambda * l;
		double T = this.T * t;
		double epsilon = this.epsilon * e;
		double alphaDecayRate = this.alphaDecayRate * ad;
		double lambdaDecayRate = this.lambdaDecayRate * ld;
		double TDecayRate = this.TDecayRate * td;
		double epsilonDecayRate = this.epsilonDecayRate * ed;
		int numAgents = (int) Math.round(this.numAgents * na);
		int numInputs = (int) Math.round(this.numInputs * ni);
		int numOutputs = (int) Math.round(this.numOutputs * no);
		int memoryInterval = (int) Math.round(this.memoryInterval * mi);
		int memoryLength = (int) Math.round(this.memoryLength * ml);
		return new Settings(hiddenNodes, alpha, gamma, lambda, T, epsilon, alphaDecayRate, lambdaDecayRate, TDecayRate,
				epsilonDecayRate, numAgents, numInputs, numOutputs, memoryInterval, memoryLength);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(T);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(TDecayRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(alpha);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(alphaDecayRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(epsilon);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(epsilonDecayRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(gamma);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + hiddenNodes;
		temp = Double.doubleToLongBits(lambda);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(lambdaDecayRate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + memoryInterval;
		result = prime * result + memoryLength;
		result = prime * result + numAgents;
		result = prime * result + numInputs;
		result = prime * result + numOutputs;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Settings other = (Settings) obj;
		if (Double.doubleToLongBits(T) != Double.doubleToLongBits(other.T))
			return false;
		if (Double.doubleToLongBits(TDecayRate) != Double.doubleToLongBits(other.TDecayRate))
			return false;
		if (Double.doubleToLongBits(alpha) != Double.doubleToLongBits(other.alpha))
			return false;
		if (Double.doubleToLongBits(alphaDecayRate) != Double.doubleToLongBits(other.alphaDecayRate))
			return false;
		if (Double.doubleToLongBits(epsilon) != Double.doubleToLongBits(other.epsilon))
			return false;
		if (Double.doubleToLongBits(epsilonDecayRate) != Double.doubleToLongBits(other.epsilonDecayRate))
			return false;
		if (Double.doubleToLongBits(gamma) != Double.doubleToLongBits(other.gamma))
			return false;
		if (hiddenNodes != other.hiddenNodes)
			return false;
		if (Double.doubleToLongBits(lambda) != Double.doubleToLongBits(other.lambda))
			return false;
		if (Double.doubleToLongBits(lambdaDecayRate) != Double.doubleToLongBits(other.lambdaDecayRate))
			return false;
		if (memoryInterval != other.memoryInterval)
			return false;
		if (memoryLength != other.memoryLength)
			return false;
		if (numAgents != other.numAgents)
			return false;
		if (numInputs != other.numInputs)
			return false;
		if (numOutputs != other.numOutputs)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Settings [hiddenNodes=" + hiddenNodes + ", alpha=" + alpha + ", gamma=" + gamma + ", lambda=" + lambda
				+ ", T=" + T + ", epsilon=" + epsilon + ", alphaDecayRate=" + alphaDecayRate + ", lambdaDecayRate="
				+ lambdaDecayRate + ", TDecayRate=" + TDecayRate + ", epsilonDecayRate=" + epsilonDecayRate
				+ ", numAgents=" + numAgents + ", numInputs=" + numInputs + ", numOutputs=" + numOutputs
				+ ", memoryLength=" + memoryLength + ", memoryInterval=" + memoryInterval + "]";
	}

}