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
 * The persistent class for the myext_evento_iscritti database table.
 * 
 */
@Entity
@Table(name = "myext_evento_iscritti")
@NamedQuery(name = "IscrittoEvento.findAll", query = "SELECT i FROM IscrittoEvento i")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class IscrittoEvento extends AuditModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_iscritto", unique = true, nullable = false)
	private Integer idIscritto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_utente", nullable = true)
	private Utente utente;

	@Column(name = "id_evento", nullable = false, length = 250)
	private String idEvento;

	@Column(name = "flg_relatore", nullable = false)
	private Integer flgRelatore;

	@Column(name = "flg_partecip_loco", nullable = false)
	private Integer flgPartecipLoco;

	@Column(name = "flg_partecip_remoto", nullable = false)
	private Integer flgPartecipRemoto;

	@Column(name = "flg_partecip_pref", nullable = true, length = 3)
	private String flgPartecipPref;

	@Column(name = "dt_iscrizione", nullable = false)
	protected LocalDate dtIscrizione;

	@Column(name = "dt_invio_attestato", nullable = true)
	protected LocalDate dtInvioAttestato;

	@Column(name = "dt_rich_questionario", nullable = true)
	protected LocalDate dtRichQuestionario;

	@Column(name = "nome", nullable = true, length = 150)
	private String nome;

	@Column(name = "cognome", nullable = true, length = 150)
	private String cognome;

	@Column(name = "email", nullable = true, length = 150)
	private String email;
	
	@Column(name="azienda", nullable=true, length=250)
	private String azienda;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ente", nullable = true)
	private Ente ente;

	public Integer getIdIscritto() {
		return idIscritto;
	}

	public void setIdIscritto(Integer idIscritto) {
		this.idIscritto = idIscritto;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
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

	public Ente getEnte() {
		return ente;
	}

	public void setEnte(Ente ente) {
		this.ente = ente;
	}

	public IscrittoEvento() {
	}
	
	

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		IscrittoEvento iscritto = (IscrittoEvento) o;
		return Objects.equals(idIscritto, iscritto.idIscritto);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idIscritto);
	}
}