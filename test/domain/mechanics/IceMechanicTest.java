package domain.mechanics;

import domain.entities.Player;
import domain.game.Level;
import domain.model.Board;
import domain.model.CellType;
import domain.model.Position;
import domain.utils.Direction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class IceMechanicTest {

    @Test
    public void crearHieloEnPisoVacio() {
        Board board = new Board(5, 5);
        Position frente = new Position(2, 2);
        board.setCellType(frente, CellType.FLOOR);

        Player p = new Player(new Position(2, 1));
        p.setDirection(Direction.RIGHT);

        Level level = new Level(board, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        IceMechanic mechanic = new IceMechanic();
        mechanic.triggerIceAction(level, p);

        assertEquals(CellType.PLAYER_ICE, board.getCellType(frente), "El piso debió convertirse en hielo");
    }

    @Test
    public void romperHieloExistente() {
        Board board = new Board(5, 5);
        Position frente = new Position(2, 2);

        board.setCellType(frente, CellType.PLAYER_ICE);

        Player p = new Player(new Position(2, 1));
        p.setDirection(Direction.RIGHT);

        Level level = new Level(board, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
        IceMechanic mechanic = new IceMechanic();

        mechanic.triggerIceAction(level, p);

        assertEquals(CellType.FLOOR, board.getCellType(frente), "El hielo debió romperse");
    }
}