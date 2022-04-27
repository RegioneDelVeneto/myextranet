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

import it.regioneveneto.myp3.myextranet.bean.StatEntiProdottiRow;
import it.regioneveneto.myp3.myextranet.bean.StatServiziErogatiRow;
import it.regioneveneto.myp3.myextranet.web.dto.DatiRichiestaProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.GenericAvanzamentoStepRichiestaInputDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileEnteDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivabileEnteFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoComunicazioneFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoWithStatusDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoWithUsersDTO;

public interface ProdottoAttivatoService {
    /**
     * Get Prodotti 
     * @return Prodotti
     */
	PagedData<ProdottoAttivatoWithStatusDTO> getProdottiAttivati(ProdottoAttivatoFilterDTO prodottoAttivabileFilter, int pageNumber, int pageSize, String sortProperty, String sortDirection);

	ProdottoAttivatoDTO insertProdottoAttivato(@Valid ProdottoAttivatoDTO newProdottoAttivato) throws Exception;

	ProdottoAttivatoDTO updateProdottoAttivato(@Valid ProdottoAttivatoDTO patchProdottoAttivato, boolean isPatch) throws Exception;

	ProdottoAttivatoDTO getProdottoAttivato(Integer idAttivazione) throws Exception;
	
	ProdottoAttivatoWithUsersDTO getProdottoAttivatoWithUsers(Integer idAttivazione) throws Exception;

	PagedData<ProdottoAttivabileEnteDTO> getProdottiAttivabiliEnte(
			ProdottoAttivabileEnteFilterDTO prodottoAttivabileEnteFilter, Integer pageNumber, Integer pageSize,
			String orderProperty, String orderDirection);

	List<StatEntiProdottiRow> getStatEntiProdottiRows();
	
	List<StatServiziErogatiRow> getStatServiziErogatiRows();

	MessaggioDTO sendMessaggioToUtentiProdottiAttivato(
			ProdottoAttivatoComunicazioneFilterDTO prodottoAttivatoComunicazioneFilter, MessaggioDTO messaggio) throws Exception;

	ProdottoAttivatoDTO genericAvanzamentoStepRichiesta(GenericAvanzamentoStepRichiestaInputDTO input) throws Exception;

	DatiRichiestaProdottoAttivatoDTO getDatiRichiestaProdottoAttivato(Integer idEnte, Integer idProdottoAtt,
			String codTipoRich) throws Exception;

	ProdottoAttivatoWithStatusDTO getProdottoAttivatoWithStatus(Integer idAttivazione) throws Exception;

}
