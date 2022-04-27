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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.DocumentoRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.RichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.StepRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class DocumentoRichiestaProdottoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	protected Integer idProdRichDoc;
	@NotNull(message = "RichiestaProdotto è obbligatorio")
	protected RichiestaProdottoDTO richiestaProdotto;
	@NotNull(message = "StepRichiestaProdotto è obbligatorio")
	protected StepRichiestaProdottoDTO stepRichiestaProdotto;
	@NotBlank(message = "IdDocumento è obbligatorio")
	protected String idDocumento;
	@NotBlank(message = "NomeDocumento è obbligatorio")
	protected String nomeDocumento;
	@NotNull(message = "FlgEnabled è obbligatorio")
	protected Integer flgEnabled;
	
	public Integer getIdProdRichDoc() {
		return idProdRichDoc;
	}

	public void setIdProdRichDoc(Integer idProdRichDoc) {
		this.idProdRichDoc = idProdRichDoc;
	}

	public RichiestaProdottoDTO getRichiestaProdotto() {
		return richiestaProdotto;
	}

	public void setRichiestaProdotto(RichiestaProdottoDTO richiestaProdotto) {
		this.richiestaProdotto = richiestaProdotto;
	}

	public StepRichiestaProdottoDTO getStepRichiestaProdotto() {
		return stepRichiestaProdotto;
	}

	public void setStepRichiestaProdotto(StepRichiestaProdottoDTO stepRichiestaProdotto) {
		this.stepRichiestaProdotto = stepRichiestaProdotto;
	}

	public String getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(String idDocumento) {
		this.idDocumento = idDocumento;
	}

	public String getNomeDocumento() {
		return nomeDocumento;
	}

	public void setNomeDocumento(String nomeDocumento) {
		this.nomeDocumento = nomeDocumento;
	}

	public Integer getFlgEnabled() {
		return flgEnabled;
	}

	public void setFlgEnabled(Integer flgEnabled) {
		this.flgEnabled = flgEnabled;
	}

	@Override
	public String toString() {
		return "DocumentoRichiestaProdottoDTO [idProdRichDoc=" + idProdRichDoc + ", richiestaProdotto="
				+ richiestaProdotto + ", stepRichiestaProdotto=" + stepRichiestaProdotto + ", idDocumento="
				+ idDocumento + ", nomeDocumento=" + nomeDocumento + ", flgEnabled=" + flgEnabled + "]";
	}

	public static DocumentoRichiestaProdotto patchDocumentoRichiestaProdotto(DocumentoRichiestaProdottoDTO patchDocumentoRichiestaProdotto, DocumentoRichiestaProdotto originalDocumentoRichiestaProdotto, boolean ignoreNulls, boolean cloneOriginal) {
		DocumentoRichiestaProdotto documentoRichiestaProdottoResult;
		
		if (cloneOriginal) {
			originalDocumentoRichiestaProdotto = ObjectMapperUtils.map(originalDocumentoRichiestaProdotto, new DocumentoRichiestaProdotto());
		}
		
		if(ignoreNulls) {
			documentoRichiestaProdottoResult = originalDocumentoRichiestaProdotto;
					
			// richiestaProdotto
			if (patchDocumentoRichiestaProdotto.getRichiestaProdotto() != null) {
				RichiestaProdotto richiestaProdotto = new RichiestaProdotto();
				richiestaProdotto.setIdProdAttivRich(patchDocumentoRichiestaProdotto.getRichiestaProdotto().getIdProdAttivRich());
				originalDocumentoRichiestaProdotto.setRichiestaProdotto(richiestaProdotto);
			}
			
			// stepRichiestaProdotto
			if (patchDocumentoRichiestaProdotto.getStepRichiestaProdotto() != null) {
				StepRichiestaProdotto stepRichiestaProdotto = new StepRichiestaProdotto();
				stepRichiestaProdotto.setIdStep(patchDocumentoRichiestaProdotto.getStepRichiestaProdotto().getIdStep());
				originalDocumentoRichiestaProdotto.setStepRichiestaProdotto(stepRichiestaProdotto);
			}
			
			// idDocumento
			if(StringUtils.hasText(patchDocumentoRichiestaProdotto.getIdDocumento())) {
				originalDocumentoRichiestaProdotto.setIdDocumento(patchDocumentoRichiestaProdotto.getIdDocumento());
			}
			
			// nomeDocumento
			if(StringUtils.hasText(patchDocumentoRichiestaProdotto.getNomeDocumento())) {
				originalDocumentoRichiestaProdotto.setNomeDocumento(patchDocumentoRichiestaProdotto.getNomeDocumento());
			}
			
			// flgEnabled
			if (patchDocumentoRichiestaProdotto.getFlgEnabled() != null) {
				originalDocumentoRichiestaProdotto.setFlgEnabled(patchDocumentoRichiestaProdotto.getFlgEnabled());
			}
							
		} else {
			documentoRichiestaProdottoResult = ObjectMapperUtils.map(patchDocumentoRichiestaProdotto, originalDocumentoRichiestaProdotto);
		}
		
		return documentoRichiestaProdottoResult;
	}

	
}
