import java.util.*;

public class AIPlayerMinimax extends AIPlayer {

    public AIPlayerMinimax(Board board) {
        super(board);
    }

    @Override
    public int[] move() {
        int[] result = minimax(2, mySeed); // Depth set to 2
        return new int[]{result[1], result[2]};
    }

    private int[] minimax(int depth, Seed player) {
        List<int[]> nextMoves = generateMoves();

        int bestScore = (player == mySeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestRow = -1, bestCol = -1;

        if (nextMoves.isEmpty() || depth == 0) {
            bestScore = evaluate();
        } else {
            for (int[] move : nextMoves) {
                cells[move[0]][move[1]].content = player;
                int currentScore = minimax(depth - 1, (player == mySeed) ? oppSeed : mySeed)[0];
                if (player == mySeed) {
                    if (currentScore > bestScore) {
                        bestScore = currentScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                } else {
                    if (currentScore < bestScore) {
                        bestScore = currentScore;
                        bestRow = move[0];
                        bestCol = move[1];
                    }
                }
                cells[move[0]][move[1]].content = Seed.NO_SEED; // Undo move
            }
        }
        return new int[]{bestScore, bestRow, bestCol};
    }

    private List<int[]> generateMoves() {
        List<int[]> nextMoves = new ArrayList<>();
        if (hasWon(mySeed) || hasWon(oppSeed)) return nextMoves;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    nextMoves.add(new int[]{row, col});
                }
            }
        }
        return nextMoves;
    }

    private int evaluate() {
        int score = 0;
        score += evaluateLine(0, 0, 0, 1, 0, 2); // Rows
        score += evaluateLine(1, 0, 1, 1, 1, 2);
        score += evaluateLine(2, 0, 2, 1, 2, 2);
        score += evaluateLine(0, 0, 1, 0, 2, 0); // Columns
        score += evaluateLine(0, 1, 1, 1, 2, 1);
        score += evaluateLine(0, 2, 1, 2, 2, 2);
        score += evaluateLine(0, 0, 1, 1, 2, 2); // Diagonals
        score += evaluateLine(0, 2, 1, 1, 2, 0);
        return score;
    }

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

    private boolean hasWon(Seed seed) {
        int[][] winningPatterns = {
                {0b111000000, 0b000111000, 0b000000111}, // Rows
                {0b100100100, 0b010010010, 0b001001001}, // Columns
                {0b100010001, 0b001010100}              // Diagonals
        };
        int pattern = 0b000000000;
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                if (cells[row][col].content == seed) {
                    pattern |= (1 << (row * COLS + col));
                }
            }
        }
        for (int[] winningPattern : winningPatterns) {
            if ((pattern & winningPattern[0]) == winningPattern[0]) return true;
        }
        return false;
    }
}
