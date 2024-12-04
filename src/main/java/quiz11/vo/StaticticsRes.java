package quiz11.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class StaticticsRes extends BasicRes {

	@JsonProperty("statistics_list")
	private List<StatisticsVo> statisticsVoList;

	public StaticticsRes() {
		super();
	}

	public StaticticsRes(int code, String massage) {
		super(code, massage);
	}

	public StaticticsRes(int code, String massage, List<StatisticsVo> statisticsVoList) {
		super(code, massage);
		this.statisticsVoList = statisticsVoList;
	}

	public List<StatisticsVo> getStatisticsVoList() {
		return statisticsVoList;
	}

}
