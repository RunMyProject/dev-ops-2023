package myspring.checkmime;

/*
 **********************
 * Checkmime
 * by Edoardo Sabatini
 * @2023
 **********************
 */

import myspring.checkmime.model.Checkmime;
import myspring.checkmime.repository.CheckmimeRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CheckmimeApplication.class)
class CheckmimeApplicationTests {

	@Autowired
	CheckmimeRepository checkmimeRepository;

	@Test
	void contextLoads() {

	}

	@Test
	@Sql(scripts={"/import_checkmime_table.sql"})
	public void givenCheckmimeRepository_whenRetreiveCheckmimeList_thenOK() {

		List<Checkmime> checkmimeList = new ArrayList<>();
		checkmimeRepository.findAll().forEach(checkmimeList::add);

		assertNotNull(checkmimeList);
		assertTrue(!checkmimeList.isEmpty());
		assertTrue(checkmimeList.size()==4);
	}
}