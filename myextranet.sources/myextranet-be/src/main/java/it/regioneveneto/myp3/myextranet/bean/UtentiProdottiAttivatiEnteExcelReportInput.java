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

import java.util.List;

public class UtentiProdottiAttivatiEnteExcelReportInput {
	
	private String ente;
	private Utente rappresentante;
	private List<ProdottoUtenti> prodotti;

	public String getEnte() {
		return ente;
	}

	public void setEnte(String ente) {
		this.ente = ente;
	}

	public Utente getRappresentante() {
		return rappresentante;
	}

	public void setRappresentante(Utente rappresentante) {
		this.rappresentante = rappresentante;
	}

	public List<ProdottoUtenti> getProdotti() {
		return prodotti;
	}

	public void setProdotti(List<ProdottoUtenti> prodotti) {
		this.prodotti = prodotti;
	}

	public UtentiProdottiAttivatiEnteExcelReportInput() {
		
	}
	
	public UtentiProdottiAttivatiEnteExcelReportInput(String ente, Utente rappresentante, List<ProdottoUtenti> prodotti) {
		super();
		this.ente = ente;
		this.rappresentante = rappresentante;
		this.prodotti = prodotti;
	}

	public static class ProdottoUtenti {
		private String descrizioneProdotto;
		List<Utente> utenti;

		public String getDescrizioneProdotto() {
			return descrizioneProdotto;
		}

		public void setDescrizioneProdotto(String descrizioneProdotto) {
			this.descrizioneProdotto = descrizioneProdotto;
		}

		public List<Utente> getUtenti() {
			return utenti;
		}

		public void setUtenti(List<Utente> utenti) {
			this.utenti = utenti;
		}

		public ProdottoUtenti() {
			super();
		}

	}
	
	public static class Utente {
		private String nome;
		private String cognome;
		private String codFiscale;
		private String telefono;
		private String email;
		private String stato;
		private String ruolo;

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

		public String getTelefono() {
			return telefono;
		}

		public void setTelefono(String telefono) {
			this.telefono = telefono;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getStato() {
			return stato;
		}

		public void setStato(String stato) {
			this.stato = stato;
		}

		public String getRuolo() {
			return ruolo;
		}

		public void setRuolo(String ruolo) {
			this.ruolo = ruolo;
		}

		public Utente() {
			super();
		}

	}
}
