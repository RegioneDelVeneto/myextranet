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
import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { filter } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { Models } from '../models/model';
import { SnackbarComponent } from './snackbar/snackbar.component';
import { UserService } from './user.service';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private baseUrl = '';

  constructor(private snackBar: MatSnackBar, private usrSrv: UserService) {

    this.alertSubject.pipe(filter(() => !!this.usrSrv.returnUserInfo())).subscribe((val: { method: string, url: string, snackType: string, error?: Models.ApiError }) => {
      const method = val.method;
      const url = val.url;
      const snackType = val.snackType;
      const error = val.error;
      if (!!error && error.code === 'VALIDATION_ERROR') {
        this.openSnackBar(error.data, snackType);
      } else {
        this.openSnackBar(this.getMessage(method, url, snackType), snackType);
      }

    });
  }

  private endpointMsg: Object = {
    '/frontoffice/iscritto-evento': {
      POST: {
        Error: `Errore durante l'inserimento dell'iscrizione all'evento`,
        Success: `Iscrizione all'evento avvenuta con successo`
      }
    },
    'acl-general-error': {
      POST: {
        Error: 'Non disponi delle autorizzazioni necessarie a questa operazione'
      }
    },
    '/invito/evento': {
      POST: {
        Error: `Errore durante l'inserimento dell'invito`,
        Success: `Inserimento dell'invito avvenuto con successo`
      }
    },
    '/utente-messaggi': {
      POST: {
        Error: `Errore durante l'inserimento del messaggio`,
        Success: `Inserimento del messaggio avvenuto con successo`
      },
      PUT: {
        Error: `Errore durante la modifica del messaggio`,
        Success: `Modifica del messaggio avvenuto con successo`
      },
      PATCH: {
        Error: `Errore durante la modifica del messaggio`,
        Success: `Modifica del messaggio avvenuto con successo`
      },
    },
    '/utente/comunicazione': {
      POST: {
        Error: `Errore durante l'invio delle comunicazioni`,
        Success: `Invio delle comunicazioni avvenuto con successo`
      }
    },
    '/collaboratore-progetto': {
      POST: {
        Error: `Errore durante l'inserimento del collaboratore. Verificare che l'utente selezionato non sia già un collaboratore a questo progetto`,
        Success: `Inserimento del collaboratore avvenuto con successo`
      }
    },
    '/collaboratore-progetto/comunicazione': {
      POST: {
        Error: `Errore durante l'invio delle comunicazioni`,
        Success: `Invio delle comunicazioni avvenuto con successo`
      }
    },
    '/comunicazione': {
      POST: {
        Error: `Errore durante l'inserimento della Comunicazione`,
        Success: `Inserimento della Comunicazione avvenuto con successo`
      }
    },
    '/iscritto-evento': {
      POST: {
        Error: `Errore durante l'inserimento dell'iscrizione all'evento`,
        Success: `Iscrizione dell'utente all'evento avvenuta con successo`
      },
      PATCH: {
        Error: `Errore durante la modifica dell'iscrizione all'evento`,
        Success: `Modifica all'iscrizione all'evento avvenuta con successo`
      },
    },
    '/iscritto-evento/operazione-massiva': {
      POST: {
        Error: `Errore durante l'operazione sugli iscritti`,
        Success: `Operazione sugli iscritti all'evento avvenuta con successo`
      },
    },
    '/iscritto-evento/{idEvento}/comunicazione': {
      POST: {
        Error: `Errore durante l'invio della comunicazione agli iscritti`,
        Success: `Invio della comunicazione agli iscritti all'evento avvenuto con successo`
      },
    },
    '/iscritto-evento/{idIscitto}': {
      DELETE: {
        Error: `Errore durante l'annullamento dell'iscrizione`,
        Success: `Annullamento dell'iscrizione avvenuto con successo`
      }
    },
    '/frontoffice/ente': {
      POST: {
        Error: `Errore durante l'inserimento del nuovo ente`,
        Success: `Inserimento del nuovo ente avvenuto con successo`
      },
      PATCH: {
        Error: `Errore durante la modifica dell'ente`,
        Success: `Modifica dell'ente avvenuta con successo`
      },
      PUT: {
        Error: `Errore durante la modifica dell'ente`,
        Success: `Modifica dell'ente avvenuta con successo`
      },
    },
    '/ente': {
      POST: {
        Error: `Errore durante l'inserimento del nuovo ente`,
        Success: `Inserimento del nuovo ente avvenuto con successo`
      },
      PATCH: {
        Error: `Errore durante la modifica dell'ente`,
        Success: `Modifica dell'ente avvenuta con successo`
      },
      PUT: {
        Error: `Errore durante la modifica dell'ente`,
        Success: `Modifica dell'ente avvenuta con successo`
      },
    },
    '/prodotto-attivabile': {
      POST: {
        Error: `Errore durante l'inserimento del prodotto attivabile`,
        Success: `Inserimento del prodotto attivabile avvenuta con successo`
      },
      PATCH: {
        Error: `Errore durante la modifica del prodotto attivabile`,
        Success: `Modifica del prodotto attivabile avvenuta con successo`
      },
      PUT: {
        Error: `Errore durante la modifica del prodotto attivabile`,
        Success: `Modifica del prodotto attivabile avvenuta con successo`
      }
    },
    '/utente-prodotto-attivato/aggiorna-utenti-prodotto': {
      POST: {
        Error: `Errore durante l'inserimento degli utenti`,
        Success: `Inserimento degli utenti avvenuta con successo`
      }
    },
    '/rappresentante-ente-rap/conferma-rappresentante': {
      POST: {
        Error: `Errore durante il cambio di stato del RAP`,
        Success: `Cambio di stato del RAP avvenuto con successo`
      }
    },
    '/collaboratore-progetto/cancel': {
      POST: {
        Error: `Errore durante la cancellazione della collaborazione`,
        Success: `Cancellazione della collaborazione avvenuto con successo`
      }
    },
    '/richiesta-prodotto/{id}/comunicazione': {
      POST: {
        Error: `Errore durante l'invio della comunicazione`,
        Success: `Invio della comunicazione avvenuto con successo`
      }
    },
    '/prodotto-attivato/comunicazione': {
      POST: {
        Error: `Errore durante l'invio della comunicazione ai gruppi`,
        Success: `Invio della comunicazione ai gruppi avvenuto con successo`
      }

    }
  };

  private generalError = `Errore durante il completamento dell'operazione`;

  private alertSubject: Subject<{ method: string, url: string, snackType: string }> = new Subject<{ method: string, url: string, snackType: string }>();

  private progressBarSubject: Subject<{ progress: number, operation: string, id: string }> = new Subject<{ progress: number, operation: string, id: string }>();

  private progressBarObjStatus: { [key: string]: Subject<{ status: boolean }> } = { 'Invio attestati': new BehaviorSubject<{ status: boolean }>({ status: false }) };


  public openSnackBar(msg: string, snackType: string) {

    if (!msg) { return null; }
    this.snackBar.openFromComponent(SnackbarComponent, {
      duration: 5000,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      data: { msg, snackType },

      panelClass: ['msg-snackbar-' + snackType.toLocaleLowerCase()]
    });
  }

  public getAlertSubj() {
    return this.alertSubject;
  }

  public passDataToSubject(data: { method: string, url: string, snackType: string, error?: Models.ApiError }) {
    this.alertSubject.next(data);
  }

  private getMessage(methodData: string, methodUrl: string, snackType: string): string {
    const method = methodData;
    const type = snackType;
    let url = methodUrl.split('?')[0].replace(this.baseUrl, '');

    if (url.split('/')[1] === 'iscritto-evento' && url.split('/').length > 3 && url.split('/')[url.split('/').length - 1] === 'comunicazione') {
      url = '/iscritto-evento/{idEvento}/comunicazione';
    } else if (url.split('/')[1] === 'richiesta-prodotto' && url.split('/').length > 3 && url.split('/')[url.split('/').length - 1] === 'comunicazione') {
      url = '/richiesta-prodotto/{id}/comunicazione';
    }
    if (url && this.endpointMsg.hasOwnProperty(url.split('?')[0].replace(this.baseUrl, ''))
      && (this.endpointMsg[url] as Object).hasOwnProperty(method)
      && (this.endpointMsg[url][method] as Object).hasOwnProperty(type)) {
      return this.endpointMsg[url][method][type];
    } else if (url.split('/')[1] === 'iscritto-evento' && url.split('/').length > 2 && method === 'DELETE') {
      url = '/iscritto-evento/{idIscitto}';
      if (url && this.endpointMsg.hasOwnProperty(url.split('?')[0].replace(this.baseUrl, ''))
        && (this.endpointMsg[url] as Object).hasOwnProperty(method)
        && (this.endpointMsg[url][method] as Object).hasOwnProperty(type)) {
        return this.endpointMsg[url][method][type];
      }
    }

    if (snackType === 'Error') {
      return this.generalError;
    }
    return null;
  }

  public getProgressBarSubject(): Subject<{ progress: number, operation: string, id: string }> {
    return this.progressBarSubject;
  }
  public getProgressBarObservable(): Observable<{ progress: number, operation: string, id: string }> {
    return this.progressBarSubject.asObservable();
  }

  public passDataToProgressSubject(data: { progress: number, operation: string, id: string }): void {
    if (!this.progressBarObjStatus[data.operation]) {
      this.progressBarObjStatus[data.operation] = new Subject<{ status: boolean }>();
      this.progressBarObjStatus[data.operation].next({ status: true });
    } else if (!!this.progressBarObjStatus[data.operation]) {
      this.progressBarObjStatus[data.operation].next({ status: true });
    }
    else if (!!this.progressBarObjStatus[data.operation] && data.progress > 99) {
      this.progressBarObjStatus[data.operation].next({ status: false });
    }
    this.progressBarSubject.next(data);
  }

  public returnProgressBarObjStatus(): { [key: string]: Subject<{ status: boolean }> } {
    return this.progressBarObjStatus;
  }

  public completeProgress(operation: string, id: string) {
    if (!this.progressBarObjStatus[operation]) {
      this.progressBarObjStatus[operation] = new Subject<{ status: boolean }>();
      this.progressBarObjStatus[operation].next({ status: false });
    } else if (!!this.progressBarObjStatus[operation]) {
      this.progressBarObjStatus[operation].next({ status: false });
    }
    this.progressBarSubject.next({ progress: 101, operation, id });
  }

}
