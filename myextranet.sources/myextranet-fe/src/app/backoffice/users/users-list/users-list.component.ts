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
import { DataTableService, EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';
import { CommunicationService } from 'src/app/services/communication.service';
import { EnteService } from 'src/app/services/ente.service';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { GeneralMailComponent } from 'src/app/shared/modals/general-mail/general-mail.component';
import { environment } from 'src/environments/environment';
import { Models } from '../../../models/model';

@Component({
  selector: 'app-users-list',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './users-list.component.html',
  styleUrls: ['./users-list.component.scss']
})
export class UsersListComponent implements OnInit, AfterViewInit {
  @ViewChild('morfeoUsersFilter') morfeoFilter: EngDynamicFormsComponent;


  public sortBy: string = 'cognome';
  public sortOrder: SortDirection = 'asc';
  public lastSearchFilter: any = null;

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Utenti' }
    )
  };

  public formJson: IForm = {

    components: [
      { key: 'nome', type: 'textfield', label: 'Nome' },
      { key: 'cognome', type: 'textfield', label: 'Cognome' },
      {
        key: 'idEnte',
        type: 'autocomplete',
        label: 'Ente', // idente
        dataSrc: 'url',
        //  rerender : 'records',
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
      { key: 'azienda', type: 'textfield', label: 'Azienda' }
    ]

  };
  public isLoading = false;

  public tableHeader: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'cognome',
      header: 'Cognome',
      span: 3
    },
    {
      sortActive: true,
      definition: 'nome',
      header: 'Nome',
      span: 4
    },

    {
      type: 'list',
      definition: 'enteAzienda',
      header: 'Ente o Azienda',
      span: 3
    },
    {
      type: 'list',
      definition: 'riferimenti',
      header: 'Riferimenti',
      span: 3
    },
    {
      sortActive: true,
      definition: 'codFiscale',
      header: 'Codice fiscale',
      span: 3
    }
  ];

  public formFilterConfig: FormContainerConfig = {
    showSubmit: true,
    submitLabel: 'CERCA',
    submitIcon: 'search',
    submitCallback: (f: NgForm) => {

      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      if (this.formValue.idEnte !== null && this.formValue.idEnte !== undefined) {
        this.formValue.idEnte = this.formValue.idEnte.value || null;
      }
      this.lastSearchFilter = JSON.parse(JSON.stringify(f.form.value));
      this.pageIndex = 0;
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);
    },
    showReset: true,
    resetIcon: 'clear',
    resetLabel: 'CANCELLA',
    resetCallback: (f: NgForm) => {
      this.pageIndex = 0;

      this.sortBy = 'cognome';
      this.sortOrder = 'asc';

      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.lastSearchFilter = JSON.parse(JSON.stringify(f.form.value));
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);
    }
  };

  public get isOpen(): boolean {
    return !!this.formValue && !!Object.values(this.formValue).find(a => a !== null && a !== undefined && a !== '');

  }


  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;
  public eventsDataSource: { [key: string]: string | Date }[] = [];


  private formValue: { [jkey: string]: any } = null;

  constructor(
    private tableService: DataTableService,
    public dialog: MatDialog,
    private communicationService: CommunicationService,
    private enteService: EnteService,
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

    let url = `/utente?flgArchived=0&_page=${pageindex}&_pageSize=${pagesize}`;

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
      response.records = response.records.map((res: Models.utenteDTO) =>
      ({
        ...res,
        enteAzienda: res.ente !== null && res.ente !== undefined && !!res.ente.denominazione ? [res.ente.denominazione] : [res.azienda, res.partitaIva],
        riferimenti: [res.email, res.telefono, res.telefonoUff].filter(v => !!v)
      })
      );
      this.eventsDataSource = Array.from(response.records);
      this.filterHistory.register(this.formValue, { length: this.length, pageSize: this.pageSize, pageIndex: this.pageIndex }, { sortByFilter: this.sortBy, sortByHeader: null, sortOrder: this.sortOrder });
      this.isLoading = false;

    }).catch(err => {
      this.isLoading = false;
    });
  }

  openDiag(): void {
    let modalFilters = {};
    const filters = this.lastSearchFilter;
    let idEnte = filters && filters.idEnte && filters.idEnte.value ? filters.idEnte.value : null;
    if (!!filters) {
      modalFilters = {
        ente: filters.idEnte && filters.idEnte.label ? filters.idEnte.label : null,
        nome: filters.nome,
        cognome: filters.cognome,
        azienda: filters.azienda
      };
    }
    const dialogRef = this.dialog.open(GeneralMailComponent,
      {
        data: { title: 'Invia Comunicazione', description: 'Invia comunicazione a tutti gli utenti risultanti dall\'ultima ricerca effettuata', filter: modalFilters }
      });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const comm: Models.BaseCommunication = {
          titolo: result.oggetto || null,
          messaggio: result.messaggio || null
        };
        const commFilters: Models.utenteFilter = { idEnte: idEnte, nome: filters && filters.nome ? filters.nome : null, cognome: filters && filters.cognome ? filters.cognome : null, azienda: filters && filters.azienda ? filters.azienda : null, flgArchived: 0 };
        this.communicationService.postComunicationToUsersByFilter(comm, commFilters).then(v => { }).catch(err => { });
      }

    });

  }

  updatePaginator(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);
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
