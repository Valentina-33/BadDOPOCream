package domain.game;

import domain.entities.*;
import java.util.List;

public class LevelFactory {

    public static Level createLevel(int levelNumber) {
        return switch (levelNumber) {
            case 1 -> createLevel1();
            case 2 -> createLevel2();
            // puedes ir añadiendo más niveles aquí
            default -> createLevel1(); // fallback
        };
    }

    private static Level createLevel1() {
        //Mapa del nivel 1
        String path = "/maps/level1.txt";

        //Orden de frutas para el nivel 1:
        //primero Bananas, luego Grapes
        List<Class<? extends Fruit>> phases = List.of(
                Banana.class,
                Grape.class
        );

        return LevelLoader.loadFromResource(path, phases);
    }

    private static Level createLevel2() {
        String path = "/maps/level2.txt";

        // Ejemplo: primero Grapes, luego Pineapples
        List<Class<? extends Fruit>> phases = List.of(
                Banana.class,
                Pineapple.class
        );

        return LevelLoader.loadFromResource(path, phases);
    }
}

