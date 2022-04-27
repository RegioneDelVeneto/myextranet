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
import { STEPPER_GLOBAL_OPTIONS } from '@angular/cdk/stepper';
import { Component, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { DisabledProductRequest, NoRequestConfigurationFound } from 'src/app/errors/custom-errors';
import { Models } from 'src/app/models/model';
import { EnteService } from 'src/app/services/ente.service';
import { MyboxService } from 'src/app/services/mybox.service';
import { MyportalService } from 'src/app/services/myportal.service';
import { MysysconfigService } from 'src/app/services/mysysconfig.service';
import { ProductRequestsService } from 'src/app/services/product-requests.service';
import { ProductsService } from 'src/app/services/products.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { ProductRequestStepManagementComponent } from 'src/app/shared/components/product-request-step-management/product-request-step-management.component';
import { NotesModalComponent } from '../../../../../shared/modals/notes-modal/notes-modal.component'




@Component({
  selector: 'app-product-request-procedure-fo',
  templateUrl: './product-request-procedure-fo.component.html',
  styleUrls: ['./product-request-procedure-fo.component.scss'],
  encapsulation: ViewEncapsulation.None,
  providers: [
    {
      provide: STEPPER_GLOBAL_OPTIONS,
      useValue: { displayDefaultIndicatorType: false }
    }
  ]
})
export class ProductRequestProcedureFoComponent implements OnInit {

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca', url: '/utente/bacheca' },
      { label: 'I miei Prodotti', url: '/utente/prodotti' },
      { label: 'Prodotti attivi' }
    )
  };
  public documentOperations: 1 | 2 | 0 = 0;

  public mainErrorMsg = 'Errore durante il caricamento della pagina';

  public showGoBackLink = false;

  public prodottiRuoli: Models.RuoloDto[] = [];
  public utentiRuoliArray: Models.GruppoUtentiRuoloDTO[] = [];
  public documentsDs: any[] = [];
  public isLoading: boolean = false;
  public isFirstLoading: boolean = true;

  public activableProduct: Models.ProdottoAttivabileDTO = null;

  public productCMS: Models.MyPortalContent = null;

  public showSave: boolean = true;

  public isWarning: boolean = false;
  public warningTitle: string = null;
  public warningMsg: string = null;

  public isErrorLoading: boolean = false;
  public isError: boolean = false;
  public errorTitle: string = null;
  public errorMsg: string = null;

  public isSuccess: boolean = false;
  public successTitle: string = null;
  public successMsg: string = null;

  public showNotes: boolean = true;
  public notesType: 1 | 2 | 3 = 2;
  public notesTitle: string = null;
  public notesMsg: string = null;

  public ultimeNote: string = null;

  private myportalUrl: string = null;
  public logoSrc: string = null;

  public idEnte: string = null;
  public ente: Models.enteDTO = null;
  public status: 0 | 1 | 2 | 3 = null; //0 -> da attivare; 1-> in corso; 2 -> attivo

  private activeProduct: Models.ProdottoAttivatoDto = null;

  public isRo: boolean = true;
  public isAssignedToTenant: boolean = false;


  public idProdotto: string = null;

  public firstStep: Models.ProcProdottoDTO = null;

  public step: number = 1;

  private activeStep: Models.StepProcProdottoDTO = null;
  private idProdAttivRich: number = null;
  public idRappr: string = null;

  public richStatus: string = null;
  public richDesStep: string = null;
  public richAssignedTo: string = null;
  public richDesProdottoProc: string = null;

  public isProductActive: boolean = false;

  public idAttivazione: number | string = null;

  public fakeform: FormControl = new FormControl(null, Validators.required);

  private queryParams: ParamMap = null;
  public disableProductControl: FormControl = new FormControl(null, Validators.required);

  @ViewChild('productRequestStepManagement') productRequestStepManagement: ProductRequestStepManagementComponent;

  constructor(
    private productService: ProductsService,
    private router: Router,
    private route: ActivatedRoute,
    private productRequestService: ProductRequestsService,
    private myportalSrv: MyportalService,
    private enteSrv: EnteService,
    private myboxSrv: MyboxService,
    private pService: ProductsService,
    private mysysConfigSrv: MysysconfigService,
    public dialog: MatDialog

  ) { }

  ngOnInit(): void {
    this.isFirstLoading = true;
    this.productService.getRuoliProdotti(true).then(prodottiRuoli => {
      this.prodottiRuoli = prodottiRuoli;
      this.utentiRuoliArray = prodottiRuoli.map(val => {
        return {
          ruolo: val,
          utenti: []
        };
      });

      this.route.queryParamMap.subscribe(queryParams => {
        this.showGoBackLink = false;
        this.mainErrorMsg = 'Errore durante il caricamento della pagina';
        this.queryParams = queryParams;
        const opRich: string = this.route?.snapshot?.data?.opRich ?? null;
        this.getProductData(opRich).then(() => {

          if (!!this.activableProduct.idProdotto) return this.myportalSrv.getContent(this.activableProduct.idProdotto, true);
          else return Promise.resolve(null);
        }).then(prodottoCms => {
          this.productCMS = prodottoCms;

          return this.enteSrv.getEnteById(parseInt(this.idEnte, 10), true).toPromise();
        }).then(ente => {
          this.ente = ente;
          this.richAssignedTo = this.isAssignedToTenant ? this.ente.denominazione : "Regione Veneto";
          return this.mysysConfigSrv.returnMyPortalUrl();
        }).then(mypUrl => {
          this.myportalUrl = mypUrl;
          if (!!this.productCMS) {
            this.logoSrc = this.myportalUrl.endsWith('/') ? `${this.myportalUrl}api/content/download?id=${this.productCMS.logo}` : `${this.myportalUrl}/api/content/download?id=${this.productCMS.logo}`; //logo
          }
          if (this.status !== 2) {
            this.breadcrumbs = {
              base: '',
              entries: new Array<IBreadcrumbsEntry>(
                { label: 'Bacheca', url: '/utente/bacheca' },
                { label: 'I miei Prodotti', url: '/utente/prodotti' }, 
                { label: 'Prodotti attivabili', url: this.idRappr !== null ? `/utente/prodotti/gestione?idEnte=${this.idEnte}&idRappr=${this.idRappr}` : `/utente/prodotti` },
                { label: `Attivazione Prodotto ${this.activableProduct.nomeProdottoAttiv}` }
              )
            };
          } else {

            this.breadcrumbs = {
              base: '',
              entries: new Array<IBreadcrumbsEntry>(
                { label: 'Bacheca', url: '/utente/bacheca' },
                { label: 'I miei Prodotti', url: '/utente/prodotti' }, 
                { label: 'Prodotti attivabili', url: this.idRappr !== null ? `/utente/prodotti/gestione?idEnte=${this.idEnte}&idRappr=${this.idRappr}` : `/utente/prodotti` },
                { label: `Dettaglio Prodotto ${this.activableProduct.nomeProdottoAttiv}` }
              )
            };
          }
          this.isFirstLoading = false;
        }).catch(err => {
          console.error(err);
          if (err instanceof NoRequestConfigurationFound || err instanceof DisabledProductRequest) {
            this.mainErrorMsg = err.message;
          } else if (err?.error?.code === 'VALIDATION_ERROR' || err?.error?.code === 'MISSING_CONF_ERROR') {
            this.showGoBackLink = err?.error?.code === 'MISSING_CONF_ERROR';
            this.mainErrorMsg = err.error.data;
          }
          this.isFirstLoading = false;
          this.isErrorLoading = true;
          window.scrollTo(0, 0);
        });
      });

    });

  }

  public getProductData(opRich: string): Promise<boolean> {
    if (this.queryParams.has('idRappr')) {
      this.idRappr = this.queryParams.get('idRappr');
    }
    if (this.queryParams.has('idProdotto') && this.queryParams.has('idEnte')) {
      this.idProdotto = this.queryParams.get('idProdotto');
      this.idEnte = this.queryParams.get('idEnte');
    }
    return this.productRequestService.getProductRequestData(this.idEnte, this.idProdotto, opRich, true)
      .then(resp => {
        let getStatus: number;
        if (!resp?.prodottoAttivato && !resp?.datiRichiesta && !!resp?.datiProcedimento && resp?.datiProcedimento.flgEnabled === 1) {
          getStatus = 0;
        } else if (!!resp?.prodottoAttivato && !resp?.datiRichiesta && !!resp?.datiProcedimento) {
          getStatus = -1; 
        } else if (!!resp?.datiRichiesta) {
          getStatus = 1;
        } else if (!!resp?.prodottoAttivato && !resp?.datiRichiesta && resp?.prodottoAttivato?.valid) {
          getStatus = 2;
        } else if (!!resp?.prodottoAttivato && !resp?.datiRichiesta && !resp?.prodottoAttivato?.valid) {
          getStatus = 3;
        } else {
          if (!resp?.datiProcedimento) {
            throw new NoRequestConfigurationFound('#PRCNF:Procedure request configuration not found');
          } else if (!!resp?.datiProcedimento && resp?.datiProcedimento.flgEnabled === 0) {
            throw new DisabledProductRequest(resp?.datiProcedimento?.prodotto?.nomeProdottoAttiv ?? null, '#DPR');
          }
          throw new Error('Unable to identify request status');
        }

        switch (getStatus) {

          case 0: {
            this.isRo = false;
            this.notesType = 2;
            this.firstStep = resp.datiProcedimento;
            this.activableProduct = resp.datiProcedimento.prodotto;
            
            this.isAssignedToTenant = resp.datiProcedimento.step[0].competenza === 'EN';
            this.documentOperations = this.isAssignedToTenant ? 1 : 0;
            this.richStatus = this.firstStep.step[0].codStato;
            this.richDesStep = this.firstStep.step[0].desStato;
            this.ultimeNote = null;
            this.step = 1;
            this.richDesProdottoProc = resp?.datiProcedimento?.desProdottoProc;

            this.isProductActive = resp?.prodottoAttivato?.stato === 2 || false;

            this.status = 0;

            break;
          }

          case 1: {
            this.activeProduct = resp.datiRichiesta.prodottoAttivato;
            this.activableProduct = resp.datiRichiesta.prodottoAttivato.prodottoAttivabile;
            
            this.isProductActive = resp?.prodottoAttivato?.stato === 2 || false;

            this.populateUserArray(resp.datiRichiesta.utenteRichiestaProdottoList || []);
            this.documentsDs = resp.datiRichiesta.documentoRichiestaProdottoList;
            this.ultimeNote = resp.datiRichiesta.ultimeNote;

            this.notesType = 2;
            this.idProdAttivRich = resp.datiRichiesta.idProdAttivRich;
            this.activeStep = resp.datiRichiesta.stepProcedimentoProdotto;
            this.isRo = !(this.activeStep.competenza === 'EN' && this.activeStep.numStep === 1);
            
            this.isAssignedToTenant = this.activeStep.competenza === 'EN';

            this.documentOperations = this.isAssignedToTenant ? (this.activeStep.numStep === 1 ? 1 : 2) : 0;
            this.step = this.activeStep.numStep;

            this.richStatus = this.activeStep.codStato;
            this.richDesStep = this.activeStep.desStato;

            this.richDesProdottoProc = resp?.datiProcedimento?.desProdottoProc;
            this.status = 1;

            break;
          }


          case 2: {
            this.activeProduct = resp.prodottoAttivato;
            this.activableProduct = resp.prodottoAttivato.prodottoAttivabile;
            this.isProductActive = resp?.prodottoAttivato?.stato === 2;
            this.populateUserArray(this.activeProduct.utenteProdottoAttivatoList || []);

            this.disableProductControl.patchValue(new Date(this.activeProduct.dtFineValLD));
            this.isRo = true;
            this.documentOperations = 0;

            this.notesType = 1;
            this.isAssignedToTenant = true;
            this.step = null;

            this.status = 2;
            break;
          }

          case 3: {
            this.activeProduct = resp.prodottoAttivato;
            this.activableProduct = resp.prodottoAttivato.prodottoAttivabile;
            this.isProductActive = resp?.prodottoAttivato?.stato === 2;
            this.populateUserArray(this.activeProduct.utenteProdottoAttivatoList || []);

            this.disableProductControl.patchValue(new Date(this.activeProduct.dtFineValLD));
            this.isRo = true;
            this.documentOperations = 0;

            this.notesType = 2;
            this.isAssignedToTenant = false;
            this.disableProductControl.disable();
            this.step = null;

            this.status = 3;

            break;
          }

          case -1: {
            this.isRo = false;
            this.activeProduct = resp.prodottoAttivato;

            this.idAttivazione = this.activeProduct.idAttivazione;
            this.isProductActive = resp?.prodottoAttivato?.stato === 2 || false;

            this.notesType = 2;
            this.firstStep = resp.datiProcedimento;
            this.activableProduct = resp.prodottoAttivato.prodottoAttivabile;
            

            this.populateUserArray(this.activeProduct.utenteProdottoAttivatoList || []);


            this.isAssignedToTenant = resp.datiProcedimento.step[0].competenza === 'EN';
            this.documentOperations = this.isAssignedToTenant ? 1 : 0;
            this.richStatus = this.firstStep.step[0].codStato;
            this.richDesStep = this.firstStep.step[0].desStato;
            this.ultimeNote = null;
            this.richDesProdottoProc = resp?.datiProcedimento?.desProdottoProc;
            this.step = 1;

            this.status = 0;

            break;
          }
        }

        this.idAttivazione = this.idAttivazione ?? (this.activeProduct && this.activeProduct.idAttivazione? this.activeProduct.idAttivazione : null);

        return Promise.resolve(true);
      });

  }


  private populateUserArray(user: Models.UtenteRichiestaProdottoModel[]) {
    this.utentiRuoliArray = this.utentiRuoliArray.map(ruolo => {
      return {
        ruolo: ruolo.ruolo,
        utenti: user.filter(val => val?.richOper !== 'DEL' && val?.ruoloProdotto?.codRuolo === ruolo.ruolo.codRuolo || val?.ruolo?.codRuolo === ruolo.ruolo.codRuolo).map(uttenteMap => {
          if (uttenteMap.idUtenteProd !== null && uttenteMap.idUtenteProd !== undefined) {
            return { ...uttenteMap, utenteProdottoAttivato: { idUtenteProd: uttenteMap.idUtenteProd } };
          }
          else {
            return uttenteMap;
          }
        })
      };
    });
  }

  getProductDataOld(opRich: string): Promise<boolean> {

    if (this.queryParams.has('idRappr')) {
      this.idRappr = this.queryParams.get('idRappr');
    }
    let promise: Promise<any> = null;
    if (this.queryParams.has('idProdotto') && this.queryParams.has('idEnte')) {
      this.idProdotto = this.queryParams.get('idProdotto');
      this.status = 0;
      this.isRo = false;
      this.idEnte = this.queryParams.get('idEnte');
      //call per primo step
      promise = this.productRequestService.getFirstStep(this.queryParams.get('idEnte'), this.queryParams.get('idProdotto'), opRich);
    } else if (this.queryParams.has('idAttivazione')) {
      //call per richiesta
      this.idAttivazione = this.queryParams.get('idAttivazione');
      promise = this.productService.getProdottoAttivatoById(this.queryParams.get('idAttivazione'), true);

    } else {
      this.isLoading = false;
      this.isError = true;
      return null;
    }
    return promise.then(resp => {

      if (this.status === 0) {

        this.notesType = 2;
        this.firstStep = resp;
        this.activableProduct = resp.prodotto;
        
        this.isAssignedToTenant = resp.step[0].competenza === 'EN';
        this.documentOperations = this.isAssignedToTenant ? 1 : 0;
        this.richStatus = this.firstStep.step[0].codStato;
        
        this.ultimeNote = null;
        this.step = 1;
        // get per il singolo prodotto per i lcontenuto di mycms
      } else {
        this.activeProduct = resp;
        this.activableProduct = resp.prodottoAttivabile;
        this.idEnte = this.activeProduct.ente.idEnte.toString();

        if (!resp.richiestaProdottoWithStep) {
          this.utentiRuoliArray = this.utentiRuoliArray.map(ruolo => {
            return {
              ruolo: ruolo.ruolo,
              utenti: this.activeProduct.utenteProdottoAttivatoList.filter(val => val.ruolo.codRuolo === ruolo.ruolo.codRuolo)
            };
          });
          this.disableProductControl.patchValue(new Date(resp.dtFineValLD));
          this.isRo = true;
          this.documentOperations = 0;

          if (this.activeProduct.valid) { //prodotto attivo
            this.status = 2;
            this.notesType = 1;
            this.isAssignedToTenant = true;
            this.step = null;
          } else { //prodotto attivo "scaduto"
            this.status = 3;
            this.notesType = 2;
            this.isAssignedToTenant = false;
            this.disableProductControl.disable();
            this.step = null;
          }


        } else if (!!resp.richiestaProdottoWithStep) {
          this.utentiRuoliArray = this.utentiRuoliArray.map(ruolo => {
            return {
              ruolo: ruolo.ruolo,
              utenti: this.activeProduct.richiestaProdottoWithStep.utenteRichiestaProdottoList.filter(val => val.ruoloProdotto.codRuolo === ruolo.ruolo.codRuolo)
            };
          });
          this.documentsDs = this.activeProduct.richiestaProdottoWithStep.documentoRichiestaProdottoList;
          this.ultimeNote = this.activeProduct.richiestaProdottoWithStep.ultimeNote;
          this.status = 1;
          this.notesType = 2;
          this.idProdAttivRich = resp.richiestaProdottoWithStep.idProdAttivRich;
          this.activeStep = resp.richiestaProdottoWithStep.stepProcedimentoProdotto;
          this.isRo = !(this.activeProduct.richiestaProdottoWithStep.stepProcedimentoProdotto.competenza === 'EN' && this.activeStep.numStep === 1);
          
          this.isAssignedToTenant = this.activeProduct.richiestaProdottoWithStep.stepProcedimentoProdotto.competenza === 'EN';

          this.documentOperations = this.isAssignedToTenant ? (this.activeStep.numStep === 1 ? 1 : 2) : 0;
          this.step = this.activeStep.numStep;

          this.richStatus = this.activeStep.codStato;
                    

        }

      }
      return Promise.resolve(true);
    }).catch(err => {
      throw new Error(err);
    });
  }

  public showSaveBar(event: boolean): void {
    this.showSave = !!event;
  }

  public save(esito: 1 | 0, flgAnnulla: 1 | 0 = 0): void {
    if (this.status === 3) {
      return null;
    }

    const utRuoliExt = this.productRequestStepManagement.utentiRuoliExtended;

    const dialogRef = this.dialog.open(NotesModalComponent, {
      minWidth: 600,
      data: {
        title: flgAnnulla === 1 ? 'Annulla richiesta' : esito === 1 ? 'Conferma avanzamento di stato' : 'Rifiuta avanzamento di stato',
        description: flgAnnulla === 0 ? `Inserire una nota prima di procedere` : `Specificare la causa dell'annullamento della richiesta`,
        isFo: true,
        flgAnnulla
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (!result) return null;
      else if (!!result && !!result.saved){
        this.isLoading = true;
        this.productRequestService.mapDocumentsToPostObject(this.productRequestStepManagement.docManager.documentsDs, true).then(filesToSend => {
          if (this.status === 1) { //step intermedio

            let richiestaStep = {
              idProdAttivRich: this.idProdAttivRich,
              esito: esito, 
              
              codStatoNew: null,
              note: result.value,
              documenti: filesToSend,
              utenti: this.productRequestService.filterUser(utRuoliExt),
              flgAnnulla: flgAnnulla
            };
            return this.productRequestService.aggiornaRichiesta(richiestaStep, true);

          } else if (this.status === 0) { //step iniziale

            let richiestaStepIniziale = {
              idEnte: this.idEnte,
              idAttivazione: this.idAttivazione || null,
              esito: 1,
              idProdottoAtt: this.idProdotto,
              codTipoRich: this.firstStep.tipoRichiestaProdotto.codTipoRich,
              note: result.value,
              documenti: filesToSend,
              utenti: this.productRequestService.filterUser(utRuoliExt)
            }
            return this.productRequestService.postAvvioRichiesta(richiestaStepIniziale, true);

          }
        }).then(resp => {
          this.isLoading = false;
          this.navigateBack();
        }).catch(err => {
          this.isLoading = false;
          this.isError = true;
          window.scrollTo(0, 0);
          console.error(err);
        });
      }
    });
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
        this.router.navigate(['/utente/prodotti/gestione/nuova-richiesta-aggiorna-utenti'], { queryParams: { idProdotto: this.activeProduct.prodottoAttivabile.idProdottoAtt, idEnte: this.idEnte, idRappr: this.idRappr } });
        break;
      }
      case 'navBack': {
        this.navigateBack();
        break;
      }
    }
  }

  private navigateBack(): void {
    if (this.idRappr !== null) {
      this.router.navigate(['/utente/prodotti/gestione'], { queryParams: { idEnte: this.idEnte, idRappr: this.idRappr } });

    }
    else {
      this.router.navigate(['/utente/prodotti']);
    }
  }

  public updateEndOfCal(): void {
    if (this.status !== 2) {
      return null;
    }
    let parsedDate = new Date(this.disableProductControl.value);
    let adjustedDate = new Date(parsedDate.valueOf() - parsedDate.getTimezoneOffset() * 60000);
    this.isLoading = true;
    this.pService.patchProdottoAttivo({
      idAttivazione: this.idAttivazione,
      dtFineVal: adjustedDate.valueOf()
    }, true
    ).then(resp => {
      return this.getProductData(null);
    }).then(() => {
      window.scrollTo(0, 0);
      this.isLoading = false;      
    }).catch(err => {
      this.isError = true;
      window.scrollTo(0, 0);
      this.isLoading = false;
    });

  }
}
