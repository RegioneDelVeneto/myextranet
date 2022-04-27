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
import { BrowserModule } from '@angular/platform-browser';
import { LOCALE_ID, NgModule } from '@angular/core';
import { HttpClient, HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';

//componenti
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MrfFormModule } from '@eng/morfeo';
import { SharedModule } from './shared/shared.module';
import { BackofficeModule } from './backoffice/backoffice.module';
import { registerLocaleData } from '@angular/common';
import localeIt from '@angular/common/locales/it';
import { HttpInterceptorService } from './services/http-interceptor.service';
import { SnackbarComponent } from './services/snackbar/snackbar.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { FrontOfficeModule } from './front-office/front-office.module';

registerLocaleData(localeIt, 'it-IT');

// AoT requires an exported function for factories
export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
  return new TranslateHttpLoader(http, './assets/i18n/', '.json');
}

@NgModule({
  declarations: [
    AppComponent,
    SnackbarComponent,
  ],
  imports: [
    MrfFormModule,
    BrowserModule,
    SharedModule,
    AppRoutingModule,
    HttpClientModule,
    BackofficeModule,
    FrontOfficeModule,
    TranslateModule.forRoot({ defaultLanguage: 'it', loader: { provide: TranslateLoader, useFactory: HttpLoaderFactory, deps: [HttpClient] } }),

  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'it-IT' },
    {
      provide: HTTP_INTERCEPTORS, useClass: HttpInterceptorService, multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
