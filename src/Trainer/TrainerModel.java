package Trainer;

import com.github.openjson.JSONArray;
import com.github.openjson.JSONObject;
import com.github.openjson.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Verwaltet die Vokabellisten des Trainers. Beim Erzeugen werden die
 * Einträge aus einer JSON-Datei geladen, standardmäßig aus
 * {@code src/Trainer/Vocabsets/defaultvocab.json}.
 */
public class TrainerModel {
    private final List<String> vocabEnglish = new ArrayList<>();
    private final List<String> vocabGerman = new ArrayList<>();

    public TrainerModel() {
        this("src/Trainer/Vocabsets/defaultvocab.json");
    }

    public TrainerModel(String path) {
        loadFromJson(path);
    }

    /**
     * Lädt die Vokabeln aus der angegebenen JSON-Datei. Erwartet ein Array aus
     * Objekten mit dem Aufbau:
     * {@code {"translations": {"en": "...", "de": "..."}}}
     * Fehlschläge führen lediglich zu einer leeren Liste und werden auf der
     * Konsole gemeldet.
     */
    public void loadFromJson(String path) {
        vocabEnglish.clear();
        vocabGerman.clear();
        Path p = Path.of(path);
        try (InputStream in = Files.newInputStream(p)) {
            JSONTokener tokener = new JSONTokener(new java.io.InputStreamReader(in, java.nio.charset.StandardCharsets.UTF_8));
            JSONArray arr = new JSONArray(tokener);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (!obj.has("translations")) continue;
                JSONObject trans = obj.getJSONObject("translations");
                vocabEnglish.add(trans.optString("en"));
                vocabGerman.add(trans.optString("de"));
            }
        } catch (IOException e) {
            System.err.println("Konnte Vokabeldatei nicht lesen: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ungültiges JSON in " + path + ": " + e.getMessage());
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
