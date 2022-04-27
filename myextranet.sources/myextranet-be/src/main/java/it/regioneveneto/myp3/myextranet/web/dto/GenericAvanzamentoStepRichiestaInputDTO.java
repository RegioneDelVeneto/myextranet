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

public class GenericAvanzamentoStepRichiestaInputDTO {

	protected Integer idEnte;
	protected Integer idProdottoAtt;
	protected Integer idAttivazione;
	protected Integer idProdAttivRich;
	protected String codTipoRich;
	protected String esecutore;
	protected String codStatoOld;
	protected String codStatoNew;
	protected String note;
	protected Integer esito;
	protected Integer flgAnnulla;
	protected List<OperazioneDocumentoRichiestaProdottoDTO> documenti;
	protected List<OperazioneUtenteRichiestaProdottoDTO> utenti;
	
	public Integer getIdEnte() {
		return idEnte;
	}

	public void setIdEnte(Integer idEnte) {
		this.idEnte = idEnte;
	}

	public Integer getIdProdottoAtt() {
		return idProdottoAtt;
	}

	public void setIdProdottoAtt(Integer idProdottoAtt) {
		this.idProdottoAtt = idProdottoAtt;
	}

	public Integer getIdAttivazione() {
		return idAttivazione;
	}

	public void setIdAttivazione(Integer idAttivazione) {
		this.idAttivazione = idAttivazione;
	}

	public Integer getIdProdAttivRich() {
		return idProdAttivRich;
	}

	public void setIdProdAttivRich(Integer idProdAttivRich) {
		this.idProdAttivRich = idProdAttivRich;
	}

	public String getCodTipoRich() {
		return codTipoRich;
	}

	public void setCodTipoRich(String codTipoRich) {
		this.codTipoRich = codTipoRich;
	}

	public String getEsecutore() {
		return esecutore;
	}

	public void setEsecutore(String esecutore) {
		this.esecutore = esecutore;
	}

	public String getCodStatoOld() {
		return codStatoOld;
	}

	public void setCodStatoOld(String codStatoOld) {
		this.codStatoOld = codStatoOld;
	}

	public String getCodStatoNew() {
		return codStatoNew;
	}

	public void setCodStatoNew(String codStatoNew) {
		this.codStatoNew = codStatoNew;
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

	public Integer getFlgAnnulla() {
		return flgAnnulla;
	}

	public void setFlgAnnulla(Integer flgAnnulla) {
		this.flgAnnulla = flgAnnulla;
	}

	public GenericAvanzamentoStepRichiestaInputDTO() {
		
	}

}
