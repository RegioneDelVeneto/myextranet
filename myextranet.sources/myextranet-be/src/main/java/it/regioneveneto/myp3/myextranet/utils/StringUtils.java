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
package it.regioneveneto.myp3.myextranet.utils;

import java.util.Arrays;

public class StringUtils {

	public static String stringOrEmpty(Object o) {
		if (o == null) return "";
		
		return o.toString();
	}
	
	public static String stringOrDefault(Object o, String defaultStr) {
		if (o == null) return defaultStr;
		
		return o.toString();
	}
	
	public static String concatenateNotNulls(String [] tokens, String separator) {
		if (separator == null) {
			separator = ", ";
		}
		String[] cleanedUp = Arrays.asList(tokens).stream()
			.filter(t -> org.springframework.util.StringUtils.hasText(t)).toArray(String[]::new);
		
		if (cleanedUp.length == 0) {
			return "";
		}
		
		return String.join(separator, cleanedUp);
	}
}
