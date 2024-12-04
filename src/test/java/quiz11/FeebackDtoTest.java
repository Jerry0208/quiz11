package quiz11;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import quiz11.repository.FeedbackDao;
import quiz11.service.ifs.QuizService;
import quiz11.vo.FeedbackDto;
import quiz11.vo.StaticticsRes;
import quiz11.vo.StatisticsDto;
import quiz11.vo.StatisticsVo;

@SpringBootTest
public class FeebackDtoTest {

	@Autowired
	private FeedbackDao feedbackDao;

	@Autowired
	private QuizService quizService;

	@Test
	public void feedbackDtoTest() {
		List<FeedbackDto> test = feedbackDao.getFeedBackByQuizId(6);
	}

	@Test
	public void getStatisticsByQuizIdTest() {
		List<StatisticsDto> res = feedbackDao.getStatisticsByQuizId(6);
		System.out.println(res.size());
	}

	@Test
	public void statistics() {
		quizService.statistics(6);
	}

}
