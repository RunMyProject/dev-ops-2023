package myspring.checkmime.service;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import myspring.checkmime.config.CheckmimeUserDetails;
import myspring.checkmime.model.CheckmimeUser;
import myspring.checkmime.repository.CheckmimeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
@Service
public class CheckmimeUserService implements UserDetailsService {

	@Autowired
	private CheckmimeUserRepository repository;

	@Override
	public CheckmimeUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		CheckmimeUser checkmimeUser = repository
				.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No user found with username = " + username));

		if(checkmimeUser!=null) {
			return new CheckmimeUserDetails(checkmimeUser);
		}

		return null;
	}

	public void createUser(UserDetails user) {
		repository.save((CheckmimeUser) user);
	}

	public void updateLoginUser(CheckmimeUserDetails checkmimeUserDetails, boolean logged) {
		CheckmimeUser chechmimeUser = checkmimeUserDetails.getCheckmimeUser();
		chechmimeUser.setLogged(logged);
		repository.save(chechmimeUser);
	}

	public void loginUser(CheckmimeUserDetails user) {
		updateLoginUser(user, true);
	}

	public void logoutUser(CheckmimeUserDetails user) {
		updateLoginUser(user, false);
	}

	public void deleteUser(String username) {
		CheckmimeUser checkmimeUser = repository
				.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("No user found with username = " + username));
		repository.delete(checkmimeUser);
	}

	@Transactional
	public void changePassword(String oldPassword, String newPassword) {

		CheckmimeUser userDetails = repository.findByPassword(oldPassword)
				.orElseThrow(() -> new UsernameNotFoundException("Invalid password "));
		userDetails.setPassword(newPassword);
		repository.save(userDetails);
	}

	public boolean userExists(String username) {
		return repository.findByUsername(username).isPresent();
	}

}