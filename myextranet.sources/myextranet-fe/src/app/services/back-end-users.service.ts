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
import { environment } from 'src/environments/environment';
import { Models } from '../models/model';

@Injectable({
  providedIn: 'root'
})
export class BackEndUsersService {


  constructor(
    private http: HttpClient
  ) { }

  getUserById(id: number, isFO: boolean = true): Promise<any> {
    const FOstring: string = isFO ? 'frontoffice/' : '';
    return this.http.get(`/${FOstring}utente/${id}`).toPromise();
  }

  saveNewUser(data: Models.utenteDTO, isFO: boolean = true) {
    const FOstring: string = isFO ? 'frontoffice/' : '';
    return this.http.post(`/${FOstring}utente/`, data).toPromise();
  }


  saveEditUser(data: Models.utenteDTO, isFO: boolean = true) {
    const FOstring: string = isFO ? 'frontoffice/' : '';
    return this.http.patch(`/${FOstring}utente/`, data).toPromise();
  }

  getAutocomplete(pSize: number, isFO: boolean = true): Promise<{ idUtente: number, nome: string, cognome: string, codFiscale: string, email: string, telefono: string }[]> {
    const FOstring: string = isFO ? 'frontoffice/' : '';
    return this.http.get<{ idUtente: number, nome: string, cognome: string, codFiscale: string, email: string, telefono: string }[]>(`/${FOstring}utente/autocomplete?maxLength=${pSize}`).toPromise();
  }

  getAutocompleteAsync(pSize: number, cf: string, isFO: boolean = true): Observable<{ idUtente: number, nome: string, cognome: string, codFiscale: string, email: string, telefono: string }[]> {
    const FOstring: string = isFO ? 'frontoffice/' : '';
    return this.http.get<{ idUtente: number, nome: string, cognome: string, codFiscale: string, email: string, telefono: string }[]>(`/${FOstring}utente/autocomplete?maxLength=${pSize}&searchString=${cf.toUpperCase()}`);
  }
}
