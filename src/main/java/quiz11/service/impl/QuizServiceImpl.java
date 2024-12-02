package quiz11.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
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
import quiz11.vo.FillinReq;
import quiz11.vo.GetQuesReq;
import quiz11.vo.GetQuesRes;
import quiz11.vo.QuesOptions;
import quiz11.vo.SearchReq;
import quiz11.vo.SearchRes;

@Service
public class QuizServiceImpl implements QuizService {

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

		// 檢查 問卷名 跟 問卷敘述 是否有填寫
		if (!StringUtils.hasText(req.getName()) || !StringUtils.hasText(req.getDescription())) {
			return new BasicRes(ResMessage.QUIZ_PARAM_ERROR.getCode(), ResMessage.QUIZ_PARAM_ERROR.getMessage());
		}
		// 檢查時間、檢查開始時間不能比結束時間晚
		if (req.getStartDate() == null || req.getEndDate() == null || req.getStartDate().isAfter(req.getEndDate())) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		// 檢查開始時間不能今天比早(問卷的開始時間最晚為今天)
		if (req.getStartDate().isBefore(LocalDate.now())) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		// 檢查問卷是否有選項內容
		if (CollectionUtils.isEmpty(req.getQuesLsit())) {
			return new BasicRes(ResMessage.QUES_PARAM_ERROR.getCode(), ResMessage.QUES_PARAM_ERROR.getMessage());
		}

		// 檢查 Ques
		for (Ques item : req.getQuesLsit()) {
			// 題目 id 不能小於等於 0 ， 題目名稱不能不存在
			if (item.getQuesId() <= 0 || !StringUtils.hasText(item.getQuesName())
					|| !StringUtils.hasText(item.getType())) {
				return new BasicRes(ResMessage.QUES_PARAM_ERROR.getCode(), ResMessage.QUES_PARAM_ERROR.getMessage());
			}
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
		if (StringUtils.hasText(req.getUserName()) || StringUtils.hasText(req.getEmail())) {
			return new BasicRes(ResMessage.USERNAME_AND_EMAIL_REQUIRED.getCode(),
					ResMessage.USERNAME_AND_EMAIL_REQUIRED.getMessage());
		}

		// 年齡至少 12 歲
		if (req.getAge() <= 12) {
			return new BasicRes(ResMessage.AGE_ABOVE_12.getCode(), ResMessage.AGE_ABOVE_12.getMessage());
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

		ObjectMapper mapper = new ObjectMapper();

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

}
