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
package it.regioneveneto.myp3.myextranet.security;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserWithAdditionalInfo implements UserDetails {

	private final String username;
	private final String nome;
	private final String cognome;
	private final String codiceFiscale;
	private final String email;
	private final String telefono;

	private final Long userId;

	private final Collection<GrantedAuthority> authorities = Collections.emptyList();
	
	private List<Acl> acls;

	public UserWithAdditionalInfo(String username, String nome, String cognome, String codiceFiscale, String email,
			String telefono, Long userId) {
		super();
		this.username = username;
		this.nome = nome;
		this.cognome = cognome;
		this.codiceFiscale = codiceFiscale;
		this.email = email;
		this.telefono = telefono;
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public String getNome() {
		return nome;
	}

	public String getCognome() {
		return cognome;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public String getEmail() {
		return email;
	}

	public String getTelefono() {
		return telefono;
	}

	public Long getUserId() {
		return userId;
	}

	public List<Acl> getAcls() {
		return acls;
	}

	public void setAcls(List<Acl> acls) {
		this.acls = acls;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return "$2a$10$Y8zuzAtbwHbVacVLLBK/ve3Fb0veCvNBJppMUG9XtOLky5SaGLyHq";
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
