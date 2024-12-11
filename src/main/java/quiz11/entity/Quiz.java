package quiz11.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import quiz11.constants.MsgConstants;

@Entity
@Table(name = "quiz")
public class Quiz {

	// �]�� PK �O AI(Auto Incremental)�A�ҥH�n�[�W�� @GeneratedValue
	// strategy: �����O AI ���ͦ�����
	// GenerationType.IDENTITY: �N�� PK �Ʀr�Ѹ�Ʈw�Ӧ۰ʼW�[
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id")
	private int id;

	@NotBlank(message = MsgConstants.QUIZ_PARAM_ERROR_MSG) // �ƾ�����:���ର null�B�Ŧr��B�ťզr��(message Ĳ�k����ɷ|�^���T��)
	@Column(name = "name")
	private String name;

	@NotBlank(message = MsgConstants.QUIZ_PARAM_ERROR_MSG) // �ƾ�����:���ର null�B�Ŧr��B�ťզr��(message Ĳ�k����ɷ|�^���T��)
	@Column(name = "description")
	private String description;

	@FutureOrPresent(message = MsgConstants.DATE_ERROR) // �ƾ����� : �}�l�ɶ��o�O���ѩάO���� 
	@JsonProperty("start_date")
	@Column(name = "start_date")
	private LocalDate startDate = LocalDate.now(); //���w�]��
	
	@FutureOrPresent(message = MsgConstants.DATE_ERROR) // �ƾ����� : �����ɶ��o�O���ѩάO���� 
	@JsonProperty("end_date")
	@Column(name = "end_date")
	private LocalDate endDate = LocalDate.now();

	@JsonProperty("is_published")
	@Column(name = "published")
	private boolean published;

	public Quiz() {
		super();
	}

	public Quiz(String name, String description, LocalDate startDate, LocalDate endDate, boolean published) {
		super();
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.published = published;
	}

	public Quiz(int id, String name, String description, LocalDate startDate, LocalDate endDate, boolean published) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.published = published;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public boolean isPublished() {
		return published;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

}
