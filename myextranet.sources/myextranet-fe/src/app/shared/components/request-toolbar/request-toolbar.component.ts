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
import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { Models } from 'src/app/models/model';

@Component({
  selector: 'app-request-toolbar',
  templateUrl: './request-toolbar.component.html',
  styleUrls: ['./request-toolbar.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class RequestToolbarComponent implements OnInit {

  constructor() { }
  private _configuration: Models.RequestToolbarConfiguration = null;
  @Input()
  set configuration(config: Models.RequestToolbarConfiguration) {

    this.step = config.step;
    this.status = config.status;

    this.valid = config.valid;

    if (this.step === 1) {
      this.barType = 1;
    } else if (this.status === 1) {
      this.barType = 2;
    } else if (this.status === 2) {
      this.barType = 3;
    } else {
      this.barType = null;
    }
  }
  get configuration(): Models.RequestToolbarConfiguration {
    return this._configuration;
  }
  @Input() isFo: boolean;

  @Output()
  sendEventTofather: EventEmitter<string> = new EventEmitter<string>();

  public barType: number = 0;
  public step: number;
  public status: 0 | 1 | 2 | 3;

  public valid: boolean;

  ngOnInit(): void {
  }
 
}
