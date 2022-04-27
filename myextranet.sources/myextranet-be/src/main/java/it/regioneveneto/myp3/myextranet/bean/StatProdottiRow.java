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

public class StatProdottiRow {

	private String prodotto;
	private int numEntiServiti;
	private int numOperatoriCoinvolti;

	public String getProdotto() {
		return prodotto;
	}

	public void setProdotto(String prodotto) {
		this.prodotto = prodotto;
	}

	public int getNumEntiServiti() {
		return numEntiServiti;
	}

	public void setNumEntiServiti(int numEntiServiti) {
		this.numEntiServiti = numEntiServiti;
	}

	public int getNumOperatoriCoinvolti() {
		return numOperatoriCoinvolti;
	}

	public void setNumOperatoriCoinvolti(int numOperatoriCoinvolti) {
		this.numOperatoriCoinvolti = numOperatoriCoinvolti;
	}

	public StatProdottiRow() {
		
	}

	@Override
	public String toString() {
		return "StatProdottiRow [prodotto=" + prodotto + ", numEntiServiti=" + numEntiServiti
				+ ", numOperatoriCoinvolti=" + numOperatoriCoinvolti + "]";
	}
	
	

}
