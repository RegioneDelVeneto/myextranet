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
import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'actionsFilterPipe'
})
export class ActionsFilterPipePipe implements PipeTransform {

  transform(value: {
    type?: string,
    icon: string,
    event: string,
    label?: string,
    acl?: string,
    permissions?: string[],
    showOnlyIf?: string,
    hiddenIf?: string,
    color?: string,
    legend?: string
  }[], element: any, ...args: unknown[]): any[] {

    return value.filter(item => {

      return (!item?.showOnlyIf || (!!item?.showOnlyIf && this.checkShowOnly(element, item.showOnlyIf))) &&
        (!item.hiddenIf || (!!item?.hiddenIf && !this.checkShowOnly(element, item.hiddenIf)));
    });

  }

  checkShowOnly(row: any, keyString: string): boolean {
    let retunedValue: boolean = true;
    let rowValue: any = row;
    let keyStringArray = [];
    if (keyString.startsWith('$and:[') && keyString.endsWith(']')) { 
      let returnArray: boolean[] = [];
      keyStringArray = keyString.replace('$and:[', '').replace(']', '').split(',');
      for (const key of keyStringArray) {
        returnArray.push(this.checkShowOnly(row, key.trim())); 
      }
      return returnArray.every(a => !!a);
    } else if (keyString.startsWith('$or:[') && keyString.endsWith(']')) { 
      let returnArray: boolean[] = [];
      keyStringArray = keyString.replace('$or:[', '').replace(']', '').split(',');
      for (const key of keyStringArray) {
        returnArray.push(this.checkShowOnly(row, key.trim())); 
      }
      return !!returnArray.find(a => !!a); //cerco il primo true -> se non lo trova da null -> false
    } else {

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


}
