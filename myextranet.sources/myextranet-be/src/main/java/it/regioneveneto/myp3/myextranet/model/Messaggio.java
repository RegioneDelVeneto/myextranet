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
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * The persistent class for the myext_messaggi database table.
 * 
 */
@Entity
@Table(name="myext_messaggi")
@NamedQuery(name="Messaggio.findAll", query="SELECT m FROM Messaggio m")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Messaggio extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_messaggio", unique=true, nullable=false)
	private Integer idMessaggio;

	@Column(name="tipo", nullable=false, length=3)
	private String tipo;
	
	@Column(name="area", nullable=false, length=3)
	private String area;

	@Column(name="id_contenuto", nullable=true, length=250)
	private String idContenuto;
	
	@Column(name="destinatario", nullable=false, length=250)
	private String destinatario;
	
	@Column(name="indirizzo", nullable=false)
	private String indirizzo;
	
	@Column(name="titolo", nullable=false, length=150)
	private String titolo;

	@Column(name="messaggio", nullable=false)
	private String messaggio;

	@Column(name="dt_invio", nullable=false)
	protected LocalDate dtInvio;
	
	@OneToMany(mappedBy = "messaggio")
    private Set<UtenteMessaggi> utenteMessaggi;
	
	public Integer getIdMessaggio() {
		return idMessaggio;
	}

	public void setIdMessaggio(Integer idMessaggio) {
		this.idMessaggio = idMessaggio;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getDestinatario() {
		return destinatario;
	}

	public void setDestinatario(String destinatario) {
		this.destinatario = destinatario;
	}

	public String getIndirizzo() {
		return indirizzo;
	}

	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(String messaggio) {
		this.messaggio = messaggio;
	}

	public LocalDate getDtInvio() {
		return dtInvio;
	}

	public void setDtInvio(LocalDate dtInvio) {
		this.dtInvio = dtInvio;
	}

	public String getIdContenuto() {
		return idContenuto;
	}

	public void setIdContenuto(String idContenuto) {
		this.idContenuto = idContenuto;
	}

	public Set<UtenteMessaggi> getUtenteMessaggi() {
		return utenteMessaggi;
	}

	public void setUtenteMessaggi(Set<UtenteMessaggi> utenteMessaggi) {
		this.utenteMessaggi = utenteMessaggi;
	}

	public Messaggio() {
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Messaggio messaggio = (Messaggio) o;
		return Objects.equals(idMessaggio, messaggio.idMessaggio);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idMessaggio);
	}
}