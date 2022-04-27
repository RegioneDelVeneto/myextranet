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
import { AfterViewInit, Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';
import { Models } from '../../../models/model';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { SortDirection } from '@angular/material/sort';
import { ProductsService } from 'src/app/services/products.service';
import { GeneralConfirmComponent } from 'src/app/shared/modals/general-confirm/general-confirm.component';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';

@Component({
  selector: 'app-products-list',
  templateUrl: './products-list.component.html',
  styleUrls: ['./products-list.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProductsListComponent implements OnInit, AfterViewInit {

  @ViewChild('morfeoProductsListFilter') morfeoFilter: EngDynamicFormsComponent;

  public sortBy: string = 'nomeProdottoAttiv';
  public sortOrder: SortDirection = 'asc';

  public lastSearchFilter: any = null;
  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Prodotti attivabili' }
    )
  };

  public formJson: IForm = {

    components: [
      { key: 'nomeProdottoAttiv', type: 'textfield', label: 'Nome prodotto' },
      {
        key: 'attivabile', type: 'select', label: 'Attivabile', data: {
          "values": [
            {
              "label": "Solo attivabile",
              "value": true
            },
            {
              "label": "Non attivabile",
              "value": false
            },
            {
              "label": "Tutti",
              "value": null
            }
          ]
        },
        "dataSrc": "values",
      }
    ]

  };


  public isLoading = false;

  public tableHeader: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'nomeProdottoAttiv',
      header: 'Nome prodotto',
      span: 4
    },
    {
      sortActive: true,
      definition: 'desAttivazioneBreve',
      header: 'Descrizione',
      span: 6,
      textCut: 2,
    },
    {
      definition: 'dtAttivabileDa',
      header: 'Attivabile da',
      type: 'date',
      dateFormat: 'dd/MM/y',
      span: 3
    },
    {
      definition: 'dtAttivabileA',
      header: 'Attivabile a',
      type: 'date',
      dateFormat: 'dd/MM/y',
      span: 3

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
        icon: 'schema',
        event: 'requests',
        label: 'Configura richieste',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci'],
      }, {
        icon: 'delete',
        event: 'delete',
        label: 'Elimina prodotto attivabile',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci'],
      }
      ],
      span: 1
    }
  ];

  public get isOpen(): boolean {
    return !!this.formValue && !!Object.values(this.formValue).find(a => a !== null && a !== undefined && a !== '');

  }

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

  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;
  public productsDataSource: { [key: string]: string | Date }[] = [];


  private formValue: { [jkey: string]: any } = null;


  constructor(
    private router: Router,
    public dialog: MatDialog,
    private myextranetTableSrv: MyextTableService,
    private pService: ProductsService,
    private filterHistory: FiltersHistoryService
  ) { }

  ngOnInit(): void {
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
  }


  ngAfterViewInit(): void {
    this.morfeoFilter.formReadyEvent.subscribe(val => {
      if (!!this.formValue) this.morfeoFilter.f.form.patchValue(this.formValue);
    });
  }

  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    let url = `/prodotto-attivabile?_page=${pageindex}&_pageSize=${pagesize}`;

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
      this.isLoading = false;
      this.filterHistory.register(this.formValue, { length: this.length, pageSize: this.pageSize, pageIndex: this.pageIndex }, { sortByFilter: this.sortBy, sortByHeader: null, sortOrder: this.sortOrder });

      this.productsDataSource = Array.from(response.records);
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
    if (!event) return null;
    switch (event.eventType) {
      case 'edit': {
        if (event.element) {
          this.navigateToDetail(event.element);
        }
        break;
      }
      case 'requests': {
        if (event.element) {
          this.router.navigate(['backoffice/activable-products/requests-list'], { queryParams: { productId: event.element.idProdottoAtt } });
        }
        break;
      }
      case 'delete': {
        const element = event.element;
        const dialogRef = this.dialog.open(GeneralConfirmComponent, {
          minWidth: '400px',
          data: {
            title: `Conferma eliminazione: ${element.nomeProdottoAttiv}`,
            description: `Conferma l'eliminazione del prodotto attivabile :'${element.nomeProdottoAttiv}`
          }
        });
        dialogRef.afterClosed().subscribe(result => {
          if (!!result) {
            this.isLoading = true;
            this.pService.deleteProdottoAttivabileById(element.idProdottoAtt, false).then(val => {

              this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);

            });
          }
        });
      }
    }
  }


  public navigateToDetail(row?: { idProdottoAtt: number }): void {

    const path = ['backoffice/activable-products/product-detail'];
    if (row) {
      path.push(`${row.idProdottoAtt}`);
    }
    this.router.navigate(path);
  }


  manageSort(event: { active: string, direction: SortDirection }) {

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
