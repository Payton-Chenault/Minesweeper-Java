package minesweeper.java;

import java.util.Random;

public class GameMan {
    public static final int MINE = -1;
    private final int size;
    private final int numMines;
    private final int[][] field;
    private final boolean[][] revealed;
    private final boolean[][] flagged;
    private int revealedCount;
    private int flagsPlaced;
    private boolean firstClick;

    public GameMan(App.Difficulty difficulty) {
        this.size = difficulty.size;
        this.numMines = difficulty.mines;
        this.field = new int[this.size][this.size];
        this.revealed = new boolean[this.size][this.size];
        this.flagged = new boolean[this.size][this.size];
        this.firstClick = true;
    }

    public int getSize() { return this.size; }
    public boolean isMine(int x, int y) { return this.field[x][y] == MINE; }
    public int getCell(int x, int y) { return this.field[x][y]; }
    public int getFlagsPlaced() { return this.flagsPlaced; }
    public boolean isRevealed(int x, int y) {return this.revealed[x][y]; }
    public boolean isFlagged(int x, int y) {return this.flagged[x][y]; }
    public boolean[][] getRevealed() { return this.revealed; }
    public boolean[][] getFlagged() { return this.flagged; }

    public void toggleFlag(int x, int y) {
        if(revealed[x][y]) return;

        flagged[x][y] = !flagged[x][y];
        if(flagged[x][y]) {
            this.flagsPlaced++;
        } else {
            this.flagsPlaced--;
        }
    }

    public boolean reveal(int x, int y) {
        if(this.flagged[x][y] || this.revealed[x][y]) return false;

        if(this.firstClick) {
            generateField(x, y);
            this.firstClick = false;
        }

        this.revealed[x][y] = true;
        this.revealedCount++;
        return true;
    }

    public boolean hasWon() {
        return this.revealedCount == (size * size - numMines);
    }

    private void generateField(int sx, int sy) {
        Random rand = new Random();
        int placed = 0;

        while (placed < this.numMines) {
            int x = rand.nextInt(this.size);
            int y = rand.nextInt(this.size);

            if (field[x][y] == MINE) continue;
            if (Math.abs(x - sx) <= 1 && Math.abs(y - sy) <= 1) continue;

            this.field[x][y] = MINE;
            placed++;

            for(int dx = -1; dx <=1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    int nx = x + dx, ny = y + dy;
                    if(nx >= 0 && ny >= 0 && nx < this.size && ny < this.size && this.field[nx][ny] != MINE) {
                        field[nx][ny]++;
                    }
                }
            }
        }
    }
}
