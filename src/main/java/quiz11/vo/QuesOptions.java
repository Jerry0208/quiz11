package quiz11.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuesOptions {

	private String option;

	@JsonProperty("option_number")
	private int optionNumber;

	public QuesOptions() {
		super();
	}

	public QuesOptions(String option, int optionNumber) {
		super();
		this.option = option;
		this.optionNumber = optionNumber;
	}

	public String getOption() {
		return option;
	}

	public int getOptionNumber() {
		return optionNumber;
	}

}
