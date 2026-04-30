package domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test
    public void posicionesConMismosValoresTienenMismaFilaYColumna() {
        Position p1 = new Position(2, 3);
        Position p2 = new Position(2, 3);

        assertEquals(p1.getRow(), p2.getRow());
        assertEquals(p1.getCol(), p2.getCol());
    }

    @Test
    public void posicionesConValoresDiferentesDifierenEnFilaOColumna() {
        Position p1 = new Position(2, 3);
        Position p2 = new Position(3, 2);

        assertTrue(p1.getRow() != p2.getRow() || p1.getCol() != p2.getCol());
    }

    @Test
    public void gettersRetornanFilaYColumnaCorrectas() {
        Position p = new Position(4, 7);

        assertEquals(4, p.getRow());
        assertEquals(7, p.getCol());
    }
}
