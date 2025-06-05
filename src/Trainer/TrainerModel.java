package Trainer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Das Model lädt Vokabeln aus einer Datei mithilfe von Objektserialisierung.
 */
public class TrainerModel {
    private List<String> vocabEnglish;
    private List<String> vocabGerman;

    // 👉 Konstruktor ruft die Lademethode auf
    public TrainerModel(String fileName) {
        load(fileName);
    }

    // 👉 Diese Methode lädt die Daten aus der Datei
    @SuppressWarnings("unchecked")
    private void load(String fileName) {
        try (InputStream fileInputStream = Files.newInputStream(Paths.get(fileName));
             ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {

            vocabEnglish = (List<String>) objectInputStream.readObject();
            vocabGerman = (List<String>) objectInputStream.readObject();

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            vocabEnglish = List.of(); // leere Liste zur Sicherheit
            vocabGerman = List.of();
        }
    }

    public int getSize() {
        return vocabEnglish.size();
    }

    public String get(int index) {
        return vocabEnglish.get(index);
    }

    public String getTranslation(int index) {
        return vocabGerman.get(index);
    }
}
