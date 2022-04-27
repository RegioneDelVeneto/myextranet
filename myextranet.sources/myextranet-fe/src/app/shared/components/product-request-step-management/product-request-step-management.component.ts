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
import { Component, Input, OnInit, Output, ViewChild, ViewEncapsulation, EventEmitter } from '@angular/core';
import { Models } from 'src/app/models/model';
import { UserManageTablesContainerComponent } from '../user-manage-tables-container/user-manage-tables-container.component';
import { v4 as uuidv4 } from 'uuid';
import { ProductRequestDocumentsTableComponent } from '../product-request-documents-table/product-request-documents-table.component';


@Component({
  selector: 'app-product-request-step-management',
  templateUrl: './product-request-step-management.component.html',
  styleUrls: ['./product-request-step-management.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProductRequestStepManagementComponent implements OnInit {

  @ViewChild('docManager') docManager: ProductRequestDocumentsTableComponent;


  private _utentiRuoliArray: Models.GruppoUtentiRuoloDTO[] = [];

  private _utentiRuoliArrayBackup: Models.GruppoUtentiRuoloDTO[] = [];
  public utentiRuoliExtended: Models.GruppoUtentiRuoloExtended[] = [];
  public isLoading = false;
  public touchedData: Models.UtenteRuoliPostNewOperationDtoExtended[] = [];
  public touchedDataPost: Models.UtenteRuoliPostNewOperationDto[] = [];


  public docDataInput: { value?: Models.docInputModel, row?: string } = null;



  get getShowThis() {
    if (!this.tablesConteiner) {
      return true;
    }
    return this.tablesConteiner.showOnly === null;
  }

  get getShowFormStatus() {
    if (!this.docManager) {
      return false;
    }
    else {
      return this.docManager.showDocForm;
    }
  }

  public showDocs = true;
  public downloadId: string = null;

  @Output() showSaveBar: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Input() isFo = true;

  @Input() documentOperations: 1 | 2 | 0 = 0;

  @Input() isRo = true;

  @Input() prodotto: Models.ProdottoAttivatoDto = null;

  @Input() status: number = null; //0 -> da attivare; 1-> in corso; 2 -> attivo, 3->disattivato

  @ViewChild('stepTableCont') tablesConteiner: UserManageTablesContainerComponent;

  @Input() set utentiRuoliArray(value: Models.GruppoUtentiRuoloDTO[]) {
    this.utentiRuoliExtended = value.map(d => {
      return {
        ruolo: d.ruolo,
        utenti: d.utenti.map(utente => {
          let rtObj: Models.UtenteRuoloExtended = {
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

  @Input() documentsDs: Models.DocumentoRichiestaProdottoModelDtoExtended[] = [];

  public showOnly: number = null;


  constructor(

  ) { }

  ngOnInit(): void {
  }

  public manageOperation(event: { data: Models.UtenteRuoloExtended, opType: string, row: string, codRuolo: string }): void {

    const uiid = uuidv4();
    this.isLoading = true;
    if (event.row !== null && event.row !== undefined) {

      switch (event.opType) {
        case 'INS-MOD': {

          const ruoliIdx = this.utentiRuoliExtended.map(ruoli => ruoli.ruolo.codRuolo).indexOf(event.codRuolo);

          if (ruoliIdx !== -1) {
            const usrUiids = this.utentiRuoliExtended[ruoliIdx].utenti.map(usr =>
              usr.uiid
            );
            const uidIdx = usrUiids.indexOf(event.row);
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
                  uiid,
                  status: 'Modificato',
                  touched: true,
                  deleted: false,
                });
            }
          }

          break;
        }
        case 'DEL': {

          const ruoliIdx = this.utentiRuoliExtended.map(ruoli => ruoli.ruolo.codRuolo).indexOf(event.codRuolo);

          if (ruoliIdx !== -1) {
            const usrUiids = this.utentiRuoliExtended[ruoliIdx].utenti.map(usr =>
              usr.uiid
            );
            const uidIdx = usrUiids.indexOf(event.row);
            if (uidIdx !== -1) {
              this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx] = {
                ...this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx],
                status: 'Eliminato',
                deleted: true,
              };
            } else {
            }
          }

          break;
        }
        case 'RES': {
          const ruoliIdx = this.utentiRuoliExtended.map(ruoli => ruoli.ruolo.codRuolo).indexOf(event.codRuolo);

          if (ruoliIdx !== -1) {
            const usrUiids = this.utentiRuoliExtended[ruoliIdx].utenti.map(usr =>
              usr.uiid
            );
            const uidIdx = usrUiids.indexOf(event.row);
            if (uidIdx !== -1) {
              this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx] = {
                ...this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx],
                status: this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx].idUtenteRich && this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx].touched ? 'Modificato' : !this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx].idUtenteRich ? 'Inserito' : '',
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

          const ruoliIdx = this.utentiRuoliExtended.map(ruoli => ruoli.ruolo.codRuolo).indexOf(event.codRuolo);

          if (ruoliIdx !== -1) {
            const usrUiids = this.utentiRuoliExtended[ruoliIdx].utenti.map(usr =>
              usr.uiid
            );
            const uidIdx = usrUiids.indexOf(event.row);
            if (uidIdx !== -1) {
              this.utentiRuoliExtended[ruoliIdx].utenti[uidIdx] = event.data;
            } else {
              this.utentiRuoliExtended[ruoliIdx].utenti.push(
                {
                  ...event.data,
                  uiid,
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
      this.showDocs = true;
    }, 100);

  }

  public manageShowOnlyThisOnUsers(event: any): void {
    if (event !== null && event !== undefined) {
      this.showDocs = false;
      this.showSaveBar.emit(false);
    } else {
      this.showDocs = true;
      this.showSaveBar.emit(true);
    }
  }

}
