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
import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.utils.StringUtils;

public class StatEventiRecordDTO extends CSVOutputProducer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String piattaformaAbilitante;
	private String prodotto;
	private String progetto;
	private String evento;
	private String dataDa;
	private String dataA;
	private int numIscritti;
	private int numPartecipanti;
	private int numEntiCoinvolti;
	
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

	public int getNumIscritti() {
		return numIscritti;
	}

	public void setNumIscritti(int numIscritti) {
		this.numIscritti = numIscritti;
	}

	public int getNumPartecipanti() {
		return numPartecipanti;
	}

	public void setNumPartecipanti(int numPartecipanti) {
		this.numPartecipanti = numPartecipanti;
	}

	public int getNumEntiCoinvolti() {
		return numEntiCoinvolti;
	}

	public void setNumEntiCoinvolti(int numEntiCoinvolti) {
		this.numEntiCoinvolti = numEntiCoinvolti;
	}

	public String getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(String idEvento) {
		this.idEvento = idEvento;
	}

	public StatEventiRecordDTO() {
	}

	@Override
	public String toString() {
		return "StatEventiRecordDTO [piattaformaAbilitante=" + piattaformaAbilitante + ", prodotto=" + prodotto
				+ ", progetto=" + progetto + ", evento=" + evento + ", dataDa=" + dataDa + ", dataA=" + dataA
				+ ", numIscritti=" + numIscritti + ", numPartecipanti=" + numPartecipanti + ", numEntiCoinvolti="
				+ numEntiCoinvolti + "]";
	}

	@Override
	public String toCSVRow() {
		
		return "\"" + StringUtils.stringOrEmpty(piattaformaAbilitante) + "\"" + separator + 
				"\"" + StringUtils.stringOrEmpty(prodotto) + "\"" + separator + 
				"\"" + StringUtils.stringOrEmpty(progetto) + "\"" + separator +
				"\"" + StringUtils.stringOrEmpty(evento) + "\"" + separator +
				StringUtils.stringOrEmpty(dataDa) + separator + 
				StringUtils.stringOrEmpty(dataA) + separator + 
				numIscritti + separator + 
				numPartecipanti + separator	+ 
				numEntiCoinvolti;
	}
	
	

	@Override
	public String csvHeader() {
		return "Piattaforma abilitante" + separator + 
				"Prodotto" + separator +
				"Progetto" + separator +
				"Evento" + separator +
				"Data da" + separator +
				"Data a" + separator +
				"Numero iscritti" + separator +
				"Numero partecipanti" + separator +
				"Numero enti coinvolti"
				;

	}
	
	public static StatEventiRecordDTO fromMyExtranetContent(MyExtranetContent content) {
		StatEventiRecordDTO record = new StatEventiRecordDTO();
		
		record.setPiattaformaAbilitante(content.getPiattaformaAbilitante());
		record.setProdotto(content.getProdotto());
		record.setProgetto(content.getProgetto());
		record.setEvento(content.getTitolo());
		record.setDataDa(content.getDateDa());
		record.setDataA(content.getDateA());
		record.setIdEvento(content.getId());
		
		return record;
	}

	public void mergeWithDBData(List<StatEventiRow> rows) {
		Optional<StatEventiRow> rowOptional = rows.stream().filter(r -> r.getEvento().equals(this.idEvento)).findAny();
		
		if (rowOptional.isPresent()) {
			StatEventiRow row = rowOptional.get();
			this.setNumIscritti(row.getNumIscritti());
			this.setNumPartecipanti(row.getNumPartecipanti());
			this.setNumEntiCoinvolti(row.getNumEnti());
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
