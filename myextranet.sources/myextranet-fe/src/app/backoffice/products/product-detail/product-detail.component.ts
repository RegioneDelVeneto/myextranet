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
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Models } from 'src/app/models/model';
import { MyportalService } from 'src/app/services/myportal.service';
import { ProductsService } from 'src/app/services/products.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProductDetailComponent implements OnInit {

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private myportalService: MyportalService,
    private ProductsService: ProductsService
  ) { }

  public isRo = false;
  public isEdit = false;
  public disabledSend = false;

  public breadcrumbs = null;

  public idProdotto: string = null;
  public idProdottoAtt: string = null;

  public prodottiAttivabiliArray: Models.ProdottoAttivabileDTO[] = [];
  public denominazioneProdottoAttivabileInput = '';
  public isLoading = true;

  public product: { nomeProdottoAttiv: string };
  public products: Models.ComboElement[] = [];

  public productDataForm: FormGroup = this.fb.group({
    idProdotto: [{ value: '', disabled: this.isRo }],
    idProdottoProp: [{ value: '', disabled: this.isRo }, [Validators.maxLength(250)]],
    nomeProdottoAttiv: [{ value: '', disabled: this.isRo }, [Validators.required, Validators.maxLength(150)]],
    desAttivazione: [{ value: '', disabled: this.isRo }, [Validators.maxLength(4000)]],
    desAttivazioneBreve: [{ value: '', disabled: this.isRo }, [Validators.maxLength(250)]],
    codAppProfMan: [{ value: '', disabled: this.isRo }, [Validators.maxLength(20)]],
    dtAttivabileDa: [{ value: '', disabled: this.isRo }, Validators.required],
    dtAttivabileA: [{ value: '', disabled: this.isRo }]
  });


  ngOnInit(): void {

    this.myportalService.getAllActivableProducts(false).then(val => {
      let productsRecords = val.records as Models.MyPortalContent[];
      this.products = productsRecords.map(v => ({ label: v.titolo, value: v.id }));
      this.products.unshift({ label: '', value: null });
    }).catch(err => {
      console.error(err);
    });

    this.route.paramMap.subscribe(params => {
      this.isLoading = true;
      if (params.has('idProdotto')) {
        this.idProdottoAtt = params.get('idProdotto');
        this.isEdit = true;

        this.ProductsService.getProdottoAttivabileById(this.idProdottoAtt, false).subscribe((prodotto: Models.ProdottoAttivabileDTO) => {
          this.product = prodotto;
          this.idProdotto = prodotto.idProdotto;
          this.denominazioneProdottoAttivabileInput = prodotto.prodottoProp && prodotto.prodottoProp.nomeProdottoAttiv ? prodotto.prodottoProp.nomeProdottoAttiv : null;
          this.asyncProdottoAttivabile().toPromise().then(prodotti => {

            this.productDataForm = this.fb.group({
              idProdotto: [{ value: prodotto.idProdotto, disabled: this.isRo }],
              idProdottoProp: [{ value: prodotto.prodottoProp?.idProdottoAtt ?? '', disabled: this.isRo }, [Validators.maxLength(250)]],
              nomeProdottoAttiv: [{ value: prodotto.nomeProdottoAttiv, disabled: this.isRo }, [Validators.required, Validators.maxLength(150)]],
              desAttivazione: [{ value: prodotto.desAttivazione, disabled: this.isRo }, [Validators.maxLength(4000)]],
              desAttivazioneBreve: [{ value: prodotto.desAttivazioneBreve, disabled: this.isRo }, [Validators.maxLength(250)]],
              codAppProfMan: [{ value: prodotto.codAppProfMan, disabled: this.isRo }, [Validators.maxLength(20)]],
              dtAttivabileDa: [{ value: !!prodotto.dtAttivabileDa ? new Date(prodotto.dtAttivabileDa) : null, disabled: this.isRo }, Validators.required],
              dtAttivabileA: [{ value: !!prodotto.dtAttivabileA ? new Date(prodotto.dtAttivabileA) : null, disabled: this.isRo }]
            });
            return Promise.resolve(prodotti);
          }).then((prodotti) => {
            this.prodottiAttivabiliArray = prodotti?.filter(p => p.idProdotto !== this.idProdotto);
            this.productDataForm.get('idProdottoProp').valueChanges.pipe(debounceTime(200), distinctUntilChanged()).subscribe(val => {
              this.ProductsService.getAutocompleteAsync(25, val, false).toPromise().then(value => {
                this.prodottiAttivabiliArray = value?.filter(p => p.idProdotto !== this.idProdotto);
              });
            });
            this.isLoading = false;
          });

          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'MyExtranet', url: `backoffice` },
              { label: 'Prodotti', url: `backoffice/activable-products` },
              { label: 'Modifica Prodotto' }
            )
          };
        });

      } else {
        this.isEdit = false;

        this.denominazioneProdottoAttivabileInput = null;

        this.asyncProdottoAttivabile().toPromise().then(prodotti => {
          this.prodottiAttivabiliArray = prodotti?.filter(p => p.idProdotto !== this.idProdotto);
          this.productDataForm.get('idProdottoProp').valueChanges.pipe(debounceTime(200), distinctUntilChanged()).subscribe(val => {
            this.ProductsService.getAutocompleteAsync(25, val, false).toPromise().then(value => {
              this.prodottiAttivabiliArray = value?.filter(p => p.idProdotto !== this.idProdotto);
            });
          });
          this.isLoading = false;

        });

        this.breadcrumbs = {
          base: '',
          entries: new Array<IBreadcrumbsEntry>(
            { label: 'MyExtranet', url: `backoffice` },
            { label: 'Prodotti', url: `backoffice/activable-products` },
            { label: 'Inserisci Prodotto' }
          )
        };
      }
    });
  }

  sendData(): void {
    const prodottoAttivabileDTO: any = {
      idProdottoAtt: this.idProdottoAtt || null,
      idProdotto: this.productDataForm.value.idProdotto || null,
      prodottoProp: this.productDataForm.value.idProdottoProp ? { idProdottoAtt: this.productDataForm.value.idProdottoProp } : null,
      nomeProdottoAttiv: this.productDataForm.value.nomeProdottoAttiv,
      desAttivazione: this.productDataForm.value.desAttivazione,
      desAttivazioneBreve: this.productDataForm.value.desAttivazioneBreve,
      codAppProfMan: this.productDataForm.value.codAppProfMan,
      dtAttivabileDa: this.fixTimezone(this.productDataForm.value.dtAttivabileDa),
      dtAttivabileA: this.fixTimezone(this.productDataForm.value.dtAttivabileA)
    };
    let savePromise: Promise<any> = null;
    if (this.isEdit) {
      savePromise = this.ProductsService.putProdottoAttivabile(prodottoAttivabileDTO, false);
    } else {
      savePromise = this.ProductsService.insertProdottoAttivabile(prodottoAttivabileDTO, false);
    }
    savePromise.then(esito => {
      this.router.navigate([`/backoffice/activable-products`]);
    });
  }

  fixTimezone(origDate: Date): Date {
    if (!!origDate) {
      const adjustedDate = new Date(origDate.valueOf() - origDate.getTimezoneOffset() * 60000);
      return adjustedDate;
    }
  }

  showDenominationProdottoAttivabile(id: string) {

    if (id === null || id === undefined) {
      return null;
    }

    const found = this.prodottiAttivabiliArray.find(prodotto => prodotto.idProdottoAtt === id);

    return found ? found.nomeProdottoAttiv : null;
  }

  asyncProdottoAttivabile() {
    return this.ProductsService.getAutocompleteAsync(25, this.denominazioneProdottoAttivabileInput, false);
  }


  undo() {
    this.router.navigate([`/backoffice/activable-products`]);
  }


}
