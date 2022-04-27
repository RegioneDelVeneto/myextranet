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
import java.time.LocalDate;

import it.regioneveneto.myp3.myextranet.model.UtenteMessaggi;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class UtenteMessaggiDTO implements Serializable {
	private UtenteDTO utente;
	private MessaggioDTO messaggio;
	private Integer flgOnline;
	private Integer flgReadOnline;
	protected LocalDate dtReadOnline;

    //Getter and setter
	
	public UtenteDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteDTO utente) {
		this.utente = utente;
	}

	public MessaggioDTO getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(MessaggioDTO messaggio) {
		this.messaggio = messaggio;
	}

	public Integer getFlgOnline() {
		return flgOnline;
	}

	public void setFlgOnline(Integer flgOnline) {
		this.flgOnline = flgOnline;
	}

	public Integer getFlgReadOnline() {
		return flgReadOnline;
	}

	public void setFlgReadOnline(Integer flgReadOnline) {
		this.flgReadOnline = flgReadOnline;
	}

	public LocalDate getDtReadOnline() {
		return dtReadOnline;
	}

	public void setDtReadOnline(LocalDate dtReadOnline) {
		this.dtReadOnline = dtReadOnline;
	}
	
	public static UtenteMessaggi patchUtenteMessaggi(UtenteMessaggiDTO patchUtenteMessaggi, UtenteMessaggi originalUtenteMessaggi, boolean ignoreNulls, boolean cloneOriginal) {
		
		UtenteMessaggi utenteMessaggiResult;
		
		if (cloneOriginal) {
			originalUtenteMessaggi = ObjectMapperUtils.map(originalUtenteMessaggi, new UtenteMessaggi());
		}
		
		if(ignoreNulls) {
			utenteMessaggiResult = originalUtenteMessaggi;
			
			// flgOnline
			if(patchUtenteMessaggi.getFlgOnline() != null) {
				originalUtenteMessaggi.setFlgOnline(patchUtenteMessaggi.getFlgOnline());
			}
			// flgReadOnline
			if(patchUtenteMessaggi.getFlgReadOnline() != null) {
				originalUtenteMessaggi.setFlgReadOnline(patchUtenteMessaggi.getFlgReadOnline());
			}
			// dtReadOnline
			if(patchUtenteMessaggi.getDtReadOnline() != null) {
				originalUtenteMessaggi.setDtReadOnline(patchUtenteMessaggi.getDtReadOnline());
			}

			
		} else {
			utenteMessaggiResult = ObjectMapperUtils.map(patchUtenteMessaggi, originalUtenteMessaggi);
		}
		
		return utenteMessaggiResult;
	}

}
