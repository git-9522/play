package services.common;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import common.utils.Factory;
import common.utils.PageBean;
import common.utils.ResultInfo;
import daos.common.GroupMenbersDao;
import daos.common.GroupMenbersUserDao;
import models.common.bean.ShowGroupMenberInfo;
import models.common.entity.t_group_menbers;
import models.common.entity.t_group_menbers_user;
import models.common.entity.t_user_info;
import services.base.BaseService;

/**
 * 会员分组信息表service接口
 *
 * @description 
 *
 * @author jiayijian
 * @createDate 2017年3月31日
 */
public class GroupMenbersService extends BaseService<t_group_menbers>{
	
	protected GroupMenbersUserDao groupMenbersUserDao = Factory.getDao(GroupMenbersUserDao.class);
	
	protected GroupMenbersDao groupMenbersDao = Factory.getDao(GroupMenbersDao.class);
	
	protected GroupMenbersService() {
		super.basedao = this.groupMenbersDao;
	}
	
	/**
	 * 后台-会员管理-会员分组列表
	 * 
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijan
	 * @createDate 2017年03月31日
	 *
	 */
	public PageBean<t_group_menbers> pageOfGroupMenbers( int currPage, int pageSize, String userName) {
		
		return groupMenbersDao.pageOfGroupMenbers( currPage, pageSize, userName);
	}
	
	/**
	 * 后台-会员管理-编辑分组
	 * 
	 * @param gId
	 * @param groupName
	 *
	 * @author jiayijan
	 * @createDate 2017年03月31日
	 *
	 */
	public ResultInfo editGroupName( long gId, String groupName) {
		
		ResultInfo result = new ResultInfo();
		
		if(StringUtils.isBlank(groupName)){
			
			result.code = -1;
			result.msg ="请填写分组名称";
			
			return result;
		}
		
		t_group_menbers group  = groupMenbersDao.findByID(gId);
		
		if(group == null){
			
			result.code = -1;
			result.msg ="该分组不存在";
			
			return result;
		}
		
		group.name = groupName;
		group.last_edit_time = new Date();
		
		if(!groupMenbersDao.save(group)){
			
			result.code = -1;
			result.msg ="保存分组失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg ="保存分组成功";
		return result;
	}
	
	
	/**
	 * 后台-会员管理-添加分组
	 * 
	 * @param groupName
	 *
	 * @author jiayijan
	 * @createDate 2017年03月31日
	 *
	 */
	public ResultInfo addGroupName(String groupName) {
		
		ResultInfo result = new ResultInfo();
		
		if(StringUtils.isBlank(groupName)){
			
			result.code = -1;
			result.msg ="请填写分组名称";
			
			return result;
		}
		
		if(isExistGroupName(groupName)){
			
			result.code = -1;
			result.msg ="该分组已存在";
			
			return result;
		}
		
		
		t_group_menbers group  = new t_group_menbers();
		
		group.time = new Date();
		group.name = groupName;
		
		
		if(!groupMenbersDao.save(group)){
			
			result.code = -1;
			result.msg ="保存分组失败";
			
			return result;
		}
		
		result.obj = group;
		result.code = 1;
		result.msg ="保存分组成功";
		return result;
	}
	
	/**
	 * 该分组是否已存在
	 * @param groupName
	 * @return 已存在 true
	 */
	public boolean isExistGroupName(String groupName){
		
		t_group_menbers group  = groupMenbersDao.findByColumn(" name = ? ", groupName);
		
		if(group != null){
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * 后台-会员管理-删除分组
	 * 
	 * @param groupName
	 *
	 * @author jiayijan
	 * @createDate 2017年03月31日
	 *
	 */
	public ResultInfo delGroupMenber(long gId) {
		
		ResultInfo result = new ResultInfo();
		
		if(gId <= 0){
			
			result.code = -1;
			result.msg ="参数错误";
			
			return result;
		}
		
		if(groupMenbersDao.delete(gId) != 1){
			
			result.code = -1;
			result.msg ="删除分组失败";
			
			return result;
		}
		
		int count = groupMenbersUserDao.countMenbersUser(gId);
		
		if(count > 0){
			
			int row = groupMenbersUserDao.delMenbersUser(gId);
			
			if(row < 1){
				
				result.code = -1;
				result.msg ="删除分组成员失败";
				
				return result;
			}
		}
		
		result.code = 1;
		result.msg ="删除分组成功";
		return result;
	}

	
	/**
	 * 后台-会员管理-会员分组成员列表
	 * @param currPage
	 * @param pageSize
	 *
	 * @author jiayijain
	 * @createDate 2017年4月1日
	 */
	public PageBean<ShowGroupMenberInfo> pageOfGroupMenbersUse(int gId, int currPage, int pageSize,String userName) {
		
		return groupMenbersUserDao.pageOfGroupMenbersUse( gId, currPage,  pageSize, userName);
	}

	
	/**
	 *  后台-会员管理-会员分组成员列表-添加成员
	 * 
	 * @author jiayijain
	 * @createDate 2017年4月1日
	 */
	public ResultInfo addGroupMenberUser(t_group_menbers group,t_user_info userInfo) {
		
		ResultInfo result = new ResultInfo();
		
		t_group_menbers_user menbUser = findMenberUserInfo(group.id, userInfo.user_id);
		
		if(menbUser != null){
			result.code = -1;
			result.msg ="该成员已存在";
			
			return result;
		}
		
		menbUser  = new t_group_menbers_user();
		
		menbUser.time = new Date();
		menbUser.group_name = group.name;
		menbUser.group_id = group.id;
		menbUser.user_id = userInfo.user_id;
		
		if(!groupMenbersUserDao.save(menbUser)){
			
			result.code = -1;
			result.msg ="保存分组成员失败";
			
			return result;
		}
		
		int row = groupMenbersDao.updateMenberCountAdd(group.id,1);
		
		if(row < 1){
			
			result.code = -1;
			result.msg ="更新会员人数失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg ="保存分组成员成功";
		return result;
	}
	
	/**
	 *  后台-会员管理-会员分组成员列表-添加成员
	 * 
	 * @param mId 成员记录id
	 * @param gId 分组记录id
	 * 
	 * @author jiayijain
	 * @createDate 2017年4月1日
	 */
	public ResultInfo delGroupMenberUser(long mId,long gId) {
		
		ResultInfo result = new ResultInfo();
		
		if(groupMenbersUserDao.delete(mId) != 1){
			
			result.code = -1;
			result.msg ="删除分组成员失败";
			
			return result;
		}
		
		int row = groupMenbersDao.updateMenberCountMinus(gId,1);
		
		if(row < 1){
			
			result.code = -1;
			result.msg ="更新会员人数失败";
			
			return result;
		}
		
		result.code = 1;
		result.msg ="删除分组成员成功";
		return result;
	}
	
	/**
	 *  后台-会员管理-会员分组成员列表-查询分组成员记录
	 * 
	 * @param mIg 成员记录id
	 * 
	 * @author jiayijain
	 * @createDate 2017年4月1日
	 */
	public t_group_menbers_user findMenberUserById(long mId){
		
		return groupMenbersUserDao.findByID(mId);
	}
	
	/**
	 * 投资标准备查询分组关系
	 * 
	 * @param gId 分组记录id
	 * @param userId 
	 * 
	 * @author jiayijain
	 * @createDate 2017年4月1日
	 * @return
	 */
	public t_group_menbers_user findMenberUserInfo(long gId,long userId){
		
		return groupMenbersUserDao.findMenberUserInfo(gId,userId);
	}
	
}
