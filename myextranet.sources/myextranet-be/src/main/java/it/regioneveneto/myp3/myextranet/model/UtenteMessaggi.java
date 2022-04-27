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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.regioneveneto.myp3.myextranet.model.key.UtenteMessaggiKey;

@Entity
@Table(name="myext_utente_messaggi")
@NamedQuery(name="UtenteMessaggi.findAll", query="SELECT e FROM UtenteMessaggi e")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class UtenteMessaggi extends AuditModel implements Serializable {
	
	@EmbeddedId
	UtenteMessaggiKey id = new UtenteMessaggiKey();
	
	@ManyToOne
    @MapsId("idUtente")
    @JoinColumn(name = "id_utente")
    Utente utente;
	
	@ManyToOne
    @MapsId("idMessaggio")
    @JoinColumn(name = "id_messaggio")
    Messaggio messaggio;
	
	@Column(name = "flg_online", nullable = false)
	private Integer flgOnline;

	@Column(name = "flg_read_online", nullable = false)
	private Integer flgReadOnline;
	
	@Column(name = "dt_read_online", nullable = true)
	protected LocalDate dtReadOnline;

	public UtenteMessaggiKey getId() {
		return id;
	}

	public void setId(UtenteMessaggiKey id) {
		this.id = id;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

	public Messaggio getMessaggio() {
		return messaggio;
	}

	public void setMessaggio(Messaggio messaggio) {
		this.messaggio = messaggio;
	}

	public Integer getFlgOnline() {
		return flgOnline;
	}

	public void setFlgOnline(Integer flgOnline) {
		this.flgOnline = flgOnline;
	}

	public Integer getFlgReadOnline() {
		return flgReadOnline;
	}

	public void setFlgReadOnline(Integer flgReadOnline) {
		this.flgReadOnline = flgReadOnline;
	}

	public LocalDate getDtReadOnline() {
		return dtReadOnline;
	}

	public void setDtReadOnline(LocalDate dtReadOnline) {
		this.dtReadOnline = dtReadOnline;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dtReadOnline == null) ? 0 : dtReadOnline.hashCode());
		result = prime * result + ((flgOnline == null) ? 0 : flgOnline.hashCode());
		result = prime * result + ((flgReadOnline == null) ? 0 : flgReadOnline.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((messaggio == null) ? 0 : messaggio.hashCode());
		result = prime * result + ((utente == null) ? 0 : utente.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UtenteMessaggi utenteMessaggi = (UtenteMessaggi) o;
		return Objects.equals(id, utenteMessaggi.id);
	}
	
}
