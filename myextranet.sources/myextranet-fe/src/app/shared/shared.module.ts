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

import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatButtonModule } from '@angular/material/button';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatMenuModule } from '@angular/material/menu';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { GeneralListModalComponent } from './modals/general-list-modal/general-list-modal.component';
import { CommonModule } from '@angular/common';
import { MatDialogModule } from '@angular/material/dialog';
import { GeneralMailComponent } from './modals/general-mail/general-mail.component';
import { MatCardModule } from '@angular/material/card';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSortModule } from '@angular/material/sort';
import { GeneralDataTableComponent } from './components/data-table/general-data-table/general-data-table.component';
import { CardTableContainerComponent } from './components/data-table/card-table-container/card-table-container.component';
import { BreadcrumbsComponent } from './components/breadcrumbs/breadcrumbs.component';
import { GenericCardComponent } from './components/generic-card/generic-card.component';
import { GenericCardContainerComponent } from './components/generic-card-container/generic-card-container.component';
import { RouterModule } from '@angular/router';
import { ACLDirectiveDirective } from './directives/acl-directive.directive';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { LoadingDirective } from './directives/loading.directive';
import { LoaderComponent } from './directives/loader/loader.component';
import { MatRadioModule } from '@angular/material/radio';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { AssociationTableLabelComponent } from './components/data-table/general-data-table/association-table-label/association-table-label.component';
import { GenericUserFormComponent } from './components/generic-user-form/generic-user-form.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AlertInPageFoComponent } from './components/alert-in-page-fo/alert-in-page-fo.component';
import { GeneralConfirmComponent } from './modals/general-confirm/general-confirm.component';
import { FileUploadComponent } from './components/file-upload/file-upload.component';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { CardHorizontalComponent } from './components/card-horizontal/card-horizontal.component';
import { HorizontalCardContainerComponent } from './components/horizontal-card-container/horizontal-card-container.component';
import { DateSelectModalComponent } from './modals/date-select-modal/date-select-modal.component';
import { TableChipComponent } from './components/data-table/general-data-table/table-chip/table-chip.component';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { ProductRequestStepManagementComponent } from './components/product-request-step-management/product-request-step-management.component';
import { UserManageFormComponent } from './components/user-manage-form/user-manage-form.component';
import { UserManageTableComponent } from './components/user-manage-table/user-manage-table.component';
import { UserManagementContainerComponent } from './components/user-management-container/user-management-container.component';
import { UserManageTablesContainerComponent } from './components/user-manage-tables-container/user-manage-tables-container.component';
import { DocumentInsertFormComponent } from './components/document-insert-form/document-insert-form.component';
import { GeneralTableDeepPropComponent } from './components/data-table/general-data-table/general-table-deep-prop/general-table-deep-prop.component';
import { MatTabsModule } from '@angular/material/tabs';
import { ProductRequestDocumentsTableComponent } from './components/product-request-documents-table/product-request-documents-table.component';
import { RequestToolbarComponent } from './components/request-toolbar/request-toolbar.component';
import { NotesModalComponent } from './modals/notes-modal/notes-modal.component';
import { MultiMailModalComponent } from './modals/multi-mail-modal/multi-mail-modal.component';
import { MatStepperModule } from '@angular/material/stepper';
import { StepperComponent } from './components/stepper/stepper.component';
import { MatBadgeModule } from '@angular/material/badge';
import { ActionsFilterPipePipe } from './components/data-table/general-data-table/pipes/actions-filter-pipe.pipe';
import { MrfFormModule } from '@eng/morfeo';




@NgModule({
  declarations: [
    GeneralListModalComponent,
    GeneralMailComponent,
    GeneralDataTableComponent,
    CardTableContainerComponent,
    BreadcrumbsComponent,
    GenericCardComponent,
    GenericCardContainerComponent,
    ACLDirectiveDirective,
    LoadingDirective,
    LoaderComponent,
    AssociationTableLabelComponent,
    GenericUserFormComponent,
    AlertInPageFoComponent,
    GeneralConfirmComponent,
    FileUploadComponent,
    CardHorizontalComponent,
    HorizontalCardContainerComponent,
    DateSelectModalComponent,
    TableChipComponent,
    ProductRequestStepManagementComponent,
    UserManageFormComponent,
    UserManageTableComponent,
    UserManagementContainerComponent,
    UserManageTablesContainerComponent,
    DocumentInsertFormComponent,
    GeneralTableDeepPropComponent,
    ProductRequestDocumentsTableComponent,
    RequestToolbarComponent,
    NotesModalComponent,
    MultiMailModalComponent,
    StepperComponent,
    ActionsFilterPipePipe
  ],
  imports: [
    CommonModule,
    MrfFormModule,
    NgbModule,
    MatToolbarModule,
    MatSidenavModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatListModule,
    MatIconModule,
    MatMenuModule,
    MatExpansionModule,
    MatSelectModule,
    ReactiveFormsModule,
    FormsModule,
    MatCardModule,
    MatProgressBarModule,
    MatGridListModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSortModule,
    RouterModule,
    MatAutocompleteModule,
    MatRadioModule,
    MatInputModule,
    MatCheckboxModule,
    BrowserAnimationsModule,
    MatDatepickerModule,
    MatChipsModule,
    MatSortModule,
    MatTabsModule,
    MatTooltipModule,
    MatStepperModule,
    MatBadgeModule
  ],
  exports: [
    GeneralListModalComponent,
    // MrfFormModule,
    NgbModule,
    MatGridListModule,
    MatToolbarModule,
    MatSidenavModule,
    MatButtonModule,
    MatFormFieldModule,
    MatListModule,
    MatIconModule,
    MatMenuModule,
    MatExpansionModule,
    MatSelectModule,
    ReactiveFormsModule,
    FormsModule,
    MatCardModule,
    MatProgressBarModule,
    MatTableModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatSortModule,
    GeneralDataTableComponent,
    CardTableContainerComponent,
    BreadcrumbsComponent,
    GenericCardComponent,
    GenericCardContainerComponent,
    ACLDirectiveDirective,
    MatAutocompleteModule,
    LoadingDirective,
    LoaderComponent,
    MatRadioModule,
    MatInputModule,
    MatCheckboxModule,
    GenericUserFormComponent,
    BrowserAnimationsModule,
    AlertInPageFoComponent,
    GeneralConfirmComponent,
    FileUploadComponent,
    MatDatepickerModule,
    CardHorizontalComponent,
    HorizontalCardContainerComponent,
    DateSelectModalComponent,
    TableChipComponent,
    MatSortModule,
    MatTooltipModule,
    UserManageFormComponent,
    UserManageTableComponent,
    UserManagementContainerComponent,
    UserManageTablesContainerComponent,
    MatTabsModule,
    ProductRequestStepManagementComponent,
    ProductRequestDocumentsTableComponent,
    RequestToolbarComponent,
    NotesModalComponent,
    MatStepperModule,
    StepperComponent,
    MatBadgeModule
  ], entryComponents: [
    GeneralListModalComponent,
    GeneralConfirmComponent,
    DateSelectModalComponent,
    NotesModalComponent
  ]
})
export class SharedModule { }
