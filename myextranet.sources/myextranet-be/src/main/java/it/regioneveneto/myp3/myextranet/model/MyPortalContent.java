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
package it.regioneveneto.myp3.myextranet.model;

import java.util.Date;
import java.util.Map;

public class MyPortalContent {

	
	private String ipa;
	
	/**
	 * 
	 */
	private String name;
	
	/**
	 * 
	 */
	private String parent;
	
	/**
	 * 
	 */
	private String type;
	
	/**
	 * 
	 */
	private Date createdAt;
	
	/**
	 * 
	 */
	private Date modifiedAt;

	/**
	*
	 */
	 private Date firstPublishedAt;
	
	/**
	 * 
	 */
	private MyPortalContentStatus status;
	
	/**
	 * 
	 */
	private Map<String, Object> attributes;
	
	/**
	 * 
	 */
	private MyPortalBinaryContent binaryContent;
	
	/**
	 * 
	 */
	private String slug;
	
	
	/**
	 * 
	 */
	private String id;

	/**
	 * 
	 */
	private String idHash;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the idHash
	 */
	public String getIdHash() {
		return idHash;
	}

	/**
	 * @param idHash the idHash to set
	 */
	public void setIdHash(String idHash) {
		this.idHash = idHash;
	}
	
	
	/**
	 * @return the ipa
	 */
	public String getIpa() {
		return ipa;
	}

	/**
	 * @param ipa the ipa to set
	 */
	public void setIpa(String ipa) {
		this.ipa = ipa;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the parent
	 */
	public String getParent() {
		return parent;
	}

	/**
	 * @param parente the parent to set
	 */
	public void setParent(String parent) {
		this.parent = parent;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the createdAt
	 */
	public Date getCreatedAt() {
		return createdAt;
	}

	/**
	 * @param createdAt the createdAt to set
	 */
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	/**
	* @param firstPublishedAt the firstPublishedAt to set
	 */
	public void setFirstPublishedAt(Date firstPublishedAt) {
		this.firstPublishedAt=firstPublishedAt;
	}
	/**
		* @return the firstPublishedAt
	 */

	public Date getFirstPublishedAt() {
		return firstPublishedAt;
	}

	/**
	 * @return the modifiedAt
	 */
	public Date getModifiedAt() {
		return modifiedAt;
	}

	/**
	 * @param modifiedAt the modifiedAt to set
	 */
	public void setModifiedAt(Date modifiedAt) {
		this.modifiedAt = modifiedAt;
	}
	
	/**
	 * @return the status
	 */
	public MyPortalContentStatus getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(MyPortalContentStatus status) {
		this.status = status;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}
	
	/**
	 * @return
	 */
	public MyPortalBinaryContent getBinaryContent() {
		return binaryContent;
	}

	/**
	 * @param binaryContent
	 */
	public void setBinaryContent(MyPortalBinaryContent binaryContent) {
		this.binaryContent = binaryContent;
	}

	/**
	 * @return
	 */
	public String getSlug() {
		return slug;
	}

	/**
	 * @param slug
	 */
	public void setSlug(String slug) {
		this.slug = slug;
	}

	
	
}
