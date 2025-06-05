package ScoreBoard;

/**
 * Datenklasse für einen Eintrag in der Highscore-Tabelle.
 */
public class UserRow {
    private final String name;
    private final int points;

    public UserRow(String name, int points) {
        this.name = name;
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public int getPoints() {
        return points;
    }
}
