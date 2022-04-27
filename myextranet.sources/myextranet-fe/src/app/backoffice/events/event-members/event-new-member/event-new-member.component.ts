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
import { AfterViewInit, ChangeDetectorRef, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { ButtonService, DataTableService } from '@eng/morfeo';
import { debounceTime, distinctUntilChanged, filter } from 'rxjs/operators';
import { Models } from 'src/app/models/model';
import { CommunicationService } from 'src/app/services/communication.service';
import { EnteService } from 'src/app/services/ente.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { GeneralListModalComponent } from 'src/app/shared/modals/general-list-modal/general-list-modal.component';
import { environment } from 'src/environments/environment';

@Component({
  selector: 'app-event-new-member',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './event-new-member.component.html',
  styleUrls: ['./event-new-member.component.scss']
})
export class EventNewMemberComponent implements OnInit, AfterViewInit {


  constructor(
    private route: ActivatedRoute,
    private myPortalService: MyportalService,
    private router: Router,
    public dialog: MatDialog,
    private buttonService: ButtonService,
    private tableService: DataTableService,
    private commService: CommunicationService,
    private changeDetector: ChangeDetectorRef,
    private fb: FormBuilder,
    private enteService: EnteService,

  ) {


    this.buttonService.registerCallback('openModal', () => {
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
        };
      });
      return respbody;
    });
  }


  public prePartecipStatus: 'PRE' | 'REM' = null;
  public breadcrumbs = null;
  public eventId: string = null;
  public userid = null;
  public event: Models.MyPortalEvent = null;

  public isEdit: boolean = false;
  public isLoading: boolean;

  public signedId: number = null;
  public entiArray: Models.enteDTO[] = [];

  public memberDataForm: FormGroup = this.fb.group({
    selectInsType: [null, Validators.required],
    nome: ['', [Validators.required, Validators.maxLength(150)]],
    cognome: ['', [Validators.required, Validators.maxLength(150)]],
    slectOrg: [null, Validators.required],
    email: ['', [Validators.required, Validators.maxLength(150), Validators.pattern('^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$')]],
    enteAzienda: ['', Validators.maxLength(250)],
    preferenza: ['', Validators.required],
  });


  public isSearchActive: boolean = false;

  public signed: Models.iscrittoDTO = null;

  ngOnInit(): void {
    this.isLoading = true;
    this.loadAllTenants();
    this.memberDataForm.get('enteAzienda').valueChanges.pipe(debounceTime(200),
      filter(entAz => !!this.memberDataForm.get('slectOrg').value && this.memberDataForm.get('slectOrg').value == '1' && !this.isSearchActive),
      distinctUntilChanged()).subscribe(val => {
        const den = !!val && !!val.denominazione ? val.denominazione : val;
        this.enteService.getAutocompleteAsync(25, den, false).toPromise().then(value => {
          this.entiArray = value;
        });
      });
    this.route.queryParamMap.subscribe(params => {
      if (params.has('eventId')) {
        this.eventId = params.get('eventId');
        this.breadcrumbs = {
          base: '',
          entries: new Array<IBreadcrumbsEntry>(
            { label: 'MyExtranet', url: `backoffice` },
            { label: 'Eventi', url: `backoffice/events` },
            { label: 'Lista Iscritti', url: `backoffice/events/members?eventId=${this.eventId}` },
            { label: 'Inserisci iscritto' }
          )
        };
        this.myPortalService.getContent(this.eventId, false).then((val: Models.MyPortalEvent) => {
          this.event = val;
          if (!!this.event.inPresenza && !this.event.inStreaming) {
            this.prePartecipStatus = 'PRE';
          } else if (!this.event.inPresenza && !!this.event.inStreaming) {
            this.prePartecipStatus = 'REM';
          } else {
            this.prePartecipStatus = null;
          }
          this.memberDataForm.patchValue({ preferenza: this.prePartecipStatus });

          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'MyExtranet', url: `backoffice` },
              { label: 'Eventi', url: `backoffice/events` },
              { label: `Lista Iscritti all'evento ${this.event.titolo}`, url: `backoffice/events/members?eventId=${this.eventId}` },
              { label: 'Inserisci iscritto' }
            )
          };

          if (params.has('signedId')) {
            this.isEdit = true;
            this.signedId = parseInt(params.get('signedId'), 10);
            this.commService.getIscrittoById(parseInt(params.get('signedId'), 10)).then(signed => {
              this.signed = signed;
              this.breadcrumbs = {
                base: '',
                entries: new Array<IBreadcrumbsEntry>(
                  { label: 'MyExtranet', url: `backoffice` },
                  { label: 'Eventi', url: `backoffice/events` },
                  { label: `Lista Iscritti all'evento ${this.event.titolo}`, url: `backoffice/events/members?eventId=${this.eventId}` },
                  { label: 'Modifica iscritto' }
                )
              };
              this.loadData();
            });
          } else {
            this.isEdit = false;
            this.isLoading = false;
          }

        }).catch(err => {
          this.eventId = null;
          this.signed = null;
          this.isEdit = false;
          this.event = null;
          console.error(err);
        });
      }
    });

  }

  private loadAllTenants() {
    this.enteService.getAutocomplete(99999, false, false).then(enti => {
      this.entiArray = enti;
    });
  }

  public loadData(): void {
    const preferenzaPartecipazione = !this.signed.flgPartecipPref && !!this.prePartecipStatus ? this.prePartecipStatus : !!this.signed.flgPartecipPref ? this.signed.flgPartecipPref : null;
    this.memberDataForm.patchValue({
      selectInsType: !this.signed.utente || !this.signed.utente.idUtente ? '1' : '2',
      nome: this.signed.nome,
      cognome: this.signed.cognome,
      email: this.signed.email,
      slectOrg: !!this.signed.ente && this.signed.ente.idEnte ? '1' : !!this.signed.azienda ? '2' : '3',
      preferenza: preferenzaPartecipazione,
      enteAzienda: this.signed.ente && this.signed.ente.idEnte ? { idEnte: this.signed.ente.idEnte, denominazione: this.signed.ente.denominazione } : this.signed.azienda ? this.signed.azienda : null,
    });
    this.manageInstTypeChange({ value: !this.signed.utente || !this.signed.utente.idUtente ? '1' : '2', })

  }

  ngAfterViewInit(): void {
    this.changeDetector.detectChanges();
  }



  public sendData(): void {
    const f = this.memberDataForm.getRawValue();
    let comm: Models.IscrizioneEvento;
    if (!!this.userid) {
      comm = {
        utente: !!this.userid ? { idUtente: this.userid } : null,
        idEvento: this.eventId,
        flgPartecipPref: f.preferenza,
        idIscritto: this.isEdit ? this.signedId : null

      };
    } else {
      comm = {
        cognome: f.cognome,
        nome: f.nome,
        email: f.email,
        idEvento: this.eventId,
        flgPartecipPref: f.preferenza,
        idIscritto: this.isEdit ? this.signedId : null,
        azienda: f.slectOrg == '2' && !!f.enteAzienda ? f.enteAzienda : null,
        ente: f.slectOrg == '1' && !!f.enteAzienda && !!f.enteAzienda.idEnte ? { idEnte: f.enteAzienda.idEnte } : null,
      };
    }

    this.commService.postOrPatchIscrizioneEvento(comm, !!this.isEdit).then(a => {
      if (a && a.status === 'OK') {
        this.router.navigate(['backoffice/events/members'], { queryParams: { eventId: this.eventId } });
      }
      else { throw Error(a.message || 'Error during message insertion'); }
    }).catch(err => {
      console.error(err);
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
    this.userid = row.idUtente;
    this.memberDataForm.patchValue({
      nome: `${row.nome}`,
      cognome: `${row.cognome}`,
      email: `${row.email}`,
      slectOrg: row.ente && row.ente.idEnte !== null && row.ente.idEnte !== undefined ? '1' : row.azienda !== null && row.azienda !== undefined ? '2' : '3',
    });
    setTimeout(() => {
      if (row.ente && row.ente.idEnte !== null && row.ente.idEnte !== undefined) {
        this.enteService.getAutocompleteAsync(25, row.ente.denominazione, false).toPromise().then(value => {
          this.entiArray = value;
          this.memberDataForm.patchValue({ enteAzienda: { idEnte: row.ente.idEnte, denominazione: row.ente.denominazione } });
        });

      } else if (row.azienda !== null && row.azienda !== undefined) {
        this.memberDataForm.patchValue({ enteAzienda: row.azienda });
      } else {
        this.memberDataForm.patchValue({ enteAzienda: null });
      }
    }, 10);

  }


  showDenomination(entity: { idEnte: number, denominazione: string }) {
    if (!entity || !entity.idEnte) return null;
    const id = entity.idEnte;
    return id !== null && id !== undefined ? entity.denominazione : null;
  }

  manageInstTypeChange(event: { value: string }) {
    const resetFields = !this.isLoading;
    this.userid = null;
    switch (event.value) {
      case '2': { //da anagrafica
        if (resetFields) {
          this.loadAllTenants();
          this.memberDataForm.get('nome').reset();
        }
        this.memberDataForm.get('nome').disable();
        setTimeout(() => {
          if (resetFields) {
            this.memberDataForm.get('cognome').reset();
          }
          this.memberDataForm.get('cognome').disable();
          setTimeout(() => {
            if (resetFields) {
              this.memberDataForm.get('slectOrg').reset();
            }
            this.memberDataForm.get('slectOrg').disable();
            setTimeout(() => {
              if (resetFields) {
                this.memberDataForm.get('email').reset();
              }
              this.memberDataForm.get('email').disable();
              setTimeout(() => {
                if (resetFields) {
                  this.memberDataForm.get('enteAzienda').reset();
                }
                this.memberDataForm.get('enteAzienda').disable();
                this.isSearchActive = true;
                this.isLoading = false;
              }, 10);
            }, 10);
          }, 10);
        }, 10);

        break;
      }
      case '1': {  // manuale
        this.isSearchActive = false;
        this.memberDataForm.get('nome').enable();
        if (resetFields) {
          this.memberDataForm.get('nome').reset();
        }
        setTimeout(() => {
          this.memberDataForm.get('cognome').enable();
          if (resetFields) {
            this.memberDataForm.get('cognome').reset();
          }
          setTimeout(() => {
            this.memberDataForm.get('slectOrg').enable();
            if (resetFields) {
              this.memberDataForm.get('slectOrg').reset();
            }
            setTimeout(() => {
              this.memberDataForm.get('email').enable();
              if (resetFields) {
                this.memberDataForm.get('email').reset();
              }
              setTimeout(() => {
                this.memberDataForm.get('enteAzienda').enable();
                if (resetFields) {
                  this.memberDataForm.get('enteAzienda').reset();
                }
                this.isLoading = false;
              }, 10);
            }, 10);
          }, 10);
        }, 10);
        break;
      }
    }
  }

  public resetEnteAziendaField(event: { value: string }) {
    if (!this.isSearchActive && event.value !== '3') {
      this.memberDataForm.get('enteAzienda').enable();
      this.memberDataForm.get('enteAzienda').reset();
    } else if (!this.isSearchActive) {
      this.memberDataForm.get('enteAzienda').reset();
      this.memberDataForm.get('enteAzienda').disable();
    }
  }

}
