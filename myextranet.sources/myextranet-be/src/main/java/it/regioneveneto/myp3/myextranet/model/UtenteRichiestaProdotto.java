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
* The persistent class for the myext_prodotto_rich_utenti database table.
* 
*/
@Entity
@Table(name = "myext_prodotto_rich_utenti")
@NamedQuery(name = "UtenteRichiestaProdotto.findAll", query = "SELECT u FROM UtenteRichiestaProdotto u")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class UtenteRichiestaProdotto extends AuditModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id_utente_rich", unique=true, nullable=false)
	private Integer idUtenteRich;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prod_attiv_rich", nullable = false)
	private RichiestaProdotto richiestaProdotto;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_utente_prod", nullable = true)
	private UtenteProdottoAttivato utenteProdottoAttivato;

	@Column(name="rich_oper", nullable=true, length=5)
	private String richOper;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cod_ruolo", nullable = false)
	private RuoloProdotto ruoloProdotto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_utente", nullable = true)
	private Utente utente;

	@Column(name="cod_fiscale", nullable=true, length=16)
	private String codFiscale;
	
	@Column(name="nome", nullable=true, length=150)
	private String nome;
	
	@Column(name="cognome", nullable=true, length=150)
	private String cognome;

	@Column(name="email", nullable=true, length=150)
	private String email;

	@Column(name="telefono", nullable=true, length=25)
	private String telefono;

	public Integer getIdUtenteRich() {
		return idUtenteRich;
	}

	public void setIdUtenteRich(Integer idUtenteRich) {
		this.idUtenteRich = idUtenteRich;
	}

	public RichiestaProdotto getRichiestaProdotto() {
		return richiestaProdotto;
	}

	public void setRichiestaProdotto(RichiestaProdotto richiestaProdotto) {
		this.richiestaProdotto = richiestaProdotto;
	}

	public UtenteProdottoAttivato getUtenteProdottoAttivato() {
		return utenteProdottoAttivato;
	}

	public void setUtenteProdottoAttivato(UtenteProdottoAttivato utenteProdottoAttivato) {
		this.utenteProdottoAttivato = utenteProdottoAttivato;
	}

	public String getRichOper() {
		return richOper;
	}

	public void setRichOper(String richOper) {
		this.richOper = richOper;
	}

	public RuoloProdotto getRuoloProdotto() {
		return ruoloProdotto;
	}

	public void setRuoloProdotto(RuoloProdotto ruoloProdotto) {
		this.ruoloProdotto = ruoloProdotto;
	}

	public Utente getUtente() {
		return utente;
	}

	public void setUtente(Utente utente) {
		this.utente = utente;
	}

	public String getCodFiscale() {
		return codFiscale;
	}

	public void setCodFiscale(String codFiscale) {
		this.codFiscale = codFiscale;
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

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public UtenteRichiestaProdotto() {
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UtenteRichiestaProdotto utente = (UtenteRichiestaProdotto) o;
		return Objects.equals(idUtenteRich, utente.idUtenteRich);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idUtenteRich);
	}

}
