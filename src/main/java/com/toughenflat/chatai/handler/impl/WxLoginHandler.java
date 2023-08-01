package com.toughenflat.chatai.handler.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.toughenflat.chatai.entity.UserEntity;
import com.toughenflat.chatai.entity.UserLoginRecord;
import com.toughenflat.chatai.enums.UserLevel;
import com.toughenflat.chatai.handler.LoginHandler;
import com.toughenflat.chatai.service.UserLoginRecordService;
import com.toughenflat.chatai.service.UserService;
import com.toughenflat.chatai.session.LoginSession;
import com.toughenflat.chatai.utils.JwtUtil;
import com.toughenflat.chatai.utils.OkHttpClientUtil;
import com.toughenflat.chatai.utils.SequentialUuidHexGenerator;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service(value = "WxLoginHandler")
public class WxLoginHandler implements LoginHandler {
    public static final String HOST = "https://api.weixin.qq.com/sns/jscode2session";

    private static final String MAP_KEY_USERNAME = "username";
    private static final String MAP_KEY_USER_ID = "userId";
    private static final String MAP_KEY_USER_LEVEL = "userLevel";
    private static final String MAP_KEY_USER_TOKEN = "token";

    @Resource
    private UserService userService;

    @Resource
    private UserLoginRecordService userLoginRecordService;

    @Resource
    private TaskExecutor queueThreadPool;

    @Override
    public Map<String, Object> login(LoginSession loginSession) {
        Map<String, Object> loginMap = new HashMap<>(5);

        // 登录凭证校验
        String requestHost = HOST + "?appid=" + loginSession.getAppId() + "&secret=" + loginSession.getAppSecret() + "&js_code=" + loginSession.getCode() + "&grant_type=authorization_code";
        Request request = new Request.Builder()
                .url(requestHost)
                .build();
        try (Response response = OkHttpClientUtil.getClient().newCall(request).execute()) {
            assert response.isSuccessful();
            String wxBody = response.body().string();
            JSONObject wxJsonBody = JSONObject.parseObject(wxBody);
            Long errcode = wxJsonBody.getLong("errcode");
            String openid = wxJsonBody.getString("openid");
            String sessionKey = wxJsonBody.getString("session_key");
            String unionid = wxJsonBody.getString("unionid");

            // 微信登录凭证无效
            if (errcode == 40029) {
                loginMap.put("err", "登录认证失败");
                return loginMap;
            }
            // API调用太频繁
            if (errcode == 45011) {
                loginMap.put("err", "系统繁忙, 请稍后再试");
                return loginMap;
            }
            // 高风险用户被禁止登录
            if (errcode == 40226) {
                loginMap.put("err", "用户被禁止登录, 请联系管理员");
                return loginMap;
            }
            // 系统繁忙, 稍后再试
            if (errcode == -1) {
                loginMap.put("err", "系统繁忙, 请稍后再试");
                return loginMap;
            }

            // 登录成功, 存储用户信息返回数据
            LambdaQueryWrapper<UserEntity> userQuery = new LambdaQueryWrapper<>();
            userQuery.eq(UserEntity::getUnionid, unionid);
            UserEntity userEntity = userService.getOne(userQuery);
            if (userEntity == null) {       // 新登录的用户
                userEntity = UserEntity.builder()
                        .id(SequentialUuidHexGenerator.generate())
                        .nickname(loginSession.getLoginAcct())
                        .header(loginSession.getAvatar())
                        .level(UserLevel.NORMAL.levelNo)
                        .openid(openid)
                        .unionid(unionid)
                        .sessionKey(sessionKey)
                        .createTime(new Date())
                        .updateTime(new Date()).build();
                userService.save(userEntity);
            } else {                    // 旧用户登录
                UpdateWrapper<UserEntity> userUpdate = new UpdateWrapper<>();
                userUpdate.eq("unionid", unionid);
                userEntity.setNickname(loginSession.getLoginAcct());
                userEntity.setHeader(loginSession.getAvatar());
                userEntity.setSessionKey(sessionKey);
                userEntity.setUpdateTime(new Date());
                userService.update(userEntity, userUpdate);
            }
            loginMap.put(MAP_KEY_USERNAME, userEntity.getNickname());
            loginMap.put(MAP_KEY_USER_ID, userEntity.getId());
            loginMap.put(MAP_KEY_USER_LEVEL, String.valueOf(userEntity.getLevel()));
            loginMap.put(MAP_KEY_USER_TOKEN, JwtUtil.createToken(userEntity.getId(), userEntity.getNickname(), userEntity.getLevel()));

            // 登录入库
            UserEntity finalUserEntity = userEntity;
            queueThreadPool.execute(() -> userLoginRecordService.save(
                    UserLoginRecord.builder()
                            .userId(finalUserEntity.getId())
                            .loginType(loginSession.getLoginType().typeNo)
                            .loginIp(loginSession.getIp())
                            .build())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return loginMap;
    }
}
