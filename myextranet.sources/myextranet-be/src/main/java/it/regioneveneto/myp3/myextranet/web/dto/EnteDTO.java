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
import java.sql.Date;
import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.mybox.ContentMetadata;
import it.regioneveneto.myp3.myextranet.model.CategoriaEnte;
import it.regioneveneto.myp3.myextranet.model.Comune;
import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class EnteDTO implements Serializable {
	private Integer idEnte;
	private String codIpa;
	private String denominazione;
	private String logo;
	private ComuneDTO comune;
	private String indirizzo;
	private String cap;
	private String email;
	private String pec;
	private String codFiscale;
	private String telefono;
	private String fax;
	private String urlWebSite;
	private String urlFacebook;
	private String urlInstagram;
	private String urlTwitter;
	private EnteDTO enteUnione;
	
	private InputStream logoFileInputStream;
	private ContentMetadata logoFileMetadata;
	
	private CategoriaEnteDTO categoria;
	
	private Date dtInizioVal;
	private Date dtFineVal;

    //Getter and setter

	public Integer getIdEnte() {
		return idEnte;
	}

	public void setIdEnte(Integer idEnte) {
		this.idEnte = idEnte;
	}

	public String getCodIpa() {
		return codIpa;
	}

	public void setCodIpa(String codIpa) {
		this.codIpa = codIpa;
	}

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public ComuneDTO getComune() {
		return comune;
	}

	public void setComune(ComuneDTO comune) {
		this.comune = comune;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}

	public String getCodFiscale() {
		return codFiscale;
	}

	public void setCodFiscale(String codFiscale) {
		this.codFiscale = codFiscale;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getUrlWebSite() {
		return urlWebSite;
	}

	public void setUrlWebSite(String urlWebSite) {
		this.urlWebSite = urlWebSite;
	}

	public String getUrlFacebook() {
		return urlFacebook;
	}

	public void setUrlFacebook(String urlFacebook) {
		this.urlFacebook = urlFacebook;
	}

	public String getUrlInstagram() {
		return urlInstagram;
	}

	public void setUrlInstagram(String urlInstagram) {
		this.urlInstagram = urlInstagram;
	}

	public String getUrlTwitter() {
		return urlTwitter;
	}

	public void setUrlTwitter(String urlTwitter) {
		this.urlTwitter = urlTwitter;
	}

	public EnteDTO getEnteUnione() {
		return enteUnione;
	}

	public void setEnteUnione(EnteDTO enteUnione) {
		this.enteUnione = enteUnione;
	}

	public InputStream getLogoFileInputStream() {
		return logoFileInputStream;
	}

	public void setLogoFileInputStream(InputStream logoFileInputStream) {
		this.logoFileInputStream = logoFileInputStream;
	}

	public ContentMetadata getLogoFileMetadata() {
		return logoFileMetadata;
	}

	public void setLogoFileMetadata(ContentMetadata logoFileMetadata) {
		this.logoFileMetadata = logoFileMetadata;
	}

	public CategoriaEnteDTO getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaEnteDTO categoria) {
		this.categoria = categoria;
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
	public String toString() {
		return "EnteDTO [idEnte=" + idEnte + ", denominazione=" + denominazione + "]";
	}
	
	public static Ente patchEnte(EnteDTO patchEnte, Ente originalEnte, boolean ignoreNulls, boolean cloneOriginal) {
		
		Ente enteResult;
		
		if (cloneOriginal) {
			originalEnte = ObjectMapperUtils.map(originalEnte, new Ente());
		}
		
			enteResult = originalEnte;
			
			// codIpa
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getCodIpa())) {
				originalEnte.setCodIpa(patchEnte.getCodIpa());
			}
			// denominazione
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getDenominazione())) {
				originalEnte.setDenominazione(patchEnte.getDenominazione());
			}
			// logo
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getLogo())) {
				originalEnte.setLogo(patchEnte.getLogo());
			}
			// indirizzo
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getIndirizzo())) {
				originalEnte.setIndirizzo(patchEnte.getIndirizzo());
			}
			// cap
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getCap())) {
				originalEnte.setCap(patchEnte.getCap());
			}
			// email
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getEmail())) {
				originalEnte.setEmail(patchEnte.getEmail());
			}
			// pec
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getPec())) {
				originalEnte.setPec(patchEnte.getPec());
			}
			// codFiscale
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getCodFiscale())) {
				originalEnte.setCodFiscale(patchEnte.getCodFiscale());
			}
			// telefono
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getTelefono())) {
				originalEnte.setTelefono(patchEnte.getTelefono());
			}
			// fax
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getFax())) {
				originalEnte.setFax(patchEnte.getFax());
			}
			// urlWebSite
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getUrlWebSite())) {
				originalEnte.setUrlWebSite(patchEnte.getUrlWebSite());
			}
			// urlFacebook
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getUrlFacebook())) {
				originalEnte.setUrlFacebook(patchEnte.getUrlFacebook());
			}
			// urlInstagram
			if(!ignoreNulls || StringUtils.hasText(patchEnte.getUrlInstagram())) {
				originalEnte.setUrlInstagram(patchEnte.getUrlInstagram());
			}
			// urlTwitter
			if(StringUtils.hasText(patchEnte.getUrlTwitter())) {
				originalEnte.setUrlTwitter(patchEnte.getUrlTwitter());
			}
			
			
			// comune
			if (!ignoreNulls || patchEnte.getComune() != null) {
				Comune e = ObjectMapperUtils.mapOrNull(patchEnte.getComune(), Comune.class);
				originalEnte.setComune(e);
			}
			// enteUnione
			if (!ignoreNulls || patchEnte.getEnteUnione() != null) {
				Ente e = ObjectMapperUtils.mapOrNull(patchEnte.getEnteUnione(), Ente.class);
				originalEnte.setEnteUnione(e);
			}
			// categoria
			if (!ignoreNulls || patchEnte.getCategoria() != null) {
				CategoriaEnte c = ObjectMapperUtils.mapOrNull(patchEnte.getCategoria(), CategoriaEnte.class);
				originalEnte.setCategoria(c);
			}
			
			// dtFineVal
			if (!ignoreNulls || patchEnte.getDtFineVal() != null) {
				originalEnte.setDtFineVal(patchEnte.getDtFineVal());
			}
			
		return enteResult;
	}

	public static Ente patchEnteOrig(EnteDTO patchEnte, Ente originalEnte, boolean ignoreNulls, boolean cloneOriginal) {
		
		Ente enteResult;
		
		if (cloneOriginal) {
			originalEnte = ObjectMapperUtils.map(originalEnte, new Ente());
		}
		
		if(ignoreNulls) {
			enteResult = originalEnte;
			
			// codIpa
			if(StringUtils.hasText(patchEnte.getCodIpa())) {
				originalEnte.setCodIpa(patchEnte.getCodIpa());
			}
			// denominazione
			if(StringUtils.hasText(patchEnte.getDenominazione())) {
				originalEnte.setDenominazione(patchEnte.getDenominazione());
			}
			// logo
			if(StringUtils.hasText(patchEnte.getLogo())) {
				originalEnte.setLogo(patchEnte.getLogo());
			}
			// indirizzo
			if(StringUtils.hasText(patchEnte.getIndirizzo())) {
				originalEnte.setIndirizzo(patchEnte.getIndirizzo());
			}
			// cap
			if(StringUtils.hasText(patchEnte.getCap())) {
				originalEnte.setCap(patchEnte.getCap());
			}
			// email
			if(StringUtils.hasText(patchEnte.getEmail())) {
				originalEnte.setEmail(patchEnte.getEmail());
			}
			// pec
			if(StringUtils.hasText(patchEnte.getPec())) {
				originalEnte.setPec(patchEnte.getPec());
			}
			// codFiscale
			if(StringUtils.hasText(patchEnte.getCodFiscale())) {
				originalEnte.setCodFiscale(patchEnte.getCodFiscale());
			}
			// telefono
			if(StringUtils.hasText(patchEnte.getTelefono())) {
				originalEnte.setTelefono(patchEnte.getTelefono());
			}
			// fax
			if(StringUtils.hasText(patchEnte.getFax())) {
				originalEnte.setFax(patchEnte.getFax());
			}
			// urlWebSite
			if(StringUtils.hasText(patchEnte.getUrlWebSite())) {
				originalEnte.setUrlWebSite(patchEnte.getUrlWebSite());
			}
			// urlFacebook
			if(StringUtils.hasText(patchEnte.getUrlFacebook())) {
				originalEnte.setUrlFacebook(patchEnte.getUrlFacebook());
			}
			// urlInstagram
			if(StringUtils.hasText(patchEnte.getUrlInstagram())) {
				originalEnte.setUrlInstagram(patchEnte.getUrlInstagram());
			}
			// urlTwitter
			if(StringUtils.hasText(patchEnte.getUrlTwitter())) {
				originalEnte.setUrlTwitter(patchEnte.getUrlTwitter());
			}
			
			
			// comune
			if (patchEnte.getComune() != null) {
				Comune e = ObjectMapperUtils.map(patchEnte.getComune(), Comune.class);
				originalEnte.setComune(e);
			}
			// enteUnione
			if (patchEnte.getEnteUnione() != null) {
				Ente e = ObjectMapperUtils.map(patchEnte.getEnteUnione(), Ente.class);
				originalEnte.setEnteUnione(e);
			}
			// categoria
			if (patchEnte.getCategoria() != null) {
				CategoriaEnte c = ObjectMapperUtils.map(patchEnte.getCategoria(), CategoriaEnte.class);
				originalEnte.setCategoria(c);
			}
			
			// dtFineVal
			if (patchEnte.getDtFineVal() != null) {
				originalEnte.setDtFineVal(patchEnte.getDtFineVal());
			}
			
		} else {
			enteResult = ObjectMapperUtils.map(patchEnte, originalEnte);
		}
		
		return enteResult;
	}


}
