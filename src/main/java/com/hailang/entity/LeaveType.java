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
@TableName("leave_type")
@Schema(description = "假期类型")
public class LeaveType {
    @TableId(type = IdType.AUTO)
    @Schema(description = "自增主键(内部)")
    private Long id;

    @Schema(description = "业务主键")
    private String uuid;

    @Schema(description = "假期类型名称")
    private String name;

    @Schema(description = "显示颜色")
    private String color;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "是否扣减余额 0不扣 1扣减")
    private Integer deductBalance;

    @Schema(description = "假删除 0删除 1保留")
    private Integer isDelete;

    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;
}
