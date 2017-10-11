package Backgammon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Class implementing a game of Backgammon. Constructor gives a default board,
 * availableMoves() returns all the possible game states after 1 ply.
 * 
 * @author Alex Braithwaite
 *
 */
public class Board {

	/**
	 * Representation of the number of pieces on each point. Points are indexed
	 * in white's direction of movement, with point(0) being the bottom left,
	 * point(23) the top right.
	 */
	private List<Integer> pointsWhite;
	private List<Integer> pointsBlack;

	/**
	 * Number of pieces on the bar.
	 */
	private int barWhite;
	private int barBlack;

	/**
	 * Number of pieces borne off by each player.
	 */
	private int borneOffWhite;
	private int borneOffBlack;

	/**
	 * True if white's turn, false if black's.
	 */
	private boolean whiteTurn;

	private int dice1;
	private int dice2;

	/**
	 * Roll a dice.
	 * 
	 * @return Integer from 1-6
	 */
	private void rollDice() {
		Random r = new Random();
		dice1 = r.nextInt(6) + 1;
		dice2 = r.nextInt(6) + 1;
	}

	/**
	 * Initialise board with standard starting positions.
	 */
	public Board() {
		this.pointsWhite = Arrays.asList(2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 0, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, 0);
		this.pointsBlack = Arrays.asList(0, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2);
		this.barWhite = 0;
		this.barBlack = 0;
		this.borneOffWhite = 0;
		this.borneOffBlack = 0;

		if (Math.random() < 0.5)
			this.whiteTurn = true;
		else
			this.whiteTurn = false;

		rollDice();
	}

	/**
	 * Copy constructor
	 */
	public Board(Board toCopy) {
		this.pointsWhite = new ArrayList<Integer>(24);
		this.pointsBlack = new ArrayList<Integer>(24);
		for (Integer i : toCopy.pointsWhite) {
			this.pointsWhite.add(i);
		}
		for (Integer i : toCopy.pointsBlack) {
			this.pointsBlack.add(i);
		}
		this.barWhite = toCopy.barWhite;
		this.barBlack = toCopy.barBlack;
		this.borneOffWhite = toCopy.borneOffWhite;
		this.borneOffBlack = toCopy.borneOffBlack;
		this.whiteTurn = toCopy.whiteTurn;
		this.dice1 = toCopy.dice1;
		this.dice2 = toCopy.dice2;
	}

	/**
	 * 
	 * @return True if white's turn, false if black's.
	 */
	public boolean whiteTurn() {
		return whiteTurn;
	}

	/**
	 * Test for end game.
	 * 
	 * @return True if game over.
	 */
	public boolean gameOver() {
		if (borneOffWhite == 15 || borneOffBlack == 15)
			return true;
		return false;
	}

	/**
	 * Test for white win.
	 * 
	 * @return True if white wins.
	 */
	public boolean whiteWin() {
		if (borneOffWhite == 15)
			return true;
		return false;
	}

	/**
	 * Test for black win.
	 * 
	 * @return True if black wins.
	 */
	public boolean blackWin() {
		if (borneOffBlack == 15)
			return true;
		return false;
	}

	public void swapTurn() {
		whiteTurn = !whiteTurn;
	}

	/**
	 * Get a list of boards representing available next moves.
	 * 
	 * @return List of Boards available to move to next.
	 */
	public Set<Board> availableMoves() {
		rollDice();

		boolean amWhite = whiteTurn;

		Set<Board> bigRoll = new HashSet<Board>();
		Set<Board> smallRoll = new HashSet<Board>();
		Set<Board> bothRoll = new HashSet<Board>();
		Set<Board> doubleMove3 = new HashSet<Board>();
		Set<Board> doubleMove4 = new HashSet<Board>();
		Set<Board> result = new HashSet<Board>();

		Board firstMoveBoard;
		Board secondMoveBoard;

		int bigDice = Math.max(dice1, dice2);
		int smallDice = Math.min(dice1, dice2);
		boolean doubles = bigDice == smallDice;

		int barPlayer = amWhite ? barWhite : barBlack;
		int playerBarPosition = amWhite ? -1 : 24;

		if (doubles) {
			int movesLeft = 4;
			int movesTaken = 0;
			Board currentBoard = this;
			barPlayer = amWhite ? currentBoard.barWhite : currentBoard.barBlack;

			// moving off bar
			while (movesLeft > 0 && currentBoard != null && barPlayer > 0) {
				currentBoard = currentBoard.move(amWhite, playerBarPosition, bigDice);
				if (currentBoard != null) {
					switch (movesTaken) {
					case 0:
						bigRoll.add(currentBoard);
						break;
					case 1:
						bothRoll.add(currentBoard);
						break;
					case 2:
						doubleMove3.add(currentBoard);
						break;
					default:
						doubleMove4.add(currentBoard);
						break;
					}
					barPlayer = amWhite ? currentBoard.barWhite : currentBoard.barBlack;
					movesLeft--;
					movesTaken++;
				}
			}

			// normal moves
			while (movesLeft > 0 && currentBoard != null && barPlayer == 0) {
				Set<Board> currentBoards;
				switch (movesTaken) {
				case 0:
					currentBoards = new HashSet<Board>();
					currentBoards.add(currentBoard);
					break;
				case 1:
					currentBoards = bigRoll;
					break;
				case 2:
					currentBoards = bothRoll;
					break;
				default:
					currentBoards = doubleMove3;
					break;
				}
				
				for (Board b : currentBoards) {
					for (int point = 0; point < 24; point++) {
						Board afterMove = b.move(amWhite, point, bigDice);
						if (afterMove != null) {
							switch (movesTaken) {
							case 0:
								bigRoll.add(afterMove);
								break;
							case 1:
								bothRoll.add(afterMove);
								break;
							case 2:
								doubleMove3.add(afterMove);
								break;
							default:
								doubleMove4.add(afterMove);
								break;
							}
						}
					}
				}
				movesLeft--;
				movesTaken++;
			}
		} else {
			// moving pieces off bar
			if (barPlayer > 0) {
				firstMoveBoard = move(amWhite, playerBarPosition, bigDice);
				if (firstMoveBoard != null) {
					bigRoll.add(firstMoveBoard);
					if (barPlayer > 1) {
						// move 2 off bar
						secondMoveBoard = firstMoveBoard.move(amWhite, playerBarPosition, smallDice);
						if (secondMoveBoard != null)
							bothRoll.add(secondMoveBoard);
					} else {
						// move 1 off bar then use other dice
						for (int point = 0; point < 24; point++) {
							secondMoveBoard = firstMoveBoard.move(amWhite, point, smallDice);
							if (secondMoveBoard != null)
								bothRoll.add(secondMoveBoard);
						}
					}
				}

				firstMoveBoard = move(amWhite, playerBarPosition, smallDice);
				if (firstMoveBoard != null) {
					smallRoll.add(firstMoveBoard);
					if (barPlayer > 1) {
						// move 2 off bar
						secondMoveBoard = firstMoveBoard.move(amWhite, playerBarPosition, bigDice);
						if (secondMoveBoard != null)
							bothRoll.add(secondMoveBoard);
					} else {
						// move 1 off bar then use other dice
						for (int point = 0; point < 24; point++) {
							secondMoveBoard = firstMoveBoard.move(amWhite, point, bigDice);
							if (secondMoveBoard != null)
								bothRoll.add(secondMoveBoard);
						}
					}
				}

			} else {
				// none on bar, move both with dice

				for (int point = 0; point < 24; point++) {
					firstMoveBoard = move(amWhite, point, bigDice);
					if (firstMoveBoard != null) {
						bigRoll.add(firstMoveBoard);
						for (int point2 = 0; point2 < 24; point2++) {
							secondMoveBoard = firstMoveBoard.move(amWhite, point2, smallDice);
							if (secondMoveBoard != null)
								bothRoll.add(secondMoveBoard);
						}
					}
				}

				for (int point = 0; point < 24; point++) {
					firstMoveBoard = move(amWhite, point, smallDice);
					if (firstMoveBoard != null) {
						smallRoll.add(firstMoveBoard);
						for (int point2 = 0; point2 < 24; point2++) {
							secondMoveBoard = firstMoveBoard.move(amWhite, point2, bigDice);
							if (secondMoveBoard != null)
								bothRoll.add(secondMoveBoard);
						}
					}
				}
			}
		}

		// return the most preferred, non-empty list of possible next moves
		if (smallRoll.size() > 0)
			result = smallRoll;

		if (bigRoll.size() > 0)
			result = bigRoll;

		if (bothRoll.size() > 0)
			result = bothRoll;

		if (doubleMove3.size() > 0)
			result = doubleMove3;

		if (doubleMove4.size() > 0)
			result = doubleMove4;

		// no available moves, return this board with turn swapped
		if (result.size() == 0)
			result.add(new Board(this));

		// swap turn
		for (Board b : result)
			b.swapTurn();

		return result;
	}

	/**
	 * Return true if the given player can bear pieces off.
	 * 
	 * @param white
	 *            True if white to move, false if black.
	 * @return True if all of their pieces are in their home board.
	 */
	private boolean canBearOff(boolean white) {
		if (white) {
			if (barWhite > 0)
				return false;
			for (int i = 17; i > -1; i--)
				if (pointsWhite.get(i) > 0)
					return false;
		} else {
			if (barBlack > 0)
				return false;
			for (int i = 6; i < 24; i++)
				if (pointsBlack.get(i) > 0)
					return false;
		}
		return true;
	}

	/**
	 * Try to move a piece.
	 * 
	 * @param white
	 *            True if white to move, false if black.
	 * @param point
	 *            Point to move piece from. If moving from bar, point = -1 for
	 *            white, 24 for black.
	 * @param roll
	 *            Distance to try and move piece.
	 * @return new Board representing changed game state after move. null if
	 *         this move is illegal.
	 */
	private Board move(boolean white, int point, int roll) {
		Board newBoard = new Board(this);

		int minPoint = white ? -1 : 0;
		int maxPoint = white ? 23 : 24;
		int minDestination = white ? 0 : -1;
		int maxDestination = white ? 24 : 23;
		int barPoint = white ? -1 : 24;
		int bearOffPoint = white ? 24 : -1;
		int barPlayer = white ? barWhite : barBlack;
		List<Integer> pointsPlayer = white ? pointsWhite : pointsBlack;
		List<Integer> pointsOpponent = white ? pointsBlack : pointsWhite;

		int destination = white ? point + roll : point - roll;

		// invalid moves
		if ((point < minPoint) || (point > maxPoint) || (point == barPoint && barPlayer == 0)
				|| (point != barPoint && pointsPlayer.get(point) == 0) || (destination > maxDestination)
				|| (destination < minDestination)
				|| (destination != bearOffPoint && pointsOpponent.get(destination) > 1)
				|| (destination == bearOffPoint && !canBearOff(white)))
			return null;

		if (point == barPoint) {
			// move from bar
			if (pointsOpponent.get(destination) == 0) {
				if (white) {
					newBoard.barWhite--;
					newBoard.pointsWhite.set(destination, pointsWhite.get(destination) + 1);
				} else {
					newBoard.barBlack--;
					newBoard.pointsBlack.set(destination, pointsBlack.get(destination) + 1);
				}
			} else {
				if (white) {
					newBoard.barWhite--;
					newBoard.pointsWhite.set(destination, pointsWhite.get(destination) + 1);
					newBoard.pointsBlack.set(destination, pointsBlack.get(destination) - 1);
					newBoard.barBlack++;
				} else {
					newBoard.barBlack--;
					newBoard.pointsBlack.set(destination, pointsBlack.get(destination) + 1);
					newBoard.pointsWhite.set(destination, pointsWhite.get(destination) - 1);
					newBoard.barWhite++;
				}
			}
		} else if (destination == bearOffPoint) {
			// bear off
			if (white) {
				newBoard.pointsWhite.set(point, pointsWhite.get(point) - 1);
				newBoard.borneOffWhite++;
			} else {
				newBoard.pointsBlack.set(point, pointsBlack.get(point) - 1);
				newBoard.borneOffBlack++;
			}
		} else if (pointsOpponent.get(destination) == 0) {
			// no opponent on where we move to
			if (white) {
				newBoard.pointsWhite.set(point, pointsWhite.get(point) - 1);
				newBoard.pointsWhite.set(destination, pointsWhite.get(destination) + 1);
			} else {
				newBoard.pointsBlack.set(point, pointsBlack.get(point) - 1);
				newBoard.pointsBlack.set(destination, pointsBlack.get(destination) + 1);
			}
		} else {
			// opponent on where we move to
			if (white) {
				newBoard.pointsWhite.set(point, pointsWhite.get(point) - 1);
				newBoard.pointsWhite.set(destination, pointsWhite.get(destination) + 1);
				newBoard.pointsBlack.set(destination, pointsBlack.get(destination) - 1);
				newBoard.barBlack++;
			} else {
				newBoard.pointsBlack.set(point, pointsBlack.get(point) - 1);
				newBoard.pointsBlack.set(destination, pointsBlack.get(destination) + 1);
				newBoard.pointsWhite.set(destination, pointsWhite.get(destination) - 1);
				newBoard.barWhite++;
			}
		}

		return newBoard;
	}

	/**
	 * Get the current game state in a form usable by a learning NN.
	 * 
	 * @return
	 */
	public List<Double> getBoardState() {
		List<Double> state = new ArrayList<Double>(198);

		for (int point = 0; point < 24; point++) {
			// add details for each point
			for (int i = 0; i < 3; i++) {
				if (pointsWhite.get(point) > i)
					state.add(10.);
				else
					state.add(-10.);
				if (pointsBlack.get(point) > i)
					state.add(10.);
				else
					state.add(-10.);
			}
			if (pointsWhite.get(point) > 3)
				state.add((20 * (pointsWhite.get(point) - 3.) / 2) - 10);
			else
				state.add(-10.);
			if (pointsBlack.get(point) > 3)
				state.add((20 * (pointsBlack.get(point) - 3.) / 2) - 10);
			else
				state.add(10.);
		}
		// pieces on the bar
		state.add((20 * barWhite / 2.) - 10);
		state.add((20 * barBlack / 2.) - 10);
		state.add((20 * borneOffWhite / 15.) - 10);
		state.add((20 * borneOffBlack / 15.) - 10);
		state.add(whiteTurn ? 10. : -10.);
		state.add(whiteTurn ? -10. : 10.);

		return state;
	}

	/**
	 * Return an ASCII representation of the current board.
	 */
	public String toString() {
		String board = "";
		board += "Roll dice: " + dice1 + " + " + dice2 + "\n";
		board += "============================================\n";
		board += "| 13 14 15 16 17 18 |  | 19 20 21 22 23 24 |\n";
		// draw top half of board
		for (int i = 0; i < 7; i++) {
			board += "| ";
			for (int point = 12; point < 24; point++) {
				if (pointsWhite.get(point) > 7 + i)
					board += "XX ";
				else if (pointsWhite.get(point) > i)
					board += " X ";
				else if (pointsBlack.get(point) > 7 + i)
					board += "OO ";
				else if (pointsBlack.get(point) > i)
					board += " O ";
				else
					board += "   ";
				if (point == 17) {
					board += "|";
					if (barBlack > 7 + i)
						board += "OO";
					else if (barBlack > i)
						board += " O";
					else
						board += "  ";
					board += "| ";
				}
			}
			board += "| ";
			if (borneOffWhite > 7 + i)
				board += "XX";
			else if (borneOffWhite > i)
				board += " X";
			board += "\n";
		}
		board += "|                   |  |                   |\n";

		// draw bottom half of board
		for (int i = 6; i > -1; i--) {
			board += "| ";
			for (int point = 11; point > -1; point--) {
				if (pointsWhite.get(point) > 7 + i)
					board += "XX ";
				else if (pointsWhite.get(point) > i)
					board += " X ";
				else if (pointsBlack.get(point) > 7 + i)
					board += "OO ";
				else if (pointsBlack.get(point) > i)
					board += " O ";
				else
					board += "   ";
				if (point == 6) {
					board += "|";
					if (barWhite > 7 + i)
						board += "XX";
					else if (barWhite > i)
						board += " X";
					else
						board += "  ";
					board += "| ";
				}
			}
			board += "| ";
			if (borneOffBlack > 7 + i)
				board += "OO";
			else if (borneOffBlack > i)
				board += " O";
			board += "\n";
		}
		board += "| 12 11 10  9  8  7 |  |  6  5  4  3  2  1 |\n";
		board += "============================================\n";
		board += whiteTurn ? "WHITE (X) to move next\n" : "BLACK (O) to move next\n";
		return board;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + barBlack;
		result = prime * result + barWhite;
		result = prime * result + borneOffBlack;
		result = prime * result + borneOffWhite;
		result = prime * result + dice1;
		result = prime * result + dice2;
		result = prime * result + ((pointsBlack == null) ? 0 : pointsBlack.hashCode());
		result = prime * result + ((pointsWhite == null) ? 0 : pointsWhite.hashCode());
		result = prime * result + (whiteTurn ? 1231 : 1237);
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
		Board other = (Board) obj;
		if (barBlack != other.barBlack)
			return false;
		if (barWhite != other.barWhite)
			return false;
		if (borneOffBlack != other.borneOffBlack)
			return false;
		if (borneOffWhite != other.borneOffWhite)
			return false;
		if (dice1 != other.dice1)
			return false;
		if (dice2 != other.dice2)
			return false;
		if (pointsBlack == null) {
			if (other.pointsBlack != null)
				return false;
		} else if (!pointsBlack.equals(other.pointsBlack))
			return false;
		if (pointsWhite == null) {
			if (other.pointsWhite != null)
				return false;
		} else if (!pointsWhite.equals(other.pointsWhite))
			return false;
		if (whiteTurn != other.whiteTurn)
			return false;
		return true;
	}

}
