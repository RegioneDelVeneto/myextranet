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

public class ProcedimentoProdottoFilterDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected Integer idProdottoAtt;
	protected String idProdotto;
	protected Integer numVersione;
	protected String codTipoRich;
	protected String desProdottoProc;
	protected String desProdottoProcEstesa;
	protected Integer flgEnabled;

	public Integer getIdProdottoAtt() {
		return idProdottoAtt;
	}

	public void setIdProdottoAtt(Integer idProdottoAtt) {
		this.idProdottoAtt = idProdottoAtt;
	}

	public String getIdProdotto() {
		return idProdotto;
	}

	public void setIdProdotto(String idProdotto) {
		this.idProdotto = idProdotto;
	}

	public Integer getNumVersione() {
		return numVersione;
	}

	public void setNumVersione(Integer numVersione) {
		this.numVersione = numVersione;
	}

	public String getCodTipoRich() {
		return codTipoRich;
	}

	public void setCodTipoRich(String codTipoRich) {
		this.codTipoRich = codTipoRich;
	}

	public String getDesProdottoProc() {
		return desProdottoProc;
	}

	public void setDesProdottoProc(String desProdottoProc) {
		this.desProdottoProc = desProdottoProc;
	}

	public String getDesProdottoProcEstesa() {
		return desProdottoProcEstesa;
	}

	public void setDesProdottoProcEstesa(String desProdottoProcEstesa) {
		this.desProdottoProcEstesa = desProdottoProcEstesa;
	}

	public Integer getFlgEnabled() {
		return flgEnabled;
	}

	public void setFlgEnabled(Integer flgEnabled) {
		this.flgEnabled = flgEnabled;
	}

	@Override
	public String toString() {
		return "ProcedimentoProdottoFilterDTO [idProdottoAtt=" + idProdottoAtt + ", idProdotto=" + idProdotto
				+ ", numVersione=" + numVersione + ", codTipoRich=" + codTipoRich + ", desProdottoProc="
				+ desProdottoProc + ", desProdottoProcEstesa=" + desProdottoProcEstesa + ", flgEnabled=" + flgEnabled
				+ "]";
	}

}
