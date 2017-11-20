package jobs;

import java.util.List;

import models.common.entity.t_user;
import models.core.entity.t_red_packet;
import models.core.entity.t_red_packet_sending;
import models.core.entity.t_red_packet_task;
import models.core.entity.t_red_packet_task.TaskSendType;
import common.utils.Factory;
import common.utils.LoggerUtil;
import common.utils.ResultInfo;
import play.Logger;
import play.db.jpa.JPA;
import play.db.jpa.JPAPlugin;
import play.jobs.Every;
import play.jobs.Job;
import services.common.UserService;
import services.core.RedpacketService;

/**
 * 扫描红包任务，添加群发信息到发送表，每隔1分钟执行
 * 
 */
@Every("1min")
public class MassRedPacketTask extends Job {

	protected RedpacketService redpacketService = Factory.getService(RedpacketService.class);

	protected UserService userService = Factory.getService(UserService.class);

	@Override
	public void doJob() throws Exception {
		Logger.info("-----------开始执行定时任务：群发红包任务扫描-----------");

		t_red_packet_task task = redpacketService.queryUnSendTask();

		if (task == null){
			return;
		}
		
		long currentId = task.current_user_id;

		// 任务结束符
		if (task.is_send_number >= task.total_number) {
			return;
		}

		//判断执行添加发送表
		if (task.send_type == TaskSendType.MASS.code) {

			List<t_user> sendUserList = redpacketService.findUserList(currentId,task.id);

			if (null == sendUserList || sendUserList.size() <= 0) {
				Logger.info("-----------暂无红包任务需要发送-----------");
				Logger.info("-----------结束执行定时任务：群发红包任务扫描-----------");

				return;
			}
			
			/* 关闭自动事务 */
			JPAPlugin.closeTx(false);
			
			for (t_user user : sendUserList) {
				
				try {
					/* 开启自动事务 */
					JPAPlugin.startTx(false);
					
					// 循环插入到redpacket_sending表
					saveToSending(user, task);
					
				} catch (Exception e) {
					LoggerUtil.error(true, "群发红包失败：" + e.getMessage());
	
					return;
				} finally {
					/* 关闭自动事务 */
					JPAPlugin.closeTx(false);
					LoggerUtil.info(false, "群发红包任务事务正常关闭");
				}
			}
			
		}else if (task.send_type == TaskSendType.CHOOSE.code) {
			
			String userIds = task.user_id_str.replaceAll("\\s", "");
			String[] userId = userIds.split(",");
			
			if (null == userId || userId.length <= 0) {
				Logger.info("-----------暂无红包任务需要发送-----------");
				Logger.info("-----------结束执行定时任务：群发红包任务扫描-----------");

				return;
			}
		
			/* 关闭自动事务 */
			JPAPlugin.closeTx(false);
			
			// 循环插入到redpacket_sending表
			for(String id : userId){
				try {
					/* 开启自动事务 */
					JPAPlugin.startTx(false);
					
					t_user user = userService.findByID(Long.parseLong(id));
					// 循环插入到redpacket_sending表
					saveToSending(user, task);
					
				}catch (Exception e) {
					LoggerUtil.error(true, "群发红包失败：" + e.getMessage());
	
					return;
				} finally {
					/* 关闭自动事务 */
					JPAPlugin.closeTx(false);
					LoggerUtil.info(false, "群发红包任务事务正常关闭");
				}
			}
		}
		
		/* 开启自动事务 */
		JPAPlugin.startTx(false);
		Logger.info("-----------结束执行定时任务：群发红包任务扫描-----------");
	}
	
	/**
	 * 插入到redpacket_sending表
	 * 
	 * @param user 当前用户
	 * @param task 当前任务
	 */
	public void saveToSending (t_user user, t_red_packet_task task){
		
		ResultInfo result = new ResultInfo();
		
		// 插入数量+1,成功才执行后面代码
		result = redpacketService.updateRedPacketTaskCount(task.id);
		if (result.code < 1) {
			Logger.info("-----------结束执行定时任务：群发红包任务扫描-----------");
			LoggerUtil.error(true, "修改发送数量失败：%s，原因：%s", result.msg);
			return;
		}

		// 唯一索引
		String sign = task.identification + "" + user.id;
		result = redpacketService.saveSending(user,	task.red_packet_id, sign);
		if (result.code < 1) {
			LoggerUtil.error(true, "添加索引失败：%s，原因：%s", result.msg);
			
			return;
		}

		/* 插入成功修改current_user_id */
		result = redpacketService.updateRedPacketTaskUser(user.id, task.id);
		if (result.code < 1) {
			LoggerUtil.error(true, "更新当前用户失败：%s，原因：%s", result.msg);
			
			return;
		}

		//查询当前发送数量=总数量时任务结束
		t_red_packet_task currentTask = redpacketService.queryTaskById(task.id);
		if (currentTask.is_send_number == currentTask.total_number) {
			result = redpacketService.updateRedPacketTaskStatus(task.id);
			if(result.code < 1){
				LoggerUtil.error(true, "更新任务状态失败：%s，原因：%s", result.msg);
				
				return;
			}
		}
	}
	
	
}
