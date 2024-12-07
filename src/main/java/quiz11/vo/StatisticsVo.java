package quiz11.vo;

import java.util.List;
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

	@JsonProperty("text_answer")
	private List<String> textAnswerList;

	public StatisticsVo() {
		super();
	}

	public StatisticsVo(String quizName, int quesId, String quesName, Map<String, Integer> optionCountMap,
			List<String> textAnswerList) {
		super();
		this.quizName = quizName;
		this.quesId = quesId;
		this.quesName = quesName;
		this.optionCountMap = optionCountMap;
		this.textAnswerList = textAnswerList;
	}

	public StatisticsVo( String quizName, int quesId, String quesName) {
		super();
		this.quesId = quesId;
		this.quizName = quizName;
		this.quesName = quesName;
	}

	public String getQuizName() {
		return quizName;
	}

	public void setQuizName(String quizName) {
		this.quizName = quizName;
	}

	public int getQuesId() {
		return quesId;
	}

	public void setQuesId(int quesId) {
		this.quesId = quesId;
	}

	public String getQuesName() {
		return quesName;
	}

	public void setQuesName(String quesName) {
		this.quesName = quesName;
	}

	public Map<String, Integer> getOptionCountMap() {
		return optionCountMap;
	}

	public void setOptionCountMap(Map<String, Integer> optionCountMap) {
		this.optionCountMap = optionCountMap;
	}

	public List<String> getTextAnswerList() {
		return textAnswerList;
	}

	public void setTextAnswerList(List<String> textAnswer) {
		this.textAnswerList = textAnswer;
	}

}
