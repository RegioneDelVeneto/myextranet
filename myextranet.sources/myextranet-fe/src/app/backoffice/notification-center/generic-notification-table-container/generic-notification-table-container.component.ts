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
import { Component, EventEmitter, Input, OnInit, Output, TemplateRef, ViewEncapsulation } from '@angular/core';
import { PageEvent } from '@angular/material/paginator';
import { SortDirection } from '@angular/material/sort';
import { Models } from 'src/app/models/model';
import { MyextTableService } from 'src/app/services/myext-table.service';

@Component({
  selector: 'app-generic-notification-table-container',
  templateUrl: './generic-notification-table-container.component.html',
  styleUrls: ['./generic-notification-table-container.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class GenericNotificationTableContainerComponent implements OnInit {


  @Input() public templateHeader: TemplateRef<any>;

  @Input() public tableHeader: Models.TableHeader[] = [];

  @Input() public type: string = null;

  @Input() public sortField: { active: string, direction: SortDirection } = { active: null, direction: 'asc' };

  @Output() public actionEvent: EventEmitter<any> = new EventEmitter<any>();
  @Output() public loadingStatus: EventEmitter<{ isLoading: boolean, count: number, type: string }> = new EventEmitter<{ isLoading: boolean, count: number, type: string }>();

  public dataSource: any[] = [];

  public length = 0;
  public pageSize = 5;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;

  constructor(private myextranetTableSrv: MyextTableService) { }

  ngOnInit(): void {
    this.getDataFromService(1, 5, this.sortField.active, null, this.sortField.direction);
  }

  public getDataFromService(pageindex: number = 1, pagesize: number = 5, sortBy?: string, filter?: any, order?: string): void {
    this.loadingStatus.emit({count: 0, isLoading : true, type : this.type});
    let urlProdAtt = `/prodotto-attivato?_page=${pageindex}&_pageSize=${pagesize}&withPendingRequests=true`;
    let urlProjects = `/myportal/progetti-richieste-pendenti?_page=${pageindex}&_pageSize=${pagesize}&withPendingRequestsOnly=true`;
    let urlRAP = `/rappresentante-ente-rap?_page=${pageindex}&_pageSize=${pagesize}&flgRichInCorso=1`;

    let mapper: (data: any) => any = (data) => data;

    let url: string = null;

    switch (this.type) {
      case 'RAP': {
        url = urlRAP;
        mapper = (item: { utente: Models.utenteDTO, ente: Models.enteDTO, utenteNome: string, utenteCognome: string, enteDesc: string }) => ({
          ...item,
          utenteNome: item.utente.nome,
          utenteCognome: item.utente.cognome,
          enteDesc: item.ente.denominazione
        });
        if (!!order) {
          url = `${url}&_orderDir=${order}`;
        }
        break;
      }
      case 'Projects': {
        url = urlProjects;
        if (!!order) {
          url = `${url}&_orderDir=${order === 'desc' ? true : false}`;
        }

        break;
      }
      case 'Products': {
        url = urlProdAtt;
        mapper = (item: { ente: Models.enteDTO, stato: 1 | 2 | 0, prodottoAttivabile: { nomeProdottoAttiv: string } }) => ({
          ...item,
          enteDef: item.ente.denominazione,
          prodottoNome: item.prodottoAttivabile.nomeProdottoAttiv,
          areUserEditable: item.stato === 2
        });
        if (!!order) {
          url = `${url}&_orderDir=${order}`;
        }
        break;
      }
    }

    if (!!sortBy) {
      url = `${url}&_orderBy=${sortBy}`;
    }


    if (!!filter) {
      Object.keys(filter).forEach(key => {
        if (filter[key] !== null && filter[key] !== undefined) {
          if (key === 'dataDa' || key === 'dataA') {
            url = `${url}&${key}=${new Date(filter[key]).valueOf()}`;
          } else {
            url = `${url}&${key}=${filter[key]}`;
          }

        }
      });
    }
    this.myextranetTableSrv.getData(url).then(response => {      
      if (pageindex === 1 || pageindex === 0) {
        this.length = response.pagination.totalRecords;
      }

      response.records = response.records.map(mapper);

      this.dataSource = Array.from(response.records);
      this.loadingStatus.emit({count: this.length, isLoading : false, type : this.type});      
    }).catch(err => {
      
    });
  }


  public manageSort(event: { active: string, direction: SortDirection }): void {
    
    let sortBy: string = '';
    let sortOrder: string = 'asc';
    let sortByHeader: string = '';
    switch (this.type) {
      case 'RAP': {

        if (!!event.direction) {
          sortBy = this.filterNameConversionRAP(event.active);
          sortByHeader = event.active;
          sortOrder = event.direction;
        }
        else {
          sortBy = null;
          sortByHeader = null;
          sortOrder = null;
        }

        break;
      }
      case 'Projects': {
        if (!!event.direction) {
          sortBy = event.active;
          sortByHeader = sortBy;
          sortOrder = event.direction;
        }
        else {
          sortBy = null;
          sortByHeader = null;
          sortOrder = null;
        }

        break;
      }
      case 'Products': {


        if (!!event.direction) {
          sortBy = this.filterNameConversionProd(event.active);
          sortByHeader = event.active;
          sortOrder = event.direction;
        }
        else {
          sortBy = null;
          sortByHeader = null;
          sortOrder = null;
        }

        break;
      }
    }
    this.sortField.active = sortByHeader;
    this.getDataFromService(1, 5, sortBy, null, sortOrder);
  }

  private filterNameConversionRAP(filterIn: string): string {
    switch (filterIn) {
      case 'utenteNome': {
        return 'utente.nome';
      }
      case 'utenteCognome': {
        return 'utente.cognome';
      }
      case 'enteDesc': {
        return 'ente.denominazione';
      }
      default: {
        return filterIn;
      }
    }
  }

  private filterNameConversionProd(filterIn: string): string {
    switch (filterIn) {
      case 'enteDef': {
        return 'ente.denominazione';
      }
      case 'prodottoNome': {
        return 'prodottoAttivabile.nomeProdottoAttiv';
      }
      default: {
        return filterIn;
      }
    }
  }

  public manageEvent(event: { element: any, eventType: string }): void {
    this.actionEvent.emit(event);
  }

  updatePaginator(event: PageEvent): void {    
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortField.active, null, this.sortField.direction);
  }
}
