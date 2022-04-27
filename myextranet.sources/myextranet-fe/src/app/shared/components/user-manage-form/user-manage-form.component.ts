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
import { Component, EventEmitter, Input, OnInit, Output, ViewEncapsulation } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldAppearance } from '@angular/material/form-field';
import { debounceTime, distinctUntilChanged, filter } from 'rxjs/operators';
import { Models } from 'src/app/models/model';
import { BackEndUsersService } from 'src/app/services/back-end-users.service';

@Component({
  selector: 'app-user-manage-form',
  templateUrl: './user-manage-form.component.html',
  styleUrls: ['./user-manage-form.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class UserManageFormComponent implements OnInit {

  constructor(private formBuilder: FormBuilder, private utentiService: BackEndUsersService) { }

  @Output() operationEventEmitter: EventEmitter<{ data: any, opType: string, row: string, codRuolo: string }> = new EventEmitter<{ data: any, opType: string, row: string, codRuolo: string }>();
  @Output() closeEmitter: EventEmitter<boolean> = new EventEmitter<boolean>();

  @Input() isRo = false;
  @Input() isFo = true;
  @Input() appearance: MatFormFieldAppearance = 'standard' as MatFormFieldAppearance;
  private _userDataInput: Models.utenteDTO = null;
  private _userDataConfig: { value?: Models.utenteDTO, row?: string, codRuolo: string } = null;
  private _row: string = null;
  private _role = null;
  @Input() set userDataInput(config: { value?: Models.utenteDTO, row?: string, codRuolo: string }) {
    const value: Models.utenteDTO = !!config && config.value ? config.value : null;
    if (!!value) {
      this.isEdit = true;
      this._userDataInput = value;
      this._userDataConfig = config;
      this._row = config.row;
      this._role = config.codRuolo;
      this.patchUserData(value, true);
    }
    else {
      this._role = config.codRuolo;
      this.isEdit = false;
    }

  }

  get userDataInput() {
    return this._userDataConfig;
  }


  isEdit = false;


  userid: number = null;
  public utenteArray: { idUtente: number, nome: string, cognome: string, codFiscale: string, email: string, telefono: string }[] = [];
  public utenteArrayFull: { idUtente: number, nome: string, cognome: string, codFiscale: string, email: string, telefono: string }[] = [];

  userDataForm = this.formBuilder.group({
    selectInsType: '1',
    utenteSelezionato: null,
    nome: [{ value: '', disabled: true }, [Validators.required, Validators.maxLength(150)]],
    cognome: [{ value: '', disabled: true }, [Validators.required, Validators.maxLength(150)]],
    codFiscale: [{ value: '', disabled: true }, [Validators.required, Validators.maxLength(16), Validators.pattern('^(?:[A-Z][AEIOU][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[15MR][\\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$')]],
    email: [{ value: '', disabled: this.isRo }, [Validators.required, Validators.maxLength(60), Validators.pattern('^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$')]],
    telefono: [{ value: '', disabled: this.isRo }, [Validators.required, Validators.maxLength(25)]],
  });

  emptyUserDataForm = {
    utenteSelezionato: null,
    nome: null,
    cognome: null,
    codFiscale: null,
    email: null,
    telefono: null
  };

  public ngOnInit(): void {
    this.utentiService.getAutocomplete(99999, this.isFo).then(utenti => {
      this.utenteArray = utenti;
      this.utenteArrayFull = utenti;
    });
    this.userDataForm.get('utenteSelezionato').valueChanges.pipe(debounceTime(200),
      filter(entAz => !!this.userDataForm.get('selectInsType').value && this.userDataForm.get('selectInsType').value == '1'),
      distinctUntilChanged()).subscribe(val => {
        if (val) {

          const den = !!val.codFiscale ? val.codFiscale : val;
          this.utentiService.getAutocompleteAsync(25, den, this.isFo).toPromise().then(value => {
            this.utenteArray = value;
          });
        }
      });
  }

  public showDenomination(entity: { idUtente: number, nome: string, cognome: string, codFiscale: string, email: string, telefono: string }): string {
    if (!entity || !entity.idUtente) { return null; }
    const id = entity.idUtente;
    let returnedDenom: string = null;
    if (id !== null && id !== undefined && this.utenteArray.find(utente => utente.idUtente === id)) {
      const usr: Models.utenteDTO = this.utenteArray.find(utente => utente.idUtente === id);

      this.patchUserData(usr, false);


      returnedDenom = `${usr.cognome} ${usr.nome} ${usr.codFiscale}`;
    }

    return returnedDenom;
  }

  public patchUserData(usr: Models.utenteDTO, selectInsType: boolean): void {

    let data: { utenteSelezionato?: Models.utenteDTO, nome: string, cognome: string, codFiscale: string, email: string, telefono: string, selectInsType?: string } = {

      nome: usr.nome,
      cognome: usr.cognome,
      codFiscale: usr.codFiscale,
      email: usr.email,
      telefono: usr.telefono
    };
    if (selectInsType) {
      data = {
        ...data,
        selectInsType: !!usr.idUtente ? '1' : '2',
        utenteSelezionato: !!usr.idUtente ? usr : null,
      };
    }
    this.userDataForm.patchValue(data);
    if (!!usr.idUtente) {
      this.userDataForm.get('nome').disable();
      this.userDataForm.get('cognome').disable();
      this.userDataForm.get('codFiscale').disable();
    } else {
      this.userDataForm.get('nome').enable();
      this.userDataForm.get('cognome').enable();
      this.userDataForm.get('codFiscale').enable();
    }
  }

  public manageInstTypeChange(event: { value: string }): void {
    this.utenteArray = JSON.parse(JSON.stringify(this.utenteArrayFull));
    this.userid = null;
    this.userDataForm.patchValue(this.emptyUserDataForm);
    switch (event.value) {

      case '1': {
        this.userDataForm.get('nome').disable();
        this.userDataForm.get('cognome').disable();
        this.userDataForm.get('codFiscale').disable();
        break;
      }
      case '2': {
        this.userDataForm.get('nome').enable();
        this.userDataForm.get('cognome').enable();
        this.userDataForm.get('codFiscale').enable();
        break;
      }
    }
  }

  public save(): void {
    this.operationEventEmitter.emit({ data: this.userDataForm.getRawValue(), opType: 'INS-MOD', row: this._row, codRuolo: this._role });
  }

  public back(): void {
    this.closeEmitter.emit(true);
  }


}
