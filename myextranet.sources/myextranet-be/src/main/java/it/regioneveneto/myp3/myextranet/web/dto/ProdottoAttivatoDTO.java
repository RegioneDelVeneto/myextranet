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
import java.sql.Date;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivato;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class ProdottoAttivatoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected Integer idAttivazione;
	@NotNull(message = "ProdottoAttivabile è obbligatorio")
	protected ProdottoAttivabileDTO prodottoAttivabile;
	@NotNull(message = "Ente è obbligatorio")
	protected EnteDTO ente;
	
	protected Date dtFineVal;
	
	protected Date dtInizioVal;


	public Integer getIdAttivazione() {
		return idAttivazione;
	}

	public void setIdAttivazione(Integer idAttivazione) {
		this.idAttivazione = idAttivazione;
	}

	public ProdottoAttivabileDTO getProdottoAttivabile() {
		return prodottoAttivabile;
	}

	public void setProdottoAttivabile(ProdottoAttivabileDTO prodottoAttivabile) {
		this.prodottoAttivabile = prodottoAttivabile;
	}

	public EnteDTO getEnte() {
		return ente;
	}

	public void setEnte(EnteDTO ente) {
		this.ente = ente;
	}

	public Date getDtFineVal() {
		return dtFineVal;
	}

	public void setDtFineVal(Date dtFineVal) {
		this.dtFineVal = dtFineVal;
	}
	
	public Date getDtInizioVal() {
		return dtInizioVal;
	}

	public void setDtInizioVal(Date dtInizioVal) {
		this.dtInizioVal = dtInizioVal;
	}
	
	public LocalDate getDtInizioValLD() {
		return dtInizioVal != null ? dtInizioVal.toLocalDate() : null;
	}

	public void setDtInizioValLD(LocalDate dtInizioValLD) {
		this.dtInizioVal = dtInizioValLD != null ? Date.valueOf(dtInizioValLD) : null;
	}

	public LocalDate getDtFineValLD() {
		return dtFineVal != null ? dtFineVal.toLocalDate() : null;
	}

	public void setDtFineValLD(LocalDate dtFineValLD) {
		this.dtFineVal = dtFineValLD != null ? Date.valueOf(dtFineValLD): null;
	}

	@Override
	public String toString() {
		return "ProdottoAttivatoDTO [idAttivazione=" + idAttivazione + ", prodottoAttivabile=" + prodottoAttivabile
				+ ", ente=" + ente + ", dtFineVal=" + dtFineVal + ", dtInizioVal=" + dtInizioVal + "]";
	}

	public static ProdottoAttivato patchProdottoAttivato(ProdottoAttivatoDTO patchProdottoAttivato, ProdottoAttivato originalProdottoAttivato, boolean ignoreNulls, boolean cloneOriginal) {
		ProdottoAttivato prodottoAttivatoResult;
		
		if (cloneOriginal) {
			originalProdottoAttivato = ObjectMapperUtils.map(originalProdottoAttivato, new ProdottoAttivato());
		}
		
		if(ignoreNulls) {
			prodottoAttivatoResult = originalProdottoAttivato;
					
			// prodottoAttivabile
			if (patchProdottoAttivato.getProdottoAttivabile() != null) {
				ProdottoAttivabile prodottoAttivabile = new ProdottoAttivabile();
				prodottoAttivabile.setIdProdotto(patchProdottoAttivato.getProdottoAttivabile().getIdProdotto());
				originalProdottoAttivato.setProdottoAttivabile(prodottoAttivabile);
			}
			// ente
			if (patchProdottoAttivato.getEnte() != null) {
				Ente ente = new Ente();
				ente.setIdEnte(patchProdottoAttivato.getEnte().getIdEnte());
				originalProdottoAttivato.setEnte(ente);
			}
			// dtFineVal
			if (patchProdottoAttivato.getDtFineVal() != null) {
				originalProdottoAttivato.setDtFineVal(patchProdottoAttivato.getDtFineVal());
			}
				
		} else {
			prodottoAttivatoResult = ObjectMapperUtils.map(patchProdottoAttivato, originalProdottoAttivato);
		}
		
		return prodottoAttivatoResult;
	}

	public boolean isValid() {
		
		Date today = Date.valueOf(LocalDate.now());
		
		boolean isValid = this.getDtInizioVal() != null && this.getDtFineVal() != null &&
				this.getDtInizioVal().compareTo(today) <= 0 &&
						this.getDtFineVal().compareTo(today) > 0;

		return isValid;
	}
	
}
