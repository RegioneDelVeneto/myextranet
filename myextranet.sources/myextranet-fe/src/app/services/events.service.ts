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
import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Models } from '../models/model';

@Injectable({
  providedIn: 'root'
})
export class EventsService {

  constructor(
    private http: HttpClient
  ) { }

  signUp(data: Models.IscrizioneEventoFO): Promise<any> {
    return this.http.post(`/frontoffice/iscritto-evento`, data).toPromise();
  }

  getIscrittoEvento(eventId: string, idUtente: number): Promise<Models.iscrizioneEventoDTO> {
    let httpParams: HttpParams = new HttpParams();
    httpParams = httpParams.append('idEvento', eventId);
    httpParams = httpParams.append('idUtente', idUtente.toString());
    return this.http.get<Models.iscrizioneEventoDTO>(`/frontoffice/iscritto-evento/iscrizione`, { params: httpParams }).toPromise();
  }

  patchIscrittoEventoFO(data: any): Promise<any> {

    return this.http.patch(`/frontoffice/iscritto-evento`, data).toPromise();
  }

  deleteIscrittoEvento(idIscrizione: number, isFo: boolean = true): Promise<Models.iscrizioneEventoDTO> {
    let FoString: string = '';
    if (isFo) {
      FoString = '/frontoffice';
    }
    return this.http.delete<Models.iscrizioneEventoDTO>(`${FoString}/iscritto-evento/${idIscrizione}`).toPromise();
  }

  getMyEvents(page: number = 0, pageSize: number = 10): Promise<Models.PagedContent> {
    let httpParams: HttpParams = new HttpParams();
    httpParams = httpParams.append('_page', page.toString());
    httpParams = httpParams.append('_pageSize', pageSize.toString());
    return this.http.get<Models.PagedContent>(`/frontoffice/iscritto-evento/eventi`, { params: httpParams }).toPromise();
  }

  downloadStatisticheEventi(filter: { titolo: string, descrizione: string, prodotto: string, progetto: string, dataDa: Date, dataA: Date }): string {
    let urlStats = '/statistiche/stat-eventi';
    let httpParams: HttpParams = new HttpParams();

    Object.keys(filter).forEach(key => {
      if (filter[key] !== null && filter[key] !== undefined) {
        if ((key === 'dataDa' || key === 'dataA') && !!filter[key].valueOf()) {
          httpParams = httpParams.append(key, filter[key].valueOf().toString());
        } else {
          httpParams = httpParams.append(key, filter[key]);
        }


      }
    });
    urlStats = `${urlStats}?${httpParams.toString()}`;

    return urlStats;

  }

  downloadStatistichePartecipantiEvento(filter: { titolo: string, descrizione: string, prodotto: string, progetto: string, dataDa: Date, dataA: Date }): string {
    let urlStats = '/statistiche/stat-partecipanti';
    let httpParams: HttpParams = new HttpParams();

    Object.keys(filter).forEach(key => {
      if (filter[key] !== null && filter[key] !== undefined) {
        if (key === 'dataDa' || key === 'dataA') {
          httpParams = httpParams.append(key, filter[key].valueOf().toString());
        } else {
          httpParams = httpParams.append(key, filter[key]);
        }


      }
    });
    urlStats = `${urlStats}?${httpParams.toString()}`;

    return urlStats;

  }
}
