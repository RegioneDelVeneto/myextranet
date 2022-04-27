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
import { environment } from 'src/environments/environment';
import { Models } from '../models/model';

@Injectable({
  providedIn: 'root'
})
export class EnteService {


  constructor(
    private http: HttpClient
  ) { }

  getAutocomplete(maxres: number = 20, activeOnly: boolean = false, FO: boolean = true): Promise<Models.enteDTO[]> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.enteDTO[]>(`/${FOstring}ente/autocomplete?maxLength=${maxres}&activeOnly=${activeOnly}`).toPromise();
  }

  getAutocompleteAll(maxres: number = 20, FO: boolean = true): Promise<Models.enteDTO[]> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.enteDTO[]>(`/${FOstring}ente/autocomplete?maxLength=${maxres}`).toPromise();
  }

  getAutocompleteAsync(maxres: number = 20, denominazione: string, FO: boolean = true): Observable<Models.enteDTO[]> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.enteDTO[]>(`/${FOstring}ente/autocomplete?maxLength=${maxres}&denominazione=${denominazione}&activeOnly=true`);
  }

  getEnteById(idEnte: number, FO: boolean = true): Observable<Models.enteDTO> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.enteDTO>(`/${FOstring}ente/${idEnte}`);
  }

  public downloadLogo(path: string, isFo: boolean = true) {
    const foString = isFo ? '/frontoffice' : '';
    if (!path) {
      return null;
    }
    else {
      return `${foString}${path}`;
    }
  }

  public getCategoriaEnteList(isFo: boolean = true): Promise<Models.CategoriaEnteDTO[]> {
    const FOstring: string = isFo ? 'frontoffice/' : '';
    return this.http.get<Models.CategoriaEnteDTO[]>(`/${FOstring}categoria-ente`).toPromise();
  }

  insertEnte(enteDTO: Models.enteDTO, fileLogo: File, FO: boolean = true): Promise<Models.enteDTO> {
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/form-data');
    const dataToSend = this.jsonToFormData({ newEnte: JSON.stringify(enteDTO), logoFile: fileLogo });

    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.post<Models.enteDTO>(`/${FOstring}ente`, dataToSend, { headers }).toPromise();
  }

  patchEnte(enteDTO: Models.enteDTO, fileLogo: File, FO: boolean = true): Promise<Models.enteDTO> {

    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/form-data');
    const dataToSend = this.jsonToFormData({ newEnte: JSON.stringify(enteDTO), logoFile: fileLogo });


    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.patch<Models.enteDTO>(`/${FOstring}ente`, dataToSend, { headers }).toPromise();
  }

  putEnte(enteDTO: Models.enteDTO, fileLogo: File, FO: boolean = true): Promise<Models.enteDTO> {

    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/form-data');
    const dataToSend = this.jsonToFormData({ newEnte: JSON.stringify(enteDTO), logoFile: fileLogo });


    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.put<Models.enteDTO>(`/${FOstring}ente`, dataToSend, { headers }).toPromise();
  }


  public getEntiRappresentati(pageIndex: number = 1, pageSize: number = 9, isFo: boolean = true): Promise<Models.PagedContent> {
    let parameters: HttpParams = new HttpParams();
    parameters = parameters.append('_page', pageIndex.toString());
    parameters = parameters.append('_pageSize', pageSize.toString());
    const FOstring: string = isFo ? 'frontoffice/' : '';
    return this.http.get<Models.PagedContent>(`/${FOstring}rappresentante-ente-rap/enti-rappresentati`, {
      params: parameters
    }).toPromise();
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
