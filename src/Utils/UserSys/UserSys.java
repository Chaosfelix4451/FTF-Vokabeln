package Utils.UserSys;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class UserSys {

    private static final List<User> users = new ArrayList<>();
    private static String currentUser = "user";

    public static void loadFromJson(Path path) {
        users.clear();
        try (InputStream in = Files.newInputStream(path)) {
            JSONObject root = new JSONObject(new JSONTokener(in));
            currentUser = root.optString("currentUser", "user");
            JSONArray userArray = root.getJSONArray("users");
            for (int i = 0; i < userArray.length(); i++) {
                JSONObject obj = userArray.getJSONObject(i);
                users.add(User.fromJson(obj));
            }
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Laden der JSON-Datei", e);
        }
    }

    public static void saveToJson(Path path) {
        JSONObject root = new JSONObject();
        root.put("currentUser", currentUser);
        JSONArray userArray = new JSONArray();
        for (User u : users) {
            userArray.put(u.toJson());
        }
        root.put("users", userArray);
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(root.toString(2));
        } catch (IOException e) {
            throw new RuntimeException("Fehler beim Speichern", e);
        }
    }

    public static void addUser(String name) {
        if (getUser(name) == null) users.add(new User(name));
    }

    public static void setCurrentUser(String name) {
        if (getUser(name) != null) currentUser = name;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static User getUser(String name) {
        return users.stream().filter(u -> u.name.equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static List<String> getAllUserNames() {
        List<String> list = new ArrayList<>();
        for (User u : users) list.add(u.name);
        return list;
    }

    public static class User {
        String name;
        int baseScore;
        int points;
        Map<String, VocabStats> statsPerList = new HashMap<>();

        public User(String name) {
            this.name = name;
            this.baseScore = 0;
            this.points = 0;
        }

        public static User fromJson(JSONObject obj) {
            User u = new User(obj.getString("name"));
            u.baseScore = obj.optInt("baseScore", 0);
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
            obj.put("baseScore", baseScore);
            obj.put("points", points);
            JSONObject stats = new JSONObject();
            for (Map.Entry<String, VocabStats> entry : statsPerList.entrySet()) {
                stats.put(entry.getKey(), entry.getValue().toJson());
            }
            obj.put("stats", stats);
            return obj;
        }
        public static boolean userExists(String name) {
            return getUser(name) != null;
        }

        public VocabStats getStats(String listId) {
            return statsPerList.computeIfAbsent(listId, k -> new VocabStats());
        }


    }

    public static class VocabStats {
        int total, correct, incorrect;
        int lastTotal, lastCorrect, lastIncorrect;

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
    }
}
