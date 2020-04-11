package com.jiezi.emotion.shiro;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jiezi.emotion.common.Constants;
import com.jiezi.emotion.common.util.JWTUtil;
import com.jiezi.emotion.config.redis.JedisUtil;
import com.jiezi.emotion.sys.user.entity.Role;
import com.jiezi.emotion.sys.user.entity.User;
import com.jiezi.emotion.sys.user.service.UserService;

@Service
public class CustomRealm extends AuthorizingRealm {

//    private static final Logger LOGGER = LogManager.getLogger(CustomRealm.class);

    private  UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }


    /**
     * 必须重写此方法，不然会报错
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JWTToken;
    }

    /**
     * 默认使用此方法进行用户名正确与否验证，错误抛出异常即可。
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        System.out.println("————身份认证方法————");
        String token = (String) authenticationToken.getCredentials();
        // 解密获得username，用于和数据库进行对比
        String username = JWTUtil.getClaim(token, Constants.ACCOUNT);
        Optional<User> user = userService.getUser(username);
        if (!user.isPresent()) {
            throw new AuthenticationException("该用户不存在！");
        }
        if (user.get().getBan() == 1) {
            throw new AuthenticationException("该用户已被封号！");
        }
        // 开始认证，要AccessToken认证通过，且Redis中存在RefreshToken，且两个Token时间戳一致
        if(JWTUtil.verify(token) && JedisUtil.exists(Constants.PREFIX_SHIRO_REFRESH_TOKEN + username)){
            // 获取RefreshToken的时间戳
            String currentTimeMillisRedis = JedisUtil.getObject(Constants.PREFIX_SHIRO_REFRESH_TOKEN + username).toString();
            // 获取AccessToken时间戳，与RefreshToken的时间戳对比
            if(JWTUtil.getClaim(token, Constants.CURRENT_TIME_MILLIS).equals(currentTimeMillisRedis)){
                return new SimpleAuthenticationInfo(token, token, "CustomRealm");
            }
        }
        throw new AuthenticationException("Token已过期(Token expired or incorrect.)");
    }
   

    /**
     * 只有当需要检测用户权限的时候才会调用此方法，例如checkRole,checkPermission之类的
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        System.out.println("————权限认证————");
        String username = JWTUtil.getClaim(principals.toString(), Constants.ACCOUNT);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        Optional<User> user = userService.getUser(username);
        
        //获得该用户角色
        Set<String> roleSet = new HashSet<>();
        for (Role role : user.get().getRoles()) {
        	roleSet.add(role.getRoleName());
        }
        // 根据用户查询当前用户权限
        Set<String> permissionSet = userService.findStringPermissions(user);
        //设置该用户拥有的角色和权限
        info.setRoles(roleSet);
        info.setStringPermissions(permissionSet);
        return info;
    }
}