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
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Models } from '../models/model';

@Injectable({
  providedIn: 'root'
})
export class ProductRequestsService {
  private baseUrl = '';

  constructor(
    private http: HttpClient
  ) { }

  public getProdRequestConfigById(id: string, isFO: boolean = true): Promise<Models.ProcProdottoDTO> {
    const FOstring: string = isFO ? 'frontoffice/' : '';
    return this.http.get<Models.ProcProdottoDTO>(`/${FOstring}procedimento-prodotto/${id}`).toPromise();
  }

  public deleteProdRequestConfigById(id: string, isFO: boolean = true): Promise<Models.ProcProdottoDTO> {
    const FOstring: string = isFO ? 'frontoffice/' : '';
    return this.http.delete<Models.ProcProdottoDTO>(`/${FOstring}procedimento-prodotto/${id}`).toPromise();
  }

  public getProductRequestData(idEnte: string, idProdottoAtt: string, codTipoRich: string, isFo: boolean): Promise<Models.DatiRichiestaProdottoModel> {
    const FOstring: string = isFo ? 'frontoffice/' : '';

    let params: HttpParams = new HttpParams();

    params = params.append('idEnte', idEnte);
    params = params.append('idProdottoAtt', idProdottoAtt);
    if (codTipoRich !== null) {
      params = params.append('codTipoRich', codTipoRich);
    }

    return this.http.get<Models.DatiRichiestaProdottoModel>(`/${FOstring}prodotto-attivato/dati-richiesta-prodotto`, { params }).toPromise();

  }

  public getRequestsTypes(): Promise<Models.PagedContentTyped<Models.TipoRichiestaProdDTO>> {
    return this.http.get<Models.PagedContentTyped<Models.TipoRichiestaProdDTO>>(`/tipo-richiesta-prodotto?_pageSize=999999`).toPromise();

  }
  public postStepsForProductRequest(postObject: any) {

    return this.http.post(`/procedimento-prodotto/aggiorna-step-procedimento-prodotto`, postObject).toPromise();
  }

  public getFirstStep(idEnte: number | string, idProdotto: string, opr: string) {

    return this.http.get(`/frontoffice/procedimento-prodotto/primo-step?idEnte=${idEnte}&idProdottoAtt=${idProdotto}&codTipoRich=${opr}`).toPromise();
  }

  public postAvvioRichiesta(data: any, isFo: boolean = true) {
    const FOstring: string = isFo ? 'frontoffice/' : '';

    return this.http.post(`/${FOstring}prodotto-attivato/avvia-richiesta`, data).toPromise();
  }

  public aggiornaRichiesta(data: any, isFo: boolean = true) {
    const FOstring: string = isFo ? 'frontoffice/' : '';

    return this.http.post(`/${FOstring}prodotto-attivato/aggiorna-richiesta`, data).toPromise();
  }

  private loadMultipleFiles(files: { file: File | string, uuid: string }[], isFO: boolean = true): Promise<Models.MyBoxMultiResponseModel> {
    const FOstring: string = isFO ? 'frontoffice/' : '';
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'application/form-data');

    const filesToSend = new FormData();
    files.forEach(data => {
      filesToSend.append(data.uuid, data.file);
    });


    return this.http.post<Models.MyBoxMultiResponseModel>(`/${FOstring}mybox/multi`, filesToSend, { headers }).toPromise();
  }

  public sendComToMultipleInputUsers(comm: Models.RequestMultiCom, id: string): Promise<any> {
    return this.http.post(`/richiesta-prodotto/${id}/comunicazione`, comm).toPromise();
  }


  public mapDocumentsToPostObject(files: Models.DocumentoRichiestaProdottoModelDtoExtended[], isFo: boolean): Promise<Models.DocumentoRichiestaConOperazione[]> {

    let fileToSendExtended: Models.DocumentoRichiestaConOperazioneExtended[] = [];

    files.forEach(file => {
      if (file.deleted && file.idProdRichDoc) { //file cancellati che erano presenti a DB
        fileToSendExtended.push({
          tipoOperazione: 'elimina',
          documentoRichiestaProdotto: file
        });
      } else if (file.idProdRichDoc && file.touched) { //file modificati che erano presenti a DB
        fileToSendExtended.push({
          tipoOperazione: 'modifica',
          documentoRichiestaProdotto: file
        });
      } else if (!file.idProdRichDoc && !file.deleted) { //Nuovi inserimenti
        fileToSendExtended.push({
          tipoOperazione: 'modifica',
          documentoRichiestaProdotto: file
        });
      }
    });

    return this.sendDocumentsToMyBox(fileToSendExtended.map(v => v.documentoRichiestaProdotto), isFo).then(out => {
      const data = out.data;
      if (!data || out.status !== 'OK') {
        throw new Error('DOC_ERROR : Unable do upload documents');
      }
      let fileToSend: Models.DocumentoRichiestaConOperazione[] = [];

      fileToSendExtended.forEach(file => {
        const fileRichExt = file.documentoRichiestaProdotto;
        if (!!data[file.documentoRichiestaProdotto.uuid]) {

          fileToSend.push(
            {
              //...file,
              tipoOperazione: file.tipoOperazione,
              documentoRichiestaProdotto: {

                flgEnabled: 1,
                idProdRichDoc: fileRichExt.idProdRichDoc,
                nomeDocumento: fileRichExt.nomeDocumento,
                idDocumento: data[file.documentoRichiestaProdotto.uuid]
              }
            }
          );
        }
        else {
          fileToSend.push({

            tipoOperazione: file.tipoOperazione,
            documentoRichiestaProdotto: {
              flgEnabled: 1,
              idProdRichDoc: fileRichExt.idProdRichDoc,
              nomeDocumento: fileRichExt.nomeDocumento,
              idDocumento: fileRichExt.idDocumento
            }
          });
        }
      });

      return Promise.resolve(fileToSend);
    });

  }

  public sendDocumentsToMyBox(docs: Models.DocumentoRichiestaProdottoModelDtoExtended[], isFo: boolean): Promise<Models.MyBoxMultiResponseModel> {
    const docToSend: {
      file: File | string,
      uuid: string
    }[] = docs.filter(doc => {
      return !doc.deleted && !doc.idDocumento;
    }).map(doc => {
      return {
        file: doc.file,
        uuid: doc.uuid
      };
    });
    if (docToSend.length > 0) {
      return this.loadMultipleFiles(docToSend, isFo);
    }
    else {
      return Promise.resolve({
        status: "OK",
        message: "No content to upload",
        data: {},
        code: "SUCCESS"
      });
    }
  }


  public filterUser(users: Models.GruppoUtentiRuoloExtended[]): Models.UtenteRichiestaConOperazione[] {
    let touchedDataPost: Models.UtenteRichiestaConOperazione[] = [];
    users.forEach(ruolo => {

      ruolo.utenti.forEach(utente => {
        let usr: Models.UtenteRichiestaProdottoModel = {

          ruoloProdotto: ruolo.ruolo,

          codFiscale: null,
          cognome: null,
          email: null,
          nome: null,

          telefono: null,
          utente: null
        };
        if (utente) {
          Object.keys(utente).forEach(key => {
            //remove fe specific keys
            if (key !== 'uiid' && key !== 'status' && key !== 'prevStatus' && key !== 'deleted' && key !== 'touched' && key !== 'ruolo') {
              usr[key] = utente[key];
            }
            if (key === 'utente' && utente[key] && utente[key].idUtente) {
              usr.utente = {
                idUtente: utente[key].idUtente
              };

            }

          });

          if (usr.idUtenteProd !== null && usr.idUtenteProd !== undefined) {
            usr = { ...usr, utenteProdottoAttivato: { idUtenteProd: usr.idUtenteProd } };
          }

          if (!!usr.utente && !!usr.utente.idUtente) {
            usr.codFiscale = null;
            usr.cognome = null;
            usr.nome = null;
          }

          if (utente.deleted) { 
            let pushedObj: Models.UtenteRichiestaConOperazione = { tipoOperazione: 'modifica', utenteRichiestaProdotto: usr };
            if (usr?.utenteProdottoAttivato?.idUtenteProd) {
              pushedObj.utenteRichiestaProdotto = { ...pushedObj.utenteRichiestaProdotto, richOper: 'DEL' };
            }
            touchedDataPost.push(pushedObj);
          } else if (!utente.deleted && utente.touched) {
            let pushedObj: Models.UtenteRichiestaConOperazione = { tipoOperazione: 'modifica', utenteRichiestaProdotto: usr };
            if (usr?.utenteProdottoAttivato?.idUtenteProd) {
              pushedObj.utenteRichiestaProdotto = { ...pushedObj.utenteRichiestaProdotto, richOper: 'MOD' };
            } else {
              pushedObj.utenteRichiestaProdotto = { ...pushedObj.utenteRichiestaProdotto, richOper: 'INS' };
            }
            touchedDataPost.push(pushedObj);
          } else if (!utente.deleted && !utente.touched && !utente.idUtenteRich) {
            let pushedObj: Models.UtenteRichiestaConOperazione = { tipoOperazione: 'modifica', utenteRichiestaProdotto: usr };
            if (!usr?.utenteProdottoAttivato?.idUtenteProd) {
              pushedObj.utenteRichiestaProdotto = { ...pushedObj.utenteRichiestaProdotto, richOper: 'INS' };
            }
            touchedDataPost.push(pushedObj);
          }

        }
      });
    });

    return touchedDataPost;
  }



}
