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
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Models } from 'src/app/models/model';
import { BackEndUsersService } from 'src/app/services/back-end-users.service';
import { EnteService } from 'src/app/services/ente.service';
import { EventsService } from 'src/app/services/events.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { UserService } from 'src/app/services/user.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-event-signup',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './event-signup.component.html',
  styleUrls: ['./event-signup.component.scss']
})
export class EventSignupComponent implements OnInit {

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private beUserSrv: BackEndUsersService,
    private enteService: EnteService,
    private router: Router,
    private route: ActivatedRoute,
    private myPortalService: MyportalService,
    private eventService: EventsService
  ) { }

  eventForm: FormGroup = this.formBuilder.group({
    nome: ['', [Validators.required, Validators.maxLength(150)]],
    cognome: ['', [Validators.required, Validators.maxLength(150)]],
    email: ['', [Validators.required, Validators.maxLength(60), Validators.pattern('^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$')]],
    telefono: ['', [Validators.required, Validators.maxLength(25)]],
    ente: ['', Validators.required],
    preferenza: ['']
  });

  breadcrumbs = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca', url: '/utente/bacheca' },
      { label: 'I miei eventi', url: '/utente/eventi' },
      { label: `Iscrizione all'evento` },
    )
  };

  public userDtoData: Models.utenteDTO;

  public signedMsg: string = `prima dell'evento verranno pubblicati i link e le istruzioni per partecipare`;
  private userInfo: Models.UserInfo;

  private iscrizioneEvento: Models.iscrizioneEventoDTO = null;
  public iscrittoEvento: Models.iscrittoDTO;
  public isLoading = false;
  public isError = false;
  public isAlreadySigned = false;
  public eventId: string;

  public event: Models.MyPortalEvent;

  private userData: Models.utenteDTO = null;
  public entiArray: Models.enteDTO[] = [];

  private prePartecipStatus: 'PRE' | 'REM' = null;
  public showPreferenza = false;
  public showInStreaming = false;
  public hideError = false;
  public isLoadingError = false;
  public isUndone = false;
  public errorMsg: string = null;
  public defError = "Si è verificato un errore durante il completamento dell'iscrizione. Si prega di riprovare più tardi.";

  date: Date = new Date();
  ngOnInit(): void {
    this.isLoading = true;
    this.enteService.getAutocomplete(99999, true).then(enti => {
      this.entiArray = enti;
    });
    this.userService.getUserInfo().then(val => {
      this.userInfo = val;
      this.userService.checkUserInfo();
      if (this.userInfo.userId) {
        this.beUserSrv.getUserById(this.userInfo.userId).then(userDto => {
          this.userDtoData = userDto;
          this.route.queryParamMap.subscribe(params => {
            if (params.has('eventId')) {
              this.eventId = params.get('eventId');
              this.loadData();
            } else {
              this.isLoading = false;
              this.router.navigate(['/utente/eventi']);
            }

          });
        });
      }
    }).catch(err => {
      this.isLoading = false;
      this.hideError = false;
      this.errorMsg = "Si è verificato un errore durante il recupero dell'evento";
      this.isLoadingError = true;
    });
    this.eventForm.disable();
  }

  public loadData() {
    this.prePartecipStatus = null;
    this.isLoading = true;
    const userDto = this.userDtoData;
    this.eventService.getIscrittoEvento(this.eventId, this.userInfo.userId)
      .then(iscrizioneEvento => {
        if (!!iscrizioneEvento && !!iscrizioneEvento.iscrittoEvento && !!iscrizioneEvento.myExtranetContent) {
          this.isAlreadySigned = true;
          this.iscrizioneEvento = iscrizioneEvento;
          this.iscrittoEvento = iscrizioneEvento.iscrittoEvento;
          return Promise.resolve(iscrizioneEvento.myExtranetContent);
        } else if (!!iscrizioneEvento && !iscrizioneEvento.iscrittoEvento && !!iscrizioneEvento.myExtranetContent) {
          this.isAlreadySigned = false;
          this.iscrizioneEvento = null;
          return Promise.resolve(iscrizioneEvento.myExtranetContent);
        } else {
          this.isAlreadySigned = false;
          this.iscrizioneEvento = null;
          return this.myPortalService.getEventFO(this.eventId);
        }
      }).then((event: Models.MyPortalEvent) => {
        this.event = event;
        let preferenza: string = '';
        if (this.isAlreadySigned && this.iscrizioneEvento) {
          preferenza = this.iscrizioneEvento.iscrittoEvento.flgPartecipPref === 'REM' ? '2' : this.iscrizioneEvento.iscrittoEvento.flgPartecipPref === 'PRE' ? '1' : '';
        }
        this.userData = userDto;
        this.eventForm = this.formBuilder.group({
          nome: [{ value: this.userData.nome, disabled: true }, [Validators.required, Validators.maxLength(150)]],
          cognome: [{ value: this.userData.cognome, disabled: true }, [Validators.required, Validators.maxLength(150)]],
          email: [{ value: this.userData.email, disabled: true }, [Validators.required, Validators.maxLength(60), Validators.pattern('^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$')]],
          telefono: [{ value: [this.userData.telefono, this.userData.telefonoUff].filter(v => !!v).join(', '), disabled: true }, [Validators.required, Validators.maxLength(25)]],
          ente: [{ value: this.userData.ente && !!this.userData.ente.idEnte ? this.userData.ente.denominazione : !!this.userData.azienda ? this.userData.azienda : null, disabled: true }, Validators.required],
          codFiscale: [{ value: this.userData.codFiscale, disabled: true }, [Validators.required, Validators.maxLength(16), Validators.pattern('^(?:[A-Z][AEIOU][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[15MR][\\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$')]],
          preferenza: [{ value: preferenza, disabled: this.isAlreadySigned }]
        });
        setTimeout(() => {
          if (!!this.event.inPresenza && !this.event.inStreaming) {
            this.prePartecipStatus = 'PRE';
          } else if (!this.event.inPresenza && !!this.event.inStreaming) {
            this.prePartecipStatus = 'REM';
          }
          else if (!!this.event.inPresenza && !!this.event.inStreaming) {
            this.eventForm.get('preferenza').setValidators([Validators.required]);
            setTimeout(() => {
              this.showPreferenza = true;
              this.isLoading = false;
              this.showInStreaming = true;
            }, 100);
          }
          if (!!this.event.inStreaming) {
            this.showInStreaming = true;
            this.isLoading = false;
          }
          else {
            this.isLoading = false;
          }
        }, 100);


      }).catch(err => {
        this.isLoading = false;
        this.hideError = false;
        this.errorMsg = "Si è verificato un errore durante il recupero dell'evento";
        this.isLoadingError = true;
      });
  }

  sumbmit() {
    this.isError = false;
    this.isUndone = false;
    this.isLoading = true;
    const fValue: { nome: string, cognome: string, email: string, telefono: string, ente: number, preferenza: number } = this.eventForm.value;
    const data: Models.IscrizioneEventoFO = {
      utente: {
        idUtente: this.userInfo.userId
      },
      idEvento: this.eventId,
      flgPartecipPref: this.showPreferenza ? (fValue.preferenza == 1 ? 'PRE' : 'REM') : this.prePartecipStatus
    };
    this.eventService.signUp(data).then(val => {
      this.loadData();
    }).catch(err => {
      if (err.error && err.error.data && !!err.error.code && (err.error.code == 'VALIDATION_ERROR')) {
        this.errorMsg = err.error.data;
      }
      else {
        this.errorMsg = this.defError;
      }
      this.isError = true;
      this.hideError = false;
      this.isLoading = false;
    })

  }

  showDenomination(id: number) {

    return id !== null && id !== undefined && this.entiArray.find(ente => ente.idEnte === id) ? this.entiArray.find(ente => ente.idEnte === id).denominazione : null;
  }

  gotToStreamingLink(link: string) {
    this.eventService.patchIscrittoEventoFO({ idIscritto: this.iscrizioneEvento.iscrittoEvento.idIscritto, flgPartecipRemoto: 1 }).then(() => {
      this.isError = false;
      if (!!link && link.length > 0) {
        window.open(link, '_blank');
      }
    }).catch(err => {
      this.isError = true;
      this.errorMsg = "Errore durante l'aggiornamento dei dati di partecipazione all'evento";
    });

  }

  undoSignIn() {
    this.isLoading = true;
    this.eventService.deleteIscrittoEvento(this.iscrizioneEvento.iscrittoEvento.idIscritto).then(() => {
      this.isUndone = true;
      this.loadData();
    }).catch(err => {
      if (err.error && err.error.data) {
        this.errorMsg = err.error.data;
      }
      else {
        this.errorMsg = null;
      }
      this.isError = true;
      this.hideError = false;
      this.isLoading = false;
    })
  }



}
