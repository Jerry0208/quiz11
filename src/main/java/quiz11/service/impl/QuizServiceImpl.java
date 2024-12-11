package quiz11.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import quiz11.constants.QuesType;
import quiz11.constants.ResMessage;
import quiz11.entity.Feedback;
import quiz11.entity.Ques;
import quiz11.entity.Quiz;
import quiz11.repository.FeedbackDao;
import quiz11.repository.QuesDao;
import quiz11.repository.QuizDao;
import quiz11.service.ifs.QuizService;
import quiz11.vo.BasicRes;
import quiz11.vo.CreateUpdateReq;
import quiz11.vo.DeleteReq;
import quiz11.vo.FeedbackRes;
import quiz11.vo.FillinReq;
import quiz11.vo.GetQuesReq;
import quiz11.vo.GetQuesRes;
import quiz11.vo.QuesOptions;
import quiz11.vo.SearchReq;
import quiz11.vo.SearchRes;
import quiz11.vo.StatisticsRes;
import quiz11.vo.StatisticsDto;
import quiz11.vo.StatisticsVo;

@Service
public class QuizServiceImpl implements QuizService {

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private QuesDao quesDao;

	@Autowired
	private FeedbackDao feedbackDao;

	@Transactional
	@Override
	public BasicRes create(CreateUpdateReq req) {
		// 參數檢查
		BasicRes checkResult = checkParams(req, true);
		if (checkResult != null) {
			return checkResult;
		}
		// 因為quiz 的 pk 是流水號，不會重複寫入，所以不用檢查 DB 是否已存在相同的 PK
		// 新增問卷
		// 因為 Quiz 中的 id 是 AI 自動生成的流水號，要讓 quizDao 執行 save 後可以把該 id 的值回傳，
		// 必須要在 Quiz 此 Entity 中將資料型態為 int 的屬性 id
		// 加上 @GeneratedValue(strategy = GenerationType.IDENTITY)
		Quiz quizRes = quizDao.save(new Quiz(req.getName(), req.getDescription(), //
				req.getStartDate(), req.getEndDate(), req.isPublished()));

		// 將 quiz 中的 id 加入到 Ques
		int quizId = quizRes.getId();
		for (Ques item : req.getQuesLsit()) {
			item.setQuizId(quizId);
		}

		// 新增問題
		quesDao.saveAll(req.getQuesLsit());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// 參數檢查
	private BasicRes checkParams(CreateUpdateReq req, boolean isCreateQuiz) {
		// 檢查新增問卷時，id 要為 0 ， checkQuizId 新增:true 更新:false
		if (req.getId() != 0 && isCreateQuiz) {
			return new BasicRes(ResMessage.QUIZ_PARAM_ERROR.getCode(), ResMessage.QUIZ_PARAM_ERROR.getMessage());
		}

		// 更新問卷時 Id 不得為 0 ， checkQuizId 新增:true 更新:false
		if (req.getId() == 0 && !isCreateQuiz) {
			return new BasicRes(ResMessage.QUIZ_PARAM_ERROR.getCode(), ResMessage.QUIZ_PARAM_ERROR.getMessage());
		}


		// 註解的程式碼已在 req 中使用 @valid 做資料檢查
		
		// 檢查 問卷名 跟 問卷敘述 是否有填寫
//		if (!StringUtils.hasText(req.getName()) || !StringUtils.hasText(req.getDescription())) {
//			return new BasicRes(ResMessage.QUIZ_PARAM_ERROR.getCode(), ResMessage.QUIZ_PARAM_ERROR.getMessage());
//		}
		
		// 檢查時間、檢查開始時間不能比結束時間晚
		if (req.getStartDate() == null || req.getEndDate() == null || req.getStartDate().isAfter(req.getEndDate())) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		// 檢查開始時間不能今天比早(問卷的開始時間最晚為今天)
//		if (req.getStartDate().isBefore(LocalDate.now())) {
//			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
//		}

		// 檢查問卷是否有選項內容
		if (CollectionUtils.isEmpty(req.getQuesLsit())) {
			return new BasicRes(ResMessage.QUES_PARAM_ERROR.getCode(), ResMessage.QUES_PARAM_ERROR.getMessage());
		}

		// 檢查 Ques
		for (Ques item : req.getQuesLsit()) {
			// 題目 id 不能小於等於 0 ， 題目名稱不能不存在
//			if (item.getQuesId() <= 0 || !StringUtils.hasText(item.getQuesName())
//					|| !StringUtils.hasText(item.getType())) {
//				return new BasicRes(ResMessage.QUES_PARAM_ERROR.getCode(), ResMessage.QUES_PARAM_ERROR.getMessage());
//			}
			// 檢查題目類型: 單選(single)、 多選(multi)、文字(text)
			if (!QuesType.checkType(item.getType())) {
				return new BasicRes(ResMessage.QUES_TYPE_ERROR.getCode(), ResMessage.QUES_TYPE_ERROR.getMessage());
			}
			// 檢查非文字類型時，選項沒有值
			if (!item.getType().equalsIgnoreCase(QuesType.TEXT.toString()) && !StringUtils.hasText(item.getOptions())) {
				return new BasicRes(ResMessage.QUES_TYPE_ERROR.getCode(), ResMessage.QUES_TYPE_ERROR.getMessage());
			}
		}

		// 成功
		return null;
	}

	@Transactional
	@Override
	public BasicRes update(CreateUpdateReq req) {

		BasicRes checkResult = checkParams(req, false);
		if (checkResult != null) {
			return checkResult;
		}

		// 檢查 Ques 的 quiz_id 是否與 Quiz 的 id 相符
		int quizId = req.getId();
		for (Ques item : req.getQuesLsit()) {
			if (item.getQuizId() != quizId) {
				return new BasicRes(ResMessage.QUIZ_ID_MISMATCH.getCode(), ResMessage.QUIZ_ID_MISMATCH.getMessage());
			}
		}

		// 問卷可以更新的狀態: 1.未發布 2.已發布但尚未開始
		Optional<Quiz> op = quizDao.findById(req.getId());
		// 確認問卷是否存在
		if (op.isEmpty()) { // true 時表示資料不存在
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}

		// 取得資料庫中的待更新的問卷
		Quiz quiz = op.get();
		// 確認問卷是否可以進行更新
		// 尚未發布 : !quiz.isPublished()
		// 已發布但尚未開始 : quiz.isPublished() && req.getStartDate().isAfter(LocalDate.now())
		// 小括號分組用
		// 排除法: 所以整個邏輯式前面加 !
		if (!(!quiz.isPublished() || (quiz.isPublished() && req.getStartDate().isAfter(LocalDate.now())))) {
			return new BasicRes(ResMessage.QUIZ_UPDATE_FAILED.getCode(), ResMessage.QUIZ_UPDATE_FAILED.getMessage());
		}

		// 透過自訂義的建溝方法將 req 中的值 set 回從資料庫取出的 quiz 中
		quiz.setName(req.getName());
		quiz.setDescription(req.getDescription());
		quiz.setStartDate(req.getStartDate());
		quiz.setEndDate(req.getEndDate());
		quiz.setPublished(req.isPublished());

		// 更新問卷
		quizDao.save(quiz);

		// 先刪除原本的問卷選項
		quesDao.deleteByQuizId(req.getId());

		// 新增問題
		quesDao.saveAll(req.getQuesLsit());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public BasicRes delete(DeleteReq req) {
		// 刪問卷
		quizDao.deleteByIdIn(req.getQuizIdList());
		// 刪相同 問卷ID 的選項
		quesDao.deleteByQuizIdIn(req.getQuizIdList());
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public SearchRes search(SearchReq req) {

		// 檢視條件
		String name = req.getName();
		// 如果 name = null或空字串或全空白字串，一律都轉成空字串
		if (!StringUtils.hasText(name)) {
			name = "";
		}

		// 依狀態搜尋
		if (StringUtils.hasText(req.getStatus())) {
			if (req.getStatus().equalsIgnoreCase("進行中")) {
				return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
						quizDao.getInProgress(name, LocalDate.now()));
			}
			if (req.getStatus().equalsIgnoreCase("已結束")) {
				return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
						quizDao.getCompleted(name, LocalDate.now()));
			}
			if (req.getStatus().equalsIgnoreCase("尚未開始")) {
				return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
						quizDao.getNotStartedYet(name, LocalDate.now()));
			}
			if (req.getStatus().equalsIgnoreCase("尚未公布")) {
				return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
						quizDao.getNotYetAnnounced(name, LocalDate.now()));
			}
		}

		// 若沒有開始日期條件，將日期轉成很早的時間
		LocalDate startDate = req.getStartDate();
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1);
		}
		// 若沒有開始日期條件，將日期轉成很久的未來時間
		LocalDate endDate = req.getEndDate();
		if (endDate == null) {
			endDate = LocalDate.of(9999, 12, 31);
		}

		if (req.isAdminMode()) {
			return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
					quizDao.getByConditionsAll(name, startDate, endDate));
		}

		return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
				quizDao.getByConditions(name, startDate, endDate));
	}

	@Override
	public GetQuesRes getQues(GetQuesReq req) {
		if (req.getQuizId() <= 0) {
			return new GetQuesRes(ResMessage.QUES_PARAM_ERROR.getCode(), ResMessage.QUES_PARAM_ERROR.getMessage());
		}
		List<Ques> quesList = quesDao.getByQuizId(req.getQuizId());
		if (CollectionUtils.isEmpty(quesList)) {
			return new GetQuesRes(ResMessage.QUESTION_NOT_FOUND.getCode(), ResMessage.QUESTION_NOT_FOUND.getMessage());
		}

		return new GetQuesRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(), quesList);
	}

	@Override
	public BasicRes fillin(FillinReq req) {
		// 參數檢查
		// quiz id
		if (req.getQuizId() <= 0) {
			return new BasicRes(ResMessage.QUIZ_ID_ERROR.getCode(), ResMessage.QUIZ_ID_ERROR.getMessage());
		}

		// 使用者名稱、電子信箱
		if (!StringUtils.hasText(req.getUserName()) || !StringUtils.hasText(req.getEmail())) {
			return new BasicRes(ResMessage.USERNAME_AND_EMAIL_REQUIRED.getCode(),
					ResMessage.USERNAME_AND_EMAIL_REQUIRED.getMessage());
		}

		// 檢查回答 不得為空
		if (CollectionUtils.isEmpty(req.getAnswer())) {
			return new BasicRes(ResMessage.ANSWER_REQUIRED.getCode(), ResMessage.ANSWER_REQUIRED.getMessage());
		}

		// 檢查同一張問卷是否已有相同的 email 填寫
		if (feedbackDao.existsByQuizIdAndEmail(req.getQuizId(), req.getEmail())) {
			return new BasicRes(ResMessage.EMAIL_DUPLICATED.getCode(), ResMessage.EMAIL_DUPLICATED.getMessage());
		}

		// 比對資料庫的問卷和問題
		// 取資料庫資料，同時檢查問卷是否是以公布的問卷
		Quiz quiz = quizDao.getByIdAndPublishedTrue(req.getQuizId());
		// 確認是否確實存在這份問卷
		if (quiz == null) {
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}

		// 日期需要檢查填寫的日期是否是問卷可以填寫的時間範圍內
		if (req.getFillinDate() == null || req.getFillinDate().isBefore(quiz.getStartDate())
				|| req.getFillinDate().isAfter(quiz.getEndDate())) {
			return new BasicRes(ResMessage.DATE_RANGE_ERROR.getCode(), ResMessage.DATE_RANGE_ERROR.getMessage());
		}

		// 比對問題
		List<Ques> quesList = quesDao.getByQuizId(req.getQuizId());
		if (CollectionUtils.isEmpty(quesList)) {
			return new BasicRes(ResMessage.QUESTION_NOT_FOUND.getCode(), ResMessage.QUESTION_NOT_FOUND.getMessage());
		}

		// 有回答才有 題號跟回答內容
		// 題號 選項(1~多個)
		Map<Integer, List<String>> answerMap = req.getAnswer();
		for (Ques item : quesList) {
			// req 中的選項(答案)
			List<String> ansList = answerMap.get(item.getQuesId());
			// 必填但沒有答案
			if (item.isRequired() && CollectionUtils.isEmpty(ansList)) {
				return new BasicRes(ResMessage.ANSWER_REQUIRED.getCode(), ResMessage.ANSWER_REQUIRED.getMessage());
			}

			// 單選 或 短述題 不能有複數答案
			if ((item.getType().equalsIgnoreCase(QuesType.SINGLE.getType())
					|| item.getType().equalsIgnoreCase(QuesType.TEXT.getType())) && ansList.size() > 1) {
				return new BasicRes(ResMessage.ONE_OPTION_IS_ALLOWED.getCode(),
						ResMessage.ONE_OPTION_IS_ALLOWED.getMessage());
			}

			// 當問題 Type 不是文字(短述題)類型時，需要將資料庫中的選項字串轉成選項(QuesOtions)類別
			if (!item.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
				// ObjectMapper 把 Ques 中的 options 取出 反序列化 用

				List<QuesOptions> optionList = new ArrayList<>();
				// 把 Ques 中的 options 取出 反序列化
				// 將存在 DB 中的資料型態為 String 的 JSON 格式選項內容，透過 mapper.readValue 放入 QuesOptions 中
				try {
					optionList = mapper.readValue(item.getOptions(), new TypeReference<>() {
					});

				} catch (Exception e) {
					return new BasicRes(ResMessage.OPTIONS_TRANSFER_ERROR.getCode(),
							ResMessage.OPTIONS_TRANSFER_ERROR.getMessage());
				}

				// 蒐集 List<QuesOptions> 中所以的 option
				List<String> optionListInDB = new ArrayList<>();
				for (QuesOptions opt : optionList) {
					optionListInDB.add(opt.getOption());
				}

				// 比對 req 中的答案與資料庫中的選項是否一致
				// 因為 DB 中的選項會比答案多，所以是用多的 List 去 contains 小的 list 中的每一題
				for (String ans : ansList) {
					if (!optionListInDB.contains(ans)) {
						return new BasicRes(ResMessage.OPTION_ANSWER_MISMATCH.getCode(),
								ResMessage.OPTION_ANSWER_MISMATCH.getMessage());
					}
				}
			}

		}

		// 存資料
		List<Feedback> feedbackList = new ArrayList<>();
		// map.getKey()(題號) 的資料型態是 Integer ; str(答案選項) 的資料型態是 List<String>
		for (Entry<Integer, List<String>> map : answerMap.entrySet()) {
			try {
				String str = mapper.writeValueAsString(map.getValue());
				feedbackList.add(new Feedback(req.getQuizId(), (int) map.getKey(), str, req.getUserName(), //
						req.getPhone(), req.getEmail(), req.getAge(), req.getFillinDate()));
			} catch (JsonProcessingException e) {
				return new BasicRes(ResMessage.OPTIONS_TRANSFER_ERROR.getCode(),
						ResMessage.OPTIONS_TRANSFER_ERROR.getMessage());
			}
		}
		// 存檔
		feedbackDao.saveAll(feedbackList);
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public FeedbackRes feedback(int quizId) {
		// 參數檢查
		if (quizId <= 0) {
			return new FeedbackRes(ResMessage.QUIZ_ID_ERROR.getCode(), ResMessage.QUIZ_ID_ERROR.getMessage());
		}

		return new FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
				feedbackDao.getFeedBackByQuizId(quizId));
	}

	@Override
	public StatisticsRes statistics(int quizId) {
		// 參數檢查
		if (quizId <= 0) {
			return new StatisticsRes(ResMessage.QUIZ_ID_ERROR.getCode(), //
					ResMessage.QUIZ_ID_ERROR.getMessage());
		}

		List<StatisticsDto> dtoList = feedbackDao.getStatisticsByQuizId(quizId);
		
		if(CollectionUtils.isEmpty(dtoList)) {
			return new StatisticsRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage(), new ArrayList<>());
		}

		// 將 Dto 的內容轉成 Vo
		List<StatisticsVo> voList = new ArrayList<>();

		mainLoop: for (StatisticsDto dto : dtoList) {
			// 用來判斷 voList 中是否已存在相同問題編號的 vo
			boolean isDuplicated = false;
			Map<String, Integer> optionCountMap = new HashMap<>();
			StatisticsVo vo = new StatisticsVo();

			// 從 volist 取出有相同 quesId 的 vo --> 目的是不用再重新蒐集選項，直接計算次數
			for (StatisticsVo voItem : voList) {
				if (voItem.getQuesId() == dto.getQuesId()) {
					optionCountMap = voItem.getOptionCountMap();
					vo = voItem;
					isDuplicated = true;
					break;
				}
			}
			// !isDuplicated 表示 voList 中沒有相同問題編號的 vo
			if (!isDuplicated) {
				voList.add(vo);
			}

			List<QuesOptions> optionList = new ArrayList<>();
			List<String> answerList = new ArrayList<>();
			// 把 dto 中的 OptionsStr 取出 反序列化
			// 將存在 DB 中的資料型態為 String 的 JSON 格式選項內容，透過 mapper.readValue 放入 QuesOptions 中
			try {
				// 題型是 text 時，選項沒有值
				if (!dto.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
					optionList = mapper.readValue(dto.getOptionsStr(), new TypeReference<>() {
					});
				}

				// 只有以下條件才能將答案字串轉乘 List<String>
				// 1.answer 有內容的字串(空白字串除外)
				if (StringUtils.hasText(dto.getAnswerStr())) {
					answerList = mapper.readValue(dto.getAnswerStr(), new TypeReference<>() {
					});
				}
			} catch (Exception e) {
				return new StatisticsRes(ResMessage.OPTIONS_TRANSFER_ERROR.getCode(),
						ResMessage.OPTIONS_TRANSFER_ERROR.getMessage());
			}

			// 題型是 text 的時候，不蒐集選項以及答案
			if (dto.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
				// 重複
				if (isDuplicated) {
					//有內容的字串(空白字串除外)
					if(StringUtils.hasText(answerList.get(0))) {
						vo.getTextAnswerList().addAll(answerList);						
					}
					continue mainLoop; // 跳過一次外層迴圈
				}
				// set 基本資料
				vo.setQuizName(dto.getQuizName());
				vo.setQuesId(dto.getQuesId());
				vo.setQuesName(dto.getQuesName());
				vo.setOptionCountMap(optionCountMap);
				// 不重複時，有內容的字串(空白字串除外)
				if(StringUtils.hasText(answerList.get(0))) {
					vo.setTextAnswerList(answerList);					
				}else {
					vo.setTextAnswerList(new ArrayList<>());
				}
				continue;
			}

			// 選項蒐集
			// 沒有重複的 vo 就需要再收集選項
			if (!isDuplicated) {
				for (QuesOptions option : optionList) {
					// 題目 數量
					optionCountMap.put(option.getOption(), 0);
				}
			}
			
			// 蒐集答案(計算選項次數)
			for (String str : answerList) {
				// 取出舊的選項的次數
				if(optionCountMap.containsKey(str)) {
					int previousCount = optionCountMap.get(str);
					optionCountMap.put(str, previousCount + 1);					
				}
			}

			vo.setQuizName(dto.getQuizName());
			vo.setQuesId(dto.getQuesId());
			vo.setQuesName(dto.getQuesName());
			vo.setOptionCountMap(optionCountMap);
			// 最後不需要將 vo add 到 voList 中，是因為迴圈開始的時候，已經有將其加入
		}
		return new StatisticsRes(ResMessage.SUCCESS.getCode(),
				ResMessage.SUCCESS.getMessage(), voList);
	}

	
	@Override
	public StatisticsRes statisticsAAA(int quizId) {
		// 參數檢查
		if (quizId <= 0) {
			return new StatisticsRes(ResMessage.QUIZ_ID_ERROR.getCode(), //
					ResMessage.QUIZ_ID_ERROR.getMessage());
		}
		List<StatisticsDto> dtoList = feedbackDao.getStatisticsByQuizId(quizId);
		// 將 Dto 的內容轉成 Vo
		List<StatisticsVo> voList = new ArrayList<>();
		for (StatisticsDto dto : dtoList) {
			boolean isDuplicated = false;
			// voList.stream().filter(vo -> vo.getQuesId() == dto.getQuesId()): 有資料就會保留，沒資料就是 null
			// filter 的結果會是多個，所以用 findFirst() 取得第一筆
			// orElse(null) 表示沒資料時回傳 null
			StatisticsVo vo = voList.stream().filter(item -> item.getQuesId() == dto.getQuesId())//
					.findFirst().orElse(null);
			
			// 如果 vo 已存在則  isDuplicated 為 true
			// 沒有則以當筆 dto 的問卷名稱、題目id、題目名稱建立一個新的 vo 後加入 voList 內
			if(vo != null) {
				isDuplicated = true;
			}else {
				vo = new StatisticsVo(dto.getQuizName(), dto.getQuesId(), dto.getQuesName());
				voList.add(vo);
			}
			
			List<QuesOptions> optionsList = new ArrayList<>();
			List<String> answerList = new ArrayList<>();
			// 題型非 text:
			if (!dto.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
				try {
					// 1. 題號不重複時，轉換選項字串為選項類別
					// 1.1 把 Ques 中的 options 字串轉成 Options 類別			
					// 1.2 要將選項字串傳換成對應的 List
					if(!isDuplicated) {
						optionsList = mapper.readValue(dto.getOptionsStr(), new TypeReference<>() {
						});
					}
					
					// 2. 轉換答案字串回 List: answer 此欄位有值(包括空陣列的字串)
					if(StringUtils.hasText(dto.getAnswerStr())) {
						answerList = mapper.readValue(dto.getAnswerStr(), new TypeReference<>() {
						});
					}
				} catch (Exception e) {
					return new StatisticsRes(ResMessage.OPTIONS_TRANSFER_ERROR.getCode(), //
							ResMessage.OPTIONS_TRANSFER_ERROR.getMessage());
				}
			}
			// 蒐集選項以及次數
			// 題號重複時，從 vo 取 optionCountMap
			Map<String, Integer> optionCountMap = new HashMap<>();
			if(isDuplicated) {
				optionCountMap = vo.getOptionCountMap();
			} else {
				for(QuesOptions opItem : optionsList) {
					optionCountMap.put(opItem.getOption(), 0);
				}
			}
			// 將次數 + 1
			for(String ans : answerList) {
					optionCountMap.put(ans, optionCountMap.get(ans) + 1);
			}
			
			
			vo.setOptionCountMap(optionCountMap);
		}


		return new StatisticsRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), voList);
	}

}
