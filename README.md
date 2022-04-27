
# MyExtranet


## INTRODUZIONE

Oltre al portale informativo pubblico (gestito e pubblicato dal modulo MyCMS di MyPortal) la MyExtranet ha ulteriori 2 aree:

 - l’ area privata del portale “MyExtranet”  messa a disposizione degli operatori degli Enti e delle Aziende, allo scopo di gestire
	 - la partecipazione ad Eventi
	 - la collaborazione nei Progetti e Gruppi di Lavoro
	 - l’attivazione di Prodotti per il proprio Ente o Azienda 
 - l'area amministrativa messa a disposizione degli operatori amministratori
	 - gestione Utenti
	 - gestione Enti
	 - gestione Eventi
	 - gestione Progetti e Gruppi di lavoro
	 - gestione Prodotti


## STRUTTURA DEL REPOSITORY
Il repository git di MyExtranet ha le seguente struttura:

**/myextranet.sources**: E’ la cartella che contiene i sorgenti e gli script gradle per la compilazione la creazione dell’immagine e la pubblicazione sul Repository Nexus.

**/myextranet.deploy:** E’ la cartella che contiene i descrittori di base per il dispiegamento su kubernets e gli overlay specifici per ogni ambiente target di deploy.


## I SORGENTI
La cartella **myextranet.sources** contiene i sorgenti dell'applicazione che eroga sia l'area privata del portale sia l'area amministrativa  ed è così strutturata:

`myextranet-be`: contiene i sorgenti del back-end 
`myextranet-fe`: contiene i sorgenti del front-end


## Esecuzione in modalità standalone
Per l'esecuzione in modalità standalone è necessario generare il file jar dopo aver aggiornato i file di confugurazione

### Prerequisiti servizi MyPlace e verticali MyP3
La soluzione MyExtranet ha dipendenza verso alcuni servizi della piattaforma MyPlace:

- **MyId:** per al gestione dell’autenticazione
- **MyProfile:** per la gestione delle autorizzazioni e dei profili
- **MyConfig:** per la gestione dei parametri di configurazione applicativi
- **MySysConfig:** per la gestione dei parametri di configurazione di sistema
- **MyChannel:** per l’invio dei messaggi via email
- **MyReportGenerator:** per la produzione di report

E’ richiesta inoltre la comunicazione verso i componenti:

- myportal-ro
- myportal-rw

per l’ente MyPortal dedicato all’applicazione MyExtranet.

### Configurazione
Per poter eseguire lo start dell’applicazione, è importante inserire i puntamenti dei propri ambienti per i componenti da cui dipende MyExtranet.
Se si vuole far partire l’applicazione nei propri ambienti, è necessario aggiungere le seguenti configurazioni:

- `myextranet-be/src/main/resources/application.yml`
	- spring.datasource: url e credenziali di connessione al db postgres
- `myextranet-be/src/main/resources/application-default.yml`
	- server.ssl.*: se si vuole abilitare HTTPS per Spring Boot
	- auth.fake.enabled: se il parametro è a true, si esegue un’autenticazione fake senza invocare MyID
	- saml.proxy.*: per la connessione a MyID, se si è in presenza di Load Balancer o Reverse Proxy
	- saml.key-*: keystore con la chiave per firmare asserzioni SAML
	- saml.app-entity-id: Nome dell'applicazione nella configurazione per l'IDP
	- saml.app-base-url: URL dell'applicazione da fornire nel processo di generazione dei metadati per MyID
	- saml.idp-metadata-url: URL da cui ricavare i metadata IDP
	- jwt.secret: chiave con cui firmare i JWT
	- myChannel: url di MyChannel
	- myBox: impostare il nome del bucket
	- myprofile: impostare i vari url di MyProfile
	- myIntranet: impostare baseUrl della MyIntranet
	- myConfig: impostare url di MyConfig
	- mySysConfig: impostare url di MySysConfig
	- logstash.*: riferimenti per contattare logstash
	- cache.sentinel.*: Riferimenti di Redis se si usa la modalità sentinel
	- cache.standalone.*: Riferimenti di Redis se si usa la modalità normale
- `myextranet-be/src/main/resources/mybox.properties`
	- s3.*: indicare i riferimenti a s3 se si usa s3 come Object Store
	- swift.*: indicare i riferimenti a SWIFT se si usa SWIFT come Object Store

### Creazione del file jar
Per eseguire la compilazione dei sorgenti e l’esecuzione degli stessi in un unico comando eseguire il comando

    ./gradlew bootJar

che genera il file jar nella cartella /lib.
Nel caso di ambiente Microsoft Windows, eseguire il comando

    gradlew.bat bootRun

### Esecuzione dell'applicazione
Per mettere in esecuzione l’applicativo, posizionarsi nella directory dove l’artefatto è stato generato, ed eseguire il comando di

    java -jar <nome artefatto>.jar

L'applicazione è raggiungibile ad indirizzi del tipo:

 - https://localhost/myextranet/backoffice (area amministratori)
 - https://localhost/myextranet/utente (area privata)

## Esecuzione su cluster kubernetes
Per l'esecuzione su cluster kubernetes si rmanda al manuale di istallazione presente sotto documentazione.



