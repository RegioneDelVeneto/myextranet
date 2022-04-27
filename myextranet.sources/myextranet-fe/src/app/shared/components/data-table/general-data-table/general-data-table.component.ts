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
import { SelectionChange, SelectionModel } from '@angular/cdk/collections';
import { Component, EventEmitter, Input, OnInit, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { SortDirection } from '@angular/material/sort';
import { MatTable } from '@angular/material/table';
import { Models } from 'src/app/models/model';

@Component({
  selector: 'app-general-data-table',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './general-data-table.component.html',
  styleUrls: ['./general-data-table.component.scss']
})
export class GeneralDataTableComponent implements OnInit {

  constructor() { }

  @ViewChild(MatTable) dataTable: MatTable<any>;

  @Input() lineThroughKeyCheck: string = null;

  public sortField: {
    active: string, direction: SortDirection;
  } = null;

  private _dataSource: { [key: string]: string | Date | any }[] = [];
  set dataSource(ds: { [key: string]: string | Date | any }[]) {
    this._dataSource = ds;
  }
  get dataSource() {
    return this._dataSource;
  }
  @Input() set dataSourceInput(datasrcInput: {
    data: {
      [key: string]: string | Date | any
    }[],
    preselectedRows: { objMapper: any, key: string },
    sortField?: {
      active: string, direction: SortDirection;
    }
  }) {
    this.dataSource = datasrcInput.data;
    this.preselectedRows = datasrcInput.preselectedRows;
    this.sortField = datasrcInput.sortField;
  }
  @Output() sortEvent: EventEmitter<{ active: string, direction: SortDirection }> = new EventEmitter<{ active: string, direction: SortDirection }>();

  private _preselectedRows: { objMapper: any, key: string } = null;

  set preselectedRows(val: { objMapper: any, key: string }) {
    this.selection.clear();
    if (!!val && !!val.objMapper && !!val.key && typeof (val.objMapper) !== 'string') {
      Object.keys(val.objMapper).forEach(k => {
        const dataRow = this.dataSource.find(data => data[val.key] == k);
        if (dataRow && !this.selection.isSelected(dataRow) && !!val.objMapper[k]) {
          this.selection.select(dataRow);
        } else if (dataRow && this.selection.isSelected(dataRow) && !val.objMapper[k]) {
          this.selection.deselect(dataRow);
        }

      });
      this._preselectedRows = val;
    }

  }
  get preselectedRows() {
    return this._preselectedRows;
  }


  public displayedColumnsDefinition: string[] = [];

  public displayedColumnsHeader: string[] = [];

  public displayedColumnsSpan: number[] = [];

  private _displayedColumns: Models.TableHeader[] = [];

  @Input() set displayedColumns(value: Models.TableHeader[]) {
    this._displayedColumns = value;
    this.displayedColumnsHeader = value.map(v => v.header);
    this.displayedColumnsDefinition = value.map(v => v.definition);
    this.displayedColumnsSpan = value.map(v => {
      return (v.span || 1) * 100 / ((value.reduce((total, v) => {
        return total + (v.span || 1);
      }, 0)));
    });
  }
  get displayedColumns(): Models.TableHeader[] {
    return this._displayedColumns;
  }



  @Input() footer: {
    active: boolean,
    buttons: {
      label: string,
      event: string,
      icon: string,
      color?: string
    }[]
  } = null;

  @Output() toolBarEvent: EventEmitter<{ element: any, eventType: string, index: number }> = new EventEmitter<{ element: any, eventType: string, index: number }>();

  manageEvent(element: any, eventType: string, tbIdx: number): void {
    this.toolBarEvent.emit({ element, eventType, index: tbIdx });
  }

  selection = new SelectionModel<any>(true, [], true);

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.length;
    return numSelected === numRows;
  }

  /** Selects all rows if they are not all selected; otherwise clear selection. */
  masterToggle(showOnlyIf: string) {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.forEach(row => {
        if (!showOnlyIf || (!!showOnlyIf && !!row[showOnlyIf])) {
          this.selection.select(row);
        }
      });
  }

  /** The label for the checkbox on the passed row */
  checkboxLabel(row?: any): string {
    if (!row) {
      return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.position + 1}`;
  }
  onCheckBoxChange(row) {
    this.selection.toggle(row);

  }

  ngOnInit(): void {
    this.selection.changed.subscribe((val: SelectionChange<any>) => {      
      this.manageEvent(val, 'selection', null);
    })
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
    } else if (keyString.startsWith('$or:[') && keyString.endsWith(']')) { // || -> any
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

  manageSort(event: any) {    
    this.sortEvent.emit(event);
  }


}
