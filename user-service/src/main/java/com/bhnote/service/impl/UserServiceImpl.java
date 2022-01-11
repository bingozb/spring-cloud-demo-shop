package com.bhnote.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bhnote.enums.BizCodeEnum;
import com.bhnote.interceptor.LoginInterceptor;
import com.bhnote.mapper.UserMapper;
import com.bhnote.model.LoginUser;
import com.bhnote.model.UserDO;
import com.bhnote.model.ro.UserLoginRequest;
import com.bhnote.model.ro.UserRegisterRequest;
import com.bhnote.model.vo.UserVO;
import com.bhnote.service.UserService;
import com.bhnote.utils.CodeUtil;
import com.bhnote.utils.JsonData;
import com.bhnote.utils.JwtUtil;
import com.bhnote.utils.SnowFlakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Bingo
 * @since 2022-01-05
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public JsonData register(UserRegisterRequest userRegisterRequest) {
        // 检查唯一性
        Integer count = userMapper.selectCount(new QueryWrapper<UserDO>().eq("username", userRegisterRequest.getUsername()));
        if (count > 0) {
            return JsonData.buildError(BizCodeEnum.ACCOUNT_REPEAT);
        }
        // 注册
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userRegisterRequest, userDO);
        userDO.setId(SnowFlakeUtil.getInstance().nextId());
        // 生成秘钥
        userDO.setSecret("$1$" + CodeUtil.randomNumberAndLetter(8));
        // 密码加盐处理
        String cryptPwd = Md5Crypt.md5Crypt(userRegisterRequest.getPassword().getBytes(), userDO.getSecret());
        userDO.setPassword(cryptPwd);
        int rows = userMapper.insert(userDO);
        log.info("账号注册:{}, rows:{}", userDO, rows);
        return JsonData.buildSuccess();
    }

    @Override
    public JsonData login(UserLoginRequest userLoginRequest) {
        // 校验账号是否存在
        List<UserDO> userDOList = userMapper.selectList(new QueryWrapper<UserDO>().eq("username", userLoginRequest.getUsername()));
        if (userDOList == null || userDOList.size() != 1) {
            return JsonData.buildError(BizCodeEnum.ACCOUNT_UNREGISTER);
        }
        // 校验密码
        UserDO userDO = userDOList.get(0);
        String cryptPwd = Md5Crypt.md5Crypt(userLoginRequest.getPassword().getBytes(), userDO.getSecret());
        if (!StringUtils.equals(cryptPwd, userDO.getPassword())) {
            return JsonData.buildError(BizCodeEnum.ACCOUNT_PWD_ERROR);
        }
        // 登录成功，JWT生成token
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(userDO, loginUser);
        String token = JwtUtil.generateToken(loginUser);
        return JsonData.buildSuccess(token);
    }

    @Override
    public JsonData getUserDetail() {
        UserDO userDO = userMapper.selectById(LoginInterceptor.threadLocal.get().getId());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userDO, userVO);
        return JsonData.buildSuccess(userVO);
    }
}
