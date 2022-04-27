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
package it.regioneveneto.myp3.myextranet.bean;

public class StatPartecipantiRow {

	private String evento;
	private String partecipante;
	private String ente;
	private String categoriaEnte;

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public String getPartecipante() {
		return partecipante;
	}

	public void setPartecipante(String partecipante) {
		this.partecipante = partecipante;
	}

	public String getEnte() {
		return ente;
	}

	public void setEnte(String ente) {
		this.ente = ente;
	}

	public String getCategoriaEnte() {
		return categoriaEnte;
	}

	public void setCategoriaEnte(String categoriaEnte) {
		this.categoriaEnte = categoriaEnte;
	}

	public StatPartecipantiRow() {
		
	}

	@Override
	public String toString() {
		return "StatPartecipantiRow [evento=" + evento + ", partecipante=" + partecipante + ", ente=" + ente
				+ ", categoriaEnte=" + categoriaEnte + "]";
	}

}
