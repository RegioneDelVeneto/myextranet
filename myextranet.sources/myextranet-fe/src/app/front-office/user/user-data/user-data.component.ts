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
import { FormBuilder, FormGroup, ValidationErrors, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Models } from 'src/app/models/model';
import { BackEndUsersService } from 'src/app/services/back-end-users.service';
import { EnteService } from 'src/app/services/ente.service';
import { UserService } from 'src/app/services/user.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-user-data',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './user-data.component.html',
  styleUrls: ['./user-data.component.scss']
})
export class UserDataComponent implements OnInit {


  public userInfo: Models.UserInfo = null;

  public userData: Models.utenteDTO = null;

  public entiArray: Models.enteDTO[] = [];

  public isLoading;

  public incompleteData = false;

  public hideIncompleteDataError = false;

  public callbackurl: string = '/home';

  public radio = ['Ente', 'Azienda'];

  public denominazioneInput = '';
  public isError = false;
  public isSuccess = false;
  public sucessMsg = `le modifiche potrebbero non essere immediatamente visibili nell'area privata`;
  public errorMsg: string = null;

  constructor(
    private formBuilder: FormBuilder,
    private userService: UserService,
    private beUserSrv: BackEndUsersService,
    private enteService: EnteService,
    private router: Router,
    private route: ActivatedRoute,
  ) {

  }



  userDataForm: FormGroup = null;

  public defError = `Si sono riscontrati degli errori durante l'esecuzione dell'operazione. Riprovare più tardi`;

  breadcrumbs = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca', url: '/utente/bacheca' },
      { label: `Dati utente` },
    )
  };

  date: Date = new Date();
  ngOnInit(): void {
    this.isLoading = true;
    this.userDataForm = this.formBuilder.group({
      nome: ['', [Validators.required, Validators.maxLength(150)]],
      cognome: ['', [Validators.required, Validators.maxLength(150)]],
      email: ['', [Validators.required, Validators.maxLength(60), Validators.pattern('^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$')]],
      telefono: ['', Validators.maxLength(25)],
      telefonoUff: ['', Validators.maxLength(25)],
      radio: [''],
      ente: ['', Validators.required],
      azienda: ['', [Validators.maxLength(250), Validators.required]],
      partitaIva: ['', [Validators.minLength(11), Validators.maxLength(11), Validators.pattern('^[0-9]*$'), Validators.required]],
      codFiscale: ['', [Validators.required, Validators.maxLength(16), Validators.pattern('^(?:[A-Z][AEIOU][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[15MR][\\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$')]],
    }, { validators: this.atLeastOneRequired(['telefono', 'telefonoUff']) });

    this.route.queryParamMap.subscribe(params => {
      this.callbackurl = decodeURIComponent(params.get('callbackUrl'));
      this.loadUserData();

    });

  }

  sumbmit() {
    this.isSuccess = false;
    this.isError = false;
    const newUserDTO: Models.utenteDTO = {
      ...this.userData,
      nome: this.userDataForm.value.nome,
      cognome: this.userDataForm.value.cognome,
      email: this.userDataForm.value.email,
      codFiscale: this.userInfo.codFiscale,
      ente: this.userDataForm.value.ente && this.userDataForm.value.ente.idEnte ? {
        idEnte: this.userDataForm.value.ente.idEnte
      } : null,
      azienda: this.userDataForm.value.azienda,
      partitaIva: this.userDataForm.value.partitaIva,
      telefono: this.userDataForm.value.telefono,
      telefonoUff: this.userDataForm.value.telefonoUff
    };
    let savePromise: Promise<any> = null;
    if (this.userInfo.userId !== null && this.userInfo.userId !== undefined) {
      newUserDTO.idUtente = this.userInfo.userId;
      savePromise = this.beUserSrv.saveEditUser(newUserDTO);
    }
    else {
      savePromise = this.beUserSrv.saveNewUser(newUserDTO);
    }
    savePromise.then(esito => {
      this.isLoading = true;
      this.userService.updateUserInfo().then(uInfo => {
        this.isSuccess = true;
        this.userInfo = uInfo;
        if (this.callbackurl && this.callbackurl != 'null') {
          setTimeout(() => {
            this.router.navigateByUrl(this.callbackurl);
          }, 500);

        } else {
          this.loadUserData();
        }

      });
    }).catch(err => {
      this.isError = true;
      if (err.error && err.error.data && !!err.error.code && (err.error.code == 'VALIDATION_ERROR')) {
        this.errorMsg = err.error.data;
      } else {
        this.errorMsg = this.defError;
      }
    });

  }


  undo(): void {
    this.userDataForm = this.formBuilder.group({
      nome: [this.userData.nome, [Validators.required, Validators.maxLength(150)]],
      cognome: [this.userData.cognome, [Validators.required, Validators.maxLength(150)]],
      email: [this.userData.email, [Validators.required, Validators.maxLength(60), Validators.pattern('^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$')]],
      telefono: [this.userData.telefono, Validators.maxLength(25)],
      telefonoUff: [this.userData.telefonoUff, Validators.maxLength(25)],
      radio: [''],
      ente: [this.userData.ente && !!this.userData.ente.idEnte ? { idEnte: this.userData.ente.idEnte, denominazione: this.userData.ente.denominazione } : null, Validators.required],
      azienda: [this.userData.azienda, [Validators.maxLength(250), Validators.required]],
      partitaIva: [this.userData.partitaIva, [Validators.minLength(11), Validators.maxLength(11), Validators.pattern('^[0-9]*$'), Validators.required]],
      codFiscale: [this.userData.codFiscale, [Validators.required, Validators.maxLength(16), Validators.pattern('^(?:[A-Z][AEIOU][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[15MR][\\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$')]],
    }, { validators: [this.atLeastOneRequired(['telefono', 'telefonoUff'])] });
  }

  showDenomination(entity: { idEnte: number, denominazione: string }) {
    if (!entity || !entity.idEnte) return null;
    const id = entity.idEnte;
    return id !== null && id !== undefined ? entity.denominazione : null;
  }


  setDenominationInput(event: any) {


    of(event.target.value).pipe(debounceTime(20), distinctUntilChanged()).subscribe(val => {
      const den = !!val && !!val.denominazione ? val.denominazione : val;
      this.enteService.getAutocompleteAsync(25, den).toPromise().then(value => {
        this.entiArray = value;
      });
    });
  }

  asyncEnte() {
    return this.enteService.getAutocompleteAsync(25, this.denominazioneInput);
  }

  private atLeastOneRequired = (controls: string[] = null) => (
    group: FormGroup,
  ): ValidationErrors | null => {
    if (!controls) {
      return null;
    }

    const hasAtLeastOne = group && group.controls && controls
      .some(k => !!group.controls[k].value);


    controls
      .forEach(k => {
        if (!group.controls[k].value && !hasAtLeastOne) {
          group.controls[k].setErrors({ required: true });
        } else {
          const errors = group.controls[k].errors;
          if (!!errors && Object.keys(errors).length && typeof (errors) !== 'string') {
            let errObj = {};
            Object.keys(errors).forEach(key => {
              if (key !== 'required') {
                errObj[key] = errors[key];
              }
              group.controls[k].setErrors(Object.keys(errObj).length > 0 ? errObj : null);

            })

          }
        }
      });

    return hasAtLeastOne ? null : {
      atLeastOneRequired: true,
    };
  }

  private notBoth = (controls: string[] = null) => (
    group: FormGroup,
  ): ValidationErrors | null => {
    if (!controls) {
      return null;
    }

    const both = group && group.controls && controls
      .every(k => !!group.controls[k].value);


    controls
      .forEach(k => {
        if (!!group.controls[k].value && both) {
          group.controls[k].setErrors({ required: true });
        } else {
          const errors = group.controls[k].errors;
          if (!!errors && Object.keys(errors).length && typeof (errors) !== 'string') {
            let errObj = {};
            Object.keys(errors).forEach(key => {
              if (key !== 'required') {
                errObj[key] = errors[key];
              }
              group.controls[k].setErrors(Object.keys(errObj).length > 0 ? errObj : null);

            })

          }
        }
      });

    return !both ? null : {
      notBoth: true,
    };
  }

  private loadUserData() {
    this.userService.getUserInfo().then(val => {
      this.userInfo = val;
      if (this.callbackurl && this.callbackurl !== "null" && this.callbackurl.length > 0 && this.userInfo.tenantExpired) this.incompleteData = true;
      if (this.userInfo.userId) {
        this.beUserSrv.getUserById(this.userInfo.userId).then(userDto => {
          this.userData = userDto;

          this.denominazioneInput = this.userData.ente && this.userData.ente.denominazione ? this.userData.ente.denominazione : null;
          this.asyncEnte().toPromise().then(enti => {

            this.userDataForm = this.formBuilder.group({
              nome: [this.userData.nome, [Validators.required, Validators.maxLength(150)]],
              cognome: [this.userData.cognome, [Validators.required, Validators.maxLength(150)]],
              email: [this.userData.email, [Validators.required, Validators.maxLength(60), Validators.pattern('^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$')]],
              telefono: [this.userData.telefono, Validators.maxLength(25)],
              telefonoUff: [this.userData.telefonoUff, Validators.maxLength(25)],
              radio: [this.userData.azienda ? 'Azienda' : 'Ente'],
              ente: [this.userData.ente && !!this.userData.ente.idEnte ? { idEnte: this.userData.ente.idEnte, denominazione: this.userData.ente.denominazione } : null, Validators.required],
              azienda: [this.userData.azienda, [Validators.maxLength(250), Validators.required]],
              partitaIva: [this.userData.partitaIva, [Validators.minLength(11), Validators.maxLength(11), Validators.pattern('^[0-9]*$'), Validators.required]],
              codFiscale: [this.userData.codFiscale, [Validators.required, Validators.maxLength(16), Validators.pattern('^(?:[A-Z][AEIOU][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[15MR][\\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$')]],
            }, { validators: [this.atLeastOneRequired(['telefono', 'telefonoUff'])] });

            this.checkEnte(this.userDataForm.value.radio);
            return Promise.resolve(enti);

          }).then((enti) => {

            this.entiArray = enti;
            this.userDataForm.get('ente').valueChanges.pipe(debounceTime(200), distinctUntilChanged()).subscribe(val => {
              if (val) {
                const den = !!val.denominazione ? val.denominazione : val;
                this.enteService.getAutocompleteAsync(25, den).toPromise().then(value => {
                  this.entiArray = value;
                });
              }
            });

            setTimeout(() => {
              this.userDataForm.updateValueAndValidity();
              this.isLoading = false;
            }, 100);
          });
          
        }).catch(err => {
          this.isError = true;
          this.isLoading = false;
        });
      } else {
        this.userData = { nome: val.givenName, cognome: val.familyName, email: val.email, telefono: null, telefonoUff: null, ente: null, idUtente: null, codFiscale: val.codFiscale, azienda: null, partitaIva: null };

        this.denominazioneInput = this.userData.ente && this.userData.ente.denominazione ? this.userData.ente.denominazione : null;
        this.asyncEnte().toPromise().then(enti => {


          this.userDataForm = this.formBuilder.group({
            nome: [this.userData.nome, [Validators.required, Validators.maxLength(150)]],
            cognome: [this.userData.cognome, [Validators.required, Validators.maxLength(150)]],
            email: [this.userData.email, [Validators.required, Validators.maxLength(60), Validators.pattern('^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$')]],
            telefono: [this.userData.telefono, Validators.maxLength(25)],
            telefonoUff: [this.userData.telefonoUff, Validators.maxLength(25)],
            radio: [''],
            ente: ['', Validators.required],
            azienda: [this.userData.azienda, [Validators.maxLength(250), Validators.required]],
            partitaIva: [this.userData.partitaIva, [Validators.minLength(11), Validators.maxLength(11), Validators.pattern('^[0-9]*$'), Validators.required]],
            codFiscale: [this.userData.codFiscale, [Validators.required, Validators.maxLength(16), Validators.pattern('^(?:[A-Z][AEIOU][AEIOUX]|[B-DF-HJ-NP-TV-Z]{2}[A-Z]){2}(?:[\\dLMNP-V]{2}(?:[A-EHLMPR-T](?:[04LQ][1-9MNP-V]|[15MR][\\dLMNP-V]|[26NS][0-8LMNP-U])|[DHPS][37PT][0L]|[ACELMRT][37PT][01LM]|[AC-EHLMPR-T][26NS][9V])|(?:[02468LNQSU][048LQU]|[13579MPRTV][26NS])B[26NS][9V])(?:[A-MZ][1-9MNP-V][\\dLMNP-V]{2}|[A-M][0L](?:[1-9MNP-V][\\dLMNP-V]|[0L][1-9MNP-V]))[A-Z]$')]],
          }, { validators: [this.atLeastOneRequired(['telefono', 'telefonoUff'])] });
          return Promise.resolve(enti);

        }).then((enti) => {

          this.entiArray = enti;
          this.userDataForm.get('ente').valueChanges.pipe(debounceTime(200), distinctUntilChanged()).subscribe(val => {
            const den = !!val.denominazione ? val.denominazione : val;
            this.enteService.getAutocompleteAsync(25, den).toPromise().then(value => {
              this.entiArray = value;
            });
          });
          this.isLoading = false;
        });


      }

    });
  }

  public goBack(): void {
    this.router.navigateByUrl(this.callbackurl);
  }

  radioButtonChanged($event) {
    let radioValue = event.target['value'];
    this.checkEnte(radioValue);
  }

  private checkEnte(radioValue: any) {
    if (radioValue === 'Ente') {
      this.userDataForm.get('azienda').setValue(null);
      this.userDataForm.get('partitaIva').setValue(null);
      this.userDataForm.get('azienda').disable();
      this.userDataForm.get('partitaIva').disable();

      this.userDataForm.get('ente').enable();
    } else {
      this.userDataForm.get('azienda').enable();
      this.userDataForm.get('partitaIva').enable();

      this.userDataForm.get('ente').setValue(null);
      this.userDataForm.get('ente').disable();
    }
  }
}
