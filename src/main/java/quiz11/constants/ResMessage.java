package quiz11.constants;

public enum ResMessage {

	// �榡: ���j�g�^��(code, message);
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

	// �ݭn���غc��k�M get ��k�A set �N����
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
