import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Dictionaries {
    private Map<String, ArrayList<Word>> dictionaries;

    public Map<String, ArrayList<Word>> getDictionaries() {
        return dictionaries;
    }

    public Dictionaries() {
        this.dictionaries = new HashMap<>();
    }

    /**
     * @param word Cuvantul care trebuie adaugat.
     * @param language Limba in care este cuvantul "word".
     * @return Intoarce true daca s-a adaugat cuvantul in dictionar sau false daca exista cuvantul în dictionar
     */
    public boolean addWord(Word word, String language){
        //Daca limba nu exista o creeaza
        this.getDictionaries().computeIfAbsent(language, k -> new ArrayList<>());

        ArrayList<Word> words = this.getDictionaries().get(language);
        /*check va fi false daca cuvantul exista deja in dictionar si are aceiasi parametri,
        in cazul in care cuvantul era deja in dictionar insa unii parametri se difera sau au fost adaugati
        atunci metoda va returna true. De asemenea va returna true daca cuvantul nu era in dictionar.
         */
        boolean check = false;
        for(Word e : words){
            if(e.getWord().equals(word.getWord())){
                //Face update cuvantului in caz ca acesta trebuie updatat
                check = updateWord(e,word);
                return check;
            }
        }
        //Adauga cuvantul in dictionar in caz ca acesta nu era deja acolo
        words.add(word);
        this.getDictionaries().put(language,words);
        return true;
    }

    /**
     * @param word Cuvantul care trebuie sters.
     * @param language Limba care apartine cuvantului "word".
     * @return Intoarce true daca s-a sters cuvantul in dictionar sau false daca nu exista cuvantul în dictionar
     */
    public boolean removeWord(String word, String language){
        //Verifica daca exista limba din care vreau sa sterg cuvantul
        if (this.getDictionaries().get(language) == null)
            return false;

        ArrayList<Word> words = this.getDictionaries().get(language);
        for(int i = 0; i < words.size(); i++)
            if (words.get(i).getWord().equals(word)) {
                //Daca am gasit cuvantul atunci il sterg
                words.remove(i);
                return true;
            }

        return false;
    }

    /**
     * @param word Cuvantul caruia i se adauga definitia.
     * @param language Limba din care apartine cuvantul "word".
     * @param definitions Definitia care trebuie adaugata.
     * @return Intoarce true daca s-a adaugat definitia (sau a fost updatata) sau false daca exista o definitie din acelasi dictionar.
     */
    public boolean addDefinitionForWord(String word, String language, Definitions definitions){
        //Verifica daca exista limba in care vreau sa adaug definitia
        if(this.getDictionaries().get(language) == null)
            return false;

        ArrayList<Word> words = this.getDictionaries().get(language);
        //updated va fi true daca definita a fost updatata si false daca aceasta nu a fost updatata
        boolean updated = false;
        //Parcurg lista de cuvinte din limba language
        for(int i = 0; i < words.size(); i++){
            //Daca am gasit cuvantul
            if(words.get(i).getWord().equals(word)) {
                //Parcurg toate definitiile lui
                for (int j = 0; j < words.get(i).getDefinitions().size(); j++) {
                    //Daca am gasit o definitie cu acelasi nume
                    if (words.get(i).getDefinitions().get(j).getDict().equals(definitions.getDict())) {
                        //Verific daca anul din definitia noua este diferit de anul din definitia veche
                        if (!(words.get(i).getDefinitions().get(j).getYear() == definitions.getYear())) {
                            //Daca anii sunt diferiti atunci anul devine anul din definitia noua si updated devine true
                            words.get(i).getDefinitions().get(j).setYear(definitions.getYear());
                            updated = true;
                        }
                        //Parcurg fiecare text pentru a vedea daca nu a aparut un nou text
                        for (String s : definitions.getText()) {
                            //Daca noua definitie are un nou text atunci il adaug
                            if (!words.get(i).getDefinitions().get(j).getText().contains(s)) {
                                words.get(i).getDefinitions().get(j).getText().add(s);
                                updated = true;
                            }
                        }
                        /*Daca updated nu a devenit true(nu a fost schimbat nici campul year si nici text) inseamna ca
                          definitia nu a fost updatata si returnez true intrucat nu a fost modificat nimic
                        */
                        if (!updated)
                            return false;
                    }
                }
                //Daca nu a fost gasit o definitie cu acelasi nume ca definitia noua atunci o adaug si returnez true
                if (!updated) {
                    words.get(i).getDefinitions().add(definitions);
                }
                return true;
            }
        }
        return false;

    }

    /**
     * @param word Cuvantul din care trebuie stearsa definitia.
     * @param language Limba din care apartine cuvantul "word".
     * @param dictionary Definitia.
     * @return Intoarce true daca s-a sters definitia sau false daca nu exista o definitie din dictionarul primit ca parametru.
     */
    public boolean removeDefinition(String word, String language, String dictionary){
        //Verifica daca exista limba din care vreau sa sterg definitia
        if(this.getDictionaries().get(language) == null)
            return false;

        ArrayList<Word> words = this.getDictionaries().get(language);
        //Parcurg lista de cuvinte
        for(Word e : words){
            //Daca am gasit cuvantul
            if(e.getWord().equals(word)) {
                //Parcurg toate definitiile acelui cuvant
                for (int i = 0; i < e.getDefinitions().size(); i++) {
                    //Daca am gasit definitia atunci o sters si returnez true
                    if (e.getDefinitions().get(i).getDict().equals(dictionary)) {
                        e.getDefinitions().remove(i);
                        return true;
                    }
                }
                //Definitia nu a fost gasita
                return false;
            }
        }

        return false;
    }

    /**
     *
     * @param word Cuvantul care trebuie tradus.
     * @param fromLanguage Limba din care apartine cuvantul "word".
     * @param toLanguage Limba in care va fi tradus cuvantul "word".
     * @return Intoarce traducerea cuvantului word din limba fromLanguage în limba toLanguage (chiar daca se apeleaza
     * cu un cuvant la plural, un cuvant din sinonime sau din singular).
     */
    public String translateWord(String word, String fromLanguage, String toLanguage){
        //Verific daca exista atat limba din care vreau sa traduc cat si limba in care vreau sa traduc
        if(this.getDictionaries().get(fromLanguage) == null || this.getDictionaries().get(toLanguage) == null )
            return "Una din limbi nu este specificata corect";

        ArrayList<Word> fromLanguageWords = this.getDictionaries().get(fromLanguage);
        ArrayList<Word> toLanguageWords = this.getDictionaries().get(toLanguage);
        //Parcurg toate cuvintele din limba din care vreau sa traduc
        for(Word fromLanguageWord : fromLanguageWords){
            //Check va fi true daca cuvantul a fost gasit
            boolean check = false;
            //Singular va fi true daca cuvantul va fi singular si false daca acesta va fi plural
            boolean singular = false;
            //Parcurg lista de singular a cuvantului
            for(String s : fromLanguageWord.getSingular()){
                //Daca cuvantul este gasit in singular
                if(s.equals(word)){
                    check = true;
                    singular = true;
                }
            }
            //Parcurg lista de plural a cuvantului
            for(String s : fromLanguageWord.getPlural()){
                //Daca cuvantul este gasit in plural(nu am mai scris singular = false intrucat acesta initial ramane false
                if(s.equals(word))
                    check = true;
            }
            //Parcurg lista de definition pentru a cauta dictionare de sinonime
            for(int i = 0; i < fromLanguageWord.getDefinitions().size(); i++){
                //Daca gasesc dictionar de sinonime
                if (fromLanguageWord.getDefinitions().get(i).getDictType().equals("synonyms"))
                    //Parcurg text din dictionarul de sinonime
                    for(String w : fromLanguageWord.getDefinitions().get(i).getText()){
                        //Daca am gasit cuvantul in dictionarul de sinonime
                        //Presupun ca toate cuvintele din dictionarul de sinonime sunt la singular
                        if(w.equals(word)){
                            check = true;
                            singular = true;
                        }
                    }
            }
            //Daca cuvantul a fost gasit in oricare din cazurile de mai sus
            if (check){
                //Parcurg lista de cuvinte din limba in care trebuie sa traduc
                for(Word toLanguageWord : toLanguageWords){
                    //Caut cuvantul dupa word_en(campurile word_en a 2 cuvinte asemenea din 2 limbi va fi asemenea)
                    if(fromLanguageWord.getWord_en().equals(toLanguageWord.getWord_en())){
                        //Daca cuvantul este la singular returnez singularul cuvantului din limba toLanguage
                        if(singular){
                            return toLanguageWord.getWord();
                        }
                        else{
                        //Daca cuvantul este la plural returnez pluralul cuvantului din limba toLanguage
                            return toLanguageWord.getPlural().get(0);
                        }
                    }
                }
            }
        }
        //Daca cuvantul nu a fost gasit in dicitionar se va returna cuvantul initial
        return word;
    }

    /**
     * @param sentence Propozitia care trebuie tradusa.
     * @param fromLanguage Limba in care este propozitia.
     * @param toLanguage Limba in care trebuie tradusa propozitia.
     * @return Intoarce traducerea propozitiei sentence din limba fromLanguage în limba toLanguage.
     */
    public String translateSentence(String sentence, String fromLanguage, String toLanguage){
        //Daca una din limbi nu exista
        if(this.getDictionaries().get(fromLanguage) == null || this.getDictionaries().get(toLanguage) == null )
            return "Una din limbile specificate nu exista";

        //words va fi array din toate cuvintele din propozitie(presupun ca propizitia are ca separator de cuvinte " ")
        String[] words = sentence.split(" ");
        String translatedString = new String();
        //Parcurg arrayul de cuvinte extrase
        for(String s : words){
            //Traduc fiecare cuvant in parte apeland medota translateWord
            translatedString += translateWord(s,fromLanguage,toLanguage);
            translatedString += " ";
        }
        return translatedString;
    }

    /**
     * @param word Cuvantul care trebuie tradus in Engleza.
     * @param fromLanguage Limba din care apartine cuvantul "word".
     * @return Returneaza cuvantul (String) in engleza (cauta prin singular, plural si sinonime).
     */
    public String getWordEn(String word, String fromLanguage) {
        //Verifica daca limba din care vreau sa traduc cuvantul exista
        if (this.getDictionaries().get(fromLanguage) == null)
            return null;

        ArrayList<Word> fromLanguageWords = this.getDictionaries().get(fromLanguage);
        //Parcurg toate cuvintele limbii
        for(Word fromLanguageWord : fromLanguageWords){
            //Parcurg singularul cuvantului
            for(String s : fromLanguageWord.getSingular()){
                if(s.equals(word))
                    return fromLanguageWord.getWord_en();
            }
            //Parcurg pluralul cuvantului
            for(String s : fromLanguageWord.getPlural()){
                if(s.equals(word))
                    return fromLanguageWord.getWord_en();
            }
            //Caut cuvantul prin toate dictionarele de sinonime
            for(int i = 0; i < fromLanguageWord.getDefinitions().size(); i++){
                if (fromLanguageWord.getDefinitions().get(i).getDictType().equals("synonyms"))
                    for(String w : fromLanguageWord.getDefinitions().get(i).getText()){
                        if(w.equals(word)){
                            return fromLanguageWord.getWord_en();
                        }
                    }
            }
        }
        //Daca cuvantul nu a fost gasit nicaieri returnez null
        return null;
    }

    /**
     * @param word Cuvantul care trebuie tradus.
     * @param fromLanguage Limba din care apartine cuvantul "word".
     * @param toLanguage Limba in care trebuie tradus cuvantul "word".
     * @return Returneaza cuvantul tradus (de tip Word).
     */
    public Word returnTranslatedWord(String word,String fromLanguage ,String toLanguage){
        //Verifica daca exista limbile
        if(this.getDictionaries().get(fromLanguage) == null || this.getDictionaries().get(toLanguage) == null )
            return null;

        String wordEn = getWordEn(word, fromLanguage);
        ArrayList<Word> toLanguageWords = this.getDictionaries().get(toLanguage);
        //Parcurg lista de cuvinte toLanguage
        for(Word s : toLanguageWords){
            //Daca gasesc cuvantul atunci il returnez
            if (wordEn.equals(s.getWord_en()))
                return s;
        }
        return null;
    }

    /**
     * @param index Array care pe pozitia i va pastra numarul de sinonime a cuvantului i dintr-o propozitie.
     * @return Returneaza numarul de cazuri posibile de a construi propozitii cu cuvintele din propozitie.
     */
    public int numberOfcases (int[] index){
        int prod = 1;
        for(int i = 0; i < index.length; i++){
            prod *= index[i] + 1;
        }
        return prod;
    }

    /**
     * @param sentence Propozitia care trebuie tradusa.
     * @param fromLanguage Limba in care este propozitia.
     * @param toLanguage Limba in care trebuie tradusa propozitia.
     * @return Returneaza traducerea unei propozitii cu maxim 3 variante de traducere.
     */
    ArrayList<String> translateSentences(String sentence, String fromLanguage, String toLanguage){
        //Verific daca exista limbile
        if(this.getDictionaries().get(fromLanguage) == null || this.getDictionaries().get(toLanguage) == null )
            return null;

        ArrayList<String> sentences = new ArrayList<>();
        String[] words = sentence.split(" ");
        //wordsTranslated va fi un array de cuvinte deja traduce in toLanguage
        Word[] wordsTranslated = new Word[words.length];
        for(int i = 0; i < words.length; i++){
            wordsTranslated[i] = returnTranslatedWord(words[i],fromLanguage,toLanguage);
        }
        //synonymsSize va fi un array de int cu fiecare index numarul de sinonime a fiecarui cuvant
        int[] synonymsSize = new int[wordsTranslated.length];
        for(int i = 0; i < wordsTranslated.length; i++){
            for(int j = 0; j < wordsTranslated[i].getDefinitions().size(); j++){
                if(wordsTranslated[i].getDefinitions().get(j).getDictType().equals("synonyms")){
                    synonymsSize[i] = wordsTranslated[i].getDefinitions().get(j).getText().size();
                    break;
                }
            }
        }

        //Index va fi un array (setat cu 0 pe toate pozitiile) care va incrementa mereu in dependenta de synonymsSize
        int[] index = new int[synonymsSize.length];
        //aux va fi fiecare propozitie deja tradusa pe care o voi adauga in lista de propozitii alternative
        String aux = new String();
        //Parcurg de maxim 3 ori(pentru a genera 3 variante posibile de traducere a unei propoztii)
        for(int i = 0; i < 3; i++){
            aux = "";
            //Parcurg arrayul index
            for(int j = 0; j < index.length; j++){
                //Daca index[j] == 0 atunci voi pune cuvantul general(nu sinonime)
                if(index[j] == 0){
                    aux += wordsTranslated[j].getWord();
                    aux += " ";
                }
                //Daca index[j] != 0 atunci voi pune sinonimul cu indexul de la valoarea index[j]
                if(index[j] != 0){
                    for(int z = 0; z < wordsTranslated[j].getDefinitions().size(); z++){
                        if(wordsTranslated[j].getDefinitions().get(z).getDictType().equals("synonyms")){
                            aux += wordsTranslated[j].getDefinitions().get(z).getText().get(index[j] - 1);
                            aux += " ";
                            break;
                        }
                    }
                }
            }
            //Adaug propozitia in lista de propozitii traduse
            sentences.add(i,aux);
            /*
            Fac incrementarea in index in dependenta de synonymsSize
            Ex:pentru synonymsSize[0,2,1] index va deveni [0,0,0],[0,1,0],[0,2,0],[0,0,1],[0,1,0],[0,1,1],[0,2,0],[0,2,1]
            lucreaza ca o incrementare a unui numar unde fiecare pozitie are un numar diferit(sau nu) de caractere posibile
             */
            //Counter va fi numarul de cazuri posibile, iar daca numarul de cazuri posibile ajunge la 0 atunci inseamna ca
            //nu mai exista cazuri posibile in care pot traduce o propozitie
            int counter = numberOfcases(synonymsSize);
            boolean check = false;
            for(int j = 0; j < index.length; j++){
                //Scad numarul de cazuri posibile ramase
                counter--;
                //Daca a avut deja loc incrementarea cu 1 ies din for
                if(check)
                    break;
                //Daca nu mai exista cazuri posibile de a traduce o propozitie
                if(counter == 0)
                    return sentences;
                //Fac incrementarea
                if(index[j] < synonymsSize[j]){
                    index[j] += 1;
                    check = true;
                }else{
                    //Daca nu mai pot face incrementarea pe pozitia j index[j] devine 0 si trec la incrementarea
                    //urmatorului termen daca asta este posibil
                    index[j] = 0;
                }
            }
        }
    return sentences;
    }

    /**
     * @param word Cuvantul pentru care cauta defintiile.
     * @param language Limba din care apartine cuvantul "word".
     * @return Returneaza un ArrayList de definitii a unui cuvant sortate
     * crescator dupa anul de aparitie a dictionarului.
     */
    public ArrayList<Definitions> getDefinitionsForWord(String word, String language){
        //Verific daca limba exista
        if(this.getDictionaries().get(language) == null)
            return null;

        ArrayList<Word> languageWords = this.getDictionaries().get(language);
        ArrayList<Definitions> definitions = new ArrayList<>();
        //Traduc cuvantul in engleza pentru al putea gasi mai usor
        String wordEn = getWordEn(word,language);
        //Parcurg lista de cuvinte a limbii
        for(Word s : languageWords){
            //Daca am gasit cuvantul
            if(s.getWord_en().equals(wordEn)){
                //Salvez in definitions toate definitiile cuvantului
                definitions = s.getDefinitions();
                //Sortez defintiile in dependenta de an(am suprascris metoda compareTo pentru clasa Definitions)
                Collections.sort(definitions);
                return definitions;
            }
        }
        System.out.println("Nu s-a gasit definitia");
        return definitions;
    }

    /**
     * Cuvintele din JSON sunt ordonate alfabetic, iar definitiile sunt ordonate dupa anul de aparitie al dictionarului.
     * @param language Limba din care este exportat dictionarul.
     * @throws IOException
     */
    public void exportDictionary(String language) throws IOException {
        //Verific daca exista limba
        if(this.getDictionaries().get(language) == null)
            return;

        ArrayList<Word> languageWords = this.getDictionaries().get(language);
        //Sortez cuvintele alfabetic (am suprascris metoda compareTo pentru clasa Word)
        Collections.sort(languageWords);
        //Pentru fiecare cuvant sortez si definitiile dupa an folosind metoda de mai sus(getDefinitionsForWord)
        for(Word s : languageWords){
            ArrayList<Definitions> definitions = getDefinitionsForWord(s.getWord(),language);
            s.setDefinitions(definitions);
        }
        //Scriu in json(va crea un fisier in folderul output cu numeleLimbii_output.json)
        String filename = new String("output/"+language+"_output.json");
        try(Writer writer = new FileWriter(filename)){
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            gson.toJson(languageWords,writer);
        }

    }

    /**
     * Compara cuvantul "a" cu cuvantul "b".
     * @param a
     * @param b
     * @return Returneaza true daca cuvantul a fost updatat si false in caz contrar.
     */
    public boolean updateWord(Word a, Word b){
        boolean check = false;
        //Verific dupa wordEn
        if (!a.getWord_en().equals(b.getWord_en())){
            a.setWord_en(b.getWord_en());
            check = true;
        }
        //Verific dupa tip
        if(!a.getType().equals(b.getType())){
            a.setType(b.getType());
            check = true;
        }
        //Verific dupa singular
        for(String s : b.getSingular()){
            if(!a.getSingular().contains(s)){
                a.getSingular().add(s);
                check = true;
            }
        }
        //Verific dupa plural
        for(String s : b.getPlural()){
            if(!a.getPlural().contains(s)){
                a.getPlural().add(s);
                check = true;
            }
        }
        boolean addnew = false;
        //Verific dupa definitii
        for(int i = 0; i < b.getDefinitions().size(); i++){
            for(int j = 0; j < a.getDefinitions().size(); j++){
                addnew = true;
                //Daca gasesc un dictionar cu acelasi nume
                if(a.getDefinitions().get(j).getDict().equals(b.getDefinitions().get(i).getDict())){
                    addnew = false;
                    //Verific dupa ani
                    if(!(b.getDefinitions().get(i).getYear() == a.getDefinitions().get(j).getYear())){
                        a.getDefinitions().get(j).setYear(b.getDefinitions().get(i).getYear());
                        check = true;
                    }
                    //Verific dupa text
                    for(String s : b.getDefinitions().get(i).getText()){
                        if(!a.getDefinitions().get(j).getText().contains(s)){
                            a.getDefinitions().get(j).getText().add(s);
                            check = true;
                        }
                    }
                    break;
                }
            }
            //Daca definitia cu asa nume nu exista o adaug
            if(addnew){
                a.getDefinitions().add(b.getDefinitions().get(i));
            }
        }
        return check;
    }

    /**
     *
     * @param language Limba din care apartin cuvintele newWords.
     * @param newWords Lista de cuvinte.
     * @return Returneaza lista de cuvinte din dictionarul limbii "language" (updatate in caz ca unele din cuvinte se
     * alfa in lista newWords) + cuvintele noi din newWords.
     */
    public ArrayList<Word> updateDictionary(String language, ArrayList<Word> newWords){
        ArrayList<Word> languageWords = this.getDictionaries().get(language);
        boolean updated;
        //Parcurg cuvintele noului dictionar
        for(int i = 0; i < newWords.size(); i++){
            updated = false;
            //Parcurg cuvintele vechiului dictionar
            for(int j = 0; j < languageWords.size(); j++){
                //Daca gasesc un cuvant asemanator
                if(newWords.get(i).getWord().equals(languageWords.get(j).getWord())) {
                    updateWord(languageWords.get(j), newWords.get(i));
                    updated = true;
                }
            }
            //Daca cuvantul nu a fost gasit
            if (!updated){
                languageWords.add(newWords.get(i));
            }
        }
        return languageWords;
    }

}
