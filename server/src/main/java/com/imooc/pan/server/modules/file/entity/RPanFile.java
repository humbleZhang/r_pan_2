package com.imooc.pan.server.modules.file.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 物理文件信息表
 * @TableName r_pan_file
 */
@TableName(value ="r_pan_file")
@Data
public class RPanFile implements Serializable {
    /**
     * 文件id
     */
    @TableId(value = "file_id")
    private Long fileId;

    /**
     * 文件名称
     */
    @TableField(value = "filename")
    private String filename;

    /**
     * 文件物理路径
     */
    @TableField(value = "real_path")
    private String realPath;

    /**
     * 文件实际大小
     */
    @TableField(value = "file_size")
    private String fileSize;

    /**
     * 文件大小展示字符
     */
    @TableField(value = "file_size_desc")
    private String fileSizeDesc;

    /**
     * 文件后缀
     */
    @TableField(value = "file_suffix")
    private String fileSuffix;

    /**
     * 文件预览的响应头Content-Type的值
     */
    @TableField(value = "file_preview_content_type")
    private String filePreviewContentType;

    /**
     * 文件唯一标识
     */
    @TableField(value = "identifier")
    private String identifier;

    /**
     * 创建人
     */
    @TableField(value = "create_user")
    private Long createUser;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

//    /**
//     * 文件id
//     */
//    public Long getFile_id() {
//        return fileId;
//    }
//
//    /**
//     * 文件id
//     */
//    public void setFile_id(Long file_id) {
//        this.fileId = file_id;
//    }
//
//    /**
//     * 文件名称
//     */
//    public String getFilename() {
//        return filename;
//    }
//
//    /**
//     * 文件名称
//     */
//    public void setFilename(String filename) {
//        this.filename = filename;
//    }
//
//    /**
//     * 文件物理路径
//     */
//    public String getReal_path() {
//        return realPath;
//    }
//
//    /**
//     * 文件物理路径
//     */
//    public void setReal_path(String real_path) {
//        this.realPath = real_path;
//    }
//
//    /**
//     * 文件实际大小
//     */
//    public String getFile_size() {
//        return fileSize;
//    }
//
//    /**
//     * 文件实际大小
//     */
//    public void setFile_size(String file_size) {
//        this.fileSize = file_size;
//    }
//
//    /**
//     * 文件大小展示字符
//     */
//    public String getFile_size_desc() {
//        return fileSizeDesc;
//    }
//
//    /**
//     * 文件大小展示字符
//     */
//    public void setFile_size_desc(String file_size_desc) {
//        this.fileSizeDesc = file_size_desc;
//    }
//
//    /**
//     * 文件后缀
//     */
//    public String getFile_suffix() {
//        return fileSuffix;
//    }
//
//    /**
//     * 文件后缀
//     */
//    public void setFile_suffix(String file_suffix) {
//        this.fileSuffix = file_suffix;
//    }
//
//    /**
//     * 文件预览的响应头Content-Type的值
//     */
//    public String getFile_preview_content_type() {
//        return filePreviewContentType;
//    }
//
//    /**
//     * 文件预览的响应头Content-Type的值
//     */
//    public void setFile_preview_content_type(String file_preview_content_type) {
//        this.filePreviewContentType = file_preview_content_type;
//    }
//
//    /**
//     * 文件唯一标识
//     */
//    public String getIdentifier() {
//        return identifier;
//    }
//
//    /**
//     * 文件唯一标识
//     */
//    public void setIdentifier(String identifier) {
//        this.identifier = identifier;
//    }
//
//    /**
//     * 创建人
//     */
//    public Long getCreate_user() {
//        return createUser;
//    }
//
//    /**
//     * 创建人
//     */
//    public void setCreate_user(Long create_user) {
//        this.createUser = create_user;
//    }
//
//    /**
//     * 创建时间
//     */
//    public Date getCreate_time() {
//        return createTime;
//    }
//
//    /**
//     * 创建时间
//     */
//    public void setCreate_time(Date create_time) {
//        this.createTime = create_time;
//    }
}