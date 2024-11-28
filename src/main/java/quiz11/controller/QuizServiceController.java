package quiz11.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import quiz11.service.ifs.QuizService;
import quiz11.vo.BasicRes;
import quiz11.vo.CreateUpdateReq;
import quiz11.vo.DeleteReq;
import quiz11.vo.SearchReq;
import quiz11.vo.SearchRes;

@RestController
@RequestMapping("quiz/")
@CrossOrigin(origins = "http://localhost:4200") // 允許來自 http://localhost:4200 的api請求
public class QuizServiceController {

	@Autowired
	private QuizService quizService;

	@PostMapping(value = "create")
	public BasicRes create(@RequestBody CreateUpdateReq req) {
		return quizService.create(req);
	};

	@PostMapping(value = "update")
	public BasicRes update(@RequestBody CreateUpdateReq req) {
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

}
