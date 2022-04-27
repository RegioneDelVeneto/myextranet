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

public class BeanUtils {

	public static boolean isEqual(Object val1, Object val2) {
		if (val1 == val2) {
			return true;
		}
		
		if (val1 == null || val2 == null) {
			return false;
		}
		
		return val1.equals(val2);
	}
	
	public static String flagToString(Integer flg) {
		return flg == 1 ? "true" : "false";
	}
	
	public static Integer stringToFlag(String flgStr) {
		return flgStr.equalsIgnoreCase("true") ? 1 : 0;
	}
}
