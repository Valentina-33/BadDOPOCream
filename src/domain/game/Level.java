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
    private final FruitManager fruitManager;
    private final List<Campfire> campfires;
    private final List<Class<? extends Fruit>> fruitPhases;
    private int currentPhaseIndex = 0;
    private final Map<Player, Direction> lastInputs = new HashMap<>();
    private int playerTickCounter = 0;
    private static final int TICKS_PER_PLAYER_MOVE = 8;


    public Level (Board board, List<Player> players, List<Enemy> enemies, List<Fruit> fruits, List<Campfire> campfires,
                  List<Class<? extends Fruit>> fruitPhases) {
        this.board = board;
        this.players = players != null ? players : new ArrayList<>();
        this.enemies = enemies != null ? enemies : new ArrayList<>();
        List<Fruit> fruits1 = fruits != null ? fruits : new ArrayList<>();
        this.campfires = campfires != null ? campfires : new ArrayList<>();
        this.fruitManager = new FruitManager(fruits1);
        this.fruitPhases  = fruitPhases != null ? fruitPhases : new ArrayList<>();
        initFirstFruitPhase();
    }

    public Board getBoard() {return this.board;}
    public List<Player> getPlayers() {return this.players;}
    public List<Enemy> getEnemies() {return this.enemies;}
    public FruitManager getFruitManager() {return this.fruitManager;}
    public List<Campfire> getCampfires() { return this.campfires; }


    private void initFirstFruitPhase() {
        if (fruitPhases.isEmpty()) {
            fruitManager.activateAll();
        } else {
            Class<? extends Fruit> firstClass = fruitPhases.getFirst();
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
        CollisionDetector.checkPlayerCampfire(players, campfires); // NUEVO
        updateFruitPhase();
        updateEnemies();
        updateCampfires();
    }

    private void updateFruits() {
        for (Fruit fruit : fruitManager.getActiveFruits()) {
            if (!fruit.isCollected() && !fruit.isFrozen() ) {
                fruit.update(this);
            }
        }
    }

    private void updateFruitPhase() {
        // Si no hay fases definidas, no pasa nada
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
     * Manejo de varios jugadores en el tablero
     */
    private void updatePlayers(Map<Player, Direction> playerInputs) {
        if (players.isEmpty()) return;

        // Control de velocidad del jugador
        playerTickCounter++;
        if (playerTickCounter % TICKS_PER_PLAYER_MOVE != 0) {
            return;
        }

        for (Player p : players) {
            if (p.isDead()) continue;

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

                p.setDirection(inputDir);
                if (wasStopped) {
                    // Estaba quieto: solo girar, sin avanzar todavía
                    lastInputs.put(p, inputDir);
                    continue;
                } else {
                    // Ya venía con una dirección pulsada: girar y moverse en el mismo frame
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

    private void updateEnemies() {
        for (Enemy e: enemies) {
            if (e.getMovementBehavior() != null) {
                e.getMovementBehavior().move(this, e);
            }
        }
    }
    private void updateCampfires() {
        for (Campfire campfire : campfires) {
            campfire.update(this);
        }
    }

    /**
     * Retorna true cuando ya se terminaron todas las fases de frutas del nivel.
     */
    public boolean isLevelCompleted() {
        if (!fruitManager.allActiveCollected()) return false;

        if (fruitPhases.isEmpty()) {
            return true;
        }

        return currentPhaseIndex + 1 >= fruitPhases.size();
    }

}
