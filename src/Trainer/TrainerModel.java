package Trainer;

import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
//program for reading a JSON file

import org.json.JSONArray;

import org.json.JSONObject;





/**
 * Das Model hält eine feste Liste von Vokabeln.
 * Diese kann über Getter abgefragt werden.
 */

public class TrainerModel {
    private final List<String> vocabEnglish = Arrays.asList(
            "apple", "banana", "house", "car", "computer",
            "book", "pen", "school", "teacher", "student",
            "chair", "table", "window", "door", "phone",
            "dog", "cat", "mouse", "bird", "fish",
            "water", "milk", "bread", "cheese", "butter",
            "sun", "moon", "star", "sky", "cloud"
    );

    private final List<String> vocabGerman = Arrays.asList(
            "Apfel", "Banane", "Haus", "Auto", "Computer",
            "Buch", "Stift", "Schule", "Lehrer", "Schüler",
            "Stuhl", "Tisch", "Fenster", "Tür", "Handy",
            "Hund", "Katze", "Maus", "Vogel", "Fisch",
            "Wasser", "Milch", "Brot", "Käse", "Butter",
            "Sonne", "Mond", "Stern", "Himmel", "Wolke"
    );

        public int getSize() {
            return vocabEnglish.size();
        }

        public String get(int index) {
            return vocabEnglish.get(index);
        }

        public String getTranslation(int index) {
            return vocabGerman.get(index);
        }
}

public class JSON

{

    public static void main(Strings args[])

    {

        // file name is File.json

        Object o = new JSONParser().parse(new FileReader(File.json));

        JSONObject j = (JSONObject) o;

        String Name = (String) j.get(“Name”);

        String College = (String ) j.get(“College”);



        System.out.println(“Name :” + Name);

        System.out.println(“College :” +College);

    }

}