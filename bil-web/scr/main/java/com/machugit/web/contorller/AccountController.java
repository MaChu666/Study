package com.machugit.web.contorller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.machugit.component.RedisComponent;
import com.machugit.entity.constants.Constants;
import com.machugit.entity.dto.TokenUserInfoDto;
import com.machugit.entity.vo.ResponseVO;
import com.machugit.exception.BusinessException;
import com.machugit.service.impl.UserInfoServiceImpl;
import com.wf.captcha.ArithmeticCaptcha;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@RestController
@RequestMapping("/account")
@Validated
public class AccountController extends ABaseController{

	private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

	@Resource
	private UserInfoServiceImpl userInfoService;

	@Resource
	private RedisComponent redisComponent;

	@RequestMapping("/checkCode")
	public ResponseVO chekCode() {
		ArithmeticCaptcha captcha = new ArithmeticCaptcha(100, 40);
		String code = captcha.text();
		String checkCodeKey = redisComponent.saveChackCode(code);
		String checkCodeBase64 = captcha.toBase64();

		Map<String, String> checkmap = new HashMap<>();
		checkmap.put("checkCode", checkCodeBase64);
		checkmap.put("checkCodeKey", checkCodeKey);
		return getSuccessResponseVO(checkmap);
	}

	@RequestMapping("/register")
	public ResponseVO register(@NotEmpty @Email @Size(max = 150) String email,
							   @NotEmpty @Size(max = 20) String useName,
							   @NotEmpty @Pattern(regexp = Constants.REGEX_PASSWORD) String registerPassword,
							   @NotEmpty String checkCodeKey,
							   @NotEmpty String checkCode) {
		try{
			String codeFromRedis = redisComponent.getCheckCode(checkCodeKey);
			if(codeFromRedis == null || !checkCode.trim().equalsIgnoreCase(codeFromRedis)){
				throw new BusinessException("验证码错误");
			}
			userInfoService.register(email,useName,registerPassword);
			return getSuccessResponseVO(null);
		}finally {
			redisComponent.clearCheckCode(checkCodeKey);
		}
	}

	@RequestMapping("/login")
	public ResponseVO login(HttpServletRequest request,
							HttpServletResponse response,
							@NotEmpty @Email @Size(max = 150) String email,
							@NotEmpty String password,
							@NotEmpty String checkCodeKey,
							@NotEmpty String checkCode) {
		try{
			String codeFromRedis = redisComponent.getCheckCode(checkCodeKey);
			if(codeFromRedis == null || !checkCode.trim().equalsIgnoreCase(codeFromRedis)){
				throw new BusinessException("验证码错误");
			}
			String ip =getIpAddr();
			TokenUserInfoDto tokenUserInfoDto=userInfoService.login(email,password,ip);
			saveToken2Cookie(response,tokenUserInfoDto.getToken());
			//TODO 设置 粉丝数，关注数，银币数
			return getSuccessResponseVO(tokenUserInfoDto);
		}finally {
			redisComponent.clearCheckCode(checkCodeKey);
			Cookie[] cookies = request.getCookies();
		}
	}

	@RequestMapping("/autologin")
	public ResponseVO autoLogin(HttpServletResponse response) {
		TokenUserInfoDto tokenUserInfoDto = getTokenUserInfoDto();
		if(tokenUserInfoDto==null){
			return getSuccessResponseVO(null);
        }
		if(tokenUserInfoDto.getExpireAt()-System.currentTimeMillis()<Constants.REDIS_KEY_EXPIRE_TIME_ONE_DAY){
			redisComponent.saveTokenInfo(tokenUserInfoDto);
			saveToken2Cookie(response,tokenUserInfoDto.getToken());
		}
		saveToken2Cookie(response,tokenUserInfoDto.getToken());
		//TODO 设置 粉丝数，关注数，银币数
		return getSuccessResponseVO(tokenUserInfoDto);
	}

	@RequestMapping("/logout")
	public ResponseVO logout(HttpServletResponse response){
		removeTokenFromCookie(response);
		return getSuccessResponseVO(null);
	}
}
