package ConnectTrainer;

import Trainer.TrainerModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model for the ConnectTrainer. It loads vocabulary pairs and provides
 * random pairs for the controller.
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

    /** Load vocabulary file and prepare language pair according to mode. */
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
     * Return up to {@code count} random pairs from the loaded vocabulary list.
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

    public String[] getLangPair() {
        return langPair.clone();
    }
}
