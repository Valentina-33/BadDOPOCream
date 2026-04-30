package domain.game;

import domain.ai.*;
import domain.entities.Player;
import domain.utils.Direction;

/**
 * Controlador de IA refactorizado usando el Patrón Strategy.
 * Delega la lógica de decisión a una implementación de AIMovementStrategy.
 */
public class AIController {

    private final AIProfile profile;
    private final AIMovementStrategy strategy;

    public AIController(AIProfile profile) {
        this.profile = profile;

        // Simple Factory: inicializa la estrategia correcta según el perfil
        this.strategy = switch (profile) {
            case HUNGRY -> new HungryStrategy();
            case FEARFUL -> new FearfulStrategy();
            case EXPERT -> new ExpertStrategy();
        };
    }

    public AIProfile getProfile() {
        return profile;
    }

    public Direction decide(Level level, Player me) {
        if (me == null || me.isDead()) return Direction.NONE;

        // Delegación pura
        return strategy.decideNextMove(level, me);
    }
}