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
import { SelectionChange } from '@angular/cdk/collections';
import { AfterViewInit, Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router } from '@angular/router';
import { EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';
import { CommunicationService } from 'src/app/services/communication.service';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { ProjectsService } from 'src/app/services/projects.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { GeneralConfirmComponent } from 'src/app/shared/modals/general-confirm/general-confirm.component';
import { GeneralMailComponent } from 'src/app/shared/modals/general-mail/general-mail.component';
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
  dtInizioValLD: number | Date;
  dtFineValLD: number | Date;
  dtRange: Date[];
  contatti: string[];

  constructor(dto: Models.CollaboratoreDTO) {
    this.fullData = dto;
    this.idCollab = dto.idCollab;
    this.idProgetto = dto.idProgetto;
    this.valid = dto.valid;
    this.dtInizioValLD = !!dto.dtInizioVal ? (new Date(dto.dtInizioValLD)) : null;
    this.dtFineValLD = !!dto.dtFineVal ? (new Date(dto.dtFineValLD)) : null;
    this.dtRange = [this.dtInizioValLD, this.dtFineValLD];
    this.contatti = [dto?.utente?.telefono, dto?.utente?.email, dto?.utente?.telefonoUff].filter(v => !!v);
    this.nome = !!dto.utente && dto.utente.nome ? dto.utente.nome : null;
    this.cognome = !!dto.utente && dto.utente.cognome ? dto.utente.cognome : null;
    this.denominazioneEnteAzienda = !!dto.utente && dto.utente.ente && dto.utente.ente.denominazione && dto.utente.ente.denominazione.length > 0 ? dto.utente.ente.denominazione :
      !!dto.utente && !!dto.utente.azienda ? dto.utente.azienda : null;
    this.flgCoordStr = !!dto.flgCoord ? 'Si' : 'No';

    this.nome = dto.utente.nome || null;
  }


  manageDateZones(data: Date, ingresso: boolean = true): Date {
    let sign = 1;
    if (ingresso) {
      sign = -1;
    }
    let parsedDate = new Date(data);
    let adjustedDate = new Date(parsedDate.valueOf() - (sign) * parsedDate.getTimezoneOffset() * 60000);
    return adjustedDate
  }


}


@Component({
  selector: 'app-project-collaborators',
  templateUrl: './project-collaborators.component.html',
  styleUrls: ['./project-collaborators.component.scss'],
  encapsulation: ViewEncapsulation.None,

})
export class ProjectCollaboratorsComponent implements OnInit, AfterViewInit {

  public sortByFilter: string = 'utente.cognome';
  public sortByHeaderFilter: string = 'cognome';
  public sortOrder: SortDirection = 'asc';

  public opType: string = null;

  public get saveLabel() {
    switch (this.opType) {
      case 'ImpostaCoordinatore': {

        return 'Salva Coordinatori';
      }
      default: return 'Salva';
    }
  }
  public project: Models.MyPortalContent = null;
  public projectId: string = null;
  public formJson: IForm = null;
  public breadcrumbs = null;
  public showCollaboratorsMangament = false;

  public isLoading = false;

  public preselectedRows: Models.iscrittoDTO[] = [];
  public signUpObject: { [key: string]: boolean } = {};


  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;
  public collaboratorsDataSource: { [key: string]: string | Date }[] = [];

  private formValue: { [key: string]: any } = { validOnly: true };

  @ViewChild('projectFilter') projectFilterForm: EngDynamicFormsComponent;

  public get isOpen(): boolean {
    return !!this.formValue && !!Object.values(this.formValue).find(a => a !== null && a !== undefined && a !== '');

  }

  public formFilterConfig: FormContainerConfig = {
    showSubmit: true,
    submitLabel: 'CERCA',
    submitIcon: 'search',
    submitCallback: (f: NgForm) => {
      if (this.showCollaboratorsMangament && !!this.opType && this.signUpObject !== null) {
        this.signUpObject = {};
      }
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
      if (this.showCollaboratorsMangament && !!this.opType && this.signUpObject !== null) {
        this.signUpObject = {};
      }
      this.projectFilterForm.f.setValue({ validOnly: true });
      setTimeout(() => {

        this.sortByFilter = 'utente.cognome';
        this.sortByHeaderFilter = 'cognome';
        this.sortOrder = 'asc';
        this.pageIndex = 0;
        this.formValue = JSON.parse(JSON.stringify(f.form.value));
        this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, { validOnly: true }, this.sortOrder);
      }, 150)
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
      sortActive: true,
      definition: 'dtRange',
      header: 'Periodo collaborazione',
      type: 'list',
      dateFormat: 'dd/MM/y',
      span: 2
    },

    {
      sortActive: true,
      definition: 'flgCoordStr',
      header: 'Coordinatore',
      span: 1
    },
    {
      definition: 'action',
      showOnlyIf: 'valid',
      header: '',
      type: 'actions',
      actions: [{
        icon: 'delete',
        event: 'delete',
        label: 'Rimuovi',
        acl: 'myextranet.progetti',
        permissions: ['gestisci'],
        showOnlyIf: 'valid'
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
    private comService: CommunicationService,
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
            { label: 'Lista Collaboratori' }
          )
        };
        this.myPortalService.getContent(this.projectId, false).then((val: Models.MyPortalEvent) => {
          this.project = val;

          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'MyExtranet', url: `backoffice` },
              { label: 'Progetti', url: `backoffice/projects` },
              { label: `Lista Collaboratori al progetto ${this.project.titolo}` }
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
              },
              { key: 'azienda', type: 'textfield', label: 'Azienda' },
              {
                key: 'flgCoordStr', type: 'select', label: 'Tipo collaboratore', data: {
                  "values": [
                    {
                      "label": "Tutti",
                      "value": null
                    },
                    {
                      "label": "Solo coordinatori",
                      "value": true
                    },
                    {
                      "label": "Solo collaboratori e non coordinatori",
                      "value": false
                    }
                  ]
                },
                "dataSrc": "values",
              },
              { key: 'validOnly', type: 'checkbox', label: ' Mostra solo validi', defaultValue: true }

            ]
          };
          setTimeout(() => {
            const filterFromHistory = this.filterHistory.getSavedData();
            this.formValue = { validOnly: true };
            this.projectFilterForm.f.setValue({ validOnly: true });

            if (!!filterFromHistory) {

              const paginataor = filterFromHistory.paginator;
              const sort = filterFromHistory.sort;
              this.formValue = this.projectFilterForm.f.value;
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
    this.projectFilterForm.formReadyEvent.subscribe(val => {
      if (!!this.formValue) this.projectFilterForm.f.form.patchValue(this.formValue);
    });
  }

  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    
    let url = `/collaboratore-progetto/collaboratori?_page=${pageindex}&_pageSize=${pagesize}&idProgetto=${this.projectId}`;

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


      if (this.showCollaboratorsMangament && !!this.opType && this.signUpObject !== null) {
        switch (this.opType) {
          case 'ImpostaCoordinatore': {
            const coordinatori: Models.CollaboratoreDTO[] = response.records.filter((vs: Models.CollaboratoreDTO) => !!vs.flgCoord);
            coordinatori.forEach(coordinatore => {
              if (this.signUpObject[coordinatore.idCollab] === null || this.signUpObject[coordinatore.idCollab] === undefined) {
                this.signUpObject[coordinatore.idCollab] = true;
              }
            });
            break;
          }
        }
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
      case 'delete': {
        const dialogRef = this.dialog.open(GeneralConfirmComponent, {
          minWidth: '400px',
          data: {
            title: `Conferma annullamento`,
            description: `Conferma l'annullamento della collborazione del utente ${event.element.cognome} ${event.element.nome}  al progetto: ${this.project.titolo}.`
          }
        });
        dialogRef.afterClosed().subscribe(result => {
          if (!!result) {
            this.isLoading = true;
            this.projectsService.cancelCollaboration(event.element.idCollab, false).then(resp => {
     
              this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
            }).catch(err => {
              this.isLoading = false;
            });
          }
        });

        break;
      }
      case 'selection': {
        const selectCahnge: SelectionChange<any> = event.element;
        selectCahnge.added.forEach(rowVal => this.changeSignupValue(rowVal, true));
        selectCahnge.removed.forEach(rowVal => this.changeSignupValue(rowVal, false));
        break;
      }
    }
  }

  private changeSignupValue: ((row: any, status: boolean) => void) = (rowVal, status) => {
    if (!!this.signUpObject && rowVal.idCollab !== null && rowVal.idCollab !== undefined) {
      this.signUpObject[rowVal.idCollab] = status;
    }
  }
  saveCollaboratorsActions(): void {

    if (!this.opType || this.opType === '') {
      return null;
    }
    this.isLoading = true;
    let operationDataObject: { [key: number]: boolean } = {};
    switch (this.opType) {
      default: {
        operationDataObject = this.signUpObject;
        break;
      }
    }
  


    this.projectsService.postMassiveOperation(this.opType, operationDataObject).then(resp => {
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
      this.showCollaboratorsMangament = false;
      this.isLoading = false;
      this.signUpObject = {};
   
      this.changeHeader();
    }).catch(err => {
      this.isLoading = false;
      this.showCollaboratorsMangament = false;
      this.signUpObject = {};
      console.error(err);
    });

  }


  public changeHeader(withSelect: boolean = false): void {
    if (withSelect) {
      this.tableHeader = [
        {
          definition: 'cognome',
          header: 'Cognome',
          span: 3
        },
        {
          definition: 'nome',
          header: 'Nome',
          span: 3
        },
        {
          definition: 'denominazioneEnteAzienda',
          header: 'Ente o Azienda',
          span: 2
        },
        {
          definition: 'flgCoordStr',
          header: 'Coordinatore',
          span: 1
        },
        {
          definition: 'select',
          type: 'select',
          header: '',
          span: 1,
          showOnlyIf: 'valid'
        },
      ];

      if (this.showCollaboratorsMangament && !!this.opType && this.signUpObject !== null) {
        switch (this.opType) {
          case 'ImpostaCoordinatore': {
            const coordinatori: Models.CollaboratoreMyExtranet[] = (this.collaboratorsDataSource as any).filter((vs: Models.CollaboratoreMyExtranet) => !!vs.fullData.flgCoord);
            coordinatori.forEach(coordinatore => {
              if (this.signUpObject[coordinatore.idCollab] === null || this.signUpObject[coordinatore.idCollab] === undefined) {
                this.signUpObject[coordinatore.idCollab] = true;
              }
            });
            break;
          }

        }
      }

    } else {
      this.tableHeader = [
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
          span: 3
        },
        {
          definition: 'denominazioneEnteAzienda',
          header: 'Ente o Azienda',
          span: 2
        }, {
          sortActive: true,
          definition: 'dtRange',
          header: 'Periodo collaborazione',
          type: 'list',
          dateFormat: 'dd/MM/y',
          span: 2
        },
        {
          sortActive: true,
          definition: 'flgCoordStr',
          header: 'Coordinatore',
          span: 1
        },
        {
          definition: 'action',
          showOnlyIf: 'valid',
          header: '',
          type: 'actions',
          actions: [{
            icon: 'delete',
            event: 'delete',
            label: 'Rimuovi',
            acl: 'myextranet.progetti',
            permissions: ['gestisci'],
            showOnlyIf: 'valid'
          }
          ],
          span: 1
        }
      ];
    }

  }

  sendCommToColl(): void {
    const dialogRef = this.dialog.open(GeneralMailComponent, {
      minWidth: '400px',
      data: {
        title: 'Invia Comunicazione ai Collaboratori', description: `Progetto : ${this.project.titolo}`,
        filter: {
          nome: !!this.formValue && this.formValue.nome ? this.formValue.nome : null,
          cognome: !!this.formValue && this.formValue.cognome ? this.formValue.cognome : null,
          ente: !!this.formValue && this.formValue.idEnte && this.formValue.idEnte.label ? this.formValue.idEnte.label : null,
          coordinatore: !!this.formValue && this.formValue.flgCoordStr == true ? 'Si' : !!this.formValue && this.formValue.flgCoordStr == false ? 'No' : null
        }
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        const com: Models.BaseCommunication = {
          titolo: result.oggetto,
          messaggio: result.messaggio,
          idContenuto: this.projectId,
        };
        this.isLoading = true;
        this.comService.postComunicationToCollabByFilter(com, this.formValue).then(() => {
          this.isLoading = false;
        }).catch(err => {
          this.isLoading = false;
          console.error(err);
        });
      }

    });
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
      case 'dtRange': {
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
