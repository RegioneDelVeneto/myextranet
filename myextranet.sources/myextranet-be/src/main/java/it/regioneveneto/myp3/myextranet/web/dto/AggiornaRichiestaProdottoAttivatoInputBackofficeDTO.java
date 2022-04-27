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

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class AggiornaRichiestaProdottoAttivatoInputBackofficeDTO {
	
	@NotNull(message = "idProdAttivRich è obbligatorio")
	protected Integer idProdAttivRich;
	protected String codStatoNew;	
	protected String note;
	@NotNull(message = "esito è obbligatorio")
	protected Integer esito;
	protected Integer flgAnnulla;
	protected List<OperazioneDocumentoRichiestaProdottoDTO> documenti;
	protected List<OperazioneUtenteRichiestaProdottoDTO> utenti;

	public Integer getIdProdAttivRich() {
		return idProdAttivRich;
	}

	public void setIdProdAttivRich(Integer idProdAttivRich) {
		this.idProdAttivRich = idProdAttivRich;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getEsito() {
		return esito;
	}

	public void setEsito(Integer esito) {
		this.esito = esito;
	}

	public Integer getFlgAnnulla() {
		return flgAnnulla;
	}

	public void setFlgAnnulla(Integer flgAnnulla) {
		this.flgAnnulla = flgAnnulla;
	}

	public List<OperazioneDocumentoRichiestaProdottoDTO> getDocumenti() {
		return documenti;
	}

	public void setDocumenti(List<OperazioneDocumentoRichiestaProdottoDTO> documenti) {
		this.documenti = documenti;
	}

	public List<OperazioneUtenteRichiestaProdottoDTO> getUtenti() {
		return utenti;
	}

	public void setUtenti(List<OperazioneUtenteRichiestaProdottoDTO> utenti) {
		this.utenti = utenti;
	}

	public AggiornaRichiestaProdottoAttivatoInputBackofficeDTO() {
		
	}

	public String getCodStatoNew() {
		return codStatoNew;
	}

	public void setCodStatoNew(String codStatoNew) {
		this.codStatoNew = codStatoNew;
	}

	public GenericAvanzamentoStepRichiestaInputDTO toGenericAvanzamentoStepRichiestaInputDTO() {
		GenericAvanzamentoStepRichiestaInputDTO res = ObjectMapperUtils.map(this, GenericAvanzamentoStepRichiestaInputDTO.class);
		return res;
	}
}
