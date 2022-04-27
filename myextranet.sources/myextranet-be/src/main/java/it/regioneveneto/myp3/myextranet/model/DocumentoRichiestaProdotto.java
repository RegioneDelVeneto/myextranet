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
 * The persistent class for the myext_prodotto_rich_docs database table.
 * 
 */
@Entity
@Table(name = "myext_prodotto_rich_docs")
@NamedQuery(name = "DocumentoRichiestaProdotto.findAll", query = "SELECT d FROM DocumentoRichiestaProdotto d")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class DocumentoRichiestaProdotto /* extends AuditModel */ implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_prod_rich_doc", unique = true, nullable = false)
	private Integer idProdRichDoc;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_prod_attiv_rich", nullable = false)
	private RichiestaProdotto richiestaProdotto;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_step", nullable = false)
	private StepRichiestaProdotto stepRichiestaProdotto;
	
	@Column(name = "id_documento", nullable = false, length = 250)
	private String idDocumento;

	@Column(name = "nome_documento", nullable = false, length = 150)
	private String nomeDocumento;

	@Column(name = "flg_enabled", nullable = false)
	private Integer flgEnabled;


	public Integer getIdProdRichDoc() {
		return idProdRichDoc;
	}

	public void setIdProdRichDoc(Integer idProdRichDoc) {
		this.idProdRichDoc = idProdRichDoc;
	}

	public RichiestaProdotto getRichiestaProdotto() {
		return richiestaProdotto;
	}

	public void setRichiestaProdotto(RichiestaProdotto richiestaProdotto) {
		this.richiestaProdotto = richiestaProdotto;
	}

	public StepRichiestaProdotto getStepRichiestaProdotto() {
		return stepRichiestaProdotto;
	}

	public void setStepRichiestaProdotto(StepRichiestaProdotto stepRichiestaProdotto) {
		this.stepRichiestaProdotto = stepRichiestaProdotto;
	}

	public String getIdDocumento() {
		return idDocumento;
	}

	public void setIdDocumento(String idDocumento) {
		this.idDocumento = idDocumento;
	}

	public String getNomeDocumento() {
		return nomeDocumento;
	}

	public void setNomeDocumento(String nomeDocumento) {
		this.nomeDocumento = nomeDocumento;
	}

	public Integer getFlgEnabled() {
		return flgEnabled;
	}

	public void setFlgEnabled(Integer flgEnabled) {
		this.flgEnabled = flgEnabled;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DocumentoRichiestaProdotto doc = (DocumentoRichiestaProdotto) o;
		return Objects.equals(idProdRichDoc, doc.idProdRichDoc);
	}

	@Override
	public int hashCode() {
		return Objects.hash(idProdRichDoc);
	}
}