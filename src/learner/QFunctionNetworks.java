package learner;

import java.util.ArrayList;

/**
 * Class for a group of Neural Networks to be used by a SARSA Learner.
 * 
 * @author Alex Braithwaite
 * 
 */
public class QFunctionNetworks implements FunctionApproximator {
	
	private ArrayList<QNetwork> networks;

	/**
	 * Initialise the Q function approximator.
	 * 
	 * @param hiddenNodes
	 *            Number of hidden nodes to use for this network.
	 * @param numInputs
	 *            Size of state space.
	 * @param numOutputs
	 *            Number of possible actions.
	 * @param hiddenNodes
	 *            Number of hidden nodes to use for this network.
	 * @param numAgents
	 *            Number of agents to be using this.
	 */
	public QFunctionNetworks(int hiddenNodes, int numInputs, int numOutputs, int numAgents) {

		numInputs++;
		
		networks = new ArrayList<QNetwork>(numOutputs);
		
		for (int i = 0; i < numOutputs; i++) {
			networks.add(new QNetwork(hiddenNodes, numInputs, numAgents));
		}
	}

	@Override
	public void update(int agentNum, double alpha, double delta) {
		for (QNetwork n : networks) {
			n.update(agentNum, alpha, delta);
		}
	}

	@Override
	public double get(SAPair<Double> saPair) {
		return networks.get(saPair.action).get(saPair);
	}

	@Override
	public void accumulateEligibilities(int agentNum, SAPair<Double> saPair) {
		networks.get(saPair.action).accumulateEligibilities(agentNum, saPair);
	}

	@Override
	public void degradeEligibilities(int agentNum, double gamma, double lambda) {
		for (QNetwork n : networks) {
			n.degradeEligibilities(agentNum, gamma, lambda);
		}
	}

	@Override
	public void resetEligibilities(int agentNum) {
		for (QNetwork n : networks) {
			n.resetEligibilities(agentNum);
		}
	}

	@Override
	public String toString() {
		String s = "";
		for (QNetwork n : networks) {
			s += n.toString();
			s += "\n***************************\n";
		}
		s += "\n*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*\n";
		return s;
	}

}
