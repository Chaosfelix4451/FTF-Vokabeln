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
 * Einträge aus einer JSON-Datei geladen.
 */

public class TrainerModel {
    private final Map<String, Map<String, String>> vocabData = new HashMap<>();
    private final Set<String> availableLanguages = new HashSet<>();

    /**
     * Open the vocabulary file either from the filesystem (development) or
     * bundled resources when running from a JAR.
     */
    private InputStream openVocabStream(String fileName) throws IOException {
        System.out.println("TrainerModel: opening vocab file " + fileName);
        // Try direct path first
        Path path = Path.of(fileName);
        if (!Files.exists(path)) {
            path = Path.of("src", "Trainer", "Vocabsets", fileName);
        }
        if (Files.exists(path)) {
            return Files.newInputStream(path);
        }

        InputStream in = TrainerModel.class.getResourceAsStream("/Trainer/Vocabsets/" + fileName);
        if (in != null) {
            return in;
        }
        throw new IOException("Vokabelliste nicht gefunden: " + fileName);
    }

    public void LoadJSONtoDataObj(String fileName) {
        System.out.println("TrainerModel: loading data from " + fileName);
        vocabData.clear();
        availableLanguages.clear();
        try (InputStream in = openVocabStream(fileName)) {
            JSONArray array = new JSONArray(new JSONTokener(in));
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String id = obj.getString("id");
                String difficulty = obj.getString("difficulty");
                JSONObject translations = obj.getJSONObject("translations");

                Map<String, String> data = new HashMap<>();
                for (String lang : translations.keySet()) {
                    String value = translations.getString(lang).trim();
                    if (!value.isEmpty()) {
                        data.put(lang, value);
                        availableLanguages.add(lang);
                    }
                }
                data.put("difficulty", difficulty);
                vocabData.put(id, data);
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Laden der Vokabelliste", e);
        }
    }

    public String get(String id, String field) {
        Map<String, String> entry = vocabData.get(id);
        if (entry == null) return "";
        return entry.getOrDefault(field, "");
    }

    public Set<String> getAllIds() {
        return vocabData.keySet();
    }

    public Set<String> getAvailableLanguages() {
        return new HashSet<>(availableLanguages);
    }

    public boolean hasValidTranslation(String id, String mode) {
        String[] langPair = getLangPairForMode(mode);
        if (langPair == null) return false;
        Map<String, String> entry = vocabData.get(id);
        if (entry == null) return false;
        return entry.containsKey(langPair[0]) && entry.containsKey(langPair[1]);
    }

    public String[] getLangPairForMode(String mode) {
        if (mode == null) return null;
        if ("Zufällig".equalsIgnoreCase(mode)) {
            List<String> langs = new ArrayList<>(availableLanguages);
            if (langs.size() < 2) return null;
            Collections.shuffle(langs);
            String from = langs.get(0);
            String to = langs.stream().filter(l -> !l.equals(from)).findFirst().orElse(null);
            if (to == null) return null;
            return new String[]{from, to};
        }

        // Format: "Deutsch zu Englisch"
        String[] parts = mode.split(" zu ");
        if (parts.length != 2) return null;

        String from = langCode(parts[0].trim());
        String to = langCode(parts[1].trim());

        if (from == null || to == null || from.equals(to)) return null;
        return new String[]{from, to};
    }

    private static final Map<String, String> LANG_NAME_TO_CODE = Map.of(
            "Deutsch", "de",
            "Englisch", "en",
            "Französisch", "fr",
            "Spanisch", "es"
    );

    private String langCode(String name) {
        return LANG_NAME_TO_CODE.getOrDefault(name, name.toLowerCase());
    }
}
