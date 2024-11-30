package quiz11;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import quiz11.entity.Ques;
import quiz11.service.ifs.QuizService;
import quiz11.vo.GetQuesReq;
import quiz11.vo.GetQuesRes;
import quiz11.vo.QuesOptions;

@SpringBootTest
public class QuizServiceTest {

	@Autowired
	QuizService quizservice;

	@Test // ���ճz�L�ݨ� id �j���ҥH�ݨ����D���e
	public void getQuesTest() {
		ObjectMapper mapper = new ObjectMapper();
		GetQuesRes res = quizservice.getQues(new GetQuesReq(4));
		System.out.println(res.getCode() + res.getMassage());
		for (Ques item : res.getQues()) {
			System.out.printf("�ݨ�id : %d, ���Did : %d, ���D�W��: %s, �ﶵ�����N��: %s, �O�_����: %b, �ﶵ���e: %s \n", item.getQuizId(),
					item.getQuesId(), item.getQuesName(), item.getType(), item.isRequired(), item.getOptions());
			String quesStr = item.getOptions();
			// �N�s�b DB ������ƫ��A�� String �� JSON�榡�ﶵ���e�A�z�L mapper.readValue �z�JQuesOptions�� 
			try {
				List<QuesOptions> optionList = mapper.readValue(quesStr, new TypeReference<>() {
				});
				for(QuesOptions option : optionList) {
					System.out.println(option.getOptionNumber() + " " + option.getOption());
				}
			} catch (JsonProcessingException e) {
				System.out.println("����");
				e.printStackTrace();
			}
		}
	}
}
