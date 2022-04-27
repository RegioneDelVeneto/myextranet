/**
 *     MyExtranet, il portale per collaborare con l’ente Regione Veneto.
 *     Copyright (C) 2022  Regione Veneto
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of the
 *     License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package it.regioneveneto.myp3.myextranet.utils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class Constants {

	public static final String RESULT_OK = "OK";
	public static final String RESULT_KO = "KO";
	
	public static final String MESSAGGIO_TIPO_INVITO = "INV";
	public static final String MESSAGGIO_TIPO_COMUNICAZIONE = "COM";
	
	public static final String MESSAGGIO_AREA_EVENTO = "EVT";
	public static final String MESSAGGIO_AREA_PRODOTTO = "PRD";
	public static final String MESSAGGIO_AREA_PROGETTO = "PRG";
	public static final String MESSAGGIO_AREA_REGISTRAZIONE = "REG";
	public static final String MESSAGGIO_AREA_UTENTE = "UT";
	
	public static final String OPERAZIONE_ISCRITTOEVENTO_IMPOSTA_PRESENZE = "ImpostaPresenze";
	public static final String OPERAZIONE_ISCRITTOEVENTO_IMPOSTA_RELATORE = "ImpostaRelatore";
	public static final String OPERAZIONE_ISCRITTOEVENTO_INVIA_ATTESTATI = "InviaAttestati";
	public static final String OPERAZIONE_ISCRITTOEVENTO_INVIA_RICHIESTA_COMPILAZIONE_QUESTIONARIO = "InviaRichiestaCompilazioneQuestionario";
	
	public static final String OPERAZIONE_COLLABORATOREPROGETTO_IMPOSTA_COORDINATORE = "ImpostaCoordinatore";
	
	public static final String COOKIE_NAME_ACCESS_TOKEN = "MYINTRANET_ACCESS_TOKEN";
	
	public static final String CACHE_NAME_USER_ROLES = "MyExtranet-UserRoles";
	public static final String CACHE_NAME_USER_VALIDITY = "MyExtranet-UserValidity";
	public static final String CACHE_NAME_VALID_TOKENS = "MyExtranet-ValidTokens";
	
	public static final String ACL_UTENTI = "myextranet.utenti";
	public static final String ACL_ENTI = "myextranet.enti";
	public static final String ACL_EVENTI = "myextranet.eventi";
	public static final String ACL_PROGETTI = "myextranet.progetti";
	public static final String ACL_PRODOTTI = "myextranet.prodotti";

	
	public static final String PERMISSION_VISUALIZZA = "visualizza";
	public static final String PERMISSION_GESTISCI = "gestisci";

	public static final String MYSYSCONFIG_KEY_MYEXTDOMAIN = ">params>domainsurl>customdomain";
	public static final String MYSYSCONFIG_KEY_MYEXT_AREAPRODOTTI = "/myextranet/utente/prodotti";
	
	public static final String MYSYSCONFIG_KEY_MYPORTALDOMAIN = ">params>domainsurl>myportaldomain";
	public static final String MYSYSCONFIG_KEY_MYPORTALCONTEXT = ">params>domainsurl>myportalcontext";
	
	public static final String MYSYSCONFIG_KEY_MYINTRANETLDOMAIN = ">params>domainsurl>myintranetdomain";
	public static final String MYSYSCONFIG_KEY_MYINTRANETCONTEXT = ">params>domainsurl>myintranetcontext";
	
	public static final LocalDate DEFAULT_DATE_END_VALIDITY = LocalDate.of(9999, 12, 31);
	
	public static final Integer COLLABORATORE_PROGETTO_FLG_CONFERMA_IN_ATTESA = 0;
	public static final Integer COLLABORATORE_PROGETTO_FLG_CONFERMA_CONFERMATA = 1;
	public static final Integer COLLABORATORE_PROGETTO_FLG_CONFERMA_RIFIUTATA = 2;
	public static final Integer COLLABORATORE_PROGETTO_FLG_CONFERMA_ANNULLATA_DALL_UTENTE = 3;
	public static final Integer COLLABORATORE_PROGETTO_FLG_CONFERMA_ANNULLATA_DA_OPERATORE = 4;
	
	
	public static final Integer PRODOTTO_ATTIVATO_STATO_DA_ATTIVARE = 0;
	public static final Integer PRODOTTO_ATTIVATO_STATO_IN_FASE_DI_ATTIVAZIONE = 1;
	public static final Integer PRODOTTO_ATTIVATO_STATO_ATTIVATO = 2;
	
	public static final String RAPPRESENTANTE_ENTE_TIPO_RAP = "RAP";
	public static final String RAPPRESENTANTE_ENTE_TIPO_GRE = "GRE";
	
	public static final Integer RAPPRESENTANTE_ENTE_FLG_CONFERMA_DA_RISOTTOMETTERE = -1;
	public static final Integer RAPPRESENTANTE_ENTE_FLG_CONFERMA_IN_ATTESA = 0;
	public static final Integer RAPPRESENTANTE_ENTE_FLG_CONFERMA_CONFERMATA = 1;
	public static final Integer RAPPRESENTANTE_ENTE_FLG_CONFERMA_RIFIUTATA = 2;
	
	public static final String UTENTE_PRODOTTO_ATTIVATO_OPERAZIONE_MODIFICA = "modifica";
	public static final String UTENTE_PRODOTTO_ATTIVATO_OPERAZIONE_ELIMINA = "elimina";

	
	public static final String STEP_PROCEDIMENTO_PRODOTTO_OPERAZIONE_MODIFICA = "modifica";
	public static final String STEP_PROCEDIMENTO_PRODOTTO_OPERAZIONE_ELIMINA = "elimina";
	
	public static final String STEP_ESECUTORE_ENTE = "EN";
	public static final String STEP_ESECUTORE_REGIONE = "RV";

	public static final String OPERAZIONE_MODIFICA = "modifica";
	public static final String OPERAZIONE_ELIMINA = "elimina";
	
	public static final String UTENTE_RICHIESTA_PRODOTTO_OPERAZIONE_INSERIMENTO = "INS";
	public static final String UTENTE_RICHIESTA_PRODOTTO_OPERAZIONE_MODIFICA = "MOD";
	public static final String UTENTE_RICHIESTA_PRODOTTO_OPERAZIONE_CANCELLAZIONE = "DEL";
	
	public static final Integer GET_COLLABORATORI_PROGETTO_FILTER_TYPE_ALL = 0;
	public static final Integer GET_COLLABORATORI_PROGETTO_FILTER_TYPE_COLLABORATORI = 1;
	public static final Integer GET_COLLABORATORI_PROGETTO_FILTER_TYPE_RICHIESTE_COLLABORAZIONE = 2;
	
	public static final String API_ERROR_GENERIC_MESSAGE = "Si è verificato un problema";
	
	public static final String API_ERROR_GENERIC_CODE = "GENERIC_ERROR";
	public static final String API_ERROR_SECURITY_CODE = "SECURITY_ERROR";
	public static final String API_ERROR_VALIDATION_CODE = "VALIDATION_ERROR";
	public static final String API_ERROR_MISSING_CONF_CODE = "MISSING_CONF_ERROR";
	
	public static final String API_ERROR_SECURITY_NOT_LOGGED_IN_USER_MESSAGE ="Accesso ai dati di un utente diverso da quello collegato";
	public static final String API_ERROR_SECURITY_CANT_GET_USER_MESSAGE ="Impossibile recuperare l'utente";
	public static final String API_ERROR_SECURITY_NOT_OWN_DATA_MESSAGE ="Impossibile visualizzare o modificare dati non propri";
	
	public static final String API_ERROR_ACCESS_DENIED_TO_DATA_MESSAGE = "Accesso ai dati non consentito";
	public static final String API_ERROR_VALIDATION_MESSAGE = "Errore validazione";
	public static final String API_ERROR_MISSING_CONF_MESSAGE = "Errore di configurazione";
	
	public static final String API_SUCCESS_CODE = "SUCCESS";
	
	public static final String RIPRESA_DATI_TABELLA_COMUNI = "comuni";
	public static final String RIPRESA_DATI_TABELLA_TIPOLOGIE_ENTE = "tipologie_ente";
	public static final String RIPRESA_DATI_TABELLA_ENTI = "enti";
	public static final String RIPRESA_DATI_TABELLA_UTENTI = "utenti";
	public static final String RIPRESA_DATI_TABELLA_PRODOTTI_ATTIVABILI = "prodotti_attivabili";
	public static final String RIPRESA_DATI_TABELLA_PRODOTTI_ATTIVATI = "prodotti_attivati";
	public static final String RIPRESA_DATI_TABELLA_UTENTI_PRODOTTI = "utenti_prodotti";
	public static final String RIPRESA_DATI_TABELLA_RAP = "rap";
	
	public static final String RIPRESA_DATI_ID_OPERATORE = "RIPRESA_DATI";
	public static final String RIPRESA_DATI_OPERATORE = "Ripresa Dati";
	
	// this should comprise all tables
	public static final String[] RIPRESA_DATI_DEFAULT_LISTA_TABELLA = {
			RIPRESA_DATI_TABELLA_COMUNI,
			RIPRESA_DATI_TABELLA_TIPOLOGIE_ENTE,
			RIPRESA_DATI_TABELLA_ENTI,
			RIPRESA_DATI_TABELLA_UTENTI,
			RIPRESA_DATI_TABELLA_PRODOTTI_ATTIVABILI,
			RIPRESA_DATI_TABELLA_PRODOTTI_ATTIVATI,
			RIPRESA_DATI_TABELLA_UTENTI_PRODOTTI,
			RIPRESA_DATI_TABELLA_RAP
	};
}
