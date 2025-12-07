package domain.entities;
import domain.model.Position;
import domain.utils.Direction;

public class Player extends Entity {
    private int score = 0;
    private boolean canPlaceIce = true;
    //Lo usamos para cuando vaya a lanzar hielo
    private Direction direction =  Direction.DOWN;

    public Player(Position position) {
        super(position);
    }
    public Direction getDirection() {return this.direction;}
    public void setDirection(Direction direction) {this.direction = direction;}
    public int getScore() {return score;}
    public boolean canplaceIce() {return this.canPlaceIce;}
    public void addScore(int pts) {this.score += pts;}

    public void onHitByEnemy(Enemy e) {
        //Morir
    }

    public void toogleIce() {
        canPlaceIce = !canPlaceIce;
    }




}
