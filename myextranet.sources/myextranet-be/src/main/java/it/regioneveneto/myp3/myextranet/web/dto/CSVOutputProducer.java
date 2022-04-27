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

import java.io.PrintWriter;

public abstract class CSVOutputProducer {
	
	protected String separator = ",";

	protected String getSeparator() {
		return separator;
	}

	protected void setSeparator(String separator) {
		this.separator = separator;
	}

	public CSVOutputProducer() {
		
	}

	public abstract String toCSVRow();
	
	public abstract String csvHeader();
	
	public void writeCSVRow(PrintWriter writer) {
		writer.println(toCSVRow());
	}
	
	public void writeCSVHeader(PrintWriter writer) {
		writer.println(csvHeader());
	}
}
