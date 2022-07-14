import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class ReadFromJson {

    /**
     * Citeste toate fisierele json din fisierul input
     * @return Intoarce lista de dictionare citite.
     */
    public static Dictionaries readJson() {
        Dictionaries dictionariesList = new Dictionaries();
        Gson gson = new Gson();
        ArrayList<Word> words;
        try{
            String fileName = "input";
            File folder = new File(fileName);
            File[] fileList = folder.listFiles();
            assert fileList != null;
            for(File file : fileList){
                //numele limbii vor fi caracterele de pana la "_" (ex ro_dict.json => language = "ro")
                String languageName = file.getName().split("_")[0];
                JsonReader jsonReader = new JsonReader(new FileReader(file));
                words = gson.fromJson(jsonReader,new TypeToken<ArrayList<Word>>(){}.getType());
                //Daca deja exista o limba cu asa nume , fac update datelor
                if(dictionariesList.getDictionaries().containsKey(languageName)){
                    words = dictionariesList.updateDictionary(languageName,words);
                }
                dictionariesList.getDictionaries().put(languageName,words);
            }
        }catch (Exception ex){
            System.out.println("Parsing eroor " + ex.toString());
        }
        return dictionariesList;
    }

    /**
     * Testeaza toate metodele
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

    //Citeste dictionarele din fisierele json
        Dictionaries dictionaries = readJson();

    //Creez un cuvant nou in limba romana
        ArrayList<Definitions> testDefinitionList = new ArrayList<>();
        ArrayList<String> testString = new ArrayList<>();
        testString.add("text1");
        testString.add("text2");
        Definitions testDefinition = new Definitions("Dictionar explicativ","explicatii",2021,testString);

        ArrayList<String> testSingular = new ArrayList<>();
        testSingular.add("cal");
        ArrayList<String> testPlural = new ArrayList<>();
        testPlural.add("cai");
        testDefinitionList.add(testDefinition);

        Word testWord = new Word("cal","horse","noun",testSingular,testPlural,testDefinitionList);

    //Creez un cuvant deja existent in limba romana(identic)
        ArrayList<Definitions> testDefinitionList1 = new ArrayList<>();
        ArrayList<String> testString1 = new ArrayList<>();
        testString1.add("persoana întâi, eu se referă la persoana care vorbește.");
        Definitions testDefinition1 = new Definitions("Wiktionary","definitions",2019,testString1);

        ArrayList<String> testSingular1 = new ArrayList<>();
        testSingular1.add("eu");
        ArrayList<String> testPlural1 = new ArrayList<>();
        testPlural1.add("noi");
        testDefinitionList1.add(testDefinition1);

        Word testWord1 = new Word("eu","I","noun",testSingular1,testPlural1,testDefinitionList1);
    //Creez un cuvant deja existent in limba romana cu mici update-uri
        ArrayList<Definitions> testDefinitionList2 = new ArrayList<>();
        ArrayList<String> testString2 = new ArrayList<>();
        testString2.add("persoana întâi, eu se referă la persoana care vorbește.");
        testString2.add("Am adaugat o noua definitie");
        Definitions testDefinition2 = new Definitions("Wiktionary","definitions",2019,testString2);


        ArrayList<String> testSingular2 = new ArrayList<>();
        testSingular2.add("eu");
        ArrayList<String > testPlural2 = new ArrayList<>();
        testPlural2.add("noi");
        testPlural2.add("noua");
        testDefinitionList2.add(testDefinition2);

        Word testWord2 = new Word("eu","I","noun",testSingular2,testPlural2,testDefinitionList2);

    //Verificare addWord
    //Adaug un cuvant nou in lista de cuvinte a limbii romane
        System.out.println("Verificare Task2 addWord");
        System.out.println(dictionaries.addWord(testWord,"ro"));//Adauga un nou cuvant(True)
        System.out.println(dictionaries.addWord(testWord1,"ro"));//Nu adauga cuvantul intrucat exista(False)
        System.out.println(dictionaries.addWord(testWord2,"ro"));//Nu adauga un cuvant nou dar il updateaza(True)


        System.out.println();
    //Verificare removeWord
        System.out.println("Verificare Task3 removeWord");
        //Sterg cuvantul "merge" din limba romana
        System.out.println(dictionaries.removeWord("merge","ro"));//Sterge cu succes deci returneaza true
        //Incerc sa sterg un cuvant inexistent din limba romana
        System.out.println(dictionaries.removeWord("ananas","ro"));//Intoarce false intrucat nu a gasit ce sa stearga
        //Incerc sa sterg un cuvant dintr-o limba inexistenta
        System.out.println(dictionaries.removeWord("cal","bg"));//Intoarce false intrucat nu exista sa limba

    //Verificare addDefinitionForWord
        System.out.println();
        System.out.println("Verificare Task4 addDefinitionForWord");
        //Incerc sa adaug o definitie deja existenta cuvantului "eu" din limba romana
        //Va returna false pentru ca deja exista asa definitie si nu a fost updatat nimic
        System.out.println(dictionaries.addDefinitionForWord("eu","ro",testDefinition2));
        //Adaug o noua definitie deja existenta dar putin modificata
        testDefinition2.setYear(2021);
        ArrayList<String> testString3 = new ArrayList<>();
        testString3.add(new String("persoana întâi"));
        testDefinition2.setText(testString3);
        //Va intoarce true pentru ca a facut update definitiei
        System.out.println(dictionaries.addDefinitionForWord("eu","ro",testDefinition2));

    //Verificare removeDefinition
        System.out.println();
        System.out.println("Verificare Task5 removeDefinition");
        //Incerc sa sterg o definitie inexistenta de la un cuvant existent
        //Va returna false intrucat nu a sters nimic
        System.out.println(dictionaries.removeDefinition("eu","ro","blablabla"));
        //Sterg o definitie existenta(returneaza true)
        System.out.println(dictionaries.removeDefinition("discussion","fr","GoogleTranslate"));
        //Incearca sa stearga ceva dintr-o limba inexistenta(returneaza false)
        System.out.println(dictionaries.removeDefinition("cuvant","lt","GoogleTranslate"));

    //Verificare translateWord
        System.out.println();
        System.out.println("Verificare Task6 translateWord");
        //Traduc un cuvant existent in ambele limbi
        System.out.println("pisica = " + dictionaries.translateWord("pisică","ro","fr"));
        //Traduc un cuvant din sinonimele acestuia
        System.out.println("cotoroabă = " + dictionaries.translateWord("cotoroabă","ro","fr"));
        //Traduc un cuvant din plural in plural
        System.out.println("pisici = " + dictionaries.translateWord("pisici","ro","fr"));
        //Traduc un cuvant inexistent
        System.out.println("Cuvant inexistent = " + dictionaries.translateWord("blablabla","ro","fr"));

    //Verificare translateSentence
        System.out.println();
        System.out.println("Verificare Task7 translateSentence");
        //Traducerea unei propozitii normale
        System.out.println(dictionaries.translateSentence("chiens manger crêpe","fr","ro"));
        //Una din limbi nu exista
        System.out.println(dictionaries.translateSentence("chiens manger crêpe","fr","bg"));

    //Verificare translateSentences
        System.out.println();
        System.out.println("Verificare Task8 translateSentences");
        //Traducerea unei propozitii care are cel putin 3 variante posibile
        System.out.println(dictionaries.translateSentences("chien manger crêpe","fr","ro"));
        //Traducerea unei propozitii care are mai putin de 3 variante posibile
        System.out.println(dictionaries.translateSentences("manger publier","fr","ro"));

    //Verificare getDefinitionsForWord
        System.out.println();
        System.out.println("Verificarea Task9 getDefinitionsForWord");
        //Intoarcerea definitiilor unui cuvant care are definitiile sortate descrescator
        ArrayList<Definitions> definitions = dictionaries.getDefinitionsForWord("clătită","ro");
        //Definitiile au fost sortate ascendent
        for(Definitions s : definitions){
            System.out.println(s.getDict());
        }
        //Incerc sa caut o definitie care nu exista
        definitions = dictionaries.getDefinitionsForWord("blablabla","ro");
        for(Definitions s : definitions){
            System.out.println(s.getDict());
        }

    //Verificarea exportDictionary
        System.out.println();
        System.out.println("Verificarea Task10 exportDefinition");

        //Exportez dictionarul roman
        //Dictionarul se la exporta cu cuvintele in ordine alfabetica si definitiile crescator in dependenta de anul publicarii
        dictionaries.exportDictionary("ro");


    }

}
