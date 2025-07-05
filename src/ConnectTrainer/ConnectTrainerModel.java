package ConnectTrainer;

import Trainer.TrainerModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Datenmodell des ConnectTrainers. Es lädt Vokabelpaare und stellt
 * zufällige Kombinationen für den Controller bereit.
 */
public class ConnectTrainerModel {

    public static class VocabPair {
        public final String left;
        public final String right;
        public VocabPair(String left, String right) {
            this.left = left;
            this.right = right;
        }
    }

    private final TrainerModel trainerModel = new TrainerModel();
    private final List<String> ids = new ArrayList<>();
    private String[] langPair = {"de", "en"};

    /**
     * Lädt die gewünschte Vokabelliste und bereitet das Sprachpaar entsprechend
     * dem gewählten Modus vor.
     */
    public void loadData(String vocabFile, String mode) {
        trainerModel.LoadJSONtoDataObj(vocabFile);
        String[] pair = trainerModel.getLangPairForMode(mode);
        if (pair != null) {
            langPair = pair;
        }
        ids.clear();
        ids.addAll(trainerModel.getAllIds());
        Collections.shuffle(ids);
    }

    /**
     * Gibt bis zu {@code count} zufällige Paare aus der geladenen Liste zurück.
     */
    public List<VocabPair> getRandomPairs(int count) {
        List<VocabPair> result = new ArrayList<>();
        for (int i = 0; i < count && i < ids.size(); i++) {
            String id = ids.get(i);
            String left = trainerModel.get(id, langPair[0]);
            String right = trainerModel.get(id, langPair[1]);
            result.add(new VocabPair(left, right));
        }
        return result;
    }

    /**
     * Liefert das aktuell verwendete Sprachpaar.
     *
     * @return Array mit Quell- und Zielsprache
     */
    public String[] getLangPair() {
        return langPair.clone();
    }
}
