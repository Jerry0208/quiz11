package quiz11.service.ifs;

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

public interface QuizService {

	public BasicRes create(CreateUpdateReq req);
	
	public BasicRes update(CreateUpdateReq req);
	
	public BasicRes delete(DeleteReq req);
	
	public SearchRes search(SearchReq req);
	
	public GetQuesRes getQues(GetQuesReq req);
	
	public BasicRes fillin(FillinReq req);
	
	public FeedbackRes feedback(int quizId);
	
	public StatisticsRes statistics(int quizId);
	
	public StatisticsRes statisticsAAA(int quizId);
}
