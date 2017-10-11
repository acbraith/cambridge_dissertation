package learner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Sarsa Learner using an arbitrary function approximator to represent the
 * Q-function.
 * 
 * @author Alex Braithwaite
 * 
 */
public class SarsaLambda implements Learner {

	/**
	 * Q-function. qFunction.get(SAPair) is expected future reward for a
	 * state-action pair.
	 */
	private FunctionApproximator qFunction;

	public Settings settings;

	private double decayedAlpha;
	private double decayedLambda;
	private double decayedT;
	private double decayedEpsilon;

	/**
	 * State-action pair for previous time step.
	 */
	private List<SAPair<Double>> previousSAPair;

	/**
	 * Queue of agent's memory.
	 */
	private List<Queue<List<Double>>> memory;

	/**
	 * Time steps for each agent.
	 */
	private List<Integer> timeSteps;

	/**
	 * Constructor for a Sarsa learner.
	 * 
	 * @param s
	 *            Sarsa parameters.
	 */
	public SarsaLambda(Settings s) {

		this.settings = s;

		// XXX Can change this to any other FunctionApproximator here
		this.qFunction = new QFunctionNetworks(settings.hiddenNodes < 1 ? 1 : settings.hiddenNodes,
				settings.numInputs * (1 + settings.memoryLength), settings.numOutputs, settings.numAgents);
		// this.qFunction = new QFunctionTable(settings.numAgents);
		// this.qFunction = new QFunctionCache(settings.numAgents);
		
		this.decayedAlpha = settings.alpha;
		this.decayedLambda = settings.lambda;
		this.decayedEpsilon = settings.epsilon;
		this.decayedT = settings.T;

		this.reset();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param sl
	 */
	public SarsaLambda(SarsaLambda sl) {
		this.settings = sl.settings;

		// XXX Can change this to any other FunctionApproximator here
		this.qFunction = new QFunctionNetworks(settings.hiddenNodes, settings.numInputs * (1 + settings.memoryLength),
				settings.numOutputs, settings.numAgents);
		// this.qFunction = new QFunctionTable(settings.numAgents);
		// this.qFunction = new QFunctionCache(settings.numAgents);

		this.decayedAlpha = settings.alpha;
		this.decayedEpsilon = settings.epsilon;
		this.decayedT = settings.T;

		this.reset();
	}

	/**
	 * Convert a queue of memories to a list to be used by the function
	 * approximator.
	 * 
	 * @param memory
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Double> memoryToState(Queue<List<Double>> memory) {
		// convert a queue of memories into a list for use in the function
		// approximator
		List<Double> state = new ArrayList<Double>(settings.numInputs * (1 + settings.memoryLength));
		Object[] memories = memory.toArray();
		for (Object m : memories) {
			state.addAll((Collection<? extends Double>) m);
		}
		return state;
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getAction(List<Double> currentState, double reward, int agentNum) {

		// update memory
		timeSteps.set(agentNum, timeSteps.get(agentNum) + 1);
		if (timeSteps.get(agentNum) % (settings.memoryInterval == 0 ? 1 : settings.memoryInterval) == 0) {
			memory.get(agentNum).add(currentState);
			memory.get(agentNum).remove();
		}

		// if memory not full, exit
		for (Object m : memory.toArray()) {
			if (((Collection<? extends Double>) m).contains(null))
				return 0;
		}

		// get current state including memory
		List<Double> state = memoryToState(memory.get(agentNum));
		state.addAll(currentState);

		// select max Q
		double qMax = 0;
		int actionMax = 0;
		double[] Q = new double[settings.numOutputs];
		for (int i = 0; i < settings.numOutputs; i++) {
			SAPair<Double> sa = new SAPair<Double>(state, i);
			Q[i] = qFunction.get(sa);

			if (Q[i] > qMax) {
				qMax = Q[i];
				actionMax = i;
			}
		}

		// exploration policy
		// XXX to change this to Q-learning, move this section to the end (just
		// after update)
		// Boltzmann distribution strategy
		double[] p = new double[settings.numOutputs];
		double totalP = 0;
		for (int i = 0; i < settings.numOutputs; i++) {
			p[i] = Math.exp(Q[i] / decayedT);
			totalP += p[i];
		}
		for (int i = 0; i < settings.numOutputs; i++) {
			p[i] /= totalP;
			if (i > 0) {
				p[i] += p[i - 1];
			}
		}
		double r = Math.random();
		for (int i = 0; i < settings.numOutputs; i++) {
			if (r < p[i]) {
				actionMax = i;
				qMax = Q[i];
				break;
			}
		}

		// epsilon greedy strategy
		if (Math.random() < decayedEpsilon) {
			actionMax = (int) (Math.random() * settings.numOutputs);
			qMax = Q[actionMax];
		}

		// Perform the update equation
		forceAction(state, reward, agentNum, actionMax);

		// return action to do
		return actionMax;
	}

	@Override
	public int getAction(List<Double> state, double reward) {
		return getAction(state, reward, 0);
	}

	@Override
	public void reset() {
		// reset decayed variables

		// generate initial state
		ArrayList<Double> initialState = new ArrayList<Double>(settings.numInputs * (1 + settings.memoryLength));
		for (int j = 0; j < settings.numInputs; j++) {
			initialState.add(1.0);
		}

		// reset all memory
		memory = new ArrayList<Queue<List<Double>>>(settings.numAgents);
		for (int i = 0; i < settings.numAgents; i++) {
			memory.add(new LinkedList<List<Double>>());
			for (int j = 0; j < settings.memoryLength; j++) {
				memory.get(i).add(initialState);
			}
		}

		List<Double> initialStateMemory = memoryToState(memory.get(0));
		initialStateMemory.addAll(initialState);

		// reset previous state-action pair and eligibilities
		previousSAPair = new ArrayList<SAPair<Double>>(settings.numAgents);
		for (int i = 0; i < settings.numAgents; i++) {
			qFunction.resetEligibilities(i);
			previousSAPair.add(null);
		}

		// reset all time steps
		timeSteps = new ArrayList<Integer>(settings.numAgents);
		for (int i = 0; i < settings.numAgents; i++) {
			timeSteps.add(0);
		}
	}

	private void degradeAlpha(double s) {
		decayedAlpha = decayedAlpha * (1 - s);
	}

	private void degradeLambda(double s) {
		decayedLambda = decayedLambda * (1 - s);
	}

	private void degradeT(double s) {
		decayedT = decayedT * (1 - s);
	}

	private void degradeEpsilon(double s) {
		decayedEpsilon = decayedEpsilon * (1 - s);
	}

	@Override
	public List<Double> evaluateState(List<Double> state) {
		List<Double> qs = new ArrayList<Double>(settings.numOutputs);

		for (int i = 0; i < settings.numOutputs; i++) {
			SAPair<Double> sa = new SAPair<Double>(state, i);
			qs.add(qFunction.get(sa));
		}

		return qs;
	}

	@Override
	public void forceAction(List<Double> state, double reward, int agentNum, int action) {
		SAPair<Double> saCurrent = new SAPair<Double>(state, action);
		SAPair<Double> saPrev = previousSAPair.get(agentNum);

		// can only update once we have had previous experiences
		if (saPrev != null) {
			// update eligibilities for previous state-action pair
			qFunction.accumulateEligibilities(agentNum, saPrev);

			double Q = qFunction.get(saCurrent);

			// update Q function and eligibilities for given agent
			double delta = reward + settings.gamma * Q - qFunction.get(saPrev);
			qFunction.update(agentNum, decayedAlpha, delta);
			qFunction.degradeEligibilities(agentNum, settings.gamma, settings.lambda);
		}

		// update previous SAPair
		previousSAPair.set(agentNum, saCurrent);

		// decay alpha, lambda, T, epsilon
		degradeAlpha(settings.alphaDecayRate);
		degradeLambda(settings.lambdaDecayRate);
		degradeT(settings.TDecayRate);
		degradeEpsilon(settings.epsilonDecayRate);
	}

	@Override
	public String toString() {
		return qFunction.toString();
	}

}
