package domain.entities;

import domain.model.Position;
import domain.utils.Direction;

import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

/**
 * Player con manejo de sprites vanilla, tiene puntaje
 */
public class Player extends Entity {
    private int score = 0;
    private Direction direction = Direction.DOWN;

    private static final AnimatedSprite PLAYER_SPRITE;

    static {
        Map<Direction, String> playerSprites = new EnumMap<>(Direction.class);
        playerSprites.put(Direction.UP, "/vanilla-up.gif");
        playerSprites.put(Direction.DOWN, "/vanilla-down.gif");
        playerSprites.put(Direction.LEFT, "/vanilla-left.gif");
        playerSprites.put(Direction.RIGHT, "/vanilla-right.gif");

        PLAYER_SPRITE = new AnimatedSprite(playerSprites);
    }

    public Player(Position position) {
        super(position);
        setAnimatedSprite(PLAYER_SPRITE);
    }

    public void render(Graphics2D g, int tileSize) {
        if (animatedSprite != null) {
            int x = position.getCol() * tileSize;
            int y = position.getRow() * tileSize;
            animatedSprite.draw(g, x, y, tileSize, tileSize, direction);
        }
    }

    public Direction getDirection() { return this.direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public int getScore() { return score; }
    public void addScore(int pts) { this.score += pts; }
    public void setScore(int playerScore) { this.score = playerScore; }

    // Maneja la muerte del jugador
    public void onHitByEnemy(Enemy e) { }
}
