
# Využitie Apache Spark na detekciu a ošetrenie obchodne relevantných udalostí 📈

### V spolupráci s:

[![FEI STU](https://i.imgur.com/qlXeryE.png)](https://www.fei.stuba.sk/)

[![SLSP](https://i.imgur.com/muHN2uS.png)](https://www.slsp.sk/sk/ludia)  
   
## Technológie

- Java

- Apache Spark

- Apache Kafka

- Drools

- Confluent Platform


## Zadanie
Bankové systémy sú zdrojom "streamov" dát, ktoré môžu indikovať množstvo zaujímavých situácií, na ktoré je vhodné reagovať s čo najkratším oneskorením. Transakčné dáta, procesné a iné logy, keď dokážu byť včas porozumené a interpretované, môžu priniesť rôzne benefity ako napríklad:

- zníženie nákladov na fraudulentné transakcie
- eliminácia problémov s priepustnosťou procesov (škálovanie)
- včasná identifikácia chýb
- riešenie problémových situácií alebo včasná reakcia na príležitosti bankových klientov

Cieľom práce bolo vybrať efektívny prístup k implementácii detegovania udalostí nad "streamom" dát (**Apache Kafka**) prostredníctvom **Apache Spark**, vytvoriť prototyp aplikácie a na ňom demonštrovať vlastnosti riešenia.

Práca bola vypracovaná v spolupráci s firmou Slovenská sporiteľňa a.s.

## Úlohy

-  [X] oboznámiť sa s platformou Apache Kafka a jej konceptmi (zdroj a cieľ dátových "streamov" riešenia),

-  [X] oboznámiť sa s platformou Apache Spark a jej konceptmi (tu v roli analytického "engine" pre spracovanie dátových "streamov"),
-  [X] popíať jednotlivé komponenty a ich účel, preveriť prípadnú redundanciu/podobnosť funkcionalít,
-  [X] identifikovať možnosti, vlastnosti, výhody a nevýhody použiteľných programovacích jazykov (Java, Scala) a preveriť možnosť zaintegrovania kódu modelu v jazyku Python,
-  [X] nad poskytnutými dátami identifikovať a špecifikovať udalosť, na ktorú je obchodne prínosné reagovať s krátkym oneskorením,
-  [X] v závislosti od očakávanej logiky spracovania, vyberať vhodný spôsob jej implementácie (integrovanie kódu modelu, integrovanie nástroja pre nastavovanie a exekúciu pravidiel "rule engine" a pod.),
-  [X] implementovať načítanie dát zo vstupného dátového "streamu", kód detegovania udalosti a zápis informácie o detegovanej udalosti do výstupného dátového "streamu",
-  [X] popísať vlastnosti riešenia (priepustnosť, robustnosť, zotaviteľnosť, škálovateľnosť, udržiavateľnosť/čitateľnosť).

Súčasťou práce sú taktiež aj dve video ukážky na ktorých sú demonštrované niektoré z vlastností aplikácie:

[![Priepustnosť](https://i.imgur.com/md87U2R.jpg)](https://drive.google.com/file/d/1nxrPASZ97A2IgC0b89G6MX7ZAnPT1cLY/view?usp=sharing)

[![Zotaviteľnosť](https://i.imgur.com/GjKYFAT.jpg)](https://drive.google.com/file/d/1lxhnoP3-2xS_kgKfEozJSqCeFmcfhul5/view?usp=sharing)

Prácu je taktiež možné nájsť aj v [CRZP](https://opac.crzp.sk/?fn=detailBiblioForm&sid=64930206F366C8E34104E4C607D8).
