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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.Ente;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivabile;
import it.regioneveneto.myp3.myextranet.model.ProdottoAttivato;
import it.regioneveneto.myp3.myextranet.model.RuoloProdotto;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.model.UtenteProdottoAttivato;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class UtenteProdottoAttivatoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	protected Integer idUtenteProd;
	@NotNull(message = "ProdottoAttivato è obbligatorio")
	protected ProdottoAttivatoDTO prodottoAttivato;
	@NotNull(message = "Ruolo è obbligatorio")
	protected RuoloProdottoDTO ruolo;
	@NotNull(message = "FlgEnabled è obbligatorio")
	protected Integer flgEnabled;
	protected UtenteDTO utente;
	@NotBlank(message = "CodFiscale è obbligatorio")
	protected String codFiscale;
	protected String nome;
	protected String cognome;
	protected String email;
	protected String telefono;
	
	public Integer getIdUtenteProd() {
		return idUtenteProd;
	}

	public void setIdUtenteProd(Integer idUtenteProd) {
		this.idUtenteProd = idUtenteProd;
	}

	public ProdottoAttivatoDTO getProdottoAttivato() {
		return prodottoAttivato;
	}

	public void setProdottoAttivato(ProdottoAttivatoDTO prodottoAttivato) {
		this.prodottoAttivato = prodottoAttivato;
	}

	public RuoloProdottoDTO getRuolo() {
		return ruolo;
	}

	public void setRuolo(RuoloProdottoDTO ruolo) {
		this.ruolo = ruolo;
	}

	public Integer getFlgEnabled() {
		return flgEnabled;
	}

	public void setFlgEnabled(Integer flgEnabled) {
		this.flgEnabled = flgEnabled;
	}

	public UtenteDTO getUtente() {
		return utente;
	}

	public void setUtente(UtenteDTO utente) {
		this.utente = utente;
	}

	public String getCodFiscale() {
		return codFiscale;
	}

	public void setCodFiscale(String codFiscale) {
		this.codFiscale = codFiscale;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Override
	public String toString() {
		return "UtenteProdottoAttivatoDTO [idUtenteProd=" + idUtenteProd + ", prodottoAttivato=" + prodottoAttivato
				+ ", ruolo=" + ruolo + ", flgEnabled=" + flgEnabled + ", utente=" + utente + ", codFiscale="
				+ codFiscale + ", nome=" + nome + ", cognome=" + cognome + ", email=" + email + ", telefono=" + telefono
				+ "]";
	}

	public static UtenteProdottoAttivato patchUtenteProdottoAttivato(UtenteProdottoAttivatoDTO patchUtenteProdottoAttivato, UtenteProdottoAttivato originalUtenteProdottoAttivato, boolean ignoreNulls, boolean cloneOriginal) {
		UtenteProdottoAttivato utenteProdottoAttivatoResult;
		
		if (cloneOriginal) {
			originalUtenteProdottoAttivato = ObjectMapperUtils.map(originalUtenteProdottoAttivato, new UtenteProdottoAttivato());
		}
		
		if(ignoreNulls) {
			utenteProdottoAttivatoResult = originalUtenteProdottoAttivato;
			
			// codFiscale
			if(StringUtils.hasText(patchUtenteProdottoAttivato.getCodFiscale())) {
				originalUtenteProdottoAttivato.setCodFiscale(patchUtenteProdottoAttivato.getCodFiscale());
			}
			// nome
			if(StringUtils.hasText(patchUtenteProdottoAttivato.getNome())) {
				originalUtenteProdottoAttivato.setNome(patchUtenteProdottoAttivato.getNome());
			}
			// cognome
			if(StringUtils.hasText(patchUtenteProdottoAttivato.getCognome())) {
				originalUtenteProdottoAttivato.setCognome(patchUtenteProdottoAttivato.getCognome());
			}
			// email
			if(StringUtils.hasText(patchUtenteProdottoAttivato.getEmail())) {
				originalUtenteProdottoAttivato.setEmail(patchUtenteProdottoAttivato.getEmail());
			}
			// telefono
			if(StringUtils.hasText(patchUtenteProdottoAttivato.getTelefono())) {
				originalUtenteProdottoAttivato.setTelefono(patchUtenteProdottoAttivato.getTelefono());
			}
			// flgEnabled
			if (patchUtenteProdottoAttivato.getFlgEnabled() != null) {
				originalUtenteProdottoAttivato.setFlgEnabled(patchUtenteProdottoAttivato.getFlgEnabled());
			}
			
			// prodottoAttivato
			if (patchUtenteProdottoAttivato.getProdottoAttivato() != null) {
				ProdottoAttivato prodottoAttivato = new ProdottoAttivato();
				prodottoAttivato.setIdAttivazione(patchUtenteProdottoAttivato.getProdottoAttivato().getIdAttivazione());
				originalUtenteProdottoAttivato.setProdottoAttivato(prodottoAttivato);
			}
			// ruolo
			if (patchUtenteProdottoAttivato.getRuolo() != null) {
				RuoloProdotto ruoloProdotto = new RuoloProdotto();
				ruoloProdotto.setCodRuolo(patchUtenteProdottoAttivato.getRuolo().getCodRuolo());
				originalUtenteProdottoAttivato.setRuolo(ruoloProdotto);
			}
			// utente
			if (patchUtenteProdottoAttivato.getUtente() != null) {
				Utente utente = new Utente();
				utente.setIdUtente(patchUtenteProdottoAttivato.getUtente().getIdUtente());
				originalUtenteProdottoAttivato.setUtente(utente);
			}

				
		} else {
			utenteProdottoAttivatoResult = ObjectMapperUtils.map(patchUtenteProdottoAttivato, UtenteProdottoAttivato.class);
			AuditUtils.copyAudit(originalUtenteProdottoAttivato, utenteProdottoAttivatoResult);
		}
		
		return utenteProdottoAttivatoResult;
	}

	
}
