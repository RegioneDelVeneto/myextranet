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
package it.regioneveneto.myp3.myextranet.service;

import java.text.ParseException;

import it.regioneveneto.myp3.myextranet.model.MyExtranetContent;
import it.regioneveneto.myp3.myextranet.model.MyPortalContent;
import it.regioneveneto.myp3.myextranet.web.dto.ContentFilterDTO;
import it.regioneveneto.myp3.myextranet.web.dto.PagedData;

public interface MyPortalService {
	MyExtranetContent getContentById( String  id);

	PagedData<MyExtranetContent> advancedSearchPaginated(ContentFilterDTO contentFilter, String contentType, int pageNumber, int pageSize, String orderProperty, String orderDirection) throws ParseException;

	PagedData<MyExtranetContent> getContentsByIds(String[] contentIds, Integer pageNumber, Integer pageSize,
			String orderProperty, String orderDirection);

	PagedData<MyExtranetContent> advancedSearchPaginated(ContentFilterDTO contentFilter, String contentType,
			Integer pageNumber, Integer pageSize, String orderProperty, String orderDirection, String[] ids) throws ParseException;
	
	
}
