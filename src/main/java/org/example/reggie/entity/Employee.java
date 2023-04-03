package org.example.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体类
 */
@Data
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;    // 序列化

    private Long id;            // 员工id

    private String username;    // 用户名

    private String name;        // 员工姓名

    private String password;    // 密码

    private String phone;       // 手机号码

    private String sex;         // 性别

    private String idNumber;    // 身份证号

    private Integer status;    // 状态(0:禁用,1:启用)

    @TableField(fill = FieldFill.INSERT)           // 新增时填充
    private LocalDateTime createTime;              // 创建时间

    @TableField(fill = FieldFill.INSERT_UPDATE)    // 新增和修改时都会填充
    private LocalDateTime updateTime;              // 修改时间

    @TableField(fill = FieldFill.INSERT)           // 新增时填充
    private Long createUser;                       // 创建人

    @TableField(fill = FieldFill.INSERT_UPDATE)     // 新增和修改时都会填充
    private Long updateUser;                        // 修改人

}
