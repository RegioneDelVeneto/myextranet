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
package it.regioneveneto.myp3.myextranet.web.dto;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import it.regioneveneto.myp3.myextranet.utils.ObjectMapperUtils;

public class PagedData<T> implements Serializable {

	private long count;
	private int page;
	private int pageSize;
	
	private List<T> records;
	
	public PagedData(List<T> data, long count, int page, int pageSize) {
		super();
		this.records = data;
		this.count = count;
		this.page = page;
		this.pageSize = pageSize;
	}
	
	public PagedData(List<T> data, Page<?> page) {
		this(data, page.getTotalElements(), page.getNumber() + 1, page.getNumberOfElements());
	}
	
	public PagedData(Page<?> page, Class<T> cls) {
		this(ObjectMapperUtils.mapAll(page.getContent(), cls), page.getTotalElements(), page.getNumber() + 1, page.getNumberOfElements());
	}

	public List<T> getRecords() {
		return records;
	}

	public void setRecords(List<T> data) {
		this.records = data;
	}
	
	public Pagination getPagination() {
		return new Pagination(count);
	}
	
	public static Pageable buildPageable(int pageNumber, int pageSize, Sort sort) {
		
		Pageable pageable = pageNumber > 0 ? PageRequest.of(pageNumber - 1, pageSize, sort) : PageRequest.of(0, 99999, sort);

		return pageable;
	}

	public static Pageable buildPageable(int pageNumber, int pageSize, String sortProperty, String sortDirection) {
		
		Sort sort = (sortProperty != null && sortDirection != null) ? Sort.by(Direction.fromString(sortDirection), sortProperty) : Sort.unsorted();
		
		return buildPageable(pageNumber, pageSize, sort);
		
	}
	
	public static Pageable buildPageable(int pageNumber, int pageSize) {
		return buildPageable(pageNumber, pageSize, null, null);
	}
	
	private class Pagination {
		private long totalRecords;

		public long getTotalRecords() {
			return totalRecords;
		}

		public void setTotalRecords(long totalRecords) {
			this.totalRecords = totalRecords;
		}

		public Pagination(long totalRecords) {
			super();
			this.totalRecords = totalRecords;
		}
		
	}
}
