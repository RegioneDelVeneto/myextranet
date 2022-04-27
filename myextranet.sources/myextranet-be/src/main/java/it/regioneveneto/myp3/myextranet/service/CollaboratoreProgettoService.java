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

import it.regioneveneto.myp3.myextranet.bean.CounterPerEntity;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoDettaglioFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.CollaboratoreProgettoFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.DettaglioCollaborazioneDTO;
import it.regioneveneto.myp3.myextranet.web.dto.MessaggioDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneMassivaDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

public interface CollaboratoreProgettoService {
    /**
     * Get Collaboratori 
     * @return Collaboratori
     */
	PagedData<CollaboratoreProgettoDTO> getCollaboratoriProgetto(CollaboratoreProgettoFilterDTO iscrittoEventoFilter, int pageNumber, int pageSize, String sortProperty, String sortDirection, Integer filterType);
	
	CollaboratoreProgettoDTO insertCollaboratoreProgetto(@Valid CollaboratoreProgettoDTO newCollaboratoreProgetto, boolean notValidYet) throws Exception;

	CollaboratoreProgettoDTO updateCollaboratoreProgetto(@Valid CollaboratoreProgettoDTO patchCollaboratoreProgetto, boolean isPatch) throws Exception;

	void executeOperationOnCollaboratoriProgetto(@Valid OperazioneMassivaDTO operazione) throws Exception;

	CollaboratoreProgettoDTO getCollaboratoreProgetto(Integer collaboratoreId) throws Exception;

	MessaggioDTO sendMessaggioToCollaboratoriProgetto(MessaggioDTO messaggio) throws Exception;

	MessaggioDTO sendMessaggioToCollaboratoriProgetto(CollaboratoreProgettoFilterDTO collaboratoreProgettFilter,
			MessaggioDTO messaggio) throws Exception;

	boolean isActiveCollaboratoreProgetto(String idProgetto, String codiceFiscale);

	PagedData<MyExtranetContent> getProgettiCollaborati(UserWithAdditionalInfo user, Integer pageNumber,
			Integer pageSize, String orderProperty, String orderDirection);

	PagedData<DettaglioCollaborazioneDTO> getDettaglioProgettiCollaborati(UserWithAdditionalInfo user,
			Integer pageNumber, Integer pageSize, String orderProperty, String orderDirection);

	DettaglioCollaborazioneDTO getDettaglioCollaborazioneProgetto(Integer collaboratoreId) throws Exception;

	DettaglioCollaborazioneDTO getDettaglioCollaborazioneProgetto(
			CollaboratoreProgettoDettaglioFilterDTO collaboratoreProgettoDettaglioFilter, UserWithAdditionalInfo user) throws Exception;

	List<CounterPerEntity> getPendingCollaborationRequestsPerProject(String[] ids);
}
