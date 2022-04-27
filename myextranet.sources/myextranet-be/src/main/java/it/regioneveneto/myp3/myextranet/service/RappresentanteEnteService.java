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

import java.util.List;

import javax.validation.Valid;

import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RappresentanteEnteFilterDTO;

public interface RappresentanteEnteService {
    /**
     * Get Rappresentanti 
     * @return Rappresentanti
     */
	PagedData<RappresentanteEnteDTO> getRappresentantiEnte(RappresentanteEnteFilterDTO rappresentanteEnteFilter, int pageNumber, int pageSize, String sortProperty, String sortDirection);

	RappresentanteEnteDTO insertRappresentanteEnte(@Valid RappresentanteEnteDTO newRappresentanteEnte, boolean notValidYet) throws Exception;

	RappresentanteEnteDTO updateRappresentanteEnte(@Valid RappresentanteEnteDTO patchRappresentanteEnte, boolean isPatch, boolean isFrontoffice, boolean isResubmission) throws Exception;

	RappresentanteEnteDTO getRappresentanteEnte(Integer idRappresentante) throws Exception;

	boolean isRappresentante(String codiceFiscale, Integer idEnte);

	PagedData<RappresentanteEnteDTO> getEntiRappresentati(UserWithAdditionalInfo user, Integer pageNumber,
			Integer pageSize, String orderProperty, String orderDirection);

	List<RappresentanteEnteDTO> prepareRappresentantiListDTO(List<RappresentanteEnteDTO> rappresentanti);

	RappresentanteEnteDTO setFlgAttivoOnRAP(RappresentanteEnteDTO RAP);

}
