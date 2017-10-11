package learner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Simple table based Q-function.
 * 
 * @author Alex Braithwaite
 * 
 */
public class QFunctionTable implements FunctionApproximator {

	/**
	 * HashMap to store Q-function. Just store the hashes instead of actual keys
	 * to save some memory. Hopefully this is ok. To change to storing actual
	 * keys, replace all "Integer" with "SAPair<Double>" and modify
	 * inputProcessing function appropriately.
	 */
	private HashMap<Integer, Float> table;

	/**
	 * Starting value for every element of Q-function table.
	 */
	private float initialQ = 50f;

	/**
	 * Eligibilities of each state action pair for each agent.
	 * e.get(agentNum).get(SAPair) is eligibility of that SA pair for the given
	 * agent.
	 */
	private ArrayList<Map<Integer, Double>> e;

	/**
	 * Minimum eligibility before being ignored.
	 */
	private double eligibilityMin = 0.01;

	/**
	 * Number of agents using this function approximator.
	 */
	@SuppressWarnings("unused")
	private int numAgents;

	public QFunctionTable(int numAgents) {
		this.numAgents = numAgents;

		table = new HashMap<Integer, Float>();

		e = new ArrayList<Map<Integer, Double>>(numAgents);

		for (int i = 0; i < numAgents; i++) {
			e.add(new HashMap<Integer, Double>());
		}
	}

	/**
	 * Convert raw state representation to how we want to store it.
	 * 
	 * @param saPair
	 * @return
	 */
	private Integer inputProcessing(SAPair<Double> saPair) {
		ArrayList<Boolean> state = new ArrayList<Boolean>(saPair.state.size());
		for (int i = 0; i < saPair.state.size(); i++) {
			state.add(saPair.state.get(i) > 0);
		}
		return new SAPair<Boolean>(state, saPair.action).hashCode();
	}

	@Override
	public void update(int agentNum, double alpha, double delta) {
		Iterator<Map.Entry<Integer, Double>> it = e.get(agentNum).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Double> entry = it.next();
			Integer saCurrent = entry.getKey();
			Double eligibility = entry.getValue();

			table.put(saCurrent, (float) (table.get(saCurrent) + alpha * delta * eligibility));
		}
	}

	@Override
	public double get(SAPair<Double> saPair) {
		Integer processed = inputProcessing(saPair);
		if (table.containsKey(processed))
			return table.get(processed);

		table.put(processed, initialQ);
		if (Math.random() < 0.00001)
			System.out.println(table.size());
		return table.get(processed);
	}

	@Override
	public void accumulateEligibilities(int agentNum, SAPair<Double> saPair) {
		Integer processed = inputProcessing(saPair);

		if (!e.get(agentNum).containsKey(processed)) {
			e.get(agentNum).put(processed, 0.);
		}
		e.get(agentNum).put(processed, 1.);
	}

	@Override
	public void degradeEligibilities(int agentNum, double gamma, double lambda) {
		Iterator<Map.Entry<Integer, Double>> it = e.get(agentNum).entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, Double> entry = it.next();
			Integer saCurrent = entry.getKey();
			Double eligibility = entry.getValue();

			e.get(agentNum).put(saCurrent, eligibility * gamma * lambda);

			if (e.get(agentNum).get(saCurrent) < eligibilityMin)
				it.remove();
		}
	}

	@Override
	public void resetEligibilities(int agentNum) {
		e.set(agentNum, new HashMap<Integer, Double>());
	}

}
