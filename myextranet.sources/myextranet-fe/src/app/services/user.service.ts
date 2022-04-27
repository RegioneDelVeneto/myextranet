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
import { Router } from '@angular/router';
import { environment } from 'src/environments/environment';
import { Models } from '../models/model';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {

  }

  private excludedRoutes: string[] = ['/utente/user/new', '/utente/user/edit'];

  private userInfo: Models.UserInfo;

  public getUserInfo(isFO: boolean = true): Promise<Models.UserInfo> {
    if (!!this.userInfo) {
      return Promise.resolve(this.userInfo);
    }
    const foString = isFO ? `/frontoffice` : '';
    return this.http.get<Models.UserInfo>(`${foString}/utente/userinfo`).toPromise().then(userInfo => {
      this.userInfo = userInfo;
      return this.userInfo;
    });
  }

  public logout() {
    this.userInfo = null;
    return this.http.get(`/user-logout`).toPromise();
  }

  public updateUserInfo(isFO: boolean = true) {
    const foString = isFO ? `/frontoffice` : '';
    return this.http.get<Models.UserInfo>(`${foString}/utente/userinfo`).toPromise().then(userInfo => {
      this.userInfo = userInfo;
      return this.userInfo;
    });
  }

  public returnUserInfo() {
    return this.userInfo;
  }

  public checkUserInfo(isFO: boolean = true) {
    this.getUserInfo(isFO).then(val => {

      const url: string = this.router.url;
      for (const excludedUrl of this.excludedRoutes) {
        if (url.startsWith(excludedUrl)) {
          return null;
        }
      }
      if (!!val.userExpired) {
        this.router.navigate(['/utente/cortesia']);
      }
      else if (val.userId === null) {
        this.router.navigate(['/utente/user/new'], { queryParams: { callbackUrl: encodeURIComponent(url) } });
      }
      else if (!!val.tenantExpired) {
        this.router.navigate(['/utente/user/edit'], { queryParams: { callbackUrl: encodeURIComponent(url) } });
      }
    });
  }

  public checkUserInfoForRoute(full: boolean = true, inputUrl?: string): Promise<boolean | string> {
    return this.getUserInfo().then(val => {
      // this.userInfo = val;
      const url: string = !!inputUrl ? inputUrl : this.router.url;
      for (const excludedUrl of this.excludedRoutes) {
        if (url.startsWith(excludedUrl)) {
          return true;
        }
      }
      if (!!val.userExpired) {
        return '/utente/cortesia';
      }
      else if (full && val.userId === null) {
        if (inputUrl.indexOf('?callbackUrl') === -1) {
          return `/utente/user/new?callbackUrl=${encodeURIComponent(url)}`;
        } else {
          return url;
        }
      }
      else if (full && !!val.tenantExpired) {
        return `/utente/user/edit?callbackUrl=${encodeURIComponent(url)}`;
      }
      else { return true; }
    });
  }
}
