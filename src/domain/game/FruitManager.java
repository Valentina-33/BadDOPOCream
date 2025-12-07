package domain.game;

import domain.entities.Fruit;

import java.util.ArrayList;
import java.util.List;

public class FruitManager {

    private final List<Fruit> allFruits;
    private final List<Fruit> activeFruits = new ArrayList<>();

    public FruitManager(List<Fruit> allFruits) {
        this.allFruits = allFruits != null ? allFruits : new ArrayList<>();
    }

    public List<Fruit> getAllFruits() {
        return allFruits;
    }

    public List<Fruit> getActiveFruits() {
        return activeFruits;
    }

    // Activa todas las frutas
    public void activateAll() {
        activeFruits.clear();
        activeFruits.addAll(allFruits);
    }

    // Activa solo las frutas de una clase concreta (Banana, Grape, etc.)
    public void activateByClass(Class<? extends Fruit> clazz) {
        activeFruits.clear();
        for (Fruit f : allFruits) {
            if (clazz.isInstance(f)) {
                activeFruits.add(f);
            }
        }
    }

    // ¿Ya se comieron todas las frutas activas?
    public boolean allActiveCollected() {
        for (Fruit f : activeFruits) {
            if (!f.isCollected()) return false;
        }
        return true;
    }
}
