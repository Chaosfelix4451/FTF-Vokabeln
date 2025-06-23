package Utils.UserScore;

import java.io.*;
import java.util.*;


public final class UserSystem {

    private UserSystem() { /* nur statische Nutzung */ }

    private static String currentUser = "user";

    // Standardliste, falls keine Liste spezifiziert wurde
    private static final String DEFAULT_LIST = "default";


    /**
     * Eine Vokabelstatistik zu einer bestimmten Liste.
     */
    private static class VocabListEntry {
        String listId;
        VocabStats stats;

        VocabListEntry(String listId, VocabStats stats) {
            this.listId = listId;
            this.stats = stats;
        }
    }

    /**
     * Ein Benutzer mit Punktestand und individuellen Statistiken.
     */
    private static class User {
        String name;
        int points;
        List<VocabListEntry> vocabStatsPerList = new ArrayList<>();

        public User(String name) {
            this.name = (name == null || name.trim().isEmpty()) ? "user" : name.trim();
            this.points = 0;
        }

        // Serialisiert Benutzer inkl. Statistiken
        private String toCSV() {
            String csv = name + "," + points;
            for (VocabListEntry entry : vocabStatsPerList) {
                csv += ",LIST:" + entry.listId + "," + entry.stats.toCSV();
            }
            return csv;
        }

        // Deserialisiert Benutzer aus CSV-Zeile
        private static User fromCSV(String line) {
            String[] parts = line.strip().split(",");
            if (parts.length < 2) return null;

            try {
                User u = new User(parts[0].trim());
                u.points = Integer.parseInt(parts[1].trim());
                for (int i = 2; i < parts.length - 1; i++) {
                    if (parts[i].startsWith("LIST:")) {
                        String listId = parts[i].substring(5);
                        VocabStats stats = VocabStats.fromCSV(parts[++i]);
                        if (stats != null) u.vocabStatsPerList.add(new VocabListEntry(listId, stats));
                    }
                }
                return u;
            } catch (NumberFormatException e) {
                System.err.println("Ungültige Daten in Zeile: " + line);
                return null;
            }
        }

        // Gibt die Statistik für eine bestimmte Liste zurück (oder erzeugt neue)
        public VocabStats getStats(String listId) {
            String id = (listId == null || listId.isBlank()) ? DEFAULT_LIST : listId;
            for (VocabListEntry entry : vocabStatsPerList) {
                if (entry.listId.equals(id)) {
                    return entry.stats;
                }
            }
            VocabStats newStats = new VocabStats();
            vocabStatsPerList.add(new VocabListEntry(id, newStats));
            return newStats;
        }
    }

    /**
     * Vokabelstatistik mit Verlauf für Vergleich zum Vorlauf.
     */
    private static class VocabStats {
        int total, correct, incorrect;
        int lastTotal, lastCorrect, lastIncorrect;

        void startNewSession() {
            lastTotal = total;
            lastCorrect = correct;
            lastIncorrect = incorrect;
            total = correct = incorrect = 0;
        }

        void record(boolean correctAnswer) {
            total++;
            if (correctAnswer) correct++;
            else incorrect++;
        }

        int getDiffCorrect() { return correct - lastCorrect; }
        int getDiffIncorrect() { return incorrect - lastIncorrect; }

        String toCSV() {
            return total + ":" + correct + ":" + incorrect + ":" +
                    lastTotal + ":" + lastCorrect + ":" + lastIncorrect;
        }


        static VocabStats fromCSV(String csv) {
            try {
                String[] parts = csv.split(":");
                if (parts.length != 6) return null;
                VocabStats vs = new VocabStats();
                vs.total = Integer.parseInt(parts[0]);
                vs.correct = Integer.parseInt(parts[1]);
                vs.incorrect = Integer.parseInt(parts[2]);
                vs.lastTotal = Integer.parseInt(parts[3]);
                vs.lastCorrect = Integer.parseInt(parts[4]);
                vs.lastIncorrect = Integer.parseInt(parts[5]);
                return vs;
            } catch (Exception e) {
                return null;
            }
        }
    }

    // Liste aller Benutzer
    private static final List<User> users = new ArrayList<>();

    // Benutzerverwaltung
    public static void addUser(String name) {
        if (getUserByName(name) == null) users.add(new User(name));
    }

    public static void removeUser(String name) {
        users.removeIf(u -> u.name.equalsIgnoreCase(name));
    }

    public static void clearAllUsers() {
        users.clear();
    }

    public static int getUserCount() {
        return users.size();
    }

    private static User getUserByName(String name) {
        for (User u : users) {
            if (u.name.equalsIgnoreCase(name)) return u;
        }
        return null;
    }

    public static boolean userExists(String name) {
        return getUserByName(name) != null;
    }

    private static User getUserByIndex(int index) {
        return (index >= 0 && index < users.size()) ? users.get(index) : null;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(String name) {
        if (userExists(name)) {
            currentUser = name;
        }
    }

    // Punktestandverwaltung
    public static void addPoint(String name) {
        addPoints(name, 1);
    }

    /**
     * Adds the given amount of points to the user. Negative values are ignored.
     */
    public static void addPoints(String name, int amount) {
        if (amount <= 0) return;
        User u = getUserByName(name);
        if (u != null) u.points += amount;
    }

    public static void setPoints(String name, int points) {
        User u = getUserByName(name);
        if (u != null) u.points = Math.max(0, points);
    }

    public static void resetPoints(String name) {
        setPoints(name, 0);
    }

    public static String getName(int index) {
        User u = getUserByIndex(index);
        return (u != null) ? u.name : null;
    }

    public static int getPoints(String name) {
        User u = getUserByName(name);
        return (u != null) ? u.points : -1;
    }

    // Datenzugriff auf alle Benutzerwerte
    public static List<String> getAllUserNames() {
        List<String> names = new ArrayList<>();
        for (User u : users) names.add(u.name);
        return names;
    }

    public static List<Integer> getAllScores() {
        List<Integer> scores = new ArrayList<>();
        for (User u : users) scores.add(u.points);
        return scores;
    }

    public static int getDiffCorrect(String name, String listId) {
        VocabStats stats = getStatsForUser(name, listId);
        return stats != null ? stats.getDiffCorrect() : 0;
    }

    public static int getDiffIncorrect(String name, String listId) {
        VocabStats stats = getStatsForUser(name, listId);
        return stats != null ? stats.getDiffIncorrect() : 0;
    }

    public static int getTotalCorrect(String name, String listId) {
        VocabStats stats = getStatsForUser(name, listId);
        return stats != null ? stats.correct : 0;
    }

    public static int getTotalIncorrect(String name, String listId) {
        VocabStats stats = getStatsForUser(name, listId);
        return stats != null ? stats.incorrect : 0;
    }

    // Sortierung und Highscore
    public static void sortByScoreDescending() {
        users.sort((a, b) -> Integer.compare(b.points, a.points));
    }

    public static int getHighscore() {
        int high = 0;
        for (User u : users) {
            if (u.points > high) high = u.points;
        }
        return high;
    }

    public static List<String> getTopUsers() {
        int high = getHighscore();
        List<String> top = new ArrayList<>();
        for (User u : users) {
            if (u.points == high) top.add(u.name);
        }
        return top;
    }

    // Datei-Speicherung der Benutzerdaten
    public static void saveToFile() {
        File file = new File("src/Utils/UserScore/user_data.csv");
        file.getParentFile().mkdirs();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (User u : users) writer.write(u.toCSV() + "\n");
        } catch (IOException e) {
            System.err.println("Fehler beim Speichern: " + e.getMessage());
        }
    }

    // Datei-Laden der Benutzerdaten
    public static void loadFromFile() {
        File file = new File("src/Utils/UserScore/user_data.csv");
        if (!file.exists()) return;

        users.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                User u = User.fromCSV(line);
                if (u != null) users.add(u);
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Laden: " + e.getMessage());
        }
    }

    // Vokabel-Statistiken verwalten
    public static void recordAnswer(String name, boolean correct, String listId) {
        if (name == null) return;
        var stats = getStatsForUser(name, listId);
        if (stats != null) stats.record(correct);
    }

    public static void startNewSession(String name, String listId) {
        var stats = getStatsForUser(name, listId);
        if (stats != null) stats.startNewSession();
    }

    public static VocabStats getStatsForUser(String name, String listId) {
        User u = getUserByName(name);
        return (u != null) ? u.getStats(listId) : null;
    }

    public static List<String> getAllListIds(String name) {
        User u = getUserByName(name);
        List<String> ids = new ArrayList<>();
        if (u != null) {
            for (VocabListEntry entry : u.vocabStatsPerList) {
                ids.add(entry.listId);
            }
        }
        return ids;
    }
}
