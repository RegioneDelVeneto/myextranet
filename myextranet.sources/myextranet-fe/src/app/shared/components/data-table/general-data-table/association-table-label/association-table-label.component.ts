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
import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { MyportalService } from 'src/app/services/myportal.service';


@Component({
  selector: 'app-association-table-label',
  templateUrl: './association-table-label.component.html',
  styleUrls: ['./association-table-label.component.scss'],
  encapsulation : ViewEncapsulation.None
})
export class AssociationTableLabelComponent implements OnInit {

  private _associationData: { id: string, key: string } = null;
  public associationLabel: string = '';
  @Input() set associationData(val: { id: string, key: string }) {
    this._associationData = val;
    if (!val.id || val.id.length < 1) {
      this.associationLabel = '';
    } else {

      this.getAsyncAssociationFromMyPortal(val.id, val.key).then(v => {
        this.associationLabel = v;
      }).catch(err => {
        this.associationLabel = '';
      });
    }
  }


  constructor(private myPortalSrv: MyportalService) { }

  ngOnInit(): void {
  }

  getAsyncAssociationFromMyPortal(id: string, key: string): Promise<string> {
    if (!key || key.length === 0) key = 'titolo';
    return this.myPortalSrv.getContent(id, false).then(val => val[key]);
  }
}
