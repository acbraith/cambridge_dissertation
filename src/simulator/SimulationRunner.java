package simulator;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import Backgammon.BackgammonGame;
import demolition.DemolitionGame;
import learner.SarsaLambda;
import learner.Settings;
import trader.TradingGame;

/**
 * Class to run a simulation in it's own thread
 * 
 * @author Alex Braithwaite
 * 
 */
public class SimulationRunner implements Runnable {

	private int numGames;
	private int totalDataPoints;
	private String gameType;
	public Settings settings;
	public List<Double> simResult;
	private PrintWriter logFile;

	/**
	 * Create a simulation Runner to store all the data and ready to run the
	 * required games.
	 * 
	 * @param numGames
	 * @param totalDataPoints
	 * @param settings
	 * @param gameType
	 * @param logFile
	 */
	public SimulationRunner(int numGames, int totalDataPoints, Settings settings, String gameType,
			PrintWriter logFile) {
		this.numGames = numGames;
		this.totalDataPoints = totalDataPoints;
		this.settings = settings;
		this.gameType = gameType;
		this.logFile = logFile;
	}

	/**
	 * Start a simulation with given settings for the SARSALearner.
	 * 
	 * @param numGames
	 *            Number of games to run.
	 * @param totalDataPoints
	 *            Number of times to sample moving average reward.
	 * @param s
	 *            Settings of SARSALeaner.
	 * @return Results of this simulation, as series of moving averages.
	 */
	@Override
	public void run() {

		simResult = new ArrayList<Double>();

		// create game to use
		// XXX draw or not
		boolean toDraw = true;
		boolean printInfo = toDraw;
		boolean printEndInfo = printInfo;

		Game game = null;
		if (gameType.equals("Demolition"))
			game = new DemolitionGame(toDraw);
		else if (gameType.equals("Backgammon"))
			game = new BackgammonGame(toDraw);
		else if (gameType.equals("Trading"))
			game = new TradingGame();

		game.setupGame();

		game.setLeaner(new SarsaLambda(settings));

		for (int trialNum = 1; trialNum <= numGames; trialNum++) {

			// run 1 game
			double trialReward = game.runTrial();

			if (printInfo) {
				System.out.println("Trial " + trialNum + ", Reward: " + trialReward);
				System.out
						.println(
								"Memory: "
										+ (int) ((Runtime.getRuntime().totalMemory()
												- Runtime.getRuntime().freeMemory()) / 1000000)
										+ " used / " + (int) (Runtime.getRuntime().totalMemory() / 1000000)
										+ " allocated / " + (int) (Runtime.getRuntime().maxMemory() / 1000000)
										+ " total");
			}

			// add to data if necessary
			if (trialNum % Math.ceil(numGames * (1. / totalDataPoints)) == 0) {
				simResult.add(trialReward);
			}

		}

		game.teardown();

		String toPrint = "";
		toPrint += "\n" + SimulationRunManager2.getDateTime() + ":\n\tFinished with settings:\n\t\t"
				+ settings.toString() + "\n\tFinal reward: " + simResult.get(simResult.size() - 1) + "\n\tRewards: "
				+ simResult.toString();
		if (printEndInfo) {
			System.out.println(toPrint);
		}
		logFile.println(toPrint);
		logFile.flush();
	}
}
