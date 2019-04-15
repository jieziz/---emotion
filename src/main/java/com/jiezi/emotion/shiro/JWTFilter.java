package com.jiezi.emotion.shiro;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.jiezi.emotion.common.Constants;
import com.jiezi.emotion.common.ServiceException;
import com.jiezi.emotion.common.util.JWTUtil;
import com.jiezi.emotion.common.util.PropertiesUtil;
import com.jiezi.emotion.config.redis.JedisUtil;

/**
 *
 * @Author zhangliang
 * @Description preHandle->isAccessAllowed->isLoginAttempt->executeLogin
 */
public class JWTFilter extends BasicHttpAuthenticationFilter {

	// private Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 这里我们详细说明下为什么最终返回的都是true，即允许访问 例如我们提供一个地址 GET /article 登入用户和游客看到的内容是不同的
	 * 如果在这里返回了false，请求会被直接拦截，用户看不到任何东西 所以我们在这里返回true，Controller中可以通过
	 * subject.isAuthenticated() 来判断用户是否登入
	 * 如果有些资源只有登入用户才能访问，我们只需要在方法上面加上 @RequiresAuthentication 注解即可
	 * 但是这样做有一个缺点，就是不能够对GET,POST等请求进行分别过滤鉴权(因为我们重写了官方的方法)，但实际上对应用影响不大
	 */
	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
		// 判断用户是否想要登入
		if (this.isLoginAttempt(request, response)) {
			try {
				// 进行Shiro的登录UserRealm
				this.executeLogin(request, response);
			} catch (Exception e) {
				// 认证出现异常，传递错误信息msg
				String msg = e.getMessage();
				// 获取应用异常(该Cause是导致抛出此throwable(异常)的throwable(异常))
				Throwable throwable = e.getCause();
				if (throwable != null && throwable instanceof SignatureVerificationException) {
					// 该异常为JWT的AccessToken认证失败(Token或者密钥不正确)
					msg = "Token或者密钥不正确(" + throwable.getMessage() + ")";
				} else if (throwable != null && throwable instanceof TokenExpiredException) {
					// 该异常为JWT的AccessToken已过期，判断RefreshToken未过期就进行AccessToken刷新
					if (this.refreshToken(request, response)) {
						return true;
					} else {
						msg = "Token已过期(" + throwable.getMessage() + ")";
					}
				} else {
					// 应用异常不为空
					if (throwable != null) {
						// 获取应用异常msg
						msg = throwable.getMessage();
					}
				}
				throw new ServiceException(msg);
			}
		}
		return true;
	}

	/**
	 * 判断用户是否想要登入。 检测 header 里面是否包含 Token 字段
	 */
	@Override
	protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
		HttpServletRequest req = (HttpServletRequest) request;
		String token = req.getHeader(Constants.PARAM_TOKEN);
		return token != null;
	}

	/**
	 * 进行AccessToken登录认证授权
	 */
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String token = httpServletRequest.getHeader(Constants.PARAM_TOKEN);
		JWTToken jwtToken = new JWTToken(token);
		// 提交给realm进行登入，如果错误他会抛出异常并被捕获
		getSubject(request, response).login(jwtToken);
		// 如果没有抛出异常则代表登入成功，返回true
		return true;
	}

	/**
	 * 此处为AccessToken刷新，进行判断RefreshToken是否过期，未过期就返回新的AccessToken且继续正常访问
	 */
	private boolean refreshToken(ServletRequest request, ServletResponse response) {
		// 拿到当前Header中Authorization的AccessToken(Shiro中getAuthzHeader方法已经实现)
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String token = httpServletRequest.getHeader(Constants.PARAM_TOKEN);
		// 获取当前Token的帐号信息
		String account = JWTUtil.getClaim(token, Constants.ACCOUNT);
		// 判断Redis中RefreshToken是否存在
		if (JedisUtil.exists(Constants.PREFIX_SHIRO_REFRESH_TOKEN + account)) {
			// Redis中RefreshToken还存在，获取RefreshToken的时间戳
			String currentTimeMillisRedis = JedisUtil.getObject(Constants.PREFIX_SHIRO_REFRESH_TOKEN + account)
					.toString();
			// 获取当前AccessToken中的时间戳，与RefreshToken的时间戳对比，如果当前时间戳一致，进行AccessToken刷新
			if (JWTUtil.getClaim(token, Constants.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)) {
				// 获取当前最新时间戳
				String currentTimeMillis = String.valueOf(System.currentTimeMillis());
				// 读取配置文件，获取refreshTokenExpireTime属性
				PropertiesUtil.readProperties("config.properties");
				String refreshTokenExpireTime = PropertiesUtil.getProperty("refreshTokenExpireTime");
				// 设置RefreshToken中的时间戳为当前最新时间戳，且刷新过期时间重新为30分钟过期(配置文件可配置refreshTokenExpireTime属性)
				JedisUtil.setObject(Constants.PREFIX_SHIRO_REFRESH_TOKEN + account, currentTimeMillis,
						Integer.parseInt(refreshTokenExpireTime));
				// 刷新AccessToken，设置时间戳为当前最新时间戳
				token = JWTUtil.sign(account, currentTimeMillis);
				// 将新刷新的AccessToken再次进行Shiro的登录
				JWTToken jwtToken = new JWTToken(token);
				// 提交给UserRealm进行认证，如果错误他会抛出异常并被捕获，如果没有抛出异常则代表登入成功，返回true
				this.getSubject(request, response).login(jwtToken);
				// 最后将刷新的AccessToken存放在Response的Header中的Authorization字段返回
				HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.setHeader(Constants.PARAM_TOKEN, token);
                httpServletResponse.setHeader("Access-Control-Expose-Headers", Constants.PARAM_TOKEN);
				return true;
			}
		}
		return false;
	}

	/**
	 * 对跨域提供支持
	 */
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,OPTIONS,PUT,DELETE");
		httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
		// 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
		if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
			httpServletResponse.setStatus(HttpStatus.OK.value());
			return false;
		}
		return super.preHandle(request, response);
	}
}