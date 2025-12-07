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
 * Maceta:
 * - MODO RANDOM: se mueve de forma pseudo-aleatoria por el mapa (~5 segundos).
 * - MODO CHASING: persigue al jugador usando BFS, más rápido (~5 segundos).
 */
public class MacetaChaseMovement implements MovementBehavior {

    private enum Mode { RANDOM, CHASING }

    private Mode mode = Mode.RANDOM;

    // Contador de movimientos reales en el modo actual
    private int movementCounter = 0;

    // Duraciones en número de MOVIMIENTOS (valores más bajos para testing)
    // Si tu juego corre a 60 FPS y mueves cada 2 frames = 30 movimientos/seg
    // 5 segundos = 150 movimientos en modo random (30 mov/seg * 5 seg)
    // 5 segundos = 300 movimientos en modo chase (60 mov/seg * 5 seg)
    private static final int RANDOM_MOVEMENTS = 25;  // Reducido para testing
    private static final int CHASE_MOVEMENTS  = 35; // Reducido para testing

    // Velocidades (cada cuántos ticks se mueve)
    private static final int RANDOM_TICKS_PER_MOVE = 2; // más lento en random
    private static final int CHASE_TICKS_PER_MOVE  = 1;  // más rápido en chase

    // Contador de ticks para el movimiento actual
    private int ticksSinceLastMove = 0;

    // Estado interno para random
    private Direction randomDir = Direction.LEFT;
    private final Random rng = new Random();

    // Estado interno para persecución
    private List<Direction> currentPath = new ArrayList<>();
    private int lastTargetRow = -1;
    private int lastTargetCol = -1;

    @Override
    public void move(Level level, Entity object) {
        Enemy enemy = (Enemy) object;
        Board board = level.getBoard();
        List<Player> players = level.getPlayers();
        if (players.isEmpty()) return;

        Player target = players.get(0);
        Position start = enemy.getPosition();
        Position goal  = target.getPosition();

        // Incrementar contador de ticks
        ticksSinceLastMove++;

        // Determinar cada cuántos ticks se mueve según el modo actual
        int ticksPerMove = (mode == Mode.RANDOM)
                ? RANDOM_TICKS_PER_MOVE
                : CHASE_TICKS_PER_MOVE;

        // Si no ha pasado suficiente tiempo, no moverse
        if (ticksSinceLastMove < ticksPerMove) {
            return;
        }

        // Resetear contador de ticks
        ticksSinceLastMove = 0;

        // IMPORTANTE: Incrementar DESPUÉS de verificar el cambio de modo
        // para que el primer movimiento cuente correctamente

        // Ejecutar lógica del modo actual
        if (mode == Mode.RANDOM) {
            randomStep(board, enemy);
            movementCounter++;

            // Cambiar a CHASING después de suficientes movimientos
            if (movementCounter >= RANDOM_MOVEMENTS) {
                switchToChaseMode();
            }

        } else { // CHASING
            chaseStep(board, enemy, start, goal);
            movementCounter++;
            // Volver a RANDOM después de suficientes movimientos
            if (movementCounter >= CHASE_MOVEMENTS) {
                switchToRandomMode();
            }
        }
    }

    private void switchToChaseMode() {
        mode = Mode.CHASING;
        movementCounter = 0;
        ticksSinceLastMove = 0; // Reset para que empiece inmediatamente
        currentPath.clear();
    }

    private void switchToRandomMode() {
        mode = Mode.RANDOM;
        movementCounter = 0;
        ticksSinceLastMove = 0; // Reset para que empiece inmediatamente
        currentPath.clear();
    }

    // ===================== MODO RANDOM =====================

    private void randomStep(Board board, Enemy enemy) {
        Position pos = enemy.getPosition();

        // 30% de probabilidad de cambiar de dirección aunque no choque
        if (rng.nextDouble() < 0.3) {
            randomDir = randomWalkableDirection(board, pos);
        }

        // Si la dirección actual está bloqueada, buscamos otra
        Position next = pos.translated(randomDir.getDRow(), randomDir.getDCol());
        if (!board.isInside(next) || !board.isWalkable(next)) {
            randomDir = randomWalkableDirection(board, pos);
            next = pos.translated(randomDir.getDRow(), randomDir.getDCol());
        }

        // Si encontramos una dirección válida, nos movemos
        if (board.isInside(next) && board.isWalkable(next)) {
            enemy.setPosition(next);
            enemy.setDirection(randomDir);
        }
    }

    private Direction randomWalkableDirection(Board board, Position pos) {
        Direction[] dirs = {
                Direction.UP,
                Direction.DOWN,
                Direction.LEFT,
                Direction.RIGHT
        };

        List<Direction> candidates = new ArrayList<>();

        for (Direction d : dirs) {
            Position np = pos.translated(d.getDRow(), d.getDCol());
            if (board.isInside(np) && board.isWalkable(np)) {
                candidates.add(d);
            }
        }

        if (candidates.isEmpty()) {
            return Direction.NONE;
        } else {
            return candidates.get(rng.nextInt(candidates.size()));
        }
    }

    // ===================== MODO CHASING (BFS) =====================

    private void chaseStep(Board board, Enemy enemy, Position start, Position goal) {

        // Recalcular camino si:
        //  - No tenemos camino
        //  - El jugador cambió de casilla
        if (currentPath.isEmpty()
                || goal.getRow() != lastTargetRow
                || goal.getCol() != lastTargetCol) {

            currentPath = bfs(board, start, goal);
            lastTargetRow = goal.getRow();
            lastTargetCol = goal.getCol();
        }

        if (currentPath.isEmpty()) {
            // No hay camino, quedarse quieto
            return;
        }

        Direction stepDir = currentPath.remove(0);
        Position next = start.translated(stepDir.getDRow(), stepDir.getDCol());

        if (board.isInside(next) && board.isWalkable(next)) {
            enemy.setPosition(next);
            enemy.setDirection(stepDir);
        } else {
            // Camino bloqueado (hielo nuevo, etc.)
            currentPath.clear();
        }
    }

    private List<Direction> bfs(Board board, Position start, Position goal) {
        int rows = board.getRows();
        int cols = board.getCols();

        boolean[][] visited = new boolean[rows][cols];
        int[][] parentR = new int[rows][cols];
        int[][] parentC = new int[rows][cols];

        for (int r = 0; r < rows; r++) {
            Arrays.fill(parentR[r], -1);
            Arrays.fill(parentC[r], -1);
        }

        Queue<Position> queue = new ArrayDeque<>();
        queue.add(start);
        visited[start.getRow()][start.getCol()] = true;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        boolean found = false;

        while (!queue.isEmpty()) {
            Position current = queue.poll();
            int cr = current.getRow();
            int cc = current.getCol();

            if (cr == goal.getRow() && cc == goal.getCol()) {
                found = true;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nr = cr + dr[i];
                int nc = cc + dc[i];

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

        List<Direction> path = new ArrayList<>();
        int r = goal.getRow();
        int c = goal.getCol();

        while (!(r == start.getRow() && c == start.getCol())) {
            int pr = parentR[r][c];
            int pc = parentC[r][c];

            if (pr == -1 && pc == -1) {
                return new ArrayList<>();
            }

            Direction d = directionFrom(pr, pc, r, c);
            path.add(0, d);

            r = pr;
            c = pc;
        }

        return path;
    }

    private Direction directionFrom(int r1, int c1, int r2, int c2) {
        if (r2 == r1 - 1 && c2 == c1) return Direction.UP;
        if (r2 == r1 + 1 && c2 == c1) return Direction.DOWN;
        if (r2 == r1 && c2 == c1 - 1) return Direction.LEFT;
        if (r2 == r1 && c2 == c1 + 1) return Direction.RIGHT;
        return Direction.NONE;
    }
}