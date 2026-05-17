package com.hailang.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
@Schema(description = "系统用户")
public class SysUser {
    @TableId(type = IdType.AUTO)
    @Schema(description = "自增主键(内部)")
    private Long id;

    @Schema(description = "业务主键")
    private String uuid;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "密码(MD5加密)")
    private String password;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "工号")
    private String workNum;

    @Schema(description = "级别")
    private String level;

    @Schema(description = "考勤类型")
    private String type;

    @Schema(description = "公司id")
    private String companyId;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
