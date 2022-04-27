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

import java.util.Map;

import javax.validation.Valid;

import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteFilterDTO;

public interface UtenteService {
    /**
     * Get Utenti
     * @return Utenti
     */
	PagedData<UtenteDTO> getUtenti(int pageNumber, int pageSize, String sortProperty, String sortDirection);
	
	PagedData<UtenteDTO> getUtenti(UtenteFilterDTO utenteFilter, int pageNumber, int pageSize, String sortProperty, String sortDirection);

	UtenteDTO insertUtente(@Valid UtenteDTO newUtente);

	UtenteDTO updateUtente(@Valid UtenteDTO patchUtente, boolean isPatch) throws Exception;

	UtenteDTO getUtente(Integer id) throws Exception;

	MessaggioDTO sendMessaggioToUtenti(UtenteFilterDTO utenteFilter, MessaggioDTO messaggio) throws Exception;

	Map<String, Object> getUserValidityProperties(String fiscalCode);
}
