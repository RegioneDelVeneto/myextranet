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
package it.regioneveneto.myp3.myextranet.web.dto;

public class EventoDTO {

	private String titolo;
	private String descrizione;
	private String data;
	private String prodotto;
	private String progetto;
	private String id;
	private String tipo;
	private String inPresenza;
	private String inStreaming;
	private Integer numeroMaxPartecipanti;
	private String luogo;
	private String parent;
	private String aula;
	private String sottotitolo;
	private String dateDa;
	private String dateA;
	private String urlQuestionario;

	private Integer postiDisponibili;

	public String getTitolo() {
		return titolo;
	}

	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getProdotto() {
		return prodotto;
	}

	public void setProdotto(String prodotto) {
		this.prodotto = prodotto;
	}

	public String getProgetto() {
		return progetto;
	}

	public void setProgetto(String progetto) {
		this.progetto = progetto;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getInPresenza() {
		return inPresenza;
	}

	public void setInPresenza(String inPresenza) {
		this.inPresenza = inPresenza;
	}

	public String getInStreaming() {
		return inStreaming;
	}

	public void setInStreaming(String inStreaming) {
		this.inStreaming = inStreaming;
	}

	public Integer getNumeroMaxPartecipanti() {
		return numeroMaxPartecipanti;
	}

	public void setNumeroMaxPartecipanti(Integer numeroMaxPartecipanti) {
		this.numeroMaxPartecipanti = numeroMaxPartecipanti;
	}

	public String getLuogo() {
		return luogo;
	}

	public void setLuogo(String luogo) {
		this.luogo = luogo;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public String getAula() {
		return aula;
	}

	public void setAula(String aula) {
		this.aula = aula;
	}

	public String getSottotitolo() {
		return sottotitolo;
	}

	public void setSottotitolo(String sottotitolo) {
		this.sottotitolo = sottotitolo;
	}

	public String getDateDa() {
		return dateDa;
	}

	public void setDateDa(String dateDa) {
		this.dateDa = dateDa;
	}

	public String getDateA() {
		return dateA;
	}

	public void setDateA(String dateA) {
		this.dateA = dateA;
	}

	public String getUrlQuestionario() {
		return urlQuestionario;
	}

	public void setUrlQuestionario(String urlQuestionario) {
		this.urlQuestionario = urlQuestionario;
	}

	public Integer getPostiDisponibili() {
		return postiDisponibili;
	}

	public void setPostiDisponibili(Integer postiDisponibili) {
		this.postiDisponibili = postiDisponibili;
	}

	public EventoDTO() {
		
	}

	@Override
	public String toString() {
		return "EventoDTO [titolo=" + titolo + ", descrizione=" + descrizione + ", data=" + data + ", prodotto="
				+ prodotto + ", progetto=" + progetto + ", id=" + id + ", tipo=" + tipo + ", inPresenza=" + inPresenza
				+ ", inStreaming=" + inStreaming + ", numeroMaxPartecipanti=" + numeroMaxPartecipanti + ", luogo="
				+ luogo + ", parent=" + parent + ", aula=" + aula + ", sottotitolo=" + sottotitolo + ", dateDa="
				+ dateDa + ", dateA=" + dateA + ", urlQuestionario=" + urlQuestionario + ", postiDisponibili="
				+ postiDisponibili + "]";
	}

}
