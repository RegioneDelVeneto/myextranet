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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The persistent class for the myext_ente database table.
 * 
 */
@Entity
@Table(name="myext_utente")
@NamedQuery(name="Utente.findAll", query="SELECT u FROM Utente u")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Utente extends AuditWithValidityModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_utente", unique=true, nullable=false)
	private Integer idUtente;

	@Column(name="nome", nullable=false, length=150)
	private String nome;
	
	@Column(name="cognome", nullable=false, length=150)
	private String cognome;

	@Column(name="cod_fiscale", nullable=false, length=16)
	private String codFiscale;
	
	@Column(name="email", nullable=false, length=150)
	private String email;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_ente", nullable=true)
	private Ente ente;

	@Column(name="azienda", nullable=true, length=250)
	private String azienda;

	@Column(name="partita_iva", nullable=true, length=11)
	private String partitaIva;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_utente_padre", nullable=true)
	private Utente utentePadre;
	
	@Column(name="telefono", nullable=true, length=25)
	private String telefono;
	
	@Column(name="telefono_uff", nullable=true, length=25)
	private String telefonoUff;
	
	@Column(name = "flg_archived", nullable = true)
	private Integer flgArchived;
	
	@OneToMany(mappedBy = "utente")
    private Set<UtenteMessaggi> utenteMessaggi;
	
	public Utente() {
	}

	public Integer getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(Integer idUtente) {
		this.idUtente = idUtente;
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

	public String getCodFiscale() {
		return codFiscale;
	}

	public void setCodFiscale(String codFiscale) {
		this.codFiscale = codFiscale;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Ente getEnte() {
		return ente;
	}

	public void setEnte(Ente ente) {
		this.ente = ente;
	}

	public String getAzienda() {
		return azienda;
	}

	public void setAzienda(String azienda) {
		this.azienda = azienda;
	}

	public String getPartitaIva() { return partitaIva; }

	public void setPartitaIva(String partitaIva) { this.partitaIva = partitaIva; }

	public Utente getUtentePadre() {
		return utentePadre;
	}

	public void setUtentePadre(Utente utentePadre) {
		this.utentePadre = utentePadre;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Set<UtenteMessaggi> getUtenteMessaggi() {
		return utenteMessaggi;
	}

	public void setUtenteMessaggi(Set<UtenteMessaggi> utenteMessaggi) {
		this.utenteMessaggi = utenteMessaggi;
	}

	public Integer getFlgArchived() {
		return flgArchived;
	}

	public void setFlgArchived(Integer flgArchived) {
		this.flgArchived = flgArchived;
	}

	public String getTelefonoUff() {
		return telefonoUff;
	}

	public void setTelefonoUff(String telefonoUff) {
		this.telefonoUff = telefonoUff;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Utente utente = (Utente) o;
		return Objects.equals(idUtente, utente.idUtente);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idUtente);
	}
}