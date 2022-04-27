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

import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class ProdottoAttivabileDTO implements Serializable {
	
	private Integer idProdottoAtt;
	private String idProdotto;
	private ProdottoAttivabileDTO prodottoProp;
	@NotBlank(message = "nomeProdottoAttiv è obbligatorio")
	protected String nomeProdottoAttiv;
	protected String desAttivazione;
	protected String desAttivazioneBreve;
	private LocalDate dtAttivabileDa;
	private LocalDate dtAttivabileA;
	private String codAppProfMan;

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

	public ProdottoAttivabileDTO getProdottoProp() {
		return prodottoProp;
	}

	public void setProdottoProp(ProdottoAttivabileDTO prodottoProp) {
		this.prodottoProp = prodottoProp;
	}

	public String getNomeProdottoAttiv() {
		return nomeProdottoAttiv;
	}

	public void setNomeProdottoAttiv(String nomeProdottoAttiv) {
		this.nomeProdottoAttiv = nomeProdottoAttiv;
	}

	public String getDesAttivazione() {
		return desAttivazione;
	}

	public void setDesAttivazione(String desAttivazione) {
		this.desAttivazione = desAttivazione;
	}

	public String getDesAttivazioneBreve() {
		return desAttivazioneBreve;
	}

	public void setDesAttivazioneBreve(String desAttivazioneBreve) {
		this.desAttivazioneBreve = desAttivazioneBreve;
	}

	public LocalDate getDtAttivabileDa() {
		return dtAttivabileDa;
	}

	public void setDtAttivabileDa(LocalDate dtAttivabileDa) {
		this.dtAttivabileDa = dtAttivabileDa;
	}

	public LocalDate getDtAttivabileA() {
		return dtAttivabileA;
	}

	public void setDtAttivabileA(LocalDate dtAttivabileA) {
		this.dtAttivabileA = dtAttivabileA;
	}

	public String getCodAppProfMan() {
		return codAppProfMan;
	}

	public void setCodAppProfMan(String codAppProfMan) {
		this.codAppProfMan = codAppProfMan;
	}

	@Override
	public String toString() {
		return "ProdottoAttivabileDTO [idProdottoAtt=" + idProdottoAtt + ", idProdotto=" + idProdotto
				+ ", prodottoProp=" + prodottoProp + ", nomeProdottoAttiv=" + nomeProdottoAttiv + ", desAttivazione="
				+ desAttivazione + ", desAttivazioneBreve=" + desAttivazioneBreve + ", dtAttivabileDa=" + dtAttivabileDa
				+ ", dtAttivabileA=" + dtAttivabileA + ", codAppProfMan=" + codAppProfMan + "]";
	}

	public static ProdottoAttivabile patchProdottoAttivabile(ProdottoAttivabileDTO patchProdottoAttivabile, ProdottoAttivabile originalProdottoAttivabile, boolean ignoreNulls, boolean cloneOriginal) {
		ProdottoAttivabile prodottoAttivabileResult;
		
		if (cloneOriginal) {
			originalProdottoAttivabile = ObjectMapperUtils.map(originalProdottoAttivabile, new ProdottoAttivabile());
		}
		
		if(ignoreNulls) {
			prodottoAttivabileResult = originalProdottoAttivabile;
			
			// idProdotto
			if(StringUtils.hasText(patchProdottoAttivabile.getIdProdotto())) {
				originalProdottoAttivabile.setIdProdotto(patchProdottoAttivabile.getIdProdotto());
			}
			// prodottoProp
			if (patchProdottoAttivabile.getProdottoProp() != null) {
				ProdottoAttivabile prodottoProp = ObjectMapperUtils.map(patchProdottoAttivabile.getProdottoProp(), ProdottoAttivabile.class);
				originalProdottoAttivabile.setProdottoProp(prodottoProp);
			}		
			// nomeProdottoAttiv
			if(StringUtils.hasText(patchProdottoAttivabile.getNomeProdottoAttiv())) {
				originalProdottoAttivabile.setNomeProdottoAttiv(patchProdottoAttivabile.getNomeProdottoAttiv());
			}
			// desAttivazione
			if(StringUtils.hasText(patchProdottoAttivabile.getDesAttivazione())) {
				originalProdottoAttivabile.setDesAttivazione(patchProdottoAttivabile.getDesAttivazione());
			}
			// desAttivazioneBreve
			if(StringUtils.hasText(patchProdottoAttivabile.getDesAttivazioneBreve())) {
				originalProdottoAttivabile.setDesAttivazioneBreve(patchProdottoAttivabile.getDesAttivazioneBreve());
			}
			// dtAttivabileDa
			if (patchProdottoAttivabile.getDtAttivabileDa() != null) {
				originalProdottoAttivabile.setDtAttivabileDa(patchProdottoAttivabile.getDtAttivabileDa());
			}
			// dtAttivabileA
			if (patchProdottoAttivabile.getDtAttivabileA() != null) {
				originalProdottoAttivabile.setDtAttivabileA(patchProdottoAttivabile.getDtAttivabileA());
			}
			// codAppProfMan
			if(StringUtils.hasText(patchProdottoAttivabile.getCodAppProfMan())) {
				originalProdottoAttivabile.setCodAppProfMan(patchProdottoAttivabile.getCodAppProfMan());
			}
				
		} else {
			prodottoAttivabileResult = ObjectMapperUtils.map(patchProdottoAttivabile, originalProdottoAttivabile);
		}
		
		return prodottoAttivabileResult;
	}

	
}
