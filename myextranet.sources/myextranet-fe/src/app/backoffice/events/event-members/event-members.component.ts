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
import { DatePipe } from '@angular/common';
import { AfterViewInit, Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { SortDirection } from '@angular/material/sort';
import { ActivatedRoute, Router } from '@angular/router';
import { EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';

import { AlertService } from 'src/app/services/alert.service';
import { CommunicationService } from 'src/app/services/communication.service';
import { EventsService } from 'src/app/services/events.service';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { GeneralConfirmComponent } from 'src/app/shared/modals/general-confirm/general-confirm.component';
import { GeneralMailComponent } from 'src/app/shared/modals/general-mail/general-mail.component';
import { environment } from 'src/environments/environment';
import { Models } from '../../../models/model';


@Component({
  selector: 'app-event-members',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './event-members.component.html',
  styleUrls: ['./event-members.component.scss']
})
export class EventMembersComponent implements OnInit, AfterViewInit {


  public sortByFilter: string = 'cognome';
  public sortByHeaderFilter: string = 'denomination';
  public sortOrder: SortDirection = 'asc';

  
  public opType: string = null;

  public get saveLabel() {
    switch (this.opType) {
      case 'ImpostaPresenze': {

        return 'Salva persenze';
      }
      case 'ImpostaRelatore': {
        return 'Salva relatori';
      }
      case 'InviaAttestati': {
        return 'Invia attestati';
      }
      case 'InviaRichiestaCompilazioneQuestionario': {
        return 'Invia richiesta compilazione questionario';
      }
      default: return 'Salva';
    }
  }
  public eventId: string = null;

  public event: Models.MyPortalEvent = null;

  public formJson: IForm = null;

  public breadcrumbs = null;

  public showSignUpMangament = false;

  public signUpObject: { [key: string]: boolean } = {};


  public lastSearchFilterValue: { [key: string]: any };

  public isLoading = false;

  public preselectedRows: Models.iscrittoDTO[] = [];

  public inviaAttestatiActive: boolean = false;

  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;
  public eventsDataSource: { [key: string]: string | Date }[] = [];


  private formValue: { [jkey: string]: any } = null;


  public get isOpen(): boolean {
    return !!this.formValue && !!Object.values(this.formValue).find(a => a !== null && a !== undefined && a !== '');
  }


  public formFilterConfig: FormContainerConfig = {
    showSubmit: true,
    submitLabel: 'CERCA',
    submitIcon: 'search',
    submitCallback: (f: NgForm) => {
      if (this.showSignUpMangament && !!this.opType && this.signUpObject !== null) {
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
      if (this.showSignUpMangament && !!this.opType && this.signUpObject !== null) {
        this.signUpObject = {};
      }
      this.sortByFilter = 'cognome';
      this.sortByHeaderFilter = 'denomination';
      this.sortOrder = 'asc';
      this.pageIndex = 0;
      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.getDataFromService(this.pageIndex + 1, this.pageSize,  this.sortByFilter, f.form.value, this.sortOrder);
    }
  };

  public tableHeader: Models.TableHeader[] = [

    {
      sortActive: true,
      definition: 'denomination',
      header: 'Nome e Cognome',
      span: 3
    },
    {
      definition: 'enteAzienda',
      header: 'Ente o Azienda',
      span: 3
    },
    {
      sortActive: true,
      definition: 'email',
      header: 'Email',
      span: 3
    },
    {
      sortActive: true,
      definition: 'dtIscrizione',
      header: 'Data di iscrizione',
      span: 2
    },
    {
      sortActive: true,
      definition: 'flgPartecipLoco',
      header: `Presenza all'evento`,
      span: 2
    },
    {
      sortActive: true,
      definition: 'flgRelatore',
      header: 'Relatore',
      span: 1
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [{
        icon: 'edit',
        event: 'edit',
        label: 'Modifica',
        acl: 'myextranet.eventi',
        permissions: ['gestisci'],
        hiddenIf: 'utente.idUtente'
      }, {
        icon: 'print',
        event: 'attestatoPrint',
        label: 'Stampa attestato',
        acl: 'myextranet.eventi',
        permissions: ['visualizza']
      },
      {
        icon: 'delete',
        event: 'deleteSubscription',
        label: 'Annulla iscrizione',
        acl: 'myextranet.eventi',
        permissions: ['gestisci']
      }
      ],
      span: 1
    }
  ];

  @ViewChild('membersForm') memForm: EngDynamicFormsComponent;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private myPortalService: MyportalService,
    private commService: CommunicationService,
    public dialog: MatDialog,
    private datePipe: DatePipe,
    private myextranetTableSrv: MyextTableService,
    private alertService: AlertService,
    private eventService: EventsService,
    private filterHistory: FiltersHistoryService,

  ) {

  }

  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    
    let url = `/iscritto-evento?_page=${pageindex}&_pageSize=${pagesize}&idEvento=${this.eventId}`;

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


      if (this.showSignUpMangament && !!this.opType && this.signUpObject !== null) {
        switch (this.opType) {
          case 'ImpostaRelatore': {
            const relatori: Models.iscrittoDTO[] = response.records.filter((vs: Models.iscrittoDTO) => !!vs.flgRelatore);
            relatori.forEach(relatore => {
              if (this.signUpObject[relatore.idIscritto] === null || this.signUpObject[relatore.idIscritto] === undefined) {
                this.signUpObject[relatore.idIscritto] = true;
              }
            });
            break;
          }
          case 'ImpostaPresenze': {
            const presenti: Models.iscrittoDTO[] = response.records.filter((vs: Models.iscrittoDTO) => !!vs.flgPartecipLoco);
            presenti.forEach(presente => {
              if (this.signUpObject[presente.idIscritto] === null || this.signUpObject[presente.idIscritto] === undefined) {
                this.signUpObject[presente.idIscritto] = true;
              }
            });
            break;
          }
        }
      }

      response.records = response.records.map((res: Models.iscrittoDTO) =>
        ({
          ...res,
          enteAzienda: res.ente !== null && res.ente !== undefined && !!res.ente.denominazione ? res.ente.denominazione : res.azienda,
          flgRelatore: res.flgRelatore ? 'Si' : 'No',
          dtIscrizione: res.dtIscrizione ? this.datePipe.transform(res.dtIscrizione, 'dd/MM/yyyy') : null,
          dtInvioAttestato: res.dtInvioAttestato ? this.datePipe.transform(res.dtInvioAttestato, 'dd/MM/yyyy') : null,
          hasTakenPartInEvent: !!res.flgPartecipLoco || !!res.flgPartecipRemoto,
          dtRichQuestionario: res.dtRichQuestionario ? this.datePipe.transform(res.dtRichQuestionario, 'dd/MM/yyyy') : null,
          flgPartecipLoco: res.flgPartecipLoco ? 'Di Persona' : res.flgPartecipRemoto ? 'Da remoto' : null,
          denomination: [res.nome, res.cognome].join(' '),
        })
      );

      this.eventsDataSource = Array.from(response.records);

      this.filterHistory.register(this.formValue, { length: this.length, pageSize: this.pageSize, pageIndex: this.pageIndex }, { sortByFilter: this.sortByFilter, sortByHeader: this.sortByHeaderFilter, sortOrder: this.sortOrder });

      this.isLoading = false;

    }).catch(err => {
      this.isLoading = false;
    });
  }

  ngOnInit(): void {
    this.alertService.returnProgressBarObjStatus()['Invio attestati'].subscribe(val => {
      this.inviaAttestatiActive = val.status;
    }
    );
    this.route.queryParamMap.subscribe(queryParams => {

      const filterFromHistory = this.filterHistory.getSavedData();


      if (queryParams.has('eventId')) {
        this.eventId = queryParams.get('eventId');
        this.breadcrumbs = {
          base: '',
          entries: new Array<IBreadcrumbsEntry>(
            { label: 'MyExtranet', url: `backoffice` },
            { label: 'Eventi', url: `backoffice/events` },
            { label: 'Lista Iscritti' }
          )
        };
        this.myPortalService.getContent(this.eventId, false).then((val: Models.MyPortalEvent) => {
          this.event = val;

          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'MyExtranet', url: `backoffice` },
              { label: 'Eventi', url: `backoffice/events` },
              { label: `Lista Iscritti all'evento ${this.event.titolo}` }
            )
          };
          if (!!filterFromHistory && !!filterFromHistory.filter) {
          
            const paginataor = filterFromHistory.paginator;
            const sort = filterFromHistory.sort;
            this.formValue = filterFromHistory.filter;
            this.pageSize = paginataor.pageSize;
            this.sortByFilter = sort.sortByFilter;
            this.sortOrder = sort.sortOrder as SortDirection;
            this.sortByHeaderFilter = sort.sortByHeader;
            this.length = paginataor.length;
            this.lastSearchFilterValue = filterFromHistory.filter;
          }
          this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);



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
              { key: 'flgRelatoreStr', type: 'checkbox', label: 'Relatore' }
            ]
          };
        }).catch(err => {
          this.eventId = null;
          this.event = null;
          console.error(err);
        });
      }

    });

  }

  ngAfterViewInit(): void {
    this.memForm.formReadyEvent.subscribe(val => {
      if (!!this.formValue) this.memForm.f.form.patchValue(this.formValue);
    })

  }

  saveSignUp(): void {

    if (!this.opType || this.opType === '') {
      return null;
    }
    this.isLoading = true;
    let operationDataObject: { [key: number]: boolean } = {};
    switch (this.opType) {
      case 'InviaAttestati': case 'InviaRichiestaCompilazioneQuestionario': {
        Object.keys(this.signUpObject).forEach(key => {
          if (!!this.signUpObject[key]) {
            operationDataObject[key] = true;
          }
        });
        break;
      }
      default: {
        operationDataObject = this.signUpObject;
        break;
      }
    }

    if (this.opType === 'InviaAttestati') {
      this.inviaAttestatiActive = true;
      this.commService.postMassiveOperationProgress(this.opType, operationDataObject);
      this.showSignUpMangament = false;
      this.isLoading = false;
      this.signUpObject = {};
      this.changeHeader();

    }
    else {

      this.commService.postMassiveOperation(this.opType, operationDataObject).then(resp => {
        this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
        this.showSignUpMangament = false;
        this.isLoading = false;
        this.signUpObject = {};        
        this.changeHeader();
      }).catch(err => {
        this.isLoading = false;
        this.showSignUpMangament = false;
        this.signUpObject = {};
        console.error(err);
      });
    }
  }


  printAll(): void {
    window.open(this.commService.getFoglioPresenze(this.eventId), '_blank');
  }

  sendCommToMembers(): void {
    const dialogRef = this.dialog.open(GeneralMailComponent, {
      minWidth: '400px',
      data: { title: 'Invia Comunicazione agli iscritti', description: `Evento : ${this.event.titolo}` }
    });
    dialogRef.afterClosed().subscribe(result => {
      this.opType = null;
      if (result) {
        const com: Models.BaseCommunication = {
          titolo: result.oggetto,
          messaggio: result.messaggio
        };
        this.commService.postCommunicationToAllMembers(com, this.eventId).then(() => {

        }).catch(err => {
          console.error(err);
        });
      }

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
      case 'edit': {
        if (event.element) { this.router.navigate([`/backoffice/events/members/edit`], { queryParams: { signedId: event.element.idIscritto }, queryParamsHandling: 'merge' }); }
        break;
      }
      case 'attestatoPrint': {
        if (event.element && event.element.idIscritto) { window.open(this.commService.getAttestato(this.eventId, event.element.idIscritto), '_blank'); }
        break;
      }
      case 'selection': {
        const selectCahnge: SelectionChange<any> = event.element;
        selectCahnge.added.forEach(rowVal => this.changeSignupValue(rowVal, true));
        selectCahnge.removed.forEach(rowVal => this.changeSignupValue(rowVal, false));
        break;
      }
      case 'deleteSubscription': {
        const dialogRef = this.dialog.open(GeneralConfirmComponent, {
          minWidth: '400px',
          data: {
            title: `Conferma annullamento`,
            description: `Conferma l'annullamento dell'iscrizione all'evento Evento ${this.event.titolo} per l'utente : ${event.element.cognome} ${event.element.nome}`
          }
        });
        dialogRef.afterClosed().subscribe(result => {
          if (!!result) {
            this.isLoading = true;
            this.eventService.deleteIscrittoEvento(event.element.idIscritto, false).then(val => {
              this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);

            });
          }
        });

        break;
      }
    }
  }

  private changeSignupValue: ((row: any, status: boolean) => void) = (rowVal, status) => {
    if (!!this.signUpObject && rowVal.idIscritto !== null && rowVal.idIscritto !== undefined) {
      this.signUpObject[rowVal.idIscritto] = status;
    }
  }

  public changeHeader(withSelect: boolean = false): void {
    if (withSelect) {
      switch (this.opType) {
        case 'InviaRichiestaCompilazioneQuestionario': {

          this.tableHeader = [
            {
              definition: 'denomination',
              header: 'Cognome e Nome',
              span: 3
            },
            {
              definition: 'enteAzienda',
              header: 'Ente o Azienda',
              span: 3
            },
            {
              definition: 'dtIscrizione',
              header: 'Data di iscrizione',
              span: 2
            },
            {
              definition: 'flgPartecipLoco',
              header: `Presenza all'evento`,
              span: 2
            }, {
              definition: 'flgRelatore',
              header: 'Relatore',
              span: 1
            }, {
              definition: 'dtRichQuestionario',
              header: 'Data ultimo invio questionario',
              span: 2,
            },
            {
              definition: 'select',
              type: 'select',
              header: '',
              span: 1,
              showOnlyIf: 'hasTakenPartInEvent'
            },
          ];
          break;
        }
        case 'InviaAttestati': {

          this.tableHeader = [
            {
              definition: 'denomination',
              header: 'Cognome e Nome',
              span: 3
            },
            {
              definition: 'enteAzienda',
              header: 'Ente o Azienda',
              span: 3
            },
            {
              definition: 'dtIscrizione',
              header: 'Data di iscrizione',
              span: 2
            },
            {
              definition: 'flgPartecipLoco',
              header: `Presenza all'evento`,
              span: 2
            }, {
              definition: 'flgRelatore',
              header: 'Relatore',
              span: 1
            }, {
              definition: 'dtInvioAttestato',
              header: 'Data ultimo invio attestato',
              span: 2,
            },
            {
              definition: 'select',
              type: 'select',
              header: '',
              span: 1,
              showOnlyIf: 'hasTakenPartInEvent'
            },
          ];
          break;
        }
        default: {

          this.tableHeader = [

            {
              definition: 'denomination',
              header: 'Cognome e Nome',
              span: 3
            },
            {
              definition: 'enteAzienda',
              header: 'Ente o Azienda',
              span: 3
            },
            {
              definition: 'dtIscrizione',
              header: 'Data di iscrizione',
              span: 2
            },
            {
              definition: 'flgPartecipLoco',
              header: `Presenza all'evento`,
              span: 2
            },
            {
              definition: 'flgRelatore',
              header: 'Relatore',
              span: 1
            },
            {
              definition: 'select',
              type: 'select',
              header: '',
              span: 1
            },
          ];
          break;
        }
      }

      if (this.showSignUpMangament && !!this.opType && this.signUpObject !== null) {
        switch (this.opType) {
          case 'ImpostaRelatore': {
            const relatori: { idIscritto: number, flgRelatoreStr: 'true' | 'false' }[] = <any>this.eventsDataSource.filter((vs: { flgRelatoreStr: 'true' | 'false' }) => !!vs.flgRelatoreStr && vs.flgRelatoreStr === 'true');
            relatori.forEach(relatore => {
              if (this.signUpObject[relatore.idIscritto] === null || this.signUpObject[relatore.idIscritto] === undefined) {
                this.signUpObject[relatore.idIscritto] = true;
              }
            });
            break;
          }
          case 'ImpostaPresenze': {
            const presenti: { idIscritto: number, flgPartecipLocoStr: 'true' | 'false' }[] = <any>this.eventsDataSource.filter((vs: { flgPartecipLocoStr: 'true' | 'false' }) => !!vs.flgPartecipLocoStr && vs.flgPartecipLocoStr === 'true');
            presenti.forEach(presente => {
              if (this.signUpObject[presente.idIscritto] === null || this.signUpObject[presente.idIscritto] === undefined) {
                this.signUpObject[presente.idIscritto] = true;
              }
            });
            break;
          }
        }
      }


    }
    else {
      this.tableHeader = [
        {
          sortActive: true,
          definition: 'denomination',
          header: 'Cognome e Nome',
          span: 3
        },
        {
          definition: 'enteAzienda',
          header: 'Ente o Azienda',
          span: 3
        },
        {
          sortActive: true,
          definition: 'email',
          header: 'Email',
          span: 3
        },
        {
          sortActive: true,
          definition: 'dtIscrizione',
          header: 'Data di iscrizione',
          span: 2
        },
        {
          sortActive: true,
          definition: 'flgPartecipLoco',
          header: `Presenza all'evento`,
          span: 2
        },
        {
          sortActive: true,
          definition: 'flgRelatore',
          header: 'Relatore',
          span: 1
        },
        {
          definition: 'action',
          header: '',
          type: 'actions',
          actions: [{
            icon: 'edit',
            event: 'edit',
            label: 'Modifica',
            acl: 'myextranet.eventi',
            permissions: ['gestisci'],
            hiddenIf: 'utente.idUtente'
          }, {
            icon: 'print',
            event: 'attestatoPrint',
            label: 'Stampa attestato',
            acl: 'myextranet.eventi',
            permissions: ['visualizza']
          },
          {
            icon: 'delete',
            event: 'deleteSubscription',
            label: 'Annulla iscrizione',
            acl: 'myextranet.eventi',
            permissions: ['gestisci']
          }
          ],
          span: 1
        }
      ];
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
      case 'denomination': {
        return 'cognome';
      }
      default: {
        return filterIn;
      }
    }
  }

}
