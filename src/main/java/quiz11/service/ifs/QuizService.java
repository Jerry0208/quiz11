package quiz11.service.ifs;

import quiz11.vo.BasicRes;
import quiz11.vo.CreateUpdateReq;
import quiz11.vo.DeleteReq;
import quiz11.vo.SearchReq;
import quiz11.vo.SearchRes;

public interface QuizService {

	public BasicRes create(CreateUpdateReq req);
	
	public BasicRes update(CreateUpdateReq req);
	
	public BasicRes delete(DeleteReq req);
	
	public SearchRes search(SearchReq req);
}
