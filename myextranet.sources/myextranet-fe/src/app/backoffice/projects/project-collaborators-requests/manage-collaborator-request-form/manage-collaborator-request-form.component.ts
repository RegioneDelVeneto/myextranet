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
import { FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonService, DataTableService } from '@eng/morfeo';
import { Models } from 'src/app/models/model';
import { MyportalService } from 'src/app/services/myportal.service';
import { ProjectsService } from 'src/app/services/projects.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { GeneralListModalComponent } from 'src/app/shared/modals/general-list-modal/general-list-modal.component';
import { environment } from 'src/environments/environment';


@Component({
  selector: 'app-manage-collaborator-request-form',
  templateUrl: './manage-collaborator-request-form.component.html',
  styleUrls: ['./manage-collaborator-request-form.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ManageCollaboratorRequestFormComponent implements OnInit {

  public isLoading: boolean = false;

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
  public idCollab: string = null;
  public userid = null;
  public project: Models.MyPortalContent = null;

  public isRo: boolean = false;

  public projectId: string = null;
  public collab: Models.CollaboratoreDTO = null;

  public collStatus: FormGroup = null;
  public stati: { value: number, label: string }[] = [
    { label: 'Richiesta confermata', value: 1 },
    { label: 'Richiesta non confermata', value: 2 }
  ];

  constructor(
    private route: ActivatedRoute,
    private myPortalService: MyportalService,
    private router: Router,
    private formBuilder: FormBuilder,
    public dialog: MatDialog,
    private buttonService: ButtonService,
    private tableService: DataTableService,
    private projectsService: ProjectsService,
    private changeDetector: ChangeDetectorRef,
  ) {

  }

  ngOnInit(): void {
    this.isLoading = true;
    this.collStatus = this.formBuilder.group({
      flgConferma: [null, Validators.required],
      motivation: [null, Validators.maxLength(1000)]
    }, { validators: this.conditionalRequiredForMotivations() });

    this.route.queryParamMap.subscribe(params => {
      if (params.has('idCollab')) {
        this.idCollab = params.get('idCollab');
        // this.isEdit = true;
        this.projectsService.getCollaboratoreById(parseInt(this.idCollab, 10)).then(signed => {
          this.collab = signed;
          this.loadData();
          this.projectId = this.collab.idProgetto;
          if (this.collab.flgConferma !== 0) {
            this.isRo = true;
            this.collStatus = this.formBuilder.group({
              flgConferma: [{ value: this.collab.flgConferma, disabled: true }],
              motivation: [{ value: this.collab.motivConferma, disabled: true }]
            });
          }
          return this.myPortalService.getContent(this.projectId, false);
        })
          .then((val: Models.MyPortalEvent) => {
            this.project = val;
            this.breadcrumbs = {
              base: '',
              entries: new Array<IBreadcrumbsEntry>(
                { label: 'MyExtranet', url: `backoffice` },
                { label: 'Progetti', url: `backoffice/projects` },
                { label: `Lista richieste di collaborazione al progetto ${this.project.titolo}`, url: `backoffice/projects/collaborators-requests-list?projectId=${this.projectId}` },
                { label: 'Gestisci richiesta di collaborazione' }

              )
            };

            this.isLoading = false;
          }).catch(err => {
            this.projectId = null;
            this.collab = null;

            this.isLoading = false;
            this.project = null;
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
      idCollab: parseInt(this.idCollab, 10),
      motivConferma: this.collStatus.get('motivation').value,
      flgConferma: this.collStatus.get('flgConferma').value
    };
    this.projectsService.postOrPatchCollaboratoreProgetto(comm, true, false).then(a => {

      if (a && a.status === 'OK') {
        this.router.navigate(['backoffice/projects/collaborators-requests-list'], { queryParams: { projectId: this.projectId } });
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
    if (!!this.collab.utente && !!this.collab.utente.idUtente) {
      this.setValeuFromUserSearch(this.collab.utente);
    }
  }

  private conditionalRequiredForMotivations = () => (
    fg: FormGroup,
  ): ValidationErrors | null => {
    if (!this.collStatus) return null;
    const controlNotNull = !!fg.get('motivation').value;
    const flgConfermaValue = fg.get('flgConferma').value;
    const requiredActive = !controlNotNull && (flgConfermaValue == 2);
    if (requiredActive) {
      fg.get('motivation').setErrors({ required: true }
      );
    } else {
      fg.get('motivation').setErrors(null);
    }
    return requiredActive ? { required: true } : null;

  }

}
