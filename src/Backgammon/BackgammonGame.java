package Backgammon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import learner.Learner;
import learner.SarsaLambda;
import simulator.Game;

/**
 * Class to manage the playing of Backgammon games by the Learner module.
 * 
 * @author Alex Braithwaite
 *
 */
public class BackgammonGame implements Game {

	private Learner learner;
	private double averageAIvRandomWinRate = 50;
	private double whiteAIvAIWinRate = 50;
	private double whiteAIvRandomWinRate = 50;
	private double blackAIvRandomWinRate = 50;
	private boolean toDraw = false;
	private int trialNum = 0;
	private boolean drawing = false;
	private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	/**
	 * When toDraw is on, outputs from certain games and summary data will be
	 * sent to stdout.
	 * 
	 * @param toDraw
	 */
	public BackgammonGame(boolean toDraw) {
		this.toDraw = toDraw;
	}

	@Override
	public void setLeaner(Learner brain) {
		learner = brain;
	}

	@Override
	public void setupGame() {
	}

	@Override
	public void teardown() {
		// nothing to do here
	}

	@Override
	public double runTrial() {
		trialNum++;
		int matchLength = 1;

		// play AI vs AI
		boolean playWhite = true;
		boolean playBlack = true;
		boolean learn = true;
		boolean whiteWin = playMatch(matchLength, playWhite, playBlack, learn);
		whiteAIvAIWinRate += ((whiteWin ? 100 : 0) - whiteAIvAIWinRate) / 20;

		// play AI vs random (AI controls random player)
		playWhite = trialNum % 2 == 0;// new Random().nextBoolean();
		playBlack = !playWhite;
		learn = true;
		boolean AIWin = playMatch(matchLength, playWhite, playBlack, learn) == playWhite;
		averageAIvRandomWinRate += ((AIWin ? 100 : 0) - averageAIvRandomWinRate) / 20;
		whiteAIvRandomWinRate += (playWhite ? 1 : 0) * (((AIWin ? 100 : 0) - whiteAIvRandomWinRate) / 10);
		blackAIvRandomWinRate += (playWhite ? 0 : 1) * (((AIWin ? 100 : 0) - blackAIvRandomWinRate) / 10);

		// compute estimated rating
		double randomRating = 700;
		double a = randomRating * Math.sqrt(matchLength);
		double b = (2000 * Math.log((averageAIvRandomWinRate / 100) / (1 - averageAIvRandomWinRate / 100)));
		double c = (Math.log(2) + Math.log(5));
		double d = Math.sqrt(matchLength);
		double rank = (a + b / c) / d;

		// output basic information
		if (toDraw) {
			System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ");
			System.out.println("Match Group " + trialNum);
			System.out.println("Number of games");
			System.out.println("\tTotal        : " + trialNum * matchLength * 2);
			System.out.println("\tAI vs AI     : " + trialNum * matchLength);
			System.out.println("\tAI vs random : " + trialNum * matchLength);
			System.out.println("Win Rates");
			System.out.println("\tAI vs AI");
			System.out.println("\t\tWhite    : " + Math.round(whiteAIvAIWinRate) + "%");
			System.out.println("\tAI vs Random");
			System.out.println("\t\tAverage  : " + Math.round(averageAIvRandomWinRate) + "%");
			System.out.println("\t\tWhite AI : " + Math.round(whiteAIvRandomWinRate) + "%");
			System.out.println("\t\tBlack AI : " + Math.round(blackAIvRandomWinRate) + "%");

			System.out.println("Estimated Ranking");
			System.out.println("\t" + Math.round(rank));

			// System.out.println(learner.toString());
		}

		return averageAIvRandomWinRate;
	}

	/**
	 * Runs a series of games.
	 * 
	 * @param numGames
	 *            Number of games to play
	 * @param playWhite
	 *            True if AI is controlling white player
	 * @param playBlack
	 *            True if AI is controlling black player
	 * @param learn
	 *            True if AI is learning from this game
	 * @return True if white wins most games
	 */
	public boolean playMatch(int numGames, boolean playWhite, boolean playBlack, boolean learn) {
		int whiteWins = 0;
		for (int i = 0; i < numGames; i++) {
			if (runGame(playWhite, playBlack, learn))
				whiteWins++;
			drawing = false;
		}
		return (whiteWins > numGames / 2);
	}

	/**
	 * Runs a single game.
	 * 
	 * @param playWhite
	 *            True if AI is controlling white player
	 * @param playBlack
	 *            True if AI is controlling black player
	 * @param learn
	 *            True if AI is learning from this game
	 * @return True if white wins
	 */
	public boolean runGame(boolean playWhite, boolean playBlack, boolean learn) {

		if (learn)
			learner.reset();

		boolean AIvAI = true;
		try {
			if (drawing && br.ready()) {
				String line = br.readLine();
				drawing = true;
				if (line.equals("'"))
					AIvAI = false;
				while (br.ready())
					br.readLine();
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Board current = new Board();
		@SuppressWarnings("unused")
		Board prev = current;

		while (!current.gameOver()) {

			// print game
			if (drawing && (((playWhite == playBlack) && AIvAI) || ((playWhite != playBlack) && !AIvAI))) {
				System.out.println(current.toString());
				if (!AIvAI)
					System.out.println("(AI playing " + (playWhite ? "white (X)" : "black (O)") + ")");
				try {
					if (br.ready()) {
						drawing = false;
						while (br.ready())
							br.readLine();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			prev = current;

			// train learner
			// output 0 calculates chance of white winning
			// output 1 calculates chance of black winning
			if (learn) {
				learner.forceAction(current.getBoardState(), 0, 0, 0);
				learner.forceAction(current.getBoardState(), 0, 1, 1);
			}

			Set<Board> nextBoards = current.availableMoves();
			List<Board> nextBoardsList = new ArrayList<Board>(nextBoards);

			// selecting next board
			if (playWhite == current.whiteTurn() || playBlack == !current.whiteTurn()) {
				double max = -Double.MAX_VALUE;
				Board bestBoard = null;
				for (Board nextBoard : nextBoardsList) {
					List<Double> boardState = nextBoard.getBoardState();

					double winChance = ((SarsaLambda) learner).evaluateState(boardState).get(playWhite ? 0 : 1);

					if (winChance > max) {
						max = winChance;
						bestBoard = nextBoard;
					}
				}
				current = bestBoard;
			} else {
				current = nextBoardsList.get(new Random().nextInt(nextBoardsList.size()));
			}
		}

		if (learn) {
			learner.forceAction(current.getBoardState(), current.whiteWin() ? 1 : 0, 0, 0);
			learner.forceAction(current.getBoardState(), current.whiteWin() ? 0 : 1, 1, 1);
		}

		return current.whiteWin();
	}

}
