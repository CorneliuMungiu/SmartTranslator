import java.util.ArrayList;

public class Definitions implements Comparable{
    private String dict;
    private String dictType;
    private int year;
    private ArrayList<String> text;

    public String getDictType() {
        return dictType;
    }

    public String getDict() {
        return dict;
    }

    public int getYear() {
        return year;
    }

    public ArrayList<String> getText() {
        return text;
    }

    public Definitions(String dict, String dictType, int year, ArrayList<String> text) {
        this.dict = dict;
        this.dictType = dictType;
        this.year = year;
        this.text = text;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setText(ArrayList<String> text) {
        this.text = text;
    }

    /**
     * Sortare crescatoare alfabetic.
     * @param o Obiectul cu care se compara.
     * @return Returneaza diferenta dintre this.year si o.year .
     */
    @Override
    public int compareTo(Object o) {
        Definitions comparestu = (Definitions)o;
        int comparage = ((Definitions)comparestu).getYear();
        return this.year-comparage;
    }
}
