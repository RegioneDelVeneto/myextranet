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

public class StatEventiRow {

	private String evento;
	private int numIscritti;
	private int numPartecipanti;
	private int numEnti;

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public int getNumIscritti() {
		return numIscritti;
	}

	public void setNumIscritti(int numIscritti) {
		this.numIscritti = numIscritti;
	}

	public int getNumPartecipanti() {
		return numPartecipanti;
	}

	public void setNumPartecipanti(int numPartecipanti) {
		this.numPartecipanti = numPartecipanti;
	}

	public int getNumEnti() {
		return numEnti;
	}

	public void setNumEnti(int numEnti) {
		this.numEnti = numEnti;
	}

	public StatEventiRow() {
		
	}

	public StatEventiRow(String evento, int numIscritti, int numPartecipanti, int numEnti) {
		super();
		this.evento = evento;
		this.numIscritti = numIscritti;
		this.numPartecipanti = numPartecipanti;
		this.numEnti = numEnti;
	}

	@Override
	public String toString() {
		return "StatEventiRow [evento=" + evento + ", numIscritti=" + numIscritti + ", numPartecipanti="
				+ numPartecipanti + ", numEnti=" + numEnti + "]";
	}

	
}
