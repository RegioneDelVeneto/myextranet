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
import { AfterViewInit, Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { PageEvent } from '@angular/material/paginator';
import { CommunicationService } from 'src/app/services/communication.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { DataTableService, EngDynamicFormsComponent, FormContainerConfig, IForm } from '@eng/morfeo';
import { Models } from '../../../models/model';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { EnteService } from 'src/app/services/ente.service';
import { SortDirection } from '@angular/material/sort';
import { FiltersHistoryService } from 'src/app/services/filters-history.service';

@Component({
  selector: 'app-tenants-list',
  templateUrl: './tenants-list.component.html',
  styleUrls: ['./tenants-list.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class TenantsListComponent implements OnInit, AfterViewInit {
  @ViewChild('morfeoTenantsListFilter') morfeoFilter: EngDynamicFormsComponent;

  public sortByFilter: string = 'denominazione';
  public sortByHeaderFilter: string = 'denominazione';
  public sortOrder: SortDirection = 'asc';

  public lastSearchFilter: any = null;

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Enti' }
    )
  };

  public formJson: IForm = {

    components: [
      { key: 'codIpa', type: 'textfield', label: 'Codice IPA' },
      { key: 'denominazione', type: 'textfield', label: 'Denominazione' }
    ]

  };

  public isLoading = false;

  public tableHeader: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'denominazione',
      header: 'Denominazione',
      span: 6
    },
    {
      sortActive: true,
      definition: 'codIpa',
      header: 'Codice IPA',
      span: 3
    },

    {
      sortActive: true,
      definition: 'comuneProvincia',
      header: 'Comune',
      span: 4
    },
    {
      sortActive: true,
      definition: 'dtInizioValLD',
      header: 'Valido da',
      type: 'date',
      dateFormat: 'dd/MM/y',
      span: 4
    },
    {
      sortActive: true,
      definition: 'dtFineValLD',
      header: 'Valido al',
      type: 'date',
      dateFormat: 'dd/MM/y',
      span: 4
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [{
        icon: 'edit',
        event: 'edit',
        label: 'Dettaglio'
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

      this.lastSearchFilter = JSON.parse(JSON.stringify(f.form.value));
      this.pageIndex = 0;
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
    },
    showReset: true,
    resetIcon: 'clear',
    resetLabel: 'CANCELLA',
    resetCallback: (f: NgForm) => {
      this.pageIndex = 0;
      this.sortByFilter = 'denominazione';
      this.sortByHeaderFilter = 'denominazione';
      this.sortOrder = 'asc';
      this.formValue = JSON.parse(JSON.stringify(f.form.value));
      this.lastSearchFilter = JSON.parse(JSON.stringify(f.form.value));
      this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
    }
  };

  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;
  public tenantsDataSource: { [key: string]: string | Date }[] = [];


  private formValue: { [jkey: string]: any } = null;


  constructor(
    private router: Router,
    private tableService: DataTableService,
    public dialog: MatDialog,
    private communicationService: CommunicationService,
    private enteService: EnteService,
    private myextranetTableSrv: MyextTableService,
    private filterHistory: FiltersHistoryService
  ) { }

  ngOnInit(): void {
    const filterFromHistory = this.filterHistory.getSavedData();

    if (!!filterFromHistory) {
      const paginataor = filterFromHistory.paginator;
      const sort = filterFromHistory.sort;
      this.formValue = filterFromHistory.filter;
      this.pageSize = paginataor.pageSize;
      this.sortByFilter = sort.sortByFilter;
      this.sortOrder = sort.sortOrder as SortDirection;
      this.sortByHeaderFilter = sort.sortByHeader;
      this.length = paginataor.length;
      this.lastSearchFilter = filterFromHistory.filter;
    }
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);

  }

  ngAfterViewInit(): void {
    this.morfeoFilter.formReadyEvent.subscribe(val => {
      if (!!this.formValue) this.morfeoFilter.f.form.patchValue(this.formValue);
    });
  }

  getDataFromService(pageindex: number = 1, pagesize: number = 10, sortBy?: string, filter?: any, order?: string): void {
    this.isLoading = true;
    let url = `/ente?activeOnly=true&_page=${pageindex}&_pageSize=${pagesize}`;
    
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
    this.myextranetTableSrv.getData(url).then(response => {
      if (pageindex === 1 || pageindex === 0) {
        this.length = response.pagination.totalRecords;
      }
      response.records = response.records.map((item: { comune: { desComune: string, provincia: { codProvincia: string } } }) => ({
        ...item, comuneProvincia: `${item.comune.desComune} (${item.comune.provincia.codProvincia})`
      }));
      this.tenantsDataSource = Array.from(response.records);
      this.filterHistory.register(this.formValue, { length: this.length, pageSize: this.pageSize, pageIndex: this.pageIndex }, { sortByFilter: this.sortByFilter, sortByHeader: this.sortByHeaderFilter, sortOrder: this.sortOrder });
      this.isLoading = false;

    }).catch(err => {
      this.isLoading = false;
    });
  }

  updatePaginator(event: PageEvent): void {
    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);
  }

  public manageEvent(event: { element: any, eventType: string }) {
    if (!event) return null;
    switch (event.eventType) {
      case 'edit': {
        if (event.element) {
          this.navigateToDetail(event.element);
        }
        break;
      }
    }
  }


  public navigateToDetail(row?: { idEnte: number }): void {

    const path = ['backoffice/tenants/tenant-detail'];
    if (row) {
      path.push(`${row.idEnte}`);
    }
    this.router.navigate(path);
  }


  openDiag(): void {
  }

  manageSort(event: { active: string, direction: SortDirection }) {

    if (!!event.direction) {
      this.sortByFilter = this.filterNameConversion(event.active);
      this.sortByHeaderFilter = event.active;
      this.sortOrder = event.direction;
    }
    else {
      this.sortByFilter = null;
      this.sortByHeaderFilter = null;
      this.sortOrder = null;
    }

    this.isLoading = true;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, this.sortByFilter, this.formValue, this.sortOrder);

  }

  private filterNameConversion(filterIn: string): string {
    switch (filterIn) {

      case 'dtInizioValLD': {
        return 'dtInizioVal';
      }
      case 'dtFineValLD': {
        return 'dtFineVal';
      }
      case 'comuneProvincia': {
        return 'comune.desComune';
      }
      default: {
        return filterIn;
      }
    }
  }

}
