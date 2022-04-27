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
import { CommonModule } from '@angular/common';
import { FooterPublicComponent } from './public-layout/footer-public/footer-public.component';
import { HeaderPublicComponent } from './public-layout/header-public/header-public.component';
import { PublicLayoutComponent } from './public-layout/public-layout.component';
import { FrontOfficeRoutingModule } from './front-office-routing.module';
import { IconCardComponent } from './home-page/icon-card/icon-card.component';
import { SharedModule } from '../shared/shared.module';
import { HomePageComponent } from './home-page/home-page/home-page.component';
import { EventSignupComponent } from './events/event-signup/event-signup.component';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ReactiveFormsModule } from '@angular/forms';
import { MatSelectModule } from '@angular/material/select';
import { EventsComponent } from './events/events.component';
import { UserDataComponent } from './user/user-data/user-data.component';
import { EventStramingDetailComponent } from './events/event-straming-detail/event-straming-detail.component';
import { PaginaCortesiaComponent } from './extra/pagina-cortesia/pagina-cortesia.component';
import { ProjectsComponent } from './projects/projects.component';
import { ProductsComponent } from './products/products.component';
import { TenantDetailFoComponent } from './products/tenants/tenant-detail-fo/tenant-detail-fo.component';
import { TenantProductsComponent } from './products/tenants/tenant-products/tenant-products.component';
import { ProjectCollaborationsComponent } from './projects/project-collaborations/project-collaborations.component';
import { RAPDetailComponent } from './products/tenants/rap-detail/rap-detail.component';
import { BachecaComponent } from './home-page/bacheca/bacheca.component';
import { ProductRequestProcedureFoComponent } from './products/tenants/tenant-products/product-request-procedure-fo/product-request-procedure-fo.component';



@NgModule({
  declarations: [
    FooterPublicComponent,
    HeaderPublicComponent,
    PublicLayoutComponent,
    IconCardComponent,
    HomePageComponent,
    EventSignupComponent,
    EventsComponent,
    UserDataComponent,
    EventStramingDetailComponent,
    PaginaCortesiaComponent,
    ProjectsComponent,
    ProductsComponent,
    TenantDetailFoComponent,
    TenantProductsComponent,
    ProjectCollaborationsComponent,
    RAPDetailComponent,
    BachecaComponent,
    ProductRequestProcedureFoComponent,
  ],
  imports: [
    CommonModule,
    FrontOfficeRoutingModule,
    SharedModule,
    MatFormFieldModule,
    MatButtonModule,
    ReactiveFormsModule,
    MatSelectModule
  ]
})
export class FrontOfficeModule { }
