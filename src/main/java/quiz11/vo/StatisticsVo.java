package quiz11.vo;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatisticsVo {

	@JsonProperty("quiz_name")
	private String quizName;

	@JsonProperty("ques_id")
	private int quesId;

	@JsonProperty("ques_name")
	private String quesName;

	// 選項名稱 次數
	@JsonProperty("option_count_map")
	private Map<String, Integer> optionCountMap;

	public StatisticsVo() {
		super();
	}

	public StatisticsVo(String quizName, int quesId, String quesName, Map<String, Integer> optionCountMap) {
		super();
		this.quizName = quizName;
		this.quesId = quesId;
		this.quesName = quesName;
		this.optionCountMap = optionCountMap;
	}

	public String getQuizName() {
		return quizName;
	}

	public int getQuesId() {
		return quesId;
	}

	public String getQuesName() {
		return quesName;
	}

	public Map<String, Integer> getOptionCountMap() {
		return optionCountMap;
	}

	public void setQuizName(String quizName) {
		this.quizName = quizName;
	}

	public void setQuesId(int queId) {
		this.quesId = queId;
	}

	public void setQuesName(String quesName) {
		this.quesName = quesName;
	}

	public void setOptionCountMap(Map<String, Integer> optionCountMap) {
		this.optionCountMap = optionCountMap;
	}

}
