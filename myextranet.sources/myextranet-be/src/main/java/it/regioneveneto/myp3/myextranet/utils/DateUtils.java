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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.util.StringUtils;

public class DateUtils {
	
	public static String formatDateRange(String dataDa, String dataA, String pattern, boolean compact) throws ParseException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date dateFrom = simpleDateFormat.parse(dataDa);
		
		if (!StringUtils.hasLength(dataA)) {
			return formatSingleDate(dateFrom);
		}
		
		Date dateTo = simpleDateFormat.parse(dataA);
		return formatDateRange(dateFrom, dateTo, compact);
	}

	private static String formatSingleDate(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm"); 
		return format.format(date);
	}

	public static String formatDateRange(Date dateFrom, Date dateTo, boolean compact) {
		
		SimpleDateFormat dayFormat = new SimpleDateFormat("dd/MM/yyyy"); 
		String dayFrom = dayFormat.format(dateFrom);
		String dayTo = dayFormat.format(dateTo);
		SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
		String timeFrom = timeFormat.format(dateFrom);
		String timeTo = timeFormat.format(dateTo);
		// check if same-day
		boolean isSameDay = dayFrom.equals(dayTo);
		
		if (isSameDay) {
			String pattern = compact ? "%s %s - %s" : "in data %s dalle ore %s alle ore %s";
			return String.format(pattern, dayFrom, timeFrom, timeTo);
		} else {
			String pattern = compact ? "%s %s - %s %s" : "dal %s alle ore %s al %s alle ore %s";				
			return String.format(pattern, dayFrom, timeFrom, dayTo, timeTo);
		}
	}

}
