package Trainer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Modellklasse für den Trainer. Sie lädt Vokabellisten aus JSON-Dateien
 * und stellt Methoden zum Zugriff auf Übersetzungen bereit.
 */

public class TrainerModel {
    private final Map<String, Map<String, String>> vocabData = new HashMap<>();
    private final Set<String> availableLanguages = new HashSet<>();

    /**
     * Öffnet die angegebene Vokabeldatei. Zunächst wird im Dateisystem gesucht,
     * anschließend in den eingebetteten Ressourcen.
     */
    private InputStream openVocabStream(String fileName) throws IOException {
        System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] 📖 Öffne Vokabelliste " + fileName);
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

    /**
     * Lädt die angegebenen Vokabeldaten und speichert sie intern.
     *
     * @param fileName Name der JSON-Datei
     */
    public void LoadJSONtoDataObj(String fileName) {
        System.out.println("[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] ⏳ Lade Daten aus " + fileName);
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

    /**
     * Liefert einen bestimmten Wert zu einer ID zurück.
     *
     * @param id    eindeutige Vokabel-ID
     * @param field gewünschtes Feld, z. B. Sprachcode
     * @return Inhalt des Feldes oder leerer String
     */
    public String get(String id, String field) {
        Map<String, String> entry = vocabData.get(id);
        if (entry == null) return "";
        return entry.getOrDefault(field, "");
    }

    /**
     * @return Menge aller geladenen Vokabel-IDs
     */
    public Set<String> getAllIds() {
        return vocabData.keySet();
    }

    /**
     * @return verfügbare Sprachcodes der aktuellen Liste
     */
    public Set<String> getAvailableLanguages() {
        return new HashSet<>(availableLanguages);
    }

    /**
     * Prüft, ob für eine ID im angegebenen Modus beide Übersetzungen vorhanden sind.
     */
    public boolean hasValidTranslation(String id, String mode) {
        String[] langPair = getLangPairForMode(mode);
        if (langPair == null) return false;
        Map<String, String> entry = vocabData.get(id);
        if (entry == null) return false;
        return entry.containsKey(langPair[0]) && entry.containsKey(langPair[1]);
    }

    /**
     * Ermittelt das Sprachpaar für den gewählten Modus.
     *
     * @param mode Bezeichner wie "Deutsch zu Englisch" oder "Zufällig"
     * @return Array mit Quell- und Zielsprache oder {@code null}
     */
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

    /**
     * Wandelt einen Sprachnamen in seinen Code um.
     */
    private String langCode(String name) {
        return LANG_NAME_TO_CODE.getOrDefault(name, name.toLowerCase());
    }
}
