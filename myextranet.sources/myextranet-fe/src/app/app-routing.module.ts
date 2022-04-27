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
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { AuthAclGuard } from './auth/auth-acl.guard';
import { UserInfoGuard } from './auth/user/user-info.guard';
import { BackofficeLayoutComponent } from './backoffice/backoffice-layout/backoffice-layout.component';
import { EventMembersComponent } from './backoffice/events/event-members/event-members.component';
import { EventNewMemberComponent } from './backoffice/events/event-members/event-new-member/event-new-member.component';
import { EventsInviteListComponent } from './backoffice/events/events-invite-list/events-invite-list.component';
import { EventsInviteNewComponent } from './backoffice/events/events-invite-list/events-invite-new/events-invite-new.component';
import { EventsListComponent } from './backoffice/events/events-list/events-list.component';
import { NotificationCenterComponent } from './backoffice/notification-center/notification-center.component';
import { ActiveProductUserManagementComponent } from './backoffice/products/active-product-user-management/active-product-user-management.component';
import { ActiveProductRequestDetailComponent } from './backoffice/products/active-products/active-product-requests-management/active-product-request-detail/active-product-request-detail.component';
import { ActiveProductRequestsManagementComponent } from './backoffice/products/active-products/active-product-requests-management/active-product-requests-management.component';
import { ActiveProductsComponent } from './backoffice/products/active-products/active-products.component';
import { ProductDetailComponent } from './backoffice/products/product-detail/product-detail.component';
import { ProductRequestConfigurationComponent } from './backoffice/products/products-list/product-requests/product-request-configuration/product-request-configuration.component';
import { ProductRequestsComponent } from './backoffice/products/products-list/product-requests/product-requests.component';
import { ProductsListComponent } from './backoffice/products/products-list/products-list.component';
import { RAPListComponent } from './backoffice/products/rap-list/rap-list.component';
import { RAPManagementComponent } from './backoffice/products/rap-list/rap-management/rap-management.component';
import { RapRequestsComponent } from './backoffice/products/rap-requests/rap-requests.component';
import { ManageCollaboratorRequestFormComponent } from './backoffice/projects/project-collaborators-requests/manage-collaborator-request-form/manage-collaborator-request-form.component';
import { ProjectCollaboratorsRequestsComponent } from './backoffice/projects/project-collaborators-requests/project-collaborators-requests.component';
import { CollaboratorFormComponent } from './backoffice/projects/project-collaborators/collaborator-form/collaborator-form.component';
import { ProjectCollaboratorsComponent } from './backoffice/projects/project-collaborators/project-collaborators.component';
import { ProjectsListComponent } from './backoffice/projects/projects-list/projects-list.component';
import { TenantDetailComponent } from './backoffice/tenants/tenant-detail/tenant-detail.component';
import { TenantsListComponent } from './backoffice/tenants/tenants-list/tenants-list.component';

import { UsersListComponent } from './backoffice/users/users-list/users-list.component';
import { EventSignupComponent } from './front-office/events/event-signup/event-signup.component';
import { EventsComponent } from './front-office/events/events.component';
import { PaginaCortesiaComponent } from './front-office/extra/pagina-cortesia/pagina-cortesia.component';
import { BachecaComponent } from './front-office/home-page/bacheca/bacheca.component';
import { ProductsComponent } from './front-office/products/products.component';
import { RAPDetailComponent } from './front-office/products/tenants/rap-detail/rap-detail.component';
import { TenantDetailFoComponent } from './front-office/products/tenants/tenant-detail-fo/tenant-detail-fo.component';
import { ProductRequestProcedureFoComponent } from './front-office/products/tenants/tenant-products/product-request-procedure-fo/product-request-procedure-fo.component';
import { TenantProductsComponent } from './front-office/products/tenants/tenant-products/tenant-products.component';
import { ProjectCollaborationsComponent } from './front-office/projects/project-collaborations/project-collaborations.component';
import { ProjectsComponent } from './front-office/projects/projects.component';
import { PublicLayoutComponent } from './front-office/public-layout/public-layout.component';
import { UserDataComponent } from './front-office/user/user-data/user-data.component';


const routes: Routes = [
  {
    path: 'utente',
    component: PublicLayoutComponent,
    children: [
      {
        path: 'bacheca',
        component: BachecaComponent,
        canActivate: [UserInfoGuard]
      },
      {
        path: 'eventi/iscrizione-evento',
        component: EventSignupComponent,
        canActivate: [UserInfoGuard]
      },
      {
        path: 'eventi',
        component: EventsComponent,
        canActivate: [UserInfoGuard]
      }, {
        path: 'user/edit',
        component: UserDataComponent,
        canActivate: [UserInfoGuard]
      }, {
        path: 'user/new',
        component: UserDataComponent,
        canActivate: [UserInfoGuard]
      }, {
        path: 'cortesia',
        component: PaginaCortesiaComponent,
      },
      {
        path: 'progetti',
        component: ProjectsComponent,
        canActivate: [UserInfoGuard]
      },
      {
        path: 'progetti/collabora',
        component: ProjectCollaborationsComponent,
        canActivate: [UserInfoGuard]
      },
      {
        path: 'prodotti',
        component: ProductsComponent,
        canActivate: [UserInfoGuard]
      },
      {
        path: 'prodotti/edit',
        component: TenantDetailFoComponent,
        canActivate: [UserInfoGuard]
      },
      {
        path: 'prodotti/RAP',
        component: RAPDetailComponent,
        canActivate: [UserInfoGuard]
      },
      {
        path: 'prodotti/gestione',
        component: TenantProductsComponent,
        canActivate: [UserInfoGuard]
      },
      {
        path: 'prodotti/gestione/richiesta',
        component: ProductRequestProcedureFoComponent,
        canActivate: [UserInfoGuard]
      },
      {
        path: 'prodotti/gestione/nuova-richiesta-attivazione',
        component: ProductRequestProcedureFoComponent,
        data: {
          opRich: 'ATT-PRD'
        },
        canActivate: [UserInfoGuard]
      },
      {
        path: 'prodotti/gestione/nuova-richiesta-aggiorna-utenti',
        component: ProductRequestProcedureFoComponent,
        data: {
          opRich: 'UPD-USR'
        },
        canActivate: [UserInfoGuard]
      },
      {
        path: 'prodotti/gestione/dettaglio-prodotto-attivo',
        component: ProductRequestProcedureFoComponent,
        canActivate: [UserInfoGuard]
      },
      {

        path: '**',
        redirectTo: '/utente/bacheca',
      }
    ]

  },
  {
    path: 'backoffice',
    component: BackofficeLayoutComponent,
    children: [
      {

        path: 'users',
        component: UsersListComponent,
        canActivate: [AuthAclGuard]
      },  {
        path: 'events',
        component: EventsListComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'events/members',
        component: EventMembersComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'events/members/new',
        component: EventNewMemberComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'events/members/edit',
        component: EventNewMemberComponent,
        canActivate: [AuthAclGuard]
      },
      {
        path: 'events/invite-list',
        component: EventsInviteListComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'events/invite-list/new',
        component: EventsInviteNewComponent,
        canActivate: [AuthAclGuard]
      },
      {
        path: 'projects',
        component: ProjectsListComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'projects/collaborators-list',
        component: ProjectCollaboratorsComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'projects/collaborators-list/edit',
        component: CollaboratorFormComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'projects/collaborators-requests-list',
        component: ProjectCollaboratorsRequestsComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'projects/collaborators-requests-list/manage',
        component: ManageCollaboratorRequestFormComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'tenants',
        component: TenantsListComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'tenants/tenant-detail/:idEnte',
        component: TenantDetailComponent,
        canActivate: [AuthAclGuard]
      },
      {
        path: 'tenants/tenant-detail',
        component: TenantDetailComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'active-products',
        component: ActiveProductsComponent,
        canActivate: [AuthAclGuard]
      },
      {
        path: 'active-products/manage-users/:idProdotto',
        component: ActiveProductUserManagementComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'active-products/manage-requests/:idAttivazione',
        component: ActiveProductRequestsManagementComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'active-products/manage-requests/detail/:idProdAttivRich',
        component: ActiveProductRequestDetailComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'activable-products',
        component: ProductsListComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'activable-products/requests-list',
        component: ProductRequestsComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'activable-products/requests-list/request-configuration',
        component: ProductRequestConfigurationComponent,
        canActivate: [AuthAclGuard]
      }, {
        path: 'activable-products/product-detail/:idProdotto',
        component: ProductDetailComponent,
        canActivate: [AuthAclGuard]
      },
      {
        path: 'activable-products/product-detail',
        component: ProductDetailComponent,
        canActivate: [AuthAclGuard]
      },
      {
        path: 'RAP',
        component: RAPListComponent,
        canActivate: [AuthAclGuard]
      },
      {
        path: 'RAP/detail',
        component: RAPManagementComponent,
        canActivate: [AuthAclGuard]
      },
      {
        path: 'RAP-requests',
        component: RapRequestsComponent,
        canActivate: [AuthAclGuard]
      },
      {
        path: 'RAP-requests/management',
        component: RAPManagementComponent,
        canActivate: [AuthAclGuard],
        data: {
          isRequest: true
        },
      },

      {
        path: '',
        component: NotificationCenterComponent
      },
      {
        path: '**',
        redirectTo: '/backoffice'
      }]
  },
  {

    path: '**',
    redirectTo: 'utente/bacheca',
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'top' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
