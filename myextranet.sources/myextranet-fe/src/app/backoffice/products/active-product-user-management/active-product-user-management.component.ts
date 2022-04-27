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

import { ChangeDetectorRef, Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Models } from 'src/app/models/model';
import { ProductsService } from 'src/app/services/products.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { UserManagementContainerComponent } from 'src/app/shared/components/user-management-container/user-management-container.component';

@Component({
  selector: 'app-active-product-user-management',
  templateUrl: './active-product-user-management.component.html',
  styleUrls: ['./active-product-user-management.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ActiveProductUserManagementComponent implements OnInit {


  public isLoading = false;

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Prodotti attivi', url: `backoffice/active-products` },
      { label: 'Gestione utenti' }
    )
  };
  public idProdotto: string = null;

  public prodotto: Models.ProdottoAttivatoDto = null;
  public isRo: boolean = true;
  public utentiRuoliArray: Models.GruppoUtentiRuoloDTO[] = [];
  public touchedDataPost: Models.UtenteRuoliPostNewOperationDto[] = [];


  @ViewChild('userManager') userManager: UserManagementContainerComponent;

  constructor(
    private router: Router,
    private chngeDetectorRef: ChangeDetectorRef,
    private route: ActivatedRoute,
    private pService: ProductsService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      if (params.has('idProdotto')) {
        this.idProdotto = params.get('idProdotto');

        this.isLoading = true;
        this.pService.getProdottoAttivatoById(this.idProdotto, false).then(prodotto => {
          this.prodotto = prodotto;
          this.isRo = prodotto.numRich > 0;
          this.pService.getUtentiProdottiRuoli(this.idProdotto, false).then(pa => {
            this.utentiRuoliArray = pa;
            this.isLoading = false;
          });
        });
      }
      else {
        this.idProdotto = null;
      }
    });
  }

  navigateBack() {
    this.router.navigate(['backoffice/active-products']);
  }


  createSaveArray() {
    this.touchedDataPost = [];
    this.userManager.utentiRuoliExtended.forEach(ruolo => {

      ruolo.utenti.forEach(utente => {
        let usr: Models.UtenteRuoloDto = { ruolo: ruolo.ruolo, prodottoAttivato: this.prodotto, flgEnabled: 1 };
        if (utente) {
          Object.keys(utente).forEach(key => {

            if (key !== 'uiid' && key !== 'status' && key !== 'prevStatus' && key !== 'deleted' && key !== 'touched') {
              usr[key] = utente[key];
            }
            if (key === 'prodottoAttivato') {
              usr.prodottoAttivato = {
                idAttivazione: utente[key].idAttivazione
              }
            }
            if (key === 'utente' && utente[key] && utente[key].idUtente) {
              usr.utente = {
                idUtente: utente[key].idUtente
              }
            }

          });

          if (!!usr.utente && !!usr.utente.idUtente) {
            usr.codFiscale = null;
            usr.cognome = null;
            usr.nome = null;
          }

          if (!!utente.idUtenteProd && utente.deleted) {

            this.touchedDataPost.push({ tipoOperazione: 'elimina', utenteProdottoAttivato: usr });
          } else if (!utente.deleted && utente.touched) {

            this.touchedDataPost.push({ tipoOperazione: 'modifica', utenteProdottoAttivato: usr });
          } else if (!utente.deleted && !utente.touched && !utente.idUtenteProd) {
            this.touchedDataPost.push({ tipoOperazione: 'modifica', utenteProdottoAttivato: usr });
          }

        }
      });
    });

  }

  save() {
    this.isLoading = true;
    this.createSaveArray();
    this.pService.postProdottiRuoliAggiornati(this.touchedDataPost, false).then(
      exit => {
        this.isLoading = false;
        this.touchedDataPost = [];
        this.navigateBack();
      }
    ).catch(err => {
      this.isLoading = false;
    });

  }


}
