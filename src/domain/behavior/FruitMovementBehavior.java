package domain.behavior;

import domain.entities.MovingFruit;
import domain.game.Level;

public interface FruitMovementBehavior {

    /**
     * Define cómo se mueve una fruta móvil en cada tick del juego.
     */
    void move(Level level, MovingFruit fruit);
}
