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
package it.regioneveneto.myp3.myextranet.bean;

import java.io.Serializable;

import it.regioneveneto.myp3.myextranet.utils.Constants;

public class ApiResult implements Serializable {

	private String status;
	private String message;
	private Object data;
	private String code;

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return "ApiResult [status=" + status + ", message=" + message + ", data=" + data + ", code=" + code + "]";
	}

	public ApiResult(String status, String message, Object data) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
		this.code = Constants.RESULT_KO.equals(status) ? Constants.API_ERROR_GENERIC_CODE : Constants.API_SUCCESS_CODE;
	}
	
	public ApiResult(String status, String message, Object data, String code) {
		super();
		this.status = status;
		this.message = message;
		this.data = data;
		this.code = code;
	}

}
