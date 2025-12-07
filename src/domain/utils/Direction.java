package domain.utils;

public enum Direction {
    UP(-1,0),
    DOWN(1,0),
    RIGHT(0,1),
    LEFT(0,-1),
    NONE(0,0);

    private final int dRow;
    private final int dCol;

    Direction(int dRow, int dCol) {
        this.dRow = dRow;
        this.dCol = dCol;
    }

    public int getDRow() {return this.dRow;}
    public int getDCol() {return this.dCol;}

    // Girar 90° a la derecha
    public Direction turnRight() {
        return switch (this) {
            case UP -> RIGHT;
            case RIGHT -> DOWN;
            case DOWN -> LEFT;
            case LEFT -> UP;
            default -> NONE;
        };
    }

    //Dirección opuesta
    public Direction opposite() {
        return switch (this){
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            default -> NONE;
        };
    }
}
