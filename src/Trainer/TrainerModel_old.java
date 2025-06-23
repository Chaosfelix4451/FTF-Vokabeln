package Trainer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Verwaltet die Vokabellisten des Trainers. Beim Erzeugen werden die
 * Einträge aus einer JSON-Datei geladen, standardmäßig aus
 * {@code src/Trainer/Vocabsets/defaultvocab.json}.
 */
public class TrainerModel_old {
    private final List<String> englishList = new ArrayList<>();
    private final List<String> germanList = new ArrayList<>();
    private final List<String> spanishList = new ArrayList<>();
    private final List<String> frenchList = new ArrayList<>();

    public TrainerModel_old() {}
    public void loadJsonfile(String fileName) {
        englishList.clear();
        germanList.clear();
        spanishList.clear();
        frenchList.clear();
        try (InputStream in = Files.newInputStream(Path.of(fileName))) {
            JSONArray array = new JSONArray(new JSONTokener(in));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                JSONObject translations = obj.optJSONObject("translations");
                String english = translations.optString("en", "").trim();
                String german = translations.optString("de", "").trim();
                String spanish = translations.optString("es", "").trim();
                String french = translations.optString("fr", "").trim();
                if (!(english.isEmpty() && german.isEmpty() && spanish.isEmpty() && french.isEmpty())) {
                    englishList.add(english);
                    germanList.add(german);
                    spanishList.add(spanish);
                    frenchList.add(french);
                } else return;
            }
        } catch (IOException e) {
            System.err.println("Dateifehler: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("JSON-Fehler: " + e.getMessage());
        }
    }
    public int getSize() {
        return englishList.size();
    }

    public String getEnglish(int index) {
        return (index >= 0 && index < englishList.size()) ? englishList.get(index) : "";
    }

    public String getGerman(int index) {
        return (index >= 0 && index < germanList.size()) ? germanList.get(index) : "";
    }
    public String getSpanish(int index) {
        return (index >= 0 && index < spanishList.size()) ? spanishList.get(index) : "";
    }
    public String getfrench(int index) {
        return (index >= 0 && index < frenchList.size()) ? frenchList.get(index) : "";
    }

    // Map<ID, Map<"en"/"de"/"difficulty", String>>
    private final Map<String, Map<String, String>> vocabData = new HashMap<>();
    public void LoadJsondara(String fileName){
        vocabData.clear();
        try(InputStream in = Files.newInputStream(Path.of(fileName))){
            JSONArray array = new JSONArray(new JSONTokener(in));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String id = obj.getString("id");
                String difficulty = obj.getString("difficulty");
                JSONObject translations = obj.getJSONObject("translations");
                Map<String, String> data = new HashMap<>();
                for (String lang : translations.keySet()) {
                    data.put(lang, translations.getString(lang).trim());
                }
                data.put("difficulty", difficulty);
                vocabData.put(id, data);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
