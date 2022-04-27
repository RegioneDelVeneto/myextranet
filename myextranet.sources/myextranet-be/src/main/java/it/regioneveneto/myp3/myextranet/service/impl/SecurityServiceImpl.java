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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.regioneveneto.myp3.myextranet.config.WebSecurityConfig;
import it.regioneveneto.myp3.myextranet.exception.PermissionException;
import it.regioneveneto.myp3.myextranet.exception.UserValidityException;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.security.Acl;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.service.CollaboratoreProgettoService;
import it.regioneveneto.myp3.myextranet.service.IscrittoEventoService;
import it.regioneveneto.myp3.myextranet.service.MyPortalService;
import it.regioneveneto.myp3.myextranet.service.ProdottoAttivatoService;
import it.regioneveneto.myp3.myextranet.service.RappresentanteEnteService;
import it.regioneveneto.myp3.myextranet.service.SecurityService;
import it.regioneveneto.myp3.myextranet.service.UtenteService;
import it.regioneveneto.myp3.myextranet.utils.Constants;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteProdottoAttivatoDTO;

@Service
public class SecurityServiceImpl implements SecurityService {
	
	private static final Logger LOG = LoggerFactory.getLogger(SecurityServiceImpl.class);
	
	@Autowired
	UtenteService utenteService;
	
	@Autowired
	MyPortalService myPortalService;
	
	@Autowired
	IscrittoEventoService iscrittoEventoService;
	
	@Autowired
	CollaboratoreProgettoService collaboratoreProgettoService;
	
	@Autowired
	ProdottoAttivatoService prodottoAttivatoService;
	
	@Autowired
	RappresentanteEnteService rappresentanteEnteService;

	@Override
	public void checkPermission(UserWithAdditionalInfo user, String acl, String permission) throws PermissionException {
		
		List<Acl> acls = user.getAcls();
		if (acls == null) {
			throw new PermissionException(String.format("User %s has no acls", user.getUsername()));
		}

		Optional<Acl> foundAclOptional = acls.stream()
			.filter(a -> a.getAcl().equals(acl)).findFirst();
			
		if (!foundAclOptional.isPresent()) {
			throw new PermissionException(String.format("User %s does not have the required acl \"%s\"", user.getUsername(), acl));
		}
		
		boolean foundPermission = Arrays.asList(foundAclOptional.get().getPermissions())
			.stream()
			.anyMatch(p -> p.equals(permission));
			
		if (!foundPermission) {
			throw new PermissionException(String.format("User %s does not have the required permission \"%s\" in acl \"%s\"", user.getUsername(), permission, acl));
		}
		
		// Everything is OK
		LOG.debug(String.format("Successfully checked permission \"%s\" for acl \"%s\" for user %s", permission, acl, user.getUsername()));
	}

	@Override
	public void checkUserValidity(UserWithAdditionalInfo user) throws UserValidityException {
		checkUserValidity(user, true, true, true);
	}
	
	@Override
	public void checkUserValidity(UserWithAdditionalInfo user, boolean checkUserPresent, boolean checkUserNotExpired, boolean checkTenantNotExpired) throws UserValidityException {

		checkUserValidity(user.getCodiceFiscale(), checkUserPresent, checkUserNotExpired, checkTenantNotExpired);
		
		// Everything is OK
		LOG.debug(String.format("Successfully checked validity for user %s", user.getUsername()));
	}
	
	@Override
	public void checkUserValidity(String codiceFiscale, boolean checkUserPresent, boolean checkUserNotExpired, boolean checkTenantNotExpired) throws UserValidityException {
		
		Map<String, Object> userValidityProperties = utenteService.getUserValidityProperties(codiceFiscale);
		
		if (checkUserPresent && userValidityProperties.get("userId") == null) {
			throw new UserValidityException("User not registered");
		}
		
		if (checkUserNotExpired && (boolean) userValidityProperties.get("userExpired")) {
			throw new UserValidityException("User expired");
		}
		
		if (checkTenantNotExpired && (boolean) userValidityProperties.get("tenantExpired")) {
			throw new UserValidityException("Tenant expired");
		}


	}

	@Override
	public void checkIsDataOwner(UtenteDTO utente, UserWithAdditionalInfo user) throws PermissionException {
		Integer idUtente = utente.getIdUtente();
		
		if (idUtente != null) {
			UtenteDTO storedUtente;
			try {
				storedUtente = utenteService.getUtente(idUtente);
			} catch (Exception e) {
				throw new PermissionException(Constants.API_ERROR_SECURITY_CANT_GET_USER_MESSAGE, e);
			}
			
			if (!user.getCodiceFiscale().equalsIgnoreCase(storedUtente.getCodFiscale())) {
				// trying to get/set someone else's data?!
				throw new PermissionException(Constants.API_ERROR_SECURITY_NOT_LOGGED_IN_USER_MESSAGE);
			}
			
			if (utente.getCodFiscale() != null && !user.getCodiceFiscale().equalsIgnoreCase(utente.getCodFiscale())) {
				// trying to get/set someone else's data?!
				throw new PermissionException(Constants.API_ERROR_SECURITY_NOT_OWN_DATA_MESSAGE);
			}
		} else {
			if (!user.getCodiceFiscale().equalsIgnoreCase(utente.getCodFiscale())) {
				// trying to get/set someone else's data?!
				throw new PermissionException(Constants.API_ERROR_SECURITY_NOT_OWN_DATA_MESSAGE);
			}
		}
	}
	
	@Override
	public void checkIsDataOwner(IscrittoEventoDTO iscritto, UserWithAdditionalInfo user) throws PermissionException {
		if (iscritto == null) return;
		
		IscrittoEventoDTO storedIscritto = null;
		
		Integer idIscritto = iscritto.getIdIscritto();
		if (idIscritto != null) {
			// it should exist
			
			try {
				storedIscritto = iscrittoEventoService.getIscrittoEvento(idIscritto);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		UtenteDTO iscrittoUtente = iscritto.getUtente();
		if (iscrittoUtente == null && storedIscritto != null) {
			iscrittoUtente = storedIscritto.getUtente();
		}
		
		if (iscrittoUtente != null) {
			Integer idUtente = iscrittoUtente.getIdUtente();
			
			UtenteDTO utente;
			try {
				utente = utenteService.getUtente(idUtente);
			} catch (Exception e) {
				throw new PermissionException("Unable to get utente", e);
			}
			
			if (!user.getCodiceFiscale().equalsIgnoreCase(utente.getCodFiscale())) {
				throw new PermissionException(Constants.API_ERROR_SECURITY_NOT_LOGGED_IN_USER_MESSAGE);
			}
		} else {
			
			throw new PermissionException(Constants.API_ERROR_SECURITY_NOT_OWN_DATA_MESSAGE);
		}
		
	}

	@Override
	public void checkIsDataOwner(CollaboratoreProgettoDTO collaboratore, UserWithAdditionalInfo user) 	throws PermissionException {
		CollaboratoreProgettoDTO storedCollaboratore = null;
		
		Integer idCollaboratore = collaboratore.getIdCollab() ;
		if (idCollaboratore != null) {
			// it should exist
			
			try {
				storedCollaboratore = collaboratoreProgettoService.getCollaboratoreProgetto(idCollaboratore);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		UtenteDTO collaboratoreUtente = collaboratore.getUtente();
		if (collaboratoreUtente == null && storedCollaboratore != null) {
			collaboratoreUtente = storedCollaboratore.getUtente();
		}
		
		if (collaboratoreUtente != null) {
			Integer idUtente = collaboratoreUtente.getIdUtente();
			
			UtenteDTO utente;
			try {
				utente = utenteService.getUtente(idUtente);
			} catch (Exception e) {
				throw new PermissionException("Unable to get utente", e);
			}
			
			if (!user.getCodiceFiscale().equalsIgnoreCase(utente.getCodFiscale())) {
				throw new PermissionException(Constants.API_ERROR_SECURITY_NOT_LOGGED_IN_USER_MESSAGE);
			}
		} else {
			
			throw new PermissionException(Constants.API_ERROR_SECURITY_NOT_OWN_DATA_MESSAGE);
		}

	}

	@Override
	/**
	 * Check that the progetto is specified and that the user is a confirmed collaboratore progetto of the progetto
	 */
	public void checkUserBelongsToProgetto(String idProgetto, UserWithAdditionalInfo user) throws PermissionException {
        // check that the progetto is specified
		if (idProgetto == null) {
			throw new PermissionException("No idProgetto is specified");
		}
		
		MyExtranetContent project = null;
		try {
			project = myPortalService.getContentById(idProgetto);
		} catch (Exception e) {
			throw new PermissionException("Cannot access to specified project", e);
		}
		
		if (project != null) {
			// check that the user is a confirmed collaboratore progetto of the progetto
			boolean isActiveCollaboratoreProgetto = collaboratoreProgettoService.isActiveCollaboratoreProgetto(idProgetto, user.getCodiceFiscale());
			if (!isActiveCollaboratoreProgetto) {
				throw new PermissionException("The user is not an active collaborator to the specified project");
			}
			
		} else {
			throw new PermissionException("Could not find the specified project");
		}
        

		
	}

	@Override
	public void checkIsDataOwner(EnteDTO ente, UserWithAdditionalInfo user) throws PermissionException {
		
		// check if the utente is a representative of the ente
		boolean isRappresentante = rappresentanteEnteService.isRappresentante(user.getCodiceFiscale(), ente.getIdEnte());
		
		if (!isRappresentante) {
			throw new PermissionException("Current user is not a valid representative");
		}
	}
	
	@Override
	public void checkIsDataOwner(ProdottoAttivatoDTO prodottoAttivato, UserWithAdditionalInfo user) throws PermissionException {
		ProdottoAttivatoDTO storedProdottoAttivato = null;
		
		// check if the utente is a representative of the ente
		EnteDTO ente = prodottoAttivato.getEnte();
		boolean isRappresentante = rappresentanteEnteService.isRappresentante(user.getCodiceFiscale(), ente.getIdEnte());
		
		if (!isRappresentante) {
			throw new PermissionException("Current user is not a valid representative");
		}
	}

	@Override
	public void checkIsDataOwner(RappresentanteEnteDTO rappresentante, UserWithAdditionalInfo user)
			throws PermissionException {
		RappresentanteEnteDTO storedRappresentante = null;
		
		Integer idRappresentante = rappresentante.getIdRappr();
		if (idRappresentante != null) {
			// it should exist
			
			try {
				storedRappresentante = rappresentanteEnteService.getRappresentanteEnte(idRappresentante);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		UtenteDTO rappresentanteUtente = rappresentante.getUtente();
		if (rappresentanteUtente == null && storedRappresentante != null) {
			rappresentanteUtente = storedRappresentante.getUtente();
		}
		
		if (rappresentanteUtente != null) {
			Integer idUtente = rappresentanteUtente.getIdUtente();
			
			UtenteDTO utente;
			try {
				utente = utenteService.getUtente(idUtente);
			} catch (Exception e) {
				throw new PermissionException("Unable to get utente", e);
			}
			
			if (!user.getCodiceFiscale().equalsIgnoreCase(utente.getCodFiscale())) {
				// trying to get/set someone else's data?!
				throw new PermissionException(Constants.API_ERROR_SECURITY_NOT_LOGGED_IN_USER_MESSAGE);
			}
		} else {
			
			throw new PermissionException(Constants.API_ERROR_SECURITY_NOT_OWN_DATA_MESSAGE);
		}
		
	}

	@Override
	public void checkIsDataOwner(UtenteProdottoAttivatoDTO utente, UserWithAdditionalInfo user)
			throws PermissionException {
		
		ProdottoAttivatoDTO prodottoAttivato = utente.getProdottoAttivato();
				
		EnteDTO ente = prodottoAttivato.getEnte();
		boolean isRappresentante = rappresentanteEnteService.isRappresentante(user.getCodiceFiscale(), ente.getIdEnte());
		
		if (!isRappresentante) {
			throw new PermissionException("Current user is not a valid representative");
		}
	}

}
