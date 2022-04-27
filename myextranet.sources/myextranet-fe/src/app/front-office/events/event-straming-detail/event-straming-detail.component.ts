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
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { IBreadcrumbsEntry } from 'src/app/shared/components/breadcrumbs/breadcrumbs.component';
/**
 * CLASSE NON UTILIZZATA
 */


@Component({
  selector: 'app-event-straming-detail',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './event-straming-detail.component.html',
  styleUrls: ['./event-straming-detail.component.scss']
})
export class EventStramingDetailComponent implements OnInit {

  constructor(private formBuilder: FormBuilder) { }

  eventForm: FormGroup = this.formBuilder.group({
    nome: ['', Validators.required],
    cognome: ['', Validators.required],
    email: ['', Validators.required],
    telefono: ['', Validators.required],
    ente: ['', Validators.required]
  });

  breadcrumbs = {
    base: '',
    entries: new Array<IBreadcrumbsEntry>(
      { label: 'Bacheca', url: '/utente/bacheca' },
      {label : 'I miei eventi', url : '/utente/eventi'},
      { label: `Partecipazione da remoto` },
    )
  };

  date : Date = new Date();
  ngOnInit(): void {
    this.eventForm.patchValue( {
      nome: 'Nome', cognome : 'Cognome', email:'mail@email.mail', telefono:'12123123', ente : 'Comune di Prova'
    });
    this.eventForm.disable();
  }

  sumbmit(){

  }


}
