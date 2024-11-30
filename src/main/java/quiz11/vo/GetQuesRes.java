package quiz11.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import quiz11.entity.Ques;

public class GetQuesRes extends BasicRes {

	@JsonProperty("ques_list")
	private List<Ques> quesList;

	public GetQuesRes() {
		super();
	}

	public GetQuesRes(int code, String massage) {
		super(code, massage);
	}

	public GetQuesRes(int code, String massage, List<Ques> quesList) {
		super(code, massage);
		this.quesList = quesList;
	}

	public List<Ques> getQues() {
		return quesList;
	}

	
}
