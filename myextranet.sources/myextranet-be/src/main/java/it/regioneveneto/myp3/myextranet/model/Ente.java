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
package it.regioneveneto.myp3.myextranet.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The persistent class for the myext_ente database table.
 * 
 */
@Entity
@Table(name="myext_ente")
@NamedQuery(name="Ente.findAll", query="SELECT e FROM Ente e")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Ente extends AuditWithValidityModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_ente", unique=true, nullable=false)
	private Integer idEnte;

	@Column(name="cod_ipa", nullable=false, length=15)
	private String codIpa;
	
	@Column(name="denominazione", nullable=false, length=250)
	private String denominazione;

	@Column(name="logo", nullable=false, length=250)
	private String logo;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="cod_comune", nullable=false)
	private Comune comune;

	@Column(name="indirizzo", nullable=false, length=500)
	private String indirizzo;
	
	@Column(name="cap", nullable=false, length=10)
	private String cap;
	
	@Column(name="email", nullable=false, length=100)
	private String email;
	
	@Column(name="pec", nullable=false, length=100)
	private String pec;
	
	@Column(name="cod_fiscale", nullable=false, length=16)
	private String codFiscale;
	
	@Column(name="telefono", nullable=true, length=30)
	private String telefono;
	
	@Column(name="fax", nullable=true, length=30)
	private String fax;
	
	@Column(name="url_web_site", nullable=true, length=500)
	private String urlWebSite;
	
	@Column(name="url_facebook", nullable=true, length=250)
	private String urlFacebook;
	
	@Column(name="url_instagram", nullable=true, length=250)
	private String urlInstagram;
	
	@Column(name="url_twitter", nullable=true, length=250)
	private String urlTwitter;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_ente_unione", nullable=true)
	private Ente enteUnione;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_categoria", nullable=false)
	private CategoriaEnte categoria;
		
	public Ente() {
	}

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

	public Comune getComune() {
		return comune;
	}

	public void setComune(Comune comune) {
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

	public Ente getEnteUnione() {
		return enteUnione;
	}

	public void setEnteUnione(Ente enteUnione) {
		this.enteUnione = enteUnione;
	}

	public CategoriaEnte getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaEnte categoria) {
		this.categoria = categoria;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Ente ente = (Ente) o;
		return Objects.equals(idEnte, ente.idEnte);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idEnte);
	}
}