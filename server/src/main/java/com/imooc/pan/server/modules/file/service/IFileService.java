package com.imooc.pan.server.modules.file.service;

import com.imooc.pan.server.modules.file.context.FileChunkMergeAndSaveContext;
import com.imooc.pan.server.modules.file.context.FileSaveContext;
import com.imooc.pan.server.modules.file.context.QueryFileListContext;
import com.imooc.pan.server.modules.file.context.QueryRealFileListContext;
import com.imooc.pan.server.modules.file.entity.RPanFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.pan.server.modules.file.vo.RPanUserFileVO;

import java.util.List;

/**
* @author 张海生
* @description 针对表【r_pan_file(物理文件信息表)】的数据库操作Service
* @createDate 2024-03-05 23:04:37
*/
public interface IFileService extends IService<RPanFile> {

    /**
     * 根据条件查询用户的实际文件列表
     *
     * @param context
     * @return
     */
    List<RPanFile> getFileList(QueryRealFileListContext context);

    /**
     * 上传单文件并保存实体记录
     *
     * @param context
     */
    void saveFile(FileSaveContext context);


    /**
     * 合并物理文件并保存物理文件记录
     *
     * @param context
     */
    void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context);


}
