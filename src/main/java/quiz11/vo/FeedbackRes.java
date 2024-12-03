package quiz11.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedbackRes extends BasicRes {

	@JsonProperty("feedback_dto_list")
	private List<FeedbackDto> feedbackDtoList;

	public FeedbackRes() {
		super();
	}

	public FeedbackRes(int code, String massage) {
		super(code, massage);
	}

	public FeedbackRes(int code, String massage, List<FeedbackDto> feedbackDtoList) {
		super(code, massage);
		this.feedbackDtoList = feedbackDtoList;
	}

	public List<FeedbackDto> getFeedbackDtoList() {
		return feedbackDtoList;
	}

}
