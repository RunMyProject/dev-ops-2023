package myspring.checkmime.service;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import myspring.checkmime.model.CheckmimeUser;
import myspring.checkmime.repository.CheckmimeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CheckmimeAuthenticationManager implements AuthenticationManager {

	@Autowired
	CheckmimeUserRepository checkmimeUserRepository;
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		String username = authentication.getPrincipal() + "";

		String password = authentication.getCredentials() + "";

		CheckmimeUser checkmimeUser = checkmimeUserRepository
				.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No user found with username = " + username));

		if (checkmimeUser == null) {
			throw new BadCredentialsException("1000");
		}

		if(!checkmimeUser.getPassword().equals(password)) {
			throw new BadCredentialsException("1000");
		}

		/*
		if (!encoder.matches(password, user.getPassword())) {
			throw new BadCredentialsException("1000");
		}
		*/

		/*
		if (checkmimeUser.isDisabled()) {
			throw new DisabledException("1001");
		}
		 */

		// List<Right> userRights = rightRepo.getUserRights(username);

		return new UsernamePasswordAuthenticationToken(username, null,
		//		userRights.stream().map(x -> new SimpleGrantedAuthority(x.getName())).collect(Collectors.toList())
				null
		);
	}
}
