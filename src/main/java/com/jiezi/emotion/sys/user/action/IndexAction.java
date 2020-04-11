package com.jiezi.emotion.sys.user.action;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.jiezi.emotion.common.ClearCascadeJSON;
import com.jiezi.emotion.common.Constants;
import com.jiezi.emotion.common.ServiceException;
import com.jiezi.emotion.common.entity.CommonResponse;
import com.jiezi.emotion.common.entity.CurrentUser;
import com.jiezi.emotion.common.entity.ErrorCode;
import com.jiezi.emotion.common.util.JWTUtil;
import com.jiezi.emotion.config.redis.JedisUtil;
import com.jiezi.emotion.sys.user.entity.User;
import com.jiezi.emotion.sys.user.service.UserService;

@Controller
@PropertySource("classpath:config.properties")
public class IndexAction {

	@Autowired
	private UserService userService;


	/**
	 * RefreshToken过期时间
	 */
	@Value("${refreshTokenExpireTime}")
	private String refreshTokenExpireTime;

	private ClearCascadeJSON clearCascadeJSON;
	{
		clearCascadeJSON = ClearCascadeJSON.get().addRetainTerm(User.class, "id", "username", "password", "realname",
				"gender", "createdAt");
	}

	/**
	 * 登陆
	 * 
	 * @param request
	 * @param prodcut
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@PostMapping(value = "/login")
	@ResponseBody
	public CommonResponse userLogin(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "password", required = false) String password) {
		CommonResponse cr = new CommonResponse();
		CurrentUser currentUser = new CurrentUser();
		if (JedisUtil.exists(Constants.PREFIX_SHIRO_ACCESS_TOKEN + username)) {
			cr.setMessage("已登录");
			return cr;
		}

		Optional<User> user = userService.login(username, password);
		if (user.isPresent()) {
			currentUser.setUserName(user.get().getUsername());
			// 设置RefreshToken，时间戳为当前时间戳，直接设置即可(不用先删后设，会覆盖已有的RefreshToken)
			String currentTimeMillis = String.valueOf(System.currentTimeMillis());
			// 向缓存中存入过期时间是RefreshToken时间的refresh token的username标记
			JedisUtil.setObject(Constants.PREFIX_SHIRO_REFRESH_TOKEN + username, currentTimeMillis,
					Integer.parseInt(refreshTokenExpireTime));
			// 生成过期时间是accessTokenExpireTime时间的token，如果token的accessTokenExpireTime过期，则用缓存中的RefreshToken来刷新返回token
			String token = JWTUtil.sign(username, currentTimeMillis);
			response.setHeader(Constants.PARAM_TOKEN, token);
			response.setHeader("Access-Control-Expose-Headers",Constants.PARAM_TOKEN);
			cr.setData(currentUser);
			cr.setMessage("登陆成功");
		}else{
			cr.setCode(ErrorCode.SYSTEM_USER_NAME_NOTFOUND.getCode());
			cr.setMessage("用户名或密码错误");
		}
		return cr;
	}

	/**
	 * 获取在线用户(查询Redis中的RefreshToken)
	 * 
	 * @param
	 */
	@GetMapping("/online")
	@ResponseBody
	@RequiresPermissions(value = { "userpage:view" }, logical = Logical.AND)
	public CommonResponse online() {
		CommonResponse cr = new CommonResponse();
		List<User> userli = new ArrayList<User>();
		// 查询所有Redis键
		Set<String> keys = JedisUtil.keysS(Constants.PREFIX_SHIRO_REFRESH_TOKEN + "*");
		for (String key : keys) {
			if (JedisUtil.exists(key)) {
				// 根据:分割key，获取最后一个字符(帐号)
				String[] strArray = key.split(":");
				Optional<User> user = userService.getUser(strArray[strArray.length - 1]);
				userli.add(user.get());
			}
		}
		if (userli == null || userli.size() <= 0) {
			throw new ServiceException("查询失败(Query Failure)");
		}
		cr.setData(clearCascadeJSON.format(userli).toJSON());
		return cr;
	}

	/**
	 * 退出登陆
	 * 
	 * @param request
	 * @param prodcut
	 * @return
	 */
	@GetMapping(value = "/quitLogin")
	@ResponseBody
	public CommonResponse quitLogin(HttpServletRequest request) {
		CommonResponse cr = new CommonResponse();
		SecurityUtils.getSubject().logout();
		return cr;
	}

}
