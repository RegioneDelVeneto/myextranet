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
import { Component, OnInit, HostListener, ViewChild, AfterViewInit, Input, ViewEncapsulation } from '@angular/core';
import { Models } from 'src/app/models/model';
import { MysysconfigService } from 'src/app/services/mysysconfig.service';
import { UserService } from 'src/app/services/user.service';

@Component({
  selector: 'app-header-public',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './header-public.component.html',
  styleUrls: ['./header-public.component.scss']
})
export class HeaderPublicComponent implements OnInit, AfterViewInit {


  @Input() userInfo: Models.UserInfo = null;

  constructor(private userService: UserService, private mySysConfSrv: MysysconfigService) { }

  public get urlLogo(): string {
    return this.configuration.logo;
  }

  public get isMenuOpen() {
    if (!!this.menuLeft && this.menuLeft.nativeElement) {
      return this.menuLeft.nativeElement.classList.contains('cbp-spmenu-open');
    }
    else { return false; }
  }

  @ViewChild('cbpspmenu')
  public menuLeft: any;

  @ViewChild('header')
  public header: any;

  public body: any;

  public headerWrapper: any;

  public userLoginDropdown = false;
  public showFormSuggestionMobile = false;

  public configuration: {
    hidePersonalArea: boolean,
    socialButtons: any[],
    searchPagePath: string
    showAllMenuItem: boolean,
    menu:
    {
      important: boolean,
      openInto: string,
      name: string,
      items?: any[]
    }[],
    logo: string,
    subtitle: string,
    title: string
  } = {
      hidePersonalArea: null,
      socialButtons: [],
      searchPagePath: '/ricerca',
      showAllMenuItem: true,
      menu: [
        {
          important: true,
          openInto: '/utente/eventi',
          name: 'I miei Eventi',
        },
        {
          important: true,
          openInto: '/utente/progetti',
          name: 'I miei Progetti',
        },
        {
          important: true,
          openInto: '/utente/prodotti',
          name: 'I miei Prodotti',
        },
        {
          important: true,
          openInto: '/utente/user/edit',
          name: 'I miei Dati',
        },
      ],
      logo: 'assets/images/u101.png',
      subtitle: 'Impara, partecipa e collabora',
      title: 'Area Privata'
    };

  private lastChangeTime = 0;
  private headerThrottleTime = 500;
  private stickyAdded = false;

  public myportalUrl: string = null;

  ngOnInit(): void {
    this.mySysConfSrv.getMyPortalDeployURL().then(url => this.myportalUrl = url).catch(err => console.error(err));
    this.body = document.querySelectorAll('.row .col-md-12.widget-container');

  }

  public ngAfterViewInit(): void {
    this.headerWrapper = document.querySelector('.wrapper');

  }

  public search($event: any): void {
  }

  public goToMyIDMyExtranetLoginPage() {
    return null;
  }

  public goToMyIDorSPIDLoginPage() {
    return null;
  }
  public redirectFascicolo() {
    return null;
  }

  public toggleMenu(): void {
    this.menuLeft.nativeElement.classList.toggle('cbp-spmenu-open');
    this.header.nativeElement.classList.toggle('cbp-spmenu-push-toright');
    this.body.forEach((e: Element) => e.classList.toggle('cbp-spmenu-push-toright'));
  }

  public getSocialName(name: string): string {
    if (name === 'yt') {
      return 'Youtube';
    }
    else if (name === 'instagram') {
      return 'Instagram';
    }
    else if (name === 'fb') {
      return 'Facebook';
    }
    else if (name === 'tw') {
      return 'Twitter';
    }
    else {
      return name;
    }
  }

  public removeSpaces(text: string): string {
    return text.replace(/ /g, '_');
  }

  public isAbsoluteUrl(value: string): boolean {
    return /((([A-Za-z]{3,9}:(?:\/\/)?)(?:[\-;:&=\+\$,\w]+@)?[A-Za-z0-9\.\-]+|(?:www\.|[\-;:&=\+\$,\w]+@)[A-Za-z0-9\.\-]+)((?:\/[\+~%\/\.\w\-_]*)?\??(?:[\-\+=&;%@\.\w_]*)#?(?:[\.\!\/\\\w]*))?)/g.test(value);
  }

  public openExternalUrl(value: string): void {
    window.open(value, '_blank');
  }

  public collaspeGroupMenu(id: string, group: string): void {
    const menu = document.getElementById(id + group);
    let arrayMenus: HTMLCollectionOf<Element>;
    let arrayMenusLinks: HTMLCollectionOf<Element>;
    if (id.indexOf('horizontal') !== -1) {
      const link = document.getElementById('link-' + group);
      arrayMenus = document.getElementsByClassName('megamenu-subnav');
      arrayMenusLinks = document.getElementsByClassName('megamenu-group-link');
      for (let i = 0; i < arrayMenus.length; i++) {
        if (!arrayMenus[i].classList.contains('d-none') && arrayMenus[i] !== menu) {
          arrayMenus[i].classList.add('d-none');
          arrayMenusLinks[i].classList.remove('active');
        }
      }
      menu.classList.toggle('open');
      link.classList.toggle('active');
    }
    else {
      if (menu && menu.previousElementSibling) {
        menu.previousElementSibling.classList.toggle('open');
      }
    }
    if (menu) {
      menu.classList.toggle('d-none');
    }
  }

  private getHeight() {
    const body = document.body,
      html = document.documentElement;

    const height = Math.max(body.scrollHeight, body.offsetHeight,
      html.clientHeight, html.scrollHeight, html.offsetHeight);
    return height;
  }

  @HostListener('window:scroll', [])
  public onWindowScroll(): void {
    if (new Date().getTime() - this.lastChangeTime < this.headerThrottleTime && this.stickyAdded) {
      window.scrollTo({ top: window.pageYOffset || 1 });
      return;
    }
    if (this.getHeight() - window.innerHeight < 100 && !this.stickyAdded) {
      return;
    }
    if (window.pageYOffset > 0 && !this.stickyAdded) {
      this.headerWrapper.classList.add('smaller');
      this.stickyAdded = true;
      this.lastChangeTime = new Date().getTime();
    } else if (window.pageYOffset < 1 && this.stickyAdded) {
      this.headerWrapper.classList.remove('smaller');
      this.stickyAdded = false;
      this.lastChangeTime = new Date().getTime();
    }
  }

  logout() {
    this.userService.logout().then(() => {
      window.open(this.myportalUrl, '_self');
    });
  }

}
