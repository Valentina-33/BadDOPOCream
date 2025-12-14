package domain.behavior;

import domain.entities.Enemy;
import domain.entities.Entity;
import domain.entities.Player;
import domain.game.Level;
import domain.model.Board;
import domain.model.Position;
import domain.utils.Direction;

import java.util.*;

/**
 * Comportamiento de la Maceta:
 * Alterna entre dos modos de movimiento con diferentes velocidades:
 * MODO RANDOM: Se mueve de forma random por el mapa.
 * Cambia de dirección ocasionalmente y evita obstáculos.
 * MODO CHASING: Persigue activamente al jugador usando BFS (Breadth-First Search)
 * para encontrar el camino más corto.
 */
public class MacetaChaseMovement implements MovementBehavior {

    private enum Mode { RANDOM, CHASING }

    private Mode mode = Mode.RANDOM;

    // Contador de movimientos reales en el modo actual
    private int movementCounter = 0;

    // Cantidad de movimientos antes de cambiar de modo
    private static final int RANDOM_MOVEMENTS = 25;
    private static final int CHASE_MOVEMENTS  = 35;

    // Velocidades: cada cuántos ticks se mueve en cada modo
    private static final int RANDOM_TICKS_PER_MOVE = 20;// Más lento en random
    private static final int CHASE_TICKS_PER_MOVE  = 8;  // Más rápido en chase

    // Contador de ticks desde el último movimiento
    private int ticksSinceLastMove = 0;

    // Estado para movimiento aleatorio
    private Direction randomDir = Direction.LEFT;
    private final Random rng = new Random();

    // Estado para persecución
    private List<Direction> currentPath = new ArrayList<>();
    private int lastTargetRow = -1;
    private int lastTargetCol = -1;

    /**
     * Ejecuta el comportamiento de la maceta en cada tick del juego.
     * Alterna entre modo random y chase según la cantidad de movimientos realizados.
     */
    @Override
    public void move(Level level, Entity object) {
        Enemy enemy = (Enemy) object;
        Board board = level.getBoard();
        List<Player> players = level.getPlayers();
        if (players.isEmpty()) return;

        // Incrementar contador de ticks
        ticksSinceLastMove++;

        // Determinar velocidad según el modo actual
        int ticksPerMove = (mode == Mode.RANDOM)
                ? RANDOM_TICKS_PER_MOVE
                : CHASE_TICKS_PER_MOVE;

        // Control de velocidad: solo moverse cada X ticks
        if (ticksSinceLastMove < ticksPerMove) {
            return;
        }

        ticksSinceLastMove = 0;

        // Ejecutar lógica del modo actual
        if (mode == Mode.RANDOM) {
            randomStep(board, enemy);
            movementCounter++;

            // Cambiar a modo persecución después de suficientes movimientos
            if (movementCounter >= RANDOM_MOVEMENTS) {
                switchToChaseMode();
            }

        } else { // CHASING
            Player target = findNearestAlivePlayer(level, enemy);
            if (target == null) {
                switchToRandomMode();
                return;
            }

            Position start = enemy.getPosition();
            Position goal  = target.getPosition();

            chaseStep(board, enemy, start, goal);
            movementCounter++;

            // Volver a modo random después de suficientes movimientos
            if (movementCounter >= CHASE_MOVEMENTS) {
                switchToRandomMode();
            }
        }
    }

    private Player findNearestAlivePlayer(Level level, Enemy enemy) {
        Player nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (Player p : level.getPlayers()) {
            if (p.isDead()) continue;

            int d = Math.abs(enemy.getPosition().getRow() - p.getPosition().getRow())
                    + Math.abs(enemy.getPosition().getCol() - p.getPosition().getCol());

            if (d < minDistance) {
                minDistance = d;
                nearest = p;
            }
        }

        return nearest;
    }

    /**
     * Cambia al modo de persecución activa.
     * Resetea contadores y limpia el camino anterior.
     */
    private void switchToChaseMode() {
        mode = Mode.CHASING;
        movementCounter = 0;
        ticksSinceLastMove = 0;
        currentPath.clear();
        lastTargetRow = -1;
        lastTargetCol = -1;
    }

    /**
     * Cambia al modo de movimiento aleatorio.
     * Resetea contadores y limpia el camino de persecución.
     */
    private void switchToRandomMode() {
        mode = Mode.RANDOM;
        movementCounter = 0;
        ticksSinceLastMove = 0;
        currentPath.clear();
        lastTargetRow = -1;
        lastTargetCol = -1;
    }

    /**
     * Ejecuta un paso de movimiento aleatorio.
     * La maceta tiene 30% de probabilidad de cambiar de dirección en cada movimiento,
     * y siempre cambia de dirección si encuentra un obstáculo.
     */
    private void randomStep(Board board, Enemy enemy) {
        Position pos = enemy.getPosition();

        // 30% de probabilidad de cambiar de dirección espontáneamente
        if (rng.nextDouble() < 0.3) {
            randomDir = randomWalkableDirection(board, pos);
        }

        // Verificar si la dirección actual está bloqueada
        Position next = pos.translated(randomDir.getDRow(), randomDir.getDCol());
        if (!board.isInside(next) || !board.isWalkable(next)) {
            randomDir = randomWalkableDirection(board, pos);
            next = pos.translated(randomDir.getDRow(), randomDir.getDCol());
        }

        // Moverse si hay una dirección válida
        if (board.isInside(next) && board.isWalkable(next)) {
            enemy.setPosition(next);
            enemy.setDirection(randomDir);
        }
    }

    /**
     * Encuentra una dirección aleatoria válida (sin obstáculos) desde la posición actual.
     */
    private Direction randomWalkableDirection(Board board, Position pos) {
        Direction[] allDirections = {
                Direction.UP,
                Direction.DOWN,
                Direction.LEFT,
                Direction.RIGHT
        };

        List<Direction> validDirections = new ArrayList<>();

        // Filtrar solo direcciones caminables
        for (Direction dir : allDirections) {
            Position nextPos = pos.translated(dir.getDRow(), dir.getDCol());
            if (board.isInside(nextPos) && board.isWalkable(nextPos)) {
                validDirections.add(dir);
            }
        }

        if (validDirections.isEmpty()) {
            return Direction.NONE;
        }

        return validDirections.get(rng.nextInt(validDirections.size()));
    }

    /**
     * Ejecuta un paso de persecución hacia el jugador.
     * Usa BFS para calcular el camino más corto y sigue ese camino.
     * Recalcula el camino si el jugador se mueve o si el camino se bloquea.
     */
    private void chaseStep(Board board, Enemy enemy, Position start, Position goal) {

        // Recalcular camino si no hay camino o el jugador se movió
        if (currentPath.isEmpty()
                || goal.getRow() != lastTargetRow
                || goal.getCol() != lastTargetCol) {

            currentPath = bfs(board, start, goal);
            lastTargetRow = goal.getRow();
            lastTargetCol = goal.getCol();
        }

        if (currentPath.isEmpty()) {
            // No hay camino disponible, quedarse quieto
            return;
        }

        Direction stepDir = currentPath.removeFirst();
        Position next = start.translated(stepDir.getDRow(), stepDir.getDCol());

        if (board.isInside(next) && board.isWalkable(next)) {
            enemy.setPosition(next);
            enemy.setDirection(stepDir);
        } else {
            // Camino bloqueado (nuevo hielo, etc.), limpiar y recalcular en siguiente tick
            currentPath.clear();
        }
    }

    /**
     * Algoritmo BFS (Breadth-First Search) para encontrar el camino más corto
     * desde start hasta goal en el tablero.
     * Retorna una lista de direcciones que representan el camino.
     */
    private List<Direction> bfs(Board board, Position start, Position goal) {
        int rows = board.getRows();
        int cols = board.getCols();

        boolean[][] visited = new boolean[rows][cols];
        int[][] parentR = new int[rows][cols];
        int[][] parentC = new int[rows][cols];

        // Inicializar padres como -1 (sin padre)
        for (int r = 0; r < rows; r++) {
            Arrays.fill(parentR[r], -1);
            Arrays.fill(parentC[r], -1);
        }

        Queue<Position> queue = new ArrayDeque<>();
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;

        // Direcciones: arriba, abajo, izquierda, derecha
        Direction[] directions = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

        boolean found = false;

        // BFS principal
        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int cr = current.getRow();
            int cc = current.getCol();

            // Meta alcanzada
            if (cr == goal.getRow() && cc == goal.getCol()) {
                found = true;
                break;
            }

            // Explorar vecinos
            for (Direction dir : directions) {
                int nr = cr + dir.getDRow();
                int nc = cc + dir.getDCol();
                Position np = new Position(nr, nc);

                if (!board.isInside(np)) continue;
                if (visited[nr][nc]) continue;
                if (!board.isWalkable(np)) continue;

                visited[nr][nc] = true;
                parentR[nr][nc] = cr;
                parentC[nr][nc] = cc;
                queue.add(np);
            }
        }

        if (!found) return new ArrayList<>();

        // Reconstruir camino desde goal hasta start
        List<Direction> path = new ArrayList<>();
        int r = goal.getRow();
        int c = goal.getCol();

        while (!(r == start.getRow() && c == start.getCol())) {
            int pr = parentR[r][c];
            int pc = parentC[r][c];

            if (pr == -1 && pc == -1) {
                // No hay padre válido (no debería pasar si found == true)
                return new ArrayList<>();
            }

            Direction dir = directionFrom(pr, pc, r, c);
            path.addFirst(dir);

            r = pr;
            c = pc;
        }

        return path;
    }

    /**
     * Determina la dirección de movimiento entre dos posiciones adyacentes.
     * Usado para reconstruir el camino en BFS.
     */
    private Direction directionFrom(int r1, int c1, int r2, int c2) {
        if (r2 == r1 - 1 && c2 == c1) return Direction.UP;
        if (r2 == r1 + 1 && c2 == c1) return Direction.DOWN;
        if (r2 == r1 && c2 == c1 - 1) return Direction.LEFT;
        if (r2 == r1 && c2 == c1 + 1) return Direction.RIGHT;
        return Direction.NONE;
    }
}
