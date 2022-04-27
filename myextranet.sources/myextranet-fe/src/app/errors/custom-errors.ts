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
export class NoRequestConfigurationFound extends Error {
    constructor(...params) {
        super(...params);

        this.name = '#NRCF'; //no request configiration found
        this.message = 'Non è presente una procedura di attivazione per questo prodotto';
    }
}

export class DisabledProductRequest extends Error {
    constructor(productName: string, ...params) {
        super(...params);

        this.name = '#DPR';// disabled product rquest
        if (!!productName) this.message = `La prodcedura di attivazione del prodotto ${productName} è disabilitata`;
        else this.message = 'La prodcedura di attivazione di questo prodotto è disabilitata'
    }
}
