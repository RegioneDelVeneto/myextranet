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
import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges, ViewEncapsulation } from '@angular/core';
import { Models } from 'src/app/models/model';
import { AlertService } from 'src/app/services/alert.service';

@Component({
  selector: 'app-user-manage-table',
  templateUrl: './user-manage-table.component.html',
  styleUrls: ['./user-manage-table.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class UserManageTableComponent implements OnInit, OnChanges {

  constructor(private alertSrv: AlertService) { }

  private _utenteRuolo: Models.GruppoUtentiRuoloExtended = null;
  private _role: string;
  @Input() set utenteRuolo(value: Models.GruppoUtentiRuoloExtended) {
    if (value) {
      this.userDataSource = Array.from(value.utenti);
      this._role = value.ruolo.codRuolo;
    }
    this._utenteRuolo = value;

  }
  get utenteRuolo(): Models.GruppoUtentiRuoloExtended {
    return this._utenteRuolo;
  }
  @Input() isFo = true;

  @Input() isRo = true;

  @Input() arrayIdx: number;

  @Output() operationEventEmitter: EventEmitter<{ data: Models.UtenteRuoloExtended, opType: string, row: string, codRuolo: string }> = new EventEmitter<{ data: Models.UtenteRuoloExtended, opType: string, row: string, codRuolo: string }>();

  @Output() showOnlyThis: EventEmitter<number> = new EventEmitter<number>();

  showForm: boolean = false;
  userDataSource: Models.UtenteRuoloExtended[];
  userDataInput: { value?: Models.utenteDTO, row?: string, codRuolo: string } = null;

  public tableHeaderFO: Models.TableHeader[] = [
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
    }, {
      definition: 'email',
      header: 'Email',
      span: 2
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'buttons',
      actions: [{
        icon: null,
        event: 'edit',
        label: 'MODIFICA',
        color: this.isFo ? 'accent' : 'primary',
        hiddenIf: 'deleted'
      },
      {
        icon: null,
        event: 'delete',
        label: 'ELIMINA',
        color: this.isFo ? 'accent' : 'primary',
        hiddenIf: 'deleted'
      },
      {
        icon: null,
        event: 'restore',
        label: 'RIPRISTINA',
        color: this.isFo ? 'accent' : 'primary',
        showOnlyIf: 'deleted'
      }
      ],
      span: 4
    }
  ];

  public tableHeaderRO: Models.TableHeader[] = [
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
    }, {
      definition: 'email',
      header: 'Email',
      span: 2
    },
  ];


  public tableHeaderBO: Models.TableHeader[] = [
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
    }, {
      definition: 'email',
      header: 'Email',
      span: 2
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      showAsButtonIfOne: true,
      actions: [{
        icon: 'edit',
        event: 'edit',
        label: 'Modifica',
        hiddenIf: 'deleted'
      },
      {
        icon: 'delete',
        event: 'delete',
        label: 'Elimina',
        hiddenIf: 'deleted'
      },
      {
        icon: 'restore_from_trash',
        event: 'restore',
        label: 'Ripristina',
        showOnlyIf: 'deleted'
      }
      ],
      span: 1
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
        {
          label: 'AGGIUNGI UTENTE',
          icon: 'add',
          event: 'new',
          color: this.isFo ? 'accent' : 'primary'
        }
      ]
    };

  public footerRO: {
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

  ngOnChanges(changes: SimpleChanges): void {

  }

  ngOnInit(): void {

  }
  /*
  updateDataSource(input: Models.UtenteRuoloExtended[]) {

  }
  */

  public manageOperation(event: { data: { utenteSelezionato: Models.utenteDTO, nome: string, cognome: string, codFiscale: string, email: string, telefono: string, selectInsType?: string }, opType: string, row: string, codRuolo: string }): void {
    const data = event.data;
    const CF = event.data.codFiscale;
    const uFound = this.userDataSource.find(v => v.codFiscale === CF);
    if ((!event.row && !!uFound) || (!!event && !!event.row && !!uFound && uFound.uiid !== event.row)) {
      this.alertSrv.openSnackBar(`Utente già presente in ${this.utenteRuolo.ruolo.desRuolo}`, 'Error');
      return null;
    }
    let returnedEvent: { data: Models.UtenteRuoloExtended, opType: string, row: string, codRuolo: string } = null;

    if (!data) {
      console.error('data is empty');
      return null;
    }
    if (event && event.opType === 'INS-MOD') {
      if (event.row !== null && event.row !== undefined) {

        let entity = this.userDataSource.find(utente => utente.uiid === event.row);
        if (!entity) {
          console.error('Impossibile recuperare il dato');

          return null;
        }
        if (data.utenteSelezionato !== null && data.utenteSelezionato !== undefined) {
          entity = {
            ...entity,
            ruolo: this.utenteRuolo.ruolo,
            utente: {
              ...entity.utente,
              ...data.utenteSelezionato
            },
            codFiscale: data.codFiscale,
            nome: data.nome,
            telefono: data.telefono,
            cognome: data.cognome,
            email: data.email
          };

        } else {

          entity = {
            ...entity,
            ruolo: this.utenteRuolo.ruolo,
            utente: null,
            codFiscale: data.codFiscale,
            nome: data.nome,
            telefono: data.telefono,
            cognome: data.cognome,
            email: data.email,
          };
        }
        returnedEvent = { codRuolo: event.codRuolo, data: entity, opType: event.opType, row: event.row };

      } else {
        let entity: Models.UtenteRuoloExtended = null;
        if (data.utenteSelezionato !== null && data.utenteSelezionato !== undefined) {

          entity = {
            ...entity,
            ruolo: this.utenteRuolo.ruolo,
            utente: {
              ...data.utenteSelezionato
            },
            email: data.email,
            telefono: data.telefono,
            codFiscale: data.codFiscale,
            nome: data.nome,
            cognome: data.cognome,
          };

        } else {
          entity = {
            ...entity,
            ruolo: this.utenteRuolo.ruolo,
            utente: null,
            codFiscale: data.codFiscale,
            email: data.email,
            nome: data.nome,
            telefono: data.telefono,
            cognome: data.cognome,

          };

        }
        returnedEvent = { codRuolo: event.codRuolo, data: entity, opType: event.opType, row: event.row };
      }

      this.showForm = false;
      this.showOnlyThis.emit(null);
    }
    this.userDataInput = null;
    this.operationEventEmitter.emit(returnedEvent);
  }

  public manageEvent(event: { element: any, eventType: string }): void {
    if (!event) {
      return null;
    }
    const data: Models.UtenteRuoloExtended = event.element;
    switch (event.eventType) {
      case 'edit': {
        this.showForm = true;
        this.userDataInput = {
          codRuolo: this._role,
          value: {
            idUtente: !!data.utente && data.utente.idUtente ? data.utente.idUtente : null,
            cognome: data.cognome,
            nome: data.nome,
            codFiscale: data.codFiscale,
            telefono: data.telefono,
            email: data.email,
          },
          row: data.uiid
        }
        this.showOnlyThis.emit(this.arrayIdx);
        break;
      }
      case 'delete': {
        let returnedEvent: { data: Models.UtenteRuoloExtended, opType: string, row: string, codRuolo: string } = {
          data: null,
          opType: 'DEL',
          codRuolo: this._role,
          row: data.uiid
        };

        this.operationEventEmitter.emit(returnedEvent);
        break;
      }
      case 'restore': {
        let returnedEvent: { data: Models.UtenteRuoloExtended, opType: string, row: string, codRuolo: string } = {
          data: null,
          opType: 'RES',
          codRuolo: this._role,
          row: data.uiid
        };

        this.operationEventEmitter.emit(returnedEvent);
        break;
      }
      case 'new': {
        this.userDataInput = { codRuolo: this._role };
        this.showForm = true;
        this.showOnlyThis.emit(this.arrayIdx);
        break;
      }

    }
  }

}
