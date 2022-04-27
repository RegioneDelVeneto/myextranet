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
import { CommonModule, DatePipe } from '@angular/common';
import { BackofficeLayoutComponent } from './backoffice-layout/backoffice-layout.component';
import { BackOfficeRoutingModule } from './backoffice-routing.module';
import { SharedModule } from '../shared/shared.module';
import { MrfFormModule } from '@eng/morfeo';
import { UsersListComponent } from './users/users-list/users-list.component';
import { EventsListComponent } from './events/events-list/events-list.component';
import { EventsInviteListComponent } from './events/events-invite-list/events-invite-list.component';
import { EventsInviteNewComponent } from './events/events-invite-list/events-invite-new/events-invite-new.component';

import { EventMembersComponent } from './events/event-members/event-members.component';
import { EventNewMemberComponent } from './events/event-members/event-new-member/event-new-member.component';
import { ProjectsListComponent } from './projects/projects-list/projects-list.component';
import { ProjectCollaboratorsComponent } from './projects/project-collaborators/project-collaborators.component';
import { CollaboratorFormComponent } from './projects/project-collaborators/collaborator-form/collaborator-form.component';
import { TenantsListComponent } from './tenants/tenants-list/tenants-list.component';
import { TenantDetailComponent } from './tenants/tenant-detail/tenant-detail.component';
import { ProductsListComponent } from './products/products-list/products-list.component';
import { ProductDetailComponent } from './products/product-detail/product-detail.component';
import { ActiveProductsComponent } from './products/active-products/active-products.component';

import { ActiveProductUserManagementComponent } from './products/active-product-user-management/active-product-user-management.component';
import { RAPListComponent } from './products/rap-list/rap-list.component';
import { RAPManagementComponent } from './products/rap-list/rap-management/rap-management.component';
import { ProjectCollaboratorsRequestsComponent } from './projects/project-collaborators-requests/project-collaborators-requests.component';
import { ManageCollaboratorRequestFormComponent } from './projects/project-collaborators-requests/manage-collaborator-request-form/manage-collaborator-request-form.component';
import { ProductRequestsComponent } from './products/products-list/product-requests/product-requests.component';
import { ProductRequestConfigurationComponent } from './products/products-list/product-requests/product-request-configuration/product-request-configuration.component';
import { ActiveProductRequestsManagementComponent } from './products/active-products/active-product-requests-management/active-product-requests-management.component';
import { ActiveProductRequestDetailComponent } from './products/active-products/active-product-requests-management/active-product-request-detail/active-product-request-detail.component';
import { NotificationCenterComponent } from './notification-center/notification-center.component';
import { GenericNotificationTableContainerComponent } from './notification-center/generic-notification-table-container/generic-notification-table-container.component';
import { RapRequestsComponent } from './products/rap-requests/rap-requests.component';



@NgModule({
  declarations: [
    BackofficeLayoutComponent,
    UsersListComponent,
    EventsListComponent,
    EventsInviteListComponent,
    EventsInviteNewComponent,
    EventMembersComponent,
    EventNewMemberComponent,
    ProjectsListComponent,
    ProjectCollaboratorsComponent,
    CollaboratorFormComponent,
    TenantsListComponent,
    TenantDetailComponent,
    ProductDetailComponent,
    ProductsListComponent,
    ActiveProductsComponent,
    ActiveProductUserManagementComponent,
    RAPListComponent,
    RAPManagementComponent,
    ProjectCollaboratorsRequestsComponent,
    ManageCollaboratorRequestFormComponent,
    ProductRequestsComponent,
    ProductRequestConfigurationComponent,
    ActiveProductRequestsManagementComponent,
    ActiveProductRequestDetailComponent,
    NotificationCenterComponent,
    GenericNotificationTableContainerComponent,
    RapRequestsComponent
  ],
  imports: [
    MrfFormModule,
    CommonModule,
    SharedModule,
    BackOfficeRoutingModule
  ],
  providers: [DatePipe]
})
export class BackofficeModule { }
