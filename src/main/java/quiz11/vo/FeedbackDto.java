package quiz11.vo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FeedbackDto {

	@JsonProperty("quiz_id")
	private int quizId;

	@JsonProperty("fillin_date")
	private LocalDate fliinDate;

	@JsonProperty("quiz_name")
	private String quizName;

	@JsonProperty("description")
	private String quizDesc;

	@JsonProperty("user_name")
	private String userName;

	private String phone;

	private String email;

	private int age;

	@JsonProperty("ques_id")
	private int quesId;

	@JsonProperty("ques_name")
	private String quesName;

	@JsonProperty("answer_str")
	private String answerStr;

	public FeedbackDto() {
		super();
	}

	public FeedbackDto(int quizId, LocalDate fliinDate, String quizName, String quizDesc, String userName, String phone,
			String email, int age, int quesId, String quesName, String answerStr) {
		super();
		this.quizId = quizId;
		this.fliinDate = fliinDate;
		this.quizName = quizName;
		this.quizDesc = quizDesc;
		this.userName = userName;
		this.phone = phone;
		this.email = email;
		this.age = age;
		this.quesId = quesId;
		this.quesName = quesName;
		this.answerStr = answerStr;
	}

	public int getQuizId() {
		return quizId;
	}

	public LocalDate getFliinDate() {
		return fliinDate;
	}

	public String getQuizName() {
		return quizName;
	}

	public String getQuizDesc() {
		return quizDesc;
	}

	public String getUserName() {
		return userName;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public int getAge() {
		return age;
	}

	public int getQuesId() {
		return quesId;
	}

	public String getQuesName() {
		return quesName;
	}

	public String getAnswerStr() {
		return answerStr;
	}

}
