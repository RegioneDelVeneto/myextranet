ALTER TABLE MYEXT_UTENTE ADD COLUMN PARTITA_IVA varchar(11);

ALTER TABLE MYEXT_ENTE_RAPPR DROP COLUMN IF EXISTS MODULO_RICH;
ALTER TABLE MYEXT_ENTE_RAPPR DROP COLUMN IF EXISTS MODULO_REVOCA;
ALTER TABLE MYEXT_ENTE_RAPPR ADD COLUMN NUM_PROTOCOLLO varchar(20);
ALTER TABLE MYEXT_ENTE_RAPPR ADD COLUMN DT_PROTOCOLLO DATE;
