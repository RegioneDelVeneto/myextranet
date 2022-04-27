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
import { ValidationErrors } from "@angular/forms";
import { MatFormFieldAppearance } from "@angular/material/form-field";

declare module Models {

    interface MyPortalContent {
        titolo: string;
        descrizione: string;

        id: string;
        tipo: string;
        logo: string;
        parent: string;
        sottotitolo: string;
        telefono?: string;
        email?: string

    }

    interface genericApiResponse {
        data: string | any; status: string; message: string
    }

    interface MyPortalEvent extends MyPortalContent {
        data: string;
        prodotto: string | string[];
        progetto: string | string[];
        inPresenza: boolean;
        inStreaming: boolean;
        numeroMaxPartecipanti: number;
        aula: string;
        annullato: boolean;
        luogo: string;
        dateDa: string;
        dateA: string;
        telefono: string;
        email: string;
        urlQuestionario: string;
        urlQuestionarioIn: string;
        streamingLink: string;
        htmlStreamingNotes: string;
        linkStreamingRelatore?: string;
        indicazioniStreamingRelatore?: string;

    }

    interface ProgettoExtended {
        collaboratoreProgetto: CollaboratoreDTO; myExtranetContent: MyPortalContent;
    }

    interface PagedContent {

        records: any;
        pagination: {
            totalRecords: number;
        }

    }

    interface PagedContentTyped<T> {

        records: T[];
        pagination: {
            totalRecords: number;
        }

    }

    interface ComboElement {
        value: any;
        label: string;
    }

    interface BaseCommunication {

        idMessaggio?: number;
        tipo?: string;
        area?: string;
        idEvento?: string;
        idProgetto?: string;
        idContenuto?: string;
        destinatario?: string;
        indirizzo?: string;
        titolo: string;
        messaggio: string;
        dtInvio?: Date;


    }

    interface utenteFilter {
        nome?: string;
        cognome?: string;
        idEnte?: number;
        azienda?: string;
        flgArchived: 1 | number | null;
    }

    interface CardConfiguration {
        icon: string; label: string; endpoinr: string
    }

    interface IscrizioneEvento {
        utente?: {
            idUtente: number;
        };
        ente?: {
            idEnte: number;
        };
        azienda?: string;
        email?: string;
        idEvento: string;
        nome?: string;
        cognome?: string;
        idIscritto?: number;
        flgPartecipPref?: string;
    }

    interface IscrizioneEventoFO {
        utente?: {
            idUtente: number;
        };
        idEvento: string;
        flgPartecipPref: "PRE" | "REM" | null;
    }

    interface iscrittoDTO {
        idIscritto?: number;
        utente: utenteDTO;
        idEvento: string;
        flgRelatore: number | 1;
        flgPartecipLoco: number | 1;
        flgPartecipRemoto: number | 1;
        flgPartecipPref: string;
        dtIscrizione: string;
        dtInvioAttestato: string;
        dtRichQuestionario: string;
        nome: string;
        cognome: string;
        email: string;
        azienda: string;
        ente: enteDTO;
    }

    interface iscrizioneEventoDTO {
        iscrittoEvento: iscrittoDTO;
        myExtranetContent: MyPortalEvent;
    }

    interface comuneDTO {

        codComune: number;
        desComune: string;
        provincia: {
            codProvincia: string;
            desProvincia: string;

        }
    }

    interface enteDTO {
        idEnte: number;
        codIpa: string;
        denominazione: string;
        logo?: string;
        comune: comuneDTO | { codComune: number; desComune?: string };
        indirizzo: string;
        cap: string;
        email: string;
        pec: string;
        codFiscale: string;
        telefono: string;
        fax: string;
        urlWebSite: string;
        urlFacebook: string;
        urlInstagram: string;
        urlTwitter: string;
        categoria?: {
            idCategoria?: number;
            codCategoria?: string;
            desCategoria?: string
        }
        dtFineVal?: number;
        dtInizioVal?: number;
        dtFineValLD: number[] | Date;
        dtInizioValLD: number[] | Date;
        logoFileMetadata?: {
            length: number;
            mimeType: string;
            fileName: string;
        }
    }

    interface utenteDTO {
        idUtente?: number;
        nome?: string;
        cognome?: string;
        codFiscale?: string;
        email?: string;
        ente?: enteDTO | { idEnte: number; denominazione?: string };
        azienda?: string;
        partitaIva?: string;
        utentePadre?: number;
        telefono?: string;
        telefonoUff?: string;
    }

    interface UserInfo {
        codFiscale: string;
        givenName: string;
        familyName: string;
        userId: number;
        email: string;
        username: string;
        acls: {
            acl: string;
            permissions: string[];
        }[];
        userExpired: boolean;
        tenantExpired: boolean;

    }

    interface AclPermissionRouteMap {

        route: string;
        permissions: string[];
        acl: string;

    }

    interface TableHeader {
        textCut?: number;
        hiddenIf?: string;
        sortActive?: boolean;
        definition: string;
        header: string;
        type?: 'select' | 'deepProp' | 'date' | 'html' | 'boolean' | 'association' | 'chip' | 'objectMapper' | 'list' | 'actions' | 'move' | 'buttons';
        span?: number;
        associationLabel?: string;
        showOnlyIf?: string;
        dateFormat?: string; //'dd/MM/y'
        chipsArray?: {
            label: string;
            value: string;
            color: string;
        }[];
        objectMapped?: {
            [key: string]: string
        };
        showAsButtonIfOne?: boolean;
        actions?: {
            type?: string;
            icon: string;
            event: string;
            label?: string;
            acl?: string;
            permissions?: string[];
            showOnlyIf?: string;
            hiddenIf?: string;
            color?: string;
            legend?: string
        }[]
    }

    interface CollaboratoreDTO {
        idCollab: number;
        utente: utenteDTO;
        idProgetto: string;
        flgCoord: number | boolean;
        flgConferma: number | boolean;
        dtRichiesta: string | Date;
        dtConferma: string | Date;
        motivConferma: string;
        flgCoordStr: string;
        flgConfermaStr: string
        valid?: boolean;
        dtFineVal?: number | Date
        dtInizioVal?: number | Date
        dtFineValLD?: number | Date
        dtInizioValLD?: number | Date

    }

    interface IscrizioneCollaboratoreDTO {
        idCollab?: number;
        utente?: utenteDTO | { idUtente: number };
        idProgetto?: string;
        flgCoord?: number | boolean;
        flgConferma?: number | boolean;
        dtRichiesta?: string | Date;
        dtConferma?: string | Date;
        motivConferma?: string;
        flgCoordStr?: string;
        flgConfermaStr?: string
    }

    interface CollaboratoreMyExtranet {
        idCollab: number
        nome: string;
        cognome: string;
        denominazioneEnteAzienda: string;
        flgCoordStr: string;
        idProgetto: string;
        fullData: CollaboratoreDTO;
        dtInizioVal?: number | Date;
        dtFineVal?: number | Date;
        dtInizioValLD?: number | Date;
        dtFineValLD?: number | Date;

    }

    interface CategoriaEnteDTO {

        idCategoria: number;
        codCategoria: string;
        desCategoria: string;

    }

    interface ProdottoAttivabileDTO {
        idProdottoAtt: number | string;
        idProdotto: string;
        prodottoProp?: ProdottoAttivabileDTO;
        nomeProdottoAttiv: string;
        desAttivazione?: string;
        desAttivazioneBreve?: string;
        codAppProfMan?: string;
        dtAttivabileDa?: string | Date;
        dtAttivabileA?: string | Date;
    }

    interface ProdottoAttivabilePatchDTO {
        idProdotto: string;
        prodottoProp?: ProdottoAttivabileDTO;
        nomeProdottoAttiv?: string;
        desAttivazione?: string;
        desAttivazioneBreve?: string;
        codAppProfMan?: string;
        dtAttivabileDa?: string | Date;
        dtAttivabileA?: string | Date;
    }

    interface GenericUserFormSetting { appearance?: MatFormFieldAppearance; validators: { [key: string]: ValidationErrors[] }; ro: boolean; data: { [key: string]: string }; enteAziendaRender: 1 | 2 | 3; enteAziendaArray: any[] }

    interface GenericCardModel {
        icon?: string;
        routerLink: string;
        titolo: string;
        subtitle?: string;
        card: string;
        body: string;
        data?: string;
        miniIcon?: string;
        content: any;
        id: string;
        annullato?: boolean;
        queryParams: { [key: string]: string }

    }

    interface HorizontalCardModel {
        logo: string;
        status: number;
        statusLabel: string;
        routerLink: {
            link: string[];
            params: { [key: string]: string }
        }
        routerLinkValid: {
            link: string[];
            params: { [key: string]: string }
        }
        titolo: string;
        content: any;
        id: number;
        links: {
            link: string[];
            label: string;
            icon: string;
            params: { [key: string]: string }
        }[]

    }

    interface RappEnteDTO {
        idRappr: number;
        flgAttivo?: boolean;
        utente: utenteDTO
        ente: enteDTO
        tipoRappr: string;
        dtRichiesta: Date;
        flgConferma: 1 | number | -1 | 2;
        dtInizioVal: Date;
        dtFineVal: Date;
        dtConferma: Date;
        motivConferma: null;        
        dtFineValLD?: Date | number;
        dtInizioValLD?: Date | number;
        numProtocollo: string;
        dtProtocollo: Date;
    }

    interface ModuliFileMetadata {
        length: number;
        mimeType: string;
        fileName: string;
    }

    interface RuoloDto {
        codRuolo: string;
        desRuolo: string;
        numRuolo: number;
    }

    interface ProdottoAttivatoDto {
        idAttivazione: number | string;
        prodottoAttivabile?: ProdottoAttivabileDTO;
        ente?: enteDTO;        
        dtFineVal?: Date | number;
        dtInizioVal?: Date | number;
        dtFineValLD?: Date | number;
        dtInizioValLD?: Date | number;
        utenteProdottoAttivatoList?: any[];
        richiestaProdottoWithStep?: RichiestaProdottoWithStepDto;
        valid?: boolean;
        numRich?: number;
        stato?: number
    }


    interface RichiestaProdottoWithStepDto {
        idProdAttivRich: number;
        prodottoAttivato: ProdottoAttivatoDto;
        procedimentoProdotto: ProcProdottoDTO;
        numVersione: number;
        dtRich: Date;
        codStato: string;
        ultimeNote: string;
        flgFineRich: number | 1;
        stepRichiestaProdottoList: stepRichiestaProdottoModelDto[];
        documentoRichiestaProdottoList: DocumentoRichiestaProdottoModelDto[];
        utenteRichiestaProdottoList: UtenteRichiestaProdottoModel[];
        stepProcedimentoProdotto: StepProcProdottoDTO;
    }

    interface UtenteRichiestaProdottoModel {
        utenteProdottoAttivato?: { idUtenteProd: number };
        idUtenteProd?: number; 
        idUtenteRich?: number;
        richiestaProdotto?: {
            idProdAttivRich: number;
            prodottoAttivato: ProdottoAttivatoDto;
            procedimentoProdotto: ProcProdottoDTO;
            numVersione: number;
            dtRich: Date;
            codStato: string;
            ultimeNote: string;
            flgFineRich: number | 1;
        };
        
        richOper?: 'INS' | 'DEL' | 'MOD';
        ruoloProdotto?: RuoloDto;
        ruolo?: RuoloDto;
        utente: utenteDTO;
        nome: string;
        codFiscale: string;
        cognome: string;
        email: string
        telefono: string;
    }

    interface DocumentoRichiestaProdottoModelDto {
        idProdRichDoc: number;
        richiestaProdotto?: {
            idProdAttivRich: number;
            prodottoAttivato: ProdottoAttivatoDto;
            procedimentoProdotto: ProcProdottoDTO;
            numVersione: number;
            dtRich: Date;
            codStato: string;
            ultimeNote: string;
            flgFineRich: number | 1;
        };
        stepRichiestaProdotto?: stepRichiestaProdottoModelDto;
        idDocumento: string;
        nomeDocumento: string;
        flgEnabled: 1 | number;
        metadata?: {
            fileName: string
        }

    }

    interface stepRichiestaProdottoModelDto {
        idStep: number;
        richiestaProdotto: {
            idProdAttivRich: number;
            prodottoAttivato: ProdottoAttivatoDto;
            procedimentoProdotto: ProcProdottoDTO;
            numVersione: number;
            dtRich: Date;
            codStato: string;
            ultimeNote: string;
            flgFineRich: number | 1;
        };
        dtStep: Date | number;
        esecutore: string;
        codStato: string;
        esitoStep: any;
        noteStep: string;
        operIns?: string;
    }

    interface UtenteRuoloDto {

        idUtenteProd?: number;
        idUtenteRich?: number;
        prodottoAttivato: ProdottoAttivatoDto;
        idProdAttivRich?: number;
        ruolo: RuoloDto;
        flgEnabled?: 1 | number;
        utente?: utenteDTO;
        codFiscale?: string;
        nome?: string;
        cognome?: string;
        email?: string;
        telefono?: string
        telefonoUff?: string
        azienda?: string;

    }



    interface GruppoUtentiRuoloDTO {

        ruolo: RuoloDto;
        utenti: any[];

    }

    interface GruppoUtentiRuoloExtended {
        ruolo: RuoloDto;
        utenti: UtenteRuoloExtended[];
    }

    interface UtenteRuoloExtended extends UtenteRuoloDto {
        uiid: string;
        status: string;
        prevStatus: string;
        touched: boolean;
        deleted: boolean;
    }

    interface UtenteRuoliPostNewOperationDto {
        tipoOperazione: string;
        utenteProdottoAttivato: UtenteRuoloDto;
    }

    interface UtenteRuoliPostNewOperationDtoExtended {
        tipoOperazione: string;
        utenteProdottoAttivato: UtenteRuoloExtended;
    }

    interface ApiError {
        status: string;
        message: string;
        data?: any;
        code: string;
    }

    interface RapInsertModel {
        newRappresentanteEnte: { utente: { idUtente: number }; ente: { idEnte: number } };
    }
    interface RapPatchModel {
        newRappresentanteEnte: { utente: { idUtente: number }; ente?: { idEnte: number }, idRappr: number, flgConferma: number };
    }

    interface TipoRichiestaProdDTO {
        codTipoRich: string;
        desTipoRich: string
    }

    interface ProcProdottoDTO {
        idProdottoAtt: number;
        idProdottoProc: number;
        prodotto: ProdottoAttivabileDTO;
        numVersione: number;
        tipoRichiestaProdotto: TipoRichiestaProdDTO;
        desProdottoProc: string;
        desProdottoProcEstesa: string;
        flgEnabled: 1 | 0;
        step?: StepProcProdottoDTO[]
    }

    interface StepProcProdottoDTO {
        idStatoConf: number;
        procedimentoProdotto: {
            idProdottoProc: number;
            prodotto: {
                idProdotto: string;
                nomeProdottoAttiv: string;
                desAttivazione: string;
                desAttivazioneBreve: string;
                dtAttivabileDa: Date;
                dtAttivabileA: Date;
                codAppProfMan: string
            };
            numVersione: number;
            tipoRichiestaProdotto: {
                codTipoRich: string;
                desTipoRich: string
            };
            desProdottoProc: string;
            desProdottoProcEstesa: string;
            flgEnabled: number
        };
        numStep: number;
        codStato: string;
        desStato: string;
        competenza: string;
        flgAggiornaUtenti: number;
        flgFineRich: number;
        azioni: string;
    }

    interface StepToBEDTO {
        tipoOperazione: string,
        stepProcedimentoProdotto: {
            idStatoConf: number,
            procedimentoProdotto: {
                idProdottoProc: number,
                prodotto: {
                    idProdotto: string,
                    nomeProdottoAttiv: string,
                    desAttivazione: string,
                    desAttivazioneBreve: string,
                    dtAttivabileDa: Date,
                    dtAttivabileA: Date,
                    codAppProfMan: string
                },
                numVersione: number,
                tipoRichiestaProdotto: {
                    codTipoRich: string,
                    desTipoRich: string
                },
                desProdottoProc: string,
                desProdottoProcEstesa: string,
                flgEnabled: number
            },
            numStep: number,
            codStato: string,
            desStato: string,
            competenza: string,
            flgAggiornaUtenti: number,
            flgFineRich: number,
            azioni: string
        }
    }

    interface docInputModel {
        fileLabel: string;
        file: File | string;
        nome: string;
        uuid?: string;
        touched?: boolean;
        deleted?: boolean;
    }

    interface DocumentoRichiestaProdottoModelDtoExtended extends DocumentoRichiestaProdottoModelDto {
        uuid?: string;
        touched?: boolean;
        deleted?: boolean;
        file: string | File;
        label: string;
        
    }

    interface MyBoxMultiResponseModel {
        status: string;
        message: string;
        data: {
            [key: string]: string
        };
        code: string;
    }

    interface DocumentoRichiestaConOperazioneExtended {
        tipoOperazione: 'elimina' | 'modifica';
        documentoRichiestaProdotto: DocumentoRichiestaProdottoModelDtoExtended;
    }

    interface DocumentoRichiestaConOperazione {
        tipoOperazione: 'elimina' | 'modifica';
        documentoRichiestaProdotto: DocumentoRichiestaProdottoModelDto;
    }

    interface UtenteRichiestaConOperazione {
        tipoOperazione: 'elimina' | 'modifica';
        utenteRichiestaProdotto: UtenteRichiestaProdottoModel;
    }

    interface AvvioRichiestaModel {

        idEnte: number,
        idProdotto: string,
        idAttivazione: number,
        codTipoRich: string,
        note: string,
        esito: number,
        flgAnnulla: number,
        documenti: DocumentoRichiestaConOperazione,
        utenti: UtenteRichiestaConOperazione

    }

    interface RequestToolbarConfiguration {

        step: number;
        status: 0 | 1 | 2 | 3;

        valid: boolean;

    }


    interface RequestMultiCom {

        indirizzo: string;
        titolo: string;
        messaggio: string;
        flgAllegaUtentiRich: 1 | 0;

    }

    interface DatiRichiestaProdottoModel {
        prodottoAttivato: ProdottoAttivatoDto;
        datiRichiesta: RichiestaProdottoWithStepDto
        datiProcedimento: ProcProdottoDTO
    }

}
