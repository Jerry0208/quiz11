package quiz11.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StatisticsRes extends BasicRes {

	@JsonProperty("statistics_list")
	private List<StatisticsVo> statisticsVoList;

	public StatisticsRes() {
		super();
	}

	public StatisticsRes(int code, String massage) {
		super(code, massage);
	}

	public StatisticsRes(int code, String massage, List<StatisticsVo> statisticsVoList) {
		super(code, massage);
		this.statisticsVoList = statisticsVoList;
	}

	public List<StatisticsVo> getStatisticsVoList() {
		return statisticsVoList;
	}

}
