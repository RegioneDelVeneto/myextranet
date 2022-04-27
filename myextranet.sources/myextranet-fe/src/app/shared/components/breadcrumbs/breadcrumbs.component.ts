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
import { Location } from '@angular/common';
import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { isObject, isEmpty, size, join } from 'lodash';

export interface IBreadcrumbsEntry {
  label: string;
  url?: string;
  params?: any[];
}

export interface IBreadcrumbsConfig {
  base: string;
  entries: Array<IBreadcrumbsEntry>;
  enableAddFavourite?: boolean;
}

@Component({
  selector: 'app-breadcrumbs',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './breadcrumbs.component.html',
  styleUrls: ['./breadcrumbs.component.scss']
})
export class BreadcrumbsComponent implements OnInit {

  @Input()
  public config: IBreadcrumbsConfig;

  constructor(
    private location: Location,
    private router: Router,
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {
  }

  public iconStyle(entry: IBreadcrumbsEntry, index: number): string[] {
    return (index > 0) ? this.divider() : this.empty();
  }


  public entryStyle(entry: IBreadcrumbsEntry, index: number): string[] {
    if (isObject(this.config) && isObject(entry)) {
      if (index === 0) {
        return this.home();
      }
      if (size(this.config.entries) === (index + 1)) {
        return this.current();
      }
      if (!isEmpty(entry.url)) {
        return this.link();
      }
      return this.label();
    }
    return this.empty();
  }

  public navigate(entry: IBreadcrumbsEntry): void {
    if (isObject(entry)) {
      if (!isEmpty(entry.url)) {
        this.router.navigateByUrl(
          this.buildUrl(entry)
        );
      }
    }
  }

  private buildUrl(entry: IBreadcrumbsEntry): string {
    return join([this.base(), entry.url], '');
  }

  private base(): string {
    if (isObject(this.config) && !isEmpty(this.config.base)) {
      return this.config.base;
    }
    return '';
  }

  private empty = (): string[] => [];

  private home = (): string[] => ['myp-breadcrumbs-home'];

  private link = (): string[] => ['myp-breadcrumbs-link'];

  private label = (): string[] => ['myp-breadcrumbs-label'];

  private current = (): string[] => ['myp-breadcrumbs-current'];

  private divider = (): string[] => ['icon', 'icon-arrow-right'];

}
