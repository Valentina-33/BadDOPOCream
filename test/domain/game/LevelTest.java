package domain.game;

import domain.entities.*;
import domain.model.Board;
import domain.model.Position;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LevelTest {

    @Test
    public void levelIniciaConBoardAsignado() {
        Board board = new Board(5, 5);

        Level level = new Level(board, List.of(), List.of(), List.of(), List.of(), List.of());
        assertEquals(board, level.getBoard());
    }

    @Test
    public void levelRetornaListaDeJugadoresCorrecta() {
        Player player = new Player(new Position(1, 1));

        Level level = new Level(new Board(5, 5), List.of(player), List.of(), List.of(), List.of(), List.of());

        assertEquals(1, level.getPlayers().size());
    }

    @Test
    public void levelRetornaListaDeEnemigosCorrecta() {
        Enemy enemy = new Enemy(new Position(2, 2), domain.utils.Direction.UP, null);

        Level level = new Level(new Board(5, 5), List.of(), List.of(enemy), List.of(), List.of(), List.of());

        assertEquals(1, level.getEnemies().size());
    }
}
