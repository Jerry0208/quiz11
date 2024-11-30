package quiz11.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteReq {

	@JsonProperty("quiz_id_list")
	private List<Integer> quizIdList;

	public DeleteReq() {
		super();
	}

	public DeleteReq(List<Integer> quizIdList) {
		super();
		this.quizIdList = quizIdList;
	}

	public List<Integer> getQuizIdList() {
		return quizIdList;
	}

}
