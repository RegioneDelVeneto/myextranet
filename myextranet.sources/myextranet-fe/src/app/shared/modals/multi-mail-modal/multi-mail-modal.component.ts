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
import { Component, Inject, OnInit, ViewEncapsulation } from '@angular/core';
import { FormArray, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-multi-mail-modal',
  templateUrl: './multi-mail-modal.component.html',
  styleUrls: ['./multi-mail-modal.component.scss'],
  encapsulation : ViewEncapsulation.None
})
export class MultiMailModalComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<MultiMailModalComponent>,
    @Inject(MAT_DIALOG_DATA)
    public data: { title: string, description: string },
    private fb: FormBuilder
  ) { }

  public mailForm: FormGroup = this.fb.group({
    messaggio: [null, Validators.required],
    oggetto: [null, Validators.required],
    indirizzi: this.fb.array([
    ]),
    flgAllegaUtentiRich : [null]
  });

  ngOnInit(): void {
  }

  public get indirizzi(): FormControl[] {
    return (this.mailForm.get('indirizzi') as FormArray).controls as FormControl[];
  }

  public addAddress(): void {
    (this.mailForm.get('indirizzi') as FormArray).push(
      this.fb.control('', [Validators.required, Validators.maxLength(60), Validators.pattern('^[_A-Za-z0-9-+](.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$')])
    );
  }

  public removeAddress(i: number): void {
    (this.mailForm.get('indirizzi') as FormArray).removeAt(i);

  }

}
