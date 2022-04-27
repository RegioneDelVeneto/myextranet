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
import { Router } from '@angular/router';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { MysysconfigService } from 'src/app/services/mysysconfig.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { ComboService, EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';
import { NgForm } from '@angular/forms';
import { PageEvent } from '@angular/material/paginator';
import { Models } from '../../../models/model';
import { environment } from 'src/environments/environment';
import { SortDirection } from '@angular/material/sort';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';


@Component({
  selector: 'app-projects-list',
  templateUrl: './projects-list.component.html',
  styleUrls: ['./projects-list.component.scss'],
  encapsulation: ViewEncapsulation.None,

})
export class ProjectsListComponent implements OnInit, AfterViewInit {

  @ViewChild('morfeoProjectsListFilter') morfeoFilter: EngDynamicFormsComponent;

  
  public sortBy: string = 'titolo';
  public sortOrder: SortDirection = 'asc';

  public projectsDataSource: { [key: string]: string | Date }[] = []; 

  private formValue: { [jkey: string]: any } = null;
  public products: Models.ComboElement[] = [];
  private myIntrnetBaseUrl = '';


  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Progetti' }
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
      columns: [
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
    }, {
      key: 'withPendingRequestsOnly',
      label: 'Solo progetti con richieste pendenti',
      type: 'checkbox'
    }]
  };


  public tableHeader: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'titolo',
      header: 'Titolo',
      span: 5
    },
    {
      sortActive: true,
      definition: 'descrizione',
      header: 'Descrizione',
      type: 'html',
      span: 5
    },
    {
      definition: 'prodotto',
      header: 'Prodotto',
      type: 'association',
      associationLabel: 'titolo',
      span: 3
    },
    {
      sortActive: true,
      definition: 'pendingCounter',
      header: 'Richieste pendenti'
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
        icon: 'groups',
        event: 'collab',
        label: 'Collaboratori',
        acl: 'myextranet.progetti',
        permissions: ['visualizza']
      },
      {
        icon: 'info',
        event: 'requests',
        label: 'Richieste',        
        acl: 'myextranet.progetti',
        permissions: ['visualizza']
      }
      ],
      span: 1
    }
  ];

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
    resetIcon: 'clear',
    resetLabel: 'CANCELLA',
    resetCallback: (f: NgForm) => {
      this.pageIndex = 0;
      this.sortBy = 'titolo';
      this.sortOrder = 'asc';
      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, f.form.value, this.sortOrder);
    }
  };

  constructor(

    private myportalService: MyportalService,
    private comboService: ComboService,
    private router: Router,
    private datePipe: DatePipe,
    private myextranetTableSrv: MyextTableService,
    private mysysConfigSrv: MysysconfigService,
    private filterHistory: FiltersHistoryService
  ) {
    this.mysysConfigSrv.getMyintranetUrl().then(url => this.myIntrnetBaseUrl = url);
  }

  ngOnInit(): void {
    this.myportalService.getAllProducts(false).then(val => {
      let productsRecords = val.records as Models.MyPortalContent[];
      this.products = productsRecords.map(v => ({ label: v.titolo, value: v.id }));
      this.comboService.setList('prodotto', this.products);
    }).catch(err => {
      console.error(err);
    });
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
  }



  ngAfterViewInit(): void {
    this.morfeoFilter.formReadyEvent.subscribe(val => {
      if (!!this.formValue) this.morfeoFilter.f.form.patchValue(this.formValue);
    });
  }


  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    let url = `/myportal/progetti-richieste-pendenti?_page=${pageindex}&_pageSize=${pagesize}`;

    if (!!sortBy) {
      url = `${url}&_orderBy=${sortBy}`;
    }
    if (!!order) {
      url = `${url}&_orderDir=${order === 'desc' ? true : false}`;
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


      this.projectsDataSource = Array.from(val.records);
      this.isLoading = false;
    }).catch(err => {
      this.isLoading = false;
    });
  }

  updatePaginator(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortBy, this.formValue, this.sortOrder);
  }

  private navigateToMyIntranet(row: Models.MyPortalEvent): void {

    window.open(`${this.myIntrnetBaseUrl}/admin/my-cms/content-browser${row.parent}?contentId=${row.id}`, '_blank');

  }

  public manageEvent(event: { element: any, eventType: string }) {
    if (!event) return null;
    switch (event.eventType) {
      case 'edit': {
        if (event.element) {
          this.navigateToMyIntranet(event.element);
        }
        break;
      }
      case 'collab': {
        if (event.element && event.element.id) {
          this.router.navigate(['backoffice/projects/collaborators-list'], { queryParams: { projectId: event.element.id } });
        }
        break;
      }
      case 'requests': {
        if (event.element && event.element.id) {
          this.router.navigate(['backoffice/projects/collaborators-requests-list'], { queryParams: { projectId: event.element.id } });
        }
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
