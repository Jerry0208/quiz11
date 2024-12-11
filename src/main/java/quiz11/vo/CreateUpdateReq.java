package quiz11.vo;

import java.time.LocalDate;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;

import quiz11.constants.MsgConstants;
import quiz11.entity.Ques;
import quiz11.entity.Quiz;

public class CreateUpdateReq extends Quiz {

	// 1張問卷應該會有很多題
	@Valid // 要往下驗證類別的內容時，需要下這個註解
	@NotEmpty(message = MsgConstants.QUES_IS_EMPTY) // 不得為空陣列
	@JsonProperty("ques_list")
	private List<Ques> quesLsit;

	public CreateUpdateReq() {
		super();
	}

	public CreateUpdateReq(int id, String name, String description, LocalDate startDate, LocalDate endDate, boolean published,
			List<Ques> quesLsit) {
		super(id, name, description, startDate, endDate, published);
		this.quesLsit = quesLsit;
	}

	public CreateUpdateReq(String name, String description, LocalDate startDate, LocalDate endDate, boolean published,
			List<Ques> quesLsit) {
		super(name, description, startDate, endDate, published);
		this.quesLsit = quesLsit;
	}

	public List<Ques> getQuesLsit() {
		return quesLsit;
	}
	

}
