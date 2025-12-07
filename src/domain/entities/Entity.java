package domain.entities;
import domain.model.Position;

public abstract class Entity {
    protected Position position;
    protected Entity(Position position) {
        this.position = position;
    }
    public Position getPosition() {return this.position;}
    public int getRow() {return this.position.getRow();}
    public int getCol() {return this.position.getCol();}

    public void setPosition(Position position) { this.position = position;}
    public void setRow(int row) { this.position.setRow(row);}
    public void setCol(int col) { this.position.setCol(col);}

}
