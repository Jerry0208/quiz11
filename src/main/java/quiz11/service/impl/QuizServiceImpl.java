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
		// �Ѽ��ˬd
		BasicRes checkResult = checkParams(req, true);
		if (checkResult != null) {
			return checkResult;
		}
		// �]��quiz �� pk �O�y�����A���|���Ƽg�J�A�ҥH�����ˬd DB �O�_�w�s�b�ۦP�� PK
		// �s�W�ݨ�
		// �]�� Quiz ���� id �O AI �۰ʥͦ����y�����A�n�� quizDao ���� save ��i�H��� id ���Ȧ^�ǡA
		// �����n�b Quiz �� Entity ���N��ƫ��A�� int ���ݩ� id
		// �[�W @GeneratedValue(strategy = GenerationType.IDENTITY)
		Quiz quizRes = quizDao.save(new Quiz(req.getName(), req.getDescription(), //
				req.getStartDate(), req.getEndDate(), req.isPublished()));

		// �N quiz ���� id �[�J�� Ques
		int quizId = quizRes.getId();
		for (Ques item : req.getQuesLsit()) {
			item.setQuizId(quizId);
		}

		// �s�W���D
		quesDao.saveAll(req.getQuesLsit());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	// �Ѽ��ˬd
	private BasicRes checkParams(CreateUpdateReq req, boolean isCreateQuiz) {
		// �ˬd�s�W�ݨ��ɡAid �n�� 0 �A checkQuizId �s�W:true ��s:false
		if (req.getId() != 0 && isCreateQuiz) {
			return new BasicRes(ResMessage.QUIZ_PARAM_ERROR.getCode(), ResMessage.QUIZ_PARAM_ERROR.getMessage());
		}

		// ��s�ݨ��� Id ���o�� 0 �A checkQuizId �s�W:true ��s:false
		if (req.getId() == 0 && !isCreateQuiz) {
			return new BasicRes(ResMessage.QUIZ_PARAM_ERROR.getCode(), ResMessage.QUIZ_PARAM_ERROR.getMessage());
		}


		// ���Ѫ��{���X�w�b req ���ϥ� @valid ������ˬd
		
		// �ˬd �ݨ��W �� �ݨ��ԭz �O�_����g
//		if (!StringUtils.hasText(req.getName()) || !StringUtils.hasText(req.getDescription())) {
//			return new BasicRes(ResMessage.QUIZ_PARAM_ERROR.getCode(), ResMessage.QUIZ_PARAM_ERROR.getMessage());
//		}
		
		// �ˬd�ɶ��B�ˬd�}�l�ɶ�����񵲧��ɶ���
		if (req.getStartDate() == null || req.getEndDate() == null || req.getStartDate().isAfter(req.getEndDate())) {
			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
		}

		// �ˬd�}�l�ɶ����व�Ѥ�(�ݨ����}�l�ɶ��̱߬�����)
//		if (req.getStartDate().isBefore(LocalDate.now())) {
//			return new BasicRes(ResMessage.DATE_ERROR.getCode(), ResMessage.DATE_ERROR.getMessage());
//		}

		// �ˬd�ݨ��O�_���ﶵ���e
		if (CollectionUtils.isEmpty(req.getQuesLsit())) {
			return new BasicRes(ResMessage.QUES_PARAM_ERROR.getCode(), ResMessage.QUES_PARAM_ERROR.getMessage());
		}

		// �ˬd Ques
		for (Ques item : req.getQuesLsit()) {
			// �D�� id ����p�󵥩� 0 �A �D�ئW�٤��ण�s�b
//			if (item.getQuesId() <= 0 || !StringUtils.hasText(item.getQuesName())
//					|| !StringUtils.hasText(item.getType())) {
//				return new BasicRes(ResMessage.QUES_PARAM_ERROR.getCode(), ResMessage.QUES_PARAM_ERROR.getMessage());
//			}
			// �ˬd�D������: ���(single)�B �h��(multi)�B��r(text)
			if (!QuesType.checkType(item.getType())) {
				return new BasicRes(ResMessage.QUES_TYPE_ERROR.getCode(), ResMessage.QUES_TYPE_ERROR.getMessage());
			}
			// �ˬd�D��r�����ɡA�ﶵ�S����
			if (!item.getType().equalsIgnoreCase(QuesType.TEXT.toString()) && !StringUtils.hasText(item.getOptions())) {
				return new BasicRes(ResMessage.QUES_TYPE_ERROR.getCode(), ResMessage.QUES_TYPE_ERROR.getMessage());
			}
		}

		// ���\
		return null;
	}

	@Transactional
	@Override
	public BasicRes update(CreateUpdateReq req) {

		BasicRes checkResult = checkParams(req, false);
		if (checkResult != null) {
			return checkResult;
		}

		// �ˬd Ques �� quiz_id �O�_�P Quiz �� id �۲�
		int quizId = req.getId();
		for (Ques item : req.getQuesLsit()) {
			if (item.getQuizId() != quizId) {
				return new BasicRes(ResMessage.QUIZ_ID_MISMATCH.getCode(), ResMessage.QUIZ_ID_MISMATCH.getMessage());
			}
		}

		// �ݨ��i�H��s�����A: 1.���o�� 2.�w�o�����|���}�l
		Optional<Quiz> op = quizDao.findById(req.getId());
		// �T�{�ݨ��O�_�s�b
		if (op.isEmpty()) { // true �ɪ�ܸ�Ƥ��s�b
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}

		// ���o��Ʈw�����ݧ�s���ݨ�
		Quiz quiz = op.get();
		// �T�{�ݨ��O�_�i�H�i���s
		// �|���o�� : !quiz.isPublished()
		// �w�o�����|���}�l : quiz.isPublished() && req.getStartDate().isAfter(LocalDate.now())
		// �p�A�����ե�
		// �ư��k: �ҥH����޿覡�e���[ !
		if (!(!quiz.isPublished() || (quiz.isPublished() && req.getStartDate().isAfter(LocalDate.now())))) {
			return new BasicRes(ResMessage.QUIZ_UPDATE_FAILED.getCode(), ResMessage.QUIZ_UPDATE_FAILED.getMessage());
		}

		// �z�L�ۭq�q���ط���k�N req ������ set �^�q��Ʈw���X�� quiz ��
		quiz.setName(req.getName());
		quiz.setDescription(req.getDescription());
		quiz.setStartDate(req.getStartDate());
		quiz.setEndDate(req.getEndDate());
		quiz.setPublished(req.isPublished());

		// ��s�ݨ�
		quizDao.save(quiz);

		// ���R���쥻���ݨ��ﶵ
		quesDao.deleteByQuizId(req.getId());

		// �s�W���D
		quesDao.saveAll(req.getQuesLsit());

		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public BasicRes delete(DeleteReq req) {
		// �R�ݨ�
		quizDao.deleteByIdIn(req.getQuizIdList());
		// �R�ۦP �ݨ�ID ���ﶵ
		quesDao.deleteByQuizIdIn(req.getQuizIdList());
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public SearchRes search(SearchReq req) {

		// �˵�����
		String name = req.getName();
		// �p�G name = null�ΪŦr��Υ��ťզr��A�@�߳��ন�Ŧr��
		if (!StringUtils.hasText(name)) {
			name = "";
		}

		// �̪��A�j�M
		if (StringUtils.hasText(req.getStatus())) {
			if (req.getStatus().equalsIgnoreCase("�i�椤")) {
				return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
						quizDao.getInProgress(name, LocalDate.now()));
			}
			if (req.getStatus().equalsIgnoreCase("�w����")) {
				return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
						quizDao.getCompleted(name, LocalDate.now()));
			}
			if (req.getStatus().equalsIgnoreCase("�|���}�l")) {
				return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
						quizDao.getNotStartedYet(name, LocalDate.now()));
			}
			if (req.getStatus().equalsIgnoreCase("�|������")) {
				return new SearchRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
						quizDao.getNotYetAnnounced(name, LocalDate.now()));
			}
		}

		// �Y�S���}�l�������A�N����ন�ܦ����ɶ�
		LocalDate startDate = req.getStartDate();
		if (startDate == null) {
			startDate = LocalDate.of(1970, 1, 1);
		}
		// �Y�S���}�l�������A�N����ন�ܤ[�����Ӯɶ�
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
		// �Ѽ��ˬd
		// quiz id
		if (req.getQuizId() <= 0) {
			return new BasicRes(ResMessage.QUIZ_ID_ERROR.getCode(), ResMessage.QUIZ_ID_ERROR.getMessage());
		}

		// �ϥΪ̦W�١B�q�l�H�c
		if (!StringUtils.hasText(req.getUserName()) || !StringUtils.hasText(req.getEmail())) {
			return new BasicRes(ResMessage.USERNAME_AND_EMAIL_REQUIRED.getCode(),
					ResMessage.USERNAME_AND_EMAIL_REQUIRED.getMessage());
		}

		// �ˬd�^�� ���o����
		if (CollectionUtils.isEmpty(req.getAnswer())) {
			return new BasicRes(ResMessage.ANSWER_REQUIRED.getCode(), ResMessage.ANSWER_REQUIRED.getMessage());
		}

		// �ˬd�P�@�i�ݨ��O�_�w���ۦP�� email ��g
		if (feedbackDao.existsByQuizIdAndEmail(req.getQuizId(), req.getEmail())) {
			return new BasicRes(ResMessage.EMAIL_DUPLICATED.getCode(), ResMessage.EMAIL_DUPLICATED.getMessage());
		}

		// ����Ʈw���ݨ��M���D
		// ����Ʈw��ơA�P���ˬd�ݨ��O�_�O�H�������ݨ�
		Quiz quiz = quizDao.getByIdAndPublishedTrue(req.getQuizId());
		// �T�{�O�_�T��s�b�o���ݨ�
		if (quiz == null) {
			return new BasicRes(ResMessage.QUIZ_NOT_FOUND.getCode(), ResMessage.QUIZ_NOT_FOUND.getMessage());
		}

		// ����ݭn�ˬd��g������O�_�O�ݨ��i�H��g���ɶ��d��
		if (req.getFillinDate() == null || req.getFillinDate().isBefore(quiz.getStartDate())
				|| req.getFillinDate().isAfter(quiz.getEndDate())) {
			return new BasicRes(ResMessage.DATE_RANGE_ERROR.getCode(), ResMessage.DATE_RANGE_ERROR.getMessage());
		}

		// �����D
		List<Ques> quesList = quesDao.getByQuizId(req.getQuizId());
		if (CollectionUtils.isEmpty(quesList)) {
			return new BasicRes(ResMessage.QUESTION_NOT_FOUND.getCode(), ResMessage.QUESTION_NOT_FOUND.getMessage());
		}

		// ���^���~�� �D����^�����e
		// �D�� �ﶵ(1~�h��)
		Map<Integer, List<String>> answerMap = req.getAnswer();
		for (Ques item : quesList) {
			// req �����ﶵ(����)
			List<String> ansList = answerMap.get(item.getQuesId());
			// ������S������
			if (item.isRequired() && CollectionUtils.isEmpty(ansList)) {
				return new BasicRes(ResMessage.ANSWER_REQUIRED.getCode(), ResMessage.ANSWER_REQUIRED.getMessage());
			}

			// ��� �� �u�z�D ���঳�ƼƵ���
			if ((item.getType().equalsIgnoreCase(QuesType.SINGLE.getType())
					|| item.getType().equalsIgnoreCase(QuesType.TEXT.getType())) && ansList.size() > 1) {
				return new BasicRes(ResMessage.ONE_OPTION_IS_ALLOWED.getCode(),
						ResMessage.ONE_OPTION_IS_ALLOWED.getMessage());
			}

			// ����D Type ���O��r(�u�z�D)�����ɡA�ݭn�N��Ʈw�����ﶵ�r���ন�ﶵ(QuesOtions)���O
			if (!item.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
				// ObjectMapper �� Ques ���� options ���X �ϧǦC�� ��

				List<QuesOptions> optionList = new ArrayList<>();
				// �� Ques ���� options ���X �ϧǦC��
				// �N�s�b DB ������ƫ��A�� String �� JSON �榡�ﶵ���e�A�z�L mapper.readValue ��J QuesOptions ��
				try {
					optionList = mapper.readValue(item.getOptions(), new TypeReference<>() {
					});

				} catch (Exception e) {
					return new BasicRes(ResMessage.OPTIONS_TRANSFER_ERROR.getCode(),
							ResMessage.OPTIONS_TRANSFER_ERROR.getMessage());
				}

				// �`�� List<QuesOptions> ���ҥH�� option
				List<String> optionListInDB = new ArrayList<>();
				for (QuesOptions opt : optionList) {
					optionListInDB.add(opt.getOption());
				}

				// ��� req �������׻P��Ʈw�����ﶵ�O�_�@�P
				// �]�� DB �����ﶵ�|�񵪮צh�A�ҥH�O�Φh�� List �h contains �p�� list �����C�@�D
				for (String ans : ansList) {
					if (!optionListInDB.contains(ans)) {
						return new BasicRes(ResMessage.OPTION_ANSWER_MISMATCH.getCode(),
								ResMessage.OPTION_ANSWER_MISMATCH.getMessage());
					}
				}
			}

		}

		// �s���
		List<Feedback> feedbackList = new ArrayList<>();
		// map.getKey()(�D��) ����ƫ��A�O Integer ; str(���׿ﶵ) ����ƫ��A�O List<String>
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
		// �s��
		feedbackDao.saveAll(feedbackList);
		return new BasicRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage());
	}

	@Override
	public FeedbackRes feedback(int quizId) {
		// �Ѽ��ˬd
		if (quizId <= 0) {
			return new FeedbackRes(ResMessage.QUIZ_ID_ERROR.getCode(), ResMessage.QUIZ_ID_ERROR.getMessage());
		}

		return new FeedbackRes(ResMessage.SUCCESS.getCode(), ResMessage.SUCCESS.getMessage(),
				feedbackDao.getFeedBackByQuizId(quizId));
	}

	@Override
	public StatisticsRes statistics(int quizId) {
		// �Ѽ��ˬd
		if (quizId <= 0) {
			return new StatisticsRes(ResMessage.QUIZ_ID_ERROR.getCode(), //
					ResMessage.QUIZ_ID_ERROR.getMessage());
		}

		List<StatisticsDto> dtoList = feedbackDao.getStatisticsByQuizId(quizId);
		
		if(CollectionUtils.isEmpty(dtoList)) {
			return new StatisticsRes(ResMessage.SUCCESS.getCode(), //
					ResMessage.SUCCESS.getMessage(), new ArrayList<>());
		}

		// �N Dto �����e�ন Vo
		List<StatisticsVo> voList = new ArrayList<>();

		mainLoop: for (StatisticsDto dto : dtoList) {
			// �ΨӧP�_ voList ���O�_�w�s�b�ۦP���D�s���� vo
			boolean isDuplicated = false;
			Map<String, Integer> optionCountMap = new HashMap<>();
			StatisticsVo vo = new StatisticsVo();

			// �q volist ���X���ۦP quesId �� vo --> �ت��O���ΦA���s�`���ﶵ�A�����p�⦸��
			for (StatisticsVo voItem : voList) {
				if (voItem.getQuesId() == dto.getQuesId()) {
					optionCountMap = voItem.getOptionCountMap();
					vo = voItem;
					isDuplicated = true;
					break;
				}
			}
			// !isDuplicated ��� voList ���S���ۦP���D�s���� vo
			if (!isDuplicated) {
				voList.add(vo);
			}

			List<QuesOptions> optionList = new ArrayList<>();
			List<String> answerList = new ArrayList<>();
			// �� dto ���� OptionsStr ���X �ϧǦC��
			// �N�s�b DB ������ƫ��A�� String �� JSON �榡�ﶵ���e�A�z�L mapper.readValue ��J QuesOptions ��
			try {
				// �D���O text �ɡA�ﶵ�S����
				if (!dto.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
					optionList = mapper.readValue(dto.getOptionsStr(), new TypeReference<>() {
					});
				}

				// �u���H�U����~��N���צr���୼ List<String>
				// 1.answer �����e���r��(�ťզr�갣�~)
				if (StringUtils.hasText(dto.getAnswerStr())) {
					answerList = mapper.readValue(dto.getAnswerStr(), new TypeReference<>() {
					});
				}
			} catch (Exception e) {
				return new StatisticsRes(ResMessage.OPTIONS_TRANSFER_ERROR.getCode(),
						ResMessage.OPTIONS_TRANSFER_ERROR.getMessage());
			}

			// �D���O text ���ɭԡA���`���ﶵ�H�ε���
			if (dto.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
				// ����
				if (isDuplicated) {
					//�����e���r��(�ťզr�갣�~)
					if(StringUtils.hasText(answerList.get(0))) {
						vo.getTextAnswerList().addAll(answerList);						
					}
					continue mainLoop; // ���L�@���~�h�j��
				}
				// set �򥻸��
				vo.setQuizName(dto.getQuizName());
				vo.setQuesId(dto.getQuesId());
				vo.setQuesName(dto.getQuesName());
				vo.setOptionCountMap(optionCountMap);
				// �����ƮɡA�����e���r��(�ťզr�갣�~)
				if(StringUtils.hasText(answerList.get(0))) {
					vo.setTextAnswerList(answerList);					
				}else {
					vo.setTextAnswerList(new ArrayList<>());
				}
				continue;
			}

			// �ﶵ�`��
			// �S�����ƪ� vo �N�ݭn�A�����ﶵ
			if (!isDuplicated) {
				for (QuesOptions option : optionList) {
					// �D�� �ƶq
					optionCountMap.put(option.getOption(), 0);
				}
			}
			
			// �`������(�p��ﶵ����)
			for (String str : answerList) {
				// ���X�ª��ﶵ������
				if(optionCountMap.containsKey(str)) {
					int previousCount = optionCountMap.get(str);
					optionCountMap.put(str, previousCount + 1);					
				}
			}

			vo.setQuizName(dto.getQuizName());
			vo.setQuesId(dto.getQuesId());
			vo.setQuesName(dto.getQuesName());
			vo.setOptionCountMap(optionCountMap);
			// �̫ᤣ�ݭn�N vo add �� voList ���A�O�]���j��}�l���ɭԡA�w�g���N��[�J
		}
		return new StatisticsRes(ResMessage.SUCCESS.getCode(),
				ResMessage.SUCCESS.getMessage(), voList);
	}

	
	@Override
	public StatisticsRes statisticsAAA(int quizId) {
		// �Ѽ��ˬd
		if (quizId <= 0) {
			return new StatisticsRes(ResMessage.QUIZ_ID_ERROR.getCode(), //
					ResMessage.QUIZ_ID_ERROR.getMessage());
		}
		List<StatisticsDto> dtoList = feedbackDao.getStatisticsByQuizId(quizId);
		// �N Dto �����e�ন Vo
		List<StatisticsVo> voList = new ArrayList<>();
		for (StatisticsDto dto : dtoList) {
			boolean isDuplicated = false;
			// voList.stream().filter(vo -> vo.getQuesId() == dto.getQuesId()): ����ƴN�|�O�d�A�S��ƴN�O null
			// filter �����G�|�O�h�ӡA�ҥH�� findFirst() ���o�Ĥ@��
			// orElse(null) ��ܨS��Ʈɦ^�� null
			StatisticsVo vo = voList.stream().filter(item -> item.getQuesId() == dto.getQuesId())//
					.findFirst().orElse(null);
			
			// �p�G vo �w�s�b�h  isDuplicated �� true
			// �S���h�H�� dto ���ݨ��W�١B�D��id�B�D�ئW�٫إߤ@�ӷs�� vo ��[�J voList ��
			if(vo != null) {
				isDuplicated = true;
			}else {
				vo = new StatisticsVo(dto.getQuizName(), dto.getQuesId(), dto.getQuesName());
				voList.add(vo);
			}
			
			List<QuesOptions> optionsList = new ArrayList<>();
			List<String> answerList = new ArrayList<>();
			// �D���D text:
			if (!dto.getType().equalsIgnoreCase(QuesType.TEXT.getType())) {
				try {
					// 1. �D�������ƮɡA�ഫ�ﶵ�r�ꬰ�ﶵ���O
					// 1.1 �� Ques ���� options �r���ন Options ���O			
					// 1.2 �n�N�ﶵ�r��Ǵ��������� List
					if(!isDuplicated) {
						optionsList = mapper.readValue(dto.getOptionsStr(), new TypeReference<>() {
						});
					}
					
					// 2. �ഫ���צr��^ List: answer ����즳��(�]�A�Ű}�C���r��)
					if(StringUtils.hasText(dto.getAnswerStr())) {
						answerList = mapper.readValue(dto.getAnswerStr(), new TypeReference<>() {
						});
					}
				} catch (Exception e) {
					return new StatisticsRes(ResMessage.OPTIONS_TRANSFER_ERROR.getCode(), //
							ResMessage.OPTIONS_TRANSFER_ERROR.getMessage());
				}
			}
			// �`���ﶵ�H�Φ���
			// �D�����ƮɡA�q vo �� optionCountMap
			Map<String, Integer> optionCountMap = new HashMap<>();
			if(isDuplicated) {
				optionCountMap = vo.getOptionCountMap();
			} else {
				for(QuesOptions opItem : optionsList) {
					optionCountMap.put(opItem.getOption(), 0);
				}
			}
			// �N���� + 1
			for(String ans : answerList) {
					optionCountMap.put(ans, optionCountMap.get(ans) + 1);
			}
			
			
			vo.setOptionCountMap(optionCountMap);
		}


		return new StatisticsRes(ResMessage.SUCCESS.getCode(), //
				ResMessage.SUCCESS.getMessage(), voList);
	}

}
