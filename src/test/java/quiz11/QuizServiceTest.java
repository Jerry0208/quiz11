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
 * 此 Annotation 會在跑測試方法之前，先執行整個專案(從 main 方法進入執行)，並建立所有該託管的物件<br>
 * 測試方法中若有使用到 @Autowired，就要加上此 Annotation<br>
 * @SpringBootTest 只能用於 test 類別
 */
@SpringBootTest
public class QuizServiceTest {

	@Autowired
	private QuizService quizService;
	
	@Test
	public void createCheckQuizIdTest() {
		CreateUpdateReq req = new CreateUpdateReq(1, "問卷名稱", "問卷描述", LocalDate.now(),
				LocalDate.now(), true, new ArrayList<>());
		BasicRes res = quizService.create(req);
		Assert.isTrue(res.getMessage().equalsIgnoreCase(ResMessage.QUIZ_PARAM_ERROR.getMessage()),//
				"Check quiz_id fail!!");
	}

	@Test
	public void createCheckDateTest() {
		// check start date 不能比 end date 晚
		CreateUpdateReq req = new CreateUpdateReq("問卷名稱", "問卷描述", LocalDate.now().plusDays(1),
				LocalDate.now(), true, new ArrayList<>());
		BasicRes res = quizService.create(req);
		Assert.isTrue(res.getMessage().equalsIgnoreCase(ResMessage.DATE_ERROR.getMessage()),//
				"Check date fail!!");
	}
	
	@Test // 測試透過問卷 id 搜索所以問卷問題內容
	public void getQuesTest() {
		ObjectMapper mapper = new ObjectMapper();
		GetQuesRes res = quizService.getQues(new GetQuesReq(4));
		System.out.println(res.getCode() + res.getMessage());
		for (Ques item : res.getQues()) {
			System.out.printf("問卷id : %d, 問題id : %d, 問題名稱: %s, 選項類型代號: %s, 是否必填: %b, \n選項內容: %s \n", item.getQuizId(),
					item.getQuesId(), item.getQuesName(), item.getType(), item.isRequired(), item.getOptions());
			String quesStr = item.getOptions();
			// 將存在 DB 中的資料型態為 String 的 JSON格式選項內容，透過 mapper.readValue 放入 QuesOptions 中
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
