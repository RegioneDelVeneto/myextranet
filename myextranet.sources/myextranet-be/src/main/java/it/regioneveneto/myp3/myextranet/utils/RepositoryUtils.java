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

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.CollaboratoreProgetto;
import it.regioneveneto.myp3.myextranet.model.Comune;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.IscrittoEvento;
import it.regioneveneto.myp3.myextranet.model.Messaggio;
import it.regioneveneto.myp3.myextranet.model.ProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivato;
import it.regioneveneto.myp3.myextranet.model.RappresentanteEnte;
import it.regioneveneto.myp3.myextranet.model.RichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.RuoloProdotto;
import it.regioneveneto.myp3.myextranet.model.StepProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.model.UtenteMessaggi;
import it.regioneveneto.myp3.myextranet.model.UtenteProdottoAttivato;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ComuneFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProcedimentoProdottoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoComunicazioneFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RuoloProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.StepProcedimentoProdottoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteMessaggiFilterDTO;

/**
 * Utils per creare i predicate nelle query
 */
public class RepositoryUtils {
	
	
    /**
     * Costruisce l'oggetto {@link Specification} per eseguire la query di ricerca degli utenti Messaggi, costruendo i predicati della clausola
     * where attraverso l'oggetto {@link UtenteMessaggiFilterDTO} che incapsula i filtri di ricerca.
     *
     * @param filter oggetto che incapsula i filtri di ricerca.
     * @return l'oggetto {@link Specification}
     */
    public static Specification<UtenteMessaggi> buildUtenteMessaggiFilterSpecification(final UtenteMessaggiFilterDTO filter) {
        return (Specification<UtenteMessaggi>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // idUtente
            if (filter.getIdUtente() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("utente").get("idUtente"),
                                filter.getIdUtente()
                        )
                );
            }
            // idMessaggio
            if (filter.getIdMessaggio() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("messaggio").get("idMessaggio"),
                                filter.getIdMessaggio()
                        )
                );
            }
            // flgOnline
            if (filter.getFlgOnline() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgOnline"),
                                filter.getFlgOnline()
                        )
                );
            }
            // flgReadOnline
            if (filter.getFlgReadOnline() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgReadOnline"),
                                filter.getFlgReadOnline()
                        )
                );
            }
            
            return predicate;
        };
    }


    /**
     * Costruisce l'oggetto {@link Specification} per eseguire la query di ricerca degli utenti, costruendo i predicati della clausola
     * where attraverso l'oggetto {@link UtenteFilterDTO} che incapsula i filtri di ricerca.
     *
     * @param filter oggetto che incapsula i filtri di ricerca.
     * @return l'oggetto {@link Specification}
     */
    public static Specification<Utente> buildUtenteFilterSpecification(final UtenteFilterDTO filter) {
        return (Specification<Utente>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // Id Ente
            if (filter.getIdEnte() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("ente").get("idEnte"),
                                filter.getIdEnte()
                        )
                );
            }
            // Nome
            if (StringUtils.hasText(filter.getNome())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("nome")),
                                '%' + filter.getNome().toLowerCase() + '%'
                        )
                );
            }
            // Cognome
            if (StringUtils.hasText(filter.getCognome())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("cognome")),
                                '%' + filter.getCognome().toLowerCase() + '%'
                        )
                );
            }
            // Azienda
            if (StringUtils.hasText(filter.getAzienda())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("azienda")),
                                '%' + filter.getAzienda().toLowerCase() + '%'
                        )
                );
            }
            // flgArchived
            if (filter.getFlgArchived() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgArchived"),
                                filter.getFlgArchived()
                        )
                );
            }
            // codFiscale (like R)
            if (StringUtils.hasText(filter.getCodFiscale())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("codFiscale")),
                                filter.getCodFiscale().toLowerCase() + '%'
                        )
                );
            }

            return predicate;
        };
    }

    /**
     * Costruisce l'oggetto {@link Specification} per eseguire la query di ricerca degli enti, costruendo i predicati della clausola
     * where attraverso l'oggetto {@link EnteFilterDTO} che incapsula i filtri di ricerca.
     *
     * @param filter oggetto che incapsula i filtri di ricerca.
     * @return l'oggetto {@link Specification}
     */
    public static Specification<Ente> buildEnteFilterSpecification(final EnteFilterDTO filter) {
        return (Specification<Ente>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // Codice Ipa
            if (StringUtils.hasText(filter.getCodIpa())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("codIpa")),
                                '%' + filter.getCodIpa().toLowerCase() + '%'
                        )
                );
            }
            // idCategoria
            if (filter.getIdCategoria() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("categoria").get("idCategoria"),
                                filter.getIdCategoria()
                        )
                );
            }
            // denominazione
            if (StringUtils.hasText(filter.getDenominazione())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("denominazione")),
                                '%' + filter.getDenominazione().toLowerCase() + '%'
                        )
                );
            }
            // activeOnly
            if (filter.isActiveOnly() != null) {
            	LocalDate now = LocalDate.now();
            	
                predicate = builder.and(
                        predicate,
                        builder.lessThanOrEqualTo(root.get("dtInizioVal"), Date.valueOf(now)));
                
                predicate = builder.and(
                        predicate,
                        builder.greaterThan(root.get("dtFineVal"), Date.valueOf(now)));
            }
            
            return predicate;
        };
    }

    /**
     * Costruisce l'oggetto {@link Specification} per eseguire la query di ricerca i comuni, costruendo i predicati della clausola
     * where attraverso l'oggetto {@link ComuneFilterDTO} che incapsula i filtri di ricerca.
     *
     * @param filter oggetto che incapsula i filtri di ricerca.
     * @return l'oggetto {@link Specification}
     */
    public static Specification<Comune> buildComuneFilterSpecification(final ComuneFilterDTO filter) {
        return (Specification<Comune>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // codComune
            if (StringUtils.hasText(filter.getCodComune())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("codComune"),
                                filter.getCodComune()
                        )
                );
            }
            // desComune
            if (StringUtils.hasText(filter.getDesComune())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("desComune")),
                                '%' + filter.getDesComune().toLowerCase() + '%'
                        )
                );
            }
            // codProvincia
            if (StringUtils.hasText(filter.getCodProvincia())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("provincia").get("codProvincia"),
                                filter.getCodProvincia()
                        )
                );
            }
            
            return predicate;
        };
    }
    
    /**
     * Costruisce l'oggetto {@link Specification} per eseguire la query di ricerca dei messaggi, costruendo i predicati della clausola
     * where attraverso l'oggetto {@link MessaggioFilterDTO} che incapsula i filtri di ricerca.
     *
     * @param filter oggetto che incapsula i filtri di ricerca.
     * @return l'oggetto {@link Specification}
     */
    public static Specification<Messaggio> buildMessaggioFilterSpecification(final MessaggioFilterDTO filter) {
        return (Specification<Messaggio>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // tipo
            if (StringUtils.hasText(filter.getTipo())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("tipo"),
                                filter.getTipo()
                        )
                );
            }
            // area
            if (StringUtils.hasText(filter.getArea())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("area"),
                                filter.getArea()
                        )
                );
            }
            // idContenuto
            if (StringUtils.hasText(filter.getIdContenuto())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("idContenuto"),
                                filter.getIdContenuto()
                        )
                );
            }
            // destinatario
            if (StringUtils.hasText(filter.getDestinatario())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("destinatario")),
                                '%' + filter.getDestinatario().toLowerCase() + '%'
                        )
                );
            }
            // titolo
            if (StringUtils.hasText(filter.getTitolo())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("titolo")),
                                '%' + filter.getTitolo().toLowerCase() + '%'
                        )
                );
            }
            // messaggio
            if (StringUtils.hasText(filter.getMessaggio())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("messaggio")),
                                '%' + filter.getMessaggio().toLowerCase() + '%'
                        )
                );
            }
            
            // Data invio deve essere >= dtInvioDa
            if (filter.getDtInvioDa() != null) {
                predicate = builder.and(
                        predicate,
                        builder.greaterThanOrEqualTo(root.get("dtInvio"), filter.getDtInvioDa()));
            }

            // Data invio deve essere <= dtInvioA
            if (filter.getDtInvioA() != null) {
                predicate = builder.and(
                        predicate,
                        builder.lessThanOrEqualTo(root.get("dtInvio"), filter.getDtInvioA()));
            }

            return predicate;
        };
    }

    /**
     * Costruisce l'oggetto {@link Specification} per eseguire la query di ricerca degli iscritti, costruendo i predicati della clausola
     * where attraverso l'oggetto {@link IscrittoEventoFilterDTO} che incapsula i filtri di ricerca.
     *
     * @param filter oggetto che incapsula i filtri di ricerca.
     * @return l'oggetto {@link Specification}
     */
    public static Specification<IscrittoEvento> buildIscrittoEventoFilterSpecification(final IscrittoEventoFilterDTO filter) {
        return (Specification<IscrittoEvento>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // idEvento
            if (StringUtils.hasText(filter.getIdEvento())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("idEvento"),
                                filter.getIdEvento()
                        )
                );
            }
            // nome
            if (StringUtils.hasText(filter.getNome())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("nome")),
                                '%' + filter.getNome().toLowerCase() + '%'
                        )
                );
            }
            // cognome
            if (StringUtils.hasText(filter.getCognome())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("cognome")),
                                '%' + filter.getCognome().toLowerCase() + '%'
                        )
                );
            }
            // Id Ente
            if (filter.getIdEnte() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("ente").get("idEnte"),
                                filter.getIdEnte()
                        )
                );
            }
            // flgRelatore
            if (filter.getFlgRelatore() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgRelatore"),
                                filter.getFlgRelatore()
                        )
                );
            }
            // Id Utente
            if (filter.getIdUtente() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("utente").get("idUtente"),
                                filter.getIdUtente()
                        )
                );
            }
            // azienda
            if (StringUtils.hasText(filter.getAzienda())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("azienda")),
                                '%' + filter.getAzienda().toLowerCase() + '%'
                        )
                );
            }

            return predicate;
        };
    }

    /**
     * Costruisce l'oggetto {@link Specification} per eseguire la query di ricerca dei collaboratori, costruendo i predicati della clausola
     * where attraverso l'oggetto {@link CollaboratoreProgettoFilterDTO} che incapsula i filtri di ricerca.
     *
     * @param filter oggetto che incapsula i filtri di ricerca.
     * @return l'oggetto {@link Specification}
     */
    public static Specification<CollaboratoreProgetto> buildCollaboratoreProgettoFilterSpecification(final CollaboratoreProgettoFilterDTO filter, Integer filterType) {
        return (Specification<CollaboratoreProgetto>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // idCollab
            if (filter.getIdCollab() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("idCollab"),
                                filter.getIdCollab()
                        )
                );
            }
            // Id Utente
            if (filter.getIdUtente() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("utente").get("idUtente"),
                                filter.getIdUtente()
                        )
                );
            }
            // idProgetto
            if (StringUtils.hasText(filter.getIdProgetto())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("idProgetto"),
                                filter.getIdProgetto()
                        )
                );
            }
            // flgCoord
            if (filter.getFlgCoord() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgCoord"),
                                filter.getFlgCoord()
                        )
                );
            }
            // flgConferma
            if (filter.getFlgConferma() != null && filterType.equals(Constants.GET_COLLABORATORI_PROGETTO_FILTER_TYPE_ALL)) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgConferma"),
                                filter.getFlgConferma()
                        )
                );
            }
            // filterType = GET_COLLABORATORI_PROGETTO_FILTER_TYPE_COLLABORATORI => FLG_CONFERMA = 1
            if (filterType.equals(Constants.GET_COLLABORATORI_PROGETTO_FILTER_TYPE_COLLABORATORI)) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgConferma"),
                                Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_CONFERMATA
                        )
                );
            }
            // filterType = GET_COLLABORATORI_PROGETTO_FILTER_TYPE_RICHIESTE_COLLABORAZIONE => FLG_CONFERMA = 0 or FLG_CONFERMA = 2
            if (filterType.equals(Constants.GET_COLLABORATORI_PROGETTO_FILTER_TYPE_RICHIESTE_COLLABORAZIONE)) {
                Predicate inner = builder.or(
                		builder.equal(root.get("flgConferma"), Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_IN_ATTESA),
                		builder.equal(root.get("flgConferma"), Constants.RAPPRESENTANTE_ENTE_FLG_CONFERMA_RIFIUTATA)
                		);
                
                predicate = builder.and(
                        predicate,
                        inner
                        );
            }
            
            // motivConferma
            if (StringUtils.hasText(filter.getMotivConferma())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("motivConferma")),
                                '%' + filter.getMotivConferma().toLowerCase() + '%'
                        )
                );
            }
            // nome
            if (StringUtils.hasText(filter.getNome())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("utente").get("nome")),
                                '%' + filter.getNome().toLowerCase() + '%'
                        )
                );
            }
            // cognome
            if (StringUtils.hasText(filter.getCognome())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("utente").get("cognome")),
                                '%' + filter.getCognome().toLowerCase() + '%'
                        )
                );
            }
            // denominazioneEnte
            if (StringUtils.hasText(filter.getDenominazioneEnte() )) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("utente").get("ente").get("denominazione")),
                                '%' + filter.getDenominazioneEnte().toLowerCase() + '%'
                        )
                );
            }       
            // idEnte
            if (filter.getIdEnte() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("utente").get("ente").get("idEnte"),
                                filter.getIdEnte()
                        )
                );
            }
            // azienda
            if (StringUtils.hasText(filter.getAzienda())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("utente").get("azienda")),
                                '%' + filter.getAzienda().toLowerCase() + '%'
                        )
                );
            } 
            // validOnly
            if (filter.isValidOnly() != null && filter.isValidOnly()) {
            	Date today = Date.valueOf(LocalDate.now());

                Predicate inner = builder.and(
                		builder.lessThanOrEqualTo(root.get("dtInizioVal"), today),
                		builder.greaterThan(root.get("dtFineVal"), today)
                		);
                
                predicate = builder.and(
                        predicate,
                        inner);
            }
            
            return predicate;
        };
    }


    /**
     * Costruisce l'oggetto {@link Specification} per eseguire la query di ricerca dei prodotti attivabili, costruendo i predicati della clausola
     * where attraverso l'oggetto {@link ProdottoAttivabileFilterDTO} che incapsula i filtri di ricerca.
     *
     * @param filter oggetto che incapsula i filtri di ricerca.
     * @return l'oggetto {@link Specification}
     */
	public static Specification<ProdottoAttivabile> buildProdottoAttivabileFilterSpecification(
			ProdottoAttivabileFilterDTO filter) {
        return (Specification<ProdottoAttivabile>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // idProdottoAtt
            if (filter.getIdProdottoAtt() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("idProdottoAtt"),
                                filter.getIdProdottoAtt()
                        )
                );
            }
            // idProdotto
            if (StringUtils.hasText(filter.getIdProdotto())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("idProdotto"),
                                filter.getIdProdotto()
                        )
                );
            }
            // nomeProdottoAttiv
            if (StringUtils.hasText(filter.getNomeProdottoAttiv())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("nomeProdottoAttiv")),
                                '%' + filter.getNomeProdottoAttiv().toLowerCase() + '%'
                        )
                );
            }
            // isAttivabile TRUE
            if (filter.isAttivabile() != null && filter.isAttivabile()) {
            	LocalDate now = LocalDate.now();

                Predicate inner = builder.and(
                		builder.lessThanOrEqualTo(root.get("dtAttivabileDa"), now),
                		builder.greaterThan(root.get("dtAttivabileA"), now)
                		);
                
                predicate = builder.and(
                        predicate,
                        inner);
            }
            // isAttivabile FALSE
            if (filter.isAttivabile() != null && !filter.isAttivabile()) {
            	LocalDate now = LocalDate.now();
            	
                Predicate inner = builder.or(
                		builder.greaterThan(root.get("dtAttivabileDa"), now),
                		builder.lessThanOrEqualTo(root.get("dtAttivabileA"), now)
                		);
                
                predicate = builder.and(
                        predicate,
                        inner);
            }
            // isAttivabileOrIn
            if (filter.getIsAttivabileOrIn() != null) {
            	LocalDate now = LocalDate.now();

                Predicate innerAttivabile = builder.and(
                		builder.lessThanOrEqualTo(root.get("dtAttivabileDa"), now),
                		builder.greaterThan(root.get("dtAttivabileA"), now)
                		);
                
                Predicate innerAttivabileOrIn = builder.or(
                		innerAttivabile,
                		root.get("idProdottoAtt").in(filter.getIsAttivabileOrIn())
                		);
                
                predicate = builder.and(
                        predicate,
                        innerAttivabileOrIn);
            }
          
            return predicate;
        };

	}

    /**
     * Costruisce l'oggetto {@link Specification} per eseguire la query di ricerca dei prodotti attivati, costruendo i predicati della clausola
     * where attraverso l'oggetto {@link ProdottoAttivatoFilterDTO} che incapsula i filtri di ricerca.
     *
     * @param filter oggetto che incapsula i filtri di ricerca.
     * @return l'oggetto {@link Specification}
     */
	public static Specification<ProdottoAttivato> buildProdottoAttivatoFilterSpecification(
			ProdottoAttivatoFilterDTO filter, List<Integer> withPendingRequestsIds, List<Integer> filteredByStateIds) {
        return (Specification<ProdottoAttivato>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // idProdottoAtt
            if (filter.getIdProdottoAtt() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("prodottoAttivabile").get("idProdottoAtt"),
                                filter.getIdProdottoAtt()
                        )
                );
            }
            // idProdotto
            if (StringUtils.hasText(filter.getIdProdotto())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("prodottoAttivabile").get("idProdotto"),
                                filter.getIdProdotto()
                        )
                );
            }
            // idEnte
            if (filter.getIdEnte() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("ente").get("idEnte"),
                                filter.getIdEnte()
                        )
                );
            }
            // withPendingRequests
            if (filter.getWithPendingRequests() != null && withPendingRequestsIds != null) {
            	Predicate inner = root.get("idAttivazione").in(withPendingRequestsIds);
            	
            	if (!filter.getWithPendingRequests()) {
            		inner = inner.not();
            	}
            	
                predicate = builder.and(
                        predicate,
                        inner
                );
            }
            // stato
            if (filter.getStato() != null && filteredByStateIds != null) {
            	Predicate inner = root.get("idAttivazione").in(filteredByStateIds);
            	
                predicate = builder.and(
                        predicate,
                        inner
                );
            }            
            
            return predicate;
        };
        
	}


	public static Specification<RappresentanteEnte> buildRappresentanteEnteFilterSpecification(
			RappresentanteEnteFilterDTO filter) {
        return (Specification<RappresentanteEnte>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // nomeUtente
            if (StringUtils.hasText(filter.getNomeUtente())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("utente").get("nome")),
                                '%' + filter.getNomeUtente().toLowerCase() + '%'
                        )
                );
            }
            // cognomeUtente
            if (StringUtils.hasText(filter.getCognomeUtente())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("utente").get("cognome")),
                                '%' + filter.getCognomeUtente().toLowerCase() + '%'
                        )
                );
            }
            // idEnte
            if (filter.getIdEnte() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("ente").get("idEnte"),
                                filter.getIdEnte()
                        )
                );
            }
            // flgConferma
            if (filter.getFlgConferma() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgConferma"),
                                filter.getFlgConferma()
                        )
                );
            }
            // tipoRappr
            if (filter.getTipoRappr() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("tipoRappr"),
                                filter.getTipoRappr()
                        )
                );
            }
            // flgAttivo
            if (filter.getFlgAttivo() != null) {
            	Date today = Date.valueOf(LocalDate.now());
            	
    			Predicate innerDate = builder.and(
    					builder.lessThanOrEqualTo(root.get("dtInizioVal"), today),
    					builder.greaterThan(root.get("dtFineVal"), today)
    					);
    			
    			if (filter.getFlgAttivo().equals(0)) {
    				innerDate = builder.not(innerDate);
    				
    				innerDate = builder.or(
    						builder.and(
    								builder.isNull(root.get("dtInizioVal")),
    								builder.isNull(root.get("dtFineVal"))
    								),
    						innerDate
    						);
    			}
    			
    			predicate = builder.and(
    			        predicate,
    			        innerDate);
    			
            }
            // flgRichInCorso
            if (filter.getFlgRichInCorso() != null) {
            	Predicate inner;
            	
            	if (filter.getFlgRichInCorso().equals(0)) {
            		inner = builder.equal(
            				root.get("flgConferma"),
            				1
            				);
            	} else {
            		inner = builder.notEqual(
            				root.get("flgConferma"),
            				1
            				);
            	}
    			predicate = builder.and(
    			        predicate,
    			        inner);
            }
            
            return predicate;
        };
	}


	public static Specification<RappresentanteEnte> buildEntiRappresentatiUtenteSpecification(Integer userId) {
        return (Specification<RappresentanteEnte>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            
            // id dell'utente
            predicate = builder.and(
                    predicate,
                    builder.equal(
                            root.get("utente").get("idUtente"),
                            userId
                    )
            );
            // tipoRappr = RAP
            predicate = builder.and(
                    predicate,
                    builder.equal(
                            root.get("tipoRappr"),
                            Constants.RAPPRESENTANTE_ENTE_TIPO_RAP
                    )
            );
            
            return predicate;
        };
	}


	public static Specification<CollaboratoreProgetto> buildAllCurrentCollaboratoreProgettoByCodFiscaleSpecification(
			Integer idUtente) {
		
        return (Specification<CollaboratoreProgetto>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            
            // idUtente
            predicate = builder.and(
                    predicate,
                    builder.equal(
                            root.get("utente").get("idUtente"),
                            idUtente
                    )
            );

            // valido in data odierna or flgConferma = 0 or flgConferma = 2
			Date today = Date.valueOf(LocalDate.now());
			
			Predicate innerDate = builder.and(
					builder.lessThanOrEqualTo(root.get("dtInizioVal"), today),
					builder.greaterThan(root.get("dtFineVal"), today)
					);
			
			Predicate inner = builder.or(
					builder.equal(
							root.get("flgConferma"),
							0
							),
					innerDate
					);
			
			inner = builder.or(
					builder.equal(
							root.get("flgConferma"),
							2
							),
					inner
					);
			
			predicate = builder.and(
			        predicate,
			        inner);
            
            return predicate;
        };
	}


	public static Specification<ProcedimentoProdotto> buildProcedimentoProdottoFilterSpecification(
			ProcedimentoProdottoFilterDTO filter) {
        return (Specification<ProcedimentoProdotto>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            
            // idProdottoAtt
            if (filter.getIdProdottoAtt() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("prodotto").get("idProdottoAtt"),
                                filter.getIdProdottoAtt()
                        )
                );
            }
            // idProdotto
            if (StringUtils.hasText(filter.getIdProdotto())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("prodotto").get("idProdotto"),
                                filter.getIdProdotto()
                        )
                );
            }
            // numVersione
            if (filter.getNumVersione() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("numVersione"),
                                filter.getNumVersione()
                        )
                );
            }
            // codTipoRich
            if (StringUtils.hasText(filter.getCodTipoRich())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("tipoRichiestaProdotto").get("codTipoRich"),
                                filter.getCodTipoRich()
                        )
                );
            }
            // desProdottoProc
            if (StringUtils.hasText(filter.getDesProdottoProc())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("desProdottoProc")),
                                '%' + filter.getDesProdottoProc().toLowerCase() + '%'
                        )
                );
            }
            // desProdottoProcEstesa
            if (StringUtils.hasText(filter.getDesProdottoProcEstesa())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("desProdottoProcEstesa")),
                                '%' + filter.getDesProdottoProcEstesa().toLowerCase() + '%'
                        )
                );
            }
            // flgEnabled
            if (filter.getFlgEnabled() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgEnabled"),
                                filter.getFlgEnabled()
                        )
                );
            }
            
            return predicate;
        };
        
	}


	public static Specification<StepProcedimentoProdotto> buildStepProcedimentoProdottoFilterSpecification(
			StepProcedimentoProdottoFilterDTO filter) {
        return (Specification<StepProcedimentoProdotto>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            
            // idProdottoProc
            if (filter.getIdProdottoProc() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("procedimentoProdotto").get("idProdottoProc"),
                                filter.getIdProdottoProc()
                        )
                );
            }
            // numStep
            if (filter.getNumStep() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("numStep"),
                                filter.getNumStep()
                        )
                );
            }
            // codStato
            if (StringUtils.hasText(filter.getCodStato())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("codStato")),
                                '%' + filter.getCodStato().toLowerCase() + '%'
                        )
                );
            }
            // desStato
            if (StringUtils.hasText(filter.getDesStato())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("desStato")),
                                '%' + filter.getDesStato().toLowerCase() + '%'
                        )
                );
            }
            // competenza
            if (StringUtils.hasText(filter.getCompetenza())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("competenza")),
                                '%' + filter.getCompetenza().toLowerCase() + '%'
                        )
                );
            }
            // flgAggiornaUtenti
            if (filter.getFlgAggiornaUtenti() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgAggiornaUtenti"),
                                filter.getFlgAggiornaUtenti()
                        )
                );
            }
            // flgFineRich
            if (filter.getFlgFineRich() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("flgFineRich"),
                                filter.getFlgFineRich()
                        )
                );
            }
            // azioni
            if (StringUtils.hasText(filter.getAzioni())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("azioni")),
                                '%' + filter.getAzioni().toLowerCase() + '%'
                        )
                );
            }
            
            return predicate;
        };

	}

	public static Specification<RuoloProdotto> buildRuoloProdottoFilterSpecification(
			RuoloProdottoDTO filter) {
        return (Specification<RuoloProdotto>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();
            // codRuolo
            if (StringUtils.hasText(filter.getCodRuolo())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("codRuolo"),
                                filter.getCodRuolo()
                        )
                );
            }
            // desRuolo
            if (StringUtils.hasText(filter.getDesRuolo())) {
                predicate = builder.and(
                        predicate,
                        builder.like(
                                builder.lower(root.get("desRuolo")),
                                '%' + filter.getDesRuolo().toLowerCase() + '%'
                        )
                );
            }
            // numRuolo
            if (filter.getNumRuolo() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("numRuolo"),
                                filter.getNumRuolo()
                        )
                );
            }
            
            return predicate;
        };

	}


	public static Specification<UtenteProdottoAttivato> buildUtenteProdottoAttivatoComunicazioneFilterSpecification(
			ProdottoAttivatoComunicazioneFilterDTO filter) {
        return (Specification<UtenteProdottoAttivato>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            // idProdottoAtt
            if (filter.getIdProdottoAtt() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("prodottoAttivato").get("prodottoAttivabile").get("idProdottoAtt"),
                                filter.getIdProdottoAtt()
                        )
                );
            }
            // idProdotto
            if (StringUtils.hasText(filter.getIdProdotto())) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("prodottoAttivato").get("prodottoAttivabile").get("idProdotto"),
                                filter.getIdProdotto()
                        )
                );
            }
            // idEnte
            if (filter.getIdEnte() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("prodottoAttivato").get("ente").get("idEnte"),
                                filter.getIdEnte()
                        )
                );
            }
            
            // ruoloProdotto
            String[] ruoli = filter.getRuoloProdotto();
            if (ruoli != null && ruoli.length > 0) {
            	
            	Predicate inner = builder.disjunction();
            	for (String ruolo: ruoli) {
            		
            		inner = builder.or(
            				inner,
            				builder.equal(
            						root.get("ruolo").get("codRuolo"),
            						ruolo
            						)
            				);
            	}
            	predicate = builder.and(
            			predicate,
            			inner);
            }

            return predicate;
        };

	}


	public static Specification<RichiestaProdotto> buildRichiestaProdottoFilterSpecification(RichiestaProdottoFilterDTO filter) {
        return (Specification<RichiestaProdotto>) (root, query, builder) -> {
            Predicate predicate = builder.conjunction();

            // idAttivazione
            if (filter.getIdAttivazione() != null) {
                predicate = builder.and(
                        predicate,
                        builder.equal(
                                root.get("prodottoAttivato").get("idAttivazione"),
                                filter.getIdAttivazione()
                        )
                );
            }
            
            return predicate;
        };

	}

    
}
