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
import { Component, EventEmitter, Input, OnInit, Output, ViewChild, ViewEncapsulation } from '@angular/core';
import { SortDirection } from '@angular/material/sort';
import { GeneralDataTableComponent } from '../general-data-table/general-data-table.component';

@Component({
  selector: 'app-card-table-container',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './card-table-container.component.html',
  styleUrls: ['./card-table-container.component.scss']
})
export class CardTableContainerComponent implements OnInit {

  @ViewChild(GeneralDataTableComponent) dataTableComponent: GeneralDataTableComponent;


  @Input() dataSource:
    {
      data: {
        [key: string]: string | Date | any
      }[],
      preselectedRows: { objMapper: any, key: string },
      sortField?: {
        active: string, direction: SortDirection;
      }
    } = { data: [], preselectedRows: null };

  @Input() configuration: { title?: string, icon?: string, subtitle?: string, showAll?: boolean, };
  @Input() footer: {
    active: boolean,
    buttons: {
      label: string,
      event: string,
      icon: string,
      color?: string
    }[]
  } = null;
  @Input() displayedColumns: { definition: string, header: string, span?: number }[] = [];

  @Output() toolBarEvent: EventEmitter<{ element: any, eventType: string, index: number }> = new EventEmitter<{ element: any, eventType: string, index: number }>();

  @Output() sortEvent: EventEmitter<{ active: string, direction: SortDirection }> = new EventEmitter<{ active: string, direction: SortDirection }>();

  constructor() { }


  ngOnInit(): void {
  }

  manageSort(event: any) {
    this.sortEvent.emit(event);
  }

}
