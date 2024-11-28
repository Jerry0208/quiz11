package quiz11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import quiz11.entity.Feedback;
import quiz11.entity.FeedbackId;

@Repository
public interface FeedbackDao extends JpaRepository<Feedback, FeedbackId>{

}
