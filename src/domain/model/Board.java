package domain.model;

public class Board {

    private final int rows;
    private final int cols;
    private final CellType[][] cells;

    public Board(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new CellType[rows][cols];

        initEmptyWithBorders();
    }

    /**
     *  Inicializa vacío con un borde de WALL alrededor
     *  */
    private void initEmptyWithBorders() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (r == 0 || c == 0 || r == rows - 1 || c == cols - 1) {
                    cells[r][c] = CellType.METALLIC_WALL;
                } else {
                    cells[r][c] = CellType.EMPTY;
                }
            }
        }
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public boolean isInside(Position p) {
        return p.getRow() >= 0 && p.getRow() < rows && p.getCol() >= 0 && p.getCol() < cols;
    }

    public CellType getCellType(Position p) {
        return cells[p.getRow()][p.getCol()];
    }

    public void setCellType(Position p, CellType type) {
        cells[p.getRow()][p.getCol()] = type;
    }

    public boolean isWalkable(Position p) {
        if (!isInside(p)) return false;
        return getCellType(p).isTraversable();
    }
}
