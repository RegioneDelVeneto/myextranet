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
import { Models } from 'src/app/models/model';

@Component({
  selector: 'app-generic-card-container',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './generic-card-container.component.html',
  styleUrls: ['./generic-card-container.component.scss']
})
export class GenericCardContainerComponent implements OnInit {

  cardsRows = [];

  private _configuration
  @Input() set configuration(value: { type: string, inputCards: any[] }) {
    this.cardsRows = [];
    switch (value.type) {
      case 'events': {
        value.inputCards = value.inputCards.map((value: Models.MyPortalEvent) => {

          const cardOutput: Models.GenericCardModel = {
            titolo: value.titolo,
            body: value.descrizione,
            card: value.logo,
            data: value.dateDa,
            routerLink: `/utente/eventi/iscrizione-evento`,
            queryParams: { eventId: value.id },
            annullato: !!value.annullato ? true : false,
            id: value.id,
            content: value,
            miniIcon: 'event'
          };
          return cardOutput;

        });
        break;
      }
      case 'projects': {
        value.inputCards = value.inputCards.map((project: Models.ProgettoExtended) => {
          let value = project.myExtranetContent;
          let collab = project.collaboratoreProgetto;
          const cardOutput: Models.GenericCardModel = {
            icon: !!collab && !!collab.flgConferma ? (collab.flgConferma === 1 ? 'check_circle_outline' : 'dangerous') : 'warning',
            titolo: value.titolo,
            subtitle: !!collab && !!collab.flgConferma ? null : 'Collaborazione in attesa di conferma',
            body: value.descrizione,
            card: value.logo,

            routerLink: `/utente/progetti/collabora`,
            queryParams: { projectId: value.id },
            id: value.id,
            content: project,

          };
          return cardOutput;

        });
        break;
      }
    }

    let i: number;
    let j: number;
    let temparray: any;
    let chunk = 3;

    for (i = 0, j = value.inputCards.length; i < j; i += chunk) {
      temparray = value.inputCards.slice(i, i + chunk);
      this.cardsRows.push(temparray);
    }
    this._configuration = value;

  }

  get configuration() {
    return this._configuration;
  }

  constructor() { }

  ngOnInit(): void {
  }

}
