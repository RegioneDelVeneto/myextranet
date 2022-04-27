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
export class ProjectsService {

  constructor(private http: HttpClient,
  ) { }

  postMassiveOperation(type: string, data: { [key: string]: boolean }): Promise<any> {

    const postObject = {
      tipoOperazione: type,
      targets: []
    };

    Object.keys(data).forEach(key => {
      postObject.targets.push({
        id: parseInt(key, 10),
        intValue: data[key] ? 1 : 0
      });
    });

    return this.http.post(`/collaboratore-progetto/operazione-massiva`, postObject).toPromise();
  }



  postOrPatchCollaboratoreProgetto(coll: Models.IscrizioneCollaboratoreDTO, isEdit: boolean = false, isFo: boolean = true): Promise<any> { /// invito

    const FOstring: string = isFo ? 'frontoffice/' : '';

    if (isEdit) {
      return this.http.patch(`/${FOstring}collaboratore-progetto`, coll).toPromise();
    }
    else {
      return this.http.post(`/${FOstring}collaboratore-progetto`, coll).toPromise();
    }
  }

  getCollaboratoreById(id: number): Promise<any> {
    return this.http.get(`/collaboratore-progetto/${id}`).toPromise();

  }

  getProjectsForUserFO(pageIndex: number = 1, pageSize: number = 9): Promise<Models.PagedContent> {

    let parameters: HttpParams = new HttpParams();
    parameters = parameters.append('_page', pageIndex.toString());
    parameters = parameters.append('_pageSize', pageSize.toString());

    return this.http.get<Models.PagedContent>(`/frontoffice/collaboratore-progetto/progetti`, {
      params: parameters
    }).toPromise();
  }

  public getCollaboratoriProgetto(pageSize: number, pageIndex: number, idProgetto: string, isFo: boolean = true, soloAttivi: boolean = true, validOnly: boolean = false): Promise<Models.PagedContent> {

    const FOstring: string = isFo ? 'frontoffice/' : '';

    let parameters: HttpParams = new HttpParams();
    parameters = parameters.append('_page', pageIndex.toString());
    if (!!pageSize) {
      parameters = parameters.append('_pageSize', pageSize.toString());
    }
    parameters = parameters.append('idProgetto', idProgetto.toString());

    if (soloAttivi) {
      parameters = parameters.append('flgConferma', '1');
    }

    if (validOnly) {
      parameters = parameters.append('validOnly', '1');
    }

    return this.http.get<Models.PagedContent>(`/${FOstring}collaboratore-progetto`, {
      params: parameters
    }).toPromise();

  }

  public getCollaborazione(idProgetto: string, isFo: boolean = true): Promise<Models.ProgettoExtended> {
    const FOstring: string = isFo ? 'frontoffice/' : '';

    let parameters: HttpParams = new HttpParams();

    parameters = parameters.append('idProgetto', idProgetto.toString());

    return this.http.get<Models.ProgettoExtended>(`/${FOstring}collaboratore-progetto/collaborazione`, {
      params: parameters
    }).toPromise();

  }

  cancelCollaboration(idCollab: number, isFo: boolean = true) {
    const FOstring: string = isFo ? 'frontoffice/' : '';

    return this.http.post(`/${FOstring}collaboratore-progetto/cancel`, { idCollab }).toPromise();
  }

}
