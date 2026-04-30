package domain.game;

import domain.entities.*;
import domain.model.Board;
import domain.model.Position;
import domain.utils.Direction;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CollisionDetectorTest {

    @Test
    public void jugadorRecogeFruta() {
        Position pos = new Position(2, 2);

        Player jugador = new Player(pos);
        assertEquals(0, jugador.getScore());

        Banana banana = new Banana(pos);
        assertFalse(banana.isCollected(), "La banana debería estar sin recoger al inicio");

        List<Player> players = new ArrayList<>();
        players.add(jugador);

        List<Fruit> fruits = new ArrayList<>();
        fruits.add(banana);

        CollisionDetector.checkPlayerFruit(players, fruits);

        assertTrue(banana.isCollected(), "La banana debió ser recogida");
        assertTrue(jugador.getScore() > 0, "El puntaje del jugador debió aumentar");
    }

    @Test
    public void jugadorNoRecogeFrutaLejos() {
        Player jugador = new Player(new Position(1, 1));

        Banana banana = new Banana(new Position(3, 3));

        List<Player> players = List.of(jugador);
        List<Fruit> fruits = List.of(banana);

        CollisionDetector.checkPlayerFruit(players, fruits);

        assertFalse(banana.isCollected(), "La banana está lejos, no debió recogerse");
        assertEquals(0, jugador.getScore(), "El puntaje no debió cambiar");
    }

    @Test
    public void checkPlayerFruitSumaPuntosYMarcaRecogida() {
        Player player = new Player(new Position(2, 2));

        Fruit fruit = new Fruit(new Position(2, 2), 10, null) {
            @Override
            public void update(domain.game.Level level) { }

            @Override
            public int getPoints() {
                return 10;
            }
        };

        CollisionDetector.checkPlayerFruit(List.of(player), List.of(fruit));

        assertTrue(fruit.isCollected());
        assertEquals(10, player.getScore());
    }

    @Test
    public void checkPlayerEnemyMataAlJugadorSiEstanEnLaMismaCelda() {
        Player player = new Player(new Position(1, 1));
        Enemy enemy = new Enemy(new Position(1, 1), Direction.DOWN, null);

        CollisionDetector.checkPlayerEnemy(List.of(player), List.of(enemy));

        assertTrue(player.isDead());
    }

    @Test
    public void checkPlayerCampfireMataSiLaFogataEstaEncendidaYMismaCelda() {
        Player player = new Player(new Position(2, 2));
        Campfire campfire = new Campfire(new Position(2, 2));

        CollisionDetector.checkPlayerCampfire(List.of(player), List.of(campfire));

        assertTrue(player.isDead());
    }

    @Test
    public void checkPlayerCampfireNoMataSiLaFogataEstaApagada() {
        Board board = new Board(5, 5);
        Player player = new Player(new Position(2, 2));
        Campfire campfire = new Campfire(new Position(2, 2));

        campfire.extinguish(board);

        CollisionDetector.checkPlayerCampfire(List.of(player), List.of(campfire));

        assertFalse(player.isDead());
    }

    @Test
    public void checkPlayerCactusMataSiElCactusEsPeligrosoYMismaCelda() {
        Board board = new Board(7, 7);
        Player player = new Player(new Position(1, 1));
        Cactus cactus = new Cactus(new Position(3, 3));

        Level level = new Level(board, List.of(player), List.of(), List.of(cactus), List.of(), List.of());

        for (int i = 0; i < 600; i++) {
            cactus.update(level);
        }

        player.setPosition(new Position(3, 3));

        CollisionDetector.checkPlayerCactus(List.of(player), List.of(cactus));

        assertTrue(player.isDead());
    }

}