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
import { Component, ElementRef, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { Models } from 'src/app/models/model';
import { ComuneService } from 'src/app/services/comune.service';
import { EnteService } from 'src/app/services/ente.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-tenant-detail-fo',
  templateUrl: './tenant-detail-fo.component.html',
  styleUrls: ['./tenant-detail-fo.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class TenantDetailFoComponent implements OnInit {

  @ViewChild('UploadFileInput') uploadFileInput: ElementRef;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private enteService: EnteService,
    private comuneService: ComuneService
  ) { }


  public isRo = false;
  public isEdit = false;
  public disabledSend = false;

  public breadcrumbs = null;

  public idEnte = null;

  public comuniArray: Models.comuneDTO[] = [];
  public denominazioneInput = '';
  private comuneCodice = '';
  public isLoading = true;

  public tenant: Models.enteDTO;
  public categorieEnte: Models.CategoriaEnteDTO[] = [];
  public downloadLink: string;

  public tenantDataForm: FormGroup = null;

  public imgSrc: ArrayBuffer | string = null;

  ngOnInit(): void {
    this.enteService.getCategoriaEnteList(true).then(val => {
      this.categorieEnte = val || [];
    }).catch(err => console.error(`Error during category retrieval : ${err}`));
    this.route.queryParams.subscribe(params => {
      if (params.idEnte !== null && params.idEnte !== undefined) {
        this.idEnte = params.idEnte;
        this.isEdit = true;

        this.enteService.getEnteById(this.idEnte, true).subscribe((ente: Models.enteDTO) => {
          this.tenant = ente;

          this.comuneCodice = ente.comune && ente.comune.codComune ? `${ente.comune.codComune}` : null;
          this.denominazioneInput = ente.denominazione;

          this.asyncComune().toPromise().then(comuni => {
            if (!!ente.logo) {
              this.downloadLink = this.enteService.downloadLogo(ente.logo, true);
              this.imgSrc = this.enteService.downloadLogo(ente.logo, true);
            }
            else {
              this.downloadLink = null;
            }
            this.tenantDataForm = this.fb.group({
              fileNameRO: [{ value: ente.logoFileMetadata && ente.logoFileMetadata.fileName ? ente.logoFileMetadata.fileName : null, disabled: true }],
              codIpa: [{ value: ente.codIpa, disabled: this.isRo }, [Validators.required, Validators.maxLength(15)]],
              categoria: [{ value: ente.categoria && ente.categoria.idCategoria ? ente.categoria.idCategoria : null, disabled: this.isRo }, [Validators.required]],
              denominazione: [{ value: ente.denominazione, disabled: this.isRo }, [Validators.required, Validators.maxLength(250)]],
              indirizzo: [{ value: ente.indirizzo, disabled: this.isRo }, [Validators.required, Validators.maxLength(500)]],
              codComune: [{ value: ente.comune, disabled: this.isRo }, [Validators.required, Validators.maxLength(10)]],
              cap: [{ value: ente.cap, disabled: this.isRo }, [Validators.required, Validators.minLength(5), Validators.maxLength(10)]],
              email: [{ value: ente.email, disabled: this.isRo }, [Validators.required, Validators.email, Validators.maxLength(100)]],
              pec: [{ value: ente.pec, disabled: this.isRo }, [Validators.required, Validators.email, Validators.maxLength(100), Validators.pattern('^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$')]],
              codFiscale: [{ value: ente.codFiscale, disabled: this.isRo }, [Validators.required, Validators.maxLength(16)]],
              telefono: [{ value: ente.telefono, disabled: this.isRo }, Validators.maxLength(30)],
              fax: [{ value: ente.fax, disabled: this.isRo }, Validators.maxLength(30)],
              urlWebSite: [{ value: ente.urlWebSite, disabled: this.isRo }, Validators.maxLength(500)],
              urlFacebook: [{ value: ente.urlFacebook, disabled: this.isRo }, Validators.maxLength(250)],
              urlInstagram: [{ value: ente.urlInstagram, disabled: this.isRo }, Validators.maxLength(250)],
              urlTwitter: [{ value: ente.urlTwitter, disabled: this.isRo }, Validators.maxLength(250)],
              fileLogoName: [{ value: ente.logo, disabled: this.isRo }, Validators.required],
              dtInizioVal: [
                {
                  value: ente.dtInizioValLD ?
                    new Date(ente.dtInizioValLD as Date)
                    : (ente.dtInizioVal ? this.manageDateZones(new Date(ente.dtInizioVal)) : null),
                  disabled: true
                }],
              dtFineVal: [!!ente.dtFineValLD ?
                new Date(ente.dtFineValLD as Date)
                : (ente.dtFineVal ? this.manageDateZones(new Date(ente.dtFineVal)) : null)
              ]

            });
            return Promise.resolve(comuni);
          }).then((comuni) => {
            this.comuniArray = comuni;
            this.tenantDataForm.get('codComune').valueChanges.pipe(debounceTime(200), distinctUntilChanged()).subscribe(val => {
              if (!!val.desComune) {
                val = val.desComune;
              }
              this.comuneService.getAutocompleteAsync(25, val, true).toPromise().then(value => {
                this.comuniArray = value;
              });
            });
            this.tenantDataForm.get('fileLogoName').valueChanges.subscribe(v => {
              if (!!v && v.name) {
                this.tenantDataForm.get('fileNameRO').setValue(v.name);
              }
              else this.tenantDataForm.get('fileNameRO').setValue(null);
            });

            this.isLoading = false;
          });



          this.breadcrumbs = {
            base: '',
            entries: new Array<IBreadcrumbsEntry>(
              { label: 'Bacheca', url: '/utente/bacheca' },
              { label: 'I miei Prodotti', url: '/utente/prodotti' },
              { label: 'Modifica Ente' }
            )
          };
        });

      } else {
        this.router.navigate([`utente/prodotti`]);
      }
    });


  }

  public sendData(): void {
    if (!this.isEdit) {
      return null;
    }
    let enteDTO: Models.enteDTO = {
      ...this.tenant,
      idEnte: this.idEnte,
      codIpa: this.tenantDataForm.value.codIpa,
      denominazione: this.tenantDataForm.value.denominazione,
      indirizzo: this.tenantDataForm.value.indirizzo,
      comune: this.tenantDataForm.value.codComune,
      cap: this.tenantDataForm.value.cap,
      email: this.tenantDataForm.value.email,
      pec: this.tenantDataForm.value.pec,
      codFiscale: this.tenantDataForm.value.codFiscale,
      telefono: this.tenantDataForm.value.telefono,
      fax: this.tenantDataForm.value.fax,
      urlWebSite: this.tenantDataForm.value.urlWebSite,
      urlFacebook: this.tenantDataForm.value.urlFacebook,
      urlInstagram: this.tenantDataForm.value.urlInstagram,
      urlTwitter: this.tenantDataForm.value.urlTwitter
    };
    if (!!this.tenantDataForm.value.categoria) {
      enteDTO.categoria = this.categorieEnte.find(v => v.idCategoria === this.tenantDataForm.value.categoria);
    }
    let savePromise: Promise<any> = null;
    if (this.isEdit) {
      enteDTO = {
        ...enteDTO,
        dtFineVal: this.manageDateZones(new Date(this.tenantDataForm.value.dtFineVal), false).valueOf()
      };
      savePromise = this.enteService.putEnte(this.enteDtoKeyFilter(enteDTO), this.tenantDataForm.value.fileLogoName, true);
    } else {
      savePromise = Promise.resolve(null);
    }
    savePromise.then(esito => {
      this.router.navigate([`utente/prodotti`]);
    });
  }


  public showDenomination(comune: { codComune: number, desComune: string, provincia: { codProvincia: string } }): string {

    if (comune === null || comune === undefined) {
      return null;
    }

    return comune?.desComune && comune?.provincia?.codProvincia ?
      `${comune.desComune} (${comune.provincia.codProvincia})`
      : comune?.desComune ? `${comune.desComune}` : null;

  }

  private asyncComune(): Observable<Models.comuneDTO[]> {
    return this.comuneService.getAutocompleteAsyncByCode(25, this.comuneCodice, true);
  }

  private manageDateZones(data: Date, ingresso: boolean = true): Date {
    let sign = 1;
    if (ingresso) {
      sign = -1;
    }
    let parsedDate = new Date(data);
    let adjustedDate = new Date(parsedDate.valueOf() - (sign) * parsedDate.getTimezoneOffset() * 60000);
    return adjustedDate
  }

  public undo(): void {
    this.router.navigate([`utente/prodotti`]);
  }

  public updateFileSrc(event: any): void {
    this.imgSrc = event;
  }

  private enteDtoKeyFilter(enteDTO: Models.enteDTO): Models.enteDTO {
    const tenant: any = {};
    Object.keys(enteDTO).filter(k => k !== 'dtFineValLD' && k !== 'dtInizioValLD').forEach(key => {
      tenant[key] = enteDTO[key];
    });
    return tenant as Models.enteDTO;
  }

}
