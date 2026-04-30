package domain.entities;

import domain.game.Level;
import domain.model.Position;
import java.util.List;

public class Cactus extends Fruit {
    public static final int CACTUS_SCORE = 250;
    private static final int TICKS_PER_CYCLE = 600;
    private int tickCounter = 0;
    private boolean hasSpikesDangerous = false;

    public Cactus(Position position) {
        // Pasamos null como sprite porque el LevelRenderer decide cuál pintar
        // según si es peligroso o no.
        super(position, CACTUS_SCORE, null);
    }

    @Override
    public int getPoints() { return CACTUS_SCORE; }

    public boolean getHasSpikesDangerous() { return hasSpikesDangerous; }

    @Override
    public void update(Level level) {
        if (isCollected() || isFrozen()) return;

        tickCounter++;
        if (tickCounter >= TICKS_PER_CYCLE) {
            hasSpikesDangerous = !hasSpikesDangerous;
            tickCounter = 0;
        }

        if (hasSpikesDangerous) {
            checkPlayerCollision(level);
        }
    }

    private void checkPlayerCollision(Level level) {
        List<Player> players = level.getPlayers();
        for (Player player : players) {
            if (player.getPosition().equals(this.position)) {
                player.onHitByEnemy(null);
            }
        }
    }

    @Override
    public void collect() {
        if (!hasSpikesDangerous && !isCollected()) {
            super.collect();
        }
    }

    @Override
    public void onPlayerCollision(Player p) {
        if (isCollected() || isFrozen()) return;
        if (hasSpikesDangerous) {
            p.onHitByEnemy(this);
        } else {
            super.onPlayerCollision(p); // Se comporta como fruta normal (puntos)
        }
    }

    public boolean isDangerous() {
        return hasSpikesDangerous && !isCollected();
    }
}