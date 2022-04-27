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
import { Component, Input, OnInit, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatFormFieldAppearance } from '@angular/material/form-field';
import { Models } from 'src/app/models/model';

@Component({
  selector: 'app-generic-user-form',
  templateUrl: './generic-user-form.component.html',
  styleUrls: ['./generic-user-form.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class GenericUserFormComponent implements OnInit {

  public renderMode: number;
  public isRo: boolean = false;
  public appearance: MatFormFieldAppearance = 'standard' as MatFormFieldAppearance;
  private _settings: Models.GenericUserFormSetting;

  public userDataForm: FormGroup = null;
  public entiArray: any[] = [];

  @Input() set settings(set: Models.GenericUserFormSetting) {
    if (!set || !set.enteAziendaRender) {

    } else {
      this.renderMode = set.enteAziendaRender;
      this.isRo = !!set.ro;
      this.appearance = set.appearance as MatFormFieldAppearance || 'standard' as MatFormFieldAppearance;
      this.entiArray = set.enteAziendaArray || [];
      switch (this.renderMode) {
        case 1: {

          break;
        }
        case 2: {
          this.userDataForm = this.formBuilder.group({
            nome: [{ value: set.data['nome'], disabled: this.isRo }, set.validators['nome'] || null],
            cognome: [{ value: set.data['cognome'], disabled: this.isRo }, set.validators['cognome'] || null],
            codFiscale: [{ value: set.data['codFiscale'], disabled: this.isRo }, set.validators['codFiscale'] || null],
            email: [{ value: set.data['email'], disabled: this.isRo }, set.validators['email'] || null],
            enteAzienda: [{ value: set.data['enteAzienda'], disabled: this.isRo }, set.validators['enteAzienda'] || null]
          });

          break;
        }
        case 3: {

          break;
        }
      }
    }
  }

  get showForm(): boolean {
    return !!this.userDataForm;
  }



  constructor(
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit(): void {
   
  }


  showDenomination(id: number) {

    return id !== null && id !== undefined && this.entiArray.find(ente => ente.idEnte === id) ? this.entiArray.find(ente => ente.idEnte === id).denominazione : null;
  }



}
