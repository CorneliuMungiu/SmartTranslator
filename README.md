# SmartTranslator – Serviciu de traducere texte 
## Introducere
Scopul acestei teme este de a crea o aplicație similară cu Google Translate care poate să traducă cuvinte sau propoziții dintr-o limbă în alta.

## Descrierea problemei
Firma la care lucrați are o colecție substanțială de dicționare explicative pentru mai multe limbi și vrea să creeze o aplicație care să rivalizeze cu Google Translate. Prin urmare, principala funcționalitate a aplicației este traducerea de cuvinte și propoziții.

Aplicația este dezvoltată de trei echipe separate, o echipă care se ocupă de scanarea dicționarelor și de procesul de OCR (Optical Character Recognition), o echipă care se ocupă de backend și una care se ocupă de frontend. Voi lucrați în echipa de backend care primește informațiile din dicționar de la prima echipă și trebuie să furnizeze date către echipa de frontend. 

Prima echipă v-a pus la dispoziție trei fișiere text ce conțin informații parțiale din dicționarele român,francez si rus în format JSON (JavaScript Object Notation). Restul de dicționare vor fi livrate la un alt moment din viitor. Structura unui element din dicționar este următoarea:
|Nr. linie|Exemplu dicționar român|Descriere câmpuri|
|--|--|--|
|1| { |Un obiect JSON este cuprins între { }, o listă este cuprinsă între [ ]|
|2|"word": "pisică",|Forma de dicționar în limba română|
|3|"word_en": "cat",|Forma de dicționar în limba engleză – disponibilă pentru orice cuvânt din orice dicționar|
|4|"type": "noun",|Partea de vorbire – noun, verb|
|5|"singular": ["pisică"],|Forma de singular a cuvântului|
|6|"plural": ["pisici"],|Forma de plural a cuvântului|
|7|"definitions": [|Listă de definiții pentru cuvântul curent|
|8|{||
|9|"dict": "Dicționar de sinonime",|Numele dicționarului|
|10|"dictType": "synonyms",|Tipul dicționarului – synonyms sau definitions|
|11|"year": 1998,|Anul publicării dicționarului|
|12|"text": ["mâță", "cotoroabă", "cătușă"]|Listă de sinonime sau de definiții ale cuvântului (în acest exemplu sunt disponibile 3 sinonime)|
|13|},||
|14|{||
|15|"dict": "Dicționarul explicativ al limbii române, ediția a II-a",|Numele dicționarului|
|16|"dictType": "definitions",|Tipul dicționarului – synonyms sau definitions|
|17|"year": 2002,|Anul publicării dicționarului|
|18|"text":["...","...","..."]|Listă de sinonime sau de definiții ale cuvântului (în acest exemplu sunt disponibile 3 definiții)|
|19|}||
|20|]||
|21|}||

Notă: În funcție de tipul părții de vorbire, câmpurile singular și plural pot avea următorul format:

|Parte de vorbire|Singular|Plural|
|--|--|--|
|Noun|Listă cu forma de singular a cuvântului|Listă cu forma de plural a cuvântului|
|Verb|Listă de conjugări la indicativ prezent singular în ordine persoana I, a II-a și a III-a|Listă de conjugări la indicativ prezent plural în ordine persoana I, a II-a și a III-a|

## Functionalitati implementate

1. Citirea din dicționare și salvarea informațiilor într-o singură colecție de date. Pentru citirea din JSON s-a folosit biblioteca [gson](https://github.com/google/gson). 
	- Se vor citi toate fișierele dintr-un folder de intrare care au extensia .json 
	- **Atenție** – nu știți de la început numărul lor
2. Metodă pentru adăugarea unui cuvânt în dicționar 
	- `boolean addWord(Word word, String language)`
	- Întoarce **true** dacă s-a adăugat cuvântul în dicționar sau **false** dacă există deja cuvântul în dicționar
3. Metodă pentru ștergerea unui cuvânt din dicționar ce primește ca parametru cuvântul și limba
	- `boolean removeWord(String word, String language)`
	- Întoarce **true** dacă s-a șters cuvântul în dicționar sau false dacă nu există cuvântul în dicționar
4. Metodă pentru adăugarea unei noi definiții pentru un cuvânt dat ca parametru
	- `boolean addDefinitionForWord(String word, String language, Definition definition)`
	- Întoarce **true** dacă s-a adăugat definiția sau **false** dacă există o definiție din același dicționar (dict)
5. Metodă pentru ștergerea unei definiții a unui cuvânt dat ca parametru
	- `boolean removeDefinition(String word, String language, String dictionary)`
	- Întoarce **true** dacă s-a șters definiția sau **false** dacă nu există o definiție din dicționarul primit ca parametru
6. Metodă pentru traducerea unui cuvânt
	- `String translateWord(String word, String fromLanguage, String toLanguage)`
	- Întoarce **traducerea** cuvântului word din limba **fromLanguage** în limba **toLanguage**
7. Metodă pentru traducerea unei propoziții
	- String translateSentence(String sentence, String fromLanguage, String toLanguage)
	- Întoarce traducerea propoziției sentence din limba fromLanguage în limba toLanguage
8. Metodă pentru traducerea unei propoziții și furnizarea a **3 variante** de traducere folosind sinonimele cuvintelor
	- `ArrayList translateSentences(String sentence, String fromLanguage, String toLanguage)`
	- Întoarce traducerea propoziției sentence din limba **fromLanguage** în limba **toLanguage**
	- În cazul în care nu există **3 variante** de traducere a propoziției se vor furniza doar variantele posibile
9. Metodă pentru întoarcerea definițiilor și sinonimelor unui cuvânt
	-  `ArrayList getDefinitionsForWord(String word, String language)`
	- Definițiile sunt sortate crescător după anul de apariție al dicționarului
10. Metodă pentru exportarea unui dicționar în format JSON
	- `void exportDictionary(String language)`
	- Se va exporta doar partea din structura de date ce ține de limba primită ca parametru și se vor scrie informațiile într-un fișier
	- Cuvintele din JSON sunt ordonate alfabetic, iar definițiile sunt ordonate după anul de apariție al dicționarului

# Detalii despre implementare

**Word.java**

Clasa care contine informatii despre cuvant conform formatului specificat in fisierul json.

- (Override) Metoda compareTo pentru sortare alfabetica.

**Definitions.java**

Clasa care contine informatii despre definitii conform formatului specificat in fisierul json.

- (Override) Metoda compareTo pentru sortare alfabetica.
  
**ReadFromJson**
- *Metoda readJson*  
    Citeste din folderul "input" toate fisiere json si adauga cuvintele in dictionare returnand dictionarele.

- *Metoda main*  
    Testeaza toate metodele din Dictionaries.java

**Dictionaries.java**  
    Salveaza cuvintele intr-un Map<String, ArrayList\<Word\>\> dictionaries unde String este limba cuvantului (citita din numele fisierului (ex: fr.json => limba fr)) si ArrayList\<Word\>\> este lista de cuvinte.

- *addWord*  
    Adauga cuvantul "word" in lista de cuvinte a limbii "language" si returneaza true daca cuvantul a fost adaugat cu succes in lista (sau daca acesta a fost updatat) sau false in caz ca cuvantul era deja in lista si nu a fost adaugata o informatie noua.

- *removeWord*  
    Sterge cuvantul "word" din lista de cuvinte a limbii "language" si returneaza true daca cuvantul a fost sters cu succes sau false daca cuvantul nu era in lista.

- *addDefinitionForWord*  
    Adauga cuvantului "word" din lista de cuvinte a limbii "language" o definitie noua in lista de definitii a cuvantului si returneaza true in cazul in care definitia a fost adaugata cu succes (sau a fost updatata) sau false in caz ca definitia era deja in lista de definitii si aceasta nu a fost updatata.

- *removeDefinition*  
    Sterge definitia "dictionary" din lista de definitii a cuvantului "word" din lista de cuvinte a limbii "language" si returneaza true daca definitia a fost stearsa cu succes sau false in caz ca definitia (sau cuvantul) nu au fost gasite.

- *translateWord*  
    Traduce cuvantul "word" din limba "fromLanguage" in limba "toLanguage" (chiar daca se apeleaza cu un cuvant la plural, un cuvant din sinonime sau din singular) si returneaza cuvantul deja tradus sau returneaza cuvantul initial in caz ca nu s-a gasit in lista de cuvinte a limbii "toLanguage" traducere cuvantului "word".

- *translateSentence*  
    Traduce propozitia "sentence" din limba "fromLanguage" in limba "toLanguage" si returneaza un String care reprezinta propozitia tradusa. In cazul ca unul dintre cuvinte nu a fost gasita in lista de cuvinte a limbii "toLanguage" acesta isi va pastra forma initiala.

- *getWordEn*  
    Traduce cuvantul "word" din limba "fromLanguage" in limba Engleza. Returneaza cuvantul tradus sau null in caz ca nu a gasit cuvantul.

- *returnTranslatedWord*  
    Traduce propozitia "sentence" din limba "fromLanguage" in limba "toLanguage" si returneaza cuvantul de tip Word sau null in caz contrar.

- *numberOfcases*  
    Calculeaza numarul de cazuri posibile de a forma propozitii diferite avand sinonimele tuturor cuvintelor.

- *translateSentences*  
    Traduce propozitia "sentence" din limba "fromLanguage" in limba "toLanguage" si returneaza o lista de propozitii (maxim 3) cu diferite variatii ale cuvintelor (folosind sinonimele acestora).

- *getDefinitionsForWord*  
    Returneaza toate defintiile cuvantului "word" din limba "language" sortate alfabetic.

- *exportDictionary*  
    Exporteaza lista de cuvinte a limbii "language" in folderul output unde creeaza un nou fisier (numeleLimbii_output.json) de tip json care contine informatiile despre toate cuvintele din acel dictionar.

- *updateWord*  
    Primeste doua cuvinte (a si b) si updateaza cuvantul "a" daca in "b" se afla ceva ce nu era in "a" mai devreme. Returneaza true daca a fost facuta macar o modificare in "a" si false in caz contrar.

- *updateDictionary*  
    Updateaza cuvintele din lista de cuvinte a limbii "language" cu cuvintele din lista "newWords" si returneaza noua lista creeata. Se updateaza cuvintele care au coincis cu cuvintele din "newWords" si se adauga cuvintele noi.