package domain.entities;

import domain.behavior.FruitMovementBehavior;
import domain.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MovingFruitTest {

    @Test
    public void updateLlamaMoveSiNoEstaRecogidaNiCongelada() {
        final boolean[] moved = {false};

        FruitMovementBehavior behavior = (level, fruit) -> moved[0] = true;

        MovingFruit fruit = new MovingFruit(new Position(1, 1), 10, null, behavior) {
            @Override
            public int getPoints() {
                return 10;
            }
        };

        fruit.update(null);

        assertTrue(moved[0]);
    }

    @Test
    public void updateNoLlamaMoveSiEstaRecogida() {
        final boolean[] moved = {false};

        FruitMovementBehavior behavior = (level, fruit) -> moved[0] = true;

        MovingFruit fruit = new MovingFruit(new Position(2, 2), 10, null, behavior) {
            @Override
            public int getPoints() {
                return 10;
            }
        };

        fruit.collect();
        fruit.update(null);

        assertFalse(moved[0]);
    }

    @Test
    public void updateNoLlamaMoveSiEstaCongelada() {
        final boolean[] moved = {false};

        FruitMovementBehavior behavior = (level, fruit) -> moved[0] = true;

        MovingFruit fruit = new MovingFruit(new Position(3, 3), 10, null, behavior) {
            @Override
            public int getPoints() {
                return 10;
            }
        };

        fruit.freeze();
        fruit.update(null);

        assertFalse(moved[0]);
    }
}
