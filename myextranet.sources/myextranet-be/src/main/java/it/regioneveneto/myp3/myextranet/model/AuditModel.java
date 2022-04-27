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

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class AuditModel implements Serializable {
	
	@Column(name="id_oper_ins", nullable=false, length=50)
	private String idOperIns;
	
	@Column(name="oper_ins", nullable=false, length=250)
	private String operIns;
	
	@Column(name="dt_ins", nullable=false)
	private Date dtIns;
	
	@Column(name="id_oper_ult_mod", nullable=true, length=50)
	private String idOperUltMod;
	
	@Column(name="oper_ult_mod", nullable=true, length=250)
	private String operUltMod;
	
	@Column(name="dt_ult_mod", nullable=true)
	private Date dtUltMod;

	public String getIdOperIns() {
		return idOperIns;
	}

	public void setIdOperIns(String idOperIns) {
		this.idOperIns = idOperIns;
	}

	public String getOperIns() {
		return operIns;
	}

	public void setOperIns(String operIns) {
		this.operIns = operIns;
	}

	public Date getDtIns() {
		return dtIns;
	}

	public void setDtIns(Date dtIns) {
		this.dtIns = dtIns;
	}

	public String getIdOperUltMod() {
		return idOperUltMod;
	}

	public void setIdOperUltMod(String idOperUltMod) {
		this.idOperUltMod = idOperUltMod;
	}

	public String getOperUltMod() {
		return operUltMod;
	}

	public void setOperUltMod(String operUltMod) {
		this.operUltMod = operUltMod;
	}

	public Date getDtUltMod() {
		return dtUltMod;
	}

	public void setDtUltMod(Date dtUltMod) {
		this.dtUltMod = dtUltMod;
	}
	
	
}
