package quiz11.vo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FillinReq {

	@JsonProperty("quiz_id")
	private int quizId;

	@JsonProperty("user_name")
	private String userName;

	private String phone;

	private String email;

	private int age;

	//           題號 		  選項
	private Map<Integer, List<String>> answer;

	@JsonProperty("fillin_date")
	private LocalDate fillinDate;

	public FillinReq() {
		super();
	}

	public FillinReq(int quizId, String userName, String phone, String email, int age,
			Map<Integer, List<String>> answer, LocalDate fillinDate) {
		super();
		this.quizId = quizId;
		this.userName = userName;
		this.phone = phone;
		this.email = email;
		this.age = age;
		this.answer = answer;
		this.fillinDate = fillinDate;
	}

	public int getQuizId() {
		return quizId;
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

	public Map<Integer, List<String>> getAnswer() {
		return answer;
	}

	public LocalDate getFillinDate() {
		return fillinDate;
	}

}
