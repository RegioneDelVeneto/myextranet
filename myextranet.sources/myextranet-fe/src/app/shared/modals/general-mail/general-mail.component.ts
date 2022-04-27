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
import { Component, Inject, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataTableService, EngDynamicFormsComponent, IForm } from '@eng/morfeo';

@Component({
  selector: 'app-general-mail',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './general-mail.component.html',
  styleUrls: ['./general-mail.component.scss'],
})
export class GeneralMailComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<GeneralMailComponent>,
    private tableSrv: DataTableService,
    @Inject(MAT_DIALOG_DATA) public data: { title: string, description: string, filter?: { [key: string]: string }, checkbox?: { atLeastOne: boolean, keys: { key: string, label: string }[] } }) { }


  @ViewChild('generalModaldata') geenralModalFormData: EngDynamicFormsComponent;
  public mailForm: IForm = {
    components: [
      {
        type: 'textfield',
        label: 'Oggetto',
        key: 'oggetto',
        suffix: '',
        hidden: false,
        defaultValue: null,
        validate: {
          custom: '',
          required: true
        },
        input: true,
        data: {}
      },
      {
        type: 'textarea',
        label: 'Messaggio',
        key: 'messaggio',
        suffix: '',
        hidden: false,
        defaultValue: null,
        validate: {
          custom: '',
          required: true
        },
        input: true,
        data: {}
      }
    ]
  };

  public filters: string[] = [];

  public checkBoxForm: FormGroup = null;

  get checkboxValid(): boolean {
    if (!this.data.checkbox) {
      return true;
    } else if (this.data.checkbox.atLeastOne && !!this.checkBoxForm) {
      let returnedValue = false;
      Object.keys(this.checkBoxForm.value).forEach(key => {
        if (!!this.checkBoxForm.get(key).value) {
          returnedValue = true;
        }

      });
      return returnedValue;
    }
    return true;
  }

  ngOnInit(): void {
    if (!this.data.filter) {
      return null;
    }
    Object.keys(this.data.filter).forEach(key => {
      if (!!this.data.filter[key]) {
        this.filters.push(this.getFilter(key, this.data.filter[key]));
      }
    });
    if (!!this.data.checkbox) {
      this.checkBoxForm = new FormGroup({});
      this.data.checkbox.keys.forEach(val => {
        const fc: FormControl = new FormControl(false);
        this.checkBoxForm.addControl(val.key, fc);
      });
    }
  }

  public getFilter(key: string, data: string): string {
    return [key, data].join(' : ');
  }

  public saveData(): void {
    if (!!this.data.checkbox) {
      this.dialogRef.close({ ...this.geenralModalFormData.f.value, ...this.checkBoxForm.value });
    }
    else {
      this.dialogRef.close(this.geenralModalFormData.f.value);
    }

  }

}
