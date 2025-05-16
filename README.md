# Proiect-TSS-2025

Membrii echipei:
* Ciobanu Dragoș (grupa 351)
* Petrovici Ricardo (grupa 351)
* Mihnea Vicențiu Bucă (grupa 352)

Tema aleasă: <b>(T3) Testare unitară în Java</b>

## Analiză State-of-Art Testare Unitară Java
### 1. Introducere
Testarea unitară reprezintă o practică esențială în dezvoltarea de software, contribuind la asigurarea calității codului și la detectarea rapidă a erorilor în fazele incipiente ale dezvoltării. În contextul limbajului Java, testarea unitară s-a dezvoltat semnificativ, beneficiind de instrumente și framework-uri specializate. Această analiză state-of-art își propune să ofere o perspectivă detaliată asupra conceptelor fundamentale, a tehnologiilor actuale și a resurselor disponibile, precum și să identifice avantajele și limitările principalelor instrumente utilizate în domeniu, cum ar fi Maven, JUnit, JaCoCo și PIT.

### 2. Definiții Esențiale și Fundamente Teoretice
* [Testare unitară: Termenul de „testare unitară” se referă la testarea individuală a unor unități separate dintr-un sistem software. În sistemele orientate pe obiecte, aceste „unități” sunt de regulă clase și metode.](https://users.utcluj.ro/~igiosan/Resources/POO/Lab/12-Testarea_Unitara.pdf)

* [Avantajele testării unitare: Stabilitate, detectare rapidă a erorilor, facilitarea refactorizării.](https://www.researchgate.net/publication/379508034_The_Crucial_Role_of_Unit_Tests_in_Software_Development)

* [State of Art Coverage Tools](https://www.eecs.yorku.ca/~zmjiang/publications/ase2018_chen.pdf)

#### Functional Testing
Functional testing (sau black-box testing) se concentrează pe verificarea funcționalității aplicației conform specificațiilor cerute, fără a ține cont de implementarea internă a codului. Testele sunt concepute pentru a valida dacă sistemul produce rezultatele așteptate în funcție de inputuri specifice, asigurând că toate funcțiile cerute sunt implementate corect.

#### Structural Testing 
Structural testing (sau white-box testing) se concentrează pe analiza internă a structurii codului sursă. Testele sunt proiectate pe baza cunoașterii implementării interne, verificând fluxurile de control și logica internă. Această abordare ajută la identificarea erorilor în structura și logica internă a codului.

#### Mutation Testing 
Mutation testing presupune introducerea intenționată a unor modificări minore (mutanți) în codul sursă pentru a evalua eficacitatea suitei de teste. Scopul este de a determina dacă testele existente pot detecta aceste modificări, evidențiind astfel eventuale lacune în acoperirea testelor.

### 3. [Analiza Aplicațiilor Existente](https://github.com/apache/commons-lang) (Strategii de testare)
Un exemplu concret este <b>Apache Commons Lang.</b> Acest proiect open source, parte din Apache Commons, folosește:

* Maven pentru gestionarea dependențelor și automatizarea build-ului,

* JUnit ca framework principal pentru testarea unitară,

* JaCoCo pentru măsurarea acoperirii codului de către teste,

* și, în unele cazuri, PIT pentru mutation testing, evaluând astfel eficacitatea suitei de teste.

Avantaje

* Testare Eficientă și Ușor de Implementat:
JUnit oferă un cadru robust pentru scrierea și rularea testelor unitare, simplificând validarea comportamentului codului.

* Evaluarea Calității Codului:
JaCoCo oferă rapoarte detaliate privind acoperirea codului, ajutând la identificarea zonelor care necesită teste suplimentare.

* Calitatea Suitei de Teste:
PIT, prin mutation testing, permite evaluarea eficienței testelor, identificând eventualele lacune prin simularea unor defecte minore în cod.

* Comunitate și Suport:
Aceste instrumente sunt bine-cunoscute și folosite pe scară largă în ecosistemul Java.

Dezavantaje

* Rapoartele Detaliate Necesită Interpretare:
Rapoartele generate de JaCoCo pot fi detaliate și necesită o analiză atentă pentru a extrage informațiile relevante, ceea ce poate implica un efort suplimentar din partea echipei.

### 4. Servicii Disponibile
- **Maven:**  
  Instrument de build și management al dependențelor pentru proiectele Java.  
  [Setup Maven](https://maven.apache.org/install.html)

- **JUnit:**  
  Framework popular pentru scrierea și rularea testelor unitare în Java.  
  [Setup JUnit](https://junit.org/junit5/docs/current/user-guide/)
  * Run: mvn clean test
    
- **JaCoCo:**  
  Instrument pentru măsurarea acoperirii codului de către teste.  
  [Setup JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/)
  * Run: mvn clean verify

- **PIT:**  
  Instrument de mutation testing folosit pentru evaluarea eficienței suitei de teste.  
  [Setup PIT](https://pitest.org/quickstart/)
  * Run: mvn clean test pitest:mutationCoverage

## Configurația software

* **IDE**: IntelliJ IDEA Community Edition
* **JDK**: OpenJDK 24 (2024-03-18)
* **Limbaj și compilator**: Java 17 (setat în Maven prin `maven.compiler.source` și `target`)
* **Build system**: Maven
* **Testing**:

  * **JUnit**: 4.13.2
  * **system-rules**: 1.19.0
* **Test coverage**: JaCoCo 0.8.10
* **Mutation testing**: PIT (pitest-maven) 1.15.0
* **Note**: No Virtual Machine was used. 

## Configurația hardware

* **Sistem**: Apple Mac cu cip M2
* **Sistem de operare**: macOS (ultima versiune disponibilă la momentul utilizării)

## Testare funcție - `distributeMoney`

Funcția distributeMoney distribuie o sumă de bani (amount) către diferite categorii de cheltuieli asociate unui cont (accountId), conform procentelor stabilite. Dacă în descriere este inclus cuvântul „SAVE” și procentele de distribuție însumate sunt sub 100%, diferența rămasă este adăugată automat în contul de economii. Funcția verifică validitatea contului și a sumei, afișând mesaje corespunzătoare în caz de eroare sau la finalul distribuției.

![image](https://github.com/user-attachments/assets/84906b93-cfb7-4d01-9989-35fc2cc35ba9)

## Functional Testing
### Împărțire în clase de echivalență


Împărțirea în clase de echivalență este o tehnică de testare care împarte spațiul de intrări în clase echivalente: dacă un test dintr-o clasă trece sau eșuează, se presupune că și celelalte din acea clasă se comportă la fel. Astfel, testăm câte un exemplu din fiecare clasă.

### Variabile / clase definite
| Variabilă | Clasă | Descriere                              |
| --------- | ----- | -------------------------------------- |
| `i`       | `i1`  | Contul **nu există** în sistemul bancar|
|           | `i2`  | Contul **există** în sistemul bancar   |
| `a`       | `a1`  | Suma distribuită este **negativă**     |
|           | `a2`  | Suma distribuită este **zero**         |
|           | `a3`  | Suma distribuită este **pozitivă**     |
| `p`       | `p1`  | Procentele însumează **sub 100%**      |
|           | `p2`  | Procentele însumează **exact 100%**    |
|           | `p3`  | Procentele **peste 100%** – caz exclus |
| `s`       | `s1`  | Descrierea **conține** cuvântul "SAVE" |
|           | `s2`  | Descrierea **nu conține** "SAVE"       |

### Tabel cazuri de testare (combinări spre clase de echivalență):

| Test | i – cont existent? | a – sumă | p – % cheltuieli | s – "SAVE"? | Răspuns                         |
| ---- | ------------------ | -------- | ---------------- | ----------- | ------------------------------- |
| 1    | i1 – nu există     | -        | -                | -           | Eroare: contul nu există        |
| 2    | i2 – există        | a1 < 0 | -                  | -           | Eroare: sumă negativă           |
| 3    | i2 – există        | a2 = 0   | -                | -           | Eroare: sumă zero               |
| 4    | i2 – există        | a3 > 0   | p1 < 100%        | s1 – da     | Se adaugă la economii           |
| 5    | i2 – există        | a3 > 0   | p1 < 100%        | s2 – nu     | Nicio economie                  |
| 6    | i2 – există        | a3 > 0   | p2 100%          | -           | Nicio economie (totul cheltuit) |

### Analiză valori de frontieră

Boundary Value Analysis este o tehnică de testare care se concentrează pe comportamentul aplicației la limitele valorilor acceptate. Erorile apar adesea în apropierea acestor limite, deci testarea acolo este esențială.

### Variabile / limite testate

| Variabilă | Valoare de graniță | Descriere                                                      |
| --------- | ------------------ | -------------------------------------------------------------- |
| `a`       | `0.01`             | Cea mai mică sumă pozitivă validă                              |
| `p`       | `0%`               | Nicio cheltuială – toată suma se economisește                  |
|           | `99%`              | Aproape tot cheltuit – 1% pentru economii                      |
|           | `100%`             | Totul cheltuit – nimic salvat                                  |
| `s`       | `"SAVE"` / fără    | cu sau fără economisire                                        |

### Tabel cazuri de testare (valori-limită analizate)

| Test | i – cont existent? | a – sumă | p – % cheltuieli | s – "SAVE"? | Răspuns                              |
| ---- | ------------------ | -------- | ---------------- | ----------- | ------------------------------------ |
| 1    | i2 – există        | 100.0    | 0%               | s1 – da     | Totul economisit (100%)              |
| 2    | i2 – există        | 100.0    | 99%              | s1 – da     | Se economisește 1%                   |
| 3    | i2 – există        | 120.0    | 100%             | s2 – nu     | Nicio economie, totul cheltuit       |
| 4    | i2 – există        | **0.01** | <100% implicit   | s1 – da     | Se economisește suma minimă posibilă |

### Împărțire în categorii

Category Partitioning este o tehnică de testare care presupune împărțirea intrărilor în **categorii relevante**, fiecare având **alternative posibile**. Apoi, se combină alternativele reprezentative pentru a acoperi cât mai multe situații semnificative, fără a testa toate permutările posibile.

| Categorie                       | Alternativă | Descriere                                               |
| ------------------------------- | ----------- | ------------------------------------------------------- |
| `i` – existența contului        | `i1`        | Contul **nu există** în sistem                          |
|                                 | `i2`        | Contul **există** în sistem                             |
| `a` – valoarea sumei            | `a1`        | Sumă **negativă**                                       |
|                                 | `a2`        | Sumă **zero**                                           |
|                                 | `a3`        | Sumă **minim pozitivă** (ε, ex: `0.01`)                 |
|                                 | `a4`        | Sumă **medie** (ex: `100`)                              |
|                                 | `a5`        | Sumă **mare** (ex: `1.000.000`)                         |
| `p` – suma procentelor          | `p1`        | `0%` – fără conturi de cheltuieli                       |
|                                 | `p2`        | `1…99%` – cheltuieli parțiale                           |
|                                 | `p3`        | `100%` – toți banii sunt alocați                        |
|                                 | `p4`        | `>100%` – peste capacitatea de distribuție (caz exclus) |
| `s` – descriere                 | `s1`        | Descrierea **conține** cuvântul `"SAVE"`                |
|                                 | `s2`        | Descrierea **nu conține** `"SAVE"`                      |

### Tabel cazuri Category Partitioning (notații din cod)
| Case   | i  | a  | p  | s  | Rezultat         |
| ------ | -- | -- | -- | -- | ---------------- |
| Cat 1  | i1 | a4 | p1 | s2 | `ACCOUNT_ERR`    |
| Cat 2  | i2 | a1 | p1 | s2 | `AMOUNT_ERR`     |
| Cat 3  | i2 | a2 | p1 | s2 | `AMOUNT_ERR`     |
| Cat 4  | i2 | a3 | p1 | s1 | `ALL_SAVED`      |
| Cat 5  | i2 | a4 | p1 | s2 | `NO_SAVED`       |
| Cat 6  | i2 | a4 | p1 | s1 | `ALL_SAVED`      |
| Cat 7  | i2 | a5 | p1 | s1 | `ALL_SAVED`      |
| Cat 8  | i2 | a3 | p2 | s2 | `NO_SAVED`       |
| Cat 9  | i2 | a3 | p2 | s1 | `PARTIAL_SAVED`  |
| Cat 10 | i2 | a4 | p2 | s2 | `NO_SAVED`       |
| Cat 11 | i2 | a4 | p2 | s1 | `PARTIAL_SAVED`  |
| Cat 12 | i2 | a5 | p2 | s2 | `NO_SAVED`       |
| Cat 13 | i2 | a5 | p2 | s1 | `PARTIAL_SAVED`  |
| Cat 14 | i2 | a3 | p3 | s2 | `NO_SAVED`       |
| Cat 15 | i2 | a3 | p3 | s1 | `NO_SAVED`       |
| Cat 16 | i2 | a4 | p3 | s2 | `NO_SAVED`       |
| Cat 17 | i2 | a4 | p3 | s1 | `NO_SAVED`       |
| Cat 18 | i2 | a3 | p4 | s2 | `PERCENT_ERR`    |
| Cat 19 | i2 | a4 | p4 | s1 | `PERCENT_ERR`    |
| Cat 20 | i2 | a5 | p4 | s2 | `PERCENT_ERR`    |


#### Cause Effect Graph
![photo](https://github.com/user-attachments/assets/29a72f8b-185d-4427-bdfe-4c9d32cde8db)

|                                         | **R1** | **R2** | **R3** | **R4** | **R5** |
| :-------------------------------------- | :----: | :----: | :----: | :----: | :----: |
| **C1**<br/>(*Contul există în sistem*)  |    0   |    1   |    1   |    1   |    1   |
| **C2**<br/>(*Suma este pozitivă*)       |    1   |    0   |    1   |    1   |    1   |
| **C3**<br/>(*total % > 100 − EPS*)      |    0   |    0   |    1   |    0   |    0   |
| **C4**<br/>(„SAVE” în descriere)        |    0   |    0   |    1   |    0   |    1   |
| **E1** (ACCOUNT\_ERR)                   |    1   |    0   |    0   |    0   |    0   |
| **E2** (AMOUNT\_ERR)                    |    0   |    1   |    0   |    0   |    0   |
| **E3** („Se distribuie banii…”)         |    0   |    0   |    1   |    1   |    1   |
| **E4** („Fără economii…”)               |    0   |    0   |    1   |    1   |    0   |
| **E5** („Suma rămasă…”)                 |    0   |    0   |    0   |    0   |    1   |
| **E6** (Actualizare conturi cheltuieli) |    0   |    0   |    1   |    1   |    1   |
| **E7** (Actualizare economii)           |    0   |    0   |    0   |    0   |    1   |

## Structural Testing
### Control Flow Graph
<p align="center">
  <img src="https://github.com/user-attachments/assets/7e1fdc08-bb1b-49a1-b85b-62d099b3e328" alt="diagrama" />
</p>

### (a) Statement coverage

Statement coverage testing este o tehnică de testare software care verifică dacă fiecare linie (instrucțiune) din cod a fost executată cel puțin o dată în timpul testelor. Scopul este de a asigura că toate porțiunile codului sunt testate pentru a detecta eventualele erori. Aceasta este o metodă de testare white-box și oferă o măsură a acoperirii codului.

| accountId | amount | description       | spend % | instructions covered                                                      |
| --------- | ------ | ----------------- | ------- | ------------------------------------------------------------------------- |
| 404       | 100    | Invalid id        | -       | 1, 2..3, 42                                                               |
| 1         | -1     | Negative amount   | 90%     | 1, 4..5, 6, 7..8                                                          |
| 1         | 100    | SAVE THE EXTRA    | 90%     | 1, 4..5, 6, 9..15, 16, 17..19, 20, 21, 22..27, 28..30, 31, 34..40, 41, 42 |
| 1         | 100    | No saving         | 90%     | 1, 4..5, 6, 9..15, 16, 17..19, 20, 21, 22..27, 28..30, 31, 32..33, 41, 42 |

### (b) Decision coverage

Decision coverage testing, cunoscut și ca branch coverage, este o metodă de testare care verifică dacă toate deciziile logice (ramuri „true” și „false”) din cod sunt evaluate cel puțin o dată. Este mai cuprinzătoare decât statement coverage, deoarece se asigură că fiecare cale de decizie este testată. Această tehnică ajută la identificarea erorilor în logica condițională a programului.

### Decizii:
- (1) `if (!spendingAccounts.containsKey(accountId))`
- (2) `if (amount <= 0)`
- (3) `for (Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet())`
- (4) `for (Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet())`
- (5) `if (totalPercentage > 100 - EPS || !hasSaveFlag)`

| accountId | amount | description       | spend % | (1) | (2) | (3) | (4) | (5) |
| --------- | ------ | ----------------- | ------- | --- | --- | --- | --- | --- |
| 404       | 100    | Invalid id        | -       |  T  |  -  |  -  |  -  |  -  |
| 1         | -1     | Negative amount   | 90%     |  F  |  T  |  -  |  -  |  -  |
| 1         | 100    | No saving         | 90%     |  F  |  F  |  T  |  T  |  T  |
| 2         | 100    | SAVE              | 0%      |  F  |  F  |  F  |  F  |  F  |

### (c) Condition coverage

Condition coverage testing este o tehnică de testare care verifică dacă fiecare condiție booleană dintr-o expresie decizională a fost evaluată atât la „true”, cât și la „false”. Spre deosebire de decision coverage, care analizează rezultatul final al deciziei, condition coverage se concentrează pe fiecare sub-condiție individuală. Aceasta oferă un nivel mai detaliat de acoperire logică în testare.

### Condiții
- (c1) `!spendingAccounts.containsKey(accountId)`
- (c2) `amount <= 0`
- (c3) `Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet()`
- (c4) `Map.Entry<String, AbstractMap.SimpleEntry<Double, Double>> entry : spendingMap.entrySet()`
- (c5) `totalPercentage > 100 - EPS`
- (c6) `!hasSaveFlag`

| accountId | amount | description       | spend % | c1 | c2 | c3 | c4 | c5 | c6 |
| --------- | ------ | ----------------- | ------- | -- | -- | -- | -- | -- | -- |
| 404       | 100    | Invalid id        | -       | T  | -  | -  | -  | -  | -  |
| 1         | -1     | Negative amount   | 90%     | F  | T  | -  | -  | -  | -  |
| 1         | 100    | No saving         | 90%     | F  | F  | T  | T  | F  | T  |
| 2         | 100    | SAVE              | 0%      | F  | F  | F  | F  | F  | F  |
| 2         | 100    | No saving         | 100%    | F  | F  | T  | T  | T  | T  |
| 2         | 100    | SAVE              | 100%    | F  | F  | T  | T  | T  | F  |

### (g) Independent circuit testing

Independent circuit testing (sau independent path testing) este o tehnică de testare white-box care urmărește să identifice și să testeze toate căile independente prin codul sursă. O cale este considerată independentă dacă adaugă cel puțin o ramură nouă față de celelalte căi deja analizate. Scopul este de a maximiza acoperirea logică și de a detecta erori în structurile de control complexe.

**Informațiile grafului:**
- `n = 17`
- `e = 22`
- `V(G) = 6`

### Circuite

- (1) `1, 2..3, 42, 1`
- (2) `1, 4..5, 6, 7..8, 42, 1`
- (3) `16, 17..19, 16`
- (4) `21, 22..27, 21`
- (5) `1, 4..5, 6, 9-15, 16, 20, 21, 28..30, 31, 32..33, 41, 42, 1`
- (6) `1, 4..5, 6, 9-15, 16, 20, 21, 28..30, 31, 34..40, 41, 42, 1`
  
| accountId | amount | description       | spend % | circuite acoperite |
| --------- | ------ | ----------------- | ------- | ------------------ |
| 404       | 100    | Invalid id        | -       | (1)                |
| 1         | -1     | Negative amount   | 90%     | (2)                |
| 1         | 100    | No saving         | 90%     | (3), (4)           |
| 2         | 100    | SAVE              | 0%      | (6)                |
| 2         | 100    | No saving         | 0%      | (5)                |

## Mutation Testing

În cadrul acestor teste, au fost eliminați 2 mutanți care afectau mesajele `println` din metoda `distributeMoney`.

![image](https://github.com/user-attachments/assets/1ff1a3bb-f389-4703-b1d0-3bc186324a91)

| Test                                                | Scop                                                                  | Mutant eliminat                                            |
| --------------------------------------------------- | --------------------------------------------------------------------- | ---------------------------------------------------------- |
| `MutationTesting_1_testDistributeMoneyPrintMessage` | Verifică dacă mesajul `"Distributing..."` este afișat corect          | Mutant pe `System.out.println(...)` eliminat               |
| `MutationTesting_2_testSavingNumberChanged`         | Verifică dacă mesajul despre adăugarea la economii este afișat corect | Mutant pe `println` cu suma rămasă eliminat                |

## JaCoCo Run & Analysis

Toate metodele, liniile de cod și ramurile condiționale au fost acoperite de teste — adică testele verifică complet comportamentul clasei.

* **Acoperire clase:** 100% (1 din 1)
* **Acoperire metode:** 100% (6 din 6)
* **Acoperire linii:** 100% (44 din 44)
* **Acoperire ramuri (branch):** 100% (22 din 22)


![image](https://github.com/user-attachments/assets/fb6ac978-f360-4e06-ac8c-b2de041ef6b9)

