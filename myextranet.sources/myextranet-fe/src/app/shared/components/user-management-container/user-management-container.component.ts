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
import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { Models } from 'src/app/models/model';
import { ProductsService } from 'src/app/services/products.service';
import { v4 as uuidv4 } from 'uuid';
import { UserManageTablesContainerComponent } from '../user-manage-tables-container/user-manage-tables-container.component';
@Component({
  selector: 'app-user-management-container',
  templateUrl: './user-management-container.component.html',
  styleUrls: ['./user-management-container.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class UserManagementContainerComponent implements OnInit {

  private _utentiRuoliArray: Models.GruppoUtentiRuoloDTO[] = [];

  private _utentiRuoliArrayBackup: Models.GruppoUtentiRuoloDTO[] = [];
  public utentiRuoliExtended: Models.GruppoUtentiRuoloExtended[] = [];
  public isLoading: boolean = false;
  public touchedData: Models.UtenteRuoliPostNewOperationDtoExtended[] = [];

  get getShowThis() {
    if (!this.tablesConteiner) {
      return true;
    }
    return this.tablesConteiner.showOnly === null;
  }

  @Input() set utentiRuoliArray(value: Models.GruppoUtentiRuoloDTO[]) {
    this.utentiRuoliExtended = value.map(d => {
      return {
        ruolo: d.ruolo,
        utenti: d.utenti.map(utente => {
          const rtObj: Models.UtenteRuoloExtended = {
            ...utente,
            uiid: uuidv4(),
            status: '',
            touched: false,
            deleted: false,
          };
          if (!!utente && !!utente.utente && utente.utente.idUtente) {
            rtObj.nome = !!rtObj.nome ? rtObj.nome : utente.utente.nome || null;
            rtObj.cognome = !!rtObj.cognome ? rtObj.cognome : utente.utente.cognome || null;
            rtObj.codFiscale = !!rtObj.codFiscale ? rtObj.codFiscale : utente.utente.codFiscale || null;
            rtObj.email = !!rtObj.email ? rtObj.email : utente.utente.email || null;
            rtObj.azienda = !!rtObj.azienda ? rtObj.azienda : utente.utente.azienda || null;
            rtObj.telefono = !!rtObj.telefono ? rtObj.telefono : utente.utente.telefono || null;
            rtObj.telefonoUff = !!rtObj.telefonoUff ? rtObj.telefonoUff : utente.utente.telefonoUff || null;
          }
          return rtObj;
        })
      };
    });
    this._utentiRuoliArray = value;
    this._utentiRuoliArrayBackup = value;
  }
  get utentiRuoliArray(): Models.GruppoUtentiRuoloDTO[] {
    return this._utentiRuoliArray;
  }

  public showOnly: number = null;

  @Input() isFo: boolean = true;
  @Input() isRo = true;


  @Input() prodotto: Models.ProdottoAttivatoDto = null;

  @ViewChild('tableCont') tablesConteiner: UserManageTablesContainerComponent;

  @Output() completeOperation: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor(private chngeDetectorRef: ChangeDetectorRef, private pService: ProductsService) { }

  ngOnInit(): void {
  }




  public manageOperation(event: { data: Models.UtenteRuoloExtended, opType: string, row: string, codRuolo: string }): void {

    const uiid = uuidv4();
    this.isLoading = true;
    if (event.row !== null && event.row !== undefined) {

      switch (event.opType) {
        case 'INS-MOD': {

          let ruoliIdx = this.utentiRuoliExtended.map(ruoli => ruoli.ruolo.codRuolo).indexOf(event.codRuolo);

          if (ruoliIdx !== -1) {
            let usrUiids = this.utentiRuoliExtended[ruoliIdx].utenti.map(usr =>
              usr.uiid
            );
            let uidIdx = usrUiids.indexOf(event.row);
            if (uidIdx !== -1) {
              this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx] = {
                ...event.data,
                status: 'Modificato',
                touched: true,
                deleted: false,
              };
            } else {
              this.utentiRuoliExtended[ruoliIdx].utenti.push(
                {
                  ...event.data,
                  uiid: uiid,
                  status: 'Modificato',
                  touched: true,
                  deleted: false,
                });
            }
          }

          break;
        }
        case 'DEL': {

          let ruoliIdx = this.utentiRuoliExtended.map(ruoli => ruoli.ruolo.codRuolo).indexOf(event.codRuolo);

          if (ruoliIdx !== -1) {
            let usrUiids = this.utentiRuoliExtended[ruoliIdx].utenti.map(usr =>
              usr.uiid
            );
            let uidIdx = usrUiids.indexOf(event.row);
            if (uidIdx !== -1) {
              this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx] = {
                ...this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx],
                status: 'Eliminato',
                prevStatus: this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx].status,
                deleted: true,
              };
            } else {
            }
          }

          break;
        }
        case 'RES': {
          let ruoliIdx = this.utentiRuoliExtended.map(ruoli => ruoli.ruolo.codRuolo).indexOf(event.codRuolo);

          if (ruoliIdx !== -1) {
            let usrUiids = this.utentiRuoliExtended[ruoliIdx].utenti.map(usr =>
              usr.uiid
            );
            let uidIdx = usrUiids.indexOf(event.row);
            if (uidIdx !== -1) {
              this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx] = {
                ...this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx],
                status: this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx].prevStatus,
                deleted: false,
              };
            } else {
            }
          }

          break;
        }
      }

    } else {
      switch (event.opType) {
        case 'INS-MOD': {

          let ruoliIdx = this.utentiRuoliExtended.map(ruoli => ruoli.ruolo.codRuolo).indexOf(event.codRuolo);

          if (ruoliIdx !== -1) {
            let usrUiids = this.utentiRuoliExtended[ruoliIdx].utenti.map(usr =>
              usr.uiid
            );
            let uidIdx = usrUiids.indexOf(event.row);
            if (uidIdx !== -1) {
              this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx] = event.data;
            } else {
              this.utentiRuoliExtended[ruoliIdx].utenti.push(
                {
                  ...event.data,
                  uiid: uiid,
                  status: 'Inserito',
                  touched: false,
                  deleted: false,
                });
            }
          }
          break;
        }
        case 'DEL': {

          break;
        }
      }
    }
    this.utentiRuoliExtended = Array.from(this.utentiRuoliExtended);
    this.tablesConteiner.showOnly = null;
    setTimeout(() => {
      this.isLoading = false;
      this.chngeDetectorRef.detectChanges();
    }, 100);

  }

  public showOnlyThis(idx: number): void {
    this.showOnly = idx;
  }


}
