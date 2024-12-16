package quiz11.controller;

import java.time.LocalDate;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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
		// 因為 service 中有使用 cache，所以必須要先確認 req 中的參數的值都不是 null
		// 檢視條件
		String name = req.getName();
		// 如果 name = null或空字串或全空白字串，一律都轉成空字串
		if (!StringUtils.hasText(name)) {
			name = "";
			// 把值 set 回 req
			req.setName(name);
		}
		// 若沒有開始日期條件，將日期轉成很早的時間
		LocalDate startDate = req.getStartDate();
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1);
			// 把值 set 回 req
			req.setStartDate(startDate);;
		}
		// 若沒有開始日期條件，將日期轉成很久的未來時間
		LocalDate endDate = req.getEndDate();
		if (endDate == null) {
			endDate = LocalDate.of(9999, 12, 31);
			// 把值 set 回 req
			req.setEndDate(endDate);
			
		}
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
