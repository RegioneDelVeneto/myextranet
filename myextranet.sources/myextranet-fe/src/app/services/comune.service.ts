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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Models } from '../models/model';

@Injectable({
  providedIn: 'root'
})
export class ComuneService {

  private baseUrl = '';

  constructor(
    private http: HttpClient
  ) { }

  getAutocomplete(maxres: number = 20, FO: boolean = true): Promise<Models.comuneDTO[]> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.comuneDTO[]>(`/${FOstring}comune/autocomplete?maxLength=${maxres}`).toPromise();
  }

  getAutocompleteAsync(maxres: number = 20, desComune: string, FO: boolean = true): Observable<Models.comuneDTO[]> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.comuneDTO[]>(`/${FOstring}comune/autocomplete?maxLength=${maxres}&desComune=${desComune}`);
  }


  getAutocompleteAsyncByCode(maxres: number = 20, codComune: string, FO: boolean = true): Observable<Models.comuneDTO[]> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.comuneDTO[]>(`/${FOstring}comune/autocomplete?maxLength=${maxres}&codComune=${codComune}`);
  }

}
