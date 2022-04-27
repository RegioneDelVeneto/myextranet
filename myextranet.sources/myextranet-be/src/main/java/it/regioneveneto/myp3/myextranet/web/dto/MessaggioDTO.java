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

public class MessaggioDTO implements Serializable {
	private Integer idMessaggio;
	private String tipo;
	private String area;
	private String idContenuto;
	private String destinatario;
	private String indirizzo;
	private String titolo;
	private String messaggio;
	protected LocalDate dtInvio;

	public Integer getIdMessaggio() {
		return idMessaggio;
	}

	public void setIdMessaggio(Integer idMessaggio) {
		this.idMessaggio = idMessaggio;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getIdContenuto() {
		return idContenuto;
	}

	public void setIdContenuto(String idContenuto) {
		this.idContenuto = idContenuto;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

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

	public LocalDate getDtInvio() {
		return dtInvio;
	}

	public void setDtInvio(LocalDate dtInvio) {
		this.dtInvio = dtInvio;
	}
	

	public String getIdEvento() {
		return idContenuto;
	}

	public void setIdEvento(String idEvento) {
		this.idContenuto = idEvento;
	}

	@Override
	public String toString() {
		return "MessaggioDTO [idMessaggio=" + idMessaggio + ", tipo=" + tipo + ", area=" + area + ", idContenuto="
				+ idContenuto + ", destinatario=" + destinatario + ", indirizzo=" + indirizzo + ", titolo=" + titolo
				+ ", messaggio=" + messaggio + ", dtInvio=" + dtInvio + "]";
	}
	
}
