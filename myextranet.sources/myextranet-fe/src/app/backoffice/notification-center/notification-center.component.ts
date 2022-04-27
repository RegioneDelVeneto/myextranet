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
import { SortDirection } from '@angular/material/sort';
import { Router } from '@angular/router';
import { Models } from 'src/app/models/model';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-notification-center',
  templateUrl: './notification-center.component.html',
  styleUrls: ['./notification-center.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class NotificationCenterComponent implements OnInit {

  public isLoading: boolean = false;

  public sortFieldProj: { active: string, direction: SortDirection } = { active: 'titolo', direction: 'asc' };
  public sortFieldRAP: { active: string, direction: SortDirection } = { active: 'utente.cognome', direction: 'asc' };
  public sortFieldProd: { active: string, direction: SortDirection } = { active: 'prodottoAttivabile.nomeProdottoAttiv', direction: 'asc' };

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Bacheca' }
    )
  };


  public tableHeaderProjects: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'titolo',
      header: 'Titolo',
      span: 4
    },
    {
      sortActive: true,
      definition: 'descrizione',
      header: 'Descrizone',
      type: 'html',
      span: 4
    },
    {
      definition: 'prodotto',
      header: 'Prodotto',
      type: 'association',
      associationLabel: 'titolo',
      span: 3
    },
    {
      sortActive: true,
      definition: 'pendingCounter',
      header: 'Richieste',
      span: 3
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [
        {
          icon: 'info',
          event: 'requests',
          label: 'Richieste',         
          acl: 'myextranet.progetti', 
          permissions: ['visualizza']
        }
      ],
      span: 1
    }
  ];

  public tableHeaderProducts: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'prodottoNome',
      header: 'Prodotto Attivabile',
      span: 4
    },
    {
      sortActive: true,
      definition: 'enteDef',
      header: 'Ente',
      span: 4
    },
    {

      definition: 'stato',
      header: 'Stato',
      span: 4,
      type: 'objectMapper',
      objectMapped: {
        0: 'Da attivare',
        1: 'In fase di attivazione',
        2: 'Attivato',
      }
    },
    {

      definition: 'numRich',
      header: 'Numero richieste attive',
      span: 4
    },
    {
      sortActive: true,
      definition: 'dtInizioValLD',
      header: 'Data di inizio validità',
      type: 'date',
      dateFormat: 'dd/MM/y',
      span: 2
    },
    {
      sortActive: true,
      definition: 'dtFineValLD',
      header: 'Data di fine validità',
      type: 'date',
      dateFormat: 'dd/MM/y',
      span: 2
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [
        {
          icon: 'report',
          event: 'openRequests',
          label: 'Richieste',
          acl: 'myextranet.prodotti',
          permissions: ['gestisci'],
        }
      ],
      span: 1
    }
  ];


  public tableHeaderRAP: Models.TableHeader[] = [
    {
      sortActive: true,
      definition: 'utenteCognome',
      header: 'Cognome',
      span: 4
    },
    {
      sortActive: true,
      definition: 'utenteNome',
      header: 'Nome',
      span: 4
    },

    {
      sortActive: true,
      definition: 'enteDesc',
      header: 'Ente',
      span: 4
    },
    {
      sortActive: true,
      definition: 'flgConferma',
      header: 'Stato',
      span: 4,
      type: 'objectMapper',
      objectMapped: {
        '-1': 'Da risottomettere',
        0: 'In attesa di conferma',
        1: 'Confermata',
        2: 'Non confermata',
      }
    },
    {
      sortActive: true,
      definition: 'dtConferma',
      header: 'Data conferma',
      span: 4,
      type: 'date'
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [
        {
          icon: 'report',
          event: 'openRequests',
          label: 'Richieste',
          acl: 'myextranet.prodotti',
          permissions: ['gestisci'],
        }
      ],
      span: 1
    }
  ];

  public tableHeaderStats: Models.TableHeader[] = [
    {

      definition: 'nome',
      header: 'Statistica',
      span: 4
    },
    {

      definition: 'valore',
      header: 'Numero',
      span: 4
    },
  ];

  private loadingStatusObject: Record<string, { isLoading: boolean, count: number }> = {};

  public isPActiveVisible: boolean = false;
  public pActiveNotifications: number = 0;

  public isProjectsVisibile: boolean = false;
  public projectsNotifications: number = 0;

  public isRapVisible: boolean = false;
  public RAPNotifications: number = 0;

  public statGenerali: { nome: string, valore: number }[] = [];

  constructor(
    private myextranetTableSrv: MyextTableService,
    private router: Router) { }

  ngOnInit(): void {
    this.manageLoadingStatus({ isLoading: true, count: 0, type: 'Stat' });
    let urlStatGenerali = `/statistiche/stat-generali?outputType=json`;
    this.myextranetTableSrv.getData(urlStatGenerali).then(response => {
      this.statGenerali = response.map(a => this.statGeneraliMapper(a));
      this.manageLoadingStatus({ isLoading: false, count: 0, type: 'Stat' });

    });

  }

  public manageEventProjects(event: { element: any, eventType: string }): void {
    if (!event) {
      return null;
    }
    switch (event.eventType) {
      case 'requests': {
        if (event.element && event.element.id) {
          this.router.navigate(['backoffice/projects/collaborators-requests-list'], { queryParams: { projectId: event.element.id } });
        }
        break;
      }
    }
  }

  public manageEventProducts(event: { element: any, eventType: string }): void {
    if (!event) {
      return null;
    }
    switch (event.eventType) {

      case 'openRequests': {
        if (event.element) {
          this.router.navigate([`/backoffice/active-products/manage-requests/${event.element.idAttivazione}`]);
        }
        break;
      }

    }
  }

  public manageEventRAP(event: { element: any, eventType: string }): void {
    if (!event) {
      return null;
    }
    switch (event.eventType) {

      case 'openRequests': {
        if (event.element && !!event.element.idRappr) {
          this.router.navigate(['/backoffice/RAP-requests/management'], { queryParams: { idRappr: event.element.idRappr } });
        }
        break;
      }

    }
  }

  public manageLoadingStatus(event: { isLoading: boolean, count: number, type: string }): void {
    this.loadingStatusObject[event.type] = { count: event.count, isLoading: event.isLoading };
    switch (event.type) {
      case 'Projects': {
        this.isProjectsVisibile = true;
        this.projectsNotifications = event.count;
        break;
      }
      case 'RAP': {
        this.isRapVisible = true;
        this.RAPNotifications = event.count;
        break;
      }
      case 'Products': {
        this.isPActiveVisible = true;
        this.pActiveNotifications = event.count;
        break;
      }
    }
    this.isLoading = Object.values(this.loadingStatusObject).every((a => a.isLoading));

  }

  private statGeneraliMapper(value: { nome: string, valure: number }): { nome: string, valure: number } {
    if (value.nome === 'EntiServiti') {
      value.nome = 'Enti serviti';
    } else if (value.nome === 'ProdottiErogati') {
      value.nome = 'Prodotti erogati';
    } else if (value.nome === 'OperatoriProdotti') {
      value.nome = 'Operatori della PA che usano i nostri prodotti';
    }




    return value;
  }

}
