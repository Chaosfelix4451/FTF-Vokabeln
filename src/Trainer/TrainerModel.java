package Trainer;

import java.util.List;

/**
 * Das Model hält eine feste Liste von Vokabeln.
 * Diese kann über Getter abgefragt werden.
 */
public class TrainerModel {

    // Beispiel-Vokabelliste (20 Begriffe)
    private final List<String> vocabelList = List.of(
            "apple", "banana", "cherry", "date", "eggplant",
            "fig", "grape", "honeydew", "iceberg", "jackfruit",
            "kiwi", "lemon", "mango", "nectarine", "orange",
            "peach", "quince", "raspberry", "strawberry", "tomato"
    );

    /**
     * Gibt die gesamte Vokabelliste zurück.
     * @return Liste aller Vokabeln
     */
    public List<String> getVocabelList() {
        return vocabelList;
    }

    /**
     * Gibt die Größe der Vokabelliste zurück.
     * @return Anzahl der Vokabeln
     */
    public int getSize() {
        return vocabelList.size();
    }

    /**
     * Gibt die Vokabel an der angegebenen Position zurück.
     * @param index Position in der Liste
     * @return Vokabel als String
     */
    public String get(int index) {
        return vocabelList.get(index);
    }
}
