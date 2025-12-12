package domain.game;

import domain.entities.*;
import java.util.List;

/**
 * LevelFactory crea los niveles de acuerdo con su ENUM de mapas y listas de frutas.
 */
public class LevelFactory {

    public static Level createLevel(int levelNumber) {
        return switch (levelNumber) {
            case 2 -> createLevel2();
            case 3 -> createLevel3();
            default -> createLevel1();
        };
    }

    private static Level createLevel1() {
        String path = "/maps/level1.txt";

        // Orden de aparición de frutas: bananos, luego uvas
        List<Class<? extends Fruit>> phases = List.of(
                Banana.class,
                Grape.class
        );

        return LevelLoader.loadFromResource(path, phases);
    }

    private static Level createLevel2() {
        String path = "/maps/level2.txt";

        List<Class<? extends Fruit>> phases = List.of(
                Banana.class,
                Pineapple.class
        );
        return LevelLoader.loadFromResource(path, phases);
    }

    private static Level createLevel3() {
        String path = "/maps/level3.txt";

        List<Class<? extends Fruit>> phases = List.of(
                Cactus.class,
                Cherry.class
        );
        return LevelLoader.loadFromResource(path, phases);
    }
}

