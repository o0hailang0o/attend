package com.hailang.service.dto;

import lombok.Data;

@Data
public class LeaderDTO {
    private Long id;
    private String leaderUuid;
    private String leaderName;
    private Long parentId;
    private Integer level;
    private String tree;
}
