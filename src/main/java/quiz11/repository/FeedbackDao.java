package quiz11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import quiz11.entity.Feedback;
import quiz11.entity.FeedbackId;

@Repository
public interface FeedbackDao extends JpaRepository<Feedback, FeedbackId>{

//	@Query(value = "select 1 from feedback where quiz_id = ?1 and email = ?2", nativeQuery = true)
	public boolean existsByQuizIdAndEmail(int quizId, String email); // <-- JPA ¼gªk
}
