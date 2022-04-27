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
import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { Models } from 'src/app/models/model';

@Component({
  selector: 'app-user-manage-tables-container',
  templateUrl: './user-manage-tables-container.component.html',
  styleUrls: ['./user-manage-tables-container.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class UserManageTablesContainerComponent implements OnInit {

  @Input() isFo = true;
  @Input() isRo = true;

  private test: Models.GruppoUtentiRuoloExtended[]

  @Input() set utentiRuoliExtended(value: Models.GruppoUtentiRuoloExtended[]) {
    let groups: Models.GruppoUtentiRuoloExtended[] = [];
    value.forEach(va => {
      let obj: Models.GruppoUtentiRuoloExtended = { ruolo: va.ruolo, utenti: Array.from(va.utenti) };
      groups.push(obj);
    });
    this.test = groups;
  }
  get utentiRuoliExtended() {
    return this.test;
  }

  public showOnly: number = null;


  @Output() operationEventEmitter: EventEmitter<{ data: Models.UtenteRuoloExtended, opType: string, row: string, codRuolo: string }> = new EventEmitter<{ data: Models.UtenteRuoloExtended, opType: string, row: string, codRuolo: string }>();

  @Output() showOnlyThisEmitter: EventEmitter<number> = new EventEmitter<number>();

  constructor() { }

  ngOnInit(): void {
  }


  public showOnlyThis(idx: number): void {
    this.showOnly = idx;
    this.showOnlyThisEmitter.emit(idx);
  }

  public manageOperation(event): void {
    this.operationEventEmitter.emit(event);
  }

}
