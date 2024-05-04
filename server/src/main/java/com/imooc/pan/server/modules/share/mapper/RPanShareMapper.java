package com.imooc.pan.server.modules.share.mapper;

import com.imooc.pan.server.modules.share.entity.RPanShare;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.pan.server.modules.share.vo.RPanShareUrlListVO;

import java.util.List;

/**
* @author 张海生
* @description 针对表【r_pan_share(用户分享表)】的数据库操作Mapper
* @createDate 2024-03-05 23:07:35
* @Entity com.imooc.pan.server.modules.share.entity.RPanShare
*/
public interface RPanShareMapper extends BaseMapper<RPanShare> {

    List<RPanShareUrlListVO> selectShareVOListByUserId(Long userId);
}




