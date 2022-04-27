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

public class StatEntiProdottiRow {
	
	private String ente;
	private int numProdottiAttivati;
	private int numOperatoriCoinvolti;

	public String getEnte() {
		return ente;
	}

	public void setEnte(String ente) {
		this.ente = ente;
	}

	public int getNumProdottiAttivati() {
		return numProdottiAttivati;
	}

	public void setNumProdottiAttivati(int numProdottiAttivati) {
		this.numProdottiAttivati = numProdottiAttivati;
	}

	public int getNumOperatoriCoinvolti() {
		return numOperatoriCoinvolti;
	}

	public void setNumOperatoriCoinvolti(int numOperatoriCoinvolti) {
		this.numOperatoriCoinvolti = numOperatoriCoinvolti;
	}

	public StatEntiProdottiRow() {
		
	}

	@Override
	public String toString() {
		return "StatEntiProdottiRow [ente=" + ente + ", numProdottiAttivati=" + numProdottiAttivati
				+ ", numOperatoriCoinvolti=" + numOperatoriCoinvolti + "]";
	}

}
