package tests;

import java.util.Arrays;

import learner.SarsaLambda;
import learner.Settings;

/**
 * Testing class for the Learner.
 * @author Alex Braithwaite
 *
 */
public class NetworkTester {

	public static void main(String[] args) {
		int numAgents = 1;
		int numInputs = 2;
		int numOutputs = 2;
		int memoryInterval = 1;
		int memoryLength = 0;

		int hidden = 3;
		double alpha = 0.01;
		double gamma = 0;
		double lambda = 0;
		double T = 0.01;
		double epsilon = 0.01;
		Settings s = new Settings(hidden, alpha, gamma, lambda, T, epsilon, 0., 0., 0., 0., numAgents, numInputs,
				numOutputs, memoryInterval, memoryLength);
		SarsaLambda sarsa = new SarsaLambda(s);
		double reward = 0;
		double MAReward = 0;
		int step = 0;
		while (true) {
			step++;
			double i1 = (Math.random() < 0.5) ? 10 : -10;
			double i2 = (Math.random() < 0.5) ? 10 : -10;
			int desired = (i1 == 10) ^ (i2 == 10) ? 1 : 0;
			Double[] inputs = new Double[] { i1, i2 };
			int output = sarsa.getAction(Arrays.asList(inputs), reward);

			reward = 0;
			if (output == desired) {
				reward = 1;
			}
			// update moving average reward
			double period = 100;
			MAReward += (reward - MAReward) / period;

			if (step > 0 && step % 100000 == 0) {
				System.out.println(step + ": " + Math.round(MAReward * 100) + "% correct");
				if (MAReward > 0.95) {
					System.out.println("Function Learnt");
					break;
				}
			}
		}

	}
}
