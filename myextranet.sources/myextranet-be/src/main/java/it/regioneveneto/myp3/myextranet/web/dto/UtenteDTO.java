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

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class UtenteDTO implements Serializable {
	private Integer idUtente;
	private String nome;
	private String cognome;
	private String codFiscale;
	private String email;
	private EnteDTO ente;
	private String azienda;
	private String partitaIva;
	private UtenteDTO utentePadre;
	private String telefono;
	private String telefonoUff;
	private Integer flgArchived;

    //Getter and setter

	public Integer getIdUtente() {
		return idUtente;
	}

	public void setIdUtente(Integer idUtente) {
		this.idUtente = idUtente;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public String getCodFiscale() {
		return codFiscale;
	}

	public void setCodFiscale(String codFiscale) {
		this.codFiscale = codFiscale;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public EnteDTO getEnte() {
		return ente;
	}

	public void setEnte(EnteDTO ente) {
		this.ente = ente;
	}

	public String getAzienda() {
		return azienda;
	}

	public void setAzienda(String azienda) {
		this.azienda = azienda;
	}

	public String getPartitaIva() { return partitaIva; }

	public void setPartitaIva(String partitaIva) { this.partitaIva = partitaIva; }

	public UtenteDTO getUtentePadre() {
		return utentePadre;
	}

	public void setUtentePadre(UtenteDTO utentePadre) {
		this.utentePadre = utentePadre;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Integer getFlgArchived() {
		return flgArchived;
	}

	public void setFlgArchived(Integer flgArchived) {
		this.flgArchived = flgArchived;
	}

	public String getTelefonoUff() {
		return telefonoUff;
	}

	public void setTelefonoUff(String telefonoUff) {
		this.telefonoUff = telefonoUff;
	}

	@Override
	public String toString() {
		return "UtenteDTO [idUtente=" + idUtente + ", nome=" + nome + ", cognome=" + cognome + ", codFiscale="
				+ codFiscale + ", email=" + email + ", ente=" + ente + ", azienda=" + azienda + ", utentePadre="
				+ utentePadre + ", telefono=" + telefono + ", telefonoUff=" + telefonoUff + ", flgArchived="
				+ flgArchived + "]";
	}
	
	public static Utente patchUtente(UtenteDTO patchUtente, Utente originalUtente, boolean ignoreNulls, boolean cloneOriginal) {
		
		Utente utenteResult;
		
		if (cloneOriginal) {
			originalUtente = ObjectMapperUtils.map(originalUtente, new Utente());
		}
		
		if(ignoreNulls) {
			utenteResult = originalUtente;
			
			// nome
			if(StringUtils.hasText(patchUtente.getNome())) {
				originalUtente.setNome(patchUtente.getNome());
			}else{
				originalUtente.setNome(null);
			}
			// cognome
			if(StringUtils.hasText(patchUtente.getCognome())) {
				originalUtente.setCognome(patchUtente.getCognome());
			}else{
				originalUtente.setCognome(null);
			}
			// codFiscale
			if(StringUtils.hasText(patchUtente.getCodFiscale())) {
				originalUtente.setCodFiscale(patchUtente.getCodFiscale());
			}else{
				originalUtente.setCodFiscale(null);
			}
			// email
			if(StringUtils.hasText(patchUtente.getEmail())) {
				originalUtente.setEmail(patchUtente.getEmail());
			}else{
				originalUtente.setEmail(null);
			}
			// azienda
			if(StringUtils.hasText(patchUtente.getAzienda())) {
				originalUtente.setAzienda(patchUtente.getAzienda());
			}else{
				originalUtente.setAzienda(null);
			}
			// partitaIva
			if(StringUtils.hasText(patchUtente.getPartitaIva())) {
				originalUtente.setPartitaIva(patchUtente.getPartitaIva());
			}else{
				originalUtente.setPartitaIva(null);
			}
			// telefono
			if(StringUtils.hasText(patchUtente.getTelefono())) {
				originalUtente.setTelefono(patchUtente.getTelefono());
			}else{
				originalUtente.setTelefono(null);
			}
			// telefonoUff
			if(StringUtils.hasText(patchUtente.getTelefonoUff())) {
				originalUtente.setTelefonoUff(patchUtente.getTelefonoUff());
			}else{
				originalUtente.setTelefonoUff(null);
			}
			// ente
			if (patchUtente.getEnte() != null) {
				Ente e = ObjectMapperUtils.map(patchUtente.getEnte(), Ente.class);
				originalUtente.setEnte(e);
			}else{
				originalUtente.setEnte(null);
			}
			
		} else {
			utenteResult = ObjectMapperUtils.map(patchUtente, Utente.class);
			AuditUtils.copyAudit(originalUtente, utenteResult);
		}
		
		return utenteResult;
	}


}
