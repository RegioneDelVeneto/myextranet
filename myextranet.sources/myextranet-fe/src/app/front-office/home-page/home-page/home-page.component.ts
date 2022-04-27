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

import { AfterViewInit, Component, OnInit, ViewEncapsulation } from '@angular/core';
import { Models } from 'src/app/models/model';

export interface Section {
  name: string;
  updated: Date;
}


@Component({
  selector: 'app-home-page',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.scss']
})
export class HomePageComponent implements OnInit, AfterViewInit {

  constructor() { }
  public homeCardConfig: Models.CardConfiguration[] = [
    {
      icon: 'email',
      label: 'MESSAGGI',
      endpoinr: ''
    },
    {
      icon: 'event',
      label: 'EVENTI',
      endpoinr: ''
    }, {
      icon: 'business',
      label: 'PROGETTI',
      endpoinr: ''
    }, {
      icon: 'bar_chart',
      label: 'PRODOTTI',
      endpoinr: ''
    }];

  dataSources = {
    messaggi: [
      {
        titolo: 'Titolo di prova',
        messaggio: 'Messaggio di prova'
      },
      {
        titolo: 'Titolo di prova',
        messaggio: 'Messaggio di prova'
      },
      {
        titolo: 'Titolo di prova',
        messaggio: 'Messaggio di prova'
      },
    ]
  };

  displayedColumnsConfiguration = {
    messaggi: [
      {
        definition: 'titolo',
        header: 'Titolo'
      },
      {
        definition: 'messaggio',
        header: 'Messaggio',
        span: 3
      }
    ]
  }


  ngOnInit(): void {
  }


  ngAfterViewInit() {

  }
}