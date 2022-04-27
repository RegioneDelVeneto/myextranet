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
import { AfterViewInit, Component, Inject, ViewEncapsulation } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { DataTableService, IForm } from '@eng/morfeo';

@Component({
  selector: 'app-general-list-modal',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './general-list-modal.component.html',
  styleUrls: ['./general-list-modal.component.scss']
})
export class GeneralListModalComponent implements AfterViewInit {

  constructor(
    public dialogRef: MatDialogRef<GeneralListModalComponent>,
    private tableSrv: DataTableService,
    @Inject(MAT_DIALOG_DATA) public data: { label: string, form: IForm, renderer?: [{ key: string, r_function: (a: any) => string }] }) { }

  public ngAfterViewInit(): void {
    if (this.data && this.data.renderer && this.data.renderer.length > 0) {
      this.data.renderer.forEach(val => {
        this.tableSrv.setRenderer(val.key, val.r_function);
      });

    }
  }

}
