package myspring.checkmime.repository;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import myspring.checkmime.model.CheckmimeUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CheckmimeUserRepository extends JpaRepository<CheckmimeUser, Long> {

	Optional<CheckmimeUser> findByUsername(String username);
	Optional<CheckmimeUser> findByPassword(String password);
}