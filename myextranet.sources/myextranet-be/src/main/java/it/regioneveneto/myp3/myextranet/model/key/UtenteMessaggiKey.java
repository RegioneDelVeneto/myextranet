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
package it.regioneveneto.myp3.myextranet.model.key;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class UtenteMessaggiKey implements Serializable {

	@Column(name = "id_utente")
    Integer idUtente;
 
    @Column(name = "id_messaggio")
    Integer idMessaggio;

	public Integer getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(Integer idUtente) {
		this.idUtente = idUtente;
	}

	public Integer getIdMessaggio() {
		return idMessaggio;
	}

	public void setIdMessaggio(Integer idMessaggio) {
		this.idMessaggio = idMessaggio;
	}

	public UtenteMessaggiKey(int idUtente, int idMessaggio) {
		super();
		this.idUtente = idUtente;
		this.idMessaggio = idMessaggio;
	}

	public UtenteMessaggiKey() {
		super();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + idMessaggio;
		result = prime * result + idUtente;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UtenteMessaggiKey other = (UtenteMessaggiKey) obj;
		if (idMessaggio != other.idMessaggio)
			return false;
		if (idUtente != other.idUtente)
			return false;
		return true;
	}


    
    
}
