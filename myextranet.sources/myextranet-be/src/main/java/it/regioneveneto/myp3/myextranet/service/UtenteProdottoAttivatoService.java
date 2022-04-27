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

import it.regioneveneto.myp3.myextranet.web.dto.GruppoUtenteProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.OperazioneUtenteProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.ProdottoAttivatoDTO;
import it.regioneveneto.myp3.myextranet.web.dto.UtenteProdottoAttivatoDTO;

public interface UtenteProdottoAttivatoService {
    /**
     * Get Utenti prodotto attivato raggruppati per ruolo
     * @return utenti
     */
    List<GruppoUtenteProdottoAttivatoDTO> getUtentiProdottoAttivato(ProdottoAttivatoDTO prodottoAttivato, boolean flatten);

	UtenteProdottoAttivatoDTO getUtenteProdottoAttivato(Integer idUtenteProd) throws Exception;
	
	void aggiornaListaUtentiProdottoAttivato(@Valid List<OperazioneUtenteProdottoAttivatoDTO> operazioni) throws Exception;
    
}
