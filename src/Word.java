import java.util.ArrayList;

public class Word implements Comparable{
    private String word;
    private String word_en;
    private String type;
    private ArrayList<String> singular;
    private ArrayList<String > plural;
    private ArrayList<Definitions> definitions;

    public String getWord() {
        return word;
    }

    public ArrayList<Definitions> getDefinitions() {
        return definitions;
    }

    public String getWord_en() {
        return word_en;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getSingular() {
        return singular;
    }

    public ArrayList<String> getPlural() {
        return plural;
    }

    public Word() {
        this.word = word;
    }

    public void setWord_en(String word_en) {
        this.word_en = word_en;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDefinitions(ArrayList<Definitions> definitions) {
        this.definitions = definitions;
    }

    public Word(String word, String word_en, String type, ArrayList<String> singular, ArrayList<String> plural, ArrayList<Definitions> definitions) {
        this.word = word;
        this.word_en = word_en;
        this.type = type;
        this.singular = singular;
        this.plural = plural;
        this.definitions = definitions;
    }

    @Override
    public int compareTo(Object o) {
        Word comparestu = (Word)o;
        String comparage = ((Word)comparestu).getWord();
        return this.getWord().compareTo(comparage);
    }
}
