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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import it.regioneveneto.myp3.myextranet.bean.StatEventiRow;
import it.regioneveneto.myp3.myextranet.bean.StatPartecipantiRow;
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.utils.StringUtils;

public class StatPartecipantiRecordDTO extends CSVOutputProducer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String piattaformaAbilitante;
	private String prodotto;
	private String progetto;
	private String evento;
	private String dataDa;
	private String dataA;
	private String partecipante;
	private String ente;
	private String categoriaEnte;
	
	private String idEvento;

	public String getPiattaformaAbilitante() {
		return piattaformaAbilitante;
	}

	public void setPiattaformaAbilitante(String piattaformaAbilitante) {
		this.piattaformaAbilitante = piattaformaAbilitante;
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

	public String getEvento() {
		return evento;
	}

	public void setEvento(String evento) {
		this.evento = evento;
	}

	public String getDataDa() {
		return dataDa;
	}

	public void setDataDa(String dataDa) {
		this.dataDa = dataDa;
	}

	public String getDataA() {
		return dataA;
	}

	public void setDataA(String dataA) {
		this.dataA = dataA;
	}

	public String getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(String idEvento) {
		this.idEvento = idEvento;
	}

	public String getPartecipante() {
		return partecipante;
	}

	public void setPartecipante(String partecipante) {
		this.partecipante = partecipante;
	}

	public String getEnte() {
		return ente;
	}

	public void setEnte(String ente) {
		this.ente = ente;
	}

	public String getCategoriaEnte() {
		return categoriaEnte;
	}

	public void setCategoriaEnte(String categoriaEnte) {
		this.categoriaEnte = categoriaEnte;
	}

	public StatPartecipantiRecordDTO() {	
	}

	@Override
	public String toString() {
		return "StatPartecipantiRecordDTO [piattaformaAbilitante=" + piattaformaAbilitante + ", prodotto=" + prodotto
				+ ", progetto=" + progetto + ", evento=" + evento + ", dataDa=" + dataDa + ", dataA=" + dataA
				+ ", partecipante=" + partecipante + ", ente=" + ente + ", categoriaEnte=" + categoriaEnte
				+ ", idEvento=" + idEvento + "]";
	}

	@Override
	public String toCSVRow() {
		
		return 
				"\"" + StringUtils.stringOrEmpty(evento) + "\"" + separator +
				StringUtils.stringOrEmpty(dataDa) + separator + 
				StringUtils.stringOrEmpty(dataA) + separator + 
				"\"" + StringUtils.stringOrEmpty(partecipante) + "\"" + separator +
				"\"" + StringUtils.stringOrEmpty(ente) + "\"" + separator +
				"\"" + StringUtils.stringOrEmpty(categoriaEnte) + "\"" + separator +
				"\"" + StringUtils.stringOrEmpty(prodotto) + "\"" + separator + 
				"\"" + StringUtils.stringOrEmpty(progetto) + "\"" + separator +
				"\"" + StringUtils.stringOrEmpty(piattaformaAbilitante) + "\""; 
	}
	
	

	@Override
	public String csvHeader() {
		return 
				"Evento" + separator +
				"Data da" + separator +
				"Data a" + separator +
				"Partecipante" + separator +
				"Ente" + separator +
				"Categoria Ente" + separator +
				"Prodotto" + separator +
				"Progetto" + separator +
				"Piattaforma abilitante"
				;

	}
	
	public static StatPartecipantiRecordDTO fromMyExtranetContent(MyExtranetContent content) {
		StatPartecipantiRecordDTO record = new StatPartecipantiRecordDTO();
		
		record.setPiattaformaAbilitante(content.getPiattaformaAbilitante());
		record.setProdotto(content.getProdotto());
		record.setProgetto(content.getProgetto());
		record.setEvento(content.getTitolo());
		record.setDataDa(content.getDateDa());
		record.setDataA(content.getDateA());
		record.setIdEvento(content.getId());
		
		return record;
	}

	public static StatPartecipantiRecordDTO fromStatPartecipantiRow(StatPartecipantiRow row) {
		StatPartecipantiRecordDTO record = new StatPartecipantiRecordDTO();
		
		record.setPartecipante(row.getPartecipante());
		record.setEnte(row.getEnte());
		record.setCategoriaEnte(row.getCategoriaEnte());
		record.setIdEvento(row.getEvento());
		
		return record;
	}
	
	public void mergeWithDBData(List<StatPartecipantiRow> rows) {
		Optional<StatPartecipantiRow> rowOptional = rows.stream().filter(r -> r.getEvento().equals(this.idEvento)).findAny();
		
		if (rowOptional.isPresent()) {
			StatPartecipantiRow row = rowOptional.get();
			this.setPartecipante(row.getPartecipante());
			this.setEnte(row.getEnte());
			this.setCategoriaEnte(row.getCategoriaEnte());
		}
	}

	public void mergeWithMyPortalData(List<MyExtranetContent> contents) {
		Optional<MyExtranetContent> contentOptional = contents.stream().filter(c -> c.getId().equals(this.idEvento)).findAny();
		
		if (contentOptional.isPresent()) {
			MyExtranetContent content = contentOptional.get();
			this.setPiattaformaAbilitante(content.getPiattaformaAbilitante());
			this.setProdotto(content.getProdotto());
			this.setProgetto(content.getProgetto());
			this.setEvento(content.getTitolo());
			this.setDataDa(content.getDateDa());
			this.setDataA(content.getDateA());
			this.setIdEvento(content.getId());
		}
	}

	public void resolveRefs(List<MyExtranetContent> refs, Map<String, String> productPlatformMap) {
		String idProdotto = this.getProdotto();
		
		this.setPiattaformaAbilitante(resolveRefTitolo(refs, productPlatformMap.get(idProdotto)));
		this.setProdotto(resolveRefTitolo(refs, idProdotto));
		this.setProgetto(resolveRefTitolo(refs, this.getProgetto()));
	}
	
	private String resolveRefTitolo(List<MyExtranetContent> refs, String refToResolve) {
		if (refToResolve == null) return null;
		
		Optional refTitoloOptional = refs.stream().filter(r -> r.getId().equals(refToResolve)).map(r -> r.getTitolo()).findAny();
		return refTitoloOptional.isPresent() ? (String) refTitoloOptional.get() : refToResolve;
	}



}
