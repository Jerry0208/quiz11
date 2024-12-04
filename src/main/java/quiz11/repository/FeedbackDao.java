package quiz11.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import quiz11.entity.Feedback;
import quiz11.entity.FeedbackId;
import quiz11.vo.FeedbackDto;
import quiz11.vo.StatisticsDto;

@Repository
public interface FeedbackDao extends JpaRepository<Feedback, FeedbackId> {

//	@Query(value = "select 1 from feedback where quiz_id = ?1 and email = ?2", nativeQuery = true)
	public boolean existsByQuizIdAndEmail(int quizId, String email); // <-- JPA 寫法

	// 取得同一張問卷所有人的回答
	@Query(value = "select new quiz11.vo.FeedbackDto( "//
			+ " qz.id, fb.fillinDate, qz.name, qz.description, fb.userName, "
			+ " fb.phone, fb.email, fb.age, qu.quesId, qu.quesName, fb.answer) "//
			+ " from Quiz as qz "//
			+ " join Ques as qu on qz.id = qu.quizId"//
			+ " join Feedback as fb on qz.id = fb.quizId and qu.quesId = fb.quesId"
			+ " where qz.id = ?1 ", nativeQuery = false)
	public List<FeedbackDto> getFeedBackByQuizId(int quizId);

	@Query(value = "select new quiz11.vo.StatisticsDto( "//
			+ " qz.name, qu.quesId, qu.quesName, qu.type, qu.options, fb.answer ) " //
			+ " from  Quiz as qz " //
			+ " join Ques as qu on qz.id = qu.quizId"//
			+ " join Feedback as fb on qz.id = fb.quizId and qu.quesId = fb.quesId"//
			+ " where qz.id = ?1", nativeQuery = false)
	public List<StatisticsDto> getStatisticsByQuizId(int quizIs);
}
