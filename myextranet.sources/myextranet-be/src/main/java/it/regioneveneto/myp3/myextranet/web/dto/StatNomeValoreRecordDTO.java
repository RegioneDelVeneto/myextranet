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

import it.regioneveneto.myp3.myextranet.bean.StatNomeValoreRow;
import it.regioneveneto.myp3.myextranet.utils.StringUtils;

public class StatNomeValoreRecordDTO extends CSVOutputProducer implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String nome;
	private Object valore;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Object getValore() {
		return valore;
	}

	public void setValore(Object valore) {
		this.valore = valore;
	}

	public StatNomeValoreRecordDTO() {
	}

	@Override
	public String toString() {
		return "StatNomeValoreRecordDTO [nome=" + nome + ", valore=" + valore + "]";
	}
	
	@Override
	public String toCSVRow() {
		
		return 
				"\"" + StringUtils.stringOrEmpty(nome) + "\"" + separator +
				valore ; 
	}
	
	@Override
	public String csvHeader() {
		return 
				"Nome" + separator +
				"Valore"
				;
	}

	public static StatNomeValoreRecordDTO fromStatNomeValoreRow(StatNomeValoreRow row) {
		StatNomeValoreRecordDTO record = new StatNomeValoreRecordDTO();
		
		record.setNome(row.getNome());
		record.setValore(row.getValore());
		
		return record;
	}
}
