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
import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { Router } from '@angular/router';
import { Models } from 'src/app/models/model';
import { EnteService } from 'src/app/services/ente.service';

@Component({
  selector: 'app-horizontal-card-container',
  templateUrl: './horizontal-card-container.component.html',
  styleUrls: ['./horizontal-card-container.component.scss'],
  encapsulation: ViewEncapsulation.None

})
export class HorizontalCardContainerComponent implements OnInit {


  cardsRows = [];
  
  private _configuration
  @Input() set configuration(value: { type: string, inputCards: any[] }) {
    this.cardsRows = [];
    switch (value.type) {
      case 'tenant-rap': {
        value.inputCards = value.inputCards.map((value: Models.RappEnteDTO) => {
          let status: number;

          if (value.flgConferma === 1 && value.flgAttivo === false) {
            status = 9;
          } else {
            status = value.flgConferma;
          }

          let cardOutput: Models.HorizontalCardModel = {
            content: value,
            id: value.idRappr,
            logo: this.enteService.downloadLogo(value.ente.logo),
            status: status,
            routerLink: null,
            routerLinkValid: null,
            links: [],
            statusLabel: null,
            titolo: value.ente.denominazione.toUpperCase(),
          };

          cardOutput.routerLink = {
            link: ['/utente/prodotti/RAP'],
            params: {
              idRappr: value.idRappr.toString()
            }
          };
          if (cardOutput.status === 1 && value.flgAttivo === true) {
            cardOutput.routerLink = null;
            cardOutput.statusLabel = 'RAP abilitato';
            cardOutput.routerLinkValid = {
              link: ['/utente/prodotti/RAP'],
              params: {
                idRappr: value.idRappr.toString()
              }
            };
            cardOutput.links = [{
              icon: 'launch',
              label: 'Modifica dati',
              link: ['/utente/prodotti/edit'],
              params: { idEnte: value.ente.idEnte.toString(), idRappr: value.idRappr.toString() }
            }, {
              icon: 'settings',
              label: 'Gestisci prodotti',
              link: ['/utente/prodotti/gestione'],
              params: { idEnte: value.ente.idEnte.toString(), idRappr: value.idRappr.toString() }
            }]
          }
          else if (status === 0) {
            cardOutput.statusLabel = 'In attesa di approvazione';

          }
          else if (status === -1) {
            cardOutput.statusLabel = 'Da risottomettere';

          }
          else if (status === 2) {
            cardOutput.statusLabel = 'Non confermato';

          }
          else if (status === 9) {
            cardOutput.statusLabel = 'RAP disabilitato';

          } else {
            cardOutput.routerLink = null;
          }

          return cardOutput;

        })
        break;
      }

    }

    var i, j, temparray, chunk = 3;
    for (i = 0, j = value.inputCards.length; i < j; i += chunk) {
      temparray = value.inputCards.slice(i, i + chunk);
      this.cardsRows.push(temparray);
      // do whatever
    }
    this._configuration = value;
  }

  get configuration() {
    return this._configuration;
  }

  constructor(private enteService: EnteService, private router: Router) { }

  ngOnInit(): void {
  }

  navigateTo(card: Models.HorizontalCardModel): void {
    if (!!card.routerLink && !!card.routerLink.link) {
      this.router.navigate(card.routerLink.link, { queryParams: card.routerLink.params });
    }
    else {
      return null;
    }
  }
}
