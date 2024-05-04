package com.imooc.pan.server.modules.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户信息表
 * @TableName r_pan_user
 */
@TableName(value ="r_pan_user")
@Data
public class RPanUser implements Serializable {
    /**
     * 用户id
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "password")
    private String password;

    /**
     * 随机盐值
     *
     * 在实体对象中，Salt（盐）主要用于增强密码的安全性。Salt通常是一个随机生成的字符串，它与用户的密码结合使用，
     * 通过哈希运算生成一个独特的哈希值。这个哈希值被存储起来，用于后续的验证过程。
     *
     * Salt的主要作用包括：
     *
     * 防御字典攻击和彩虹表攻击：由于Salt是随机生成的，即使两个用户使用了相同的密码，他们的哈希值也会因为Salt的不同而不同。
     * 这使得攻击者无法直接利用预先计算好的哈希值列表（如彩虹表）进行攻击，因为每个用户的密码都对应一个独特的哈希值。
     *
     * 防止暴力破解：通过向密码添加Salt，使得暴力破解变得更加困难。因为攻击者不仅需要猜测正确的密码，还需要知道对应的Salt值，这大大增加了破解的难度。
     * 在实际应用中，当用户设置密码时，系统会生成一个随机的Salt值，并将其与密码一起存储。当用户尝试登录时，
     * 系统会使用相同的Salt值和输入的密码进行哈希运算，然后将结果与存储的哈希值进行比较。如果两者匹配，则登录成功。
     *
     * 总之，Salt在实体对象中的使用是为了增加密码的安全性，防止各种形式的攻击和破解。
     *
     */
    @TableField(value = "salt")
    private String salt;

    /**
     * 密保问题
     */
    @TableField(value = "question")
    private String question;

    /**
     * 密保答案
     */
    @TableField(value = "answer")
    private String answer;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}