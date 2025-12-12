package domain.entities;

import domain.behavior.MovementBehavior;
import domain.model.Position;
import domain.utils.Direction;
import java.awt.Graphics2D;

/**
 * Los enemigos son tipo de entidades que tienen un comportamiento particular, hacen perder a los helados.
 */
public class Enemy extends Entity {
    protected Direction direction;
    protected MovementBehavior movementBehavior;

    public Enemy(Position position, Direction initialDirection, MovementBehavior movementBehavior) {
        super(position);
        this.direction = initialDirection;
        this.movementBehavior = movementBehavior;
    }

    public void render(Graphics2D g, int tileSize) {
        if (animatedSprite != null) {
            int x = position.getCol() * tileSize;
            int y = position.getRow() * tileSize;
            animatedSprite.draw(g, x, y, tileSize, tileSize, direction);
        }
    }
    public Direction getDirection() { return this.direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public MovementBehavior getMovementBehavior() { return this.movementBehavior; }

}