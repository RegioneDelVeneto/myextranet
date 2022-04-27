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
@Table(name = "myext_ente_rappr")
@NamedQuery(name = "RappresentanteEnte.findAll", query = "SELECT r FROM RappresentanteEnte r")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class RappresentanteEnte extends AuditWithValidityModel implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_rappr", unique = true, nullable = false)
	private Integer idRappr;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_utente", nullable = false)
	private Utente utente;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_ente", nullable = false)
	private Ente ente;

	@Column(name = "tipo_rappr", nullable = false, length = 3)
	private String tipoRappr; // RAP=Responsabile Attivazione Prodotti, GRE= Gestore Ruoli Ente
	
	@Column(name = "num_protocollo", nullable = true, length = 20)
	private String numProtocollo;

	@Column(name = "dt_protocollo", nullable = true)
	private LocalDate dtProtocollo;

	@Column(name = "dt_richiesta", nullable = false)
	protected LocalDate dtRichiesta;

	@Column(name = "flg_conferma", nullable = false)
	private Integer flgConferma;

	@Column(name = "dt_conferma", nullable = true)
	protected LocalDate dtConferma;
	
	@Column(name="motiv_conferma", nullable=true, length=1000)
	protected String motivConferma;

	public Integer getIdRappr() {
		return idRappr;
	}

	public void setIdRappr(Integer idRappr) {
		this.idRappr = idRappr;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

	public Ente getEnte() {
		return ente;
	}

	public void setEnte(Ente ente) {
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

	public RappresentanteEnte() {
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		RappresentanteEnte rappresentante = (RappresentanteEnte) o;
		return Objects.equals(idRappr, rappresentante.idRappr);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idRappr);
	}
}