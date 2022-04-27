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

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.CollaboratoreProgetto;
import it.regioneveneto.myp3.myextranet.utils.BeanUtils;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class CollaboratoreProgettoDTO implements Serializable {
	private Integer idCollab;
	private UtenteDTO utente;
	@NotBlank(message = "Id Progetto è obbligatorio")
	private String idProgetto;
	private Integer flgCoord;
	private Integer flgConferma;
	private LocalDate dtRichiesta;
	private LocalDate dtConferma;
	private String motivConferma;
	
	private Date dtInizioVal;
	private Date dtFineVal;
	
	private boolean valid;

	public Integer getIdCollab() {
		return idCollab;
	}

	public void setIdCollab(Integer idCollab) {
		this.idCollab = idCollab;
	}

	public UtenteDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteDTO utente) {
		this.utente = utente;
	}

	public String getIdProgetto() {
		return idProgetto;
	}

	public void setIdProgetto(String idProgetto) {
		this.idProgetto = idProgetto;
	}

	public Integer getFlgCoord() {
		return flgCoord;
	}

	public void setFlgCoord(Integer flgCoord) {
		this.flgCoord = flgCoord;
	}

	public Integer getFlgConferma() {
		return flgConferma;
	}

	public void setFlgConferma(Integer flgConferma) {
		this.flgConferma = flgConferma;
	}

	public LocalDate getDtRichiesta() {
		return dtRichiesta;
	}

	public void setDtRichiesta(LocalDate dtRichiesta) {
		this.dtRichiesta = dtRichiesta;
	}

	public LocalDate getDtConferma() {
		return dtConferma;
	}

	public void setDtConferma(LocalDate dtConferma) {
		this.dtConferma = dtConferma;
	}

	public String getMotivConferma() {
		return motivConferma;
	}

	public void setMotivConferma(String motivConferma) {
		this.motivConferma = motivConferma;
	}

	public String getFlgCoordStr() {
		return BeanUtils.flagToString(flgCoord);
	}

	public void setFlgCoordStr(String flgCoordStr) {
		this.flgCoord = BeanUtils.stringToFlag(flgCoordStr);
	}

	public String getFlgConfermaStr() {
		return BeanUtils.flagToString(flgConferma);
	}

	public void setFlgConfermaStr(String flgConfermaStr) {
		this.flgConferma = BeanUtils.stringToFlag(flgConfermaStr);
	}

	public Date getDtInizioVal() {
		return dtInizioVal;
	}

	public void setDtInizioVal(Date dtInizioVal) {
		this.dtInizioVal = dtInizioVal;
	}

	public Date getDtFineVal() {
		return dtFineVal;
	}

	public void setDtFineVal(Date dtFineVal) {
		this.dtFineVal = dtFineVal;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
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
		return "CollaboratoreProgettoDTO [idCollab=" + idCollab + ", utente=" + utente + ", idProgetto=" + idProgetto
				+ ", flgCoord=" + flgCoord + ", flgConferma=" + flgConferma + ", dtRichiesta=" + dtRichiesta
				+ ", dtConferma=" + dtConferma + ", motivConferma=" + motivConferma + "]";
	}

	public static CollaboratoreProgetto patchCollaboratoreProgetto(CollaboratoreProgettoDTO patchCollaboratoreProgetto, CollaboratoreProgetto originalCollaboratoreProgetto, boolean ignoreNulls, boolean cloneOriginal) {
		CollaboratoreProgetto collaboratoreProgettoResult;
		
		if (cloneOriginal) {
			originalCollaboratoreProgetto = ObjectMapperUtils.map(originalCollaboratoreProgetto, new CollaboratoreProgetto());
		}
		
		if(ignoreNulls) {
			collaboratoreProgettoResult = originalCollaboratoreProgetto;
			
			// flgCoord
			if (patchCollaboratoreProgetto.getFlgCoord() != null) {
				originalCollaboratoreProgetto.setFlgCoord(patchCollaboratoreProgetto.getFlgCoord());
			}
			// flgConferma
			if (patchCollaboratoreProgetto.getFlgConferma() != null) {
				originalCollaboratoreProgetto.setFlgConferma(patchCollaboratoreProgetto.getFlgConferma());
			}			
			// motivConferma
			if(StringUtils.hasText(patchCollaboratoreProgetto.getMotivConferma())) {
				originalCollaboratoreProgetto.setMotivConferma(patchCollaboratoreProgetto.getMotivConferma());
			}
				
		} else {
			collaboratoreProgettoResult = ObjectMapperUtils.map(patchCollaboratoreProgetto, originalCollaboratoreProgetto);
		}
		
		return collaboratoreProgettoResult;
	}

	
}
