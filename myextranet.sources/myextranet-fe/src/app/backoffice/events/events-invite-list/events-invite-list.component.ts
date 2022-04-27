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
import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { NgForm } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { SortDirection } from '@angular/material/sort';
import { ActivatedRoute } from '@angular/router';
import { FormContainerConfig, IForm } from '@eng/morfeo';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { environment } from 'src/environments/environment';
import { Models } from '../../../models/model';

@Component({
  selector: 'app-events-invite-list',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './events-invite-list.component.html',
  styleUrls: ['./events-invite-list.component.scss']
})
export class EventsInviteListComponent implements OnInit {

  public sortBy: string = 'dtInvio';
  public sortOrder: SortDirection = 'desc';

  public eventId: string = null;

  public event: Models.MyPortalEvent = null;

  public formJson: IForm = {
    components: [{
      key: 'row1',
      label: 'Columns',
      type: 'columns',
      readOnly: false,
      columns: [{
        components: [
          { key: 'titolo', type: 'textfield', label: 'Titolo' },
        ]
      },
      {
        components: [
          { key: 'descrizione', type: 'textfield', label: 'Descrizione' },
        ]
      }]

    }]
  };

  public breadcrumbs = null;

  public isLoading = true;

  public tableHeader: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'dtInvio',
      type: 'date',
      header: `Data d'invio`,
      dateFormat: 'dd/MM/y',
      span: 3
    },
    {
      sortActive: true,
      definition: 'destinatario',
      header: 'Nome e cognome',
      span: 4
    },
    {
      sortActive: true,
      definition: 'indirizzo',
      header: 'Mail',
      span: 3
    }
  ];

  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;
  public eventsDataSource: { [key: string]: string | Date }[] = [];


  private formValue: { [jkey: string]: any } = null;


  public formFilterConfig: FormContainerConfig = {
    showSubmit: true,
    submitLabel: 'CERCA',
    submitIcon: 'search',
    submitCallback: (f: NgForm) => {
      this.formValue = JSON.parse(JSON.stringify(f.form.value));

      this.pageIndex = 0;
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);
    },
    showReset: true,
    resetIcon: 'clear',
    resetLabel: 'CANCELLA',
    resetCallback: (f: NgForm) => {
      this.pageIndex = 0;
      this.sortBy = 'dtInvio';
      this.sortOrder = 'desc';
      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, f.form.value, this.sortOrder);
    }
  };

  constructor(
    private route: ActivatedRoute,
    private myPortalService: MyportalService,
    private myextranetTableSrv: MyextTableService,
    private filterHistory: FiltersHistoryService
  ) { }

  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    let url = `/invito?_page=${pageindex}&_pageSize=${pagesize}&idContenuto=${this.eventId}`;

    if (!!sortBy) {
      url = `${url}&_orderBy=${sortBy}`;
    }
    if (!!order) {
      url = `${url}&_orderDir=${order}`;
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
    this.myextranetTableSrv.getData(url).then(val => {
      if (pageindex === 1 || pageindex === 0) {
        this.length = val.pagination.totalRecords;
      }
      this.filterHistory.register(this.formValue, { length: this.length, pageSize: this.pageSize, pageIndex: this.pageIndex }, { sortByFilter: this.sortBy, sortByHeader: null, sortOrder: this.sortOrder });

      this.isLoading = false;
      this.eventsDataSource = Array.from(val.records);

    }).catch(err => {
      this.isLoading = false;
    });
  }


  updatePaginator(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);
  }


  ngOnInit(): void {

    this.route.queryParamMap.subscribe(queryParams => {
      if (queryParams.has('eventId')) {
        this.eventId = queryParams.get('eventId');
        this.breadcrumbs = {
          base: '',
          entries: new Array<IBreadcrumbsEntry>(
            { label: 'MyExtranet', url: `backoffice` },
            { label: 'Eventi', url: `backoffice/events` },
            { label: `Lista Inviti` }
          )
        };
        this.myPortalService.getContent(this.eventId, false).then((val: Models.MyPortalEvent) => {
          this.event = val;
          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'MyExtranet', url: `backoffice` },
              { label: 'Eventi', url: `backoffice/events` },
              { label: `Lista Inviti all'evento : ${this.event.titolo}` }
            )
          };
          const filterFromHistory = this.filterHistory.getSavedData();

          if (!!filterFromHistory) {
            const paginataor = filterFromHistory.paginator;
            const sort = filterFromHistory.sort;
            this.formValue = filterFromHistory.filter;
            this.pageSize = paginataor.pageSize;
            this.sortBy = sort.sortByFilter;
            this.sortOrder = sort.sortOrder as SortDirection;
            this.length = paginataor.length;
          }
          this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);

        }).catch(err => {
          this.eventId = null;
          this.event = null;
          this.isLoading = false;
          console.error(err);
        });
      }

    });

  }

  manageSort(event: { active: string, direction: SortDirection }) {

    if (!!event.direction) {
      this.sortBy = event.active;
      this.sortOrder = event.direction;
    }
    else {
      this.sortBy = null;
      this.sortOrder = null;
    }

    this.isLoading = true;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);

  }


}
