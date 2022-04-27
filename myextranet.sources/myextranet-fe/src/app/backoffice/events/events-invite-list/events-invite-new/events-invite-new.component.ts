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
import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonService, DataTableService, EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';
import { Models } from 'src/app/models/model';
import { CommunicationService } from 'src/app/services/communication.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { GeneralListModalComponent } from 'src/app/shared/modals/general-list-modal/general-list-modal.component';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-events-invite-new',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './events-invite-new.component.html',
  styleUrls: ['./events-invite-new.component.scss']
})
export class EventsInviteNewComponent implements OnInit {


  constructor(
    private route: ActivatedRoute,
    private myPortalService: MyportalService,
    private router: Router,
    public dialog: MatDialog,
    private buttonService: ButtonService,
    private tableService: DataTableService,
    private commService: CommunicationService,
  ) {

    buttonService.registerCallback('openModal', () => {
      this.openDialog();
    });
    this.tableService.setCallback(['usersInvite', 'tools', 'select'], (row) => {
      this.dialog.closeAll();
      this.formRef.f.setValue({ destinatario: `${row.nome} ${row.cognome}` });
      this.formRef.f.setValue({ indirizzo: `${row.email}` });
    });

    this.tableService.setResponseHandler('usersInvite', (res) => {

      const respbody = res.body;
      respbody.records = respbody.records.map(v => {
        return {
          ...v,
          denominazione: [v.nome, v.cognome].join(' '),
          enteAzienda: v.ente && v.ente.denominazione ? v.ente.denominazione : v.azienda
        }
      })
      return respbody;
    });
  }

  @ViewChild(EngDynamicFormsComponent) formRef: EngDynamicFormsComponent;

  public isLoading = false;
  public breadcrumbs = null;
  public eventId: string = null;

  public event: Models.MyPortalEvent = null;

  public formJson: IForm = {
    components: [
      {
        type: 'htmlelement',
        content: '<h2>Inserimento nuovo invito all&#39;evento</h2>'
      }, {
        type: 'htmlelement',
        content: `<p>Inserire i dati del destinatario o caricali tramite la ricerca utente</p>`
      },
      {
        key: 'columns',
        label: 'Columns',
        type: 'columns',
        readOnly: false,
        columns: [
          {
            components: [
              {
                key: 'destinatario',
                label: 'Destinatario',
                hidden: false,
                disabled: false,
                type: 'textfield',
                validate: {
                  custom: '',
                  required: true,
                  maxLength: 256
                },
                input: true,
                suffix: '',
                defaultValue: null,
                data: {}
              }
            ]
          },
          {
            components: [
              {
                key: 'indirizzo',
                label: 'Indirizzo',
                hidden: false,
                disabled: false,
                type: 'textfield',
                validate: {
                  custom: '',
                  required: true,
                  maxLength: 256
                },
                input: true,
                suffix: '',
                defaultValue: null,
                data: {}
              }
            ]
          },
          {
            components: [
              {
                key: 'openModal',
                label: 'Ricerca Utente',
                action: 'callback',
                hidden: false,
                disabled: false,
                type: 'button',
                input: true,
                suffix: '',
                defaultValue: null,
                data: {},
                validate: {
                  custom: ''
                }
              }
            ]
          }
        ],
        suffix: '',
        hidden: false,
        defaultValue: null,
        validate: {
          custom: ''
        },
        input: true,
        data: {}
      },
      {
        key: 'columns2',
        label: 'Columns 2',
        type: 'columns',
        columns: [
          {
            components: [
              {
                type: 'textfield',
                label: 'Titolo',
                key: 'titolo',
                validate: {
                  required: true,
                  custom: ''
                },
                suffix: '',
                hidden: false,
                defaultValue: null,
                input: true,
                data: {}
              },
              {
                type: 'textarea',
                label: 'Testo del messaggio',
                key: 'messaggio',
                validate: {
                  required: true,
                  custom: ''
                },
                suffix: '',
                hidden: false,
                defaultValue: null,
                input: true,
                data: {}
              }
            ]
          }
        ],
        suffix: '',
        hidden: false,
        defaultValue: null,
        validate: {
          custom: ''
        },
        input: true,
        data: {}
      }
    ],
  };


  public formSubmit: FormContainerConfig = {

    showSubmit: true,
    submitLabel: 'Invia',
    submitIcon: 'email',
    submitCallback: (f: NgForm) => {
      this.isLoading = true;
      const comm: Models.BaseCommunication = f.form.value;
      comm.idEvento = this.eventId;
      this.commService.postInvito(comm).then(a => {
        if (a && a.status === 'OK') {
          this.router.navigate(['backoffice/events/invite-list'], { queryParams: { eventId: this.eventId } });
        }
        else throw Error(a.message || 'Error during message insertion');

      }).catch(err => {
        console.error(err);
      })
    },
    showReset: true,
    resetIcon: 'clear',
    resetLabel: 'CANCELLA',
  };

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(params => {
      if (params.has('eventId')) {
        this.eventId = params.get('eventId');
        this.breadcrumbs = {
          base: '',
          entries: new Array<IBreadcrumbsEntry>(
            { label: 'MyExtranet', url: `backoffice` },
            { label: 'Eventi', url: `backoffice/events` },
            { label: 'Lista Inviti', url: `backoffice/events/invite-list?eventId=${this.eventId}` },
            { label: 'Nuovo invito' }
          )
        };

        this.myPortalService.getContent(this.eventId, false).then((val: Models.MyPortalEvent) => {
          this.event = val;
          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'MyExtranet', url: `backoffice` },
              { label: 'Eventi', url: `backoffice/events` },
              { label: `Lista Inviti all'evento : ${this.event.titolo}`, url: `backoffice/events/invite-list?eventId=${this.eventId}` },
              { label: 'Nuovo invito' }
            )
          };
        }).catch(err => {
          this.eventId = null;
          this.event = null;
          console.error(err);
        });
      }
    });

  }

  public openDialog(): void {
    const dialogRef = this.dialog.open(GeneralListModalComponent, {
      minWidth: 600,
      data: {
        label: 'Ricerca utente',
        form: {
          components: [
            {
              type: 'dataTable',
              key: 'usersInvite',
              dataSrc: 'url',
              data: {
                url: `/utente?flgArchived=0&_page=$pageNum&&_orderBy=cognome&&_orderDir=$sortDirection&&_pageSize=$pageSize&&$filter`,
                values: [
                ],
                pagination: {
                  sizeOptions: [
                    10, 20, 30
                  ],
                  size: 10
                },
                columns: [
                  {
                    value: 'cognome',
                    label: 'Cognome'
                  },
                  {
                    value: 'nome',
                    label: 'Nome'
                  },
                  {
                    value: 'enteAzienda',
                    label: 'Ente o azienda',
                  }, {

                    value: 'tools',
                    label: '',
                    style: {
                      align: 'center'
                    },
                    buttons: [
                      {
                        label: 'Seleziona',
                        style: 'icon',
                        icon: 'save_alt',
                        action: 'select',
                        color: 'red'
                      },
                    ]
                  }
                ],
                filter: {
                  components: [

                    { key: 'cognome', type: 'textfield', label: 'Cognome' },
                    { key: 'nome', type: 'textfield', label: 'Nome' },
                    {
                      key: 'idEnte',
                      type: 'select',
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
                    }
                  ]
                }
              },
              suffix: '',
              hidden: false,
              defaultValue: null,
              validate: {
                custom: ''
              },
              input: true
            }
          ]
        },
        renderer: [
          {
            key: 'ente',
            r_function: (v) => {
              if (v && v.denominazione) {
                return v.denominazione;
              }
              else { return null; }
            }
          }],

      }
    });

    dialogRef.afterClosed().subscribe(result => {

    });
  }



}
