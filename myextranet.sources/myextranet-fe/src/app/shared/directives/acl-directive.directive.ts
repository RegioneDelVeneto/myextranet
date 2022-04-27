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
import { Directive, Input, OnInit, TemplateRef, ViewContainerRef } from '@angular/core';
import { Models } from 'src/app/models/model';
import { UserService } from 'src/app/services/user.service';

@Directive({
  selector: '[appACLDirective]'
})
export class ACLDirectiveDirective implements OnInit {
  
  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private userSrv: UserService
  ) { }

  userInfo: Models.UserInfo;

  @Input()
  public set appACLDirective(obj: { acl: string, permissions: string[] }) {
    if (!obj.acl || !obj.permissions) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
    else {
      this.userSrv.getUserInfo(false).then(info => {
        this.userInfo = info;
        if (this.userInfo) {
          const userAclIndex = this.userInfo.acls.map(a => a.acl).indexOf(obj.acl);
          let userCanSee: boolean = true;
          this.viewContainer.clear();
          if (userAclIndex !== -1) {
            for (let permission of obj.permissions) {
              if (!this.userInfo.acls[userAclIndex].permissions || this.userInfo.acls[userAclIndex].permissions.indexOf(permission) === -1) {
                userCanSee = false;
                break;
              }
            }

            if (userCanSee) {
              this.viewContainer.createEmbeddedView(this.templateRef);
            }
          }
        }
      });
    }
  }

  ngOnInit(): void {

  }


}
