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
import { ActivatedRoute, Router } from '@angular/router';
import { Models } from 'src/app/models/model';
import { EnteService } from 'src/app/services/ente.service';
import { EventsService } from 'src/app/services/events.service';
import { MysysconfigService } from 'src/app/services/mysysconfig.service';
import { ProjectsService } from 'src/app/services/projects.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-bacheca',
  templateUrl: './bacheca.component.html',
  styleUrls: ['./bacheca.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class BachecaComponent implements OnInit {


  public isLoading: boolean;

  public eventsCardList: Models.MyPortalEvent[];
  public projectCardsList: Models.ProgettoExtended[];
  public rappEnte: Models.RappEnteDTO[];

  public length = 0;
  public pageSize = 10;
  public pageSizeOptions: number[] = [5, 10, 25, 100];
  public pageIndex = 0;

  private myportalUrl: string;

  breadcrumbs = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca'},
      //      { label: `Iscrizione all'evento` },
    )
  };


  constructor(
    private eventsService: EventsService,
    private mysysConfigSrv: MysysconfigService,
    private projectsService: ProjectsService,
    private enteService: EnteService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.isLoading = true;
    this.mysysConfigSrv.returnMyPortalUrl().then(mypUrl => {
      this.myportalUrl = mypUrl;
      this.getDataFromService();
    });

  }


  getDataFromService(pageindex: number = 1, pagesize: number = 3, sortBy?: string): void {
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
      return this.projectsService.getProjectsForUserFO(pageindex, pagesize)
    }).then(val => {
      if (pageindex === 1 || pageindex === 0) {
        this.length = val.pagination.totalRecords;
      }
      let pjExtended: Models.ProgettoExtended[] = val.records as Models.ProgettoExtended[];
      this.projectCardsList = pjExtended.map((mypProjectExtended: Models.ProgettoExtended) => {
        let returnedProject = mypProjectExtended;
        if (!!returnedProject && !!returnedProject.myExtranetContent && !!returnedProject.myExtranetContent.logo) {
          returnedProject.myExtranetContent.logo = this.myportalUrl.endsWith('/') ? `${this.myportalUrl}api/content/download?id=${returnedProject.myExtranetContent.logo}` : `${this.myportalUrl}/api/content/download?id=${returnedProject.myExtranetContent.logo}`; //logo
        }
        return returnedProject;
      });
      return this.enteService.getEntiRappresentati(pageindex, pagesize)
    }).then(val => {
      if (pageindex === 1 || pageindex === 0) {
        this.length = val.pagination.totalRecords;
      }
      let entiRap: Models.RappEnteDTO[] = val.records as Models.RappEnteDTO[];
      this.rappEnte = entiRap

      this.isLoading = false;
    }).catch(err => {
      this.isLoading = false;
    });
  }

  navigateToMyPortal( type : string) {
    //window.open()
    switch(type) {
      case 'events' : {
        window.open(`${this.myportalUrl}eventi`);
        break;

      }
      case 'projects' : {
        window.open(`${this.myportalUrl}progetti_gruppi_lavoro`);
        break;

      }
      case 'RAP' : {
        this.router.navigate(['/utente/prodotti/RAP']);
        break;
      }
    }
  }


}
