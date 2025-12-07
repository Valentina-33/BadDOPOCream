package domain.model;

/**
 * Posición respecto a la matriz que modela el juego
 */
public class Position {
    private int row;
    private int col;

    public Position(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getRow() {return row;}
    public int getCol() {return col;}
    public void setRow(int row) {this.row = row;}
    public void setCol(int col) {this.col = col;}

    public Position translated(int row, int col) {
        return new Position(this.row+row, this.col+col);
    }

    @Override
    public String toString() {
        return "( "+row+", "+col+")";
    }
}
