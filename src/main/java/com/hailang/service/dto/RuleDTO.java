package com.hailang.service.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;

@Data
public class RuleDTO {
    private Long id;
    private String uuid;
    private String name;
    private Integer flexibility;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer middleRest;
    private LocalTime middleStart;
    private LocalTime middleEnd;
    private Integer vacation;
    private Integer comp;
    private BigDecimal accuracy;
}
