package simulator;

import java.io.FileNotFoundException;

import demolition.DemolitionGame;
import learner.Settings;

public class Main {

	/**
	 * Main entrance method. Currently not using command line arguments, source
	 * code edited to change simulations run.
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws FileNotFoundException {

		String gameType = "Demolition";
		// String gameType = "Backgammon";
		// String gameType = "Trading";

		int numAgents = 0, numInputs = 0, numOutputs = 0, memoryInterval = 0, memoryLength = 0;
		int hidden = 0;
		double alpha = 0, gamma = 0, lambda = 0, T = 0, epsilon = 0, alphaDecayRate = 0, TDecayRate = 0,
				epsilonDecayRate = 0;

		if (gameType.equals("Demolition")) {
			numAgents = DemolitionGame.numAgents;
			numInputs = DemolitionGame.sensorsTotal;
			numOutputs = 12;
			memoryInterval = 15;
			memoryLength = 0;

			hidden = 20;
			alpha =  0.2578875171467764;// +- 0.065843621
			gamma = 0.95255647698944;// +- 1.3421772799837584E-6
			lambda = 0.3238726864076799;// +- 1.3421772799837584E-6
			T =  0.037311385459533594;// +- 0.013168724
			epsilon = 0.008779149519890258;// +- 0.013168724
			alphaDecayRate = 0;
			TDecayRate = 0;
			epsilonDecayRate = 0;
		} else if (gameType.equals("Backgammon")) {
			numAgents = 2;
			numInputs = 198;
			numOutputs = 2;
			memoryInterval = 5;
			memoryLength = 0;

			hidden = 80;// +- 40
			alpha = 5.2;// +- 0.8
			gamma = 0.8;// +- 0.1
			lambda = 0.9;// +- 0.1
			T = 0;
			epsilon = 0;
			alphaDecayRate = 0;
			TDecayRate = 0;
			epsilonDecayRate = 0;
		} else if (gameType.equals("Trading")) {
			numAgents = 1;
			numInputs = 5;
			numOutputs = 2;
			memoryInterval = 1;
			memoryLength = 30;

			hidden = 10;
			alpha = 6.4;
			gamma = 0.24;
			lambda = 0.64;
			T = 0.1;
			epsilon = 0.08;
			alphaDecayRate = 0;
			TDecayRate = 0;
			epsilonDecayRate = 0;
		}

		double alphaDecay = 4.5E-6;
		double lambdaDecay = 4.5E-6;
		double TDecay = 4.1E-5;
		double epsilonDecay = 1.3E-5;
		Settings minSettings = new Settings(hidden, alpha, gamma, lambda, T, epsilon, 0, 0, 0,
				0, numAgents, numInputs, numOutputs, memoryInterval, memoryLength);
		Settings maxSettings = new Settings(hidden, alpha, gamma, lambda, T, epsilon, 0, 0, 0,
				0, numAgents, numInputs, numOutputs, memoryInterval, memoryLength);

		SimulationRunManager2 srm2 = new SimulationRunManager2();
		try {
			int divisor = 11;
			int simulationLength = 15;
			int repeats = 1;
			srm2.explore(minSettings, maxSettings, divisor, simulationLength, repeats, gameType);
			//srm2.optimise(minSettings, maxSettings, simulationLength, gameType);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
