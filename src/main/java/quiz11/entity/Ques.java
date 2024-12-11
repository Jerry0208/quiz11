package quiz11.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import quiz11.constants.MsgConstants;

@Entity
@Table(name = "ques")
@IdClass(value = QuesId.class)
public class Ques {

	@Id
	@JsonProperty("quiz_id")
	@Column(name = "quiz_id")
	private int quizId;

	@Min(value = 1, message = MsgConstants.QUES_ID_ERROR) // quesID 必須大於等於 1
	@Id
	@JsonProperty("ques_id")
	@Column(name = "ques_id")
	private int quesId;

	@NotBlank(message = MsgConstants.QUES_NAME_ERROR) // 不能為 null、空字串、空白字串
	@JsonProperty("ques_name")
	@Column(name = "ques_name")
	private String quesName;

	@NotBlank(message = MsgConstants.QUES_TYPE_ERROR) // 不能為 null、空字串、空白字串
	@Column(name = "type")
	private String type;

	@Column(name = "required")
	private boolean required;

	@Column(name = "options")
	private String options;

	public Ques() {
		super();
	}

	public Ques(int quizId, int quesId, String quesName, String type, boolean required, String options) {
		super();
		this.quizId = quizId;
		this.quesId = quesId;
		this.quesName = quesName;
		this.type = type;
		this.required = required;
		this.options = options;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public int getQuizId() {
		return quizId;
	}

	public int getQuesId() {
		return quesId;
	}

	public String getQuesName() {
		return quesName;
	}

	public String getType() {
		return type;
	}

	public boolean isRequired() {
		return required;
	}

	public String getOptions() {
		return options;
	}

}
