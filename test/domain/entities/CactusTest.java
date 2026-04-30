package domain.entities;

import domain.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CactusTest {

    @Test
    public void cactusIniciaConPosicionCorrecta() {
        Cactus cactus = new Cactus(new Position(2, 3));

        assertEquals(2, cactus.getPosition().getRow());
        assertEquals(3, cactus.getPosition().getCol());
    }

    @Test
    public void cactusIniciaNoRecogido() {
        Cactus cactus = new Cactus(new Position(1, 1));

        assertFalse(cactus.isCollected());
    }

    @Test
    public void collectMarcaElCactusComoRecogido() {
        Cactus cactus = new Cactus(new Position(4, 4));

        cactus.collect();

        assertTrue(cactus.isCollected());
    }
}
