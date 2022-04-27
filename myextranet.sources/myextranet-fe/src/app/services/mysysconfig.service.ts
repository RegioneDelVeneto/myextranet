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
import { of } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MysysconfigService {

  private myPortalUrl: string;

  constructor(private http: HttpClient) { }

  getMyintranetUrl(): Promise<string> {
    return this.http.get<{ data: string, status: string, message: string }>(`/mysysconfig/getMyintranetDeployUrl`)
      .pipe(
        switchMap((val) => of(val.data))
      )
      .toPromise();
  }

  getMyPortalDeployURL(): Promise<string> {
    return this.http.get<{ data: string, status: string, message: string }>(`/frontoffice/mysysconfig/getMyPortalDeployUrl`)
      .pipe(
        switchMap((val) => of(val.data))
      )
      .toPromise();
  }

  returnMyPortalUrl(): Promise<string> {
    if (!this.myPortalUrl) {
      return this.getMyPortalDeployURL().then(mypUrl => {
        this.myPortalUrl = mypUrl;
        return Promise.resolve(this.myPortalUrl);
      }).catch(err => this.myPortalUrl = null);
    } else {
      return Promise.resolve(this.myPortalUrl);
    }

  }

  getMYEXTItemByCode(code: string): Promise<any> {
    return this.http.get(`/mysysconfig/getItem?codice=MYEXT>${code}`).toPromise();
  }


}
