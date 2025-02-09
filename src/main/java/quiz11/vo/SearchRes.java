package quiz11.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import quiz11.entity.Quiz;

public class SearchRes extends BasicRes {

	@JsonProperty("quiz_list")
	private List<Quiz> quizList;

	public SearchRes() {
		super();
	}

	public SearchRes(int code, String massage) {
		super(code, massage);
	}

	public SearchRes(int code, String massage, List<Quiz> quizList) {
		super(code, massage);
		this.quizList = quizList;
	}

	public List<Quiz> getQuizList() {
		return quizList;
	}

}
