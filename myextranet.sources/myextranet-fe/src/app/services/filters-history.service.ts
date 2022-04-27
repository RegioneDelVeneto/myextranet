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
import { ActivatedRoute, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class FiltersHistoryService {


  private filterHistory: Record<string, { filter: any, paginator: { length: number, pageSize: number, pageIndex: number }, sort: { sortByFilter: string, sortByHeader?: string, sortOrder: string } }> = {};

  constructor(
    private activeRoute: ActivatedRoute,
    private router: Router
    ) { }

  get routeSnpShot() {

    return this.router.url;
  }


  public register(filter: any, paginator: { length: number, pageSize: number, pageIndex: number }, sort: { sortByFilter: string, sortByHeader?: string, sortOrder: string }): void {

    this.filterHistory[this.routeSnpShot.toString()] = { filter, paginator, sort };

  }

  public getSavedData(): { filter: any, paginator: { length: number, pageSize: number, pageIndex: number }, sort: { sortByFilter: string, sortByHeader?: string, sortOrder: string } } {
    if (!!this.filterHistory[this.routeSnpShot.toString()]) {
      return this.filterHistory[this.routeSnpShot.toString()];
    }
    else return null;

  }

  public deleteData(): void {
    if (!!this.filterHistory[this.routeSnpShot.toString()]) {
      this.filterHistory[this.routeSnpShot.toString()] = null
    }

  }

}
