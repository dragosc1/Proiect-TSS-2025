# Proiect-TSS-2025

Membrii echipă:
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

#### Structural Testing 
Structural testing (sau white-box testing) se concentrează pe analiza internă a structurii codului sursă. Testele sunt proiectate pe baza cunoașterii implementării interne, verificând fluxurile de control și logica internă. Această abordare ajută la identificarea erorilor în structura și logica internă a codului.

#### Mutation Testing 
Mutation testing presupune introducerea intenționată a unor modificări minore (mutanți) în codul sursă pentru a evalua eficacitatea suitei de teste. Scopul este de a determina dacă testele existente pot detecta aceste modificări, evidențiind astfel eventuale lacune în acoperirea testelor.

### 3. [Analiza Aplicațiilor Existente](https://github.com/apache/commons-lang)
Un exemplu concret este <b>Apache Commons Lang.</b> Acest proiect open source, parte din Apache Commons, folosește:

* Maven pentru gestionarea dependențelor și automatizarea build-ului,

* JUnit ca framework principal pentru testarea unitară,

* JaCoCo pentru măsurarea acoperirii codului de către teste,

* și, în unele cazuri, PIT pentru mutation testing, evaluând astfel eficacitatea suitei de teste.

Avantaje
* Automatizare și Standardizare:
Utilizarea Maven permite automatizarea procesului de build și standardizarea structurii proiectului, facilitând gestionarea dependențelor și integrarea continuă.

* Testare Eficientă și Ușor de Implementat:
JUnit oferă un cadru robust pentru scrierea și rularea testelor unitare, simplificând validarea comportamentului codului.

* Evaluarea Calității Codului:
JaCoCo oferă rapoarte detaliate privind acoperirea codului, ajutând la identificarea zonelor care necesită teste suplimentare și la monitorizarea îmbunătățirii suitei de teste în timp.

* Calitatea Suitei de Teste:
PIT, prin mutation testing, permite evaluarea eficienței testelor, identificând eventualele lacune prin simularea unor defecte minore în cod.

* Comunitate și Suport:
Aceste instrumente sunt bine-cunoscute și folosite pe scară largă în ecosistemul Java, beneficiind de documentație extinsă și suport din partea comunității.

Dezavantaje
* Complexitate în Configurare:
Configurarea inițială a Maven, în special pentru proiecte mari sau foarte customizate, poate deveni complexă și necesită o înțelegere aprofundată a structurii proiectului.

* Resurse și Timp de Execuție:
Utilizarea mutation testing cu PIT poate consuma resurse semnificative și poate prelungi timpul de execuție al testelor, fiind o provocare în proiectele de anvergură mare.

* Limitări în Testarea Scenariilor Complexe:
Deși JUnit este excelent pentru testele unitare simple, gestionarea testelor pentru scenarii complexe (care implică interacțiuni cu componente externe sau baze de date) poate necesita integrarea unor librării suplimentare, cum ar fi framework-uri de mocking.

* Rapoarte Detaliate Necesită Interpretare:
Rapoartele generate de JaCoCo pot fi detaliate și necesită o analiză atentă pentru a extrage informațiile relevante, ceea ce poate implica un efort suplimentar din partea echipei.

### 4. Servicii Disponibile
- **Maven:**  
  Instrument de build și management al dependențelor pentru proiectele Java.  
  [Setup Maven](https://maven.apache.org/install.html)

- **JUnit:**  
  Framework popular pentru scrierea și rularea testelor unitare în Java.  
  [Setup JUnit](https://junit.org/junit5/docs/current/user-guide/)

- **JaCoCo:**  
  Instrument pentru măsurarea acoperirii codului de către teste.  
  [Setup JaCoCo](https://www.jacoco.org/jacoco/trunk/doc/)
  * Run: mvn clean test

- **PIT:**  
  Instrument de mutation testing folosit pentru evaluarea eficienței suitei de teste.  
  [Setup PIT](https://pitest.org/quickstart/)
  * Run: mvn clean test pitest:mutationCoverage




