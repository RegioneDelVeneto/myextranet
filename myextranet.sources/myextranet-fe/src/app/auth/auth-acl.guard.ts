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
import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';
import { Models } from '../models/model';
import { AlertService } from '../services/alert.service';
import { UserService } from '../services/user.service';

@Injectable({
  providedIn: 'root'
})
export class AuthAclGuard implements CanActivate {


  private routeAclAndPemissionMap: Models.AclPermissionRouteMap[] =
    [
      {
        route: 'users',
        acl: 'myextranet.utenti',
        permissions: ['visualizza']
      },
      {
        route: 'users/detail',
        acl: 'myextranet.utenti',
        permissions: ['gestisci']
      },
      {
        route: 'events',
        acl: 'myextranet.eventi',
        permissions: ['visualizza']
      },
      {
        route: 'events/members',
        acl: 'myextranet.eventi',
        permissions: ['visualizza']
      },
      {
        route: 'events/members/new',
        acl: 'myextranet.eventi',
        permissions: ['gestisci']
      },
      {
        route: 'events/members/edit',
        acl: 'myextranet.eventi',
        permissions: ['gestisci']
      },
      {
        route: 'events/invite-list',
        acl: 'myextranet.eventi',
        permissions: ['gestisci']
      },
      {
        route: 'events/invite-list/new',
        acl: 'myextranet.eventi',
        permissions: ['gestisci']
      },
      {
        route: 'projects',
        acl: 'myextranet.progetti',
        permissions: ['visualizza']
      },
      {
        route: 'projects/collaborators-list',
        acl: 'myextranet.progetti',
        permissions: ['visualizza']
      },
      {
        route: 'projects/collaborators-list/edit',
        acl: 'myextranet.progetti',
        permissions: ['gestisci']
      },
      {
        route: 'projects/collaborators-requests-list',
        acl: 'myextranet.progetti',
        permissions: ['visualizza']
      },
      {
        route: 'projects/collaborators-requests-list/manage',
        acl: 'myextranet.progetti',
        permissions: ['gestisci']
      },
      {
        route: 'tenants',
        acl: 'myextranet.enti',
        permissions: ['visualizza']
      },
      {
        route: 'tenants/tenant-detail/:idEnte',
        acl: 'myextranet.enti',
        permissions: ['gestisci']
      },
      {
        route: 'tenants/tenant-detail',
        acl: 'myextranet.enti',
        permissions: ['gestisci']
      },
      {
        route: 'active-products',
        acl: 'myextranet.prodotti',
        permissions: ['visualizza']
      },
      {
        route: 'active-products/manage-users/:idProdotto',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci']
      },
      {
        route: 'active-products/manage-requests/:idAttivazione',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci']
      },
      {
        route: 'active-products/manage-users',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci']
      },
      {
        route: 'activable-products',
        acl: 'myextranet.prodotti',
        permissions: ['visualizza']
      },
      {
        route: 'activable-products/requests-list',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci']
      },
      {
        route: 'activable-products/requests-list/request-configuration',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci']
      },
      {
        route: 'activable-products/product-detail/:idProdotto',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci']
      },
      {
        route: 'activable-products/product-detail',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci']
      }, {
        route: 'RAP',
        acl: 'myextranet.prodotti',
        permissions: ['visualizza']
      }, {
        route: 'RAP/detail',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci']
      }, {
        route: 'RAP-requests',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci']
      }, {
        route: 'RAP-requests/management',
        acl: 'myextranet.prodotti',
        permissions: ['gestisci']
      }
    ];



  constructor(private userService: UserService, private alertService: AlertService) {

  }

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    return this.userService.getUserInfo(false).then(userInfo => {
      if (!userInfo.acls) {
        throw new Error(`Non hai i permessi necessari a completare quest'operazione`);
      }


      let routes = this.routeAclAndPemissionMap.map(r => {

        let routeSplit = r.route.split('/');
        routeSplit = routeSplit.map((split: string) => {
          let returnedSplit = split;
          if (split.startsWith(':')) {
            split = split.replace(':', '');
            if (!!route.params[split]) {
              returnedSplit = route.params[split];
            }
          }
          return returnedSplit;
        });
        return { ...r, route: routeSplit.join(',') }
      });

      const matchingRoute: Models.AclPermissionRouteMap = routes.find(r => r.route === route.url.toString());
      if (!matchingRoute) {
        return true;
      }
      const userAclIndex = userInfo.acls.map(a => a.acl).indexOf(matchingRoute.acl);
      if (userAclIndex !== -1) {
        for (let permission of matchingRoute.permissions) {
          if (!userInfo.acls[userAclIndex].permissions || userInfo.acls[userAclIndex].permissions.indexOf(permission) === -1) {
            throw new Error(`Non hai i permessi necessari a completare quest'operazione`);
            break;
          }
        }
        return true;
      } else {
        throw new Error(`Non hai i permessi necessari a completare quest'operazione`);
      }
    }).catch(err => {
      console.error(err);
      this.alertService.passDataToSubject({
        method: 'POST',
        url: 'acl-general-error',
        snackType: 'Error'
      });
      return false;
    });
  }

}
