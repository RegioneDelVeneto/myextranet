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
import { DatePipe } from '@angular/common';
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Models } from 'src/app/models/model';
import { BackEndUsersService } from 'src/app/services/back-end-users.service';
import { EnteService } from 'src/app/services/ente.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { ProjectsService } from 'src/app/services/projects.service';
import { UserService } from 'src/app/services/user.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-project-collaborations',
  templateUrl: './project-collaborations.component.html',
  styleUrls: ['./project-collaborations.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProjectCollaborationsComponent implements OnInit {



  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca', url: '/utente/bacheca' },
      { label: 'I miei Progetti' }
    )
  };

  public project: Models.MyPortalContent = null;
  public projectid: string = null;

  public collaboratorsDS = [];
  public coordinatorsDS = [];

  public isLoading: boolean = false;
  public isError = false;
  public isCancelRequestOpen = false;
  private userInfo: Models.UserInfo;

  public userDtoData: Models.utenteDTO;

  public isCollaborator = false;
  public idMyCollab = null;
  public isAwaitingConfirmation = false;
  public hasCollabEntry = false;
  public isRejected = false;
  public wasCancelled = false;
  public showDialogMsg: boolean = false;

  public awaitMsgTitle = 'La tua richiesta è stata inoltrata correttamente';
  public awaitMsg = `Riceverai una email con l'esito della tua richiesta.`;

  public rejectedTitle = 'La tua richiesta è stata rifiutata';
  public rejectedMsg = 'La tua richiesta è stata rifiutata';


  public cancelledTitle = 'La tua richiesta è stata cancellata';
  public cancelledMsg = 'La tua richiesta è stata cancellata';

  private routeParams = null;

  signedMsg = 'Sei un collaboratore di questo progetto';
  errorMsg = 'Si è verificato un errore';

  public userDataRO: FormGroup = this.formBuilder.group({
    nome: [{ value: null, disabled: true }],
    cognome: [{ value: null, disabled: true }],
    email: [{ value: null, disabled: true }],
    telefono: [{ value: null, disabled: true }],
    ente: [{ value: null, disabled: true }],
    codFiscale: [{ value: null, disabled: true }]
  });

  public tableHeader: Models.TableHeader[] = [
    {
      definition: 'nome',
      header: 'Nome',
      span: 3
    },
    {
      definition: 'cognome',
      header: 'Cognome',
      span: 3
    },
    {
      definition: 'codFiscale',
      header: 'Codice Fiscale',
      span: 3
    },
    {
      definition: 'telefono',
      header: 'Telefono',
      span: 2
    },
    {
      definition: 'enteAzienda',
      header: 'Ente o Azienda',
      span: 2
    }
  ];

  public footer: {
    active: boolean,
    buttons: {
      label: string,
      event: string,
      icon: string,
      color?: string
    }[]
  } = {
      active: true,
      buttons: [

      ]
    };

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private myportalSrv: MyportalService,
    private projectsService: ProjectsService,
    private formBuilder: FormBuilder,
    private userService: UserService,
    private beUserSrv: BackEndUsersService,
    private enteService: EnteService,
    private datePipe: DatePipe

  ) { }

  ngOnInit(): void {
    this.isLoading = true;
    this.route.queryParams.subscribe(params => {
      this.routeParams = params;
      this.loadData();
    });
  }


  private loadData(fromDelete?: boolean): void {

    this.rejectedTitle = 'La tua richiesta è stata rifiutata';
    this.rejectedMsg = 'La tua richiesta è stata rifiutata';

    const params = this.routeParams;
    this.isCollaborator = false;
    this.isAwaitingConfirmation = false;
    this.hasCollabEntry = false;
    this.isRejected = false;
    this.isCancelRequestOpen = false;
    this.wasCancelled = false;
    this.showDialogMsg = false;

    this.isError = false;

    this.isLoading = true;
    if (params.projectId !== null && params.projectId !== undefined) {
      this.projectid = params.projectId;
      this.myportalSrv.getContent(this.projectid, true).then(prj => {
        this.project = prj;
        this.breadcrumbs = {
          base: '',
          entries: new Array<IBreadcrumbsEntry>(
            { label: 'Bacheca', url: '/utente/bacheca' },
            { label: 'I miei Progetti', url: '/utente/progetti' },
            { label: this.project.titolo }
          )
        };
        return this.userService.getUserInfo();
      }).then(val => {
        this.userInfo = val;
        this.userService.checkUserInfo();
        if (!this.userInfo.userId) {
          throw new Error('Unable to find the user');
        }
        return this.beUserSrv.getUserById(this.userInfo.userId);
      }).then(userDto => {
        this.userDtoData = userDto;
        this.userDataRO.patchValue({
          nome: this.userDtoData.nome,
          cognome: this.userDtoData.cognome,
          email: this.userDtoData.email,
          telefono: [this.userDtoData.telefono, this.userDtoData.telefonoUff].filter(v => !!v).join(', '),
          ente: this.userDtoData.ente && !!this.userDtoData.ente.idEnte ? this.userDtoData.ente.denominazione : !!this.userDtoData.azienda ? this.userDtoData.azienda : null,
          codFiscale: this.userDtoData.codFiscale
        });
        return this.projectsService.getCollaborazione(this.projectid);
      }).then(collab => {

        if (!collab || !collab.collaboratoreProgetto) {
          return Promise.resolve(null);
        }
        this.idMyCollab = collab.collaboratoreProgetto.idCollab;
        this.hasCollabEntry = true;

        if (!!collab && !!collab.collaboratoreProgetto && collab.collaboratoreProgetto.flgConferma === 0) {
          this.isAwaitingConfirmation = true;
        }
        else if (!!collab && !!collab.collaboratoreProgetto && collab.collaboratoreProgetto.flgConferma === 2) {
          this.isRejected = true;
          if (collab?.collaboratoreProgetto?.dtRichiesta) {
            this.rejectedTitle = `la tua richiesta di adesione inviata in data ${this.datePipe.transform(collab.collaboratoreProgetto.dtRichiesta, 'dd/MM/yyyy')} è stata rifiutata/annullata`;
          }
          this.rejectedMsg = collab.collaboratoreProgetto.motivConferma;
        }
        else if (!!collab && !!collab.collaboratoreProgetto && collab.collaboratoreProgetto.flgConferma === 1 && collab.collaboratoreProgetto.valid === true) {
          return this.projectsService.getCollaboratoriProgetto(null, -1, this.projectid, true, true, true);
        } else if (!!collab && !!collab.collaboratoreProgetto && (collab.collaboratoreProgetto.flgConferma === 3 || collab.collaboratoreProgetto.valid === false)) {
          this.wasCancelled = true;
          this.hasCollabEntry = true;
          this.cancelledMsg = collab.collaboratoreProgetto.motivConferma;
        }
        else {
          return Promise.resolve(null);
        }


      }).then((collabs: Models.PagedContent) => {
        if (!!collabs) {

          this.isCollaborator = true;
          const records: Models.CollaboratoreDTO[] = collabs.records;
          this.collaboratorsDS = records.filter(v => v.flgCoord === 0).map(collab => ({
            ...collab.utente,
            enteAzienda: collab.utente.azienda !== null ? collab.utente.azienda : collab.utente.ente.denominazione
          }));
          this.coordinatorsDS = records.filter(v => v.flgCoord === 1).map(collab => ({
            ...collab.utente,
            enteAzienda: collab.utente.azienda !== null ? collab.utente.azienda : collab.utente.ente.denominazione
          }));

        } else if (fromDelete) {
          this.showDialogMsg = true;
          this.isCollaborator = false;
          this.wasCancelled = true;
          this.cancelledMsg = "annullata dall'utente";
        } else {
          this.isCollaborator = false;
        }
        this.isLoading = false;
      }).catch(err => {
        this.isLoading = false;
      });
    }
  }

  newCollab() {
    this.isLoading = true;
    const coll: Models.IscrizioneCollaboratoreDTO = { utente: { idUtente: this.userInfo.userId }, idProgetto: this.projectid };
    this.projectsService.postOrPatchCollaboratoreProgetto(coll, false, true).then(resp => {
      this.loadData();
    }).catch(err => {
      this.isError = true;
      this.isLoading = false;
    });
  }

  changeCollab() {
    this.isCancelRequestOpen = !this.isCancelRequestOpen;
    if (this.isCancelRequestOpen) {
      const cancelBox: any = document.getElementById('cancel-collaboration-box');
      cancelBox.scrollIntoView();
    }
  }

  requestCancelCollab() {
    this.isLoading = true;
    this.projectsService.cancelCollaboration(this.idMyCollab, true).then(resp => {

      this.loadData(true);
    }).catch(err => {
      this.isError = true;
      this.isLoading = false;
    });
  }
}
