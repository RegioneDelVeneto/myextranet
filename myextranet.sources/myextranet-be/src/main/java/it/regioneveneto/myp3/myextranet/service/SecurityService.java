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
package it.regioneveneto.myp3.myextranet.service;

import it.regioneveneto.myp3.myextranet.exception.PermissionException;
import it.regioneveneto.myp3.myextranet.exception.UserValidityException;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteProdottoAttivatoDTO;

public interface SecurityService {

	void checkPermission(UserWithAdditionalInfo user, String acl, String permission) throws PermissionException;

	void checkUserValidity(UserWithAdditionalInfo user) throws UserValidityException;

	void checkIsDataOwner(IscrittoEventoDTO iscritto, UserWithAdditionalInfo user) throws PermissionException;

	void checkIsDataOwner(UtenteDTO utente, UserWithAdditionalInfo user) throws PermissionException;

	void checkIsDataOwner(CollaboratoreProgettoDTO patchCollaboratoreProgetto, UserWithAdditionalInfo user) throws PermissionException;

	void checkIsDataOwner(ProdottoAttivatoDTO prodottoAttivato, UserWithAdditionalInfo user) throws PermissionException;

	void checkIsDataOwner(RappresentanteEnteDTO updateRappresentanteEnte, UserWithAdditionalInfo user) throws PermissionException;
	
	void checkIsDataOwner(UtenteProdottoAttivatoDTO utente, UserWithAdditionalInfo user) throws PermissionException;

	void checkIsDataOwner(EnteDTO ente, UserWithAdditionalInfo user) throws PermissionException;

	void checkUserBelongsToProgetto(String idProgetto, UserWithAdditionalInfo user) throws PermissionException;

	void checkUserValidity(UserWithAdditionalInfo user, boolean checkUserPresent, boolean checkUserNotExpired,
			boolean checkTenantNotExpired) throws UserValidityException;

	void checkUserValidity(String codiceFiscale, boolean checkUserPresent, boolean checkUserNotExpired,
			boolean checkTenantNotExpired) throws UserValidityException;

}
