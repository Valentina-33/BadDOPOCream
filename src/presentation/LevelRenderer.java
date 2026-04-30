package presentation;

import domain.entities.*;
import domain.model.Board;
import domain.model.CellType;
import domain.game.Level;
import domain.model.Position;

import java.awt.*;
import java.util.List;

/**
 * Clase encargada exclusivamente de dibujar el estado actual del juego.
 * Sigue el patrón MVC (es la Vista), separando la lógica visual del dominio.
 * Utiliza SpriteFactory para obtener los recursos gráficos.
 */
public class LevelRenderer {

    //  CONSTANTES DE UI (Para que PlayingState sepa dónde hacer click) 
    public static final int BTN_SIZE = 17; // Tamaño del botón (ajústalo a tu imagen)
    public static final int MARGIN_TOP = 10;
    public static final int MARGIN_RIGHT = 10;
    public static final int GAP = 2; // Espacio entre botones

    // Coordenada X del botón PAUSA (El más a la derecha)
    public static final int BTN_PAUSE_X = GamePanel.WIDTH - MARGIN_RIGHT - BTN_SIZE;
    public static final int BTN_Y = MARGIN_TOP;

    // Coordenada X del botón REINICIAR (A la izquierda del de pausa)
    public static final int BTN_RESTART_X = BTN_PAUSE_X - GAP - BTN_SIZE;


    /**
     * Método principal de dibujo. Se llama en cada frame desde PlayingState.
     */
    public void render(Graphics2D g, Level level, int timerTicks, int timeLimit, boolean timeUp) {
        if (level == null) return;

        Board board = level.getBoard();
        int tile = GamePanel.TILE_SIZE;

        // 1. Fondo base (limpieza)
        g.setColor(new Color(240, 248, 255));
        g.fillRect(0, 0, board.getCols() * tile, board.getRows() * tile);

        // 2. BUCLE DE PROFUNDIDAD
        for (int r = 0; r < board.getRows(); r++) {

            // A. Primero dibujamos todo el suelo de esta fila
            for (int c = 0; c < board.getCols(); c++) {
                Position p = new Position(r, c);
                CellType cell = board.getCellType(p);

                // Siempre dibujamos el piso base
                if (cell == CellType.IGLOO_AREA) {
                    drawCell(g, CellType.FLOOR, c * tile, r * tile, tile);

                    if (isIglooBottomRight(board, r, c)) {
                        int xOffset = (c - 3) * tile;
                        int yOffset = (r - 3) * tile;

                        // NOTA: Asegúrate de dibujar con el tamaño completo (4x4 tiles)
                        drawBigIgloo(g, xOffset, yOffset, tile);
                    }
                } else {
                    drawCell(g, cell, c * tile, r * tile, tile);
                }
            }

            // B. Dibujar entidades
            drawEntitiesAtRow(g, level, r, tile);
        }

        // 3. HUD
        drawHUD(g, level.getPlayers(), timerTicks, timeLimit, timeUp, board.getCols() * tile, board.getRows() * tile);
        drawUIButtons(g);
    }

    // Método auxiliar corregido: Detecta la esquina INFERIOR izquierda
    // Método auxiliar: Detecta la esquina INFERIOR DERECHA
    private boolean isIglooBottomRight(Board board, int r, int c) {

        boolean currentIsIgloo = board.getCellType(new Position(r, c)) == CellType.IGLOO_AREA;
        if (!currentIsIgloo) return false;

        boolean bottomIsEdgeOrNotIgloo = (r + 1 >= board.getRows()) ||
                board.getCellType(new Position(r + 1, c)) != CellType.IGLOO_AREA;

        boolean rightIsEdgeOrNotIgloo = (c + 1 >= board.getCols()) ||
                board.getCellType(new Position(r, c + 1)) != CellType.IGLOO_AREA;

        return bottomIsEdgeOrNotIgloo && rightIsEdgeOrNotIgloo;
    }

    // Helper para detectar la esquina del Iglú
    private boolean isIglooTopLeft(Board board, int r, int c) {
        boolean topIsIgloo = (r > 0) && board.getCellType(new Position(r - 1, c)) == CellType.IGLOO_AREA;
        boolean leftIsIgloo = (c > 0) && board.getCellType(new Position(r, c - 1)) == CellType.IGLOO_AREA;
        return !topIsIgloo && !leftIsIgloo;
    }

    // Helper para dibujar la imagen grande
    private void drawBigIgloo(Graphics2D g, int x, int y, int tileSize) {
        Sprite igloo = SpriteFactory.getStaticSprite("/igloo.jpg");
        if (igloo != null) {
            // Dibuja un cuadrado de 4x4 tiles
            igloo.draw(g, x, y, tileSize * 4, tileSize * 4);
        }
    }

    // Helper para dibujar entidades en una fila específica
    private void drawEntitiesAtRow(Graphics2D g, Level level, int row, int tile) {
        // Frutas
        for (Fruit f : level.getFruitManager().getActiveFruits()) {
            if (!f.isCollected() && f.getPosition().getRow() == row) {
                // ... (Copia aquí tu lógica de dibujo de fruta: if cactus, etc) ...
                // Para ahorrar espacio aquí resumo, pero pega tu bloque 'if/else' de frutas
                if (f instanceof Cactus cactus) {
                    Sprite s = SpriteFactory.getCactusSprite(cactus.isDangerous());
                    if (s != null) s.draw(g, f.getPosition().getCol() * tile, row * tile, tile, tile);
                } else {
                    f.render(g, tile);
                }
                if (f.isFrozen()) {
                    g.setColor(new Color(100, 150, 255, 100));
                    g.fillRect(f.getPosition().getCol() * tile, row * tile, tile, tile);
                }
            }
        }

        // Fogatas
        for (Campfire c : level.getCampfires()) {
            if (c.getPosition().getRow() == row) {
                Sprite s = c.getCurrentSprite();
                if (s != null) s.draw(g, c.getPosition().getCol() * tile, row * tile, tile, tile);
            }
        }

        // Jugadores
        for (Player p : level.getPlayers()) {
            if (p.getPosition().getRow() == row) {
                drawPlayer(g, p, tile);
            }
        }

        // Enemigos
        for (Enemy e : level.getEnemies()) {
            if (e.getPosition().getRow() == row) {
                e.render(g, tile);
            }
        }
    }

    private void drawUIButtons(Graphics2D g) {
        Sprite restart = SpriteFactory.getRestartButtonSprite();
        Sprite pause = SpriteFactory.getPauseButtonSprite();

        if (restart != null) {
            restart.draw(g, BTN_RESTART_X, BTN_Y, BTN_SIZE, BTN_SIZE);
        }

        if (pause != null) {
            pause.draw(g, BTN_PAUSE_X, BTN_Y, BTN_SIZE, BTN_SIZE);
        }
    }

    /**
     * Dibuja una celda del tablero solicitando el sprite correspondiente a la fábrica.
     */
    private void drawCell(Graphics2D g, CellType cell, int x, int y, int tile) {
        Sprite sprite = null;

        switch (cell) {
            case METALLIC_WALL -> sprite = SpriteFactory.getStaticSprite("/wall.jpg");
            case RED_WALL      -> sprite = SpriteFactory.getStaticSprite("/red-wall.jpg");
            case YELLOW_WALL   -> sprite = SpriteFactory.getStaticSprite("/yellow-wall.jpg");
            case ICE_BLOCK     -> sprite = SpriteFactory.getStaticSprite("/ice.jpg");
            case PILE_SNOW     -> sprite = SpriteFactory.getStaticSprite("/pile-of-snow.jpg");
            case IGLOO_AREA    -> sprite = SpriteFactory.getStaticSprite("/igloo.jpg");
            case HOT_TILE      -> sprite = SpriteFactory.getStaticSprite("/hot-tile.png");
            case PLAYER_ICE    -> sprite = SpriteFactory.getStaticSprite("/player-ice.png");
            default            -> sprite = SpriteFactory.getStaticSprite("/empty.jpg"); // FLOOR
        }

        if (sprite != null) {
            sprite.draw(g, x, y, tile, tile);
        }
    }

    /**
     * Maneja la lógica de dibujo del jugador:
     * - Si está vivo: Sprite animado según sabor y dirección.
     * - Si está muriendo: GIF de muerte.
     * - Si murió: Imagen estática final.
     */
    private void drawPlayer(Graphics2D g, Player p, int tileSize) {
        int x = p.getPosition().getCol() * tileSize;
        int y = p.getPosition().getRow() * tileSize;

        if (p.isDead()) {
            if (p.isDeathAnimationFinished()) {
                // Animación terminada: Frame estático
                Image deadImg = SpriteFactory.getDeathLastFrame(p.getFlavour());
                if (deadImg != null) g.drawImage(deadImg, x, y, tileSize, tileSize, null);
            } else {
                // Muriendo: GIF animado
                Image deathGif = SpriteFactory.getDeathGif(p.getFlavour());
                if (deathGif != null) g.drawImage(deathGif, x, y, tileSize, tileSize, null);
            }
        } else {
            // Vivo: Sprite animado normal
            AnimatedSprite sprite = SpriteFactory.getPlayerSprite(p.getFlavour());
            if (sprite != null) {
                sprite.draw(g, x, y, tileSize, tileSize, p.getDirection());
            }
        }
    }

    /**
     * Dibuja la información de puntaje, tiempo y mensajes de fin de juego.
     */
    private void drawHUD(Graphics2D g, List<Player> players, int timerTicks, int timeLimit, boolean timeUp, int screenW, int screenH) {
        if (!players.isEmpty()) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(Color.WHITE);

            // Puntajes
            g.drawString("P1: " + players.get(0).getScore(), 10, 25);
            if (players.size() > 1) {
                g.drawString("P2: " + players.get(1).getScore(), 10, 50);
            }

            // Tiempo
            int remainingTicks = timeLimit - timerTicks;
            int minutes = remainingTicks / 3600;
            int seconds = (remainingTicks % 3600) / 60;
            String timeText = String.format("Time: %d:%02d", minutes, seconds);

            // Cambiar color del tiempo según urgencia
            if (remainingTicks < 600) g.setColor(Color.RED);        // < 10 seg
            else if (remainingTicks < 1800) g.setColor(Color.YELLOW); // < 30 seg
            else g.setColor(Color.WHITE);

            g.drawString(timeText, 200, 25);
        }

        // Mensaje de Tiempo Agotado
        if (timeUp) {
            g.setColor(new Color(0, 0, 0, 180)); // Fondo oscuro semitransparente
            g.fillRect(0, 0, screenW, screenH);

            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            String message = "TIME UP!";
            FontMetrics fm = g.getFontMetrics();
            int x = (screenW - fm.stringWidth(message)) / 2;
            int y = screenH / 2;
            g.drawString(message, x, y);

            g.setFont(new Font("Arial", Font.PLAIN, 24));
            String restart = "Restarting...";
            fm = g.getFontMetrics();
            x = (screenW - fm.stringWidth(restart)) / 2;
            g.drawString(restart, x, y + 40);
        }
    }
}