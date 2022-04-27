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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MyExtranetContent {

	private String titolo;
	private String descrizione;
	private String data;
	private String prodotto;
	private String progetto;
	private String piattaformaAbilitante;
	private String id;
	private String tipo;
	private boolean inPresenza;
	private boolean inStreaming;
	private Integer numeroMaxPartecipanti;
	private String luogo;
	private String parent;
	private String aula;
	private String sottotitolo;
	private String dateDa;
	private String dateA;
	private String urlQuestionario;
	private String urlQuestionarioIn;
	private String slug;
	private String email;
	private String telefono;
	
	private String streamingLink;
	private String htmlStreamingNotes;

	private Integer postiDisponibili;
	
	private String linkStreamingRelatore;
	private String indicazioniStreamingRelatore;

	private boolean annullato;	
	
	private long pendingCounter;
	
	public String getPiattaformaAbilitante() {
		return piattaformaAbilitante;
	}

	public void setPiattaformaAbilitante(String piattaformaAbilitante) {
		this.piattaformaAbilitante = piattaformaAbilitante;
	}

	public String getLinkStreamingRelatore() {
		return linkStreamingRelatore;
	}

	public void setLinkStreamingRelatore(String linkStreamingRelatore) {
		this.linkStreamingRelatore = linkStreamingRelatore;
	}

	public String getIndicazioniStreamingRelatore() {
		return indicazioniStreamingRelatore;
	}

	public void setIndicazioniStreamingRelatore(String indicazioniStreamingRelatore) {
		this.indicazioniStreamingRelatore = indicazioniStreamingRelatore;
	}

	public String getStreamingLink() {
		return streamingLink;
	}

	public void setStreamingLink(String streamingLink) {
		this.streamingLink = streamingLink;
	}

	public String getHtmlStreamingNotes() {
		return htmlStreamingNotes;
	}

	public void setHtmlStreamingNotes(String htmlStreamingNotes) {
		this.htmlStreamingNotes = htmlStreamingNotes;
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


	/**
	 * 
	 */
	private String logo;
	
	
	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}
	
	public String getAula() {
		return aula;
	}
	public void setAula(String aula) {
		this.aula = aula;
	}
	public String getSottotitolo() {
		return sottotitolo;
	}
	public void setSottotitolo(String sottotitolo) {
		this.sottotitolo = sottotitolo;
	}
	public String getDateDa() {
		return dateDa;
	}
	public void setDateDa(String dateDa) {
		this.dateDa = dateDa;
	}
	public String getDateA() {
		return dateA;
	}
	public void setDateA(String dateA) {
		this.dateA = dateA;
	}
	
	public String getTitolo() {
		return titolo;
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getProdotto() {
		return prodotto;
	}
	public void setProdotto(String prodotto) {
		this.prodotto = prodotto;
	}
	public String getProgetto() {
		return progetto;
	}
	public void setProgetto(String progetto) {
		this.progetto = progetto;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public String getLuogo() {
		return luogo;
	}
	public void setLuogo(String luogo) {
		this.luogo = luogo;
	}
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public String getUrlQuestionario() {
		return urlQuestionario;
	}
	
	public void setUrlQuestionario(String urlQuestionario) {
		this.urlQuestionario = urlQuestionario;
	}
	public String getUrlQuestionarioIn() {
		return urlQuestionarioIn;
	}
	
	public void setUrlQuestionarioIn(String urlQuestionarioIn) {
		this.urlQuestionarioIn = urlQuestionarioIn;
	}

	public Integer getNumeroMaxPartecipanti() {
		return numeroMaxPartecipanti;
	}

	public void setNumeroMaxPartecipanti(Integer numeroMaxPartecipanti) {
		this.numeroMaxPartecipanti = numeroMaxPartecipanti;
	}
	
	public String getSlug() {
		return slug;
	}

	public void setSlug(String slug) {
		this.slug = slug;
	}
	
	public Integer getPostiDisponibili() {
		return postiDisponibili;
	}

	public void setPostiDisponibili(Integer postiDisponibili) {
		this.postiDisponibili = postiDisponibili;
	}

	public boolean isInPresenza() {
		return inPresenza;
	}

	public void setInPresenza(boolean inPresenza) {
		this.inPresenza = inPresenza;
	}

	public boolean isInStreaming() {
		return inStreaming;
	}

	public void setInStreaming(boolean inStreaming) {
		this.inStreaming = inStreaming;
	}

	public boolean isAnnullato() {
		return annullato;
	}

	public void setAnnullato(boolean annullato) {
		this.annullato = annullato;
	}

	public long getPendingCounter() {
		return pendingCounter;
	}

	public void setPendingCounter(long pendingCounter) {
		this.pendingCounter = pendingCounter;
	}

	public String setDateFromStartAndEnd(String start, String end) throws ParseException {
		
		Date javaDateStart= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse( start);
		Date javaDateEnd = null;
		if(end!= null && !end.isBlank()) javaDateEnd= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(end);
		
		String endAsString = null;
		String startAsString = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(javaDateStart);
		if(end!= null && !end.isBlank()) endAsString = new SimpleDateFormat("dd-MM-yyyy HH:mm").format(javaDateEnd);
		
		return startAsString;
		

	}
	
	public MyExtranetContent() {
		
	}
	
	public MyExtranetContent(Map<String, Object> attributes, String id, String type, String parent, String slug) throws ParseException {
		this.titolo = (String) attributes.get("sys_title");
		this.descrizione =attributes.get("sys_description") != null && ((String) attributes.get("sys_description")).isBlank() != true ? (String) attributes.get("sys_description") : null;
		this.id = id;
		this.tipo = type;
		this.parent = parent;
		this.slug = slug;
		this.logo = attributes.get("sys_uri_immagine") != null ? (String) attributes.get("sys_uri_immagine") : null;
		this.sottotitolo = (String) attributes.get("sys_sottotitolo");
		
		switch(type) {
		case ("CT_myextranet_d_myext_products") : {
			this.piattaformaAbilitante = attributes.get("myext_platform_association") instanceof String? (String) attributes.get("myext_platform_association") : this.arrayListTostring((ArrayList<String>)attributes.get("myext_platform_association"));
			
			break;
		}
		case ("CT_myextranet_d_myext_services") : {
			this.prodotto = attributes.get("myext_product_association") instanceof String? (String) attributes.get("myext_product_association") : this.arrayListTostring((ArrayList<String>)attributes.get("myext_product_association"));
			
			break;
		}
		case ("CT_myextranet_d_myext_projects") : {
			this.prodotto = attributes.get("myext_product_association") instanceof String? (String) attributes.get("myext_product_association") : this.arrayListTostring((ArrayList<String>)attributes.get("myext_product_association"));
			this.telefono = attributes.get("myext_telephone") != null ? (String) attributes.get("myext_telephone") : null;
			this.email = attributes.get("myext_email") != null ? (String) attributes.get("myext_email") : null;

			break;
		}
		case ("CT_myextranet_d_myext_events") : {
			Map myextOutputSummary = (Map) attributes.get("myext_output_summary");
			this.urlQuestionario = myextOutputSummary != null ? (String) myextOutputSummary.get("value") : "";
			if (this.urlQuestionario == null) {
				this.urlQuestionario = "";
			}
			//myext_input_summary

			Map myextInputSummary = (Map) attributes.get("myext_input_summary");
			this.urlQuestionarioIn = myextInputSummary != null ? (String) myextInputSummary.get("value") : "";
			if (this.urlQuestionarioIn == null) {
				this.urlQuestionarioIn = "";
			}

			Map myextStreamingLink = (Map) attributes.get("myext_streaming_link");
			this.streamingLink = myextStreamingLink != null ? (String) myextStreamingLink.get("value") : "";
			if (this.streamingLink == null) {
				this.streamingLink = "";
			}
			Map myextranetRelatoreStreamingLink = (Map) attributes.get("myext_realatore_streaming_link");
			this.linkStreamingRelatore = myextranetRelatoreStreamingLink != null ? (String) myextStreamingLink.get("value") : "";
			if (this.linkStreamingRelatore == null) {
				this.linkStreamingRelatore = "";
			}
			
			this.data = attributes.get("sys_start_date") != null && ((String) attributes.get("sys_start_date")).isBlank() != true ? this.setDateFromStartAndEnd((String) attributes.get("sys_start_date"), (String) attributes.get("sys_end_date")) : null;
			this.dateA = attributes.get("sys_end_date") != null && ((String) attributes.get("sys_end_date")).isBlank() != true ? (String) attributes.get("sys_end_date") : null;
			this.dateDa = attributes.get("sys_start_date") != null && ((String) attributes.get("sys_start_date")).isBlank() != true ? (String) attributes.get("sys_start_date") : null;
			this.prodotto = attributes.get("myext_product_association") instanceof String? (String) attributes.get("myext_product_association") : this.arrayListTostring((ArrayList<String>)attributes.get("myext_product_association"));
			this.progetto = attributes.get("myext_project_association") instanceof String? (String) attributes.get("myext_project_association") : this.arrayListTostring((ArrayList<String>)attributes.get("myext_project_association"));
			this.aula = attributes.get("myext_classroom") != null && ((String) attributes.get("myext_classroom")).isBlank() != true ?  (String) attributes.get("myext_classroom") : null;
			this.inPresenza = attributes.get("myext_presence_boolean") != null && (boolean) attributes.get("myext_presence_boolean")? true : false;
			this.inStreaming = attributes.get("myext_streaming_boolean") != null && (boolean) attributes.get("myext_streaming_boolean")? true : false;
			this.numeroMaxPartecipanti = attributes.get("myext_max_participants") != null ? (Integer) attributes.get("myext_max_participants") : -1;
			this.annullato =  attributes.get("myext_cancel_event_boolean") != null && (boolean) attributes.get("myext_cancel_event_boolean")? true : false;
			this.email = attributes.get("myext_email") != null ? (String) attributes.get("myext_email") : null;
			this.telefono = attributes.get("myext_telephone") != null ? (String) attributes.get("myext_telephone") : null;
			this.luogo = attributes.get("sys_luogo") != null ?  (String) attributes.get("sys_luogo") : null;
			this.htmlStreamingNotes = attributes.get("myext_straming_notes") != null && ((String) attributes.get("myext_straming_notes")).isBlank() != true ? (String) attributes.get("myext_straming_notes") : null;
			this.indicazioniStreamingRelatore = attributes.get("myext_realatore_straming_notes") != null && ((String) attributes.get("myext_realatore_straming_notes")).isBlank() != true ? (String) attributes.get("myext_realatore_straming_notes") : null;
			break;
		}
		default :{
			
		}
		}


	}
	
	private String arrayListTostring(ArrayList<String> aList) {
		if(aList == null) return null;

		String arrayString = String.join(";", aList);
		return arrayString;
	}
	
	
	public static ArrayList<MyExtranetContent> convertMyPortalToMyExtranet( List<MyPortalContent> mypContent) {
		ArrayList<MyExtranetContent> myextList = new ArrayList<MyExtranetContent>();
		for(int i = 0; i< mypContent.size(); i++) {
			MyPortalContent entity = mypContent.get(i);
			try {
				myextList.add(i, new MyExtranetContent(entity.getAttributes(), entity.getId(), entity.getType(), entity.getParent(), entity.getSlug()));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return myextList;
		
	}
	
	public static MyExtranetContent convertMyPortalToMyExtranet( MyPortalContent mypContent) {
			MyExtranetContent myExtContent = new MyExtranetContent();
			MyPortalContent entity = mypContent;
			try {
				myExtContent= new MyExtranetContent(entity.getAttributes(), entity.getId(), entity.getType(), entity.getParent(), entity.getSlug());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		
		return myExtContent;
		
	}

	@Override
	public String toString() {
		return "MyExtranetContent [titolo=" + titolo + ", descrizione=" + descrizione + ", data=" + data + ", prodotto="
				+ prodotto + ", progetto=" + progetto + ", id=" + id + ", tipo=" + tipo + ", inPresenza=" + inPresenza
				+ ", inStreaming=" + inStreaming + ", numeroMaxPartecipanti=" + numeroMaxPartecipanti + ", luogo="
				+ luogo + ", parent=" + parent + ", aula=" + aula + ", sottotitolo=" + sottotitolo + ", dateDa="
				+ dateDa + ", dateA=" + dateA + ", urlQuestionario=" + urlQuestionario + ", slug=" + slug + ", email="
				+ email + ", telefono=" + telefono + ", streamingLink=" + streamingLink + ", htmlStreamingNotes="
				+ htmlStreamingNotes + ", postiDisponibili=" + postiDisponibili + ", linkStreamingRelatore="
				+ linkStreamingRelatore + ", indicazioniStreamingRelatore=" + indicazioniStreamingRelatore
				+ ", annullato=" + annullato + ", logo=" + logo + "]";
	}

	

	
}
