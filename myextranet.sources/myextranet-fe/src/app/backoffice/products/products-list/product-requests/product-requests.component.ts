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
import { NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router } from '@angular/router';
import { FormContainerConfig } from '@eng/morfeo';
import { Models } from 'src/app/models/model';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { ProductRequestsService } from 'src/app/services/product-requests.service';
import { ProductsService } from 'src/app/services/products.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { GeneralConfirmComponent } from 'src/app/shared/modals/general-confirm/general-confirm.component';

@Component({
  selector: 'app-product-requests',
  templateUrl: './product-requests.component.html',
  styleUrls: ['./product-requests.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProductRequestsComponent implements OnInit {

  public sortBy: string = null;
  public sortOrder: SortDirection = null;

  public lastSearchFilter: any = null;

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Prodotti attivabili' }
    )
  };

  public isLoading = false;

  public tableHeader: Models.TableHeader[] = [
    {

      definition: 'desProdottoProc',
      header: 'Descrizione',
      span: 4
    },
    {

      definition: 'desTipoRich',
      header: 'Tipo',
      span: 8
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [{
        icon: 'edit',
        event: 'edit',
        label: 'Modifica',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci'],
      },
      {
        icon: 'delete',
        event: 'delete',
        label: 'Elimina',
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
  public productsDataSource: { [key: string]: any }[] = [];

  private formValue: { [jkey: string]: any } = null;

  public formFilterConfig: FormContainerConfig = {
    showSubmit: true,
    submitLabel: 'CERCA',
    submitIcon: 'search',
    submitCallback: (f: NgForm) => {
      this.formValue = JSON.parse(JSON.stringify(f.form.value));

      this.lastSearchFilter = JSON.parse(JSON.stringify(f.form.value));
      this.pageIndex = 0;
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);
    },
    showReset: true,
    resetIcon: 'clear',
    resetLabel: 'CANCELLA',
    resetCallback: (f: NgForm) => {
      this.pageIndex = 0;
      this.sortBy = 'nomeProdottoAttiv';
      this.sortOrder = 'asc';
      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.lastSearchFilter = JSON.parse(JSON.stringify(f.form.value));
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);
    }
  };

  public productId: string;
  public product: Models.ProdottoAttivabileDTO;


  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private productsService: ProductsService,
    private requestsService: ProductRequestsService,
    public dialog: MatDialog,
    private myextranetTableSrv: MyextTableService,
    private filterHistory: FiltersHistoryService
  ) { }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(queryParams => {
      if (queryParams.has('productId')) {
        this.productId = queryParams.get('productId');
        this.breadcrumbs = {
          base: '',
          entries: new Array<IBreadcrumbsEntry>(
            { label: 'MyExtranet', url: `backoffice` },
            { label: 'Prodotti attivabili', url: `backoffice/activable-products` },
            { label: `Configurazione Richieste` }
          )
        };
        this.productsService.getProdottoAttivabileById(this.productId, false).toPromise().then((val: Models.ProdottoAttivabileDTO) => {
          this.product = val;
          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'MyExtranet', url: `backoffice` },
              { label: 'Prodotti attivabili', url: `backoffice/activable-products` },
              { label: `Lista configurazioni richieste: ${this.product.nomeProdottoAttiv}` }
            )
          };
          const filterFromHistory = this.filterHistory.getSavedData();

          if (!!filterFromHistory) {


            const paginataor = filterFromHistory.paginator;
            const sort = filterFromHistory.sort;
            this.formValue = filterFromHistory.filter;
            this.pageSize = paginataor.pageSize;
            this.sortBy = sort.sortByFilter;
            this.sortOrder = sort.sortOrder as SortDirection;

            this.length = paginataor.length;
            this.lastSearchFilter = filterFromHistory.filter;
          }
          this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);

        }).catch(err => {
          this.productId = null;
          this.product = null;
          this.isLoading = false;
          console.error(err);
        });
      }

    });


  }


  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    let url = `/procedimento-prodotto?_page=${pageindex}&_pageSize=${pagesize}&idProdottoAtt=${this.productId}`;

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

      this.productsDataSource = Array.from(response.records.map((val: Models.ProcProdottoDTO) => {
        return {
          ...val,
          desTipoRich: val.tipoRichiestaProdotto.desTipoRich
        }
      }));
      this.filterHistory.register(this.formValue, { length: this.length, pageSize: this.pageSize, pageIndex: this.pageIndex }, { sortByFilter: this.sortBy, sortByHeader: null, sortOrder: this.sortOrder });
      this.isLoading = false;

    }).catch(err => {
      this.isLoading = false;
    });
  }

  updatePaginator(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);
  }

  public manageEvent(event: { element: any, eventType: string }) {
    if (!event) {
      return null;
    }
    switch (event.eventType) {
      case 'edit': {
        if (event.element) {
          this.navigateToDetail(event.element);
        }
        break;
      }
      case 'delete': {
        const element = event.element;
        const dialogRef = this.dialog.open(GeneralConfirmComponent, {
          minWidth: '400px',
          data: {
            title: `Conferma eliminazione: ${element.desProdottoProc}`,
            description: `Conferma l'eliminazione della richiesta: ${element.desProdottoProc}; per il prodotto attivabile attivabile :'${element.prodotto.nomeProdottoAttiv}`
          }
        });
        dialogRef.afterClosed().subscribe(result => {
          if (!!result) {
            this.isLoading = true;
            this.requestsService.deleteProdRequestConfigById(element.idProdottoProc, false).then(val => {

              this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);

            });
          }
        });
      }
    }
  }


  public navigateToDetail(row?: Models.ProcProdottoDTO): void {

    const path = ['backoffice/activable-products/requests-list/request-configuration'];
    this.router.navigate(path, { queryParams: { productId: this.product.idProdottoAtt, requestId: row.idProdottoProc } });


  }

  public manageSort(event: { active: string, direction: SortDirection }): void {

    if (!!event.direction) {
      this.sortBy = event.active;
      this.sortOrder = event.direction;
    }
    else {
      this.sortBy = null;
      this.sortOrder = null;
    }

    this.isLoading = true;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);

  }

}
