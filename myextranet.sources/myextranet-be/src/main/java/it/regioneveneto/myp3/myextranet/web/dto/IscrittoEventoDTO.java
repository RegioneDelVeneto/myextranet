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

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.IscrittoEvento;
import it.regioneveneto.myp3.myextranet.utils.BeanUtils;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class IscrittoEventoDTO implements Serializable {
	private Integer idIscritto;
	private UtenteDTO utente;
	@NotBlank(message = "Id Evento è obbligatorio")
	private String idEvento;
	private Integer flgRelatore;
	private Integer flgPartecipLoco;
	private Integer flgPartecipRemoto;
	private String flgPartecipPref;
	private LocalDate dtIscrizione;
	private LocalDate dtInvioAttestato;
	private LocalDate dtRichQuestionario;
	private String nome;
	private String cognome;
	private String email;
	private String azienda;
	private EnteDTO ente;


	public Integer getIdIscritto() {
		return idIscritto;
	}

	public void setIdIscritto(Integer idIscritto) {
		this.idIscritto = idIscritto;
	}

	public String getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(String idEvento) {
		this.idEvento = idEvento;
	}

	public Integer getFlgRelatore() {
		return flgRelatore;
	}

	public void setFlgRelatore(Integer flgRelatore) {
		this.flgRelatore = flgRelatore;
	}

	public Integer getFlgPartecipLoco() {
		return flgPartecipLoco;
	}

	public void setFlgPartecipLoco(Integer flgPartecipLoco) {
		this.flgPartecipLoco = flgPartecipLoco;
	}

	public Integer getFlgPartecipRemoto() {
		return flgPartecipRemoto;
	}

	public void setFlgPartecipRemoto(Integer flgPartecipRemoto) {
		this.flgPartecipRemoto = flgPartecipRemoto;
	}

	public String getFlgPartecipPref() {
		return flgPartecipPref;
	}

	public void setFlgPartecipPref(String flgPartecipPref) {
		this.flgPartecipPref = flgPartecipPref;
	}

	public LocalDate getDtIscrizione() {
		return dtIscrizione;
	}

	public void setDtIscrizione(LocalDate dtIscrizione) {
		this.dtIscrizione = dtIscrizione;
	}

	public LocalDate getDtInvioAttestato() {
		return dtInvioAttestato;
	}

	public void setDtInvioAttestato(LocalDate dtInvioAttestato) {
		this.dtInvioAttestato = dtInvioAttestato;
	}

	public LocalDate getDtRichQuestionario() {
		return dtRichQuestionario;
	}

	public void setDtRichQuestionario(LocalDate dtRichQuestionario) {
		this.dtRichQuestionario = dtRichQuestionario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAzienda() {
		return azienda;
	}

	public void setAzienda(String azienda) {
		this.azienda = azienda;
	}
	
	public UtenteDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteDTO utente) {
		this.utente = utente;
	}

	public EnteDTO getEnte() {
		return ente;
	}

	public void setEnte(EnteDTO ente) {
		this.ente = ente;
	}

	public String getFlgRelatoreStr() {
		return BeanUtils.flagToString(flgRelatore);
	}

	public void setFlgRelatoreStr(String flgRelatoreStr) {
		this.flgRelatore = BeanUtils.stringToFlag(flgRelatoreStr);
	}

	public String getFlgPartecipLocoStr() {
		return BeanUtils.flagToString(flgPartecipLoco);
	}

	public void setFlgPartecipLocoStr(String flgPartecipLocoStr) {
		this.flgPartecipLoco = BeanUtils.stringToFlag(flgPartecipLocoStr);
	}

	public String getFlgPartecipRemotoStr() {
		return BeanUtils.flagToString(flgPartecipRemoto);
	}

	public void setFlgPartecipRemotoStr(String flgPartecipRemotoStr) {
		this.flgPartecipRemoto = BeanUtils.stringToFlag(flgPartecipRemotoStr);
	}

	@Override
	public String toString() {
		return "IscrittoEventoDTO [idIscritto=" + idIscritto + ", utente=" + utente + ", idEvento=" + idEvento
				+ ", flgRelatore=" + flgRelatore + ", flgPartecipLoco=" + flgPartecipLoco + ", flgPartecipRemoto="
				+ flgPartecipRemoto + ", flgPartecipPref=" + flgPartecipPref + ", dtIscrizione=" + dtIscrizione
				+ ", dtInvioAttestato=" + dtInvioAttestato + ", dtRichQuestionario=" + dtRichQuestionario + ", nome="
				+ nome + ", cognome=" + cognome + ", email=" + email + ", azienda=" + azienda + ", ente=" + ente + "]";
	}

	public static IscrittoEvento patchIscrittoEvento(IscrittoEventoDTO patchIscrittoEvento, IscrittoEvento originalIscrittoEvento, boolean ignoreNulls, boolean cloneOriginal) {
		IscrittoEvento iscrittoResult;
		
		if (cloneOriginal) {
			originalIscrittoEvento = ObjectMapperUtils.map(originalIscrittoEvento, new IscrittoEvento());
		}
		
		if(ignoreNulls) {
			iscrittoResult = originalIscrittoEvento;
			
			// flgRelatore
			if (patchIscrittoEvento.getFlgRelatore() != null) {
				originalIscrittoEvento.setFlgRelatore(patchIscrittoEvento.getFlgRelatore());
			}
			// flgPartecipLoco
			if (patchIscrittoEvento.getFlgPartecipLoco() != null) {
				originalIscrittoEvento.setFlgPartecipLoco(patchIscrittoEvento.getFlgPartecipLoco());
			}
			// flgPartecipRemoto
			if (patchIscrittoEvento.getFlgPartecipRemoto() != null) {
				originalIscrittoEvento.setFlgPartecipRemoto(patchIscrittoEvento.getFlgPartecipRemoto());
			}
			// flgPartecipPref
			if (patchIscrittoEvento.getFlgPartecipPref() != null) {
				originalIscrittoEvento.setFlgPartecipPref(patchIscrittoEvento.getFlgPartecipPref());
			}
			// nome
			if(StringUtils.hasText(patchIscrittoEvento.getNome())) {
				originalIscrittoEvento.setNome(patchIscrittoEvento.getNome());
			}
			// cognome
			if(StringUtils.hasText(patchIscrittoEvento.getCognome())) {
				originalIscrittoEvento.setCognome(patchIscrittoEvento.getCognome());
			}
			// email
			if(StringUtils.hasText(patchIscrittoEvento.getEmail())) {
				originalIscrittoEvento.setEmail(patchIscrittoEvento.getEmail());
			}
			// azienda
			if(StringUtils.hasText(patchIscrittoEvento.getAzienda())) {
				originalIscrittoEvento.setAzienda(patchIscrittoEvento.getAzienda());
			}
			// ente
			if (patchIscrittoEvento.getEnte() != null) {
				Ente e = ObjectMapperUtils.map(patchIscrittoEvento.getEnte(), Ente.class);
				originalIscrittoEvento.setEnte(e);
			}
			
		} else {
			iscrittoResult = ObjectMapperUtils.map(patchIscrittoEvento, originalIscrittoEvento);
		}
		
		return iscrittoResult;
	}

	
}
