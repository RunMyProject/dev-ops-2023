package myspring.checkmime.config;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import myspring.checkmime.model.CheckmimeUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CheckmimeUserDetails implements UserDetails {

	private CheckmimeUser chechmimeUser;

	public CheckmimeUser getCheckmimeUser() {
		return chechmimeUser;
	}

	public boolean isLogged() {
		return chechmimeUser.isLogged();
	}

	public CheckmimeUserDetails(CheckmimeUser chechmimeUser) {
		this.chechmimeUser = chechmimeUser;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return null;
	}

	@Override
	public String getUsername() {
		return chechmimeUser.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return false;
	}

	@Override
	public boolean isAccountNonLocked() {
		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return false;
	}

	@Override
	public boolean isEnabled() {
		return false;
	}
}
