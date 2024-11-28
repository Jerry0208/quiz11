package quiz11.vo;

public class BasicRes {

	private int code;

	private String massage;

	public BasicRes() {
		super();
	}

	public BasicRes(int code, String massage) {
		super();
		this.code = code;
		this.massage = massage;
	}

	public int getCode() {
		return code;
	}

	public String getMassage() {
		return massage;
	}

}
