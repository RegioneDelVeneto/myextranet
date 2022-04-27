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
import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { Models } from 'src/app/models/model';
import { CardTableContainerComponent } from '../data-table/card-table-container/card-table-container.component';
import { v4 as uuidv4 } from 'uuid';

import { MyboxService } from 'src/app/services/mybox.service';

@Component({
  selector: 'app-product-request-documents-table',
  templateUrl: './product-request-documents-table.component.html',
  styleUrls: ['./product-request-documents-table.component.scss']
})
export class ProductRequestDocumentsTableComponent implements OnInit {

  public headerFO: Models.TableHeader[] = [];
  public footer: {
    active: boolean,
    buttons: {
      label: string,
      event: string,
      icon: string,
      color?: string
    }[]
  } = null;

  public headerBO: Models.TableHeader[] = [];

  @Input() isFo = true;

  private _documentOperations: 0 | 1 | 2 = 0;

  @Input()
  set documentOperations(value: 0 | 1 | 2) {
    if (value === null || value === undefined) {
      value = 0;
    }
    switch (value) {

      case 0: {
        this.headerFO = this.docTableHeaderRO;
        this.headerBO = this.docTableHeaderRO;
        this.footer = this.docFooterRO;
        break;
      }

      case 1: {
        this.headerFO = this.docTableHeaderFO;
        this.headerBO = this.docTableHeaderBO;
        this.footer = this.docFooter;
        break;
      }
      case 2: {
        this.headerFO = this.docTableHeaderFOOnlyAdd;
        this.headerBO = this.docTableHeaderBOOnlyAdd;
        this.footer = this.docFooter;
        break;
      }
      default: {
        this.headerFO = this.docTableHeaderRO;
        this.headerBO = this.docTableHeaderRO;
        this.footer = this.docFooterRO;
        break;
      }
    }
    this._documentOperations = value;
  }

  get documentOperations(): 0 | 1 | 2 {
    return this._documentOperations;
  }

  @Output() showSaveBar: EventEmitter<boolean> = new EventEmitter<boolean>();

  public docDataInput: { value?: Models.docInputModel, row?: string } = null;

  public showDocForm = false;
  public downloadId: string = null;

  private _documentDs: Models.DocumentoRichiestaProdottoModelDtoExtended[] = [];
  @Input()
  set documentsDs(value: Models.DocumentoRichiestaProdottoModelDtoExtended[]) {
    this._documentDs = value.map(v => {
      return {
        ...v,
        label :  v?.label ?? v?.metadata?.fileName ?? null,
        uuid: uuidv4(),
        deleted: false,
        touched: false
      };
    });
  }

  get documentsDs(): Models.DocumentoRichiestaProdottoModelDtoExtended[] {
    return this._documentDs;
  }

  public docTableHeaderFO: Models.TableHeader[] = [
    {
      definition: 'nomeDocumento',
      header: 'Descrizione del documento',
      span: 6
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'buttons',
      actions: [{
        icon: null,
        event: 'editDoc',
        label: 'MODIFICA',
        color: this.isFo ? 'accent' : 'primary',
        hiddenIf: 'deleted'
      },
      {
        icon: null,
        event: 'deleteDoc',
        label: 'ELIMINA',
        color: this.isFo ? 'accent' : 'primary',
        hiddenIf: 'deleted'
      },
      {
        icon: 'get_app',
        event: 'downloadDoc',
        label: null,
        legend: 'scaricare documento',
        color: this.isFo ? 'accent' : 'primary',
        hiddenIf: 'deleted'
      },
      {
        icon: null,
        event: 'restoreDoc',
        label: 'RIPRISTINA',
        color: this.isFo ? 'accent' : 'primary',
        showOnlyIf: 'deleted'
      }
      ],
      span: 4
    }
  ];

  public docTableHeaderBO: Models.TableHeader[] = [
    {
      definition: 'nomeDocumento',
      header: 'Descrizione del documento',
      span: 9
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [{
        icon: 'edit',
        event: 'editDoc',
        label: 'Modifica',
        hiddenIf: 'deleted'
      },
      {
        icon: 'delete',
        event: 'deleteDoc',
        label: 'Elimina',
        hiddenIf: '$or:[deleted,idProdRichDoc]'
      },
      {
        icon: 'get_app',
        event: 'downloadDoc',
        label: "Scarica documento",
        hiddenIf: 'deleted'
      },
      {
        icon: 'restore_from_trash',
        event: 'restoreDoc',
        label: 'Ripristina',
        showOnlyIf: 'deleted'
      }
      ],
      span: 1
    }
  ];

  public docTableHeaderFOOnlyAdd: Models.TableHeader[] = [
    {
      definition: 'nomeDocumento',
      header: 'Descrizione del documento',
      span: 6
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'buttons',
      actions: [{
        icon: null,
        event: 'editDoc',
        label: 'MODIFICA',
        color: this.isFo ? 'accent' : 'primary',
        hiddenIf: 'deleted'
      },

      {
        icon: 'get_app',
        event: 'downloadDoc',
        label: null,
        legend: 'scaricare documento',
        color: this.isFo ? 'accent' : 'primary',
        hiddenIf: 'deleted'
      },

      ],
      span: 4
    }
  ];

  public docTableHeaderBOOnlyAdd: Models.TableHeader[] = [
    {
      definition: 'nomeDocumento',
      header: 'Descrizione del documento',
      span: 9
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'actions',
      actions: [{
        icon: 'edit',
        event: 'editDoc',
        label: 'Modifica',
        hiddenIf: 'deleted'
      },

      {
        icon: 'get_app',
        event: 'downloadDoc',
        label: "Scarica documento",
        hiddenIf: 'deleted'
      },

      ],
      span: 1
    }
  ];

  public docTableHeaderRO: Models.TableHeader[] = [
    {
      definition: 'nomeDocumento',
      header: 'Descrizione del documento',
      span: 8
    },
    {
      definition: 'action',
      header: 'Azioni',
      type: 'buttons',
      actions: [
        {
          icon: 'get_app',
          event: 'downloadDoc',
          label: null,
          legend: 'scaricare documento',
          color: this.isFo ? 'accent' : 'primary',
          hiddenIf: 'deleted'
        }
      ],
      span: 2
    }
  ];



  public docFooter: {
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
          label: 'AGGIUNGI DOCUMENTO',
          icon: 'add',
          event: 'newDoc',
          color: this.isFo ? 'accent' : 'primary'
        }
      ]
    };

  public docFooterRO: {
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

      ]
    };

  @ViewChild('docTable') docDataTable: CardTableContainerComponent;

  constructor(private myBoxSrv: MyboxService) { }

  ngOnInit(): void {
  }

  manageDocOperation(formEvent: { data: { fileLabel: string, file: string | File, nome: string }, opType: string, row: string, changedDoc: boolean }) {
    if (!formEvent) {
      return null;
    }

    if (!!formEvent.row) {
      let docIdx = this.documentsDs.map(v => v.uuid).indexOf(formEvent.row);
      if (docIdx === -1) {
        throw new Error('Impossibile trovare il documento da modificare');

      }
      this.documentsDs[docIdx] = {
        ...this.documentsDs[docIdx],
        nomeDocumento: formEvent.data.nome,
        file: formEvent.data.file,
        label: formEvent.data.fileLabel,
        touched: true,
        deleted: false,
        idDocumento: formEvent.changedDoc ? null : this.documentsDs[docIdx].idDocumento
      };

    } else if (!!formEvent.data) {
      this.documentsDs.push({
        nomeDocumento: formEvent.data.nome,
        file: formEvent.data.file,
        label: formEvent.data.fileLabel,
        flgEnabled: null,
        idProdRichDoc: null,
        richiestaProdotto: null,
        stepRichiestaProdotto: null,
        uuid: uuidv4(),
        touched: false,
        deleted: false,
        idDocumento: null
      });
    }
    this.downloadId = null;
    this.showDocForm = false;
    this.showSaveBar.emit(true);
    this.docDataTable.dataTableComponent.dataTable.renderRows();

  }

  docManageEvent(event: { eventType: string, index: number, element: Models.DocumentoRichiestaProdottoModelDtoExtended }) {
    if (!event || !event.eventType) {
      return null;
    }
    switch (event.eventType) {
      case 'newDoc': {
        this.docDataInput = null;
        this.downloadId = null;
        this.showDocForm = true;
        this.showSaveBar.emit(false);
        break;
      }
      case 'editDoc': {
        this.docDataInput = {
          value: {
            file: event.element.file ? event.element.file : event.element.idDocumento,
            fileLabel: event.element.label,
            nome: event.element.nomeDocumento,
            deleted: event.element.deleted,
            touched: event.element.touched,
            uuid: event.element.uuid,
          },
          row: event.element.uuid
        };
        this.downloadId = this.myBoxSrv.getDownloadLinkFromId(event.element.idDocumento, this.isFo);
        this.showDocForm = true;

        this.showSaveBar.emit(false);
        break;
      }
      case 'deleteDoc': {
        this.documentsDs[event.index] = { ...this.documentsDs[event.index], deleted: true };
        this.docDataTable.dataTableComponent.dataTable.renderRows();
        break;
      }
      case 'restoreDoc': {
        this.documentsDs[event.index] = { ...this.documentsDs[event.index], deleted: false };
        this.docDataTable.dataTableComponent.dataTable.renderRows();
        break;
      }
      case 'downloadDoc': {
        if (!!event && !!event.element && !!event.element.file && !event.element.idDocumento) {
          window.open(URL.createObjectURL(event.element.file));
        } else if (!!event && !!event.element && !!event.element.idDocumento) {
          window.open(this.myBoxSrv.getDownloadLinkFromId(event.element.idDocumento));
        }

        break;
      }
    }
  }
}
