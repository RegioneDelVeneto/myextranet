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
import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AlertService } from 'src/app/services/alert.service';
import { MysysconfigService } from 'src/app/services/mysysconfig.service';
import { UserService } from 'src/app/services/user.service';
import { Models } from '../../models/model';

@Component({
  selector: 'app-backoffice-layout',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './backoffice-layout.component.html',
  styleUrls: ['./backoffice-layout.component.scss']
})
export class BackofficeLayoutComponent implements OnInit, OnDestroy {

  public sidenavOpened: boolean = true;



  private progressObs = this.progressBarService.getProgressBarObservable();

  private progressObsSubscription: Subscription;

  public progressBars: { progress: number, operation: string, id: string }[] = [];

  private myportalUrl: string;
  private interval: any;
  userInfo: Models.UserInfo = null;

  constructor(
    private progressBarService: AlertService,
    private userService: UserService,
    private mySysConfSrv: MysysconfigService,
    private router: Router
  ) { }


  ngOnInit(): void {
    this.mySysConfSrv.getMyPortalDeployURL().then(url => this.myportalUrl = url).catch(err => console.error(err));

    this.userService.getUserInfo().then(userDto => {
      this.userInfo = userDto;

    });

    this.progressObsSubscription = this.progressObs.subscribe(val => {
      const id: string = val.id;
      const idx: number = this.progressBars.map(pbar => pbar.id).indexOf(id);
      if (idx === -1 && val.progress < 101) {
        this.progressBars.push(val);
      } else if (val.progress < 101) {
        this.progressBars[idx] = val;
      } else if (idx !== -1 && val.progress > 99) {
        this.progressBars.splice(idx, 1);
      }

    });
  }

  ngOnDestroy(): void {
    this.progressObsSubscription.unsubscribe();
  }

  logout() {

    this.userService.logout().then(() => {
      window.open(this.myportalUrl, '_self');

    });
  }

  public navigateToRoute(route: string): void {
    this.router.navigate([`/backoffice/${route}`]);
  }
}
