// AIPlayer.java
public abstract class AIPlayer {
    protected int ROWS = Board.ROWS; // Number of rows
    protected int COLS = Board.COLS; // Number of columns

    protected Cell[][] cells;
    protected Seed mySeed;
    protected Seed oppSeed;

    public AIPlayer(Board board) {
        cells = board.cells;
    }

    public void setSeed(Seed seed) {
        this.mySeed = seed;
        this.oppSeed = (mySeed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
    }

    abstract int[] move();
}