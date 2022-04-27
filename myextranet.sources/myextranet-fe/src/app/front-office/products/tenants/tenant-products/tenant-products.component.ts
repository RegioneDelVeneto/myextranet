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
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { Models } from 'src/app/models/model';
import { EnteService } from 'src/app/services/ente.service';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { ProductsService } from 'src/app/services/products.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-tenant-products',
  templateUrl: './tenant-products.component.html',
  styleUrls: ['./tenant-products.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class TenantProductsComponent implements OnInit {


  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca', url: '/utente/bacheca' },
      { label: 'I miei Prodotti', url: '/utente/prodotti' },
      { label: 'Prodotti attivi' }
    )
  };

  public isLoading = false;


  public tableHeader: Models.TableHeader[] = [

    {
      definition: 'nomeProdottoAttiv',
      header: 'Nome del prodotto',
      span: 4
    },
    {
      definition: 'statoArray',
      header: 'Stato',
      type: 'chip',
      chipsArray: [
        {
          label: 'Richiesta in corso',
          value: 'IN-CORSO',
          color: 'orange'
        },
        {
          label: 'Annullato',
          value: 'ANN',
          color: 'orange'
        },
        {
          label: 'Attivo',
          value: '2',
          color: 'green'
        }, {
          label: 'Da attivare',
          value: '0',
          color: 'grey'
        },
        {
          label: 'Da attivare',
          value: '1',
          color: 'grey'
        },
        {
          label: 'Disattivato',
          value: 'DIS',
          color: 'grey'
        }
      ],
      span: 4
    },
    {
      definition: 'dtInizioVal',
      header: 'Attivazione',
      type: 'date',
      dateFormat: 'dd/MM/y',
      span: 2
    },
    {
      definition: 'dtFineVal',
      header: 'Disattivazione',
      type: 'date',
      dateFormat: 'dd/MM/y',
      span: 2
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'buttons',
      actions: [{
        icon: '',
        event: 'manage',
        label: 'VISUALIZZA',
        showOnlyIf: 'showEdit',
        color: 'accent'
      },
      {
        icon: '',
        event: 'active',
        label: 'ATTIVA',
        showOnlyIf: 'showActivate',
        color: 'accent'
      },
      {
        icon: '',
        event: 'view',
        label: 'VISUALIZZA',
        showOnlyIf: 'showView',
        color: 'accent'
      }
      ],
      span: 2
    }
  ];

  public footer: {
    active: boolean,
    buttons: {
      label: string,
      event: string,
      icon: string,
      color?: string
    }[]
  } = {
      active: true,
      buttons: []
    };

  public productsDataSource: { [key: string]: string | Date }[] = [];
  public denominazioneInput = '';
  public idEnte = null;
  public tenant: Models.enteDTO;

  private idRappr: number = null;

  constructor(
    private pService: ProductsService,

    private enteService: EnteService,
    private route: ActivatedRoute,
    private router: Router,
    public dialog: MatDialog,
    private myextranetTableSrv: MyextTableService
  ) { }

  ngOnInit(): void {
    this.isLoading = true;
    this.route.queryParams.subscribe(params => {
      if (params.idRappr !== null && params.idRappr !== undefined) {
        this.idRappr = params.idRappr;
      }
      if (params.idEnte !== null && params.idEnte !== undefined) {
        this.idEnte = params.idEnte;

        this.enteService.getEnteById(this.idEnte, true).toPromise().then((ente: Models.enteDTO) => {
          this.tenant = ente;

          this.denominazioneInput = ente?.denominazione ?? null;
          this.getDataFromService();
        }).catch(err => {
          this.isLoading = false;
        });
      }
    });

  }


  getDataFromService(pageindex: number = -1, pagesize: number = 10, sortBy?: string, filter?: any): void {
    this.isLoading = true;
    let url = `/frontoffice/prodotto-attivato/prodotti-ente?idEnte=${this.idEnte}&_page=-1&_pageSize=${pagesize}`;
    if (!!filter) {
      Object.keys(filter).forEach(key => {
        if (filter[key] !== null && filter[key] !== undefined) {
          if (filter[key].value !== null && filter[key].value !== undefined) {
            url = `${url}&${key}=${filter[key].value}`;
          } else {
            url = `${url}&${key}=${filter[key]}`;
          }

        }
      });
    }
    this.myextranetTableSrv.getData(url).then(response => {



      response.records = response.records.map((item: { stato: number, statoArray: string[], numRich: 1 | 0, showEdit: boolean, showView: boolean, showActivate: boolean, idAttivazione: number }) => ({
        ...item,
        stato: item.stato !== null && item.stato !== undefined ? item.stato.toString() : '0',
        showView: item.stato === 2,
        showActivate: item.stato === 0 || !item.stato,
        showEdit: item.stato === 1,

        statoArray: this.getStatoArray(item.stato ?? 0, item.numRich)

      }));

      this.productsDataSource = Array.from(response.records);
      this.isLoading = false;

    }).catch(err => {
      this.isLoading = false;
    });
  }

  public getStatoArray(stato: number, nRich: 1 | 0): string[] {
    let returnArray: string[] = [stato.toString()];
    if (nRich > 0) {
      returnArray.push('IN-CORSO')
    }

    return returnArray;
  }

  public manageEvent(event: { element: any, eventType: string }) {
    if (!event) return null;
    switch (event.eventType) {
      case 'active': {
        this.router.navigate(['/utente/prodotti/gestione/nuova-richiesta-attivazione'], { queryParams: { idProdotto: event.element.idProdottoAtt, idEnte: this.idEnte, idRappr: this.idRappr } });
        break;
      }
      case 'manage': {
        this.router.navigate(['/utente/prodotti/gestione/richiesta'], { queryParams: { idProdotto: event.element.idProdottoAtt, idEnte: this.idEnte, idRappr: this.idRappr } });
        break;
      }

      case 'view': {
        this.router.navigate(['/utente/prodotti/gestione/dettaglio-prodotto-attivo'], { queryParams: { idProdotto: event.element.idProdottoAtt, idEnte: this.idEnte, idRappr: this.idRappr } });
        break;
      }
    }
  }

  public navigateToManage(row?: { idAttivazione: number }): void {

    const path = [''];
    if (row) {
      path.push(`${row.idAttivazione}`);
    }
    this.router.navigate(path);
  }

}
