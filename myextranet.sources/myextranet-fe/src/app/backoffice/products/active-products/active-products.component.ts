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
import { NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { SortDirection } from '@angular/material/sort';
import { Router } from '@angular/router';
import { ComboService, EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';
import { Models } from 'src/app/models/model';
import { CommunicationService } from 'src/app/services/communication.service';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { ProductsService } from 'src/app/services/products.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { DateSelectModalComponent } from 'src/app/shared/modals/date-select-modal/date-select-modal.component';
import { GeneralMailComponent } from 'src/app/shared/modals/general-mail/general-mail.component';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-active-products',
  templateUrl: './active-products.component.html',
  styleUrls: ['./active-products.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ActiveProductsComponent implements OnInit, AfterViewInit {

  @ViewChild('morfeoProductsListFilter') morfeoProdottiFilter: EngDynamicFormsComponent;

  public sortByFilter: string = 'prodottoAttivabile.nomeProdottoAttiv';
  public sortByHeaderFilter: string = 'prodottoNome';
  public sortOrder: SortDirection = 'asc';

  public firstLoad = false;

  public lastSearchFilter: any = null;

  public get isOpen(): boolean {
    return !!this.formValue && !!Object.values(this.formValue).find(a => a !== null && a !== undefined && a !== '');

  }

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Prodotti attivati' }
    )
  };

  public formJson: IForm = {

    components: [
      {
        key: 'idProdottoAtt',
        type: 'select',
        label: 'Prodotto Attivabile',
        defaultValue: null,
        hidden: false,
        data: {
          values: []
        },
        dataSrc: 'resource',
        suffix: '',
        input: true,
        valueProperty: 'id',
        labelProperty: 'titolo'
      },
      {
        key: 'idEnte',
        type: 'autocomplete',
        label: 'Ente',
        dataSrc: 'url',
        data: {
          url: `/ente/autocomplete?activeOnly=true`,
          method: 'GET',
          autocompleteType: 'server',
          params: {
            denominazione: ''
          },
          values: []
        },
        valueProperty: 'idEnte',
        labelProperty: 'denominazione'
      },
      {
        key: 'stato', type: 'select', label: 'Stato', data: {
          "values": [
            {
              "label": "Qualsiasi",
              "value": null
            },
            {
              "label": "Attivato",
              "value": 2
            },
            {
              "label": "Da attivare",
              "value": 0
            },
            {
              "label": 'In fase di attivazione',
              "value": 1
            }
          ]
        },
        "dataSrc": "values",
      }, {
        key: 'withPendingRequests', type: 'checkbox', label: 'Solo prodotti con richieste in corso'
      },
    ]

  };



  public isLoading = false;

  public tableHeader: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'prodottoNome',
      header: 'Prodotto Attivabile',
      span: 4
    },
    {
      sortActive: true,
      definition: 'enteDef',
      header: 'Ente',
      span: 4
    },
    {
      definition: 'stato',
      header: 'Stato',
      span: 4,
      type: 'objectMapper',
      objectMapped: {
        0: 'Da attivare',
        1: 'In fase di attivazione',
        2: 'Attivato',
      }
    },
    {
      definition: 'numRich',
      header: 'Richieste in corso',
      span: 3,
      type: 'objectMapper',
      objectMapped: {
        0: 'No',
        1: 'Si'
      }
    },
    {
      sortActive: true,
      definition: 'dtInizioValLD',
      header: 'Data di inizio validità',
      type: 'date',
      dateFormat: 'dd/MM/y',
      span: 4
    },
    {
      sortActive: true,
      definition: 'dtFineValLD',
      header: 'Data di fine validità',
      type: 'date',
      dateFormat: 'dd/MM/y',
      span: 4
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [{
        icon: 'people_alt',
        event: 'manage',
        label: 'Gestione utenti',
        acl: 'myextranet.prodotti',
        showOnlyIf: 'areUserEditable',
        permissions: ['gestisci'],
      },
      {
        icon: 'event',
        event: 'updateDtFine',
        label: 'Aggiorna fine validità',
        showOnlyIf: 'areUserEditable',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci'],
      },
      {
        icon: 'report',
        event: 'openRequests',
        label: 'Richieste',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci'],
      }
      ],
      span: 1
    }
  ];


  public formFilterConfig: FormContainerConfig = {
    showSubmit: true,
    submitLabel: 'CERCA',
    submitIcon: 'search',
    submitCallback: (f: NgForm) => {
      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.firstLoad = false;
      this.lastSearchFilter = JSON.parse(JSON.stringify(f.form.value));
      this.pageIndex = 0;
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
    },
    showReset: true,
    resetIcon: 'clear',
    resetLabel: 'CANCELLA',
    resetCallback: (f: NgForm) => {
      this.pageIndex = 0;
      this.sortByFilter = 'prodottoAttivabile.nomeProdottoAttiv';
      this.sortByHeaderFilter = 'prodottoNome';
      this.sortOrder = 'asc';


      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.lastSearchFilter = JSON.parse(JSON.stringify(f.form.value));
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
    }
  };


  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;
  public productsDataSource: { [key: string]: string | Date }[] = [];

  public products: Models.ComboElement[] = [];

  private formValue: { [jkey: string]: any } = null;

  private prodottiRroli: Models.RuoloDto[] = [];

  constructor(
    private pService: ProductsService,
    private comboService: ComboService,
    private filterHistory: FiltersHistoryService,
    private router: Router,
    public dialog: MatDialog,
    private myextranetTableSrv: MyextTableService,
    private comService: CommunicationService
  ) { }



  public ngAfterViewInit(): void {
    this.morfeoProdottiFilter.formReadyEvent.subscribe(val => {
      if (!!this.formValue) {
        this.morfeoProdottiFilter.f.form.patchValue(this.formValue);

      }
    });

    this.pService.getAutocomplete(99999, false).then(val => {
      this.products = val.map(prod => {
        return {
          label: prod.nomeProdottoAttiv,
          value: prod.idProdottoAtt
        };
      });
      this.products.unshift({ label: '', value: null });
      this.comboService.setList('idProdottoAtt', this.products);

    });

  }

  ngOnInit(): void {
    this.pService.getRuoliProdotti(false).then(prodottiRuoli => {
      this.prodottiRroli = prodottiRuoli;
    });

    const filterFromHistory = this.filterHistory.getSavedData();
    if (!!filterFromHistory && !!filterFromHistory.filter && !!Object.entries(filterFromHistory).find(v => v != null)) {
      const paginataor = filterFromHistory.paginator;
      const sort = filterFromHistory.sort;
      this.lastSearchFilter = filterFromHistory.filter;
      this.formValue = filterFromHistory.filter;

      this.pageSize = paginataor.pageSize;
      this.sortByFilter = sort.sortByFilter;
      this.sortOrder = sort.sortOrder as SortDirection;
      this.sortByHeaderFilter = sort.sortByHeader;
      this.length = paginataor.length;
      this.getDataFromService(paginataor.pageIndex + 1, paginataor.pageSize, sort.sortByFilter, filterFromHistory.filter, sort.sortOrder);
    } else {
      this.firstLoad = true;
    }


  }


  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    let url = `/prodotto-attivato?_page=${pageindex}&_pageSize=${pagesize}`;

    if (!!sortBy) {
      url = `${url}&_orderBy=${sortBy}`;
    }
    if (!!order) {
      url = `${url}&_orderDir=${order}`;
    }

    if (!!filter) {
      Object.keys(filter).forEach(key => {
        if (filter[key] !== null && filter[key] !== undefined) {
          if (key === 'withPendingRequests') {
            if (!!filter[key]) {
              url = `${url}&${key}=true`;
            }
          }
          else if (filter[key].value !== null && filter[key].value !== undefined) {
            url = `${url}&${key}=${filter[key].value}`;
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

      response.records = response.records.map((item: { ente: Models.enteDTO, stato: 1 | 2 | 0, prodottoAttivabile: { nomeProdottoAttiv: string } }) => ({
        ...item,
        enteDef: item.ente.denominazione,
        prodottoNome: item.prodottoAttivabile.nomeProdottoAttiv,
        areUserEditable: item.stato === 2
      }));

      this.productsDataSource = Array.from(response.records);
      this.filterHistory.register(this.formValue, { length: this.length, pageSize: this.pageSize, pageIndex: this.pageIndex }, { sortByFilter: this.sortByFilter, sortByHeader: this.sortByHeaderFilter, sortOrder: this.sortOrder });

    }).catch(err => {
      this.isLoading = false;
    });
  }

  public downloadProductsStats(): void {
    window.open(this.pService.downloadProdotti(this.morfeoProdottiFilter.f.form.value));
  }

  public downloadTenantsStats(): void {
    window.open(this.pService.downloadEnti(this.morfeoProdottiFilter.f.form.value));
  }

  public downloadServicesStats(): void {
    window.open(this.pService.downloadServizi(this.morfeoProdottiFilter.f.form.value));
  }


  updatePaginator(event: PageEvent): void {
    // this.length = event.length;
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
  }

  public manageEvent(event: { element: any, eventType: string }) {
    if (!event) {
      return null;
    }
    switch (event.eventType) {
      case 'manage': {
        if (event.element) {
          this.navigateToManage(event.element);
        }
        break;
      }
      case 'openRequests': {
        if (event.element) {
          this.router.navigate([`/backoffice/active-products/manage-requests/${event.element.idAttivazione}`]);
        }
        break;
      }
      case 'updateDtFine': {
        this.openUpdateDateModal(event.element);
        break;
      }
    }
  }

  public openUpdateDateModal(element: Models.ProdottoAttivatoDto): void {
    const dialogRef = this.dialog.open(DateSelectModalComponent, {
      minWidth: '400px',
      data: { title: 'Aggiorna la data di fine validità', description: `Prodotto attivo : ${element.prodottoAttivabile.nomeProdottoAttiv}`, date: element.dtFineVal }
    });
    dialogRef.afterClosed().subscribe((result: Date) => {

      if (result) {


        this.pService.patchProdottoAttivo({
          idAttivazione: element.idAttivazione,
          dtFineVal: result,
        }, false
        ).then(() => {
          this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
        });

      }

    });
  }


  public navigateToManage(row?: { idAttivazione: number }): void {

    const path = ['backoffice/active-products/manage-users'];
    if (row) {
      path.push(`${row.idAttivazione}`);
    }
    this.router.navigate(path);
  }

  manageSort(event: { active: string, direction: SortDirection }) {

    if (!!event.direction) {
      this.sortByFilter = this.filterNameConversion(event.active);
      this.sortByHeaderFilter = event.active;
      this.sortOrder = event.direction;
    }
    else {
      this.sortByFilter = null;
      this.sortByHeaderFilter = null;
      this.sortOrder = null;
    }

    this.isLoading = true;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);

  }

  private filterNameConversion(filterIn: string): string {
    switch (filterIn) {
      case 'enteDef': {
        return 'ente.denominazione';
      }
      case 'prodottoNome': {
        return 'prodottoAttivabile.nomeProdottoAttiv';
      }
      default: {
        return filterIn;
      }
    }
  }

  sendCommToUsers() {
    let roles = this.prodottiRroli;
    const dialogRef = this.dialog.open(GeneralMailComponent, {
      minWidth: '400px',
      data: {
        title: 'Invia Comunicazione ai gruppi', description: ``,
        filter: {
          ente: !!this.lastSearchFilter && !!this.lastSearchFilter.idEnte && this.lastSearchFilter.idEnte.label ? this.lastSearchFilter.idEnte.label : null,
          prodotto: !!this.lastSearchFilter && !!this.lastSearchFilter.idProdotto && !!this.products.find(val => val.value === this.lastSearchFilter.idProdotto) ?
            this.products.find(val => val.value === this.lastSearchFilter.idProdotto).label : null,
          richieste: !!this.lastSearchFilter?.withPendingRequests ? 'Solo richieste in corso' : null
        },
        checkbox: {
          atLeastOne: true,
          keys: roles.map(val => {
            return {
              key: val.codRuolo,
              label: val.desRuolo
            };
          })
        }
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const com: Models.BaseCommunication = {
          titolo: result.oggetto,
          messaggio: result.messaggio,
        };

        const lastFilter = {
          idEnte: !!this.lastSearchFilter && !!this.lastSearchFilter.idEnte && this.lastSearchFilter.idEnte.value ? this.lastSearchFilter.idEnte.value : null,
          idProdottoAtt: !!this.lastSearchFilter && !!this.lastSearchFilter.idProdotto ? this.lastSearchFilter.idProdotto : null,
          ruoloProdotto: roles.filter(ruolo => {
            return !!result[ruolo.codRuolo];
          }).map(r => r.codRuolo)
        };

        this.isLoading = true;
        this.comService.postComunicationForProductToGroupsByFilter(com, lastFilter).then(() => {
          this.isLoading = false;
        }).catch(err => {
          this.isLoading = false;
          console.error(err);
        });
      }



    });
  }

}
