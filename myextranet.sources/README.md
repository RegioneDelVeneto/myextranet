MyExtranet Sources

Getting started
---------------


Per eseguire il progetto, eseguire dalla directory principale `./gradlew bootRun` (Unix/Linux) or `gradlew.bat bootRun` (Windows).

Sono presenti servizi di esempio che ritornano dati cablati, e non c'è alcun riferimento ad un DataBase.

Lo swagger risponde all'url http://localhost:8080/swagger-ui.html


Docker
---------------

Qui di seguito i passi per eseguire il progetto su una semplice immagine docker con openjdk11

 - Eseguire la compilazione del progetto con `./gradlew bootJar`

 - Creare immagine con `docker build -t myextranet .`

 - Eseguire immagine con `docker run -p 8080:8080 myextranet`
