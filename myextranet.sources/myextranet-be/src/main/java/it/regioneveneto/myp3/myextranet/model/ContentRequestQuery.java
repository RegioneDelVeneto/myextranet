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

import java.util.Map;

public class ContentRequestQuery {
	
	private Map<String, String> preconditions;
	private Map<String, Object> attributes;
	private Map<String, Object> dateRange;
	
	
	public Map<String, String> getPreconditions() {
		return preconditions;
	}
	
	public void setPreconditions(Map<String, String> preconditions) {
		this.preconditions = preconditions;
	}
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}
	
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	public Map<String, Object> getDateRange() {
		return dateRange;
	}
	
	public void setDateRange(Map<String, Object> dateRange) {
		this.dateRange = dateRange;
	}
	
	@Override
	public String toString() {
		return "ContentRequestQuery [preconditions=" + preconditions + ", attributes=" + attributes + ", dateRange="
				+ dateRange + "]";
	}

	public ContentRequestQuery(Map<String, String> preconditions, Map<String, Object> attributes,
			Map<String, Object> dateRange) {
		super();
		this.preconditions = preconditions;
		this.attributes = attributes;
		this.dateRange = dateRange;
	}
	


	
}
