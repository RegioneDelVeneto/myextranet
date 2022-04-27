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
import { ActivatedRoute, Router } from '@angular/router';
import { EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { ProjectsService } from 'src/app/services/projects.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { environment } from 'src/environments/environment';
import { Models } from '../../../models/model';

export class MyExtCollabs implements Models.CollaboratoreMyExtranet {
  idCollab: number;
  nome: string;
  cognome: string;
  denominazioneEnteAzienda: string;
  flgCoordStr: string;
  idProgetto: string;
  fullData: Models.CollaboratoreDTO;
  valid: boolean;
  dtInizioVal: number | Date;
  dtFineVal: number | Date;
  telefono: string;
  email: string;
  dtRichiesta: Date;
  stato: string;
  contatti: string[];

  constructor(dto: Models.CollaboratoreDTO) {
    this.fullData = dto;
    this.idCollab = dto.idCollab;
    this.idProgetto = dto.idProgetto;
    this.valid = dto.valid;
    this.dtInizioVal = dto.dtInizioVal;
    this.dtFineVal = dto.dtFineVal;
    this.nome = !!dto.utente && dto.utente.nome ? dto.utente.nome : null;
    this.cognome = !!dto.utente && dto.utente.cognome ? dto.utente.cognome : null;
    this.contatti = [dto?.utente?.telefono, dto?.utente?.email, dto?.utente?.telefonoUff].filter(v => !!v);
    this.denominazioneEnteAzienda = !!dto.utente && dto.utente.ente && dto.utente.ente.denominazione && dto.utente.ente.denominazione.length > 0 ? dto.utente.ente.denominazione :
      !!dto.utente && !!dto.utente.azienda ? dto.utente.azienda : null;
    this.flgCoordStr = !!dto.flgCoord ? 'Si' : 'No';
    this.telefono = [dto.utente.telefono, dto.utente.telefonoUff].filter(v => !!v).join(', ');
    this.email = dto.utente.email;
    this.dtRichiesta = new Date(dto.dtRichiesta);
    this.stato = dto.flgConferma === 0 ? 'In attesa di conferma' : dto.flgConferma === 2 ? 'Rifiutata' : null;
    this.nome = dto.utente.nome || null;
  }
}

@Component({
  selector: 'app-project-collaborators-requests',
  templateUrl: './project-collaborators-requests.component.html',
  styleUrls: ['./project-collaborators-requests.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProjectCollaboratorsRequestsComponent implements OnInit, AfterViewInit {


  @ViewChild('morfeoCollaboratorRequests') morfeoFilter: EngDynamicFormsComponent;

  public sortByFilter: string = 'utente.cognome';
  public sortByHeaderFilter: string = 'cognome';
  public sortOrder: SortDirection = 'asc';

  public opType: string = null;


  public project: Models.MyPortalContent = null;
  public projectId: string = null;
  public formJson: IForm = null;
  public breadcrumbs = null;

  public isLoading = false;

  public preselectedRows: Models.iscrittoDTO[] = [];


  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;
  public collaboratorsDataSource: { [key: string]: string | Date }[] = [];

  private formValue: { [key: string]: any } = null;

  public get isOpen(): boolean {
    return !!this.formValue && !!Object.values(this.formValue).find(a => a !== null && a !== undefined && a !== '');

  }

  public formFilterConfig: FormContainerConfig = {
    showSubmit: true,
    submitLabel: 'CERCA',
    submitIcon: 'search',
    submitCallback: (f: NgForm) => {

      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      if (this.formValue.idEnte !== null && this.formValue.idEnte !== undefined) {
        this.formValue.idEnte = this.formValue.idEnte.value || null;
      }
      this.pageIndex = 0;
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
    },
    showReset: true,
    resetIcon: 'clear',
    resetLabel: 'CANCELLA',
    resetCallback: (f: NgForm) => {

      this.sortByFilter = 'utente.cognome';
      this.sortByHeaderFilter = 'cognome';
      this.sortOrder = 'asc';
      this.pageIndex = 0;
      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, f.form.value, this.sortOrder);
    }
  };


  public tableHeader: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'cognome',
      header: 'Cognome',
      span: 2
    },
    {
      sortActive: true,
      definition: 'nome',
      header: 'Nome',
      span: 2
    },
    {
      definition: 'denominazioneEnteAzienda',
      header: 'Ente o Azienda',
      span: 2
    },
    {
      definition: 'contatti',
      header: 'Contatti',
      type: 'list',
      span: 2
    },
    {
      definition: 'stato',
      header: 'Stato',
      span: 2
    },
    {
      definition: 'dtRichiesta',
      header: 'Data della richiesta',
      span: 2,
      type: 'date',
      dateFormat: 'dd/MM/y'
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [{
        icon: 'how_to_reg',
        event: 'changeCollStatus',
        label: 'Gestisci richiesta',
        acl: 'myextranet.progetti',
        permissions: ['gestisci'],
      }
      ],
      span: 1
    }
  ];

  constructor(
    private myextranetTableSrv: MyextTableService,
    private route: ActivatedRoute,
    private router: Router,
    private myPortalService: MyportalService,
    private projectsService: ProjectsService,
    public dialog: MatDialog,
    private filterHistory: FiltersHistoryService

  ) { }

  ngOnInit(): void {

    this.route.queryParamMap.subscribe(queryParams => {
      if (queryParams.has('projectId')) {
        this.projectId = queryParams.get('projectId');
        this.breadcrumbs = {
          base: '',
          entries: new Array<IBreadcrumbsEntry>(
            { label: 'MyExtranet', url: `backoffice` },
            { label: 'Progetti', url: `backoffice/projects` },
            { label: 'Lista richieste di collaborazione al progetto' }
          )
        };
        this.myPortalService.getContent(this.projectId, false).then((val: Models.MyPortalEvent) => {
          this.project = val;
          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'MyExtranet', url: `backoffice` },
              { label: 'Progetti', url: `backoffice/projects` },
              { label: `Lista richieste di collaborazione al progetto: ${this.project.titolo}` },
            )
          };


          this.formJson = {
            components: [
              { key: 'nome', type: 'textfield', label: 'Nome' },
              { key: 'cognome', type: 'textfield', label: 'Cognome' },
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
              }, {
                key: 'flgConferma', type: 'select', label: 'Stato', data: {
                  "values": [
                    {
                      "label": "In attesa di conferma",
                      "value": 0
                    },
                    {
                      "label": "Rifiutate",
                      "value": 2
                    },
                    {
                      "label": "Tutte",
                      "value": null
                    }
                  ]
                },
                "dataSrc": "values",
              },
              { key: 'azienda', type: 'textfield', label: 'Azienda' },

            ]
          };
          setTimeout(() => {

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

          }, 100);
        }).catch(err => {
          this.projectId = null;
          this.project = null;
          console.error(err);
        });
      }

    });

  }


  ngAfterViewInit(): void {
    this.morfeoFilter.formReadyEvent.subscribe(val => {

      if (!!this.formValue) this.morfeoFilter.f.form.patchValue(this.formValue);
    });
  }


  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;

    let url = `/collaboratore-progetto/richieste-collaborazione?_page=${pageindex}&_pageSize=${pagesize}&idProgetto=${this.projectId}&validOnly=false`;

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


      response.records = response.records.map((res: Models.CollaboratoreDTO) =>
        (new MyExtCollabs(res))
      );

      this.collaboratorsDataSource = Array.from(response.records);
      this.filterHistory.register(this.formValue, { length: this.length, pageSize: this.pageSize, pageIndex: this.pageIndex }, { sortByFilter: this.sortByFilter, sortByHeader: this.sortByHeaderFilter, sortOrder: this.sortOrder });

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
    if (!event) { return null; }
    switch (event.eventType) {
      case 'changeCollStatus': {
        this.router.navigate(['/backoffice/projects/collaborators-requests-list/manage'], { queryParams: { idCollab: event.element.idCollab } });
        break;
      }
    }
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
      case 'flgCoordStr': {
        return 'flgCoord';
      }
      case 'nome': {
        return 'utente.nome';
      }
      case 'cognome': {
        return 'utente.cognome';
      }
      case 'dtInizioVal': {
        return 'dtInizioVal';
      }
      case 'dtFineVal': {
        return 'dtFineVal';
      }
      default: {
        return filterIn;
      }
    }
  }




}
