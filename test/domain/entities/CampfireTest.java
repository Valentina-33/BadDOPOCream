package domain.entities;

import domain.model.Board;
import domain.model.CellType;
import domain.model.Position;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CampfireTest {

    @Test
    public void campfireIniciaConPosicionCorrecta() {
        Campfire campfire = new Campfire(new Position(2, 3));

        assertEquals(2, campfire.getPosition().getRow());
        assertEquals(3, campfire.getPosition().getCol());
    }

    @Test
    public void extinguishCambiaLaCeldaACampfireOff() {
        Board board = new Board(6, 6);
        Position pos = new Position(2, 2);
        Campfire campfire = new Campfire(pos);

        board.setCellType(pos, CellType.CAMPFIRE_ON);
        campfire.extinguish(board);

        assertEquals(CellType.CAMPFIRE_OFF, board.getCellType(pos));
    }

    @Test
    public void campfireOffEsTransitable() {
        assertTrue(CellType.CAMPFIRE_OFF.isTraversable());
    }
}
