package learner;

import java.util.ArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;

import demolitionUtilities.Matrix;

/**
 * Neural network for use in the Q-function. Single hidden layer and single
 * output. Output is the weighted input to the final layer. Input becomes the
 * weighted input into the input layer (as opposed to the input layer
 * activations). Allows for multiple agents to use this by having unique
 * eligibilities per agent.
 * 
 * @author Alex Braithwaite
 *
 */
public class QNetwork {

	/**
	 * Number of layers in network. num_layers = k indicates a network with 1
	 * input layer, (k-2) hidden layers and 1 output layer. Input layer is layer
	 * 0, output layer is layer k.
	 */
	private int layers;

	/**
	 * Size of each layer, from input to output. sizes = [i,j,k] indicates a
	 * network with i inputs, j hidden neurons, and k outputs.
	 */
	private int[] sizes;

	/**
	 * Weight of each link. weights[l].get(j,k) is weight of link from kth
	 * neuron in (l-1)th layer to jth neuron in lth layer. Note j and k swapped
	 * to what seems intuitive.
	 */
	private ArrayList<Matrix> w;

	/**
	 * a Activation of each neuron. a[l].get(j,0) is activation of jth neuron in
	 * lth layer.
	 */
	private ArrayList<Matrix> a;

	/**
	 * Weighted input at each neuron. z[l].get(j,0) is weighted input at jth
	 * neuron in lth layer.
	 */
	private ArrayList<Matrix> z;

	/**
	 * Error in output of each neuron. delta.get(l).get(j) is error in jth
	 * neuron in layer l.
	 */
	private ArrayList<Matrix> delta;

	/**
	 * Rate of change of output with respect to weight of a link between
	 * neurons. nablaW[l].get(j,k) is rate of change of output to the output
	 * neuron with respect to weight of link from kth neuron in (l-1)th layer to
	 * jth neuron in lth layer.
	 */
	private ArrayList<Matrix> nablaW;

	/**
	 * Eligibility of a weight of a link between neurons.
	 * e.get(agentNum).get(l).get(j,k) is agentNum's eligibility of link from
	 * kth neuron in (l-1)th layer to jth neuron in lth layer.
	 */
	private ArrayList<ArrayList<Matrix>> e;

	public QNetwork(int hiddenNodes, int numInputs, int numAgents) {

		this.layers = 3;
		this.sizes = new int[] { numInputs, hiddenNodes, 1 };

		// initialise weights
		NormalDistribution nd = new NormalDistribution(0, 0.1);
		double[][][] weights = new double[3][][];
		weights[0] = new double[][] {};
		weights[1] = new double[hiddenNodes][numInputs];
		weights[2] = new double[1][hiddenNodes];
		for (int l = 0; l < weights.length; l++) {
			for (int j = 0; j < weights[l].length; j++) {
				for (int k = 0; k < weights[l][j].length; k++) {
					// initial weights
					weights[l][j][k] = nd.sample();
				}
			}
		}

		// create matrices
		a = new ArrayList<Matrix>(sizes.length);
		z = new ArrayList<Matrix>(sizes.length);
		delta = new ArrayList<Matrix>(sizes.length);
		for (int i = 0; i < layers; i++) {
			a.add(Matrix.makeZero(sizes[i], 1));
			z.add(Matrix.makeZero(sizes[i], 1));
			delta.add(Matrix.makeZero(sizes[i], 1));
		}

		w = new ArrayList<Matrix>(weights.length);
		nablaW = new ArrayList<Matrix>(weights.length);
		w.add(null);
		nablaW.add(null);
		for (int i = 1; i < weights.length; i++) {
			w.add(Matrix.rows(weights[i]));
			nablaW.add(Matrix.makeZero(sizes[i], sizes[i - 1]));
		}

		e = new ArrayList<ArrayList<Matrix>>(numAgents);
		e.add(null);
		for (int i = 0; i < numAgents; i++) {
			e.add(null);
			resetEligibilities(i);
		}
	}

	/**
	 * Set activations of input layer neurons.
	 * 
	 * @param inputActivations
	 */
	private void input(double[] inputActivations) {
		if (inputActivations.length != sizes[0]) {
			throw new IllegalArgumentException("Incorrect number input activations given (given "
					+ inputActivations.length + ", expected " + sizes[0] + ")");
		}
		a.set(0, Matrix.rows(inputActivations));
	}

	/**
	 * Calculate activations and weighted inputs for all neurons.
	 * 
	 */
	private void feedforward() {
		for (int l = 1; l < layers; l++) {
			z.set(l, w.get(l).multiplyRight(a.get(l - 1)));
			a.set(l, sigmoidMatrix(z.get(l)));
		}
	}

	/**
	 * Backpropagate to calculate nablaW for each weight. Note this rule assumes
	 * 1 output neuron and 3 total layers.
	 * 
	 * @param y
	 *            Desired output of network.
	 */
	private void backpropagate() {
		// error in each neuron
		delta.set(2, sigmoidPrimeMatrix(a.get(2)));
		delta.set(1, w.get(2).transpose().multiplyRight(delta.get(2)).multiplyElements(sigmoidPrimeMatrix(a.get(1))));

		// rate of change of output with respect to each edge
		nablaW.set(2, delta.get(2).multiplyRight(a.get(1).transpose()));
		nablaW.set(1, delta.get(1).multiplyRight(a.get(0).transpose()));
	}

	/**
	 * Compute 1 / (1 + e^(-z))
	 * 
	 * @param z
	 * @return
	 */
	private double sigmoid(double z) {
		return 1.0 / (1.0 + Math.exp(-z));
	}

	/**
	 * Compute differential of the sigmoid function, given sigmoid function
	 * already computed (ie a = sigmoid(z)).
	 * 
	 * @param a
	 *            sigmoid(z)
	 * @return a*(1 - a)
	 */
	private double sigmoidPrime(double a) {
		return a * (1 - a);
	}

	/**
	 * Apply sigmoid function to every element of a matrix.
	 * 
	 * @param zs
	 * @return
	 */
	private Matrix sigmoidMatrix(Matrix zs) {
		double[][] as = new double[zs.countRows()][zs.countColumns()];
		for (int i = 0; i < zs.countRows(); i++)
			for (int j = 0; j < zs.countColumns(); j++)
				as[i][j] = sigmoid(zs.get(i, j));
		return Matrix.rows(as);
	}

	/**
	 * Apply sigmoid prime function to every element of a matrix.
	 * 
	 * @param as
	 * @return
	 */
	private Matrix sigmoidPrimeMatrix(Matrix as) {
		double[][] as2 = new double[as.countRows()][as.countColumns()];
		for (int i = 0; i < as.countRows(); i++)
			for (int j = 0; j < as.countColumns(); j++)
				as2[i][j] = sigmoidPrime(as.get(i, j));
		return Matrix.rows(as2);
	}

	/**
	 * Convert raw state representation to how we want to store it.
	 * 
	 * @param saPair
	 * @return
	 */
	private double[] inputProcessing(SAPair<Double> saPair) {
		double[] inputs = new double[saPair.state.size() + 1];
		for (int i = 0; i < saPair.state.size(); i++) {
			inputs[i] = sigmoid(saPair.state.get(i));
		}
		inputs[saPair.state.size()] = 1;
		return inputs;
	}

	/**
	 * Perform the Sarsa(lambda) update equations as required.
	 * 
	 * @param agentNum
	 * @param alpha
	 * @param delta
	 */
	public void update(int agentNum, double alpha, double delta) {
		for (int l = 1; l < layers; l++) {
			// w[l] += alpha * delta * e[l]
			w.set(l, w.get(l).add(e.get(agentNum).get(l).multiply(alpha * delta)));
		}
	}

	/**
	 * Get the output for a given input state (this single network only provides
	 * output for one action)
	 * 
	 * @param saPair
	 * @return
	 */
	public double get(SAPair<Double> saPair) {
		this.input(inputProcessing(saPair));
		this.feedforward();
		return this.z.get(2).get(0, 0);
	}

	/**
	 * Accumulate eligibilities for a given state-action pair for a given agent.
	 * 
	 * @param agentNum
	 * @param saPair
	 */
	public void accumulateEligibilities(int agentNum, SAPair<Double> saPair) {
		// TODO add some form of cache for previously computed results
		// 'get' can then check cache (mapping input to a and z), and only do
		// expensive computation if not in cache
		// 'get' can add result to cache if it wasn't already there

		// feedforward then backpropagate
		this.get(saPair);
		this.backpropagate();

		for (int l = 1; l < layers; l++) {
			e.get(agentNum).set(l, e.get(agentNum).get(l).add(nablaW.get(l)));
		}
	}

	/**
	 * Degrade all eligibilities for a given agent.
	 * 
	 * @param agentNum
	 * @param gamma
	 * @param lambda
	 */
	public void degradeEligibilities(int agentNum, double gamma, double lambda) {
		for (int l = 1; l < layers; l++) {
			e.get(agentNum).set(l, e.get(agentNum).get(l).multiply(gamma * lambda));
		}
	}

	/**
	 * Reset all eligibilities for a given agent.
	 * 
	 * @param agentNum
	 */
	public void resetEligibilities(int agentNum) {
		e.set(agentNum, new ArrayList<Matrix>(layers));
		e.get(agentNum).add(null);
		for (int l = 1; l < layers; l++) {
			e.get(agentNum).add(Matrix.makeZero(sizes[l], sizes[l - 1]));
		}
	}

	@Override
	public String toString() {
		String s = "";
		s += "WEIGHTED INPUTS\n";
		s += a.get(0).transpose().toString() + "\n";
		s += z.get(1).transpose().toString() + "\n";
		s += z.get(2).transpose().toString() + "\n";
		s += "ACTIVATIONS\n";
		s += a.get(0).transpose().toString() + "\n";
		s += a.get(1).transpose().toString() + "\n";
		s += a.get(2).transpose().toString() + "\n";
		s += "DELTA\n";
		s += delta.get(0).transpose().toString() + "\n";
		s += delta.get(1).transpose().toString() + "\n";
		s += delta.get(2).transpose().toString() + "\n";
		s += "Weights\n -> from\n|\nV To\n";
		s += w.get(1).toString() + "\n";
		s += w.get(2).toString() + "\n";
		s += "nablaW\n";
		s += nablaW.get(1).toString() + "\n";
		s += nablaW.get(2).toString() + "\n";
		s += "e (agent 0)\n";
		s += e.get(0).get(1).toString() + "\n";
		s += e.get(0).get(2).toString() + "\n";
		s += "e (agent 1)\n";
		s += e.get(1).get(1).toString() + "\n";
		s += e.get(1).get(2).toString() + "\n";
		s += "\n";
		return s;
	}
}
