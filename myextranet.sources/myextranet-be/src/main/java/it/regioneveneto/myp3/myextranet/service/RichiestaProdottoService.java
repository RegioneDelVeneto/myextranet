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

import it.regioneveneto.myp3.myextranet.bean.UtentiProdottiAttivatiEnteExcelReportInput;
import it.regioneveneto.myp3.myextranet.web.dto.ComunicazioneRichiestaProdottoInputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.EnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.RichiestaProdottoWithStepsDTO;

public interface RichiestaProdottoService {

	RichiestaProdottoWithStepsDTO getRichiestaProdottoWithSteps(Integer idProdAttivRich) throws Exception;

	PagedData<RichiestaProdottoDTO> getRichiesteProdotto(RichiestaProdottoFilterDTO richiestaProdottoFilter,
			Integer pageNumber, Integer pageSize, String orderProperty, String orderDirection);

	EnteDTO getEnteByIdProdAttivRich(Integer idProdAttivRich) throws Exception;

	UtentiProdottiAttivatiEnteExcelReportInput buildUtentiProdottiAttivatiEnteExcelReportInput(Integer idProdAttivRich) throws Exception;

	MessaggioDTO sendMessaggioAboutRichiestaProdotto(Integer idProdAttivRich, ComunicazioneRichiestaProdottoInputDTO input) throws Exception;

}
