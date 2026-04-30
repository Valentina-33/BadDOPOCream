package domain.entities;

import domain.game.Flavour;
import domain.model.Position;
import domain.utils.Direction;

public class Player extends Entity {
    private int score = 0;
    private Direction direction = Direction.DOWN;
    private Direction nextDirection = Direction.NONE;

    private boolean dead = false;
    private int invulnerableTicks = 0;
    private static final int INVULNERABLE_TIME = 30;

    private Flavour currentFlavour = Flavour.VANILLA;

    // Estado de la animación de muerte (solo lógica numérica, sin imágenes)
    private int deathAnimationTicks = 0;
    private static final int DEATH_GIF_DURATION = 170;

    public Player(Position position) {
        super(position);
        // NOTA: El sprite del jugador se decide dinámicamente en el LevelRenderer
        // según el sabor (Flavour), por eso no lo asignamos aquí fijo.
        setFlavour(Flavour.VANILLA);
    }

    public void update() {
        if (dead && deathAnimationTicks < DEATH_GIF_DURATION) {
            deathAnimationTicks++;
        }
        if (invulnerableTicks > 0) {
            invulnerableTicks--;
        }
    }

    // --- GETTERS Y SETTERS ---

    public void setFlavour(Flavour flavour) {
        if (flavour != null) {
            this.currentFlavour = flavour;
        }
    }
    public Flavour getFlavour() { return currentFlavour; }

    public Direction getDirection() { return this.direction; }
    public void setDirection(Direction direction) { this.direction = direction; }

    public int getScore() { return score; }
    public void addScore(int pts) { this.score += pts; }
    public void setScore(int playerScore) { this.score = playerScore; }

    public boolean isDead() { return dead; }

    // Usado por el Renderer para saber si dibujar el GIF o la imagen final
    public boolean isDeathAnimationFinished() {
        return dead && deathAnimationTicks >= DEATH_GIF_DURATION;
    }

    public void onHitByEnemy(Entity e) {
        if (dead || invulnerableTicks > 0) return;

        dead = true;
        deathAnimationTicks = 0;
        invulnerableTicks = INVULNERABLE_TIME;
    }

    public void setNextDirection(Direction d) {
        this.nextDirection = d;
    }

    public Direction getNextDirection() {
        return this.nextDirection;
    }
}