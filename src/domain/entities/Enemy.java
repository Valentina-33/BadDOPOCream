package domain.entities;

import domain.behavior.MovementBehavior;
import domain.model.Position;
import domain.utils.Direction;

import java.awt.Graphics2D;

public class Enemy extends Entity {
    protected Direction direction;
    protected MovementBehavior movementBehavior;

    public Enemy(Position position, Direction initialDirection, MovementBehavior movementBehavior) {
        super(position);
        this.direction = initialDirection;
        this.movementBehavior = movementBehavior;
    }

    // El enemigo se dibuja a sí mismo usando el sprite que le haya dado la Fábrica.
    // Esto simplifica el LevelRenderer (para no tener un switch gigante por cada enemigo).
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