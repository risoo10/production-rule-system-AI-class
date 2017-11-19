## 4. Zadanie

# Dopredný produkčný systém 

## Umelá Inteligencia (predmet)

### Richard Mocák

Slovenská technická univerzita v Bratislave,

Fakulta informatiky a informačných technológií


## Zadanie
Úlohou je vytvoriť jednoduchý dopredný produkčný systém, s prípadnými rozšíreniami, napríklad o kladenie otázok používateľovi alebo vyhodnocovanie matematických výrazov.
Produkčný systém patrí medzi znalostné systémy, teda medzi systémy, ktoré so svojimi údajmi narábajú ako so znalosťami. Znalosti vyjadrujú nielen informácie o nejakom objekte, ale aj súvislosti medzi objektami, vlastnosti zvolených problémov a spôsoby hľadania ich riešenia. Znalostný systém je teda v najjednoduchšom prípade dvojica - program, ktorý dokáže všeobecne manipulovať so znalosťami a báza znalostí, ktorá opisuje problém a vzťahy, ktoré tam platia. Znalosti majú definovanú nejakú štruktúru a spôsob narábania s touto štruktúrou - to sa nazýva formalizmus reprezentácie znalostí. Program vie pracovať s týmto formalizmom, ale nesmie byť závislý od toho, aké znalosti spracováva, inak by to už nebol systém, kde riešenie úlohy je dané použitými údajmi.

Produkčný systém na základe odvodzovacieho pravidla modus ponens (pravidlo odlúčenia) odvodzuje zo známych faktov a produkčných pravidiel nové fakty. Ak systém nemá dostatok vstupných údajov, môže klásť používateľovi otázky.
Produkčný systém ako program nepozná konkrétne pravidlá ani fakty! Pozná len formalizmus, v tomto prípade štruktúru pravidiel a faktov a spôsob ich spracovania. Pozná akcie (pridaj, vymaž, ...), ktoré sa môžu vykonávať, lebo tie patria do opisu formalizmu.


## Štruktúra systému
Dopredný produkčný systém som implementoval v programovacom jazyku JAVA ako JAVA FX aplikáciu. Funkcionalita systému je rozdelená do viacerých tried. Hlavnou triedou je **AlgorithmController**, ktorá obsahuje telo a kroky algoritmu pre produkovanie nových faktov z tých aktuálnych použitím pravidiel ( metóda *startAlgorithm()* ). 

Okrem triedy pre algoritmus boli vytvorené aj ďalšie triedy pre reprezentáciu pravidiel, faktov a akcií, ktoré sa v systéme nachádzajú alebo vykonávajú. 

Inicializačné údaje ako báza znalostí alebo báza faktov sú na začiatku algoritmu načítané z grafického rozhrania, kde su počaitočne nastavené na identický prípad ako **príklad** zadania na URL (http://www2.fiit.stuba.sk/~kapustik/dp-sys-ot.html). Pred spustením môžemu inicializačé fakty a bázu pravidiel pozmeniť podľa vlastného uváženia.


## Reprezentácia znalostí

Všetky znalosti sú v systéme reprezentované počas celého behu programu. Snažil som sa rozbiť všetky znalosti na atomické elementy, ktoré sú potom reprezentované cez základné typy premenných.

- **Pravidlo** (alebo počiatočná báza pravidiel) - **trieda Rule** 

    Pravidlo je reprezentované názvom, podmienkami, ktoré musia byť splnené a akciami, ktoré sa po splnení môžu vykonať. Podmienky a akcie sú preto reprezentvané iba obyčajným **Zoznamom znakových reťazcov**.
     
     Formalizmus - systém rozpozná pravidlo len ak je napísané práve v takomto formáte / štýle:
     
        DruhyRodic1:                            // nazov
        IF (?X je rodic ?Y)+(manzelia ?X ?Z)    // podmienky 
        THEN (ADD ?Z je rodic ?Y);              // akcie
    
     Každá podmienka alebo akcia je vložená do "( )" a oddelená "+" znakom. Pravidlá sú od seba oddelená vďaka ";". Za posledným pravidlom ";" nie je.
     
- **Fakt** 

    Fakty sú počas všetkýc krokov algoritmu udržiavané ako zreťazený zoznam znakových reťazcov. Po inicializácii sa naplniá iba inicializačnými faktami a potom sú postupne po iteráciach algoritmu doplňané novými faktami alebo sú niektoré zmazané.
    
    V grafickom rozhraní sa fakty zadávaju do textového poľa, kde je každý riadok vstupu reprezentovaný ako *fakt*. 
     
            Peter je rodic Jano     // fakt
            Peter je rodic Vlado    // fakt
            manzelia Peter Eva      // fakt
            

## Opis algoritmu
Algoritmus sa spustí po stlačení tlačidla **Start !**.

Hneď po spustení sa inicializujú fakty a pravidlá zo vstupných textových polí a podľa stanoveného formalizmu, sú potom pravidlá reprezentované pomocou objektov typu **Rule**. Podmienky a akcie sú spracované do zoznamo, čo umožňuje ľahšiu prácu pri vytvárraní aplikovateľných inštancií pravidiel. 

Po inicializácii faktov a bázy pravidiel sa začnú spracovávať pravidlá a budú sa vytvárať aktívne inštancie pravidiel. Samotný process nájdeme v metóde **processRules()**. Tento proces prebieha rekurzívne. 

Pred rekurziou sa zo všetkých bázových pravidiel vytvorí **Zoznam aktivných pravidiel**. Tu vstupuje trieda **ActiveRule**, ktorá vo svojich atribútoch obsahuje :

- Bázové pravidlo (rule)
- Hash mapa, pre rýchly prístup k hodnotám premenných v podmienkach, ktoré sú uložené pod kľúčom podľa písmena premennej. (variables)
- Počet všetkých podmienok, ktoré musia byť splnené (conditions)
- Počet už aktuálne splnených podmienok. (conditionsOK)
- True/false hodnota, ktorá rozhoduje či sa pri filtrovaní pravidiel presadí do ďalšej rekurzie, kde su spracované pravidla ďalej. (removed)
- Podmienky ako zoznam reťazcov
- Akcie ako objekt akcia

V každom stupni rekurzie sa vezme aktuálne nesplnená podmienka a algoritmus sa skúša namapovať nejaký fakt na zvolenú podmienku. Ak sa mu to podarí, tak z faktu vytiahne hodnoty premenných a uloží do hash mapz a toto pravidlo pridá do zoznamu aktívných pravidiel pre ďašiu rekurzie. 
 
 Ak sa na zvolenú podmienku nenamapuje žiaden fakt, alebo po namapovaní sú premenné z nového faktu v konflikte s premennými uloženými z predchádzajúcich podmienok, tak takéto pravidlo nie je zaradené do zoznamu pre spracovanie v ďalšom stupni rekurzie.
 
 Ak je jasné, že sme už splnili všetky podmienky pre bázové pravidlo a všetký premenné sú v zhode, tak sa spracujú akcie tohto pravidla. Ak sa stane, že nejaká akcia môže spôsobiť konflikt (pr. pridať už existujúci fakt), tak je takéto pravidlo zahodené. Ak však všetky kontroly zbehli úspešne, tak je takáto inštancia pravidla uložená do fronty úspešných pravidiel. Toto pravidlo sa už neposiela do ďalšieho stupňa iterácie.
 
 Rekurzia sa zastaví ak zoznam aktívných pravidiel v rekurzii zostane prázdny.
  
 Takto sa do fronty, dostanú iba všetky aktívne inštancie pravidiel, ktoré splnili podmienky a produkujú nové fakty, mažú existujúce fakty alebo vypisujú správy. 
 
        processRules(rules);
        
        // Process first active rule from queue and execute actions
        while(!activeRuleQueue.isEmpty()){
        
            // Poll active rule
            ActiveRule activeRule = activeRuleQueue.poll();
            
            // Execute every action
            for(Action ac : activeRule.actions){
                String message = ac.execute(facts);  // By default NULL
                printMessage(message);              // Only when action is Message 
            }
            
            // Produce new active rules from newly added or removed facts
            // Add successful ones to the queue
            processRules(rules);

        }
 
 Slučka algoritmu pokračuje kým, nie je fronta s úspešnými pravidlami komplétne prázdna. V každej iterácií sa vždy z fronty vyberie práve jedna úspešená inštancia pravidla a vykonajú sa všetky jej akcie. Potom sa zase zavolá motóda **processRules()**, ktorá zase vytvorí nové inštancie pravidiel ale už aj z novo pridanými / zmazanými faktami. 
 
 