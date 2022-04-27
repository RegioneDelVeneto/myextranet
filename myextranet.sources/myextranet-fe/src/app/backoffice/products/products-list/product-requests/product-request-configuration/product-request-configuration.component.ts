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
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Models } from 'src/app/models/model';
import { MyextTableService } from 'src/app/services/myext-table.service';
import { ProductRequestsService } from 'src/app/services/product-requests.service';
import { ProductsService } from 'src/app/services/products.service';
import { IBreadcrumbsConfig, IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
import { CardTableContainerComponent } from 'src/app/shared/components/data-table/card-table-container/card-table-container.component';
import { v4 as uuidv4 } from 'uuid';




@Component({
  selector: 'app-product-request-configuration',
  templateUrl: './product-request-configuration.component.html',
  styleUrls: ['./product-request-configuration.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class ProductRequestConfigurationComponent implements OnInit {

  @ViewChild(CardTableContainerComponent) dataTable: CardTableContainerComponent;

  public breadcrumbs: IBreadcrumbsConfig = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'MyExtranet', url: `backoffice` },
      { label: 'Prodotti attivabili' }
    )
  };

  public isLoading = false;
  public isError = false;
  public isEdit = false;

  public isEditing = false;
  public isInserting = false;

  public showForm = false;

  public productId: string;
  public requestId: number = null;

  public stepDataSource: any[] = [];
  public stepDataSourceBackUp: any[] = [];

  public deletedStepDataSource: any[] = [];

  public tipiRichiesta: Models.TipoRichiestaProdDTO[] = [];

  public product: Models.ProdottoAttivabileDTO = null;
  public productRequest: Models.ProcProdottoDTO = null;
  public productRequestBackUp: any = {
    tipoRichiestaProdotto: null,
    flgEnabled: true,
    desProdottoProc: null,
    desProdottoProcEstesa: null,
  };

  public productRequestDetails: FormGroup = this.formBuilder.group({
    tipoRichiestaProdotto: [null, Validators.required],
    flgEnabled: [true],
    desProdottoProc: [null, [Validators.required, Validators.maxLength(100)]],
    desProdottoProcEstesa: [null, [Validators.required, Validators.maxLength(2000)]],
  });

  public productRequestStep: FormGroup = this.formBuilder.group({
    numStep: [{ value: null, disabled: true }],
    codStato: [null, [Validators.required, Validators.maxLength(100)]],
    desStato: [null, [Validators.required]],
    competenza: [null, [Validators.required]],
    flgFineRich: [null, [Validators.required]],
    uuid: null
  });

  public competenze: { value: string, label: string }[] = [
    {
      value: 'RV',
      label: 'Regione Veneto'
    },
    {
      value: 'EN',
      label: 'Ente'
    }
  ];

  public statiRichiesta: { value: number, label: string }[] = [
    {
      value: 0,
      label: 'Richiesta in corso'
    },
    {
      value: 1,
      label: 'Fine richiesta con esito positivo'
    },
    {
      value: 2,
      label: 'Fine richiesta con esito negativo'
    }
  ];


  public footer: {
    active: boolean,
    buttons: {
      label: string,
      event: string,
      icon: string,
      color?: string
    }[]
  } = {
      active: true,
      buttons: [
        {
          label: 'AGGIUNGI STATO',
          icon: 'add',
          event: 'new'
        }
      ]
    };

  public tableHeader: Models.TableHeader[] = [
    {
      definition: 'move',
      header: '',
      type: 'move',
      actions: [{
        icon: 'arrow_upward',
        event: 'move-up',
        label: 'sposta la riga in alto di 1 step',
      },
      {
        icon: 'arrow_downward',
        event: 'move-down',
        label: 'sposta la riga in basso di 1 step',
      }]
    },
    {
      definition: 'codStato',
      header: 'Codice stato',
      span: 3
    },
    {
      definition: 'desStato',
      header: 'Descrizione stato',
      textCut: 2,
      span: 3
    },
    {
      definition: 'competenza',
      header: 'Competenza',
      span: 2,
      type: 'objectMapper',
      objectMapped: {
        EN: 'Ente',
        RV: 'Regione Veneto',
      }
    }, {
      definition: 'flgFineRich',
      header: 'Fine richiesta',
      span: 2,
      type: 'objectMapper',
      objectMapped: {
        0: 'No',
        1: 'Sì',
        2: 'Sì',
      }
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [{
        icon: 'edit',
        event: 'edit',
        label: 'MODIFICA',
        hiddenIf: 'deleted'
      },
      {
        icon: 'delete',
        event: 'delete',
        label: 'ELIMINA',
        hiddenIf: 'deleted'
      },
      {
        icon: 'restore_from_trash',
        event: 'restore',
        label: 'RIPRISTINA',
        showOnlyIf: 'deleted'
      }
      ],
      span: 1
    }
  ];

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private productsService: ProductsService,
    private myextranetTableSrv: MyextTableService,
    private productRequestsSrv: ProductRequestsService
  ) { }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(queryParams => {
      this.loadData(queryParams);

    });


  }

  private loadData(queryParams: ParamMap): void {

    let PromiseArray: Promise<any>[] = [];

    if (queryParams.has('productId')) {
      this.productId = queryParams.get('productId');
      this.breadcrumbs = {
        base: '',
        entries: new Array<IBreadcrumbsEntry>(
          { label: 'MyExtranet', url: `backoffice` },
          { label: 'Prodotti attivabili', url: `backoffice/activable-products` },
          { label: `Configurazione Richieste` }
        )
      };
      PromiseArray.push(this.productsService.getProdottoAttivabileById(this.productId, false).toPromise());
    } else {
      this.product = null;
      this.productRequest = null;
      this.isLoading = false;
      this.isError = true;
      return null;
    }
    if (queryParams.has('requestId')) {
      this.requestId = parseInt(queryParams.get('requestId'), 10);
      this.isEdit = true;
      PromiseArray.push(this.productRequestsSrv.getProdRequestConfigById(queryParams.get('requestId'), false));
    }

    Promise.all(PromiseArray).then(vals => {
      if (this.isEdit && !!vals[1]) {
        this.productRequest = vals[1];
        this.productRequestBackUp = JSON.parse(JSON.stringify(vals[1]));
        this.productRequestDetails.patchValue({ ...vals[1], tipoRichiestaProdotto: vals[1].tipoRichiestaProdotto.codTipoRich });
        this.stepDataSource = Array.from(this.productRequest.step.map(val => {
          return {
            ...val,
            tableStatus: 'CARICATO',
            previousStatus: null,
            isFromDb: true,
            uuid: uuidv4()
          }
        }));
        this.stepDataSourceBackUp = JSON.parse(JSON.stringify(this.stepDataSource));

      } else if (this.isEdit) {
        this.product = null;
        this.productRequest = null;
        this.isLoading = false;
        this.isError = true;
        return null;
      }
      this.product = vals[0];
      this.breadcrumbs = {
        base: '',
        entries: new Array<IBreadcrumbsEntry>(
          { label: 'MyExtranet', url: `backoffice` },
          { label: 'Prodotti attivabili', url: `backoffice/activable-products` },
          { label: `Lista configurazioni richieste: ${this.product.nomeProdottoAttiv}`, url: `backoffice/activable-products/requests-list?productId=${this.product.idProdottoAtt}` },
          { label: `Configurazione Richiesta` }
        )
      };
      return this.productRequestsSrv.getRequestsTypes();
    }).then(requestsTypes => {

      this.tipiRichiesta = requestsTypes.records;

    }).catch(err => {
      this.product = null;
      this.productRequest = null;
      this.isLoading = false;
      this.isError = true;

    });

  }

  manageEvent(event: { element: any, eventType: string, index: number }) {
    if (!event) return null;

    this.isLoading = true;
    switch (event.eventType) {
      case 'new': {
        window.scrollTo(0, 0);
        this.isEditing = false;
        this.isInserting = true;
        this.showForm = true;
        this.productRequestStep.reset({
          numStep: this.stepDataSource.filter(a => a.numStep > 0).length + 1,
        });
        this.isLoading = false;
        break;
      }
      case 'edit': {
        this.isEditing = true;
        this.isInserting = false;
        this.showForm = true;
        let stepToEdit = event.element;
        this.productRequestStep.patchValue(stepToEdit);
        this.isLoading = false;
        break;
      }
      case 'delete': {

        this.stepDataSource[event.index] = {
          ...this.stepDataSource[event.index],
          deleted: true,
          tableStatus: 'CANCELLATO',
          previousStatus: this.stepDataSource[event.index].tableStatus,

        };

        this.deletedStepDataSource.push(JSON.parse(JSON.stringify(this.stepDataSource[event.index])));
        this.stepDataSource.splice(event.index, 1);
        this.dataTable.dataTableComponent.dataTable.renderRows();
        this.isLoading = false;
        break;


      }

      case 'move-up': {
        this.isLoading = true;
        this.stepDataSource[event.index].touched = true;
        this.stepDataSource[event.index - 1].touched = true;
        let preMoveRows = this.stepDataSource.slice(0, event.index - 1);
        let movedRows = this.stepDataSource.slice(event.index - 1, event.index + 1).reverse();
        let postMoveRows = this.stepDataSource.slice(event.index + 1);

        this.stepDataSource = preMoveRows.concat(movedRows, postMoveRows);

        this.dataTable.dataTableComponent.dataTable.renderRows();
        this.isLoading = false;
        break;
      }
      case 'move-down': {
        this.isLoading = true;
        this.stepDataSource[event.index].touched = true;
        this.stepDataSource[event.index + 1].touched = true;
        let preMoveRows = this.stepDataSource.slice(0, event.index);
        let movedRows = this.stepDataSource.slice(event.index, event.index + 2).reverse();
        let postMoveRows = this.stepDataSource.slice(event.index + 2);

        this.stepDataSource = preMoveRows.concat(movedRows, postMoveRows);


        this.isLoading = false;
        break;
      }
    }
  }

  save() {

    let stepToPush = JSON.parse(JSON.stringify(this.productRequestStep.getRawValue()));
    if (this.isEditing) {
      try {
        if (!stepToPush.uuid) {
          throw new Error('Impossibile trovare il dato da modificare');
        }
        let foundIndex = this.stepDataSource.map(v => v.uuid).indexOf(stepToPush.uuid);
        let found = this.stepDataSource[foundIndex];
        if (!found) {
          throw new Error('Impossibile trovare il dato da modificare');
        }
        stepToPush = {
          ...found,
          ...stepToPush,
          tableStatus: 'MODIFICATO',
          previousStatus: found.tableStatus !== 'MODIFICATO' ? found.tableStatus : found.previousStatus,
          touched: true

        };


        this.stepDataSource[foundIndex] = stepToPush;
        this.dataTable.dataTableComponent.dataTable.renderRows();

      } catch (err) {

      }
    }
    if (this.isInserting) {
      stepToPush = {
        ...stepToPush,
        tableStatus: 'INSERITO',
        previousStatus: null,
        isFromDb: false,
        uuid: uuidv4(),
        touched: true
      };

      this.stepDataSource.push(stepToPush);
      this.dataTable.dataTableComponent.dataTable.renderRows();
    }
    this.back();
  }

  back() {
    this.isEditing = false;
    this.isInserting = false;
    this.showForm = false;
    this.productRequestStep.reset();
  }


  postToDb() {
    this.isLoading = true;

    let stepDataSourceToBE = this.deletedStepDataSource.filter(val => {
      return !(val.tableStatus === 'CANCELLATO' && val.deleted && !val.isFromDb);
    }).concat(this.stepDataSource.map((v, i) => {
      return {
        ...v,
        numStep: i + 1
      }
    })).filter(val => val.deleted || val.touched); //rimuovo gli step non modificati/eliminati

    let prodData: { tipoRichiestaProdotto: string, flgEnabled: boolean, desProdottoProc: string, desProdottoProcEstesa: string } = this.productRequestDetails.getRawValue();
    let postObj = {
      idProdottoProc: this.requestId || null,
      prodotto: {
        idProdottoAtt: this.product.idProdottoAtt
      },
      numVersione: 1,
      tipoRichiestaProdotto: this.tipiRichiesta.find(v => v.codTipoRich === prodData.tipoRichiestaProdotto),
      desProdottoProc: prodData.desProdottoProc,
      desProdottoProcEstesa: prodData.desProdottoProcEstesa,
      flgEnabled: prodData.flgEnabled ? 1 : 0,
      operazioniStep: stepDataSourceToBE.map(val => {
        return {
          tipoOperazione: val.deleted ? 'elimina' : 'modifica',
          stepProcedimentoProdotto: {
            idStatoConf: val.isFromDb ? val.idStatoConf : null,
            numStep: val.numStep,
            codStato: val.codStato,
            desStato: val.desStato,
            competenza: val.competenza,
            flgAggiornaUtenti: val.flgAggiornaUtenti || 0,
            flgFineRich: val.flgFineRich,
            azioni: val.azioni || null,
          }
        }

      })
    };

    this.productRequestsSrv.postStepsForProductRequest(postObj).then(val => {
      this.isLoading = false;
      this.goBack();
    }).catch(err => {
      this.isLoading = false;
    });


  }

  undo(back: boolean = true) {
    this.isLoading = true;
    setTimeout(() => {
      this.stepDataSource = Array.from(this.stepDataSourceBackUp);
      this.productRequest = this.isEdit ? this.productRequestBackUp : { ...this.productRequest, flgEnabled: true };
      this.productRequestDetails.patchValue(this.productRequestBackUp);
      this.deletedStepDataSource = [];
      this.isLoading = false;
      if (back) {
        this.goBack();
      }
    }, 120);

  }

  goBack() {
    this.router.navigate(['/backoffice/activable-products/requests-list'], { queryParams: { productId: this.product.idProdottoAtt } });
  }

}
