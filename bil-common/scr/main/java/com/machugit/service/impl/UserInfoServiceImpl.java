package com.machugit.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.machugit.component.RedisComponent;
import com.machugit.entity.constants.Constants;
import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.enums.UserSexEnum;
import com.machugit.entity.enums.UserStatusEnum;
import com.machugit.exception.BusinessException;
import com.machugit.utils.CopyTools;
import org.springframework.stereotype.Service;
import io.seata.spring.annotation.GlobalTransactional;

import com.machugit.entity.enums.PageSize;
import com.machugit.entity.po.UserExpLog;
import com.machugit.entity.query.UserInfoQuery;
import com.machugit.entity.po.UserInfo;
import com.machugit.entity.vo.PaginationResultVO;
import com.machugit.entity.query.SimplePage;
import com.machugit.mappers.UserInfoMapper;
import com.machugit.service.UserInfoService;
import com.machugit.utils.StringTools;

import static com.machugit.entity.constants.Constants.length_10;


/**
 * 用户信息表 业务接口实现
 */
@Service("userInfoService")
public class UserInfoServiceImpl implements UserInfoService {

	@Resource
	private RedisComponent redisComponent;

	@Resource
	private UserInfoMapper<UserInfo, UserInfoQuery> userInfoMapper;

	@Resource
	private UserExpLogServiceImpl userExpLogService;

	/**
	 * 根据条件查询列表
	 */
	@Override
	public List<UserInfo> findListByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectList(param);
	}

	/**
	 * 根据条件查询列表
	 */
	@Override
	public Integer findCountByParam(UserInfoQuery param) {
		return this.userInfoMapper.selectCount(param);
	}

	/**
	 * 分页查询方法
	 */
	@Override
	public PaginationResultVO<UserInfo> findListByPage(UserInfoQuery param) {
		int count = this.findCountByParam(param);
		int pageSize = param.getPageSize() == null ? PageSize.SIZE15.getSize() : param.getPageSize();

		SimplePage page = new SimplePage(param.getPageNo(), count, pageSize);
		param.setSimplePage(page);
		List<UserInfo> list = this.findListByParam(param);
		PaginationResultVO<UserInfo> result = new PaginationResultVO(count, page.getPageSize(), page.getPageNo(), page.getPageTotal(), list);
		return result;
	}

	/**
	 * 新增
	 */
	@Override
	public Integer add(UserInfo bean) {
		return this.userInfoMapper.insert(bean);
	}

	/**
	 * 批量新增
	 */
	@Override
	public Integer addBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertBatch(listBean);
	}

	/**
	 * 批量新增或者修改
	 */
	@Override
	public Integer addOrUpdateBatch(List<UserInfo> listBean) {
		if (listBean == null || listBean.isEmpty()) {
			return 0;
		}
		return this.userInfoMapper.insertOrUpdateBatch(listBean);
	}

	/**
	 * 多条件更新
	 */
	@Override
	public Integer updateByParam(UserInfo bean, UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.updateByParam(bean, param);
	}

	/**
	 * 多条件删除
	 */
	@Override
	public Integer deleteByParam(UserInfoQuery param) {
		StringTools.checkParam(param);
		return this.userInfoMapper.deleteByParam(param);
	}

	/**
	 * 根据UserId获取对象
	 */
	@Override
	public UserInfo getUserInfoByUserId(String userId) {
		return this.userInfoMapper.selectByUserId(userId);
	}

	/**
	 * 根据UserId修改
	 */
	@Override
	public Integer updateUserInfoByUserId(UserInfo bean, String userId) {
		return this.userInfoMapper.updateByUserId(bean, userId);
	}

	/**
	 * 根据UserId删除
	 */
	@Override
	public Integer deleteUserInfoByUserId(String userId) {
		return this.userInfoMapper.deleteByUserId(userId);
	}

	/**
	 * 根据UseName获取对象
	 */
	@Override
	public UserInfo getUserInfoByUseName(String useName) {
		return this.userInfoMapper.selectByUseName(useName);
	}

	/**
	 * 根据UseName修改
	 */
	@Override
	public Integer updateUserInfoByUseName(UserInfo bean, String useName) {
		return this.userInfoMapper.updateByUseName(bean, useName);
	}

	/**
	 * 根据UseName删除
	 */
	@Override
	public Integer deleteUserInfoByUseName(String useName) {
		return this.userInfoMapper.deleteByUseName(useName);
	}

	/**
	 * 根据Email获取对象
	 */
	@Override
	public UserInfo getUserInfoByEmail(String email) {
		return this.userInfoMapper.selectByEmail(email);
	}

	/**
	 * 根据Email修改
	 */
	@Override
	public Integer updateUserInfoByEmail(UserInfo bean, String email) {
		return this.userInfoMapper.updateByEmail(bean, email);
	}

	/**
	 * 根据Email删除
	 */
	@Override
	public Integer deleteUserInfoByEmail(String email) {
		return this.userInfoMapper.deleteByEmail(email);
	}

	/**
     * 注册验证
     */
	@Override
	@GlobalTransactional(name = "user:register", rollbackFor = Exception.class)
    public void register(String email, String useName, String registerPassword) {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if (null != userInfo) {
			throw new BusinessException("邮箱已注册");
		}
		UserInfo useInfoName = this.userInfoMapper.selectByUseName(useName);
		if (null != useInfoName) {
			throw new BusinessException("用户名已存在");
		}
		if (registerPassword == null || !registerPassword.matches(Constants.REGEX_PASSWORD)) {
			throw new BusinessException("密码必须包含字母和数字，长度6-20位");
		}
		userInfo = new UserInfo();
		String userId = StringTools.getRandomNumber(length_10);
		userInfo.setUserId(userId);
		userInfo.setUseName(useName);
		userInfo.setEmail(email);
		userInfo.setPassword(StringTools.encodeByMd5(registerPassword));
		userInfo.setJoinTime(new Date());
		userInfo.setStatus(UserStatusEnum.ENABLE.getStatus());
		userInfo.setSex(UserSexEnum.UNKNOWN.getSex());
		userInfo.setTheme(Constants.ONE);
		userInfo.setLevel(1);
		userInfo.setExp(0);
		//TODO 初始化用户的硬币
		userInfo.setCurrentCoinCount(Constants.ONE);
		userInfo.setTotalCoinCount(Constants.ONE);
		this.userInfoMapper.insert(userInfo);
	}

	/**
	 * 根据关键词搜索用户
	 */
	@Override
	public List<UserInfo> searchUsers(String keyword) {
		UserInfoQuery query = new UserInfoQuery();
		query.setUseNameFuzzy(keyword);
		query.setStatus(1);
		return this.userInfoMapper.selectList(query);
	}

	@Override
	public TokenUserInfoDto login(String email, String password, String ip) {
		UserInfo userInfo = this.userInfoMapper.selectByEmail(email);
		if(null == userInfo || !userInfo.getPassword().equals(password)) {
			throw new BusinessException("用户名或密码错误");
		}
		if(UserStatusEnum.DISABLE.getStatus().equals(userInfo.getStatus())) {
			throw new BusinessException("用户已被禁用");
		}
		UserInfo updateInfo = new UserInfo();
		updateInfo.setLastLoginTime(new Date());
		updateInfo.setLastLoginIp(ip);
		this.userInfoMapper.updateByUserId(updateInfo, userInfo.getUserId());

		// Daily login exp bonus (+5 exp, sourceType=1)
		boolean alreadyGotToday = false;
		java.util.List<UserExpLog> expLogs = userExpLogService.loadExpLog(userInfo.getUserId());
		if (expLogs != null) {
			java.util.Calendar todayCal = java.util.Calendar.getInstance();
			java.util.Calendar logCal = java.util.Calendar.getInstance();
			for (UserExpLog log : expLogs) {
				if (log.getSourceType() != null && log.getSourceType() == 1 && log.getCreateTime() != null) {
					todayCal.setTime(new Date());
					logCal.setTime(log.getCreateTime());
					if (todayCal.get(java.util.Calendar.YEAR) == logCal.get(java.util.Calendar.YEAR)
							&& todayCal.get(java.util.Calendar.DAY_OF_YEAR) == logCal.get(java.util.Calendar.DAY_OF_YEAR)) {
						alreadyGotToday = true;
						break;
					}
				}
			}
		}
		if (!alreadyGotToday) {
			userExpLogService.addExp(userInfo.getUserId(), 5, 1, null);
		}

		TokenUserInfoDto tokenUserInfoDto = CopyTools.copy(userInfo, TokenUserInfoDto.class);
		redisComponent.saveTokenInfo(tokenUserInfoDto);
		return tokenUserInfoDto;
	}

    @Override
    public void deductCoin(String userId, Integer count) {
        // TODO: implement deduct coin logic
    }
}