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

import javax.validation.constraints.NotNull;

import org.springframework.util.StringUtils;

import it.regioneveneto.myp3.myextranet.model.RichiestaProdotto;
import it.regioneveneto.myp3.myextranet.model.RuoloProdotto;
import it.regioneveneto.myp3.myextranet.model.Utente;
import it.regioneveneto.myp3.myextranet.model.UtenteProdottoAttivato;
import it.regioneveneto.myp3.myextranet.model.UtenteRichiestaProdotto;
import it.regioneveneto.myp3.myextranet.utils.AuditUtils;
import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class UtenteRichiestaProdottoDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	
	protected Integer idUtenteRich;
	@NotNull(message = "RichiestaProdotto è obbligatorio")
	protected RichiestaProdottoDTO richiestaProdotto;

	protected UtenteProdottoAttivatoDTO utenteProdottoAttivato;
	
	protected String richOper;
	@NotNull(message = "RuoloProdotto è obbligatorio")
	protected RuoloProdottoDTO ruoloProdotto;
	
	protected UtenteDTO utente;
	protected String codFiscale;
	protected String nome;
	protected String cognome;
	protected String email;
	protected String telefono;
	
	public Integer getIdUtenteRich() {
		return idUtenteRich;
	}

	public void setIdUtenteRich(Integer idUtenteRich) {
		this.idUtenteRich = idUtenteRich;
	}

	public RichiestaProdottoDTO getRichiestaProdotto() {
		return richiestaProdotto;
	}

	public void setRichiestaProdotto(RichiestaProdottoDTO richiestaProdotto) {
		this.richiestaProdotto = richiestaProdotto;
	}

	public UtenteProdottoAttivatoDTO getUtenteProdottoAttivato() {
		return utenteProdottoAttivato;
	}

	public void setUtenteProdottoAttivato(UtenteProdottoAttivatoDTO utenteProdottoAttivato) {
		this.utenteProdottoAttivato = utenteProdottoAttivato;
	}

	public String getRichOper() {
		return richOper;
	}

	public void setRichOper(String richOper) {
		this.richOper = richOper;
	}

	public RuoloProdottoDTO getRuoloProdotto() {
		return ruoloProdotto;
	}

	public void setRuoloProdotto(RuoloProdottoDTO ruoloProdotto) {
		this.ruoloProdotto = ruoloProdotto;
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
		return "UtenteRichiestaProdottoDTO [idUtenteRich=" + idUtenteRich + ", richiestaProdotto=" + richiestaProdotto
				+ ", utenteProdottoAttivato=" + utenteProdottoAttivato + ", richOper=" + richOper + ", ruoloProdotto="
				+ ruoloProdotto + ", utente=" + utente + ", codFiscale=" + codFiscale + ", nome=" + nome + ", cognome="
				+ cognome + ", email=" + email + ", telefono=" + telefono + "]";
	}

	public static UtenteRichiestaProdotto patchUtenteRichiestaProdotto(UtenteRichiestaProdottoDTO patchUtenteRichiestaProdotto, UtenteRichiestaProdotto originalUtenteRichiestaProdotto, boolean ignoreNulls, boolean cloneOriginal) {
		UtenteRichiestaProdotto utenteRichiestaProdottoResult;
		
		if (cloneOriginal) {
			originalUtenteRichiestaProdotto = ObjectMapperUtils.map(originalUtenteRichiestaProdotto, new UtenteRichiestaProdotto());
		}
		
		if(ignoreNulls) {
			utenteRichiestaProdottoResult = originalUtenteRichiestaProdotto;
			
			// richiestaProdotto
			if (patchUtenteRichiestaProdotto.getRichiestaProdotto() != null) {
				RichiestaProdotto richiestaProdotto = new RichiestaProdotto();
				richiestaProdotto.setIdProdAttivRich(patchUtenteRichiestaProdotto.getRichiestaProdotto().getIdProdAttivRich());
				originalUtenteRichiestaProdotto.setRichiestaProdotto(richiestaProdotto);
			}
			
			// utenteProdottoAttivato
			if (patchUtenteRichiestaProdotto.getUtenteProdottoAttivato() != null) {
				UtenteProdottoAttivato utenteProdottoAttivato = new UtenteProdottoAttivato();
				utenteProdottoAttivato.setIdUtenteProd(patchUtenteRichiestaProdotto.getUtenteProdottoAttivato().getIdUtenteProd());
				originalUtenteRichiestaProdotto.setUtenteProdottoAttivato(utenteProdottoAttivato);
			}
			
			// richOper
			if(StringUtils.hasText(patchUtenteRichiestaProdotto.getRichOper())) {
				originalUtenteRichiestaProdotto.setRichOper(patchUtenteRichiestaProdotto.getRichOper());
			}
			
			// ruoloProdotto
			if (patchUtenteRichiestaProdotto.getRuoloProdotto() != null) {
				RuoloProdotto ruoloProdotto = new RuoloProdotto();
				ruoloProdotto.setCodRuolo(patchUtenteRichiestaProdotto.getRuoloProdotto().getCodRuolo());
				originalUtenteRichiestaProdotto.setRuoloProdotto(ruoloProdotto);
			}
			
			// utente
			if (patchUtenteRichiestaProdotto.getUtente() != null) {
				Utente utente = new Utente();
				utente.setIdUtente(patchUtenteRichiestaProdotto.getUtente().getIdUtente());
				originalUtenteRichiestaProdotto.setUtente(utente);
			}
			
			// codFiscale
			if(StringUtils.hasText(patchUtenteRichiestaProdotto.getCodFiscale())) {
				originalUtenteRichiestaProdotto.setCodFiscale(patchUtenteRichiestaProdotto.getCodFiscale());
			}
			// nome
			if(StringUtils.hasText(patchUtenteRichiestaProdotto.getNome())) {
				originalUtenteRichiestaProdotto.setNome(patchUtenteRichiestaProdotto.getNome());
			}
			// cognome
			if(StringUtils.hasText(patchUtenteRichiestaProdotto.getCognome())) {
				originalUtenteRichiestaProdotto.setCognome(patchUtenteRichiestaProdotto.getCognome());
			}
			// email
			if(StringUtils.hasText(patchUtenteRichiestaProdotto.getEmail())) {
				originalUtenteRichiestaProdotto.setEmail(patchUtenteRichiestaProdotto.getEmail());
			}
			// telefono
			if(StringUtils.hasText(patchUtenteRichiestaProdotto.getTelefono())) {
				originalUtenteRichiestaProdotto.setTelefono(patchUtenteRichiestaProdotto.getTelefono());
			}

				
		} else {
			utenteRichiestaProdottoResult = ObjectMapperUtils.map(patchUtenteRichiestaProdotto, UtenteRichiestaProdotto.class);
			AuditUtils.copyAudit(originalUtenteRichiestaProdotto, utenteRichiestaProdottoResult);
		}
		
		return utenteRichiestaProdottoResult;
	}

	
}
