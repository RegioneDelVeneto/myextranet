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
* The persistent class for the myext_prodotto_attivabile database table.
* 
*/
@Entity
@Table(name = "myext_prodotto_attivabile")
@NamedQuery(name = "ProdottoAttivabile.findAll", query = "SELECT p FROM ProdottoAttivabile p")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class ProdottoAttivabile extends AuditModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_prodotto_att", nullable = false)
	private Integer idProdottoAtt;
	
	@Column(name = "id_prodotto", nullable = true, length = 250)
	private String idProdotto;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prodotto_att_prop", nullable = true)
	private ProdottoAttivabile prodottoProp;
	
	@Column(name="nome_prodotto_attiv", nullable=false, length=150)
	protected String nomeProdottoAttiv;

	@Column(name="des_attivazione", nullable=true, length=4000)
	protected String desAttivazione;

	@Column(name="des_attivazione_breve", nullable=true, length=250)
	protected String desAttivazioneBreve;

	@Column(name="dt_attivabile_da", nullable=false)
	private LocalDate dtAttivabileDa;
	
	@Column(name="dt_attivabile_a", nullable=false)
	private LocalDate dtAttivabileA;
	
	@Column(name="cod_app_prof_man", nullable=true, length=20)
	protected String codAppProfMan;

	public Integer getIdProdottoAtt() {
		return idProdottoAtt;
	}

	public void setIdProdottoAtt(Integer idProdottoAtt) {
		this.idProdottoAtt = idProdottoAtt;
	}

	public String getIdProdotto() {
		return idProdotto;
	}

	public void setIdProdotto(String idProdotto) {
		this.idProdotto = idProdotto;
	}

	public String getNomeProdottoAttiv() {
		return nomeProdottoAttiv;
	}

	public void setNomeProdottoAttiv(String nomeProdottoAttiv) {
		this.nomeProdottoAttiv = nomeProdottoAttiv;
	}

	public String getDesAttivazione() {
		return desAttivazione;
	}

	public void setDesAttivazione(String desAttivazione) {
		this.desAttivazione = desAttivazione;
	}

	public String getDesAttivazioneBreve() {
		return desAttivazioneBreve;
	}

	public void setDesAttivazioneBreve(String desAttivazioneBreve) {
		this.desAttivazioneBreve = desAttivazioneBreve;
	}

	public LocalDate getDtAttivabileDa() {
		return dtAttivabileDa;
	}

	public void setDtAttivabileDa(LocalDate dtAttivabileDa) {
		this.dtAttivabileDa = dtAttivabileDa;
	}

	public LocalDate getDtAttivabileA() {
		return dtAttivabileA;
	}

	public void setDtAttivabileA(LocalDate dtAttivabileA) {
		this.dtAttivabileA = dtAttivabileA;
	}

	public ProdottoAttivabile getProdottoProp() {
		return prodottoProp;
	}

	public void setProdottoProp(ProdottoAttivabile prodottoProp) {
		this.prodottoProp = prodottoProp;
	}

	public String getCodAppProfMan() {
		return codAppProfMan;
	}

	public void setCodAppProfMan(String codAppProfMan) {
		this.codAppProfMan = codAppProfMan;
	}

	public ProdottoAttivabile() {
		
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ProdottoAttivabile prodotto = (ProdottoAttivabile) o;
		return Objects.equals(idProdottoAtt, prodotto.idProdottoAtt);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idProdottoAtt);
	}

}
