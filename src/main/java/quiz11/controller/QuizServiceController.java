package quiz11.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import quiz11.service.ifs.QuizService;
import quiz11.vo.BasicRes;
import quiz11.vo.CreateUpdateReq;
import quiz11.vo.DeleteReq;
import quiz11.vo.FeedbackRes;
import quiz11.vo.FillinReq;
import quiz11.vo.GetQuesReq;
import quiz11.vo.GetQuesRes;
import quiz11.vo.SearchReq;
import quiz11.vo.SearchRes;
import quiz11.vo.StatisticsRes;

@RestController
@RequestMapping("quiz/")
@CrossOrigin(origins = "http://localhost:4200") // 允許來自 http://localhost:4200 的api請求
public class QuizServiceController {

	@Autowired
	private QuizService quizService;

	@PostMapping(value = "create")
	public BasicRes create(@Valid @RequestBody CreateUpdateReq req) {
		return quizService.create(req);
	};

	@PostMapping(value = "update")
	public BasicRes update(@Valid @RequestBody CreateUpdateReq req) {
		return quizService.update(req);
	}

	@PostMapping(value = "delete")
	public BasicRes delete(@RequestBody DeleteReq req) {
		return quizService.delete(req);
	}

	@PostMapping(value = "search")
	public SearchRes search(@RequestBody SearchReq req) {
		return quizService.search(req);
	};
	
	@PostMapping(value = "get_ques")
	public GetQuesRes getQues(@RequestBody GetQuesReq req) {
		return quizService.getQues(req);
	};
	
	@PostMapping(value = "fillin")
	public BasicRes fillin(@Valid @RequestBody FillinReq req) {
		return quizService.fillin(req);
	}
	
	@GetMapping(value = "feedback") // http://localhost:8080/quiz/feedback ? quizId = 問卷id
	public FeedbackRes feedback(@RequestParam int quizId) {
		return quizService.feedback(quizId);
	}
	
	@GetMapping(value = "statistics")
	public StatisticsRes statistics(@RequestParam int quizId) {
		return quizService.statistics(quizId);
	}

	@GetMapping(value = "statisticsAAA")
	public StatisticsRes statisticsAAA(@RequestParam int quizId) {
		return quizService.statisticsAAA(quizId);
	}

}
