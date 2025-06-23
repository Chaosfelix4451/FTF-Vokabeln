package Trainer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Verwaltet die Vokabellisten des Trainers. Beim Erzeugen werden die
 * Einträge aus einer JSON-Datei geladen, standardmäßig aus
 * {@code src/Trainer/Vocabsets/defaultvocab.json}.
 */
public class TrainerModel {
    private final List<String> englishList = new ArrayList<>();
    private final List<String> germanList = new ArrayList<>();
    private final List<String> spanishList = new ArrayList<>();
    private final List<String> frenchList = new ArrayList<>();

    public TrainerModel() {}
    // Map<ID, Map<"en"/"de"/"difficulty", String>>
    private final Map<String, Map<String, String>> vocabData = new HashMap<>();
    public void LoadJSONtoDataObj(String fileName){
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

    public String get(String id,String field){
        Map<String, String> entry = vocabData.get(id);
        if(entry == null) return "";
        return entry.get(field);
    }
    public int getSize(){
        return (vocabData.size());
    }
    public Set<String> getAllIds() {
        return vocabData.keySet();
    }

}
