package com.hailang.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("rule")
@Schema(description = "规则")
public class Rule {
    @TableId(type = IdType.AUTO)
    @Schema(description = "主键")
    private Long id;

    @Schema(description = "规则名称")
    private String name;

    @Schema(description = "规则类型")
    private String type;

    @Schema(description = "规则值")
    private String value;

    @Schema(description = "规则描述")
    private String desc;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "状态 0-禁用 1-启用")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
