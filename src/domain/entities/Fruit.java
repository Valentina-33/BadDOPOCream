package domain.entities;

import domain.game.Level;
import domain.model.Position;
import presentation.SpriteFactory;
import java.awt.Graphics2D;

public abstract class Fruit extends Entity {
    protected final Sprite sprite; // Sprite estático, se asignará desde fuera
    private final int scoreValue;
    private boolean collected = false;
    private boolean frozen = false;

    // Constructor ahora recibe el Sprite a usar
    public Fruit(Position position, int scoreValue, Sprite sprite) {
        super(position);
        this.sprite = sprite; // Sprite asignado aquí
        this.scoreValue = scoreValue;
    }

    // Getters y Setters para el estado de la fruta
    public boolean isCollected() { return this.collected; }
    public void collect() { this.collected = true; }

    public boolean isFrozen() { return this.frozen; }
    public void freeze() { this.frozen = true; }
    public void unfreeze() { this.frozen = false; }

    // Lógica de actualización específica de cada fruta (abstracta)
    public abstract void update(Level level);
    public abstract int getPoints();

    // Renderizado genérico usando el sprite asignado
    public void render(Graphics2D g, int tileSize) {
        int baseX = position.getCol() * tileSize;
        int baseY = position.getRow() * tileSize;

        // Ajuste visual para que la fruta no toque los bordes de la celda
        int size = (int) (tileSize * 0.8);
        int offset = (tileSize - size) / 2;

        int x = baseX + offset;
        int y = baseY + offset;

        if (sprite != null) {
            sprite.draw(g, x, y, size, size);
        }
    }

    /**
     * Define qué pasa cuando un jugador toca esta fruta.
     * Aplicamos polimorfismo para evitar instanceof.
     */
    public void onPlayerCollision(Player p) {
        // Comportamiento por defecto: La fruta es comida
        if (!isCollected() && !isFrozen()) {
            this.collect();
            p.addScore(this.getPoints());
        }
    }
}