package myspring.checkmime.repository;

import java.util.List;

import myspring.checkmime.model.Checkmime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckmimeRepository extends JpaRepository<Checkmime, Long> {
		List<Checkmime> findByFormatContaining(String format);
		List<Checkmime> findByEnabled(boolean enabled);
}