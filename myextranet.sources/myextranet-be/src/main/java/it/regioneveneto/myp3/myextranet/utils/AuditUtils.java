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

import java.sql.Date;
import java.time.LocalDate;

import org.springframework.security.core.context.SecurityContextHolder;

import it.regioneveneto.myp3.myextranet.model.AuditModel;
import it.regioneveneto.myp3.myextranet.model.AuditWithValidityModel;
import it.regioneveneto.myp3.myextranet.security.UserWithAdditionalInfo;

public class AuditUtils {

	/**
	 * Fills in audit data in an AuditWithValidityModel object
	 * 
	 * @param auditModel object to fill
	 * @param idOper operator Id
	 * @param operator operator name
	 * @return true if new record
	 */
	public static boolean fillAudit(AuditWithValidityModel auditModel, String idOper, String operator) {
		boolean isNew = auditModel.getDtIns() == null;
		LocalDate today = LocalDate.now();
		
		if (isNew) {
			auditModel.setDtInizioVal(Date.valueOf(today));
			auditModel.setDtFineVal(Date.valueOf(Constants.DEFAULT_DATE_END_VALIDITY));
			auditModel.setDtIns(Date.valueOf(today));
			auditModel.setIdOperIns(idOper);
			auditModel.setOperIns(operator);
		} else {
			auditModel.setDtUltMod(Date.valueOf(today));
			auditModel.setIdOperUltMod(idOper);
			auditModel.setOperUltMod(operator);
		}
		return isNew;
	}
	
	public static boolean fillAudit(AuditWithValidityModel auditModel) {
		UserWithAdditionalInfo user = (UserWithAdditionalInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return fillAudit(auditModel, user.getUsername(), String.format("%s %s", user.getNome(), user.getCognome()));
	}
	
	/**
	 * Clears audit data in an AuditWithValidityModel object
	 * 
	 * @param auditModel
	 */
	public static void clearAudit(AuditWithValidityModel auditModel) {
		auditModel.setDtInizioVal(null);
		auditModel.setDtFineVal(null);
		auditModel.setDtIns(null);
		auditModel.setIdOperIns(null);
		auditModel.setOperIns(null);
		auditModel.setDtUltMod(null);
		auditModel.setIdOperUltMod(null);
		auditModel.setOperUltMod(null);
	}
	
	/**
	 * Fills in audit data in an AuditModel object
	 * 
	 * @param auditModel object to fill
	 * @param idOper operator Id
	 * @param operator operator name
	 * @return true if new record
	 */
	public static boolean fillAudit(AuditModel auditModel, String idOper, String operator) {
		boolean isNew = auditModel.getDtIns() == null;
		LocalDate today = LocalDate.now();
		
		if (isNew) {
			auditModel.setDtIns(Date.valueOf(today));
			auditModel.setIdOperIns(idOper);
			auditModel.setOperIns(operator);
		} else {
			auditModel.setDtUltMod(Date.valueOf(today));
			auditModel.setIdOperUltMod(idOper);
			auditModel.setOperUltMod(operator);
		}
		return isNew;
	}
	
	public static boolean fillAudit(AuditModel auditModel) {
		UserWithAdditionalInfo user = (UserWithAdditionalInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return fillAudit(auditModel, user.getUsername(), String.format("%s %s", user.getNome(), user.getCognome()));
	}
	
	/**
	 * Clears audit data in an AuditWithValidityModel object
	 * 
	 * @param auditModel
	 */
	public static void clearAudit(AuditModel auditModel) {
		auditModel.setDtIns(null);
		auditModel.setIdOperIns(null);
		auditModel.setOperIns(null);
		auditModel.setDtUltMod(null);
		auditModel.setIdOperUltMod(null);
		auditModel.setOperUltMod(null);
	}
	
	public static void copyAudit(AuditModel from, AuditModel to) {
		to.setDtIns(from.getDtIns());
		to.setIdOperIns(from.getIdOperIns());
		to.setOperIns(from.getIdOperIns());
		to.setDtUltMod(from.getDtUltMod());
		to.setIdOperUltMod(from.getIdOperUltMod());
		to.setOperUltMod(from.getIdOperUltMod());
	}
}
