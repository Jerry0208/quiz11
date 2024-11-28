package quiz11.vo;

import java.time.LocalDate;

public class SearchReq {

	private String name;

	private LocalDate StartDate;

	private LocalDate endDate;

	public SearchReq() {
		super();
	}

	public SearchReq(String name, LocalDate startDate, LocalDate endDate) {
		super();
		this.name = name;
		this.StartDate = startDate;
		this.endDate = endDate;
	}

	public String getName() {
		return name;
	}

	public LocalDate getStartDate() {
		return StartDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}
}
