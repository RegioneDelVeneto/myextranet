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
import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Models } from 'src/app/models/model';
import { MyboxService } from 'src/app/services/mybox.service';
import { ProductRequestsService } from 'src/app/services/product-requests.service';
import { ProductsService } from 'src/app/services/products.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { ProductRequestDocumentsTableComponent } from 'src/app/shared/components/product-request-documents-table/product-request-documents-table.component';
import { UserManagementContainerComponent } from 'src/app/shared/components/user-management-container/user-management-container.component';
import { MultiMailModalComponent } from 'src/app/shared/modals/multi-mail-modal/multi-mail-modal.component';
import { NotesModalComponent } from 'src/app/shared/modals/notes-modal/notes-modal.component';

@Component({
  selector: 'app-active-product-request-detail',
  templateUrl: './active-product-request-detail.component.html',
  styleUrls: ['./active-product-request-detail.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ActiveProductRequestDetailComponent implements OnInit {


  public isLoading = false;
  public isError = false;
  public idProdAttivRich: string;

  public prodotto: Models.ProdottoAttivatoDto = null;
  public prodottiRuoli: Models.RuoloDto[] = [];

  public requestWithSteps: Models.RichiestaProdottoWithStepDto = null;
  public activeStep: Models.StepProcProdottoDTO = null;

  public documentsDs: any[] = [];

  public utentiRuoliArray: any[] = [];

  public isRvStep = false;
  public isOpen = false;
  public areUsersEditable = false;
  public areDocsEditable = false;
  public status: 0 | 1 | 2 | 3 = 0;
  public step: number = null;
  public lastNotes: string = null;
  public documentOperations: 1 | 2 | 0 = 0;
  public richStatus: string;
  public richDescEstesa: string;
  public richAssignedTo: string;
  public richDesProdottoProc: string = null;
  public richDesStep: string = null;

  private params: ParamMap = null;

  public get DOC() {
    return document;
  }

  @ViewChild('userManager') userManager: UserManagementContainerComponent;
  @ViewChild('docManager') docManager: ProductRequestDocumentsTableComponent;

  public steps: {
    data: Date;
    stato: string;
    esecutore: string;
    operIns: string;
    note: string;
  }[] = [];

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Prodotti attivati', url: `backoffice/active-products` },
      { label: 'Richieste' },
      { label: 'Dettaglio richiesta' }
    )
  };

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private pService: ProductsService,
    private myboxSrv: MyboxService,
    private productRequestService: ProductRequestsService,
    public dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.params = params;
      if (params.has('idProdAttivRich')) {
        this.isLoading = true;
        this.idProdAttivRich = params.get('idProdAttivRich');
        this.pService.getRuoliProdotti(true).then(prodottiRuoli => {
          this.prodottiRuoli = prodottiRuoli;
          this.utentiRuoliArray = prodottiRuoli.map(val => {
            return {
              ruolo: val,
              utenti: []
            };
          });
          return this.pService.getRichiestaProdotto(this.idProdAttivRich, false);
        }).then(val => {
          this.requestWithSteps = val;
          this.prodotto = val.prodottoAttivato;
          this.activeStep = val.stepProcedimentoProdotto;

          this.isRvStep = this.activeStep.competenza === 'RV';
          this.step = this.activeStep.numStep;
          this.isOpen = val.flgFineRich === 0;
          this.documentOperations = this.isRvStep ? (this.activeStep.numStep === 1 ? 1 : (this.isOpen ? 2 : 0)) : 0;
          this.status = this.isOpen ? 1 : 2;
          this.areDocsEditable = this.isOpen && this.isRvStep;
          this.areUsersEditable = this.isRvStep && (this.activeStep.flgAggiornaUtenti === 1 || this.activeStep.numStep === 1) && this.isOpen;
          this.lastNotes = val.ultimeNote;

          this.richDesStep = this.activeStep.desStato;
          this.richDesProdottoProc = val.procedimentoProdotto.desProdottoProc;
          this.richStatus = this.activeStep.codStato;
          this.richDescEstesa = this.activeStep.procedimentoProdotto.desProdottoProcEstesa;
          this.richAssignedTo = this.activeStep.competenza === 'RV' ? 'Regione Veneto' : `${this.prodotto.ente.denominazione}`;

          this.utentiRuoliArray = this.utentiRuoliArray.map(ruolo => {
            return {
              ruolo: ruolo.ruolo,
              utenti: val.utenteRichiestaProdottoList.filter(value => value.richOper !== 'DEL' && value.ruoloProdotto.codRuolo === ruolo.ruolo.codRuolo)
            };
          });
          this.documentsDs = val.documentoRichiestaProdottoList;

          this.steps = val.stepRichiestaProdottoList.map(step => {

            return {
              data: new Date(step.dtStep),
              stato: step.codStato,
              esecutore: step.esecutore === 'RV' ? 'Regione Veneto' : (!!this.prodotto && !!this.prodotto.ente && !!this.prodotto.ente.denominazione ? `${this.prodotto.ente.denominazione}` : 'Ente'),
              operIns: step?.operIns ?? null,
              note: step.noteStep
            };
          });
          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'MyExtranet', url: `backoffice` },
              { label: 'Prodotti attivati', url: `backoffice/active-products` },
              { label: 'Richieste', url: `/backoffice/active-products/manage-requests/${this.prodotto.idAttivazione}` },
              { label: 'Dettaglio richiesta' }
            )
          };
          this.isLoading = false;
        });
      }
    });

  }

  navigateBack() {
    this.router.navigate([`/backoffice/active-products/manage-requests/${this.prodotto.idAttivazione}`]);

  }

  public manageToolBarEvent(opType: string): void {
    if (!opType) {
      return null;
    }

    switch (opType) {
      case 'goOn': {
        this.save(1, 0);
        break;
      }
      case 'goBack': {
        this.save(0, 0);
        break;
      }
      case 'undo': {
        this.save(1, 1);
        break;
      }
      case 'reOpen': {
        break;
      }
      case 'navBack': {

        this.navigateBack();

        break;
      }
    }
  }


  private save(esito: 1 | 0, flgAnnulla: 1 | 0 = 0): void {
    const utRuoliExt = this.userManager.utentiRuoliExtended;
    const docs = this.docManager.documentsDs;

    const dialogRef = this.dialog.open(NotesModalComponent, {
      minWidth: 600,
      data: {
        title: `${this.richStatus}`,
        description: flgAnnulla === 0 ? `Inserire una nota prima di procedere` : `Specificare la causa dell'annullamento della richiesta`,
        isFo: false,
        flgAnnulla
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (!result) return null;
      else if (!!result && !!result.saved) {
        this.isLoading = true;

        this.productRequestService.mapDocumentsToPostObject(docs, false).then(filesToSend => {

          this.isLoading = true;
          const richiestaStep = {
            idProdAttivRich: this.idProdAttivRich,
            esito,
            codStatoNew: null,
            note: result.value,
            documenti: filesToSend,
            utenti: this.productRequestService.filterUser(utRuoliExt),
            flgAnnulla
          };

          return this.productRequestService.aggiornaRichiesta(richiestaStep, false);

        }).then(resp => {
          this.isLoading = false;
          this.navigateBack();
        }).catch(err => {
          this.isLoading = false;
          this.isError = true;
          console.error(err);
        });
      }
    });
  }



  public downloadXLS(): void {
    window.open(`/richiesta-prodotto/${this.idProdAttivRich}/utenti-richiesta-prodotto-excel`);
  }

  public sendCom(): void {
    const dialogRef = this.dialog.open(MultiMailModalComponent, {
      minWidth: '480px',
      data: { title: 'Invia Comunicazione', description: `` }
    });
    dialogRef.afterClosed().subscribe((result: { messaggio: string, oggetto: string, indirizzi: string[], flgAllegaUtentiRich: boolean }) => {
      if (!result) {
        return null;
      } else {
        const com: Models.RequestMultiCom = {
          flgAllegaUtentiRich: !!result.flgAllegaUtentiRich ? 1 : 0,
          indirizzo: !!result.indirizzi ? result.indirizzi.join(';') : null,
          messaggio: result.messaggio,
          titolo: result.oggetto
        }
        this.isLoading = true;
        this.productRequestService.sendComToMultipleInputUsers(com, this.idProdAttivRich).then(() => {
          this.isLoading = false;
        }).catch(() => {
          this.isLoading = false;
        });
      }
    });
  }
}
