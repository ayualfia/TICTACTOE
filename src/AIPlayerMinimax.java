import java.util.List;
import java.util.ArrayList;

/** AIPlayer using Minimax algorithm with alpha-beta pruning */

public class AIPlayerMinimax extends AIPlayer {

    /** Constructor with the given game board */
    public AIPlayerMinimax(Board board) {
        super(board);
    }

    /** Get next best move for computer. Return int[2] of {row, col} */
    @Override
    int[] move() {
        int[] result = minimax(2, mySeed, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return new int[] {result[1], result[2]}; // row, col
    }

    /** Minimax (recursive) at level of depth for maximizing or minimizing player
     with alpha-beta cut-off. Return int[3] of {score, row, col} */
    private int[] minimax(int depth, Seed player, int alpha, int beta) {
        List<int[]> nextMoves = generateMoves();

        int score;
        int bestRow = -1;
        int bestCol = -1;

        if (nextMoves.isEmpty() || depth == 0) {
            score = evaluate();
            return new int[] {score, bestRow, bestCol};
        }

        for (int[] move : nextMoves) {
            cells[move[0]][move[1]].content = player;
            if (player == mySeed) { // maximizing player
                score = minimax(depth - 1, oppSeed, alpha, beta)[0];
                if (score > alpha) {
                    alpha = score;
                    bestRow = move[0];
                    bestCol = move[1];
                }
            } else { // minimizing player
                score = minimax(depth - 1, mySeed, alpha, beta)[0];
                if (score < beta) {
                    beta = score;
                    bestRow = move[0];
                    bestCol = move[1];
                }
            }
            cells[move[0]][move[1]].content = Seed.NO_SEED; // undo move
            if (alpha >= beta) break; // alpha-beta pruning
        }

        return new int[] {(player == mySeed) ? alpha : beta, bestRow, bestCol};
    }

    /** Find all valid next moves. */
    private List<int[]> generateMoves() {
        List<int[]> nextMoves = new ArrayList<>();
        if (hasWon(mySeed) || hasWon(oppSeed)) return nextMoves;

        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    nextMoves.add(new int[] {row, col});
                }
            }
        }
        return nextMoves;
    }

    /** Evaluate the board */
    private int evaluate() {
        int score = 0;
        score += evaluateLine(0, 0, 0, 1, 0, 2);
        score += evaluateLine(1, 0, 1, 1, 1, 2);
        score += evaluateLine(2, 0, 2, 1, 2, 2);
        score += evaluateLine(0, 0, 1, 0, 2, 0);
        score += evaluateLine(0, 1, 1, 1, 2, 1);
        score += evaluateLine(0, 2, 1, 2, 2, 2);
        score += evaluateLine(0, 0, 1, 1, 2, 2);
        score += evaluateLine(0, 2, 1, 1, 2, 0);
        return score;
    }

    /** Evaluate a specific line */
    private int evaluateLine(int row1, int col1, int row2, int col2, int row3, int col3) {
        int score = 0;
        if (cells[row1][col1].content == mySeed) score = 1;
        else if (cells[row1][col1].content == oppSeed) score = -1;

        if (cells[row2][col2].content == mySeed) {
            if (score == 1) score = 10;
            else if (score == -1) return 0;
            else score = 1;
        } else if (cells[row2][col2].content == oppSeed) {
            if (score == -1) score = -10;
            else if (score == 1) return 0;
            else score = -1;
        }

        if (cells[row3][col3].content == mySeed) {
            if (score > 0) score *= 10;
            else if (score < 0) return 0;
            else score = 1;
        } else if (cells[row3][col3].content == oppSeed) {
            if (score < 0) score *= 10;
            else if (score > 0) return 0;
            else score = -1;
        }
        return score;
    }

    private boolean hasWon(Seed player) {
        int pattern = 0b000000000;
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == player) {
                    pattern |= (1 << (row * COLS + col));
                }
            }
        }

        for (int winningPattern : winningPatterns) {
            if ((pattern & winningPattern) == winningPattern) return true;
        }
        return false;
    }

    private final int[] winningPatterns = {
            0b111000000, 0b000111000, 0b000000111,
            0b100100100, 0b010010010, 0b001001001,
            0b100010001, 0b001010100
    };
}
