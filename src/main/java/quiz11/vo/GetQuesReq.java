package quiz11.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetQuesReq {

	@JsonProperty("quiz_id")
	private int quizId;

	public GetQuesReq() {
		super();
	}

	public GetQuesReq(int quizId) {
		super();
		this.quizId = quizId;
	}

	public int getQuizId() {
		return quizId;
	}

}
