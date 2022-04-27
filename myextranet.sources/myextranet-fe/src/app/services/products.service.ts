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
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Models } from '../models/model';

@Injectable({
  providedIn: 'root'
})
export class ProductsService {

  private baseUrl = '';

  constructor(
    private http: HttpClient
  ) { }

  downloadProdotti(filter): string {

    let urlStats = '/statistiche/stat-prodotti';
    let httpParams: HttpParams = new HttpParams();

    Object.keys(filter).forEach(key => {
      if (filter[key] !== null && filter[key] !== undefined) {
        if (key === 'withPendingRequests') {
          if (!!filter[key]) {
            httpParams = httpParams.append(key, 'true');
          }
        }
        else if (filter[key].value !== null && filter[key].value !== undefined) {
          httpParams = httpParams.append(key, filter[key].value);
        } else {
          httpParams = httpParams.append(key, filter[key]);
        }


      }
    });
    urlStats = `${urlStats}?${httpParams.toString()}`;

    return urlStats;

  }

  downloadEnti(filter): string {

    let urlStats = '/statistiche/stat-enti-prodotti';
    let httpParams: HttpParams = new HttpParams();

    Object.keys(filter).forEach(key => {
      if (filter[key] !== null && filter[key] !== undefined) {
        if (key === 'withPendingRequests') {
          if (!!filter[key]) {
            httpParams = httpParams.append(key, 'true');
          }
        }
        else if (filter[key].value !== null && filter[key].value !== undefined) {
          httpParams = httpParams.append(key, filter[key].value);
        } else {
          httpParams = httpParams.append(key, filter[key]);
        }


      }
    });
    urlStats = `${urlStats}?${httpParams.toString()}`;

    return urlStats;
  }

  downloadServizi(filter): string {

    let urlStats = '/statistiche/stat-servizi-erogati';
    let httpParams: HttpParams = new HttpParams();

    Object.keys(filter).forEach(key => {
      if (filter[key] !== null && filter[key] !== undefined) {
        if (key === 'withPendingRequests') {
          if (!!filter[key]) {
            httpParams = httpParams.append(key, 'true');
          }
        }
        else if (filter[key].value !== null && filter[key].value !== undefined) {
          httpParams = httpParams.append(key, filter[key].value);
        } else {
          httpParams = httpParams.append(key, filter[key]);
        }


      }
    });
    urlStats = `${urlStats}?${httpParams.toString()}`;

    return urlStats;
  }



  getAutocomplete(maxres: number = 20, FO: boolean = true): Promise<Models.ProdottoAttivabileDTO[]> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.ProdottoAttivabileDTO[]>(`/${FOstring}prodotto-attivabile/autocomplete?maxLength=${maxres}`).toPromise();
  }

  getAutocompleteAsync(maxres: number = 20, nomeProdottoAttiv: string, FO: boolean = true): Observable<Models.ProdottoAttivabileDTO[]> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.ProdottoAttivabileDTO[]>(`/${FOstring}prodotto-attivabile/autocomplete?maxLength=${maxres}&nomeProdottoAttiv=${nomeProdottoAttiv}`);
  }

  insertProdottoAttivabile(prodottoAttivabileDTO: Models.ProdottoAttivabileDTO, FO: boolean = true): Promise<Models.ProdottoAttivabileDTO> {

    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.post<Models.ProdottoAttivabileDTO>(`/${FOstring}prodotto-attivabile`, prodottoAttivabileDTO).toPromise();
  }

  patchProdottoAttivabile(prodottoAttivabileDTO: Models.ProdottoAttivabileDTO, FO: boolean = true): Promise<Models.ProdottoAttivabileDTO> {

    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.patch<Models.ProdottoAttivabileDTO>(`/${FOstring}prodotto-attivabile`, prodottoAttivabileDTO).toPromise();
  }

  putProdottoAttivabile(prodottoAttivabileDTO: Models.ProdottoAttivabileDTO, FO: boolean = true): Promise<Models.ProdottoAttivabileDTO> {

    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.put<Models.ProdottoAttivabileDTO>(`/${FOstring}prodotto-attivabile`, prodottoAttivabileDTO).toPromise();
  }

  getProdottoAttivabileById(idProdotto: string, FO: boolean = true): Observable<Models.ProdottoAttivabileDTO> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.ProdottoAttivabileDTO>(`/${FOstring}prodotto-attivabile/${idProdotto}`);
  }

  deleteProdottoAttivabileById(idProdotto: string, FO: boolean = true): Promise<Models.ProdottoAttivabileDTO> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.delete<Models.ProdottoAttivabileDTO>(`/${FOstring}prodotto-attivabile/${idProdotto}`).toPromise();
  }

  getProdottoAttivatoById(idAttivazione: string, FO: boolean = true): Promise<Models.ProdottoAttivatoDto> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.ProdottoAttivatoDto>(`/${FOstring}prodotto-attivato/${idAttivazione}`).toPromise();
  }

  patchProdottoAttivo(prodottoAttivabileDTOPatch: Models.ProdottoAttivatoDto, FO: boolean = true): Promise<Models.ProdottoAttivatoDto> {

    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.patch<Models.ProdottoAttivatoDto>(`/${FOstring}prodotto-attivato`, prodottoAttivabileDTOPatch).toPromise();
  }

  getUtentiProdottiRuoli(idAttivazione: string, FO: boolean = true): Promise<Models.GruppoUtentiRuoloDTO[]> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.GruppoUtentiRuoloDTO[]>(`/${FOstring}utente-prodotto-attivato/gruppi-utenti-prodotto?idAttivazione=${idAttivazione}`).toPromise();
  }

  postProdottiRuoliAggiornati(data: Models.UtenteRuoliPostNewOperationDto[], FO: boolean = true) {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.post(`/${FOstring}utente-prodotto-attivato/aggiorna-utenti-prodotto`, data).toPromise();
  }

  getRap(id: string, isFo: boolean = true): Promise<Models.RappEnteDTO> {
    const FOstring: string = isFo ? 'frontoffice/' : '';
    return this.http.get<Models.RappEnteDTO>(`/${FOstring}rappresentante-ente-rap/${id}`).toPromise();
  }

  insertRap(RAP: Models.RapInsertModel, isFo: boolean = true) {
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/form-data');
    const FOstring: string = isFo ? 'frontoffice/' : '';
    const formData = new FormData();

    formData.append('newRappresentanteEnte', JSON.stringify(RAP.newRappresentanteEnte));
    return this.http.post<Models.enteDTO>(`/${FOstring}rappresentante-ente-rap`, formData, { headers }).toPromise();
  }

  patchRap(RAP: Models.RapPatchModel, isFo: boolean = true) {

    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/form-data');
    const FOstring: string = isFo ? 'frontoffice/' : '';
    const formData = new FormData();

    formData.append('newRappresentanteEnte', JSON.stringify(RAP.newRappresentanteEnte));
    return this.http.patch<Models.enteDTO>(`/${FOstring}rappresentante-ente-rap`, formData, { headers }).toPromise();
  }


  redoRAP(RAP: Models.RapPatchModel, isFo: boolean = true) {

    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/form-data');
    const FOstring: string = isFo ? 'frontoffice/' : '';
    const formData = new FormData();

    formData.append('newRappresentanteEnte', JSON.stringify(RAP.newRappresentanteEnte));
    return this.http.patch<Models.enteDTO>(`/${FOstring}rappresentante-ente-rap/risottomissione`, formData, { headers }).toPromise();
  }

  editRap(RAP: Models.RapInsertModel, isFo: boolean = true) {
    
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/form-data');
    const FOstring: string = isFo ? 'frontoffice/' : '';

    const dataToSend = this.jsonToFormData(RAP);

    return this.http.patch<Models.enteDTO>(`/${FOstring}rappresentante-ente-rap`, dataToSend, { headers }).toPromise();

  }

  changeRapStatus(rapStatusObj: { idRappr: string, flgConferma: number, numProtocollo: string, dtProtocollo: Date, motivazione: string }, isFo: boolean = true) {
    const FOstring: string = isFo ? 'frontoffice/' : '';
    return this.http.post<Models.enteDTO>(`/${FOstring}rappresentante-ente-rap/conferma-rappresentante`, rapStatusObj).toPromise();
  }

  getRuoliProdotti(isFo: boolean = true): Promise<Models.RuoloDto[]> {
    const FOstring: string = isFo ? 'frontoffice/' : '';
    return this.http.get<Models.RuoloDto[]>(`/${FOstring}ruolo-prodotto/autocomplete?maxLength=9999&_orderBy=numRuolo&_orderDir=asc`).toPromise();
  }


  getRichiestaProdotto(idProdAttivRich: string | number, isFo: boolean = true): Promise<Models.RichiestaProdottoWithStepDto> {
    const FOstring: string = isFo ? 'frontoffice/' : '';
    return this.http.get<Models.RichiestaProdottoWithStepDto>(`/${FOstring}richiesta-prodotto/${idProdAttivRich}`).toPromise();
  }

  public downloadModulo(path: string, isFo: boolean = true) {
    const foString = isFo ? '/frontoffice' : '';
    if (!path) {
      return null;
    }
    else {
      return `${foString}${path}`;
    }
  }

  private jsonToFormData(data): FormData {
    const formData = new FormData();

    this.buildFormData(formData, data);

    return formData;
  }

  private buildFormData(formData, data, parentKey?): void {
    if (data && Array.isArray(data) && !(data instanceof Date) && !(data instanceof File)) {
      Object.keys(data).forEach(key => {
        this.buildFormData(formData, data[key], parentKey ? `${parentKey}[${key}]` : key);
      });
    } else if (data && typeof data === 'object' && !(data instanceof Date) && !(data instanceof File)) {
      Object.keys(data).forEach(key => {
        this.buildFormData(formData, data[key], parentKey ? `${parentKey}.${key}` : key);
      });
    } else {
      const value = data;

      if (value !== null) {
        formData.append(parentKey, value);
      }
    }
  }

}
