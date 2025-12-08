package domain.game;

import domain.behavior.TrollTurnRightMovement;
import domain.entities.*;
import domain.model.Board;
import domain.model.CellType;
import domain.model.Position;
import domain.utils.Direction;
import presentation.GamePanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static domain.game.CollisionDetector.sameCell;

/**
 * Estado de juego activo:
 * contiene un Level y maneja el movimiento de jugadores y enemigos.
 */
public class PlayingState implements GameState {

    private final Game game;
    private final Level level;
    private int timerTicks = 0;
    private static final int TIME_LIMIT = 10800; // 3 minutos a 60 FPS (3 * 60 * 60)
    private boolean timeUp = false;
    private int currentLevelNumber;


    //Dirección actual del jugador 1 (más adelante añadimos jugador 2)
    private Direction p1Dir = Direction.NONE;

    public PlayingState(Game game, int levelNumber) {
        this.game = game;
        this.currentLevelNumber = levelNumber; // NUEVO

        //Crear tablero según el tamaño lógico del GamePanel
        Board board = new Board(GamePanel.ROWS, GamePanel.COLS);

        //Crear el Level
        this.level = LevelFactory.createLevel(levelNumber);
    }

    @Override
    public void update() {
        // Verificar si el tiempo se acabó
        if (timeUp) {
            return; // No actualizar nada más
        }

        // Incrementar el timer
        timerTicks++;

        // Verificar si se acabó el tiempo
        if (timerTicks >= TIME_LIMIT) {
            timeUp = true;
            handleTimeUp();
            return;
        }

        // Construimos el mapa de inputs para los jugadores
        Map<Player, Direction> inputs = new HashMap<>();

        // Por ahora solo hay un jugador
        Player p1 = level.getPlayers().get(0);
        inputs.put(p1, p1Dir);

        // Actualizamos la lógica del nivel
        level.update(inputs);
    }

    private void handleTimeUp() {
        // Esperar 2 segundos y reiniciar
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 2 segundos de pausa

                // Reiniciar el nivel (volver a crear PlayingState con el mismo nivel)
                int currentLevel = getCurrentLevelNumber(); // Necesitamos saber qué nivel es
                game.setState(new PlayingState(game, currentLevel));

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private int getCurrentLevelNumber() {
        return this.currentLevelNumber;
    }

    @Override
    public void render(Graphics2D g) {
        Board board = level.getBoard();
        int tile = GamePanel.TILE_SIZE;

        // ===== FONDO =====
        g.setColor(new Color(240, 248, 255)); // Azul muy claro (hielo)
        g.fillRect(0, 0, board.getCols() * tile, board.getRows() * tile);

        // ===== DIBUJAR TABLERO (paredes + hielo + piso) =====
        for (int r = 0; r < board.getRows(); r++) {
            for (int c = 0; c < board.getCols(); c++) {
                Position p = new Position(r, c);
                CellType cell = board.getCellType(p);

                int x = c * tile;
                int y = r * tile;

                switch (cell) {
                    case METALLIC_WALL -> {
                        // Pared metálica - Gris oscuro
                        g.setColor(new Color(60, 60, 70));
                        g.fillRect(x, y, tile, tile);
                        // Borde para dar efecto 3D
                        g.setColor(new Color(90, 90, 100));
                        g.drawRect(x, y, tile - 1, tile - 1);
                    }

                    case ICE_BLOCK -> {
                        // Hielo normal - Azul claro brillante
                        g.setColor(new Color(150, 220, 255));
                        g.fillRect(x, y, tile, tile);
                        // Efecto de brillo
                        g.setColor(new Color(200, 240, 255, 150));
                        g.fillRect(x + 2, y + 2, tile / 3, tile / 3);
                    }

                    case PLAYER_ICE -> {
                        // Hielo del jugador - Gris plateado
                        g.setColor(new Color(180, 190, 200));
                        g.fillRect(x, y, tile, tile);
                        g.setColor(new Color(220, 230, 240));
                        g.fillRect(x + 2, y + 2, tile / 3, tile / 3);
                    }

                    case CAMPFIRE_ON -> {
                        // Fogata encendida - Rojo/Naranja (ya se dibuja después)
                        g.setColor(new Color(255, 255, 240));
                        g.fillRect(x, y, tile, tile);
                    }

                    case HOT_TILE -> {
                        g.setColor(new Color(255,120,50));
                        g.fillRect(x, y, tile, tile);
                    }

                    case CAMPFIRE_OFF -> {
                        // Fogata apagada - Gris (ya se dibuja después)
                        g.setColor(new Color(255, 255, 240));
                        g.fillRect(x, y, tile, tile);
                    }

                    default -> {
                        // Piso normal - Blanco/Crema
                        g.setColor(new Color(255, 255, 240));
                        g.fillRect(x, y, tile, tile);
                    }
                }
            }
        }

        // ===== DIBUJAR FOGATAS =====
        for (Campfire campfire : level.getCampfires()) {
            Position p = campfire.getPosition();
            int x = p.getCol() * tile;
            int y = p.getRow() * tile;

            if (campfire.isLit()) {
                // 🔥 Fogata ENCENDIDA - Rojo/Naranja vibrante
                // Base de la fogata
                g.setColor(new Color(139, 69, 19)); // Marrón (leña)
                g.fillRect(x + tile / 3, y + tile / 2, tile / 3, tile / 3);

                // Llamas grandes
                g.setColor(new Color(255, 69, 0)); // Rojo-naranja
                int[] xFlame = {x + tile / 2, x + tile / 4, x + 3 * tile / 4};
                int[] yFlame = {y + tile / 6, y + tile / 2, y + tile / 2};
                g.fillPolygon(xFlame, yFlame, 3);

                // Llamas pequeñas (amarillas)
                g.setColor(new Color(255, 215, 0)); // Dorado brillante
                int[] xFlame2 = {x + tile / 2, x + tile / 3, x + 2 * tile / 3};
                int[] yFlame2 = {y + tile / 4, y + tile / 2, y + tile / 2};
                g.fillPolygon(xFlame2, yFlame2, 3);
            } else {
                // 💨 Fogata APAGADA - Gris oscuro con cenizas
                g.setColor(new Color(50, 50, 50)); // Carbón apagado
                g.fillRect(x + tile / 3, y + tile / 2, tile / 3, tile / 3);

                // Cenizas
                g.setColor(new Color(120, 120, 120));
                g.fillOval(x + tile / 4, y + tile / 2 + 5, tile / 6, tile / 6);
                g.fillOval(x + tile / 2, y + tile / 2 + 3, tile / 6, tile / 6);
            }
        }

        // ===== DIBUJAR FRUTAS =====
        for (Fruit f : level.getFruitManager().getActiveFruits()) {
            if (!f.isCollected()) {
                int x = f.getPosition().getCol() * tile;
                int y = f.getPosition().getRow() * tile;

                if (f instanceof Cherry) {
                    // 🍒 CEREZA - Rojo brillante
                    g.setColor(new Color(220, 20, 60)); // Rojo cereza
                    g.fillOval(x + tile / 4, y + tile / 4, tile / 2, tile / 2);
                    // Brillo
                    g.setColor(new Color(255, 100, 120));
                    g.fillOval(x + tile / 3, y + tile / 3, tile / 6, tile / 6);
                    // Tallo
                    g.setColor(new Color(34, 139, 34));
                    g.drawLine(x + tile / 2, y + tile / 4, x + tile / 2, y + tile / 6);

                } else if (f instanceof Cactus) {
                    Cactus cactus = (Cactus) f;
                    if (cactus.isDangerous()) {
                        // 🌵 CACTUS PELIGROSO - Verde oscuro con púas rojas
                        g.setColor(new Color(34, 139, 34)); // Verde oscuro
                        g.fillRect(x + tile / 3, y + tile / 4, tile / 3, tile / 2);

                        // Púas rojas (peligro!)
                        g.setColor(new Color(255, 0, 0));
                        int[] xSpike1 = {x + tile / 3, x + tile / 4, x + tile / 3};
                        int[] ySpike1 = {y + tile / 3, y + tile / 2, y + 2 * tile / 3};
                        g.fillPolygon(xSpike1, ySpike1, 3);

                        int[] xSpike2 = {x + 2 * tile / 3, x + 3 * tile / 4, x + 2 * tile / 3};
                        int[] ySpike2 = {y + tile / 3, y + tile / 2, y + 2 * tile / 3};
                        g.fillPolygon(xSpike2, ySpike2, 3);
                    } else {
                        // 🌵 CACTUS SEGURO - Verde claro sin púas
                        g.setColor(new Color(50, 205, 50)); // Verde lima
                        g.fillRect(x + tile / 3, y + tile / 4, tile / 3, tile / 2);
                        // Brazos del cactus
                        g.fillRect(x + tile / 6, y + tile / 2 - 5, tile / 6, tile / 8);
                        g.fillRect(x + 2 * tile / 3, y + tile / 2 - 5, tile / 6, tile / 8);
                    }

                } else {
                    // Otras frutas (Grape, Banana, etc.) - usar render normal
                    f.render(g, tile);
                }

                // Indicador de congelado
                if (f.isFrozen()) {
                    g.setColor(new Color(100, 150, 255, 100));
                    g.fillRect(x, y, tile, tile);
                }
            }
        }

        // ===== DIBUJAR JUGADORES =====
        List<Player> players = level.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            int x = p.getPosition().getCol() * tile;
            int y = p.getPosition().getRow() * tile;

            // 🍦 JUGADOR - Cian brillante (helado)
            g.setColor(new Color(0, 255, 255)); // Cian
            g.fillOval(x + tile / 6, y + tile / 6, 2 * tile / 3, 2 * tile / 3);

            // Cara del helado
            g.setColor(Color.BLACK);
            // Ojos
            g.fillOval(x + tile / 3, y + tile / 3, tile / 8, tile / 8);
            g.fillOval(x + tile / 2, y + tile / 3, tile / 8, tile / 8);
            // Boca
            g.drawArc(x + tile / 3, y + tile / 2 - 5, tile / 3, tile / 4, 0, -180);

            // Brillo
            g.setColor(new Color(200, 255, 255));
            g.fillOval(x + tile / 4, y + tile / 4, tile / 6, tile / 6);
        }

        // ===== DIBUJAR ENEMIGOS =====
        for (Enemy e : level.getEnemies()) {
            int x = e.getPosition().getCol() * tile;
            int y = e.getPosition().getRow() * tile;

            if (e instanceof OrangeSquid) {
                // 🦑 CALAMAR NARANJA - Naranja brillante
                g.setColor(new Color(255, 140, 0)); // Naranja
                g.fillOval(x + tile / 6, y + tile / 6, 2 * tile / 3, 2 * tile / 3);

                // Tentáculos
                g.setColor(new Color(255, 165, 0));
                for (int i = 0; i < 3; i++) {
                    int tentX = x + tile / 4 + i * tile / 6;
                    g.fillRect(tentX, y + 2 * tile / 3, tile / 12, tile / 4);
                }

                // Ojos malvados
                g.setColor(Color.RED);
                g.fillOval(x + tile / 3, y + tile / 3, tile / 8, tile / 8);
                g.fillOval(x + tile / 2, y + tile / 3, tile / 8, tile / 8);

            } else if (e instanceof Troll) {
                // 👹 TROLL - Rojo oscuro
                g.setColor(new Color(178, 34, 34)); // Rojo ladrillo
                g.fillOval(x + tile / 6, y + tile / 6, 2 * tile / 3, 2 * tile / 3);

                // Cuernos
                g.setColor(new Color(139, 0, 0));
                int[] xHorn1 = {x + tile / 4, x + tile / 4 - tile / 8, x + tile / 4};
                int[] yHorn1 = {y + tile / 4, y + tile / 8, y + tile / 6};
                g.fillPolygon(xHorn1, yHorn1, 3);

                int[] xHorn2 = {x + 3 * tile / 4, x + 3 * tile / 4 + tile / 8, x + 3 * tile / 4};
                int[] yHorn2 = {y + tile / 4, y + tile / 8, y + tile / 6};
                g.fillPolygon(xHorn2, yHorn2, 3);

                // Ojos
                g.setColor(Color.YELLOW);
                g.fillOval(x + tile / 3, y + tile / 3, tile / 8, tile / 8);
                g.fillOval(x + tile / 2, y + tile / 3, tile / 8, tile / 8);

            } else if (e instanceof Maceta) {
                // 🪴 MACETA - Verde con marrón
                // Maceta (marrón)
                g.setColor(new Color(139, 69, 19));
                int[] xPot = {x + tile / 4, x + 3 * tile / 4, x + 2 * tile / 3, x + tile / 3};
                int[] yPot = {y + 2 * tile / 3, y + 2 * tile / 3, y + tile / 6, y + tile / 6};
                g.fillPolygon(xPot, yPot, 4);

                // Planta (verde)
                g.setColor(new Color(34, 139, 34));
                g.fillOval(x + tile / 3, y + tile / 6, tile / 3, tile / 2);

                // Ojos en la planta
                g.setColor(Color.WHITE);
                g.fillOval(x + tile / 3 + 5, y + tile / 3, tile / 10, tile / 10);
                g.fillOval(x + tile / 2, y + tile / 3, tile / 10, tile / 10);
                g.setColor(Color.BLACK);
                g.fillOval(x + tile / 3 + 7, y + tile / 3 + 2, tile / 15, tile / 15);
                g.fillOval(x + tile / 2 + 2, y + tile / 3 + 2, tile / 15, tile / 15);

            } else {
                // Otros enemigos - Rojo genérico
                g.setColor(new Color(255, 0, 0));
                g.fillOval(x + tile / 6, y + tile / 6, 2 * tile / 3, 2 * tile / 3);
            }
        }

        // ===== DIBUJAR HUD (puntuación y timer) =====

        if (!players.isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 18));

            // Score (izquierda)
            g.setColor(Color.WHITE);
            g.drawString("Score: " + players.get(0).getScore(), 10, 25);

            // Timer (derecha, al lado del score)
            int remainingTicks = TIME_LIMIT - timerTicks;
            int minutes = remainingTicks / 3600;
            int seconds = (remainingTicks % 3600) / 60;

            String timeText = String.format("Time: %d:%02d", minutes, seconds);

            // Cambiar color según el tiempo restante
            if (remainingTicks < 600) { // Menos de 10 segundos
                g.setColor(Color.RED);
            } else if (remainingTicks < 1800) { // Menos de 30 segundos
                g.setColor(Color.YELLOW);
            } else {
                g.setColor(Color.WHITE);
            }

            // Posicionar el timer al lado del score
            g.drawString(timeText, 200, 25); // Ajusta el valor 200 según necesites

            // Mensaje de "TIME UP!" si se acabó el tiempo
            if (timeUp) {

                g.setColor(new Color(0, 0, 0, 180)); // Fondo semi-transparente
                g.fillRect(0, 0, board.getCols() * tile, board.getRows() * tile);

                g.setColor(Color.RED);
                g.setFont(new Font("Arial", Font.BOLD, 48));
                String message = "TIME UP!";
                FontMetrics fm = g.getFontMetrics();
                int x = (board.getCols() * tile - fm.stringWidth(message)) / 2;
                int y = (board.getRows() * tile) / 2;
                g.drawString(message, x, y);

                g.setFont(new Font("Arial", Font.PLAIN, 24));
                String restart = "Restarting...";
                fm = g.getFontMetrics();
                x = (board.getCols() * tile - fm.stringWidth(restart)) / 2;
                g.drawString(restart, x, y + 40);
            }
        }

    }
    @Override
    public void keyPressed(Integer keyCode) {
        // Jugador 1 con flechas
        if (keyCode == KeyEvent.VK_UP)    p1Dir = Direction.UP;
        if (keyCode == KeyEvent.VK_DOWN)  p1Dir = Direction.DOWN;
        if (keyCode == KeyEvent.VK_LEFT)  p1Dir = Direction.LEFT;
        if (keyCode == KeyEvent.VK_RIGHT) p1Dir = Direction.RIGHT;
        if (keyCode == KeyEvent.VK_SPACE) placeOrBreakIce();

        // luego puedes añadir:
        // WASD para jugador 2, y mapear a otra Direction p2Dir
    }

    private void placeOrBreakIce() {
        Player p = level.getPlayers().get(0);
        Direction dir = p.getDirection();

        if (dir == null || dir == Direction.NONE) return;

        Board board = level.getBoard();
        Position start = p.getPosition();
        Position next = start.translated(dir.getDRow(), dir.getDCol());

        if (!board.isInside(next)) return;

        CellType firstCell = board.getCellType(next);

        // Si hay una hilera de hielo del jugador, primero descongelamos, luego rompemos
        if (firstCell == CellType.PLAYER_ICE || firstCell == CellType.ICE_BLOCK) {
            unfreezeFruitsInRay(level.getFruitManager().getAllFruits(), next, dir, board);
            breakIceRay(board, next, dir);
            return;
        }

        // Si no, creamos el rayo
        createIceRay(level, start, dir);
    }


    private void createIceRay(Level level, Position from, Direction dir) {
        Board board = level.getBoard();
        List<Fruit> allFruits = level.getFruitManager().getAllFruits();
        List<Enemy> enemies = level.getEnemies();

        Position current = from.translated(dir.getDRow(), dir.getDCol());

        while (board.isInside(current)) {
            boolean enemyThere = false;

            for (Enemy e : enemies) {
                if (sameCell(e.getPosition(), current)) {
                    enemyThere = true;
                    break;
                }
            }
            if (enemyThere) break;

            // NUEVO: Si hay una fogata, detener el rayo
            CellType cellType = board.getCellType(current);
            if (cellType == CellType.CAMPFIRE_ON || cellType == CellType.CAMPFIRE_OFF) {
                break;
            }

            // Si encontramos un obstáculo (pared, hielo, etc.), paramos
            if (CollisionDetector.isBlocked(board, current)) {
                break;
            }

            if (cellType == CellType.FLOOR) {
                board.setCellType(current, CellType.PLAYER_ICE);
            }

            // Si hay fruta en esa celda, la congelamos
            for (Fruit f : allFruits) {
                if (!f.isCollected() && !f.isFrozen() && sameCell(f.getPosition(), current)) {
                    f.freeze();
                }
            }

            current = current.translated(dir.getDRow(), dir.getDCol());
        }
    }
    private void checkAndExtinguishCampfire(Position pos, Level level) {
        for (Campfire campfire : level.getCampfires()) {
            if (campfire.getPosition().equals(pos)) {
                campfire.extinguish(level.getBoard());
                break;
            }
        }
    }

    private void breakIceRay(Board board, Position from, Direction dir) {
        Position current = from;
        Level level = this.level; // Ya tienes acceso al level

        while (board.isInside(current)) {
            CellType cell = board.getCellType(current);

            if (cell == CellType.PLAYER_ICE || cell == CellType.ICE_BLOCK) {
                // NUEVO: Verificar si hay una fogata en esta posición
                checkAndExtinguishCampfire(current, level);

                // Romper el hielo normalmente
                board.setCellType(current, CellType.FLOOR);
                current = current.translated(dir.getDRow(), dir.getDCol());
            } else {
                // Si ya no hay hielo, paramos
                break;
            }
        }
    }


    private void unfreezeFruitsInRay(List<Fruit> fruits, Position from, Direction dir, Board board) {

        Position current = from;

        while (board.isInside(current)) {
            CellType cell = board.getCellType(current);

            // Solo recorremos mientras haya hielo del jugador
            if (cell != CellType.PLAYER_ICE) break;

            for (Fruit f : fruits) {
                if (f.isFrozen() && sameCell(f.getPosition(), current)) {
                    f.unfreeze();
                }
            }

            current = current.translated(dir.getDRow(), dir.getDCol());
        }
    }




    @Override
    public void keyReleased(Integer keyCode) {
        // Si sueltas una tecla que corresponde a la dirección actual, paramos
        if ((keyCode == KeyEvent.VK_UP    && p1Dir == Direction.UP) ||
                (keyCode == KeyEvent.VK_DOWN  && p1Dir == Direction.DOWN) ||
                (keyCode == KeyEvent.VK_LEFT  && p1Dir == Direction.LEFT) ||
                (keyCode == KeyEvent.VK_RIGHT && p1Dir == Direction.RIGHT)) {
            p1Dir = Direction.NONE;
        }
    }

    public Level getLevel() {
        return level;
    }
}
