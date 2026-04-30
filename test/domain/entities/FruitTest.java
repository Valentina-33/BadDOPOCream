package domain.entities;

import domain.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class FruitTest {

    @Test
    public void frutaIniciaNoRecogida() {
        Fruit fruit = new Fruit(new Position(1, 1), 5, null) {
            @Override
            public void update(domain.game.Level level) { }

            @Override
            public int getPoints() {
                return 5;
            }
        };

        assertFalse(fruit.isCollected());
    }

    @Test
    public void collectMarcaLaFrutaComoRecogida() {
        Fruit fruit = new Fruit(new Position(2, 2), 10, null) {
            @Override
            public void update(domain.game.Level level) { }

            @Override
            public int getPoints() {
                return 10;
            }
        };

        fruit.collect();

        assertTrue(fruit.isCollected());
    }

    @Test
    public void getPositionRetornaLaPosicionCorrecta() {
        Position pos = new Position(3, 4);

        Fruit fruit = new Fruit(pos, 15, null) {
            @Override
            public void update(domain.game.Level level) { }

            @Override
            public int getPoints() {
                return 15;
            }
        };

        assertEquals(pos.getRow(), fruit.getPosition().getRow());
        assertEquals(pos.getCol(), fruit.getPosition().getCol());
    }
}
