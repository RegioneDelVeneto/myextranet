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

import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.List;

import javax.validation.Valid;

import it.regioneveneto.myp3.myextranet.bean.StatEventiRow;
import it.regioneveneto.myp3.myextranet.bean.StatPartecipantiRow;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.web.dto.DettaglioIscrizioneDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoDettaglioFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.IscrittoEventoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneMassivaDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

public interface IscrittoEventoService {
    /**
     * Get Iscritti 
     * @return Iscritti
     */
	PagedData<IscrittoEventoDTO> getIscrittiEvento(IscrittoEventoFilterDTO iscrittoEventoFilter, int pageNumber, int pageSize, String sortProperty, String sortDirection);

	IscrittoEventoDTO insertIscrittoEvento(@Valid IscrittoEventoDTO newIscrittoEvento) throws Exception;

	IscrittoEventoDTO updateIscrittoEvento(@Valid IscrittoEventoDTO patchIscrittoEvento, boolean isPatch) throws Exception;

	IscrittoEventoDTO deleteIscrittoEvento(Integer iscrittoId) throws Exception;

	void executeOperationOnIscrittiEvento(@Valid OperazioneMassivaDTO operazione) throws Exception;

	void executeOperationOnIscrittiEvento(@Valid OperazioneMassivaDTO operazione, OutputStream outputStream) throws Exception;
	
	ByteArrayInputStream generateReportFoglioPresenze(String idEvento);
	
	ByteArrayInputStream generateReportAttestato(Integer idIscritto) throws Exception;

	IscrittoEventoDTO getIscrittoEvento(Integer iscrittoId) throws Exception;

	MessaggioDTO sendMessaggioToIscritti(MessaggioDTO messaggio) throws Exception;

	int getProgressUnitMultiplier();

	ByteArrayInputStream generatePDFReportAttestato(Integer idIscritto) throws Exception;

	Integer getNumIscritti(String idEvento);

	Integer getNumIscrittiInPresenza(String idEvento);
	
	Integer getNumPostiDisponibili(String idEvento);

	PagedData<MyExtranetContent> getEventiIscritti(UserWithAdditionalInfo user, Integer pageNumber, Integer pageSize,
			String orderProperty, String orderDirection);

	DettaglioIscrizioneDTO getDettaglioIscrizione(IscrittoEventoDettaglioFilterDTO iscrittoEventoDettaglioFilter, UserWithAdditionalInfo user) throws Exception;
	
	List<StatEventiRow> getStatEventiRows(List<String> eventIds);
	
	List<StatPartecipantiRow> getStatPartecipantiRows(List<String> eventIds);
}
