package domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    public void verificarLimitesDelTablero() {
        Board board = new Board(10, 10);
        Position adentro = new Position(5, 5);
        assertTrue(board.isInside(adentro), "La posición (5,5) debería estar dentro del tablero");

        Position negativo = new Position(-1, 0);
        assertFalse(board.isInside(negativo), "La posición (-1,0) debería estar fuera");

        Position limite = new Position(10, 10);
        assertFalse(board.isInside(limite), "La posición (10,10) debería estar fuera");
    }

    @Test
    public void verificarCambioDeCeldas() {
        Board board = new Board(5, 5);
        Position pos = new Position(1, 1);

        board.setCellType(pos, CellType.METALLIC_WALL);
        assertEquals(CellType.METALLIC_WALL, board.getCellType(pos));
        board.setCellType(pos, CellType.ICE_BLOCK);
        assertEquals(CellType.ICE_BLOCK, board.getCellType(pos));
    }

    @Test
    public void verificarSiSePuedeCaminar() {
        Board board = new Board(5, 5);
        Position pos = new Position(2, 2);

        board.setCellType(pos, CellType.FLOOR);
        assertTrue(board.isWalkable(pos));

        board.setCellType(pos, CellType.METALLIC_WALL);
        assertFalse(board.isWalkable(pos));
    }
}