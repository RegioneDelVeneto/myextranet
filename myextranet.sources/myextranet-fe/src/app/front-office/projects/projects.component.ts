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
import { MysysconfigService } from 'src/app/services/mysysconfig.service';
import { ProjectsService } from 'src/app/services/projects.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-projects',
  templateUrl: './projects.component.html',
  styleUrls: ['./projects.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProjectsComponent implements OnInit {


  public isLoading: boolean;

  public projectCardsList: Models.ProgettoExtended[];

  public length = 0;
  public pageSize = 3;
  public pageSizeOptions: number[] = [3, 9, 12, 90];
  public pageIndex = 0;

  private myportalUrl: string;

  breadcrumbs = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca', url: '/utente/bacheca' },
      { label: 'I miei Progetti' }
    )
  };


  showAs = 'grid';


  constructor(
    private projectsService: ProjectsService,
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
    this.projectsService.getProjectsForUserFO(pageindex, pagesize).then(val => {
      if (pageindex === 1 || pageindex === 0) {
        this.length = val.pagination.totalRecords;
      }
      let pjExtended: Models.ProgettoExtended[] = val.records as Models.ProgettoExtended[];
      this.projectCardsList = pjExtended.map((mypProjectExtended: Models.ProgettoExtended) => {
        let returnedProject = mypProjectExtended;
        if (!!returnedProject && !!returnedProject.myExtranetContent && !!returnedProject.myExtranetContent.logo) {
          returnedProject.myExtranetContent.logo = this.myportalUrl.endsWith('/') ? `${this.myportalUrl}api/content/download?id=${returnedProject.myExtranetContent.logo}` : `${this.myportalUrl}/api/content/download?id=${returnedProject.myExtranetContent.logo}`;
        }
        return returnedProject;
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

  navigateToMyPortalProjects() {
    window.open(`${this.myportalUrl}progetti_gruppi_lavoro`);

  }
}
