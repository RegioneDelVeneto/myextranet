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
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.Parameter;

public class MessaggioFilterDTO implements Serializable {

	private String tipo;
	private String area;
	private String idContenuto;
	private String destinatario;
	private String titolo;
	private String messaggio;
    @Parameter(description = "Format should be yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	protected LocalDate dtInvioDa;
    @Parameter(description = "Format should be yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
	protected LocalDate dtInvioA;

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

	public LocalDate getDtInvioDa() {
		return dtInvioDa;
	}

	public void setDtInvioDa(LocalDate dtInvioDa) {
		this.dtInvioDa = dtInvioDa;
	}

	public LocalDate getDtInvioA() {
		return dtInvioA;
	}

	public void setDtInvioA(LocalDate dtInvioA) {
		this.dtInvioA = dtInvioA;
	}

	@Override
	public String toString() {
		return "MessaggioFilterDTO [tipo=" + tipo + ", area=" + area + ", idContenuto=" + idContenuto
				+ ", destinatario=" + destinatario + ", titolo=" + titolo + ", messaggio=" + messaggio + ", dtInvioDa="
				+ dtInvioDa + ", dtInvioA=" + dtInvioA + "]";
	}

}
