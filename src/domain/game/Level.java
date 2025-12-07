package domain.game;
import domain.model.*;
import domain.entities.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import domain.utils.Direction;


/**
 * Representa un nivel del juego
 */
public class Level {
    private final Board board;
    private final List<Player> players;
    private final List<Enemy> enemies;
    private final List<Fruit> fruits;
    private final FruitManager fruitManager;
    private final List<Class<? extends Fruit>> fruitPhases;
    private int currentPhaseIndex = 0;
    private final Map<Player, Direction> lastInputs = new HashMap<>();


    public Level (Board board, List<Player> players, List<Enemy> enemies, List<Fruit> fruits, List<Class<? extends Fruit>> fruitPhases) {
        this.board = board;
        this.players = players != null ? players : new ArrayList<>();
        this.enemies = enemies != null ? enemies : new ArrayList<>();
        this.fruits = fruits != null ? fruits : new ArrayList<>();
        this.fruitManager = new FruitManager(this.fruits);
        this.fruitPhases  = fruitPhases != null ? fruitPhases : new ArrayList<>();
        initFirstFruitPhase();
    }

    public Board getBoard() {return this.board;}
    public List<Player> getPlayers() {return this.players;}
    public List<Enemy> getEnemies() {return this.enemies;}
    public List<Fruit> getFruits() {return this.fruits;}
    public FruitManager getFruitManager() {return this.fruitManager;}


    private void initFirstFruitPhase() {
        if (fruitPhases.isEmpty()) {
            fruitManager.activateAll();
        } else {
            Class<? extends Fruit> firstClass = fruitPhases.get(0);
            fruitManager.activateByClass(firstClass);
        }
    }

    /**
     * Actualiza el estado del nivel
     * Mueve el jugador según la dirección dada
     * Mueve a los enemigos de acuerdo a su MovementBehavior
     */
    public void update(Map<Player, Direction> playersInputs) {
        updatePlayers(playersInputs);
        updateFruits();
        CollisionDetector.checkPlayerFruit(players, fruitManager.getActiveFruits());
        CollisionDetector.checkPlayerEnemy(players, enemies);

        updateFruitPhase();
        updateEnemies();
    }

    private void updateFruits() {
        for (Fruit fruit : fruitManager.getActiveFruits()) {
            if (!fruit.isCollected() && !fruit.isFrozen() ) {
                fruit.update(this);
            }
        }
    }

    private void updateFruitPhase() {
        // Si no hay fases definidas, no hacemos nada especial
        if (fruitPhases.isEmpty()) return;

        // Si aún faltan frutas activas por recoger, seguimos en la misma fase
        if (!fruitManager.allActiveCollected()) return;

        // Si ya estamos en la última fase, no hay siguiente
        if (currentPhaseIndex + 1 >= fruitPhases.size()) {
            return;
        }

        // Pasamos a la siguiente fase
        currentPhaseIndex++;
        Class<? extends Fruit> nextClass = fruitPhases.get(currentPhaseIndex);
        fruitManager.activateByClass(nextClass);
    }

    /**
     * Cuando tenemos varios jugadores en el tablero
     * @param playerInputs
     */
    private void updatePlayers(Map<Player, Direction> playerInputs) {
        if (players.isEmpty()) return;

        for (Player p : players) {
            Direction inputDir = playerInputs.getOrDefault(p, Direction.NONE);
            Direction lastInput = lastInputs.getOrDefault(p, Direction.NONE);

            // Si no hay tecla pulsada, lo consideramos quieto
            if (inputDir == null || inputDir == Direction.NONE) {
                lastInputs.put(p, Direction.NONE);
                continue;
            }

            boolean wasStopped = (lastInput == Direction.NONE);

            // Caso 1: la nueva dirección es distinta a la dirección actual de la "carita"
            if (inputDir != p.getDirection()) {

                if (wasStopped) {
                    //Estaba quieto: solo girar, sin avanzar todavía
                    p.setDirection(inputDir);

                } else {
                    //Ya venía con una dirección pulsada: girar y moverse en el mismo frame
                    p.setDirection(inputDir);
                    movePlayer(p, inputDir);
                }

            } else {
                // Caso 2: la dirección pedida coincide con la que ya mira, avanzar normal
                movePlayer(p, inputDir);
            }

            // Actualizamos el último input visto para este jugador
            lastInputs.put(p, inputDir);
        }
    }



    private void movePlayer(Player p, Direction dir){
        if (dir == null || dir == Direction.NONE) { return; }

        Position current = p.getPosition();
        Position next = current.translated(dir.getDRow(), dir.getDCol());

        if (board.isWalkable(next)) {
            p.setPosition(next);
        }
    }


    /**
     * Para cuando hay un solo jugador en el juego
     * @param dir
     */
    public void updateSinglePlayer(Direction dir) {
        if (dir == null || dir == Direction.NONE) return;

        Player p = players.get(0);
        movePlayer(p, dir);

        CollisionDetector.checkPlayerFruit(players, fruitManager.getActiveFruits());
        CollisionDetector.checkPlayerEnemy(players, enemies);

        updateFruitPhase();
        updateEnemies();
    }


    private void updateEnemies() {
        for (Enemy e: enemies) {
            if (e.getMovementBehavior() != null) {
                e.getMovementBehavior().move(this, e);
            }
        }
    }

}
