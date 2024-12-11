package quiz11.constants;

public enum ResMessage {

	// 格式: 全大寫英文(code, message);
	SUCCESS(200, "Success!!"),//
	QUIZ_PARAM_ERROR(400, "Quiz param error!!"),//
	QUES_PARAM_ERROR(400, "Ques param error!!"),//
	QUES_TYPE_ERROR(400, "Ques type error!!"),//
	DATE_ERROR(400, "Date error!!"),//
	QUIZ_NOT_FOUND(404, "Quiz not found!!"),//
	QUESTION_NOT_FOUND(404, "Question not found!!"),//
	QUIZ_ID_MISMATCH(400, "Quiz id mismatch!!"),//
	QUIZ_UPDATE_FAILED(400, "Quiz update failed!!"),//
	QUIZ_ID_ERROR(400, "Quiz id error!!"),//
	USERNAME_AND_EMAIL_REQUIRED(400, "Username and email required!!"),//
	AGE_ABOVE_12(400, "Age above 12!!"),//
	ANSWER_REQUIRED(400, "Answer required!!"),//
	DATE_RANGE_ERROR(400, "Date range error!!"),//
	ONE_OPTION_IS_ALLOWED(400, "One option is allowed!!"),//
	OPTIONS_TRANSFER_ERROR(400, "Options transger error!!"),//
	OPTION_ANSWER_MISMATCH(400, "Option answer mismatch!!"),//
	EMAIL_DUPLICATED(400, "Email duplicated!!");

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

//	public static class Constants{
//		public static final String QUIZ_PARAM_ERROR_MSG = "";
//	}
}
