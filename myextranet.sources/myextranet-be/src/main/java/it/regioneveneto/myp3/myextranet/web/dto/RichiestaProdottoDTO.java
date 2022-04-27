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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.ProcedimentoProdotto;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivato;
import it.regioneveneto.myp3.myextranet.model.RichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.TipoRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class RichiestaProdottoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected Integer idProdAttivRich;
	@NotNull(message = "ProdottoAttivato è obbligatorio")
	protected ProdottoAttivatoDTO prodottoAttivato;
	@NotNull(message = "RichiestaProdotto è obbligatorio")
	protected ProcedimentoProdottoDTO procedimentoProdotto;
	@NotNull(message = "NumVersione è obbligatorio")
	protected Integer numVersione;
	
	protected LocalDate dtRich;
	@NotBlank(message = "CodStato è obbligatorio")
	protected String codStato;
	
	protected String ultimeNote;
	@NotNull(message = "FlgFineRich è obbligatorio")
	protected Integer flgFineRich;

	public Integer getIdProdAttivRich() {
		return idProdAttivRich;
	}

	public void setIdProdAttivRich(Integer idProdAttivRich) {
		this.idProdAttivRich = idProdAttivRich;
	}

	public ProdottoAttivatoDTO getProdottoAttivato() {
		return prodottoAttivato;
	}

	public void setProdottoAttivato(ProdottoAttivatoDTO prodottoAttivato) {
		this.prodottoAttivato = prodottoAttivato;
	}

	public ProcedimentoProdottoDTO getProcedimentoProdotto() {
		return procedimentoProdotto;
	}

	public void setProcedimentoProdotto(ProcedimentoProdottoDTO procedimentoProdotto) {
		this.procedimentoProdotto = procedimentoProdotto;
	}

	public Integer getNumVersione() {
		return numVersione;
	}

	public void setNumVersione(Integer numVersione) {
		this.numVersione = numVersione;
	}

	public LocalDate getDtRich() {
		return dtRich;
	}

	public void setDtRich(LocalDate dtRich) {
		this.dtRich = dtRich;
	}

	public String getCodStato() {
		return codStato;
	}

	public void setCodStato(String codStato) {
		this.codStato = codStato;
	}

	public String getUltimeNote() {
		return ultimeNote;
	}

	public void setUltimeNote(String ultimeNote) {
		this.ultimeNote = ultimeNote;
	}

	public Integer getFlgFineRich() {
		return flgFineRich;
	}

	public void setFlgFineRich(Integer flgFineRich) {
		this.flgFineRich = flgFineRich;
	}

	@Override
	public String toString() {
		return "RichiestaProdottoDTO [idProdAttivRich=" + idProdAttivRich + ", prodottoAttivato=" + prodottoAttivato
				+ ", procedimentoProdotto=" + procedimentoProdotto + ", numVersione=" + numVersione + ", dtRich="
				+ dtRich + ", codStato=" + codStato + ", ultimeNote=" + ultimeNote + ", flgFineRich=" + flgFineRich
				+ "]";
	}

	public static RichiestaProdotto patchRichiestaProdotto(RichiestaProdottoDTO patchRichiestaProdotto, RichiestaProdotto originalRichiestaProdotto, boolean ignoreNulls, boolean cloneOriginal) {
		RichiestaProdotto richiestaProdottoResult;
		
		if (cloneOriginal) {
			originalRichiestaProdotto = ObjectMapperUtils.map(originalRichiestaProdotto, new RichiestaProdotto());
		}
		
		if(ignoreNulls) {
			richiestaProdottoResult = originalRichiestaProdotto;
					
			// prodottoAttivato
			if (patchRichiestaProdotto.getProdottoAttivato() != null) {
				ProdottoAttivato prodottoAttivato = new ProdottoAttivato();
				prodottoAttivato.setIdAttivazione(patchRichiestaProdotto.getProdottoAttivato().getIdAttivazione());
				originalRichiestaProdotto.setProdottoAttivato(prodottoAttivato);
			}
			
			// procedimentoProdotto
			if (patchRichiestaProdotto.getProcedimentoProdotto() != null) {
				ProcedimentoProdotto procedimentoProdotto = new ProcedimentoProdotto();
				procedimentoProdotto.setIdProdottoProc(patchRichiestaProdotto.getProcedimentoProdotto().getIdProdottoProc());
				originalRichiestaProdotto.setProcedimentoProdotto(procedimentoProdotto);
			}
			
			// numVersione
			if (patchRichiestaProdotto.getNumVersione() != null) {
				originalRichiestaProdotto.setNumVersione(patchRichiestaProdotto.getNumVersione());
			}
			
			// dtRich
			if (patchRichiestaProdotto.getDtRich() != null) {
				originalRichiestaProdotto.setDtRich(patchRichiestaProdotto.getDtRich());
			}
			
			// codStato
			if (StringUtils.hasText(patchRichiestaProdotto.getCodStato())) {
				originalRichiestaProdotto.setCodStato(patchRichiestaProdotto.getCodStato());
			}
			
			// ultimeNote
			if (StringUtils.hasText(patchRichiestaProdotto.getUltimeNote())) {
				originalRichiestaProdotto.setUltimeNote(patchRichiestaProdotto.getUltimeNote());
			}
			
			// flgFineRich
			if (patchRichiestaProdotto.getFlgFineRich() != null) {
				originalRichiestaProdotto.setFlgFineRich(patchRichiestaProdotto.getFlgFineRich());
			}
				
		} else {
			richiestaProdottoResult = ObjectMapperUtils.map(patchRichiestaProdotto, originalRichiestaProdotto);
		}
		
		return richiestaProdottoResult;
	}

	
}
