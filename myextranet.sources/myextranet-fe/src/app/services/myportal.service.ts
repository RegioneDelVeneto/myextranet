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
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Models } from '../models/model';

@Injectable({
  providedIn: 'root'
})
export class MyportalService {


  constructor(private http: HttpClient) {

  }


  getAllProjects(FO: boolean): Promise<Models.PagedContent> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.PagedContent>(`/${FOstring}myportal/contents?_type=CT_myextranet_d_myext_projects&_pageSize=9999`).toPromise();
  }

  getAllProducts(FO: boolean): Promise<Models.PagedContent> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.PagedContent>(`/${FOstring}myportal/contents?_type=CT_myextranet_d_myext_products&_pageSize=9999`).toPromise();
  }
  getAllActivableProducts(FO: boolean): Promise<Models.PagedContent> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.PagedContent>(`/${FOstring}myportal/contents?_type=CT_myextranet_d_myext_products&_pageSize=9999&myext_attivabile=true`).toPromise();
  }

  getContent(id: string, FO: boolean): Promise<Models.MyPortalContent | Models.MyPortalEvent> {
    const FOstring: string = FO ? 'frontoffice/' : '';
    return this.http.get<Models.MyPortalContent | Models.MyPortalEvent>(`/${FOstring}myportal/content/${id}`).toPromise();
  }

  getEventFO(id: string): Promise<Models.MyPortalContent | Models.MyPortalEvent> {    
    return this.http.get<Models.MyPortalContent | Models.MyPortalEvent>(`/frontoffice/evento/${id}`).toPromise();
  }



}
