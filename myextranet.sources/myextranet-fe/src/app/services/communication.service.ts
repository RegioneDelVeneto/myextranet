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
import { HttpClient, HttpEvent, HttpEventType, HttpParams, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AlertService } from './alert.service';
import { v4 as uuidv4 } from 'uuid';
import { Models } from '../models/model';
import { environment } from 'src/environments/environment';


@Injectable({
  providedIn: 'root'
})
export class CommunicationService {

  constructor(
    private http: HttpClient,
    private alertService: AlertService) {

  }

  postCommunication(comm: Models.BaseCommunication): Promise<any> {

    return this.http.post(`/comunicazione`, comm).toPromise();

  }

  postCommunicationToAllMembers(comm: Models.BaseCommunication, eventId: string): Promise<any> {

    return this.http.post(`/iscritto-evento/${eventId}/comunicazione`, comm).toPromise();

  }

  postComunicationToUsersByFilter(comm: Models.BaseCommunication, filters: Models.utenteFilter): Promise<any> {

    let params: HttpParams = new HttpParams();
    Object.keys(filters).forEach(key => {
      if (!!filters[key]) params = params.append(key, filters[key])
    });
    params = params.append('flgArchived', '0');
    return this.http.post(`/utente/comunicazione`, comm, { params }).toPromise();
  }


  postComunicationForProductToGroupsByFilter(comm: Models.BaseCommunication, filters: { idEnte: number, idProdottoAtt: string, ruoloProdotto: string[] }): Promise<any> {

    let params: HttpParams = new HttpParams();
    Object.keys(filters).forEach(key => {
      if (!!filters[key] && key !== 'ruoloProdotto') params = params.append(key, filters[key])
      if (!!filters[key] && key === 'ruoloProdotto') {
        filters.ruoloProdotto.forEach(ruolo => {
          params = params.append('ruoloProdotto', ruolo);
        })
      }
    });
    return this.http.post(`/prodotto-attivato/comunicazione`, comm, { params }).toPromise();
  }

  postComunicationToCollabByFilter(comm: Models.BaseCommunication, filters: any) {
    let params: HttpParams = new HttpParams();
    if (!!filters) {
      Object.keys(filters).forEach(key => {
        if (filters[key] !== null && filters[key] !== undefined) {
          params = params.append(key, filters[key]);
        }
      });
    }
    return this.http.post(`/collaboratore-progetto/comunicazione`, comm, { params }).toPromise();

  }

  getIscrittoById(id: number): Promise<any> {

    return this.http.get(`/iscritto-evento/${id}`).toPromise();
  }

  postInvito(comm: Models.BaseCommunication): Promise<any> {
    return this.http.post(`/invito/evento`, comm).toPromise();
  }

  postOrPatchIscrizioneEvento(comm: Models.IscrizioneEvento, isEdit: boolean = false): Promise<any> {

    if (isEdit) {
      return this.http.patch(`/iscritto-evento`, comm).toPromise();
    }
    else {
      return this.http.post(`/iscritto-evento`, comm).toPromise();
    }
  }

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

    return this.http.post(`/iscritto-evento/operazione-massiva`, postObject).toPromise();
  }
  postMassiveOperationProgress(type: string, data: { [key: string]: boolean }) {

    const operationId = uuidv4();

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

    const request: HttpRequest<any> = new HttpRequest('POST', `/iscritto-evento/operazione-massiva-progress`, postObject, { reportProgress: true, responseType: 'text' });

    this.http.request(request)
      .pipe(

        catchError((err: any) => {
          this.alertService.completeProgress('Invio attestati', operationId);
          return of(err);
        })
      )
      .subscribe((event: HttpEvent<any>) => {
        if (event.type === HttpEventType.Sent) {
          this.alertService.passDataToProgressSubject({
            progress: 0, operation: 'Invio attestati in corso', id: operationId
          });
          this.alertService.openSnackBar('Invio attestati avviato', 'Success');
        }
        if (event.type === HttpEventType.DownloadProgress) {

          this.alertService.passDataToProgressSubject({ progress: Math.round(100 * event.loaded / event.total), operation: 'Invio attestati in corso', id: operationId });
        } else if (event instanceof HttpResponse) {

          this.alertService.completeProgress('Invio attestati in corso', operationId);
          this.alertService.openSnackBar('Invio attestati completato', 'Success');

        }
      });

  }

  patchValuesInMemberById(key: string, id: number, newStatus: number): Promise<any> {

    const patchObj: { idIscritto: number } = {
      idIscritto: id
    };
    patchObj[key] = newStatus;

    return this.http.patch(`/iscritto-evento`, patchObj).toPromise();
  }

  getFoglioPresenze(idEvento: string): string {
    return `/iscritto-evento/${idEvento}/foglio-presenze`;
  }

  getAttestato(idEvento: string, idIscritto: number): string {
    return `/iscritto-evento/attestato/${idIscritto}`;
  }
}
