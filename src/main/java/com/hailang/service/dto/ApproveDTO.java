package com.hailang.service.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApproveDTO {
    private String uuid;
    private String applyUuid;
    private Integer order;
    private String leaderUuid;
    private String reject;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
