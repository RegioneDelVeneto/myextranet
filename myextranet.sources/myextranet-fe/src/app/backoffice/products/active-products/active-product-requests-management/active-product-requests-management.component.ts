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
import { ActivatedRoute, Router } from '@angular/router';
import { Models } from 'src/app/models/model';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { ProductsService } from 'src/app/services/products.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { SortDirection } from '@angular/material/sort';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';


@Component({
  selector: 'app-active-product-requests-management',
  templateUrl: './active-product-requests-management.component.html',
  styleUrls: ['./active-product-requests-management.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ActiveProductRequestsManagementComponent implements OnInit {

  public sortBy: string = 'procedimentoProdotto.desProdottoProc';
  public sortOrder: SortDirection = 'asc';

  public isLoading = false;

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Prodotti attivati', url: `backoffice/active-products` },
      { label: 'Richieste' }
    )
  };

  public idAttivazione: string = null;

  public prodotto: Models.ProdottoAttivatoDto = null;



  public tableHeader: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'dtRich',
      header: 'Data',
      span: 3,
      type: 'date',
      dateFormat: 'dd/MM/y'
    },
    {
      sortActive: true,
      definition: 'procedimentoProdotto.tipoRichiestaProdotto.desTipoRich',
      header: 'Tipo',
      type: 'deepProp',
      span: 5
    },
    {
      definition: 'codStato',
      header: 'Stato richiesta',
      span: 4

    },
    {
      definition: 'flgFineRich',
      header: 'Concluso',
      span: 4,
      type: 'objectMapper',
      objectMapped: {
        0: 'No',
        1: 'Si',
      }

    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [{
        icon: 'edit',
        event: 'edit',
        label: 'Dettaglio',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci'],
      },
      ],
      span: 1
    }
  ];


  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;
  public productsDataSource: { [key: string]: string | Date }[] = [];

  constructor(
    private router: Router,
    private myextranetTableSrv: MyextTableService,
    private route: ActivatedRoute,
    private pService: ProductsService,
    private filterHistory: FiltersHistoryService

  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      if (params.has('idAttivazione')) {
        this.idAttivazione = params.get('idAttivazione');

        this.isLoading = true;
        this.pService.getProdottoAttivatoById(this.idAttivazione, false).then(prodotto => {
          this.prodotto = prodotto;

          const filterFromHistory = this.filterHistory.getSavedData();

          if (!!filterFromHistory) {


            const paginataor = filterFromHistory.paginator;
            const sort = filterFromHistory.sort;

            this.pageSize = paginataor.pageSize;
            this.sortBy = sort.sortByFilter;
            this.sortOrder = sort.sortOrder as SortDirection;

            this.length = paginataor.length;

          }

          this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, null, this.sortOrder);

        }).catch(err => {
          this.isLoading = false;
        });
      }
      else {
        this.idAttivazione = null;
        this.isLoading = false;
      }
    });
  }

  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    let url = `/richiesta-prodotto?_page=${pageindex}&_pageSize=${pagesize}&idAttivazione=${this.idAttivazione}`;

    if (!!sortBy) {
      url = `${url}&_orderBy=${sortBy}`;
    }
    if (!!order) {
      url = `${url}&_orderDir=${order}`;
    }


    if (!!filter) {
      Object.keys(filter).forEach(key => {
        if (filter[key] !== null && filter[key] !== undefined) {
          if (key === 'dataDa' || key === 'dataA') {
            url = `${url}&${key}=${new Date(filter[key]).valueOf()}`;
          } else {
            url = `${url}&${key}=${filter[key]}`;
          }

        }
      });
    }
    this.myextranetTableSrv.getData(url).then(response => {
      if (pageindex === 1 || pageindex === 0) {
        this.length = response.pagination.totalRecords;
      }
      this.filterHistory.register(null, { length: this.length, pageSize: this.pageSize, pageIndex: this.pageIndex }, { sortByFilter: this.sortBy, sortByHeader: null, sortOrder: this.sortOrder });


      this.productsDataSource = Array.from(response.records);
      this.isLoading = false;

    }).catch(err => {
      this.isLoading = false;
    });
  }

  updatePaginator(event: PageEvent): void {

    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, null, this.sortOrder);
  }

  public manageEvent(event: { element: any, eventType: string }) {
    if (!event) return null;
    switch (event.eventType) {
      case 'edit': {
        this.router.navigate([`/backoffice/active-products/manage-requests/detail/${event.element.idProdAttivRich}`]);
        break;
      }
    }
  }


  manageSort(event: { active: string, direction: SortDirection }) {

    if (!!event.direction) {
      this.sortBy = this.filterNameConversion(event.active);

      this.sortOrder = event.direction;
    }
    else {
      this.sortBy = null;
      this.sortOrder = null;
    }

    this.isLoading = true;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, null, this.sortOrder);

  }

  private filterNameConversion(filterIn: string): string {
    switch (filterIn) {

      default: {
        return filterIn;
      }
    }
  }

}
