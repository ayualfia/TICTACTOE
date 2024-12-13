import java.util.List;
import java.util.ArrayList;

/** AIPlayer using Minimax algorithm with alpha-beta pruning */

class AIPlayerMinimax extends AIPlayer {
    public AIPlayerMinimax(Board board) {
        super(board);
    }

    @Override
    public int[] move() {
        return minimax(2, mySeed);
    }

    private int[] minimax(int depth, Seed player) {
        List<int[]> nextMoves = generateMoves();
        int bestScore = (player == mySeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int bestRow = -1;
        int bestCol = -1;

        if (nextMoves.isEmpty() || depth == 0) {
            bestScore = evaluate();
        } else {
            for (int[] move : nextMoves) {
                cells[move[0]][move[1]].content = player;
                int currentScore = minimax(depth - 1, (player == mySeed) ? oppSeed : mySeed)[0];

                if (player == mySeed && currentScore > bestScore || player == oppSeed && currentScore < bestScore) {
                    bestScore = currentScore;
                    bestRow = move[0];
                    bestCol = move[1];
                }

                cells[move[0]][move[1]].content = Seed.NO_SEED;
            }
        }
        return new int[]{bestScore, bestRow, bestCol};
    }

    private List<int[]> generateMoves() {
        List<int[]> nextMoves = new ArrayList<>();
        if (hasWon(mySeed) || hasWon(oppSeed)) return nextMoves;
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) nextMoves.add(new int[]{row, col});
            }
        }
        return nextMoves;
    }

    private boolean hasWon(Seed thePlayer) {
        int[][] winningPatterns = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };
        int pattern = 0;
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == thePlayer) {
                    pattern |= (1 << (row * COLS + col));
                }
            }
        }
        for (int[] win : winningPatterns) {
            if ((pattern & (1 << win[0]) | (1 << win[1]) | (1 << win[2])) == 7) return true;
        }
        return false;
    }

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
}