package quiz11;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import quiz11.repository.FeedbackDao;
import quiz11.vo.FeedbackDto;

@SpringBootTest
public class FeebackDtoTest {

	@Autowired
	private FeedbackDao feedbackDao;
	
	@Test
	public void feedbackDtoTest() {
		List<FeedbackDto> test = feedbackDao.getFeedBackByQuizId(6);
	}
}
