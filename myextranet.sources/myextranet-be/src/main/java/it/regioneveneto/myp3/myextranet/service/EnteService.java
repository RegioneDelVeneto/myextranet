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

import javax.validation.Valid;

import it.regioneveneto.myp3.myextranet.exception.EnteExpiredException;
import it.regioneveneto.myp3.myextranet.exception.EnteNotPresentException;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

public interface EnteService {
    /**
     * Get Enti
     * @return Enti
     */

	PagedData<EnteDTO> getEnti(int pageNumber, int pageSize, String sortProperty, String sortDirection);
	
	PagedData<EnteDTO> getEnti(EnteFilterDTO enteFilter, int pageNumber, int pageSize, String sortProperty, String sortDirection);

	void checkEnteIsValid(EnteDTO ente) throws EnteExpiredException, EnteNotPresentException;
	
	EnteDTO insertEnte(@Valid EnteDTO newEnte) throws Exception;

	EnteDTO updateEnte(@Valid EnteDTO patchEnte, boolean isPatch) throws Exception;

	EnteDTO getEnte(Integer idEnte) throws Exception;
}
