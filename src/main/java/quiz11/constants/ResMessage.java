package quiz11.constants;

public enum ResMessage {

	// 格式: 全大寫英文(code, message);
	SUCCESS(200, "Success!!"),//
	QUIZ_PARAM_ERROR(400, "Quiz param error!!"),//
	QUES_PARAM_ERROR(400, "Ques param error!!"),//
	QUES_TYPE_ERROR(400, "Ques type error!!"),//
	DATE_ERROR(400, "Date error!!"),//
	QUIZ_NOT_FOUND(404, "Quiz not found"),//
	QUIZ_ID_MISMATCH(400, "Quiz id mismatch"),//
	QUIZ_UPDATE_FAILED(400, "Quiz update failed");

	private int code;

	private String message;

	// 需要有建構方法和 get 方法， set 就不用
	private ResMessage(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

}
