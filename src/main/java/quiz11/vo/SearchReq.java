package quiz11.vo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SearchReq {

	private String name;

	@JsonProperty("start_date")
	private LocalDate startDate;

	@JsonProperty("end_date")
	private LocalDate endDate;

	private String status;

	@JsonProperty("is_admin")
	private boolean AdminMode;

	public SearchReq() {
		super();
	}

	public SearchReq(String name, LocalDate startDate, LocalDate endDate, String status, boolean AdminMode) {
		super();
		this.name = name;
		this.startDate = startDate;
		this.endDate = endDate;
		this.status = status;
		this.AdminMode = AdminMode;
	}

	public String getName() {
		return name;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public String getStatus() {
		return status;
	}

	public boolean isAdminMode() {
		return AdminMode;
	}

}
