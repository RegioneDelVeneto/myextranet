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
import { Component, Input, OnInit, TemplateRef, ViewEncapsulation } from '@angular/core';

@Component({
  selector: 'app-alert-in-page-fo',
  templateUrl: './alert-in-page-fo.component.html',
  styleUrls: ['./alert-in-page-fo.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class AlertInPageFoComponent implements OnInit {

  /**
   * 1 successo
   * 2 waringing
   * 3 errore
   */
  @Input() type: 1 | 2 | 3 = 1;
  @Input() msg: string;
  @Input() title: string;
  @Input() template: TemplateRef<any>;
  @Input() bottomTemplate: TemplateRef<any>;

  constructor() { }

  ngOnInit(): void {
  }


}
