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
import { EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';
import { Models } from 'src/app/models/model';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-rap-requests',
  templateUrl: './rap-requests.component.html',
  styleUrls: ['./rap-requests.component.scss'],
  encapsulation: ViewEncapsulation.None

})
export class RapRequestsComponent implements OnInit, AfterViewInit {

  @ViewChild('morfeoRapRichieste') morfeoFilter: EngDynamicFormsComponent;


  public sortByFilter: string = 'utente.cognome';
  public sortByHeaderFilter: string = 'utenteCognome';
  public sortOrder: SortDirection = 'asc';


  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Richieste RAP' }
    )
  };

  public formJson: IForm = {

    components: [
      { key: 'cognomeUtente', type: 'textfield', label: 'Cognome' },
      { key: 'nomeUtente', type: 'textfield', label: 'Nome' },
      {
        key: 'idEnte',
        type: 'autocomplete',
        label: 'Ente',
        dataSrc: 'url',
        data: {
          url: `/ente/autocomplete`,
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
    ]

  };


  public isLoading = false;

  public tableHeader: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'utenteCognome',
      header: 'Cognome',
      span: 4
    },
    {
      sortActive: true,
      definition: 'utenteNome',
      header: 'Nome',
      span: 4
    },

    {
      sortActive: true,
      definition: 'enteDesc',
      header: 'Ente',
      span: 4
    },
    {
      sortActive: true,
      definition: 'flgConferma',
      header: 'Stato',
      span: 4,
      type: 'objectMapper',
      objectMapped: {
        '-1': 'Da risottomettere',
        0: 'In attesa di conferma',
        1: 'Confermata',
        2: 'Non confermata',
      }
    },
    {
      sortActive: true,
      definition: 'dtRichiesta',
      header: 'Data richiesta',
      span: 4,
      type: 'date',
      dateFormat: 'dd/MM/y'
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

      this.pageIndex = 0;
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
    },
    showReset: true,
    resetIcon: 'clear',
    resetLabel: 'CANCELLA',
    resetCallback: (f: NgForm) => {
      this.pageIndex = 0;
      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.sortByFilter = 'utente.cognome';
      this.sortByHeaderFilter = 'utenteCognome';
      this.sortOrder = 'asc';
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
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
    private filterHistory: FiltersHistoryService
  ) { }

  ngOnInit(): void {
    const filterFromHistory = this.filterHistory.getSavedData();

    if (!!filterFromHistory) {

      const paginataor = filterFromHistory.paginator;
      const sort = filterFromHistory.sort;
      this.formValue = filterFromHistory.filter;
      this.pageSize = paginataor.pageSize;
      this.sortByFilter = sort.sortByFilter;
      this.sortOrder = sort.sortOrder as SortDirection;
      this.sortByHeaderFilter = sort.sortByHeader;
      this.length = paginataor.length;

    }
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
  }


  ngAfterViewInit(): void {
    this.morfeoFilter.formReadyEvent.subscribe(val => {
      if (!!this.formValue) this.morfeoFilter.f.form.patchValue(this.formValue);
    });
  }

  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    let url = `/rappresentante-ente-rap?_page=${pageindex}&_pageSize=${pagesize}&flgRichInCorso=1`;

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
          } else if (key === 'idEnte') {
            url = `${url}&${key}=${filter[key].value}`;
          }
          else {
            url = `${url}&${key}=${filter[key]}`;
          }

        }
      });
    }
    this.myextranetTableSrv.getData(url).then(response => {
      if (pageindex === 1 || pageindex === 0) {
        this.length = response.pagination.totalRecords;
      }

      response.records = response.records.map((item: { utente: Models.utenteDTO, ente: Models.enteDTO, utenteNome: string, utenteCognome: string, enteDesc: string }) => ({
        ...item,
        utenteNome: item.utente.nome,
        utenteCognome: item.utente.cognome,
        enteDesc: item.ente.denominazione
      }));
      this.filterHistory.register(this.formValue, { length: this.length, pageSize: this.pageSize, pageIndex: this.pageIndex }, { sortByFilter: this.sortByFilter, sortByHeader: this.sortByHeaderFilter, sortOrder: this.sortOrder });
      this.productsDataSource = Array.from(response.records);
      this.isLoading = false;

    }).catch(err => {
      this.isLoading = false;
    });
  }

  updatePaginator(event: PageEvent): void {

    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
  }

  public manageEvent(event: { element: any, eventType: string }) {
    if (!event) return null;
    switch (event.eventType) {
      case 'edit': {
        if (event.element) this.navigateToDetail(event.element);
        break;
      }
    }
  }


  public navigateToDetail(row?: { idRappr: number }): void {

    this.router.navigate(['/backoffice/RAP-requests/management'], { queryParams: { idRappr: row.idRappr } });

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
      case 'utenteNome': {
        return 'utente.nome';
      }
      case 'utenteCognome': {
        return 'utente.cognome';
      }
      case 'enteDesc': {
        return 'ente.denominazione';
      }
      default: {
        return filterIn;
      }
    }
  }


}
