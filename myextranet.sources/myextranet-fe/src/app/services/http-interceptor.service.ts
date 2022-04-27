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
import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpInterceptor,
  HttpHandler,
  HttpRequest, HttpErrorResponse, HttpResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { AlertService } from './alert.service';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class HttpInterceptorService implements HttpInterceptor {

  constructor(private alert: AlertService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const method: string = req.method;

    return next.handle(req).pipe(
      tap(evt => {
        if (evt instanceof HttpResponse) {
          if (!!evt && (evt.status === 200 || evt.status === 201)) {
            this.alert.passDataToSubject({ method, url: req.url, snackType: 'Success' });
          }


        }
      }),
      catchError((err: any) => {
        if (err instanceof HttpErrorResponse) {
          try {
            console.error(err);
            if (err.status === 401) {
              const location = window.location;
              window.open(`saml/login?callbackUrl=${location}`, "_self");
            }
            else if (err.status === 403) {
              this.alert.passDataToSubject({ method: 'POST', url: 'acl-general-error', snackType: 'Error' });
            }
            else {
              this.alert.passDataToSubject({ method, url: req.url, snackType: 'Error', error: err.error });
            }
          } catch (e) {
            console.error(e);
          }
        }
        return throwError(err);
      }));

  }
}

