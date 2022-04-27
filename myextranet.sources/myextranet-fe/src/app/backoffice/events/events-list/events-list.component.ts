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
import { DatePipe } from '@angular/common';
import { AfterViewInit, Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { NgForm } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { SortDirection } from '@angular/material/sort';
import { Router } from '@angular/router';
import { ComboService, DataTableService, EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';
import { EventsService } from 'src/app/services/events.service';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { MysysconfigService } from 'src/app/services/mysysconfig.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { environment } from 'src/environments/environment';
import { Models } from '../../../models/model';

@Component({
  selector: 'app-events-list',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './events-list.component.html',
  styleUrls: ['./events-list.component.scss'],
})
export class EventsListComponent implements OnInit, AfterViewInit {


  constructor(
    private tableService: DataTableService,
    private myportalService: MyportalService,
    private comboService: ComboService,
    private eventsService: EventsService,
    private router: Router,
    private datePipe: DatePipe,
    private myextranetTableSrv: MyextTableService,
    private mysysConfigSrv: MysysconfigService,
    private filterHistory: FiltersHistoryService
  ) {

    this.mysysConfigSrv.getMyintranetUrl().then(url => this.myIntrnetBaseUrl = url);
  }


  public projects: Models.ComboElement[] = [];
  public products: Models.ComboElement[] = [];

  private myIntrnetBaseUrl = '';

  public sortBy: string = 'dateDa';
  public sortOrder: SortDirection = 'desc';

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Eventi' }
    )
  };

  public isLoading = false;

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
    }, {
      key: 'row2',
      label: 'Columns',
      type: 'columns',
      readOnly: false,
      columns: [{
        components: [
          {
            key: 'progetto',
            type: 'select',
            label: 'Progetto',
            defaultValue: null,
            hidden: false,
            data: {
              values: []
            },
            dataSrc: 'resource',
            suffix: '',
            input: true,
            valueProperty: 'id',
            labelProperty: 'titolo'
          },
        ]
      },
      {
        components: [
          {
            key: 'prodotto',
            type: 'select',
            label: 'Prodotto',
            defaultValue: null,
            hidden: false,
            data: {
              values: []
            },
            dataSrc: 'resource',
            suffix: '',
            input: true,
            valueProperty: 'id',
            labelProperty: 'titolo'
          },
        ]
      }]
    },
    {
      key: 'row3',
      label: 'Columns',
      type: 'columns',
      readOnly: false,
      columns: [{
        components: [
          { key: 'dataDa', type: 'datetime', label: 'Data inizio da' },
        ]
      },
      {
        components: [

          { key: 'dataA', type: 'datetime', label: 'Data inizio a' },
        ]
      }]
    }]
  };

  public get isOpen(): boolean {
    return !!this.formValue && !!Object.values(this.formValue).find(a => a !== null && a !== undefined && a !== '');

  }


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
    buttons: [{
      icon: 'download',
      label: 'Statistiche eventi',
      callback: () => window.open(this.eventsService.downloadStatisticheEventi(this.morfeoEventsForm.f.form.value)),
    }, {
      icon: 'download',
      label: 'Statistiche partecipanti',
      callback: () => window.open(this.eventsService.downloadStatistichePartecipantiEvento(this.morfeoEventsForm.f.form.value)),
    }],
    resetIcon: 'clear',
    resetLabel: 'CANCELLA',
    resetCallback: (f: NgForm) => {
      this.pageIndex = 0;
      this.sortBy = 'dateDa';
      this.sortOrder = 'desc';
      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, f.form.value, this.sortOrder);
    }
  };


  public tableHeader: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'dateDa',
      header: 'Data',
      type: 'date',
      span: 2.5
    },
    {
      sortActive: true,
      definition: 'titolo',
      header: 'Titolo',
      span: 5
    },
    {
      definition: 'luogo',
      header: 'Luogo',
      type: 'list',
      span: 5
    },
    {
      sortActive: true,
      definition: 'inPresenza',
      header: 'In presenza',
      type: 'boolean',
      span: 2
    },
    {
      sortActive: true,
      definition: 'annullato',
      header: 'Annullato',
      type: 'boolean',
      span: 1
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [{
        icon: 'edit',
        event: 'edit',
        label: 'Modifica'
      }, {
        icon: 'person_add',
        event: 'invite',
        label: 'Inviti',
        acl: 'myextranet.eventi',
        permissions: ['gestisci']
      }, {
        icon: 'people_alt',
        event: 'signup',
        label: 'Iscritti'
      },
      ],
      span: 1
    }
  ];

  public eventsDataSource: { [key: string]: string | Date }[] = [];

  private formValue: { [jkey: string]: any } = null;

  @ViewChild('morfeoEventsList') morfeoEventsForm: EngDynamicFormsComponent;

  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;

  updatePaginator(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);
  }

  ngOnInit(): void {
    const filterFromHistory = this.filterHistory.getSavedData();

    if (!!filterFromHistory && !!filterFromHistory.filter) {

      const paginataor = filterFromHistory.paginator;
      const sort = filterFromHistory.sort;
      this.formValue = filterFromHistory.filter;
      this.pageSize = paginataor.pageSize;
      this.sortBy = sort.sortByFilter;
      this.sortOrder = sort.sortOrder as SortDirection;

      this.length = paginataor.length;

    }
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);

  }


  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    let url = `/myportal/contents?_type=CT_myextranet_d_myext_events&_page=${pageindex}&_pageSize=${pagesize}`;

    if (!!sortBy) {
      url = `${url}&_orderBy=${sortBy}`;
    }
    if (!!order) {
      url = `${url}&_orderDir=${order === 'desc' ? true : false}`;
    }


    if (!!filter) {
      Object.keys(filter).forEach(key => {
        if (filter[key] !== null && filter[key] !== undefined) {
          if ((key === 'dataDa' || key === 'dataA') && !!(new Date(filter[key]).valueOf())) {
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


      val.records = val.records.map((res: Models.MyPortalEvent) =>
      ({
        ...res,
        luogo: [res.luogo, res.aula].filter(v => !!v)
      })
      );
      this.eventsDataSource = Array.from(val.records);
      this.isLoading = false;

    }).catch(err => {
      this.isLoading = false;
    });
  }

  public ngAfterViewInit(): void {

    this.morfeoEventsForm.formReadyEvent.subscribe(val => {
      if (!!this.formValue) this.morfeoEventsForm.f.form.patchValue(this.formValue);
    });
    this.myportalService.getAllProducts(false).then(val => {
      let myportalContent: Models.MyPortalContent[] = val.records as Models.MyPortalContent[];
      this.products = myportalContent.map(v => ({ label: v.titolo, value: v.id }));
      this.products.unshift({ label: '', value: null });
      this.comboService.setList('prodotto', this.products);

    }).catch(err => {
      console.error(err);
    });

    this.myportalService.getAllProjects(false).then(val => {
      let myportalContent: Models.MyPortalContent[] = val.records as Models.MyPortalContent[];
      this.projects = myportalContent.map((v) => ({ label: v.titolo, value: v.id }));
      this.projects.unshift({ label: '', value: null });
      this.comboService.setList('progetto', this.projects);
    }).catch(err => {
      console.error(err);
    });

  }

  private navigateToMyIntranet(row: Models.MyPortalEvent): void {

    window.open(`${this.myIntrnetBaseUrl}/admin/my-cms/content-browser${row.parent}?contentId=${row.id}`, '_blank');

  }

  public manageEvent(event: { element: any, eventType: string }) {
    if (!event) return null;
    switch (event.eventType) {
      case 'edit': {
        if (event.element) this.navigateToMyIntranet(event.element);
        break;
      }
      case 'invite': {
        if (event.element && event.element.id) this.router.navigate(['backoffice/events/invite-list'], { queryParams: { eventId: event.element.id } });
        break;
      }
      case 'signup': {
        if (event.element && event.element.id) this.router.navigate(['backoffice/events/members'], { queryParams: { eventId: event.element.id } });
        break;
      }
    }
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
