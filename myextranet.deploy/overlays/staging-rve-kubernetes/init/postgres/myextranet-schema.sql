
CREATE TABLE public.MYEXT_PRODOTTO_TIPO_RICH (
                COD_TIPO_RICH VARCHAR(15) NOT NULL,
                DES_TIPO_RICH VARCHAR(100) NOT NULL,
                FLG_ATTIVA_PRODOTTO INTEGER,
                CONSTRAINT myext_prodotto_tipo_rich_pk PRIMARY KEY (COD_TIPO_RICH)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_TIPO_RICH.COD_TIPO_RICH IS 'Codice della tipologia di richiesta (ATT-PRD=Attivazione, UPD-PRD= Aggiornamento utenti)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_TIPO_RICH.DES_TIPO_RICH IS 'Descrizione della tipologia di richiesta';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_TIPO_RICH.FLG_ATTIVA_PRODOTTO IS 'Identifica se il tipo richiesta preveda, una volta terminata con successo, l''attivazione del prodotto (=1), la disattivazione del prodotto (=2) o nessuna azione (=0)';


CREATE SEQUENCE public.myext_main_sequence;

CREATE TABLE public.MYEXT_ENTE_CATEGORIA (
                ID_CATEGORIA INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                COD_CATEGORIA VARCHAR(30) NOT NULL,
                DES_CATEGORIA VARCHAR(100) NOT NULL,
                CONSTRAINT myext_ente_categoria_pk PRIMARY KEY (ID_CATEGORIA)
);
COMMENT ON COLUMN public.MYEXT_ENTE_CATEGORIA.ID_CATEGORIA IS 'Identificativo della categoria';
COMMENT ON COLUMN public.MYEXT_ENTE_CATEGORIA.COD_CATEGORIA IS 'Codice della categoria';
COMMENT ON COLUMN public.MYEXT_ENTE_CATEGORIA.DES_CATEGORIA IS 'Descrizione della categoria';


CREATE TABLE public.MYEXT_PRODOTTO_STATO_RICH (
                ID_STATO_RICH INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                TIPO_RICH VARCHAR(15) NOT NULL,
                COD_STATO_RICH VARCHAR(100) NOT NULL,
                DES_STATO_RICH VARCHAR NOT NULL,
                NUM_STATO INTEGER NOT NULL,
                COMPETENZA VARCHAR(5) NOT NULL,
                FINE_RICH INTEGER NOT NULL,
                CONSTRAINT myext_prodotto_stato_rich_pk PRIMARY KEY (ID_STATO_RICH)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_STATO_RICH.ID_STATO_RICH IS 'Identificativo dello stato della richiesta';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_STATO_RICH.TIPO_RICH IS 'Tipologia di richiesta (ATT-PRD=Attivazione, UPD_USR= Aggiornamento utenti)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_STATO_RICH.COD_STATO_RICH IS 'Codice dello stato della richiesta';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_STATO_RICH.DES_STATO_RICH IS 'Descrizione dello stato della richiesta';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_STATO_RICH.NUM_STATO IS 'Numero d''ordine dello stato del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_STATO_RICH.COMPETENZA IS 'Ruolo di competenza (RV=Regione Veneto, EN=Ente)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_STATO_RICH.FINE_RICH IS 'Identifica se lo stato e'' di fine richiesta con esito positivo (=1), di fine richiesta con esito negativo (=-1) o e'' di richiesta in corso (=0)';


CREATE TABLE public.MYEXT_PRODOTTO_RUOLI (
                COD_RUOLO VARCHAR(15) NOT NULL,
                DES_RUOLO VARCHAR(100) NOT NULL,
                NUM_RUOLO INTEGER NOT NULL,
                CONSTRAINT myext_prodotto_ruoli_pk PRIMARY KEY (COD_RUOLO)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RUOLI.COD_RUOLO IS 'Codice del ruolo';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RUOLI.DES_RUOLO IS 'Descrizione del ruolo';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RUOLI.NUM_RUOLO IS 'Numero d''ordine del ruolo';


CREATE TABLE public.MYEXT_PRODOTTO_ATTIV_GRUPPO (
                ID_PROD_GRUPPO INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                NOME_GRUPPO VARCHAR NOT NULL,
                CONSTRAINT myext_prodotto_attiv_gruppo_pk PRIMARY KEY (ID_PROD_GRUPPO)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_GRUPPO.ID_PROD_GRUPPO IS 'Identificativo del gruppo di prodotti';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_GRUPPO.NOME_GRUPPO IS 'Nome del gruppo';

CREATE TABLE public.MYEXT_PRODOTTO_ATTIVABILE (
                ID_PRODOTTO_ATT INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_PRODOTTO_ATT_PROP INTEGER,
                ID_PRODOTTO VARCHAR(250),
                ID_PROD_GRUPPO INTEGER,
                NOME_PRODOTTO_ATTIV VARCHAR(150) NOT NULL,
                DES_ATTIVAZIONE VARCHAR(4000),
                DES_ATTIVAZIONE_BREVE VARCHAR(250),
                DT_ATTIVABILE_DA DATE NOT NULL,
                DT_ATTIVABILE_A DATE NOT NULL,
                COD_APP_PROF_MAN VARCHAR(20),
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_prodotto_attivabile_pk PRIMARY KEY (ID_PRODOTTO_ATT)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.ID_PRODOTTO_ATT IS 'Identificativo del prodotto attivabile';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.ID_PRODOTTO_ATT_PROP IS 'Identificativo del prodotto attivabile propedeutico';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.ID_PRODOTTO IS 'Identificativo del prodotto attivabile';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.ID_PROD_GRUPPO IS 'Identificativo del gruppo di prodotti';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.NOME_PRODOTTO_ATTIV IS 'Nome del prodotto attivabile';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.DES_ATTIVAZIONE IS 'Descrizione estesa dell''attivazione prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.DES_ATTIVAZIONE_BREVE IS 'Descrizione breve dell''attivazione prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.DT_ATTIVABILE_DA IS 'Data a partire da quando è attivabile il prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.DT_ATTIVABILE_A IS 'Data fino a quando è attivabile il prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.COD_APP_PROF_MAN IS 'Codice dell''applicazione nel profile manager';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVABILE.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';

CREATE TABLE public.MYEXT_PRODOTTO_PROC (
                ID_PRODOTTO_PROC INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_PRODOTTO_ATT INTEGER NOT NULL,
                NUM_VERSIONE INTEGER NOT NULL,
                COD_TIPO_RICH VARCHAR(15) NOT NULL,
                DES_PRODOTTO_PROC VARCHAR(100) NOT NULL,
                DES_PRODOTTO_PROC_ESTESA VARCHAR(2000),
                FLG_ENABLED INTEGER NOT NULL,
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_prodotto_proc_pk PRIMARY KEY (ID_PRODOTTO_PROC)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.ID_PRODOTTO_PROC IS 'Identificativo della procedura ri richiesta sul prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.ID_PRODOTTO_ATT IS 'Identificativo del prodotto attivabile';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.NUM_VERSIONE IS 'Numero di versione della configurazione del processo di attivazione';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.COD_TIPO_RICH IS 'Codice della tipologia di richiesta (ATT-PRD=Attivazione, UPD-PRD= Aggiornamento utenti)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.DES_PRODOTTO_PROC IS 'Descrizione della procedura ri richiesta sul prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.DES_PRODOTTO_PROC_ESTESA IS 'Descrizione estesa della procedura ri richiesta sul prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.FLG_ENABLED IS 'Flag che evidenzia se la richiesta è attiva (=1) oppure no (=0)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';

CREATE TABLE public.MYEXT_PRODOTTO_PROC_STEP (
                ID_STATO_CONF INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_PRODOTTO_PROC INTEGER NOT NULL,
                NUM_STEP INTEGER NOT NULL,
                COD_STATO VARCHAR(100) NOT NULL,
                DES_STATO VARCHAR NOT NULL,
                COMPETENZA VARCHAR(5) NOT NULL,
                FLG_AGGIORNA_UTENTI INTEGER,
                FLG_FINE_RICH INTEGER NOT NULL,
                AZIONI VARCHAR,
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_prodotto_proc_step_pk PRIMARY KEY (ID_STATO_CONF)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.ID_STATO_CONF IS 'Identificativo dello step configurato';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.ID_PRODOTTO_PROC IS 'Identificativo della procedura ri richiesta sul prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.NUM_STEP IS 'Numero ordine dello step nella procedura';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.COD_STATO IS 'Codice dello stato';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.DES_STATO IS 'Descrizione dello stato dello step';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.COMPETENZA IS 'Ruolo di competenza (RV=Regione Veneto, EN=Ente)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.FLG_AGGIORNA_UTENTI IS 'Flag che identifica se in qiesto stato sia possibile inserire/aggiornare i dati degli utenti (1=Si, 0=No)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.FLG_FINE_RICH IS 'Identifica se lo stato e'' di richiesta in corso (=0), di fine richiesta con esito positivo (=1) o di fine richiesta con esito negativo (=2)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.AZIONI IS 'Contiene la configrazione delle azioni previste nello step di richiesta (per esempio notifiche, allegati)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_PROC_STEP.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';


CREATE TABLE public.MYEXT_PROVINCIA (
                COD_PROVINCIA VARCHAR(3) NOT NULL,
                DES_PROVINCIA VARCHAR(100) NOT NULL,
                CONSTRAINT myext_provincia_pk PRIMARY KEY (COD_PROVINCIA)
);
COMMENT ON COLUMN public.MYEXT_PROVINCIA.COD_PROVINCIA IS 'Codice della provincia';
COMMENT ON COLUMN public.MYEXT_PROVINCIA.DES_PROVINCIA IS 'Descrizione della provincia';


CREATE TABLE public.MYEXT_COMUNE (
                COD_COMUNE VARCHAR(10) NOT NULL,
                COD_PROVINCIA VARCHAR(3) NOT NULL,
                DES_COMUNE VARCHAR(255) NOT NULL,
                COD_CATASTALE VARCHAR(5),
                GEO_LAT VARCHAR(30),
                GEO_LONG VARCHAR(30),
                CONSTRAINT myext_comune_pk PRIMARY KEY (COD_COMUNE)
);
COMMENT ON COLUMN public.MYEXT_COMUNE.COD_COMUNE IS 'Codice del comune';
COMMENT ON COLUMN public.MYEXT_COMUNE.COD_PROVINCIA IS 'Codice della provincia';
COMMENT ON COLUMN public.MYEXT_COMUNE.DES_COMUNE IS 'Denominazione del comune';
COMMENT ON COLUMN public.MYEXT_COMUNE.COD_CATASTALE IS 'Codice catastale del comune';
COMMENT ON COLUMN public.MYEXT_COMUNE.GEO_LAT IS 'Latitudine del centro del comune';
COMMENT ON COLUMN public.MYEXT_COMUNE.GEO_LONG IS 'Longitudine del centro del comune';


CREATE TABLE public.MYEXT_MESSAGGI (
                ID_MESSAGGIO INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                TIPO VARCHAR(3) NOT NULL,
                AREA VARCHAR(3) NOT NULL,
                ID_CONTENUTO VARCHAR(250),
                DESTINATARIO VARCHAR(250) NOT NULL,
                INDIRIZZO VARCHAR NOT NULL,
                TITOLO VARCHAR(150) NOT NULL,
                MESSAGGIO VARCHAR NOT NULL,
                DT_INVIO DATE NOT NULL,
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_messaggi_pk PRIMARY KEY (ID_MESSAGGIO)
);
COMMENT ON COLUMN public.MYEXT_MESSAGGI.ID_MESSAGGIO IS 'Identificativo del messaggio';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.TIPO IS 'Tipologia del messaggio (NOT=Notifica, COM=Comunicazione, INV=Invito)';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.AREA IS 'Area del messaggio (EVT=Evento, PRG=Progetto, REG=Registrazione, UT=Utente, PRD=Prodotto)';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.ID_CONTENUTO IS 'Identificativo del contenuto oggetto del messaggio';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.DESTINATARIO IS 'Destinatario del messaggio';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.INDIRIZZO IS 'Indirizzo email dell''utente invitato';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.TITOLO IS 'Titolo dell''invito';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.MESSAGGIO IS 'Messaggio dell''invito';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.DT_INVIO IS 'Data di invio dell''invito';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.DT_INS IS 'Istante di inserimento';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_MESSAGGI.DT_ULT_MOD IS 'Istante in cui è stata eseguita l''ultima modifica';

CREATE TABLE public.MYEXT_ENTE (
                ID_ENTE INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_CATEGORIA INTEGER NOT NULL,
                COD_IPA VARCHAR(15) NOT NULL,
                DENOMINAZIONE VARCHAR(250) NOT NULL,
                LOGO VARCHAR(250) NOT NULL,
                COD_COMUNE VARCHAR(10) NOT NULL,
                INDIRIZZO VARCHAR(500) NOT NULL,
                CAP VARCHAR(10) NOT NULL,
                EMAIL VARCHAR(100) NOT NULL,
                PEC VARCHAR(100) NOT NULL,
                COD_FISCALE VARCHAR(16) NOT NULL,
                TELEFONO VARCHAR(30),
                FAX VARCHAR(30),
                URL_WEB_SITE VARCHAR(500),
                URL_FACEBOOK VARCHAR(250),
                URL_INSTAGRAM VARCHAR(250),
                URL_TWITTER VARCHAR(250),
                ID_ENTE_UNIONE INTEGER,
                DT_INIZIO_VAL DATE NOT NULL,
                DT_FINE_VAL DATE NOT NULL,
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_ente_pk PRIMARY KEY (ID_ENTE)
);
COMMENT ON COLUMN public.MYEXT_ENTE.ID_ENTE IS 'Identificativo dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE.ID_CATEGORIA IS 'Identificativo della categoria';
COMMENT ON COLUMN public.MYEXT_ENTE.COD_IPA IS 'Codice ipa dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE.DENOMINAZIONE IS 'Denominazione dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE.LOGO IS 'Riferimento all''Immagine del logo';
COMMENT ON COLUMN public.MYEXT_ENTE.COD_COMUNE IS 'Codice del comune';
COMMENT ON COLUMN public.MYEXT_ENTE.INDIRIZZO IS 'Indirizzo della sede dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE.CAP IS 'CAP del comune della sede';
COMMENT ON COLUMN public.MYEXT_ENTE.EMAIL IS 'Indirizzo email dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE.PEC IS 'Indirizzo pec dell''nte';
COMMENT ON COLUMN public.MYEXT_ENTE.COD_FISCALE IS 'Codice fiscale dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE.TELEFONO IS 'Telefono dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE.FAX IS 'Fax dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE.URL_WEB_SITE IS 'Indirizzo sito web';
COMMENT ON COLUMN public.MYEXT_ENTE.URL_FACEBOOK IS 'Indirizzo facebook';
COMMENT ON COLUMN public.MYEXT_ENTE.URL_INSTAGRAM IS 'Indirizzo Instagram';
COMMENT ON COLUMN public.MYEXT_ENTE.URL_TWITTER IS 'Indirizzo twitter';
COMMENT ON COLUMN public.MYEXT_ENTE.ID_ENTE_UNIONE IS 'Identificativo dell''ente unione di comuni';
COMMENT ON COLUMN public.MYEXT_ENTE.DT_INIZIO_VAL IS 'Data di inizio validita'' dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE.DT_FINE_VAL IS 'Data di fine validita'' dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_ENTE.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_ENTE.DT_INS IS 'Istante di inserimento';
COMMENT ON COLUMN public.MYEXT_ENTE.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_ENTE.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_ENTE.DT_ULT_MOD IS 'Istante in cui è stata eseguita l''ultima modifica';

CREATE TABLE public.MYEXT_PRODOTTO_ATTIVATO (
                ID_ATTIVAZIONE INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_PRODOTTO_ATT INTEGER NOT NULL,
                ID_ENTE INTEGER NOT NULL,
                DT_INIZIO_VAL DATE,
                DT_FINE_VAL DATE,
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_prodotto_attivato_pk PRIMARY KEY (ID_ATTIVAZIONE)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.ID_ATTIVAZIONE IS 'Identificativo dell''attivazione';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.ID_PRODOTTO_ATT IS 'Identificativo del prodotto attivabile';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.ID_ENTE IS 'Identificativo dell''ente';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.DT_INIZIO_VAL IS 'Data di inizio validita''';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.DT_FINE_VAL IS 'Data di fine validita''';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIVATO.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';


CREATE UNIQUE INDEX myext_prodotto_attivato_unq_idx
 ON public.MYEXT_PRODOTTO_ATTIVATO
 ( ID_PRODOTTO_ATT, ID_ENTE );

CREATE TABLE public.MYEXT_PRODOTTO_RICH (
                ID_PROD_ATTIV_RICH INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_ATTIVAZIONE INTEGER NOT NULL,
                ID_PRODOTTO_PROC INTEGER NOT NULL,
                NUM_VERSIONE INTEGER NOT NULL,
                DT_RICH DATE NOT NULL,
                COD_STATO VARCHAR(100) NOT NULL,
                ULTIME_NOTE VARCHAR,
                FLG_FINE_RICH INTEGER NOT NULL,
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_prodotto_rich_pk PRIMARY KEY (ID_PROD_ATTIV_RICH)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.ID_PROD_ATTIV_RICH IS 'Identificativo della richiesta di attivazione';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.ID_ATTIVAZIONE IS 'Identificativo dell''attivazione';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.ID_PRODOTTO_PROC IS 'Identificativo della procedura ri richiesta sul prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.NUM_VERSIONE IS 'Numero di versione della configurazione';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.DT_RICH IS 'Data della richiesta';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.COD_STATO IS 'Codice dello stato';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.ULTIME_NOTE IS 'Note inserite all''ultimo cambio di stato';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.FLG_FINE_RICH IS 'Identifica se lo stato e'' di fine richiesta con esito positivo (=1), di fine richiesta con esito negativo (=-1) o e'' di richiesta in corso (=0)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';


CREATE TABLE public.MYEXT_PRODOTTO_RICH_STEP (
                ID_STEP INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_PROD_ATTIV_RICH INTEGER NOT NULL,
                DT_STEP TIMESTAMP NOT NULL,
                ESECUTORE VARCHAR(5) NOT NULL,
                COD_STATO VARCHAR(100) NOT NULL,
                ESITO_STEP INTEGER,
                NOTE_STEP VARCHAR(500),
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_prodotto_rich_step_pk PRIMARY KEY (ID_STEP)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.ID_STEP IS 'Identificativo dell step';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.ID_PROD_ATTIV_RICH IS 'Identificativo della richiesta di attivazione';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.DT_STEP IS 'Data e ora di esecuzione dello step';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.ESECUTORE IS 'Ruolo di competenza (RV=Regione Veneto, EN=Ente)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.COD_STATO IS 'Codice dello stato della richiesta';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.ESITO_STEP IS 'Esito dell''esecuzione dello step (0=KO, 1=OK)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.NOTE_STEP IS 'Note dello step';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_STEP.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';

CREATE TABLE public.MYEXT_PRODOTTO_RICH_DOCS (
                ID_PROD_RICH_DOC INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_PROD_ATTIV_RICH INTEGER NOT NULL,
                ID_STEP INTEGER NOT NULL,
                ID_DOCUMENTO VARCHAR(250) NOT NULL,
                NOME_DOCUMENTO VARCHAR(150) NOT NULL,
                FLG_ENABLED INTEGER NOT NULL,
                CONSTRAINT myext_prodotto_rich_docs_pk PRIMARY KEY (ID_PROD_RICH_DOC)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_DOCS.ID_PROD_RICH_DOC IS 'Identificativo del documento allegato alla richiesta';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_DOCS.ID_PROD_ATTIV_RICH IS 'Identificativo della richiesta di attivazione';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_DOCS.ID_STEP IS 'Identificativo dell step';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_DOCS.ID_DOCUMENTO IS 'Identificatico del documento';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_DOCS.NOME_DOCUMENTO IS 'Nome del documento';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_DOCS.FLG_ENABLED IS 'Flag che identifica se il documento è attivo (=1) oppure no (=0)';


CREATE TABLE public.MYEXT_UTENTE (
                ID_UTENTE INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                NOME VARCHAR(150) NOT NULL,
                COGNOME VARCHAR(150) NOT NULL,
                COD_FISCALE VARCHAR(16) NOT NULL,
                EMAIL VARCHAR(150) NOT NULL,
                ID_ENTE INTEGER,
                TELEFONO VARCHAR(25),
                TELEFONO_UFF VARCHAR(25),
                AZIENDA VARCHAR(250),
                ID_UTENTE_PADRE INTEGER,
                FLG_ARCHIVED INTEGER NOT NULL,
                DT_INIZIO_VAL DATE NOT NULL,
                DT_FINE_VAL DATE NOT NULL,
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_utente_pk PRIMARY KEY (ID_UTENTE)
);
COMMENT ON COLUMN public.MYEXT_UTENTE.ID_UTENTE IS 'Identificativo dell''utente';
COMMENT ON COLUMN public.MYEXT_UTENTE.NOME IS 'Nome dell''utente';
COMMENT ON COLUMN public.MYEXT_UTENTE.COGNOME IS 'Cognome dell''utente';
COMMENT ON COLUMN public.MYEXT_UTENTE.EMAIL IS 'Indirizzo email dell''utente';
COMMENT ON COLUMN public.MYEXT_UTENTE.ID_ENTE IS 'Identificativo dell''ente';
COMMENT ON COLUMN public.MYEXT_UTENTE.TELEFONO IS 'Numero di telefono dell''utente';
COMMENT ON COLUMN public.MYEXT_UTENTE.TELEFONO_UFF IS 'Telefono dell''ufficio';
COMMENT ON COLUMN public.MYEXT_UTENTE.AZIENDA IS 'Denominazione dell''azienda dell''utente';
COMMENT ON COLUMN public.MYEXT_UTENTE.ID_UTENTE_PADRE IS 'Identificativo dell''utente padre iniziale';
COMMENT ON COLUMN public.MYEXT_UTENTE.FLG_ARCHIVED IS 'Identifica se rappresenta la soricizzazione di un utente (1=SI, 0=NO)';
COMMENT ON COLUMN public.MYEXT_UTENTE.DT_INIZIO_VAL IS 'Data di inizio validita''';
COMMENT ON COLUMN public.MYEXT_UTENTE.DT_FINE_VAL IS 'Data di fine validita''';
COMMENT ON COLUMN public.MYEXT_UTENTE.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_UTENTE.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_UTENTE.DT_INS IS 'Istante di inserimento';
COMMENT ON COLUMN public.MYEXT_UTENTE.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_UTENTE.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_UTENTE.DT_ULT_MOD IS 'Istante in cui è stata eseguita l''ultima modifica';


CREATE TABLE public.MYEXT_PRODOTTO_ATTIV_UTENTI (
                ID_UTENTE_PROD INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_ATTIVAZIONE INTEGER NOT NULL,
                COD_RUOLO VARCHAR(15) NOT NULL,
                FLG_ENABLED INTEGER NOT NULL,
                ID_UTENTE INTEGER,
                COD_FISCALE VARCHAR(16) NOT NULL,
                NOME VARCHAR(150),
                COGNOME VARCHAR(150),
                EMAIL VARCHAR(150),
                TELEFONO VARCHAR(30),
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_prodotto_attiv_utenti_pk PRIMARY KEY (ID_UTENTE_PROD)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.ID_UTENTE_PROD IS 'Identificativo dell''utente del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.ID_ATTIVAZIONE IS 'Identificativo dell''attivazione';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.COD_RUOLO IS 'Codice del ruolo';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.FLG_ENABLED IS 'Flag che identifica se l''utente e'' abilitato (=1) oppure no (=0)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.ID_UTENTE IS 'Identificativo dell''utente';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.COD_FISCALE IS 'Codice fiscale dell''utente del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.NOME IS 'Nome dell''utente';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.COGNOME IS 'Cognome dell''utente del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.EMAIL IS 'Indirizzo email dell''utente del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.TELEFONO IS 'Telefono dell''utente del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.OPER_INS IS 'Nome e cognome  dell''utente che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_ATTIV_UTENTI.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';


CREATE TABLE public.MYEXT_PRODOTTO_RICH_UTENTI (
                ID_UTENTE_RICH INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_PROD_ATTIV_RICH INTEGER NOT NULL,
                ID_UTENTE_PROD INTEGER,
                RICH_OPER VARCHAR(5),
                COD_RUOLO VARCHAR(15) NOT NULL,
                ID_UTENTE INTEGER,
                COD_FISCALE VARCHAR,
                NOME VARCHAR(150),
                COGNOME VARCHAR(150),
                EMAIL VARCHAR(150),
                TELEFONO VARCHAR(30),
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_prodotto_rich_utenti_pk PRIMARY KEY (ID_UTENTE_RICH)
);
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.ID_UTENTE_RICH IS 'Identificativo dell''utente all''interno della richiesta';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.ID_PROD_ATTIV_RICH IS 'Identificativo della richiesta di attivazione';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.ID_UTENTE_PROD IS 'Identificativo dell''utente del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.RICH_OPER IS 'Tipo di operazione sull''utente per la richiesta (INS=Nuovo inserimento, MOD=Modifica, DEL=Cancellazione)';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.COD_RUOLO IS 'Codice del ruolo';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.ID_UTENTE IS 'Identificativo dell''utente';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.COD_FISCALE IS 'Codice fiscale dell''utente del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.NOME IS 'Nome dell''utente';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.COGNOME IS 'Cognome dell''utente del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.EMAIL IS 'Indirizzo email dell''utente del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.TELEFONO IS 'Telefono dell''utente del prodotto';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.OPER_INS IS 'Nome e cognome  dell''utente che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PRODOTTO_RICH_UTENTI.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';

CREATE TABLE public.MYEXT_UTENTE_MESSAGGI (
                ID_UTENTE INTEGER NOT NULL,
                ID_MESSAGGIO INTEGER NOT NULL,
                FLG_ONLINE INTEGER NOT NULL,
                FLG_READ_ONLINE INTEGER NOT NULL,
                DT_READ_ONLINE DATE,
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_utente_messaggi_pk PRIMARY KEY (ID_UTENTE, ID_MESSAGGIO)
);
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.ID_UTENTE IS 'Identificativo dell''utente';
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.ID_MESSAGGIO IS 'Identificativo del messaggio';
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.FLG_ONLINE IS 'Identifica se il messaggio vada esposto nell''area personale dell''utente (0=NO, 1=SI)';
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.FLG_READ_ONLINE IS 'Identifica se il messaggio è stato letto online dall''utente nella sua area personale';
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.DT_READ_ONLINE IS 'Data di lettura del messaggio da parte dell''utente nell''area privata';
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_UTENTE_MESSAGGI.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';

CREATE TABLE public.MYEXT_EVENTO_ISCRITTI (
                ID_ISCRITTO INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_UTENTE INTEGER,
                ID_EVENTO VARCHAR(250) NOT NULL,
                FLG_RELATORE INTEGER NOT NULL,
                FLG_PARTECIP_LOCO INTEGER NOT NULL,
                FLG_PARTECIP_REMOTO INTEGER NOT NULL,
                FLG_PARTECIP_PREF VARCHAR(3),
                DT_ISCRIZIONE DATE NOT NULL,
                DT_INVIO_ATTESTATO DATE,
                DT_RICH_QUESTIONARIO DATE,
                NOME VARCHAR(150),
                COGNOME VARCHAR(150),
                ID_ENTE INTEGER,
                AZIENDA VARCHAR(250),
                EMAIL VARCHAR(150),
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_evento_iscritti_pk PRIMARY KEY (ID_ISCRITTO)
);
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.ID_ISCRITTO IS 'Identificativo dell''iscritto';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.ID_UTENTE IS 'Identificativo dell''utente';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.ID_EVENTO IS 'Identificativo dell''evento';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.FLG_RELATORE IS 'Identifica se l''utente iscritto e'' relatore (=1) oppure no (=0)';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.FLG_PARTECIP_LOCO IS 'Identifica se l''iscritto ha partecipato all''evento in loco (=1)';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.FLG_PARTECIP_REMOTO IS 'Identifica se l''iscritto ha partecipato all''evento da remoto (=1)';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.FLG_PARTECIP_PREF IS 'Indica se l''utente ha dato la preferenza a partecipare da remoto (REM) o in presenza (PRE)';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.DT_ISCRIZIONE IS 'Data di iscrizione';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.DT_INVIO_ATTESTATO IS 'Data di invio attestato';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.DT_RICH_QUESTIONARIO IS 'Data in cui viene inviata la richiesta di compilazione del questionario';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.NOME IS 'Nome dell''invitato inserito manualmente';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.COGNOME IS 'Cognome dell''invitato inserito manualmente';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.ID_ENTE IS 'Identificativo dell''ente';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.AZIENDA IS 'Denominiazione dell''azienda dell''utente inserito manualmente';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.EMAIL IS 'Indirizzo email dell''invitato inserito manualmente';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_EVENTO_ISCRITTI.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';

CREATE TABLE public.MYEXT_ENTE_RAPPR (
                ID_RAPPR INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_UTENTE INTEGER NOT NULL,
                ID_ENTE INTEGER NOT NULL,
                TIPO_RAPPR VARCHAR(3) NOT NULL,
                MODULO_RICH VARCHAR(250),
                MODULO_REVOCA VARCHAR(250),
                DT_RICHIESTA DATE NOT NULL,
                FLG_CONFERMA INTEGER NOT NULL,
                DT_CONFERMA DATE,
                MOTIV_CONFERMA VARCHAR(1000),
                DT_INIZIO_VAL DATE,
                DT_FINE_VAL DATE,
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_ente_rappr_pk PRIMARY KEY (ID_RAPPR)
);
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.ID_RAPPR IS 'Identificativo del Rappresentante dell''Ente';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.ID_UTENTE IS 'Identificativo dell''utente';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.ID_ENTE IS 'Identificativo dell''ente';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.TIPO_RAPPR IS 'Tipo di rappresentante (RAP=Responsabile Attivazione Prodotti, GRE= Gestore Ruoli Ente)';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.MODULO_RICH IS 'Riferimento al file con il modulo della domanda firmata';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.MODULO_REVOCA IS 'Riferimento al file con il modulo di richiesta di revoca';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.DT_RICHIESTA IS 'Data di richiesta';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.FLG_CONFERMA IS 'Identifica se la richiesta e'' in attesa di conferma (=0), e'' stata confermata (=1) oppure no (=2) oppure da risottomettere (=-1)';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.DT_CONFERMA IS 'Data in cui e'' stato deciso se confermare una richiesta di collaborazione';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.MOTIV_CONFERMA IS 'Motivazione dell''esito della conferma di collaborazione';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.DT_INIZIO_VAL IS 'Data di inizio validita''';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.DT_FINE_VAL IS 'Data di fine validita''';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_ENTE_RAPPR.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';


CREATE TABLE public.MYEXT_PROGETTO_COLLAB (
                ID_COLLAB INTEGER NOT NULL DEFAULT nextval('public.myext_main_sequence'),
                ID_PROGETTO VARCHAR(250) NOT NULL,
                ID_UTENTE INTEGER NOT NULL,
                FLG_COORD INTEGER NOT NULL,
                FLG_CONFERMA INTEGER NOT NULL,
                DT_RICHIESTA DATE NOT NULL,
                DT_CONFERMA DATE,
                MOTIV_CONFERMA VARCHAR(1000),
                DT_INIZIO_VAL DATE,
                DT_FINE_VAL DATE,
                ID_OPER_INS VARCHAR(50) NOT NULL,
                OPER_INS VARCHAR(250) NOT NULL,
                DT_INS DATE NOT NULL,
                ID_OPER_ULT_MOD VARCHAR(50),
                OPER_ULT_MOD VARCHAR(250),
                DT_ULT_MOD DATE,
                CONSTRAINT myext_progetto_collab_pk PRIMARY KEY (ID_COLLAB)
);
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.ID_COLLAB IS 'Identificativo del collaboratore';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.ID_PROGETTO IS 'Identificativo del progetto';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.ID_UTENTE IS 'Identificativo dell''utente';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.FLG_COORD IS 'Identifica se l''utente collaboratore e'' coordinatore (=1) oppure no (=0)';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.FLG_CONFERMA IS 'Identifica se la richiesta di collaborazione e'' in attesa di conferma (=0), e'' stata confermata (=1) oppure non e'' stata confermata (=2)';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.DT_RICHIESTA IS 'Data di richiesta collaborazione';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.DT_CONFERMA IS 'Data in cui e'' stato deciso se confermare una richiesta di collaborazione';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.MOTIV_CONFERMA IS 'Motivazione dell''esito della conferma di collaborazione';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.DT_INIZIO_VAL IS 'Data di inizio validita''';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.DT_FINE_VAL IS 'Data di fine validita''';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.ID_OPER_INS IS 'Identificativo dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.OPER_INS IS 'Nome e cognome  dell''utente operatore che ha effettuato l''inserimento del record';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.DT_INS IS 'Data di inserimento';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.ID_OPER_ULT_MOD IS 'Identificativo dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.OPER_ULT_MOD IS 'Nome e cognome dell''utente operatore che ha eseguito l''ultima modifica';
COMMENT ON COLUMN public.MYEXT_PROGETTO_COLLAB.DT_ULT_MOD IS 'Data in cui è stata eseguita l''ultima modifica';

ALTER TABLE public.MYEXT_PRODOTTO_PROC ADD CONSTRAINT myext_prodotto_tipo_rich_myext_prodotto_proc_fk
FOREIGN KEY (COD_TIPO_RICH)
REFERENCES public.MYEXT_PRODOTTO_TIPO_RICH (COD_TIPO_RICH)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_ENTE ADD CONSTRAINT myext_ente_categoria_myext_ente_fk
FOREIGN KEY (ID_CATEGORIA)
REFERENCES public.MYEXT_ENTE_CATEGORIA (ID_CATEGORIA)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_ATTIV_UTENTI ADD CONSTRAINT myext_prodotto_ruoli_myext_prodotto_utenti_fk
FOREIGN KEY (COD_RUOLO)
REFERENCES public.MYEXT_PRODOTTO_RUOLI (COD_RUOLO)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_RICH_UTENTI ADD CONSTRAINT myext_prodotto_ruoli_myext_prodott_rich_utenti_fk
FOREIGN KEY (COD_RUOLO)
REFERENCES public.MYEXT_PRODOTTO_RUOLI (COD_RUOLO)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_ATTIVABILE ADD CONSTRAINT myext_prodotto_attiv_gruppo_myext_prodotto_attivabile_fk
FOREIGN KEY (ID_PROD_GRUPPO)
REFERENCES public.MYEXT_PRODOTTO_ATTIV_GRUPPO (ID_PROD_GRUPPO)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_ATTIVATO ADD CONSTRAINT myext_prodotto_myext_prodotto_attiv_fk
FOREIGN KEY (ID_PRODOTTO_ATT)
REFERENCES public.MYEXT_PRODOTTO_ATTIVABILE (ID_PRODOTTO_ATT)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_ATTIVABILE ADD CONSTRAINT myext_prodotto_attivabile_myext_prodotto_attivabile_fk
FOREIGN KEY (ID_PRODOTTO_ATT_PROP)
REFERENCES public.MYEXT_PRODOTTO_ATTIVABILE (ID_PRODOTTO_ATT)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_PROC ADD CONSTRAINT myext_prodotto_attivabile_myext_prodotto_proc_fk
FOREIGN KEY (ID_PRODOTTO_ATT)
REFERENCES public.MYEXT_PRODOTTO_ATTIVABILE (ID_PRODOTTO_ATT)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_PROC_STEP ADD CONSTRAINT myext_prodotto_proc_myext_prodotto_proc_step_fk
FOREIGN KEY (ID_PRODOTTO_PROC)
REFERENCES public.MYEXT_PRODOTTO_PROC (ID_PRODOTTO_PROC)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_RICH ADD CONSTRAINT myext_prodotto_proc_myext_prodotto_rich_fk
FOREIGN KEY (ID_PRODOTTO_PROC)
REFERENCES public.MYEXT_PRODOTTO_PROC (ID_PRODOTTO_PROC)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_COMUNE ADD CONSTRAINT myext_provincia_myext_comune_fk
FOREIGN KEY (COD_PROVINCIA)
REFERENCES public.MYEXT_PROVINCIA (COD_PROVINCIA)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_ENTE ADD CONSTRAINT myext_comune_myext_ente_fk
FOREIGN KEY (COD_COMUNE)
REFERENCES public.MYEXT_COMUNE (COD_COMUNE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_UTENTE_MESSAGGI ADD CONSTRAINT myext_messaggi_myext_utente_messaggi_fk
FOREIGN KEY (ID_MESSAGGIO)
REFERENCES public.MYEXT_MESSAGGI (ID_MESSAGGIO)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_UTENTE ADD CONSTRAINT myext_ente_myext_utente_fk
FOREIGN KEY (ID_ENTE)
REFERENCES public.MYEXT_ENTE (ID_ENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_ENTE ADD CONSTRAINT myext_ente_myext_ente_fk
FOREIGN KEY (ID_ENTE_UNIONE)
REFERENCES public.MYEXT_ENTE (ID_ENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_ENTE_RAPPR ADD CONSTRAINT myext_ente_mext_ente_rap_fk
FOREIGN KEY (ID_ENTE)
REFERENCES public.MYEXT_ENTE (ID_ENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_EVENTO_ISCRITTI ADD CONSTRAINT myext_ente_myext_evento_iscritti_fk
FOREIGN KEY (ID_ENTE)
REFERENCES public.MYEXT_ENTE (ID_ENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_ATTIVATO ADD CONSTRAINT myext_ente_myext_prodotto_attiv_fk
FOREIGN KEY (ID_ENTE)
REFERENCES public.MYEXT_ENTE (ID_ENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_ATTIV_UTENTI ADD CONSTRAINT myext_prodotto_attivato_myext_prodotto_utenti_fk
FOREIGN KEY (ID_ATTIVAZIONE)
REFERENCES public.MYEXT_PRODOTTO_ATTIVATO (ID_ATTIVAZIONE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_RICH ADD CONSTRAINT myext_prodotto_attivato_myext_prodotto_rich_fk
FOREIGN KEY (ID_ATTIVAZIONE)
REFERENCES public.MYEXT_PRODOTTO_ATTIVATO (ID_ATTIVAZIONE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_RICH_STEP ADD CONSTRAINT myext_prodotto_rich_myext_prodotto_rich_step_fk
FOREIGN KEY (ID_PROD_ATTIV_RICH)
REFERENCES public.MYEXT_PRODOTTO_RICH (ID_PROD_ATTIV_RICH)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_RICH_DOCS ADD CONSTRAINT myext_prodotto_rich_myext_prodotto_rich_docs_fk
FOREIGN KEY (ID_PROD_ATTIV_RICH)
REFERENCES public.MYEXT_PRODOTTO_RICH (ID_PROD_ATTIV_RICH)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_RICH_UTENTI ADD CONSTRAINT myext_prodotto_rich_myext_prodott_rich_utenti_fk
FOREIGN KEY (ID_PROD_ATTIV_RICH)
REFERENCES public.MYEXT_PRODOTTO_RICH (ID_PROD_ATTIV_RICH)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_RICH_DOCS ADD CONSTRAINT myext_prodotto_rich_step_myext_prodotto_rich_docs_fk
FOREIGN KEY (ID_STEP)
REFERENCES public.MYEXT_PRODOTTO_RICH_STEP (ID_STEP)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_UTENTE ADD CONSTRAINT myext_utente_myext_utente_fk
FOREIGN KEY (ID_UTENTE_PADRE)
REFERENCES public.MYEXT_UTENTE (ID_UTENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PROGETTO_COLLAB ADD CONSTRAINT myext_utente_myext_progetto_collab_fk
FOREIGN KEY (ID_UTENTE)
REFERENCES public.MYEXT_UTENTE (ID_UTENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_ENTE_RAPPR ADD CONSTRAINT myext_utente_mext_ente_rap_fk
FOREIGN KEY (ID_UTENTE)
REFERENCES public.MYEXT_UTENTE (ID_UTENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_EVENTO_ISCRITTI ADD CONSTRAINT myext_utente_myext_evento_iscritti_fk
FOREIGN KEY (ID_UTENTE)
REFERENCES public.MYEXT_UTENTE (ID_UTENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_UTENTE_MESSAGGI ADD CONSTRAINT myext_utente_myext_utente_messaggi_fk
FOREIGN KEY (ID_UTENTE)
REFERENCES public.MYEXT_UTENTE (ID_UTENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_ATTIV_UTENTI ADD CONSTRAINT myext_utente_myext_prodotto_utenti_fk
FOREIGN KEY (ID_UTENTE)
REFERENCES public.MYEXT_UTENTE (ID_UTENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_RICH_UTENTI ADD CONSTRAINT myext_utente_myext_prodott_rich_utenti_fk
FOREIGN KEY (ID_UTENTE)
REFERENCES public.MYEXT_UTENTE (ID_UTENTE)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;

ALTER TABLE public.MYEXT_PRODOTTO_RICH_UTENTI ADD CONSTRAINT myext_prodotto_attiv_utenti_myext_prodott_rich_utenti_fk
FOREIGN KEY (ID_UTENTE_PROD)
REFERENCES public.MYEXT_PRODOTTO_ATTIV_UTENTI (ID_UTENTE_PROD)
ON DELETE NO ACTION
ON UPDATE NO ACTION
NOT DEFERRABLE;