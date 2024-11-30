package quiz11.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GetQuesReq {

	@JsonProperty("quiz_id")
	private int QuizId;

	public GetQuesReq() {
		super();
	}

	public GetQuesReq(int quizId) {
		super();
		QuizId = quizId;
	}

	public int getQuizId() {
		return QuizId;
	}

}
