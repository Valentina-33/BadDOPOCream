package domain.entities;

import domain.game.Flavour;
import domain.model.Position;
import domain.utils.Direction;

import javax.swing.*;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class Player extends Entity {
    private int score = 0;
    private Direction direction = Direction.DOWN;

    private boolean dead = false;
    private int invulnerableTicks = 0;
    private static final int INVULNERABLE_TIME = 30;

    private Flavour currentFlavour = Flavour.VANILLA;
    private Image deathGif = null;
    private Image deathLastFrame = null;
    private int deathAnimationTicks = 0;
    private static final int DEATH_GIF_DURATION = 170;

    private static final AnimatedSprite VANILLA_SPRITE;
    private static final AnimatedSprite STRAWBERRY_SPRITE;
    private static final AnimatedSprite CHOCOLATE_SPRITE;

    static {
        VANILLA_SPRITE = buildSprite("vanilla");
        STRAWBERRY_SPRITE = buildSprite("strawberry");
        CHOCOLATE_SPRITE = buildSprite("chocolate");
    }

    private static AnimatedSprite buildSprite(String prefix) {
        Map<Direction, String> sprites = new EnumMap<>(Direction.class);
        sprites.put(Direction.UP, "/" + prefix + "-up.gif");
        sprites.put(Direction.DOWN, "/" + prefix + "-down.gif");
        sprites.put(Direction.LEFT, "/" + prefix + "-left.gif");
        sprites.put(Direction.RIGHT, "/" + prefix + "-right.gif");
        return new AnimatedSprite(sprites);
    }

    public Player(Position position) {
        super(position);
        setFlavour(Flavour.VANILLA);
    }

    public void setFlavour(Flavour flavour) {
        if (flavour == null) return;

        this.currentFlavour = flavour;

        if (flavour == Flavour.STRAWBERRY) setAnimatedSprite(STRAWBERRY_SPRITE);
        else if (flavour == Flavour.CHOCOLATE) setAnimatedSprite(CHOCOLATE_SPRITE);
        else setAnimatedSprite(VANILLA_SPRITE);
    }

    private void loadDeathAnimation() {
        try {
            String gifName = "";
            String lastFrameName = "";

            if (currentFlavour == Flavour.VANILLA) {
                gifName = "/vanilla-death.gif";
                lastFrameName = "/vanilla-dead.png";
            } else if (currentFlavour == Flavour.STRAWBERRY) {
                gifName = "/strawberry-death.gif";
                lastFrameName = "/strawberry-dead.png";
            } else if (currentFlavour == Flavour.CHOCOLATE) {
                gifName = "/chocolate-death.gif";
                lastFrameName = "/chocolate-dead.png";
            }

            deathGif = new ImageIcon(Objects.requireNonNull(getClass().getResource(gifName))).getImage();
            deathLastFrame = new ImageIcon(Objects.requireNonNull(getClass().getResource(lastFrameName))).getImage();

        } catch (Exception e) {
            System.err.println("Error cargando animación de muerte: " + e.getMessage());
        }
    }

    public void update() {
        // Actualizar el contador de la animación de muerte
        if (dead && deathAnimationTicks < DEATH_GIF_DURATION) {
            deathAnimationTicks++;
        }
    }

    public void render(Graphics2D g, int tileSize) {
        int x = position.getCol() * tileSize;
        int y = position.getRow() * tileSize;

        if (dead) {
            // Si la animación del GIF ya terminó, mostrar el último frame estático
            if (deathAnimationTicks >= DEATH_GIF_DURATION && deathLastFrame != null) {
                g.drawImage(deathLastFrame, x, y, tileSize, tileSize, null);
            }
            // Si aún no termina, mostrar el GIF
            else if (deathGif != null) {
                g.drawImage(deathGif, x, y, tileSize, tileSize, null);
            }
        } else if (animatedSprite != null) {
            // Mostrar sprite normal
            animatedSprite.draw(g, x, y, tileSize, tileSize, direction);
        }
    }

    public Direction getDirection() { return this.direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public int getScore() { return score; }
    public void addScore(int pts) { this.score += pts; }
    public void setScore(int playerScore) { this.score = playerScore; }

    public boolean isDead() { return dead; }

    /**
     * Retorna true cuando la animación de muerte ha terminado completamente
     * Coloca la imagen del helado derretido
     */
    public boolean isDeathAnimationFinished() {
        return dead && deathAnimationTicks >= DEATH_GIF_DURATION;
    }

    public void onHitByEnemy(Entity e) {
        if (dead) return;
        if (invulnerableTicks > 0) return;

        dead = true;
        deathAnimationTicks = 0;
        loadDeathAnimation();
        invulnerableTicks = INVULNERABLE_TIME;
    }
}