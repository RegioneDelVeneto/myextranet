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

import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.ProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.model.TipoRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class ProcedimentoProdottoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected Integer idProdottoProc;
	@NotNull(message = "Prodotto è obbligatorio")
	protected ProdottoAttivabileDTO prodotto;
	@NotNull(message = "NumVersione è obbligatorio")
	protected Integer numVersione;
	@NotNull(message = "TipoRichiestaProdotto è obbligatorio")
	protected TipoRichiestaProdottoDTO tipoRichiestaProdotto;
	@NotNull(message = "DesProdottoProc è obbligatorio")
	protected String desProdottoProc;
	protected String desProdottoProcEstesa;
	@NotNull(message = "FlgEnabled è obbligatorio")
	protected Integer flgEnabled;

	public Integer getIdProdottoProc() {
		return idProdottoProc;
	}

	public void setIdProdottoProc(Integer idProdottoProc) {
		this.idProdottoProc = idProdottoProc;
	}

	public ProdottoAttivabileDTO getProdotto() {
		return prodotto;
	}

	public void setProdotto(ProdottoAttivabileDTO prodotto) {
		this.prodotto = prodotto;
	}

	public Integer getNumVersione() {
		return numVersione;
	}

	public void setNumVersione(Integer numVersione) {
		this.numVersione = numVersione;
	}

	public TipoRichiestaProdottoDTO getTipoRichiestaProdotto() {
		return tipoRichiestaProdotto;
	}

	public void setTipoRichiestaProdotto(TipoRichiestaProdottoDTO tipoRichiestaProdotto) {
		this.tipoRichiestaProdotto = tipoRichiestaProdotto;
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
		return "ProcedimentoProdottoDTO [idProdottoProc=" + idProdottoProc + ", prodotto=" + prodotto + ", numVersione="
				+ numVersione + ", tipoRichiestaProdotto=" + tipoRichiestaProdotto + ", desProdottoProc="
				+ desProdottoProc + ", desProdottoProcEstesa=" + desProdottoProcEstesa + ", flgEnabled=" + flgEnabled
				+ "]";
	}

	public static ProcedimentoProdotto patchProcedimentoProdotto(ProcedimentoProdottoDTO patchProcedimentoProdotto, ProcedimentoProdotto originalProcedimentoProdotto, boolean ignoreNulls, boolean cloneOriginal) {
		ProcedimentoProdotto procedimentoProdottoResult;
		
		if (cloneOriginal) {
			originalProcedimentoProdotto = ObjectMapperUtils.map(originalProcedimentoProdotto, new ProcedimentoProdotto());
		}
		
		if(ignoreNulls) {
			procedimentoProdottoResult = originalProcedimentoProdotto;
					
			// prodotto
			if (patchProcedimentoProdotto.getProdotto() != null) {
				ProdottoAttivabile prodottoAttivabile = new ProdottoAttivabile();
				prodottoAttivabile.setIdProdottoAtt(patchProcedimentoProdotto.getProdotto().getIdProdottoAtt());
				originalProcedimentoProdotto.setProdotto(prodottoAttivabile);
			}
			
			// numVersione
			if (patchProcedimentoProdotto.getNumVersione() != null) {
				originalProcedimentoProdotto.setNumVersione(patchProcedimentoProdotto.getNumVersione());
			}
			
			// tipoRichiestaProdotto
			if (patchProcedimentoProdotto.getTipoRichiestaProdotto() != null) {
				TipoRichiestaProdotto tipoRichiestaProdotto = new TipoRichiestaProdotto();
				tipoRichiestaProdotto.setCodTipoRich(patchProcedimentoProdotto.getTipoRichiestaProdotto().getCodTipoRich());
				originalProcedimentoProdotto.setTipoRichiestaProdotto(tipoRichiestaProdotto);
			}
			
			// desProdottoProc
			if(StringUtils.hasText(patchProcedimentoProdotto.getDesProdottoProc())) {
				originalProcedimentoProdotto.setDesProdottoProc(patchProcedimentoProdotto.getDesProdottoProc());
			}
			
			// desProdottoProcEstesa
			if(StringUtils.hasText(patchProcedimentoProdotto.getDesProdottoProcEstesa())) {
				originalProcedimentoProdotto.setDesProdottoProcEstesa(patchProcedimentoProdotto.getDesProdottoProcEstesa());
			}
			
			// flgEnabled
			if (patchProcedimentoProdotto.getFlgEnabled() != null) {
				originalProcedimentoProdotto.setFlgEnabled(patchProcedimentoProdotto.getFlgEnabled());
			}
				
		} else {
			procedimentoProdottoResult = ObjectMapperUtils.map(patchProcedimentoProdotto, originalProcedimentoProdotto);
		}
		
		return procedimentoProdottoResult;
	}

	
}
