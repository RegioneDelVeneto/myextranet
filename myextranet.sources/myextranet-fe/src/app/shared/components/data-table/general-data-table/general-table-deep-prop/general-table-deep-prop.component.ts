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
import { Models } from 'src/app/models/model';

@Component({
  selector: 'app-general-table-deep-prop',
  templateUrl: './general-table-deep-prop.component.html',
  styleUrls: ['./general-table-deep-prop.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class GeneralTableDeepPropComponent implements OnInit {

  public label: string;
  private _configuration: { displayedColumns: Models.TableHeader, column: string, element: any };
  @Input() set configuration(config: { displayedColumns: Models.TableHeader, column: any, element: any }) {
    this._configuration = config;
    this.label = this.getLabel(config.element, config.column);
  }
  get configuration(): { displayedColumns: Models.TableHeader, column: any, element: any } {
    return this._configuration;
  }

  constructor() { }

  ngOnInit(): void {
  }

  getLabel(element: any, column: string) { 
    if (!!element && !!column) {
      let returnObj: any = null;
      let cArray: string[] = column.split('.');
      for (let i = 0; i < cArray.length; i++) {
        let key: string = cArray[i];
        if (i === 0 && !!element[key]) {
          returnObj = element[key];
        } else if (i === 0 && !element[key]) {
          returnObj = null;
          return null;
        } else if (i !== 0 && !!returnObj[key]) {
          returnObj = returnObj[key];
        } else if (i !== 0 && !returnObj[key]) {
          returnObj = null;
          return null;
        }
      }
      return returnObj;
    }
    else {
      return null;
    }

  }


  checkShowOnly(row: any, keyString: string): boolean {
    let retunedValue: boolean = true;
    let rowValue: any = row;

    for (const key of keyString.split('.')) {
      if (!rowValue[key]) {
        retunedValue = false;
        break;
      }
      rowValue = rowValue[key];
    }
    return retunedValue;

  }

}
