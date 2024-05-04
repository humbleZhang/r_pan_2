package com.imooc.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.cache.core.constants.CacheConstants;
import com.imooc.pan.core.exception.RPanBusinessException;
import com.imooc.pan.core.response.ResponseCode;
import com.imooc.pan.core.utils.IdUtil;
import com.imooc.pan.core.utils.JwtUtil;
import com.imooc.pan.core.utils.PasswordUtil;
import com.imooc.pan.server.modules.file.constants.FileConstants;
import com.imooc.pan.server.modules.file.context.CreateFolderContext;
import com.imooc.pan.server.modules.file.entity.RPanUserFile;
import com.imooc.pan.server.modules.file.service.IUserFileService;
import com.imooc.pan.server.modules.user.constants.UserConstants;
import com.imooc.pan.server.modules.user.context.*;
import com.imooc.pan.server.modules.user.converter.UserConverter;
import com.imooc.pan.server.modules.user.entity.RPanUser;
import com.imooc.pan.server.modules.user.service.IUserService;
import com.imooc.pan.server.modules.user.mapper.RPanUserMapper;
import com.imooc.pan.server.modules.user.vo.UserInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
* @author 张海生
* @description 针对表【r_pan_user(用户信息表)】的数据库操作Service实现
* @createDate 2024-03-05 22:56:17
*/
@Service(value =  "userService")
public class UserServiceImpl extends ServiceImpl<RPanUserMapper, RPanUser>
    implements IUserService {

    @Autowired
    private UserConverter userConverter;

    @Autowired
    private IUserFileService iUserFileService;

    @Autowired
    private CacheManager cacheManager;



    /**
     * 用户注册的业务实现
     * 苏要实现的功能点
     * 注册用户信息
     * 创建新用户的根本目录信息
     *
     * 需要实现的技术难点
     * 该业务是幂等的
     * 要保证用户名全局唯一
     *
     * 实现技术难点的处理方案
     * 幂等性通过对数据库表对于用户名字添加唯一索引  我们上有业务捕获对应的冲突异常，转化返回
     *
     * @param userRegisterContext
     * @return
     */
    @Override
    public Long register(UserRegisterContext userRegisterContext) {
        assembleUserEntity(userRegisterContext);
        doRegister(userRegisterContext);
        createUserRootFolder(userRegisterContext);
        return userRegisterContext.getEntity().getUserId();
    }

    /**
     * 用户登录业务实现
     * <p>
     * 需要实现的功能：
     * 1、用户的登录信息校验
     * 2、生成一个具有时效性的accessToken
     * 3、将accessToken缓存起来，去实现单机登录
     *
     * 在实现单机登录时，将accessToken缓存起来是一个常见的做法。这样，用户只需要在首次登录时提供凭证，
     * 之后的请求就可以使用缓存中的accessToken来进行身份验证，从而简化了登录过程。以下是一个基本的实现步骤：
     *
     * 1. 设计缓存机制
     * 首先，你需要设计一个缓存机制来存储accessToken。这个缓存可以是基于内存的（如使用HashMap或ConcurrentHashMap），
     * 也可以是基于磁盘的（如使用Redis或Memcached）。如果你的应用是Web应用，也可以考虑使用Session或Cookie来存储accessToken。
     *
     * 2. 用户登录
     * 当用户首次登录时，你需要验证用户的凭证（如用户名和密码）。如果验证成功，服务器会生成一个accessToken，
     * 并将其返回给客户端。同时，你需要将这个accessToken缓存起来。
     *
     * 3. 后续请求
     * 在后续的请求中，客户端需要在请求头中携带这个accessToken。服务器在接收到请求后，会检查请求头中的accessToken。
     *
     * 如果accessToken不存在或已过期，服务器会拒绝该请求，并要求用户重新登录。
     * 如果accessToken存在且有效，服务器会从缓存中获取与accessToken相关联的用户信息，并进行后续的处理。
     * 4. 处理accessToken的过期
     * accessToken通常会有一个有效期，过期后需要重新登录或刷新。你可以设置一个定时器或监听器来定期检查并移除过期的accessToken。
     *
     * 5. 安全性考虑
     * 加密：确保accessToken在传输和存储时都是加密的，以防止泄露。
     * HTTPS：使用HTTPS来传输accessToken，以防止中间人攻击。
     * Token绑定：可以考虑将accessToken与用户的IP地址、设备信息等绑定，增加安全性。
     * 刷新Token：使用长期有效的刷新Token（refresh token）和短期有效的访问Token（access token），
     * 当访问Token过期时，可以使用刷新Token来获取新的访问Token，而不需要用户重新登录。
     * 6. 清理过期和无效的Token
     * 定期清理缓存中过期或无效的accessToken，以释放资源并减少潜在的安全风险。
     *
     * 7. 错误处理
     * 当accessToken无效或过期时，服务器应返回适当的错误码和错误信息，以便客户端进行相应的处理（如重新登录或刷新Token）。
     *
     * 总结
     * 通过缓存accessToken，你可以实现更流畅的单机登录体验。但同时也要注意安全性问题，确保accessToken的安全传输和存储。
     *
     * @param userLoginContext
     * @return
     */
    @Override
    public String login(UserLoginContext userLoginContext) {
        checkLoginInfo(userLoginContext);                                 //校验出入数据
        generateAndSaveAccessToken(userLoginContext);                     //生成和保存token
        return userLoginContext.getAccessToken();
    }

    /**
     * 用户退出登录
     * <p>
     * 1、清除用户的登录凭证缓存
     *
     * @param userId
     */
    @Override
    public void exit(Long userId) {
        try {
            Cache cache = cacheManager.getCache(CacheConstants.R_PAN_CACHE_NAME);
            cache.evict(UserConstants.USER_LOGIN_PREFIX + userId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RPanBusinessException("用户退出登录失败");
        }
    }

    /**
     * 用户忘记密码-校验用户名称
     *
     * @param checkUsernameContext
     * @return
     */
    @Override
    public String checkUsername(CheckUsernameContext checkUsernameContext) {
        String question = baseMapper.selectQuestionByUsername(checkUsernameContext.getUsername());
        if (StringUtils.isBlank(question)) {
            throw new RPanBusinessException("没有此用户");
        }
        return question;
    }

    /**
     * 用户忘记密码-校验密保答案
     *
     * @param checkAnswerContext
     * @return
     */
    @Override
    public String checkAnswer(CheckAnswerContext checkAnswerContext) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", checkAnswerContext.getUsername());
        queryWrapper.eq("question", checkAnswerContext.getQuestion());
        queryWrapper.eq("answer", checkAnswerContext.getAnswer());
        int count = count(queryWrapper);

        if (count == 0) {
            throw new RPanBusinessException("密保答案错误");
        }

        return generateCheckAnswerToken(checkAnswerContext);
    }

    /**
     * 重置用户密码
     * 1、校验token是不是有效
     * 2、重置密码
     *
     * @param resetPasswordContext
     */
    @Override
    public void resetPassword(ResetPasswordContext resetPasswordContext) {
        checkForgetPasswordToken(resetPasswordContext);
        checkAndResetUserPassword(resetPasswordContext);
    }

    /**
     * 在线修改密码
     * 1、校验旧密码
     * 2、重置新密码
     * 3、退出当前的登录状态
     *
     * @param changePasswordContext
     */
    @Override
    public void changePassword(ChangePasswordContext changePasswordContext) {
        checkOldPassword(changePasswordContext);
        doChangePassword(changePasswordContext);
        exitLoginStatus(changePasswordContext);
    }

    /**
     * 查询在线用户的基本信息
     * 1、查询用户的基本信息实体
     * 2、查询用户的根文件夹信息
     * 3、拼装VO对象返回
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfoVO info(Long userId) {
        RPanUser entity = getById(userId);
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("用户信息查询失败");
        }

        RPanUserFile rPanUserFile = getUserRootFileInfo(userId);
        if (Objects.isNull(rPanUserFile)) {
            throw new RPanBusinessException("查询用户根文件夹信息失败");
        }

        return userConverter.assembleUserInfoVO(entity, rPanUserFile);
    }

    /**
     * 获取用户根文件夹信息实体
     *
     * @param userId
     * @return
     */
    private RPanUserFile getUserRootFileInfo(Long userId) {
        return iUserFileService.getUserRootFile(userId);
    }

    /**
     * 退出用户的登录状态
     *
     * @param changePasswordContext
     */
    private void exitLoginStatus(ChangePasswordContext changePasswordContext) {
        exit(changePasswordContext.getUserId());
    }

    /**
     * 修改新密码
     *
     * @param changePasswordContext
     */
    private void doChangePassword(ChangePasswordContext changePasswordContext) {
        String newPassword = changePasswordContext.getNewPassword();
        RPanUser entity = changePasswordContext.getEntity();
        String salt = entity.getSalt();

        String encNewPassword = PasswordUtil.encryptPassword(salt, newPassword);

        entity.setPassword(encNewPassword);

        if (!updateById(entity)) {
            throw new RPanBusinessException("修改用户密码失败");
        }
    }

    /**
     * 校验用户的旧密码
     * 改不周会查询并封装用户的实体信息到上下文对象中
     *
     * @param changePasswordContext
     */
    private void checkOldPassword(ChangePasswordContext changePasswordContext) {
        Long userId = changePasswordContext.getUserId();
        String oldPassword = changePasswordContext.getOldPassword();

        RPanUser entity = getById(userId);
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("用户信息不存在");
        }
        changePasswordContext.setEntity(entity);

        String encOldPassword = PasswordUtil.encryptPassword(entity.getSalt(), oldPassword);
        String dbOldPassword = entity.getPassword();
        if (!Objects.equals(encOldPassword, dbOldPassword)) {
            throw new RPanBusinessException("旧密码不正确");
        }
    }


    /**
     * 校验用户信息并重置用户密码
     *
     * @param resetPasswordContext
     */
    private void checkAndResetUserPassword(ResetPasswordContext resetPasswordContext) {
        String username = resetPasswordContext.getUsername();
        String password = resetPasswordContext.getPassword();
        RPanUser entity = getRPanUserByUsername(username);
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("用户信息不存在");
        }

        String newDbPassword = PasswordUtil.encryptPassword(entity.getSalt(), password);
        entity.setPassword(newDbPassword);
        entity.setUpdateTime(new Date());

        if (!updateById(entity)) {
            throw new RPanBusinessException("重置用户密码失败");
        }
    }



    /**
     * 验证忘记密码的token是否有效
     *
     * @param resetPasswordContext
     */
    private void checkForgetPasswordToken(ResetPasswordContext resetPasswordContext) {
        String token = resetPasswordContext.getToken();
        Object value = JwtUtil.analyzeToken(token, UserConstants.FORGET_USERNAME);
        if (Objects.isNull(value)) {
            throw new RPanBusinessException(ResponseCode.TOKEN_EXPIRE);
        }
        String tokenUsername = String.valueOf(value);
        if (!Objects.equals(tokenUsername, resetPasswordContext.getUsername())) {
            throw new RPanBusinessException("token错误");
        }
    }

    /**
     * 生成用户忘记密码-校验密保答案通过的临时token
     * token的失效时间为五分钟之后
     *
     * @param checkAnswerContext
     * @return
     */
    private String generateCheckAnswerToken(CheckAnswerContext checkAnswerContext) {
        String token = JwtUtil.generateToken(checkAnswerContext.getUsername(), UserConstants.FORGET_USERNAME, checkAnswerContext.getUsername(), UserConstants.FIVE_MINUTES_LONG);
        return token;
    }



    /**
     * 生成并保存登陆之后的凭证
     *
     * @param userLoginContext
     */
    private void generateAndSaveAccessToken(UserLoginContext userLoginContext) {
        RPanUser entity = userLoginContext.getEntity();

        //生成jwt令牌
        String accessToken = JwtUtil.generateToken(entity.getUsername(), UserConstants.LOGIN_USER_ID, entity.getUserId(), UserConstants.ONE_DAY_LONG);



        /**
         * 这行代码通过cacheManager（缓存管理器）的getCache方法获取一个名为CacheConstants.R_PAN_CACHE_NAME的缓存实例，
         * 并将其赋值给cache变量。CacheConstants.R_PAN_CACHE_NAME是一个常量，它定义了缓存的名称，通常用于标识特定的缓存用途或存储内容。
         *
         * UserConstants.USER_LOGIN_PREFIX + entity.getUserId()：
         * 将UserConstants.USER_LOGIN_PREFIX（一个前缀常量，可能用于标识缓存键的特定部分）与entity.getUserId()（用户的唯一ID）拼接起来，
         * 形成一个完整的缓存键。这样做的目的是创建一个具有唯一性的键，用于在缓存中存储和检索特定的用户登录信息。
         *
         * cache.put(..., accessToken)：使用上面生成的缓存键作为键（key），将之前生成的accessToken作为值（value）存放到cache缓存中。
         * 这样，当需要验证用户身份或需要获取该用户的访问令牌时，可以通过相同的键从缓存中检索这个令牌。
         */
        Cache cache = cacheManager.getCache(CacheConstants.R_PAN_CACHE_NAME);
        cache.put(UserConstants.USER_LOGIN_PREFIX + entity.getUserId(), accessToken);


        System.out.println(accessToken);
        userLoginContext.setAccessToken(accessToken);
    }

    /**
     * 校验用户名密码
     *
     * @param userLoginContext
     */
    private void checkLoginInfo(UserLoginContext userLoginContext) {
        String username = userLoginContext.getUsername();                    //拿到数据
        String password = userLoginContext.getPassword();


        RPanUser entity = getRPanUserByUsername(username);                     //根据用户名你校验
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("用户名称不存在");
        }


        String salt = entity.getSalt();
        String encPassword = PasswordUtil.encryptPassword(salt, password);           //计算传入的密码
        String dbPassword = entity.getPassword();                                    //获取数据库内存的密码

        if (!Objects.equals(encPassword, dbPassword)) {
            throw new RPanBusinessException("密码信息不正确");
        }

        userLoginContext.setEntity(entity);
    }

    /**
     * 通过用户名称获取用户实体信息
     *
     * @param username
     * @return
     */
    private RPanUser getRPanUserByUsername(String username) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }

    /**
     * 创建用户的根目录信息
     *
     * @param userRegisterContext
     */
    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createFolderContext = new CreateFolderContext();
        createFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        createFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);
        iUserFileService.createFolder(createFolderContext);
    }

    /**
     * 实现注册用户的业务
     * 需要捕获数据库的唯一索引冲突异常，来实现全局用户名称唯一
     *
     * @param userRegisterContext
     */
    private void doRegister(UserRegisterContext userRegisterContext) {
        RPanUser entity = userRegisterContext.getEntity();
        if (Objects.nonNull(entity)) {
            try {
                if (!save(entity)) {
                    throw new RPanBusinessException("用户注册失败");
                }
            } catch (DuplicateKeyException duplicateKeyException) {
                throw new RPanBusinessException("用户名已存在");
            }
            return;
        }
        throw new RPanBusinessException(ResponseCode.ERROR);
    }

    /**
     * 实体转化
     * 由上下文信息转化成用户实体，封装进上下文
     *
     * @param userRegisterContext
     */
    private void assembleUserEntity(UserRegisterContext userRegisterContext) {
        RPanUser entity = userConverter.userRegisterContext2RPanUser(userRegisterContext);
        String salt = PasswordUtil.getSalt(),
                dbPassword = PasswordUtil.encryptPassword(salt, userRegisterContext.getPassword());
        entity.setUserId(IdUtil.get());
        entity.setSalt(salt);
        entity.setPassword(dbPassword);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        userRegisterContext.setEntity(entity);
    }
}




