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
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-date-select-modal',
  templateUrl: './date-select-modal.component.html',
  styleUrls: ['./date-select-modal.component.scss'],
  encapsulation : ViewEncapsulation.None

})
export class DateSelectModalComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<DateSelectModalComponent>,

    @Inject(MAT_DIALOG_DATA) public data: { title: string, description: string, date: Date }) { }

  thisDate: Date = null;



  ngOnInit(): void {
    this.thisDate = (new Date(this.data.date));
  }

  get status(): boolean {
    if (!!this.thisDate) {
      return this.thisDate instanceof Date && !isNaN(this.thisDate.getTime());
    }
    return false;
  }

  get thisDateTZ(): Date {
    return this.fixTimezone(this.thisDate);
  }

  public fixTimezone(origDate: Date): Date {
    if (!!origDate) {
      const adjustedDate = new Date(origDate.valueOf() - origDate.getTimezoneOffset() * 60000);
      return adjustedDate;
    }
  }


}
