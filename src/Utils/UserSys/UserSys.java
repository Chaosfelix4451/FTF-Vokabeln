package Utils.UserSys;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Verwaltet Benutzer, Punkte und Einstellungen. Alle Daten werden in einer
 * JSON-Datei gesichert.
 */
public class UserSys {

    private static final List<User> users = new ArrayList<>();
    private static String currentUser = "user";
    private static final Map<String, Object> preferences = new HashMap<>();
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Prints a log line with timestamp for easier reading.
     */
    public static void log(String message) {
        String time = LocalTime.now().format(TIME_FMT);
        System.out.println("[" + time + "] " + message);
    }

    /**
     * Lädt alle Benutzerdaten und Einstellungen aus der JSON-Datei.
     */
    public static void loadFromJson() {
        Path path = Path.of("src", "Utils", "UserSys", "user.json"); // Dev-Pfad (zur Laufzeit ggf. anpassen)
        log("📥 Lade Benutzerdaten von " + path.toString());
        InputStream in = null;
        try {
            if (Files.exists(path)) {
                in = Files.newInputStream(path);
            } else {
                in = UserSys.class.getResourceAsStream("/Utils/UserSys/user.json");
                if (in == null) throw new FileNotFoundException("user.json nicht gefunden");
            }

            JSONObject root = new JSONObject(new JSONTokener(in));
            currentUser = root.optString("currentUser", "user");
            JSONObject prefsObj = root.optJSONObject("preferences");
            preferences.clear();
            if (prefsObj != null) {
                for (String key : prefsObj.keySet()) {
                    preferences.put(key, prefsObj.get(key));
                }
            }
            JSONArray userArray = root.getJSONArray("users");
            users.clear();
            for (int i = 0; i < userArray.length(); i++) {
                users.add(User.fromJson(userArray.getJSONObject(i)));
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Laden der JSON-Datei", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
        }
    }


    /**
     * Speichert aktuelle Benutzerdaten und Einstellungen in die JSON-Datei.
     */
    public static void saveToJson() {
        Path path = Path.of("src", "Utils", "UserSys", "user.json"); // Dev-Pfad (zur Laufzeit ggf. anpassen)
        log("💾 Speichere Benutzerdaten nach " + path.toString());
        JSONObject root = new JSONObject();
        root.put("currentUser", currentUser);
        JSONObject prefsObj = new JSONObject(preferences);
        root.put("preferences", prefsObj);
        JSONArray userArray = new JSONArray();
        for (User u : users) {
            userArray.put(u.toJson());
        }
        root.put("users", userArray);

        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path)) {
                Files.createFile(path);
            }
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                writer.write(root.toString(2));
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Speichern der JSON-Datei: " + path.toString(), e);
        }
    }

    /**
     * Legt einen neuen Benutzer an, sofern der Name noch nicht vergeben ist.
     */
    public static void createUser(String name) {
        log("👤 Neuer Benutzer: " + name);
        if (getUser(name) == null) {
            users.add(new User(name));
        }
    }

    // backwards compatibility
    public static void addUser(String name) {
        createUser(name);
    }

    /**
     * Sucht nach Benutzernamen, die den Suchbegriff enthalten.
     */
    public static List<String> searchUsers(String query) {
        log("🔍 Suche nach Benutzern mit '" + query + "'");
        if (query == null || query.isBlank()) {
            return getAllUserNames();
        }
        List<String> result = new ArrayList<>();
        result.clear();
        for (String name : getAllUserNames()) {
            if (name.toLowerCase().contains(query.toLowerCase())) {
                result.add(name);
            }
        }
        return result;
    }

    /**
     * Löscht einen Benutzer. Der Spezialname "@all_admin_1234" entfernt alle Benutzer.
     */
    public static void deleteUser(String name) {
        log("❌ Lösche Benutzer " + name);
        if ("@all_admin_1234".equals(name)) {
            users.clear();
            currentUser = "user";
            return;
        }
        users.removeIf(u -> u.getName().equalsIgnoreCase(name));
        if (currentUser.equalsIgnoreCase(name)) {
            currentUser = users.isEmpty() ? "user" : users.get(0).getName();
        }
    }

    public static void setCurrentUser(String name) {
        log("➡️ Aktiver Benutzer: " + name);
        if (getUser(name) != null) currentUser = name;
    }
    public static void resetCurrentUser() {currentUser =""; };
    public static String getCurrentUser() {
        return currentUser;
    }

    public static String getPreference(String key, String def) {
        Object val = preferences.get(key);
        return val != null ? val.toString() : def;
    }

    public static boolean getBooleanPreference(String key, boolean def) {
        Object val = preferences.get(key);
        if (val instanceof Boolean b) return b;
        if (val instanceof String s) return Boolean.parseBoolean(s);
        return def;
    }

    public static void setPreference(String key, String value) {
        log("⚙️ Setze Einstellung " + key + "=" + value);
        preferences.put(key, value);
    }

    public static void setBooleanPreference(String key, boolean value) {
        log("⚙️ Setze Einstellung " + key + "=" + value);
        preferences.put(key, value);
    }

    public static User getUser(String name) {
        return users.stream()
                .filter(u -> u.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public static List<String> getAllUserNames() {
        List<String> list = new ArrayList<>();
        for (User u : users) {
            list.add(u.getName());
        }
        return list;
    }

    public static boolean userExists(String name) {
        return getUser(name) != null;
    }

    public static Set<String> getAllListIds(String userName) {
        User u = getUser(userName);
        return (u != null) ? u.getListIds() : Collections.emptySet();
    }

    /**
     * Get the score for a user. If name is "@all" a map of all user scores is returned.
     */
    public static Map<String, Integer> getScore(String name) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if ("@all".equals(name)) {
            for (User u : users) {
                result.put(u.getName(), u.getPoints());
            }
            return result;
        }
        User u = getUser(name);
        if (u != null) {
            result.put(u.getName(), u.getPoints());
        }
        return result;
    }

    /**
     * Setzt den Punktestand eines Benutzers. Wird "@all" übergeben, betrifft es alle.
     */
    public static void setScore(String name, int points) {
        points = Math.max(0, points);
        if ("@all".equals(name)) {
            for (User u : users) {
                u.setPoints(points);
            }
            return;
        }
        User u = getUser(name);
        if (u != null) {
            u.setPoints(points);
        }
    }

    public static class User {
        private String name;
        private int points;
        private final Map<String, VocabStats> statsPerList = new HashMap<>();

        private User(String name) {
            this.name = name;
            this.points = 0;
        }

        public static User fromJson(JSONObject obj) {
            User u = new User(obj.getString("name"));
            u.points = obj.optInt("points", 0);
            JSONObject stats = obj.optJSONObject("stats");
            if (stats != null) {
                for (String listId : stats.keySet()) {
                    u.statsPerList.put(listId, VocabStats.fromJson(stats.getJSONObject(listId)));
                }
            }
            return u;
        }

        public JSONObject toJson() {
            JSONObject obj = new JSONObject();
            obj.put("name", name);
            obj.put("points", points);
            JSONObject stats = new JSONObject();
            for (Map.Entry<String, VocabStats> entry : statsPerList.entrySet()) {
                stats.put(entry.getKey(), entry.getValue().toJson());
            }
            obj.put("stats", stats);
            return obj;
        }
        public String getName() {
            return name;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = Math.max(0, points);
        }

        public void addPoints(int amount) {
            if (amount > 0) {
                this.points += amount;
            }
        }

        public Set<String> getListIds() {
            return statsPerList.keySet();
        }

        public VocabStats getStats(String listId) {
            return statsPerList.computeIfAbsent(listId, k -> new VocabStats());
        }


    }

    public static class VocabStats {
        private int total, correct, incorrect;
        private int lastTotal, lastCorrect, lastIncorrect;

        public void record(boolean isCorrect) {
            total++;
            if (isCorrect) correct++;
            else incorrect++;
        }

        public void startNewSession() {
            lastTotal = total;
            lastCorrect = correct;
            lastIncorrect = incorrect;
            total = correct = incorrect = 0;
        }

        public JSONObject toJson() {
            JSONObject obj = new JSONObject();
            obj.put("total", total);
            obj.put("correct", correct);
            obj.put("incorrect", incorrect);
            obj.put("lastTotal", lastTotal);
            obj.put("lastCorrect", lastCorrect);
            obj.put("lastIncorrect", lastIncorrect);
            return obj;
        }

        public static VocabStats fromJson(JSONObject obj) {
            VocabStats vs = new VocabStats();
            vs.total = obj.optInt("total", 0);
            vs.correct = obj.optInt("correct", 0);
            vs.incorrect = obj.optInt("incorrect", 0);
            vs.lastTotal = obj.optInt("lastTotal", 0);
            vs.lastCorrect = obj.optInt("lastCorrect", 0);
            vs.lastIncorrect = obj.optInt("lastIncorrect", 0);
            return vs;
        }

        public int getTotal() {
            return total;
        }

        public int getCorrect() {
            return correct;
        }

        public int getIncorrect() {
            return incorrect;
        }

        public int getLastTotal() {
            return lastTotal;
        }

        public int getLastCorrect() {
            return lastCorrect;
        }

        public int getLastIncorrect() {
            return lastIncorrect;
        }
    }
}
