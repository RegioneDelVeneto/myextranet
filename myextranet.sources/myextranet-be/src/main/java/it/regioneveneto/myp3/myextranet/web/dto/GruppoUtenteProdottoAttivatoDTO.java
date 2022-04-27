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
package it.regioneveneto.myp3.myextranet.web.dto;

import java.io.Serializable;
import java.util.List;

public class GruppoUtenteProdottoAttivatoDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private RuoloProdottoDTO ruolo;
    private List<UtenteProdottoAttivatoDTO> utenti;

	public RuoloProdottoDTO getRuolo() {
		return ruolo;
	}

	public void setRuolo(RuoloProdottoDTO ruolo) {
		this.ruolo = ruolo;
	}

	public List<UtenteProdottoAttivatoDTO> getUtenti() {
		return utenti;
	}

	public void setUtenti(List<UtenteProdottoAttivatoDTO> utenti) {
		this.utenti = utenti;
	}

	@Override
	public String toString() {
		return "GruppoUtenteProdottoAttivatoDTO [ruolo=" + ruolo + ", utenti=" + utenti + "]";
	}
}
