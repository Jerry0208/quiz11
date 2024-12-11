package quiz11;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import quiz11.constants.ResMessage;
import quiz11.entity.Ques;
import quiz11.service.ifs.QuizService;
import quiz11.vo.BasicRes;
import quiz11.vo.CreateUpdateReq;
import quiz11.vo.GetQuesReq;
import quiz11.vo.GetQuesRes;
import quiz11.vo.QuesOptions;

/**
 * �� Annotation �|�b�]���դ�k���e�A�������ӱM��(�q main ��k�i�J����)�A�ëإߩҦ��ӰU�ު�����<br>
 * ���դ�k���Y���ϥΨ� @Autowired�A�N�n�[�W�� Annotation<br>
 * @SpringBootTest �u��Ω� test ���O
 */
@SpringBootTest
public class QuizServiceTest {

	@Autowired
	private QuizService quizService;
	
	@Test
	public void createCheckQuizIdTest() {
		CreateUpdateReq req = new CreateUpdateReq(1, "�ݨ��W��", "�ݨ��y�z", LocalDate.now(),
				LocalDate.now(), true, new ArrayList<>());
		BasicRes res = quizService.create(req);
		Assert.isTrue(res.getMessage().equalsIgnoreCase(ResMessage.QUIZ_PARAM_ERROR.getMessage()),//
				"Check quiz_id fail!!");
	}

	@Test
	public void createCheckDateTest() {
		// check start date ����� end date ��
		CreateUpdateReq req = new CreateUpdateReq("�ݨ��W��", "�ݨ��y�z", LocalDate.now().plusDays(1),
				LocalDate.now(), true, new ArrayList<>());
		BasicRes res = quizService.create(req);
		Assert.isTrue(res.getMessage().equalsIgnoreCase(ResMessage.DATE_ERROR.getMessage()),//
				"Check date fail!!");
	}
	
	@Test // ���ճz�L�ݨ� id �j���ҥH�ݨ����D���e
	public void getQuesTest() {
		ObjectMapper mapper = new ObjectMapper();
		GetQuesRes res = quizService.getQues(new GetQuesReq(4));
		System.out.println(res.getCode() + res.getMessage());
		for (Ques item : res.getQues()) {
			System.out.printf("�ݨ�id : %d, ���Did : %d, ���D�W��: %s, �ﶵ�����N��: %s, �O�_����: %b, \n�ﶵ���e: %s \n", item.getQuizId(),
					item.getQuesId(), item.getQuesName(), item.getType(), item.isRequired(), item.getOptions());
			String quesStr = item.getOptions();
			// �N�s�b DB ������ƫ��A�� String �� JSON�榡�ﶵ���e�A�z�L mapper.readValue ��J QuesOptions ��
			try {
				List<QuesOptions> optionList = mapper.readValue(quesStr, new TypeReference<>() {
				});
				for (QuesOptions option : optionList) {
					System.out.println(option.getOptionNumber() + " " + option.getOption());
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		}
	}

}
