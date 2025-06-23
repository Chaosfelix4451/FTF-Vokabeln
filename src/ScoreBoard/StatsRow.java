package ScoreBoard;

/**
 * Simple row model for vocabulary statistics of one list.
 */
public class StatsRow {
    private final String list;
    private final int correct;
    private final int incorrect;

    public StatsRow(String list, int correct, int incorrect) {
        this.list = list;
        this.correct = correct;
        this.incorrect = incorrect;
    }

    public String getList() { return list; }
    public int getCorrect() { return correct; }
    public int getIncorrect() { return incorrect; }
}
