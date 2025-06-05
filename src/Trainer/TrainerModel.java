package Trainer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Das Model hält eine feste Liste von Vokabeln.
 * Diese kann über Getter abgefragt werden.
 */
public class TrainerModel {
    private final List<String> vocabEnglish = new ArrayList<>(Arrays.asList(
            "apple", "banana", "house", "car", "computer",
            "book", "pen", "school", "teacher", "student",
            "chair", "table", "window", "door", "phone",
            "dog", "cat", "mouse", "bird", "fish",
            "water", "milk", "bread", "cheese", "butter",
            "sun", "moon", "star", "sky", "cloud"
    ));

    private final List<String> vocabGerman = new ArrayList<>(Arrays.asList(
            "Apfel", "Banane", "Haus", "Auto", "Computer",
            "Buch", "Stift", "Schule", "Lehrer", "Schüler",
            "Stuhl", "Tisch", "Fenster", "Tür", "Handy",
            "Hund", "Katze", "Maus", "Vogel", "Fisch",
            "Wasser", "Milch", "Brot", "Käse", "Butter",
            "Sonne", "Mond", "Stern", "Himmel", "Wolke"
    ));

    private final List<Integer> shuffledIndices = new ArrayList<>();

    public TrainerModel() {
        for (int i = 0; i < vocabEnglish.size(); i++) {
            shuffledIndices.add(i);
        }
        Collections.shuffle(shuffledIndices); // einmalige zufällige Reihenfolge
    }

    public int getSize() {
        return shuffledIndices.size();
    }

    public String get(int index) {
        return vocabEnglish.get(shuffledIndices.get(index));
    }

    public String getTranslation(int index) {
        return vocabGerman.get(shuffledIndices.get(index));
    }

}


