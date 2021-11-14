[![Java CI with Maven](https://github.com/kristiania-pgr203-2021/pgr203-exam-OrjanSkotnes/actions/workflows/maven.yml/badge.svg)](https://github.com/kristiania-pgr203-2021/pgr203-exam-OrjanSkotnes/actions/workflows/maven.yml)
# PGR203 Avansert Java eksamen


## Beskriv hvordan programmet skal testes:

## Sjekkliste

## Vedlegg: Sjekkliste for innlevering

* [x] Dere har lest eksamensteksten
* [ ] Dere har lastet opp en ZIP-fil med navn basert på navnet på deres Github repository
* [ ] Koden er sjekket inn på github.com/pgr203-2021-repository
* [ ] Dere har committed kode med begge prosjektdeltagernes GitHub konto (alternativt: README beskriver arbeidsform)

### TODO

* [ ] må beskrive erfaringene med arbeidet og løsningen
* [ ] Mer logging
* [ ] Fjerne død kode
* [ ] Legge til punkter vi vil bli vurdert på
* [ ] Logg error og respons 500 istedet for crash?
* [ ] Favicon?
* [ ] Filecontroller?
* [ ] Tester?
* [ ] Cookie?
 
### README.md

* [x] `README.md` inneholder en korrekt link til Github Actions
* [x] `README.md` beskriver prosjektets funksjonalitet, hvordan man bygger det og hvordan man kjører det
* [x] `README.md` beskriver eventuell ekstra leveranse utover minimum
* [x] `README.md` inneholder et diagram som viser datamodellen

### Koden

* [x] `mvn package` bygger en executable jar-fil
* [x] Koden inneholder et godt sett med tester
* [x] `java -jar target/...jar` (etter `mvn package`) lar bruker legge til og liste ut data fra databasen via webgrensesnitt
* [x] Serveren leser HTML-filer fra JAR-filen slik at den ikke er avhengig av å kjøre i samme directory som kildekoden
* [x] Programmet leser `dataSource.url`, `dataSource.username` og `dataSource.password` fra `pgr203.properties` for å connecte til databasen
* [x] Programmet bruker Flywaydb for å sette opp databaseskjema
* [x] Server skriver nyttige loggmeldinger, inkludert informasjon om hvilken URL den kjører på ved oppstart

### Funksjonalitet

* [x] Programmet kan opprette spørsmål og lagrer disse i databasen (påkrevd for bestått)
* [x] Programmet kan vise spørsmål (påkrevd for D)
* [x] Programmet kan legge til alternativ for spørsmål (påkrevd for D)
* [x] Programmet kan registrere svar på spørsmål (påkrevd for C)
* [x] Programmet kan endre tittel og tekst på et spørsmål (påkrevd for B)

### Ekstraspørsmål (dere må løse mange/noen av disse for å oppnå A/B)

* [ ] Før en bruker svarer på et spørsmål hadde det vært fint å la brukeren registrere navnet sitt. Klarer dere å implementere dette med cookies? Lag en form med en POST request der serveren sender tilbake Set-Cookie headeren. Browseren vil sende en Cookie header tilbake i alle requester. Bruk denne til å legge inn navnet på brukerens svar
* [x] Når brukeren utfører en POST hadde det vært fint å sende brukeren tilbake til dit de var før. Det krever at man svarer med response code 303 See other og headeren Location
* [x] Når brukeren skriver inn en tekst på norsk må man passe på å få encoding riktig. Klarer dere å lage en <form> med action=POST og encoding=UTF-8 som fungerer med norske tegn? Klarer dere å få æøå til å fungere i tester som gjør både POST og GET?
* [ ] Å opprette og liste spørsmål hadde vært logisk og REST-fult å gjøre med GET /api/questions og POST /api/questions. Klarer dere å endre måten dere hånderer controllers på slik at en GET og en POST request kan ha samme request target?
* [x] Vi har sett på hvordan å bruke AbstractDao for å få felles kode for retrieve og list. Kan dere bruke felles kode i AbstractDao for å unngå duplisering av inserts og updates?
* [ ] Dersom noe alvorlig galt skjer vil serveren krasje. Serveren burde i stedet logge dette og returnere en status code 500 til brukeren
* [x] Dersom brukeren går til http://localhost:8080 får man 404. Serveren burde i stedet returnere innholdet av index.html
* [x] Et favorittikon er et lite ikon som nettleseren viser i tab-vinduer for en webapplikasjon. Kan dere lage et favorittikon for deres server? Tips: ikonet er en binærfil og ikke en tekst og det går derfor ikke an å laste den inn i en StringBuilder
* [ ] I forelesningen har vi sett på å innføre begrepet Controllers for å organisere logikken i serveren. Unntaket fra det som håndteres med controllers er håndtering av filer på disk. Kan dere skrive om HttpServer til å bruke en FileController for å lese filer fra disk?
* [x] Kan dere lage noen diagrammer som illustrerer hvordan programmet deres virker?
* [x] JDBC koden fra forelesningen har en feil ved retrieve dersom id ikke finnes. Kan dere rette denne?
* [x] I forelesningen fikk vi en rar feil med CSS når vi hadde `<!DOCTYPE html>`. Grunnen til det er feil content-type. Klarer dere å fikse det slik at det fungerer å ha `<!DOCTYPE html>` på starten av alle HTML-filer?
* [ ] Klarer dere å lage en Coverage-rapport med GitHub Actions med Coveralls? (Advarsel: Foreleser har nylig opplevd feil med Coveralls så det er ikke sikkert dere får det til å virke)
* [ ] FARLIG: I løpet av kurset har HttpServer og tester fått funksjonalitet som ikke lenger er nødvendig. Klarer dere å fjerne alt som er overflødig nå uten å også fjerne kode som fortsatt har verdi? (Advarsel: Denne kan trekke ned dersom dere gjør det feil!)



# PGR203 - Exam - Questionnaires

## How to run this program
### Follow these steps to build an executable jar-file
* Open the program folder in IntelliJ
* Run `mvn package` in terminal. This command builds the jar-file.
* Make sure to make a `pgr203.properties` file, this file must be inside the root folder where the jar-file is stored.
* `pgr203.properties` must contain the following:
  * dataSource.url = jdbc:postgresql://localhost:5432/ <- name of your database
  * dataSource.user = the username to the owner of your database
  * dataSource.password = the password to the user. 

* To run the jar-file: `java -Dfile.encoding="UTF-8" -jar target/pgr203-exam-OrjanSkotnes-1.0-SNAPSHOT.jar`
* Server will start at: http://localhost:8080/index.html

####Your database will be inserted with dummy data, so you can test all the functionality in the app right away.

##Our work progress
During this assignment we have worked mostly physical at school. We built our applications using the pair-programming principals (test driven development).
We have worked with another during the mandatory assignments this semester, so we have a very good working relationship.

Before we started working on the project, we made our UML database diagram and implemented it into an actual database. Tested it with all references and with query's. Just to make sure the database worked as intended.
Because of this we had little issues with the database build, and the migration files was easy to implement. 

We used GitHub as version control, both are familiar with GitHub, and we had no problem with commits or pull requests during this assignment.

The server build process was built in a few days, this was quite familiar after all the assignments. After the server was built it was about time to test to a database.
In memory testing with h2.database worked out great. Creating the data-access-objects with the abstract dao-class was kinda new to us. But it worked out as intended, and removed duplicated code.

After testing all the dao-classes with h2. We made controllers to handle all request to our database, so our server-code got more compact and stable and now outsources all request to controllers. 
This worked out as planned.


###Afterthoughts


All in all, we are happy with our results in this project. 



## HttpServer design 
![](doc/httpServer.png)
Our HttpServer is designed with controllers witch is controlling the DAO's. We have implemented controllers for every action where we access data from the database. In the diagram above you'll see how the server works when you add a new questionnaire to the database. 

## Database design
![](doc/questionnaireDb.png)
We used four tables in our project. All questions refer to a questionnaire, and all options refers to questions.
The answer table contains all metadata with the answer value (1-5).

We implemented DELETE CASCADE. Then the user could delete a questionnaire and all the related questions and options would also be deleted.
The same goes to if you delete a question, it will delete the referring options tied to the question.

## DAO design
![](doc/dao.png)
We started creating DAO's to each entity, and realized early they all have the same methods, more or less. So we implemented the AbstractDao class.
Every dao extends the AbstractDao, so all the CRUD methods are inherited. This makes the dao methods less redundant, and it worked out great for us.

## Extra functionality on our application
* Fixed if user goes to "/" root issue.
* Used abstract dao-class to avoid duplicated crud-code.
* Fixed the content-type issue we had in class with CSS.
* Fixed encoding. All characters work.
* The user gets redirected after a POST.
* Diagrams for HttpServer, DAO classes and database are made.
* Server reads favicon.
* The application lets you add multiple questionnaires.
  * A set of questions can be tied to one questionnaire, and a set of options can be set to a single question.
  * Scaling on every option
  * Add questions, add options.
  * Edit every entity.
  * Delete cascade on questionnaire. All tied questions and options will also ble deleted. NB: this is optional. You can also delete a single question and all options tied to it will be deleted.
  * Lists out a summary of answers tied to a questionnaire.
* Good use of DAO-pattern and controllers to handle them.



