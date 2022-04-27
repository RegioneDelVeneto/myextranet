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

@Component({
  selector: 'app-generic-card',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './generic-card.component.html',
  styleUrls: ['./generic-card.component.scss']
})
export class GenericCardComponent implements OnInit {

  @Input() i;
  @Input() data;
  constructor(private router: Router) { }

  ngOnInit(): void {
  }

  public navigateToRoute(route: string, queryParams: Record<string, string>): void {
    if (!route) {
      return null;
    }
    else {
      this.router.navigate([`${route}`], {queryParams});
    }
  }

}
