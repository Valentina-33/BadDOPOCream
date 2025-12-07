package domain.entities;

import domain.behavior.FruitMovementBehavior;
import domain.game.Level;
import domain.model.Position;

public abstract class MovingFruit extends Fruit {

    protected FruitMovementBehavior movementBehavior;

    public MovingFruit(Position position, int points, Sprite sprite,  FruitMovementBehavior movementBehavior) {
        super(position, points, sprite);
        this.movementBehavior = movementBehavior;
    }

    /**
     * Las frutas móviles actualizan su estado solo si:
     *  - no están congeladas
     *  - no están recogidas
     */
    @Override
    public void update(Level level) {
        if (!isCollected() && !isFrozen() && movementBehavior != null) {
            movementBehavior.move(level, this);
        }
    }

    public void setMovementBehavior(FruitMovementBehavior behavior) {
        this.movementBehavior = behavior;
    }

    public FruitMovementBehavior getMovementBehavior() {
        return movementBehavior;
    }
}
