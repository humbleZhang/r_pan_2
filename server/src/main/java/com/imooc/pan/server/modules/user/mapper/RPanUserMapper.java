package com.imooc.pan.server.modules.user.mapper;

import com.imooc.pan.server.modules.user.entity.RPanUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author 张海生
* @description 针对表【r_pan_user(用户信息表)】的数据库操作Mapper
* @createDate 2024-03-05 22:56:17
* @Entity com.imooc.pan.server.modules.user.entity.RPanUser
*/
public interface RPanUserMapper extends BaseMapper<RPanUser> {

    String selectQuestionByUsername(String username);
}




