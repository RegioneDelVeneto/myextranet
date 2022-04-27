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
import { Component, OnDestroy, OnInit, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { debounceTime, distinctUntilChanged, filter } from 'rxjs/operators';
import { Models } from 'src/app/models/model';
import { EnteService } from 'src/app/services/ente.service';
import { ProductsService } from 'src/app/services/products.service';
import { UserService } from 'src/app/services/user.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-rap-detail',
  templateUrl: './rap-detail.component.html',
  styleUrls: ['./rap-detail.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class RAPDetailComponent implements OnInit, OnDestroy {

  public RapInfo: string = `Inserisci l'Ente per cui vuoi diventare RAP.`;
  public entiArray: Models.enteDTO[] = [];

  public isLoading: boolean;
  public isSearchActive: boolean = true;

  private userInfo: Models.UserInfo;

  public showError = false;
  public errorMsg = "";

  public isRO: boolean = false;
  public areDocumentsRO: boolean = false;
  public isActive = false;
  public isExpired = false;
  public isReforward = false;
  public isRejected = false;
  public ROmsg: string = "Riceverai una email con l'esito della tua richiesta";
  public ActiveMsg: string = "Sei un RAP abilitato ad operare per questo ente";
  public ExpMsg: string = "L'abilitazione per questo ente è scaduta";
  public rejMsg: string = 'La richiesta RAP è stata rifiutata';

  public idRAP: string;

  public rappEnte: Models.RappEnteDTO;

  public reforwardMsg: string = 'Verificare che gli allegati inseriti nella domanda siano corretti.';

  public RAPform: FormGroup = this.formBuilder.group({
    enteAzienda: [null, Validators.required]
  });
  public RAPformRO: FormGroup = this.formBuilder.group({
    enteAzienda: [{ value: null, disabled: true }, Validators.required]
  });


  public datesForm: FormGroup = this.formBuilder.group({
    dtRichiesta: [{ value: null, disabled: true }],
    dtInizioVal: [{ value: null, disabled: true }],
    dtFineVal: [{ value: null, disabled: true }]
  });

  downloadLink = null;

  breadcrumbs = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca', url: '/utente/bacheca' },
      { label: 'I miei Prodotti', url: '/utente/prodotti' },
      { label: `Nuovo RAP` },
    )
  };
  constructor(
    private enteService: EnteService,
    private formBuilder: FormBuilder,
    private userService: UserService,
    private productService: ProductsService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnDestroy() {

  }

  ngOnInit(): void {
    this.isLoading = true;
    this.enteService.getAutocomplete(99999, false, true).then(enti => {
      this.entiArray = enti;
    });

    this.userService.getUserInfo().then(val => {
      this.userInfo = val;
      this.userService.checkUserInfo();

      this.route.queryParamMap.subscribe(queryParams => {

        if (queryParams.has('idRappr')) {
          this.isRO = true;
          this.idRAP = queryParams.get('idRappr');

          this.productService.getRap(this.idRAP).then(rappEnte => {
            if (rappEnte.flgAttivo === false && rappEnte.flgConferma === 1) {
              this.isRO = true;
              this.areDocumentsRO = true;
              this.isExpired = true;
              this.isReforward = false;
              this.isRejected = false;
              this.isActive = false;
            } else if (rappEnte.flgConferma === -1) { //risottomettere
              this.isRO = false;
              this.isExpired = false;
              this.isReforward = true;
              this.areDocumentsRO = false;
              this.isRejected = false;
              this.isActive = false;
              this.reforwardMsg = rappEnte.motivConferma;
            } else if (rappEnte.flgConferma === 2) { // non confermata
              this.isRO = true;
              this.isExpired = false;
              this.isReforward = false;
              this.areDocumentsRO = true;
              this.isRejected = true;
              this.isActive = false;
              this.rejMsg = rappEnte.motivConferma;
            } else if (rappEnte.flgConferma === 0) {
              this.isRO = true;
              this.areDocumentsRO = true;
              this.isExpired = false;
              this.isReforward = false;
              this.isRejected = false;
              this.isActive = false;
            } else if (rappEnte.flgAttivo === true && rappEnte.flgConferma === 1) { //confermato e attivo
              this.isRO = true;
              this.isExpired = false;
              this.areDocumentsRO = true;
              this.isReforward = false;
              this.isRejected = false;
              this.isActive = true;
            }
            this.rappEnte = rappEnte;
            this.datesForm.patchValue({
              dtRichiesta: new Date(this.rappEnte.dtRichiesta),
              dtInizioVal: new Date(this.rappEnte.dtInizioVal),
              dtFineVal: new Date(this.rappEnte.dtFineVal),
            });

            if (this.isRO) {

              this.RAPformRO.patchValue({
                enteAzienda: rappEnte.ente || null,
              });
            } else {

              this.RAPform.patchValue({
                enteAzienda: rappEnte.ente || null,
              });
            }
            this.isLoading = false;

          });
        } else {
          this.isRO = false;
          this.isLoading = false;
        }
      });

    })
    this.RAPform.get('enteAzienda').valueChanges.pipe(debounceTime(200),
      filter(entAz => !!this.isSearchActive),
      distinctUntilChanged()).subscribe(val => {
        const den = !!val && !!val.denominazione ? val.denominazione : val;
        this.enteService.getAutocompleteAsync(25, den, false).toPromise().then(value => {
          this.entiArray = value;
        });
      });
  }

  showDenomination(entity: { idEnte: number, denominazione: string }) {
    if (!entity || !entity.idEnte) return null;
    const id = entity.idEnte;
    return id !== null && id !== undefined ? entity.denominazione : null;
  }

  sumbmit() {

    const rapInfo: Models.RapInsertModel = {
      newRappresentanteEnte: {
        utente: { idUtente: this.userInfo.userId },
        ente: { idEnte: this.RAPform.value.enteAzienda.idEnte },
      },
    };

    this.isLoading = true;
    this.showError = false;
    this.errorMsg = "";
    if (this.isReforward) {

      let rappInfoPatch: Models.RapPatchModel = {
        newRappresentanteEnte: {
          utente: { idUtente: this.userInfo.userId },
          flgConferma: 0,
          idRappr: parseInt(this.idRAP, 10)
        },

      };

      this.productService.redoRAP(rappInfoPatch).then(() => {
        this.isLoading = false;
        this.router.navigate(['/utente/prodotti']);
      }).catch(err => {
        this.showError = true;
        this.errorMsg = "Si è verificato un errore durante la risoluzione della richiesta";
        if (!!err && err.error.code == "VALIDATION_ERROR") {

          this.errorMsg = "L'utente ha già una richiesta pendente";
        }
        this.isLoading = false;
      });
    } else {

      this.productService.insertRap(rapInfo).then(() => {
        this.isLoading = false;
        this.router.navigate(['/utente/prodotti']);
      }).catch(err => {
        this.showError = true;
        this.errorMsg = "Si è verificato un errore durante la risoluzione della richiesta";
        if (!!err && err.error.code == "VALIDATION_ERROR") {

          this.errorMsg = err.error.data;
        }
        this.isLoading = false;
      });
    }
  }

  getFileName(fileNameWithId: string): string {
    if (!fileNameWithId) return null;
    return fileNameWithId.split('_', (fileNameWithId.split('_').length - 1)).join('_');
  }

}
