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
import { Models } from 'src/app/models/model';
import { EnteService } from 'src/app/services/ente.service';
import { ProductsService } from 'src/app/services/products.service';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';

@Component({
  selector: 'app-rap-management',
  templateUrl: './rap-management.component.html',
  styleUrls: ['./rap-management.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class RAPManagementComponent implements OnInit {



  public entiArray: Models.enteDTO[] = [];
  public showConfirmDate: boolean = false;

  public RAPformRO: FormGroup = this.formBuilder.group({
    nome: [{ value: null, disabled: true }],
    cognome: [{ value: null, disabled: true }],
    enteAzienda: [{ value: null, disabled: true }],
    dtConferma: [{ value: null, disabled: true }],
    dtRichiesta: [{ value: null, disabled: true }],
    dtFineValLD: [{ value: null, disabled: true }],
  });

  public rapStatus: FormGroup = null;

  public breadcrumbs = null;

  public RAP: Models.RappEnteDTO;
  public idRAP: string;
  public isLoading = false;
  public isRequest = false;
  public readOnly = false;

  public showFineValidDate = false;

  public stati: { value: number, label: string }[] = [
    { label: 'Richiesta confermata', value: 1 },
    { label: 'La richiesta va risottomessa', value: -1 },
    { label: 'Richiesta non confermata', value: 2 }
  ];


  constructor(
    private enteService: EnteService,
    private formBuilder: FormBuilder,
    private productService: ProductsService,
    private router: Router,
    private route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.isLoading = true;
    this.rapStatus = this.formBuilder.group({
      flgConferma: [null, Validators.required],
      numProtocollo: [null, [Validators.maxLength(20)]],
      dtProtocollo: [null],
      motivation: [null, Validators.maxLength(1000)]
    }, { validators: this.conditionalRequiredForMotivations() });

    this.breadcrumbs = {
      base: '',
      entries: new Array<IBreadcrumbsEntry>(
        { label: 'MyExtranet', url: `backoffice` },
        { label: 'RAP confermati', url: `backoffice/RAP` },
        { label: 'Dettaglio RAP' }
      )
    };

    this.enteService.getAutocomplete(99999, false, false).then(enti => {
      this.entiArray = enti;
    });

    this.route.queryParamMap.subscribe(queryParams => {
      if (this.route?.snapshot?.data?.isRequest) {
        this.isRequest = true;
        this.breadcrumbs = {
          base: '',
          entries: new Array<IBreadcrumbsEntry>(
            { label: 'MyExtranet', url: `backoffice` },
            { label: 'Richieste RAP', url: `backoffice/RAP-requests` },
            { label: 'Dettaglio richiesta RAP' }
          )
        };
      }
      if (queryParams.has('idRappr')) {
        this.idRAP = queryParams.get('idRappr');

        this.productService.getRap(this.idRAP, false).then(rappEnte => {
          this.RAP = rappEnte;
          if (!!this.RAP.motivConferma) {
            this.rapStatus.get('motivation').setValue(this.RAP.motivConferma);
          }
          this.showFineValidDate = !rappEnte.flgAttivo && !!rappEnte.flgConferma;
          this.showConfirmDate = !!rappEnte.flgConferma;
          this.RAPformRO.patchValue({
            nome: rappEnte.utente.nome,
            cognome: rappEnte.utente.cognome,
            enteAzienda: rappEnte.ente || null,
            dtRichiesta: rappEnte.dtRichiesta ? new Date(rappEnte.dtRichiesta) : null,
            dtConferma: rappEnte.dtConferma ? new Date(rappEnte.dtConferma) : null,
            dtFineValLD: rappEnte.dtFineValLD ? new Date(rappEnte.dtFineValLD) : null
          });
          if (!!this.RAP && this.RAP.flgConferma !== null && this.RAP.flgConferma !== undefined && this.RAP.flgConferma !== 0) {
            this.readOnly = true;
            this.rapStatus.patchValue({
              flgConferma: this.RAP.flgConferma,
              numProtocollo: rappEnte.numProtocollo,
              dtProtocollo: rappEnte.dtProtocollo ? new Date(rappEnte.dtProtocollo) : null,
            });
            setTimeout(() => {
              this.rapStatus.disable();
            }, 100);

          }
          setTimeout(() => {
            this.isLoading = false;
          }, 150);


        }).catch(err => {
          this.isLoading = false;
          this.readOnly = true;
        });
      } else {
        this.isLoading = false;
      }
    });

  }

  showDenomination(entity: { idEnte: number, denominazione: string }) {
    if (!entity || !entity.idEnte) return null;
    const id = entity.idEnte;
    return id !== null && id !== undefined ? entity.denominazione : null;
  }

  sendData() {
    this.isLoading = true;
    if (this.readOnly) {
      return null;
    }
    let postObj = {
      idRappr: this.idRAP,
      flgConferma: this.rapStatus.get('flgConferma').value,
      numProtocollo: this.rapStatus.get('numProtocollo').value,
      dtProtocollo: this.manageDateZones(new Date(this.rapStatus.get('dtProtocollo').value), false),
      motivazione: this.rapStatus.get('motivation').value
    }
    this.rapStatus.updateValueAndValidity();

    this.productService.changeRapStatus(postObj, false).then(val => {
      this.isLoading = false;
      if (this.isRequest) {
        this.router.navigate(['/backoffice/RAP-requests']);
      } else {
        this.router.navigate(['/backoffice/RAP']);
      }

    }).catch(err => {
      this.isLoading = false;
    });
  }

  private conditionalRequiredForMotivations = () => (
    fg: FormGroup,
  ): ValidationErrors | null => {
    if (!this.rapStatus) return null;
    const controlNotNullM = !!fg.get('motivation').value;
    const controlNotNullP = !!fg.get('numProtocollo').value;
    const controlNotNullPdt = !!fg.get('dtProtocollo').value;
    const flgConfermaValue = fg.get('flgConferma').value;
    const requiredActiveM = !controlNotNullM && (flgConfermaValue == 2 || flgConfermaValue == -1);
    if (requiredActiveM) {
      fg.get('motivation').setErrors({ required: true });
    } else {
      fg.get('motivation').setErrors(null);
    }
    if (!controlNotNullP && flgConfermaValue == 1) {
      fg.get('numProtocollo').setErrors({ required: true });
    } else {
      fg.get('numProtocollo').setErrors(null);
    }
    if (!controlNotNullPdt && flgConfermaValue == 1) {
      fg.get('dtProtocollo').setErrors({ required: true });
    } else {
      fg.get('dtProtocollo').setErrors(null);
    }
    return requiredActiveM || (!controlNotNullP && flgConfermaValue == 1) || (!controlNotNullPdt && flgConfermaValue == 1) ? { required: true } : null;

  }


  getFileName(fileNameWithId: string): string {
    if (!fileNameWithId) return null;
    return fileNameWithId.split('_', (fileNameWithId.split('_').length - 1)).join('_');

  }

  manageDateZones(data: Date, ingresso: boolean = true): Date {
    let sign = 1;
    if (ingresso) {
      sign = -1;
    }
    let parsedDate = new Date(data);
    let adjustedDate = new Date(parsedDate.valueOf() - (sign) * parsedDate.getTimezoneOffset() * 60000);
    return adjustedDate
  }

}


