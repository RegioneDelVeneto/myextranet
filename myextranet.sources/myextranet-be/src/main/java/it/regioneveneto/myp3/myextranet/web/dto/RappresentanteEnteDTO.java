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

import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.sql.Date;

import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.mybox.ContentMetadata;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.RappresentanteEnte;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class RappresentanteEnteDTO implements Serializable {
	private Integer idRappr;
	@NotNull(message = "utente è obbligatorio")
	private UtenteDTO utente;
	@NotNull(message = "ente è obbligatorio")
	private EnteDTO ente;
	private String tipoRappr;
	private String numProtocollo;
	private LocalDate dtProtocollo;
	private LocalDate dtRichiesta;
	private Integer flgConferma;
	private LocalDate dtConferma;
	private String motivConferma;
	
	private boolean flgAttivo;
	
	private Date dtInizioVal;
	private Date dtFineVal;


	public Integer getIdRappr() {
		return idRappr;
	}

	public void setIdRappr(Integer idRappr) {
		this.idRappr = idRappr;
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

	public String getTipoRappr() {
		return tipoRappr;
	}

	public void setTipoRappr(String tipoRappr) {
		this.tipoRappr = tipoRappr;
	}

	public String getNumProtocollo() {
		return numProtocollo;
	}

	public void setNumProtocollo(String numProtocollo) {
		this.numProtocollo = numProtocollo;
	}

	public LocalDate getDtProtocollo() {
		return dtProtocollo;
	}

	public void setDtProtocollo(LocalDate dtProtocollo) {
		this.dtProtocollo = dtProtocollo;
	}

	public LocalDate getDtRichiesta() {
		return dtRichiesta;
	}

	public void setDtRichiesta(LocalDate dtRichiesta) {
		this.dtRichiesta = dtRichiesta;
	}

	public Integer getFlgConferma() {
		return flgConferma;
	}

	public void setFlgConferma(Integer flgConferma) {
		this.flgConferma = flgConferma;
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

	public boolean isFlgAttivo() {
		return flgAttivo;
	}

	public void setFlgAttivo(boolean flgAttivo) {
		this.flgAttivo = flgAttivo;
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
	public java.lang.String toString() {
		return "RappresentanteEnteDTO{" +
				"idRappr=" + idRappr +
				", utente=" + utente +
				", ente=" + ente +
				", tipoRappr='" + tipoRappr + '\'' +
				", numProtocollo='" + numProtocollo + '\'' +
				", dtProtocollo=" + dtProtocollo +
				", dtRichiesta=" + dtRichiesta +
				", flgConferma=" + flgConferma +
				", dtConferma=" + dtConferma +
				", motivConferma='" + motivConferma + '\'' +
				'}';
	}

	public static RappresentanteEnte patchRappresentanteEnte(RappresentanteEnteDTO patchRappresentanteEnte, RappresentanteEnte originalRappresentanteEnte, boolean ignoreNulls, boolean cloneOriginal,
			boolean isFrontoffice) {
		RappresentanteEnte rappresentanteEnteResult;
		
		if (cloneOriginal) {
			originalRappresentanteEnte = ObjectMapperUtils.map(originalRappresentanteEnte, new RappresentanteEnte());
		}
		
		if(ignoreNulls) {
			rappresentanteEnteResult = originalRappresentanteEnte;
			
			// tipoRappr
			if (patchRappresentanteEnte.getTipoRappr() != null && !isFrontoffice) {
				originalRappresentanteEnte.setTipoRappr(patchRappresentanteEnte.getTipoRappr());
			}
			// moduloRich
			if (patchRappresentanteEnte.getNumProtocollo() != null) {
				originalRappresentanteEnte.setNumProtocollo(patchRappresentanteEnte.getNumProtocollo());
			}
			// moduloRevoca
			if (patchRappresentanteEnte.getDtProtocollo() != null) {
				originalRappresentanteEnte.setDtProtocollo(patchRappresentanteEnte.getDtProtocollo());
			}
			// flgConferma
			if (patchRappresentanteEnte.getFlgConferma() != null && !isFrontoffice) {
				originalRappresentanteEnte.setFlgConferma(patchRappresentanteEnte.getFlgConferma());
			}			
			// motivConferma
			if(StringUtils.hasText(patchRappresentanteEnte.getMotivConferma()) && !isFrontoffice) {
				originalRappresentanteEnte.setMotivConferma(patchRappresentanteEnte.getMotivConferma());
			}
				
		} else {
			
			if (isFrontoffice) {
				// preserve some fields
				patchRappresentanteEnte.setDtConferma(originalRappresentanteEnte.getDtConferma());
				patchRappresentanteEnte.setDtRichiesta(originalRappresentanteEnte.getDtRichiesta());
				patchRappresentanteEnte.setFlgConferma(originalRappresentanteEnte.getFlgConferma());
				patchRappresentanteEnte.setMotivConferma(originalRappresentanteEnte.getMotivConferma());
				patchRappresentanteEnte.setTipoRappr(originalRappresentanteEnte.getTipoRappr());
				
				patchRappresentanteEnte.setDtInizioVal(originalRappresentanteEnte.getDtInizioVal());
				patchRappresentanteEnte.setDtFineVal(originalRappresentanteEnte.getDtFineVal());
			}
			
			rappresentanteEnteResult = ObjectMapperUtils.map(patchRappresentanteEnte, originalRappresentanteEnte);
		}
		
		return rappresentanteEnteResult;
	}

	
}
