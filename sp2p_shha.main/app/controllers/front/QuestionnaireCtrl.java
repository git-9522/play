package controllers.front;

import java.util.HashMap;
import java.util.Map;

import common.constants.ModuleConst;
import common.utils.Factory;
import common.utils.LoggerUtil;
import controllers.common.interceptor.AccountInterceptor;
import models.common.bean.CurrUser;
import models.common.entity.t_questionnaire;
import models.common.entity.t_score_user;
import play.Logger;
import play.db.jpa.JPA;
import play.mvc.Controller;
import play.mvc.With;
import services.common.QuestionnaireService;
import services.common.ScoreUserService;
import services.common.UserFundService;
import services.common.UserService;

@With(value = AccountInterceptor.class)
public class QuestionnaireCtrl extends Controller {
	
	protected static QuestionnaireService questionnaireService = Factory.getService(QuestionnaireService.class);

	protected static UserFundService userFundService = Factory.getService(UserFundService.class);
	
	protected static ScoreUserService scoreUserService = Factory.getService(ScoreUserService.class);
	
	protected static UserService userService = Factory.getService(UserService.class);
	
	/**
	 * 获取问卷调查页面
	 * @param id questionnaireId
	 */
	public static void enterQuestionnairePre(long id) {
		CurrUser currUser = LoginAndRegisteCtrl.getCurrUser();
		t_questionnaire questionnaire = questionnaireService.queryById(id, currUser.id);
		if(questionnaire == null) {
			throw new RuntimeException("找不到该问卷");
		}
		boolean flag = false;
		if(questionnaire != null && questionnaire.details != null && !questionnaire.details.isEmpty()) {
			if(questionnaire.details.get(0).records != null && !questionnaire.details.get(0).records.isEmpty()) {
				flag = true;
			}
		}
		renderArgs.put("questionnaire", questionnaire);
		renderArgs.put("flag", flag);
		render();
	}
	
	/**
	 * 提交问卷调查
	 * @param id questionnaireId
	 * @param params 参数
	 */
	public static void commitQuestionnairePre(long id, String params) {
		CurrUser currUser = LoginAndRegisteCtrl.getCurrUser();
		boolean flag = questionnaireService.hasCommit(id, currUser.id);
		if(!flag) {
			enterQuestionnairePre(id);
		}
		questionnaireService.commitQuestionnaire(params, currUser.id);
		// 处理积分
		if(ModuleConst.EXT_MALL){
			int score = 100;
			//增加积分
			int rows = userFundService.updateUserScoreBalanceAdd(currUser.id, score);
			if (rows <= 0) {
				JPA.setRollbackOnly();
				LoggerUtil.info(true, "注册成功 获得积分时出错，数据回滚");
				enterQuestionnairePre(id);
			}
			//及时查询用户的可用积分
			double scoreBalance = userFundService.findUserScoreBalance(currUser.id);
			/** 添加用户积分记录 */
			Map<String, String> summaryPrams = new HashMap<String, String>();
			summaryPrams.put("score", (int) score + "");
			boolean addDeal = scoreUserService.addScoreUserInfo(currUser.id, score,	scoreBalance, 
					t_score_user.OperationType.QUESTIONNAIRE_COMMIT, summaryPrams);
			if (!addDeal) {
				JPA.setRollbackOnly();
				enterQuestionnairePre(id);
			}
		}
		userService.flushUserCache(currUser.id);
		enterQuestionnairePre(id);
	}
	
}