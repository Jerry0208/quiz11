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

	@Test // 測試透過問卷 id 搜索所以問卷問題內容
	public void getQuesTest() {
		ObjectMapper mapper = new ObjectMapper();
		GetQuesRes res = quizservice.getQues(new GetQuesReq(4));
		System.out.println(res.getCode() + res.getMassage());
		for (Ques item : res.getQues()) {
			System.out.printf("問卷id : %d, 問題id : %d, 問題名稱: %s, 選項類型代號: %s, 是否必填: %b, 選項內容: %s \n", item.getQuizId(),
					item.getQuesId(), item.getQuesName(), item.getType(), item.isRequired(), item.getOptions());
			String quesStr = item.getOptions();
			// 將存在 DB 中的資料型態為 String 的 JSON格式選項內容，透過 mapper.readValue 透入QuesOptions中 
			try {
				List<QuesOptions> optionList = mapper.readValue(quesStr, new TypeReference<>() {
				});
				for(QuesOptions option : optionList) {
					System.out.println(option.getOptionNumber() + " " + option.getOption());
				}
			} catch (JsonProcessingException e) {
				System.out.println("不行");
				e.printStackTrace();
			}
		}
	}
}
