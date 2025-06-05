package Utils.Inhalt;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class VocabularySerializer {
    public static void main(String[] args) {
        // Vokabeln vorbereiten
        List<String> english = Arrays.asList(
                "apple", "banana", "house", "car", "computer",
                "book", "pen", "school", "teacher", "student",
                "chair", "table", "window", "door", "phone",
                "dog", "cat", "mouse", "bird", "fish",
                "water", "milk", "bread", "cheese", "butter",
                "sun", "moon", "star", "sky", "cloud"
        );

        List<String> german = Arrays.asList(
                "Apfel", "Banane", "Haus", "Auto", "Computer",
                "Buch", "Stift", "Schule", "Lehrer", "Schüler",
                "Stuhl", "Tisch", "Fenster", "Tür", "Handy",
                "Hund", "Katze", "Maus", "Vogel", "Fisch",
                "Wasser", "Milch", "Brot", "Käse", "Butter",
                "Sonne", "Mond", "Stern", "Himmel", "Wolke"
        );

        // Datei speichern
        try (OutputStream fileOut = Files.newOutputStream(Paths.get("/src/Utils/Inhalt/vokabeln.ser"));
             ObjectOutputStream objectOut = new ObjectOutputStream(fileOut)) {

            objectOut.writeObject(english);
            objectOut.writeObject(german);
            System.out.println("Datei 'vokabeln.ser' erfolgreich erstellt.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
