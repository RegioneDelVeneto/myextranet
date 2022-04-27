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
import { ChangeDetectorRef, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonService, DataTableService, IForm } from '@eng/morfeo';
import { Models } from 'src/app/models/model';
import { MyportalService } from 'src/app/services/myportal.service';
import { ProjectsService } from 'src/app/services/projects.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { GeneralListModalComponent } from 'src/app/shared/modals/general-list-modal/general-list-modal.component';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-collaborator-form',
  templateUrl: './collaborator-form.component.html',
  styleUrls: ['./collaborator-form.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class CollaboratorFormComponent implements OnInit {

  private selectedUserId: number = null;

  get disabledSend(): boolean {
    return !!!this.selectedUserId;
  }

  public formSettings: Models.GenericUserFormSetting = {
    enteAziendaArray: [],
    ro: true,
    validators: {},
    enteAziendaRender: 2,
    data: {
      nome: ``,
      cognome: ``,
      codFiscale: ``,
      email: ``,
      enteAzienda: ``,
    }
  };
  public breadcrumbs = null;
  public projectId: string = null;
  public userid = null;
  public project: Models.MyPortalContent = null;

  public isEdit: boolean = false;

  public signedId: number = null;

  public signed: Models.iscrittoDTO = null;


  public formJsonText: IForm = {
    components: [
      {
        type: 'htmlelement',
        content: !this.isEdit ? '<h2>Inserimento nuovo collaboratore al progetto</h2>' : '<h2>Modifica collaboratore al progetto</h2>'
      }
    ]
  };

  public formJson: IForm = {
    components: [

      {
        key: 'columns1',
        label: 'Columns2',
        type: 'columns',
        readOnly: true,
        columns: [
          {
            components: [
              {
                key: 'nome',
                label: 'Nome',
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
              },
              {
                key: 'utente',
                label: 'idUtente',
                hidden: true,
                disabled: false,
                type: 'textfield',
                validate: null,
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
                key: 'cognome',
                label: 'Cognome',
                placeholder: 'Cognome',
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
          }, {
            components: [{
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
            },]
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


  public submit: IForm = {
    components: []
  };

  public isLoading = false;

  constructor(
    private route: ActivatedRoute,
    private myPortalService: MyportalService,
    private router: Router,
    public dialog: MatDialog,
    private buttonService: ButtonService,
    private tableService: DataTableService,
    private projectsService: ProjectsService,
    private changeDetector: ChangeDetectorRef,
  ) {

    buttonService.registerCallback('openModal', () => {
      this.openDialog();
    });
    this.tableService.setCallback(['usersMembersNewOrEdit', 'tools', 'select'], (row) => {
      this.dialog.closeAll();
      this.setValeuFromUserSearch(row);

    });

    this.tableService.setResponseHandler('usersMembersNewOrEdit', (res) => {

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

  ngOnInit(): void {
    this.isLoading = true;
    this.route.queryParamMap.subscribe(params => {
      if (params.has('projectId')) {
        this.projectId = params.get('projectId');
        this.breadcrumbs = {
          base: '',
          entries: new Array<IBreadcrumbsEntry>(
            { label: 'MyExtranet', url: `backoffice` },
            { label: 'Progetti', url: `backoffice/projects` },
            { label: 'Lista Collaboratori', url: `backoffice/projects/collaborators-list?projectId=${this.projectId}` },
            { label: 'Inserisci Collaboratore' }
          )
        };
        this.myPortalService.getContent(this.projectId, false).then((val: Models.MyPortalEvent) => {
          this.project = val;
          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'MyExtranet', url: `backoffice` },
              { label: 'Progetti', url: `backoffice/projects` },
              { label: `Lista Collaboratori al progetto ${this.project.titolo}`, url: `backoffice/projects/collaborators-list?projectId=${this.projectId}` },
              { label: 'Inserisci Collaboratore' }
            )
          };
          if (params.has('signedId')) {
            this.isEdit = true;
            this.signedId = parseInt(params.get('signedId'), 10);
            this.projectsService.getCollaboratoreById(parseInt(params.get('signedId'), 10)).then(signed => {
              this.isLoading = false;
              this.signed = signed;
              this.breadcrumbs = {
                base: '',
                entries: new Array<IBreadcrumbsEntry>(
                  { label: 'MyExtranet', url: `backoffice` },
                  { label: 'Progetti', url: `backoffice/projects` },
                  { label: `Lista Collaboratori al progetto ${this.project.titolo}`, url: `projects/collaborators-list?projectId=${this.projectId}` },
                  { label: 'Modifica Collaboratore' }
                )
              };
            });
          }
          if (!this.isEdit) {
            this.isLoading = false;
          }
        }).catch(err => {
          this.projectId = null;
          this.signed = null;
          this.isEdit = false;
          this.project = null;
          this.isLoading = false;
          console.error(err);
        });
      }
    });
  }

  ngAfterViewInit(): void {
    this.changeDetector.detectChanges();
  }


  public reset(): void {

  }

  public sendData(): void {
    this.isLoading = true;
    const comm: Models.IscrizioneCollaboratoreDTO = {
      utente: { idUtente: this.selectedUserId },
      idProgetto: this.projectId,
    };

    this.projectsService.postOrPatchCollaboratoreProgetto(comm, false, false).then(a => {
      if (a && a.status === 'OK') {
        this.router.navigate(['backoffice/projects/collaborators-list'], { queryParams: { projectId: this.projectId } });
        this.isLoading = false;
      }
      else { throw Error(a.message || 'Error during message insertion'); }
    }).catch(err => {
      console.error(err);
      this.isLoading = false;
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
              key: 'usersMembersNewOrEdit',
              dataSrc: 'url',
              data: {
                url: `/utente?flgArchived=0&_page=$pageNum&&_orderBy=nome&&_orderDir=$sortDirection&&_pageSize=$pageSize&&$filter`,
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
                    value: 'denominazione',
                    label: 'Denominazione'
                  },
                  {
                    value: 'enteAzienda',
                    label: 'Ente o azienda'
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
                    { key: 'nome', type: 'textfield', label: 'Nome' },
                    { key: 'cognome', type: 'textfield', label: 'Cognome' },
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


  setValeuFromUserSearch(row) {
    this.selectedUserId = row.idUtente;
    this.formSettings = {
      enteAziendaArray: [],
      ro: true,
      validators: {},
      enteAziendaRender: 2,
      data: {
        nome: `${row.nome}`,
        cognome: `${row.cognome}`,
        codFiscale: `${row.codFiscale}`,
        email: `${row.email}`,
        enteAzienda: row.ente && row.ente.denominazione ? row.ente.denominazione : row.azienda ? row.azienda : null,
      }
    }
  }

  public loadData(): void {
    if (!!this.signed.utente && !!this.signed.utente.idUtente) {
      this.setValeuFromUserSearch(this.signed.utente);
    }
  }

}
