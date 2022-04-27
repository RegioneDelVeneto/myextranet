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
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { Models } from 'src/app/models/model';
import { EnteService } from 'src/app/services/ente.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProductsComponent implements OnInit {


  public isLoading: boolean;

  public rappEnte: Models.RappEnteDTO[];

  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;

  breadcrumbs = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca', url: '/utente/bacheca' },
      { label: 'I miei Prodotti' }
    )
  };

  constructor(private enteService: EnteService) { }

  ngOnInit(): void {
    this.isLoading = true;
    this.getDataFromService();
  }

  getDataFromService(pageindex: number = -1, pagesize: number = 999999, sortBy?: string): void {
    this.isLoading = true;
    this.enteService.getEntiRappresentati(pageindex, pagesize).then(val => {
      if (pageindex === 1 || pageindex === 0) {
        this.length = val.pagination.totalRecords;
      }
      let entiRap: Models.RappEnteDTO[] = val.records as Models.RappEnteDTO[];
      this.rappEnte = entiRap;

      this.isLoading = false;
    }).catch(err => {
      this.isLoading = false;
    });
  }

  updatePaginator(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, null);
  }



}
