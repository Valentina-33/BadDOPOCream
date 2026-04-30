package domain.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DirectionTest {

    @Test
    public void directionUpTieneDeltaCorrecto() {
        Direction d = Direction.UP;

        assertEquals(-1, d.getDRow());
        assertEquals(0, d.getDCol());
    }

    @Test
    public void directionRightTieneDeltaCorrecto() {
        Direction d = Direction.RIGHT;

        assertEquals(0, d.getDRow());
        assertEquals(1, d.getDCol());
    }

    @Test
    public void directionDownNoEsIgualADirectionUp() {
        Direction d1 = Direction.DOWN;
        Direction d2 = Direction.UP;

        assertNotEquals(d1, d2);
    }
}
