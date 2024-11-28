package quiz11.repository;

import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import quiz11.entity.Quiz;
import quiz11.vo.SearchRes;

@Repository
public interface QuizDao extends JpaRepository<Quiz, Integer> {

	@Transactional
	@Modifying
	@Query(value = "delete from quiz where id in(?1)", nativeQuery = true)
	public void deleteByIdIn(List<Integer> idList);

	@Query(value = "select id, name, description, start_date, end_date, published from quiz"//
			+ " where name like %?1% and start_date >= ?2 and end_date <= ?3", nativeQuery = true)
	public List<Quiz> getByConditions(String name, LocalDate startDate, LocalDate endDate);

}