package com.imooc.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.imooc.pan.server.modules.user.entity.RPanUserSearchHistory;
import com.imooc.pan.server.modules.user.service.IUserSearchHistoryService;
import com.imooc.pan.server.modules.user.mapper.RPanUserSearchHistoryMapper;
import org.springframework.stereotype.Service;

/**
* @author 张海生
* @description 针对表【r_pan_user_search_history(用户搜索历史表)】的数据库操作Service实现
* @createDate 2024-03-05 22:56:17
*/
@Service(value = "userSearchHistoryService")
public class UserSearchHistoryServiceImpl extends ServiceImpl<RPanUserSearchHistoryMapper, RPanUserSearchHistory>
    implements IUserSearchHistoryService {

}




