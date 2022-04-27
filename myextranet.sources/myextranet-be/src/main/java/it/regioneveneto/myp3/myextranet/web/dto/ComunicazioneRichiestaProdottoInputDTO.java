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

public class ComunicazioneRichiestaProdottoInputDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	protected String indirizzo;
	protected String titolo;
	protected String messaggio;
	protected Integer flgAllegaUtentiRich;

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	public Integer getFlgAllegaUtentiRich() {
		return flgAllegaUtentiRich;
	}

	public void setFlgAllegaUtentiRich(Integer flgAllegaUtentiRich) {
		this.flgAllegaUtentiRich = flgAllegaUtentiRich;
	}

	public ComunicazioneRichiestaProdottoInputDTO() {
		
	}

}
