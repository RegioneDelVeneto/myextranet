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
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Models } from '../models/model';

@Injectable({
  providedIn: 'root'
})
export class MyboxService {

  private baseUrl = '';

  constructor(
    private http: HttpClient
  ) { }

  public loadMultipleFiles(files: { file: File | string, uuid: string }[], isFO: boolean = true): Promise<Models.MyBoxMultiResponseModel> {
    const FOstring: string = isFO ? 'frontoffice/' : '';
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/form-data');

    const filesToSend = new FormData();
    files.forEach(data => {
      filesToSend.append(data.uuid, data.file);
    });


    return this.http.post<Models.MyBoxMultiResponseModel>(`/${FOstring}mybox/multi`, filesToSend, { headers }).toPromise();
  }


  public getDownloadLinkFromId(id: string, isFo: boolean = true) {
    const foString = isFo ? '/frontoffice' : '';

    return `${foString}/mybox/${id}`;
  }

}
