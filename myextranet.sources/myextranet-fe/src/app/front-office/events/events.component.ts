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
import { PageEvent } from '@angular/material/paginator';
import { Models } from 'src/app/models/model';
import { EventsService } from 'src/app/services/events.service';
import { MysysconfigService } from 'src/app/services/mysysconfig.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-events',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.scss']
})
export class EventsComponent implements OnInit {



  public isLoading: boolean;

  public eventsCardList: Models.MyPortalEvent[];

  public length = 0;
  public pageSize = 9;
  public pageSizeOptions: number[] = [3, 9, 12, 90];
  public pageIndex = 0;

  private myportalUrl: string;

  breadcrumbs = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca', url: '/utente/bacheca' },
      { label: 'I miei eventi' }
    )
  };


  showAs = 'grid';

  constructor(
    private eventsService: EventsService,
    private mysysConfigSrv: MysysconfigService
  ) { }

  ngOnInit(): void {
    this.isLoading = true;
    this.mysysConfigSrv.returnMyPortalUrl().then(mypUrl => {
      this.myportalUrl = mypUrl;
      this.getDataFromService();
    });

  }


  getDataFromService(pageindex: number = 1, pagesize: number = 9, sortBy?: string): void {
    this.isLoading = true;
    this.eventsService.getMyEvents(pageindex, pagesize).then(val => {
      if (pageindex === 1 || pageindex === 0) {
        this.length = val.pagination.totalRecords;
      }
      let eventslist: Models.MyPortalEvent[] = val.records as Models.MyPortalEvent[];
      this.eventsCardList = eventslist.map((mypEvent: Models.MyPortalEvent) => {
        return {
          ...mypEvent,

          logo: this.myportalUrl.endsWith('/') ? `${this.myportalUrl}api/content/download?id=${mypEvent.logo}` : `${this.myportalUrl}/api/content/download?id=${mypEvent.logo}`, //logo

        };
      });
      this.isLoading = false;
    }).catch(err => {
      this.isLoading = false;
    });
  }

  updatePaginator(event: PageEvent): void {

    this.pageSize = event.pageSize;
    this.pageIndex = event.pageIndex;
    this.getDataFromService(this.pageIndex + 1, this.pageSize, null);
  }

  navigateToMyPortalEvents() {
    window.open(`${this.myportalUrl}eventi`);
  }

}
