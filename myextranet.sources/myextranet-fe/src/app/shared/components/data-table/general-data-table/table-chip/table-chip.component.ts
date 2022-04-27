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

@Component({
  selector: 'app-table-chip',
  templateUrl: './table-chip.component.html',
  styleUrls: ['./table-chip.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class TableChipComponent implements OnInit {

  public possibleChipsArray: {
    label: string,
    value: string;
    color: string;
  }[] = [];

  public keys: { class: string, label: string }[] = [];

  constructor() { }
  
  @Input() set config(value: {
    data: string[], array: {
      label: string,
      value: string;
      color: string;
    }[]
  }) {
    this.possibleChipsArray = value.array;
    this.keys = value.data.map(key => {
      return {
        class: this.class(key),
        label: this.label(key),
      };
    });
  }

  ngOnInit(): void {
  }

  public class(key: string): string {
    const found = this.possibleChipsArray.find(value => {
      return value.value === key;
    });
    if (!!found) {
      return `table-chip-${found.color}`;
    }
    else {
      return '';
    }
  }

  public label(key: string): string {

    const found = this.possibleChipsArray.find(value => {
      return value.value === key;
    });
    if (!!found) {
      return found.label;
    }
    else {
      return '';
    }
  }

}
