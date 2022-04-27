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

@Component({
  selector: 'app-footer-public',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './footer-public.component.html',
  styleUrls: ['./footer-public.component.scss']
})
export class FooterPublicComponent implements OnInit {

  constructor() { }

  public configuration: { [key: string]: any } = {
    urlLogo: 'assets/images/u101.png',
    hidePersonalArea: null,
    copyright: 'Copyright: © 2011-2014 Regione del Veneto - P.I. 02392630279 - | Centralino: 041.279.21.11',
    bottomLinks: [
      {
        url: '/utente/bacheca',
        label: 'Homepage',
        type: 'route'
      },
      {
        url: 'https://www.regione.veneto.it/web/guest/accessibilita',
        label: 'Accessibilità',
        type: 'url'
      },
      {
        url: 'https://www.regione.veneto.it/web/guest/accessibilita',
        label: 'Dichiarazione di accessibilità',
        type: 'url'
      },
      {
        url: 'http://www.regione.veneto.it/web/guest/privacy',
        label: 'Informativa Privacy',
        type: 'url'
      },
      {
        url: 'http://myportal-infopbl.regione.veneto.it/myportal/INFOPBL/informative/info_cookies',
        label: 'Informativa Cookie',
        type: 'url'
      },
      {
        url: 'http://myportal-infopbl.regione.veneto.it/myportal/INFOPBL/informative/note_legali',
        label: 'Note legali',
        type: 'url'
      }
    ],
    socialButtons: [],
    freeText2Text: '',
    freeText2Title: '',
    freeText1Text: '',
    freeText1Title: '',
    links: [],
    logo: '',
    title: ' MyExtranet'
  };

  ngOnInit(): void {
  }

  public get hidePersonalArea(): boolean {
    return !!this.configuration.hidePersonalArea;
  }

  public decodeHTML(encoded: string): string {
    return decodeURIComponent(encoded);
  }

}
