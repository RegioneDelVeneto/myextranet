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
package it.regioneveneto.myp3.myextranet.service.impl;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.exception.MyExtranetException;
import it.regioneveneto.myp3.myextranet.exception.UserValidityException;
import it.regioneveneto.myp3.myextranet.model.AuditModel;
import it.regioneveneto.myp3.myextranet.model.AuditWithValidityModel;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivato;
import it.regioneveneto.myp3.myextranet.model.RuoloProdotto;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.model.UtenteProdottoAttivato;
import it.regioneveneto.myp3.myextranet.repository.RuoloProdottoRepository;
import it.regioneveneto.myp3.myextranet.repository.UtenteProdottoAttivatoRepository;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.service.UtenteProdottoAttivatoService;
import it.regioneveneto.myp3.myextranet.service.UtenteService;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.GruppoUtenteProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneUtenteProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RuoloProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteProdottoAttivatoDTO;

@Service
public class UtenteProdottoAttivatoServiceImpl implements UtenteProdottoAttivatoService {
    private static final Logger LOG = LoggerFactory.getLogger(UtenteProdottoAttivatoServiceImpl.class);

    @Autowired
    UtenteProdottoAttivatoRepository utenteProdottoAttivatoRepository;
    
    @Autowired
    RuoloProdottoRepository ruoloProdottoRepository;
    
    @Autowired
    SecurityService securityService;
    
    @Autowired
    UtenteService utenteService;



	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public List<GruppoUtenteProdottoAttivatoDTO> getUtentiProdottoAttivato(ProdottoAttivatoDTO prodottoAttivato, boolean flatten) {
		
		ProdottoAttivato prodotto = new ProdottoAttivato();
		prodotto.setIdAttivazione(prodottoAttivato.getIdAttivazione());
		List<UtenteProdottoAttivato> utenti = utenteProdottoAttivatoRepository.findAllByProdottoAttivatoAndFlgEnabledOrderByRuoloCodRuoloAscUtenteCognomeAscQuery(prodotto.getIdAttivazione(), 1);
		
		List<RuoloProdotto> ruoli = ruoloProdottoRepository.findAllByOrderByNumRuoloAsc();
				
		List<GruppoUtenteProdottoAttivatoDTO> gruppi = ruoli.stream()
				.map(ruolo -> {
					GruppoUtenteProdottoAttivatoDTO gruppo = new GruppoUtenteProdottoAttivatoDTO();
					gruppo.setRuolo(ObjectMapperUtils.map(ruolo, RuoloProdottoDTO.class));
					gruppo.setUtenti(new ArrayList<UtenteProdottoAttivatoDTO>());
					return gruppo;
				}).collect(Collectors.toList());
		
		
		if (utenti.size() == 0) {
			return gruppi;
		}
		
		GruppoUtenteProdottoAttivatoDTO activeGroup = null;
		String activeCodRuolo = null;
		for (UtenteProdottoAttivato utente : utenti) {
			
			if (utente.getUtente() != null) {				
				// skip invalid users
				try {
					securityService.checkUserValidity(utente.getUtente().getCodFiscale(), true, true, false);
				} catch (UserValidityException e) {
					LOG.debug("Skipping invalid user", e);
					continue;
				}
			}
			
			String currentCodRuolo = utente.getRuolo().getCodRuolo();
			
			if (!currentCodRuolo.equals(activeCodRuolo)) {
				activeGroup = gruppi.stream().filter(g -> g.getRuolo().getCodRuolo().equals(currentCodRuolo)).findAny().get();
				
				activeCodRuolo = currentCodRuolo;
			}
			UtenteProdottoAttivatoDTO utenteDto = ObjectMapperUtils.map(utente, UtenteProdottoAttivatoDTO.class);
			if (flatten) {
				// reduce depth
				this.flatten(utenteDto);
			}
			activeGroup.getUtenti().add(utenteDto);
		}
		
		return gruppi;
	}

	private void flatten(UtenteProdottoAttivatoDTO utente) {
		// utente
		if (utente.getUtente() != null) {
			UtenteDTO inner = utente.getUtente();
			utente.setCodFiscale(inner.getCodFiscale());
			utente.setNome(inner.getNome());
			utente.setCognome(inner.getCognome());
			if (utente.getEmail() == null) {
				utente.setEmail(inner.getEmail());
			}
			if (utente.getTelefono() == null) {
				utente.setTelefono(inner.getTelefono());
			}
			UtenteDTO ut = new UtenteDTO();
			ut.setIdUtente(inner.getIdUtente());
			utente.setUtente(ut);
		}
		// prodotto attivato
		if (utente.getProdottoAttivato() != null) {			
			ProdottoAttivatoDTO pa = new ProdottoAttivatoDTO();
			pa.setIdAttivazione(utente.getProdottoAttivato().getIdAttivazione());
			utente.setProdottoAttivato(pa);
		}
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = true)
	public UtenteProdottoAttivatoDTO getUtenteProdottoAttivato(Integer idUtenteProd) throws Exception {
		Optional<UtenteProdottoAttivato> utente = utenteProdottoAttivatoRepository.findById(idUtenteProd);

		if(utente.isPresent()) {
			UtenteProdottoAttivatoDTO utenteData = ObjectMapperUtils.map(utente.get(), UtenteProdottoAttivatoDTO.class);
			return utenteData;
		} else {
			throw new Exception("Utente not found");
		}
	}
	
	@Override
	@Transactional(rollbackFor = Exception.class, readOnly = false)
	public void aggiornaListaUtentiProdottoAttivato(@Valid List<OperazioneUtenteProdottoAttivatoDTO> operazioni)
			throws Exception {
		
		for (OperazioneUtenteProdottoAttivatoDTO operazione : operazioni) {
			switch (operazione.getTipoOperazione()) {
			case Constants.UTENTE_PRODOTTO_ATTIVATO_OPERAZIONE_ELIMINA:
				eliminaUtenteProdottoAttivato(operazione.getUtenteProdottoAttivato());
				break;
			case Constants.UTENTE_PRODOTTO_ATTIVATO_OPERAZIONE_MODIFICA:
				modificaUtenteProdottoAttivato(operazione.getUtenteProdottoAttivato());
				break;

			default:
				break;
			}
		}
		
	}

	private void modificaUtenteProdottoAttivato(UtenteProdottoAttivatoDTO utenteProdottoAttivato) throws Exception {
		if (utenteProdottoAttivato == null) {
			throw new MyExtranetException("Impossibile modificare un record null");
		}
		
		if (utenteProdottoAttivato.getIdUtenteProd() == null) {
			// insert
			insertUtenteProdottoAttivato(utenteProdottoAttivato);
		} else {
			// update
			updateProdottoAttivato(utenteProdottoAttivato, false);
		}
	}
	
	private UtenteProdottoAttivatoDTO updateProdottoAttivato(UtenteProdottoAttivatoDTO patchUtenteProdottoAttivato, boolean isPatch) throws Exception {
		
		// retrieve original
		Optional<UtenteProdottoAttivato> originalUtenteProdottoAttivatoOptional = utenteProdottoAttivatoRepository.findById(patchUtenteProdottoAttivato.getIdUtenteProd());
		if (!originalUtenteProdottoAttivatoOptional.isPresent()) {
			throw new Exception("Utente prodotto attivato not found");
		}
		UtenteProdottoAttivato originalUtenteProdottoAttivato = originalUtenteProdottoAttivatoOptional.get();

		UtenteProdottoAttivato utenteProdottoAttivato = UtenteProdottoAttivatoDTO.patchUtenteProdottoAttivato(patchUtenteProdottoAttivato, originalUtenteProdottoAttivato, isPatch, true);
		
		// set audit fields
		AuditModel audit = (AuditModel) utenteProdottoAttivato;
		AuditUtils.fillAudit(audit);
		
		// cleanup redundant fields
		cleanupFields(utenteProdottoAttivato);
				
		UtenteProdottoAttivato savedUtenteProdottoAttivato = utenteProdottoAttivatoRepository.save(utenteProdottoAttivato);
		
		return ObjectMapperUtils.map(savedUtenteProdottoAttivato, UtenteProdottoAttivatoDTO.class);

	}
	
	private void cleanupFields(UtenteProdottoAttivato utenteProdottoAttivato) throws Exception {
		
		if (utenteProdottoAttivato.getUtente() != null) {
			UtenteDTO utente = utenteService.getUtente(utenteProdottoAttivato.getUtente().getIdUtente());
			// if there is an Utente, don't fill nome, cognome...
			utenteProdottoAttivato.setNome(null);
			utenteProdottoAttivato.setCognome(null);
			// ... and copy codFiscale from Utente
			utenteProdottoAttivato.setCodFiscale(utente.getCodFiscale());
		} else {
			// if an Utente is not provided there must be codFiscale, nome, cognome, email, telefono
			if (
				!StringUtils.hasText(utenteProdottoAttivato.getCodFiscale()) ||
				!StringUtils.hasText(utenteProdottoAttivato.getNome()) ||
				!StringUtils.hasText(utenteProdottoAttivato.getCognome()) ||
				!StringUtils.hasText(utenteProdottoAttivato.getEmail()) ||
				!StringUtils.hasText(utenteProdottoAttivato.getTelefono()) 
				) {
				throw new MyExtranetException("I campi codFiscale, nome, cognome, email, telefono sono obbligatori");
			}
		}
		
		
	}

	private UtenteProdottoAttivatoDTO insertUtenteProdottoAttivato(@Valid UtenteProdottoAttivatoDTO newUtenteProdottoAttivato) throws Exception {
		
		UtenteProdottoAttivato utenteProdottoAttivato = ObjectMapperUtils.map(newUtenteProdottoAttivato, UtenteProdottoAttivato.class);
		
		// some checks
		if (utenteProdottoAttivato.getProdottoAttivato() == null || utenteProdottoAttivato.getRuolo() == null) {
			throw new Exception("ProdottoAttivato e Ruolo sono obbligatori");
		}
		
		// add audit fields
		AuditModel audit = (AuditModel) utenteProdottoAttivato;
		AuditUtils.fillAudit(audit);
		
		// cleanup redundant fields
		cleanupFields(utenteProdottoAttivato);
				
		// check if there is another record for the same idAttivazione and codFiscale and codRuolo
		checkForExistingRecord(utenteProdottoAttivato);
		
		UtenteProdottoAttivato savedUtenteProdottoAttivato = utenteProdottoAttivatoRepository.save(utenteProdottoAttivato);
		
		return ObjectMapperUtils.map(savedUtenteProdottoAttivato, UtenteProdottoAttivatoDTO.class);
	}

	private void checkForExistingRecord(UtenteProdottoAttivato utenteProdottoAttivato) throws Exception {
		ProdottoAttivato prodottoAttivato = utenteProdottoAttivato.getProdottoAttivato();
		String codFiscale = utenteProdottoAttivato.getCodFiscale();
		RuoloProdotto ruolo = utenteProdottoAttivato.getRuolo();
		
		
		List<UtenteProdottoAttivato> records = utenteProdottoAttivatoRepository.findRecordsByProdottoAttivatoAndCodFiscaleAndRuoloProdottoAndFlgEnabled(prodottoAttivato, codFiscale, ruolo, 1);
		if (records != null && records.size() > 0) {
			throw new Exception("UtenteProdottoAttivato already present");
		}
	}

	private void eliminaUtenteProdottoAttivato(UtenteProdottoAttivatoDTO utenteProdottoAttivatoDTO) throws Exception {
		
		if (utenteProdottoAttivatoDTO == null || utenteProdottoAttivatoDTO.getIdUtenteProd() == null) {
			throw new MyExtranetException("Impossibile eliminare un record senza id");
		}		
		// retrieve original
		Optional<UtenteProdottoAttivato> originalUtenteProdottoAttivatoOptional = utenteProdottoAttivatoRepository.findById(utenteProdottoAttivatoDTO.getIdUtenteProd());
		
		if (!originalUtenteProdottoAttivatoOptional.isPresent()) {
			throw new Exception("Utente prodotto attivato not found");
		}		
		
		UtenteProdottoAttivato originalUtenteProdottoAttivato = originalUtenteProdottoAttivatoOptional.get();
		
		originalUtenteProdottoAttivato.setFlgEnabled(0);
		
		// set audit fields
		AuditModel audit = (AuditModel) originalUtenteProdottoAttivato;
		AuditUtils.fillAudit(audit);	
				
		UtenteProdottoAttivato savedUtenteProdottoAttivato = utenteProdottoAttivatoRepository.save(originalUtenteProdottoAttivato);
				
	}
}
