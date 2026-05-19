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
@TableName("dept")
@Schema(description = "部门")
public class Dept {
    @TableId(type = IdType.AUTO)
    @Schema(description = "自增主键(内部)")
    private Long id;

    @Schema(description = "业务主键")
    private String uuid;

    @Schema(description = "部门名称")
    private String name;

    @Schema(description = "上级部门uuid")
    private String parentUuid;

    @Schema(description = "假删除 0删除 1保留")
    private Integer isDelete;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
