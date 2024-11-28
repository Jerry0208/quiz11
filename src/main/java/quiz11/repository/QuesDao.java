package quiz11.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import quiz11.entity.Ques;
import quiz11.entity.QuesId;

@Repository
public interface QuesDao extends JpaRepository<Ques, QuesId>{

	@Transactional
	@Modifying
	@Query(value = "delete from ques Where quiz_id = :quiz_id", nativeQuery = true)
	public void deleteByQuizId(@Param("quiz_id") int quizId);
	
	@Transactional
	@Modifying
	@Query(value = "delete from ques where quiz_id in (:quiz_id)", nativeQuery = true)
	public void deleteByQuizIdIn(@Param("quiz_id") List<Integer> quizIdList);

}
