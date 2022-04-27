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
import { Component, Input, Output, OnInit, ViewEncapsulation, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatFormFieldAppearance } from '@angular/material/form-field';
import { Models } from 'src/app/models/model';

@Component({
  selector: 'app-document-insert-form',
  templateUrl: './document-insert-form.component.html',
  styleUrls: ['./document-insert-form.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class DocumentInsertFormComponent implements OnInit {

  public isEdit = false;

  public docDataForm: FormGroup = this.formBuilder.group({
    fileLabel: { value: null, disabled: true },
    file: [null, Validators.required],
    nome: null
  });

  @Input() isRO: boolean = false;
  @Input() downloadId: string = null;
  @Input() isFo = true;
  @Input() appearance: MatFormFieldAppearance = 'standard' as MatFormFieldAppearance;


  @Output()
  operationEventEmitter: EventEmitter<{ data: { fileLabel: string, file: string | File, nome: string }, opType: string, row: string, changedDoc: boolean }>
    = new EventEmitter<{ data: { fileLabel: string, file: string | File, nome: string }, opType: string, row: string, changedDoc: boolean }>();

  @Output() closeEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();


  public changedDoc: boolean = false;

  private _docDataInput: Models.docInputModel = null;
  private _docDataConfig: { value?: Models.docInputModel, row?: string } = null;
  private _row: string = null;

  @Input() set docDataInput(config: { value?: Models.docInputModel, row?: string }) {
    const value: Models.docInputModel = !!config && config.value ? config.value : null;
    if (!!value) {
      this.isEdit = true;
      this._docDataInput = value;
      // usa id utente per prevalorizzare la select
      this._docDataConfig = config;
      this._row = config.row;
      this.patchDocData(value);
    }
    else {
      this.isEdit = false;
    }

  }

  get docDataInput() {
    return this._docDataConfig;
  }


  constructor(
    private formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.docDataForm.get('file').valueChanges.subscribe(v => {
      this.changedDoc = true;
      if (!!v && v.name) {
        this.docDataForm.get('fileLabel').setValue(v.name);
      }
      else {
        this.docDataForm.get('fileLabel').setValue(null);
      }
    });
  }

  back() {
    this.closeEmitter.emit(true);
  }

  save() {
    this.operationEventEmitter.emit({ data: this.docDataForm.value, row: this.isEdit ? this._row : null, opType: 'INS-MOD', changedDoc: this.changedDoc });
    this.docDataForm.reset();
  }

  patchDocData(doc: Models.docInputModel) {

    this.docDataForm.patchValue(doc);

  }


}
