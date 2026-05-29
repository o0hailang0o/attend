package com.hailang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hailang.config.utils.BeanUtils;
import com.hailang.dao.ApplyDao;
import com.hailang.dao.DailyAttendanceDao;
import com.hailang.dao.DoorAccessDao;
import com.hailang.dao.RuleDao;
import com.hailang.dao.SysUserDao;
import com.hailang.entity.Apply;
import com.hailang.entity.DailyAttendance;
import com.hailang.entity.DoorAccess;
import com.hailang.entity.Rule;
import com.hailang.entity.SysUser;
import com.hailang.service.DailyAttendanceService;
import com.hailang.service.dto.DailyAttendanceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DailyAttendanceServiceImpl implements DailyAttendanceService {

    private final DailyAttendanceDao dailyAttendanceDao;
    private final SysUserDao sysUserDao;
    private final DoorAccessDao doorAccessDao;
    private final ApplyDao applyDao;
    private final RuleDao ruleDao;

    private static final Set<LocalDate> HOLIDAYS;

    static {
        Set<LocalDate> h = new HashSet<>();
        h.add(LocalDate.of(2025, 1, 1));
        h.add(LocalDate.of(2025, 1, 28));
        h.add(LocalDate.of(2025, 1, 29));
        h.add(LocalDate.of(2025, 1, 30));
        h.add(LocalDate.of(2025, 1, 31));
        h.add(LocalDate.of(2025, 2, 1));
        h.add(LocalDate.of(2025, 2, 2));
        h.add(LocalDate.of(2025, 2, 3));
        h.add(LocalDate.of(2025, 4, 4));
        h.add(LocalDate.of(2025, 4, 5));
        h.add(LocalDate.of(2025, 4, 6));
        h.add(LocalDate.of(2025, 5, 1));
        h.add(LocalDate.of(2025, 5, 2));
        h.add(LocalDate.of(2025, 5, 3));
        h.add(LocalDate.of(2025, 5, 4));
        h.add(LocalDate.of(2025, 5, 5));
        h.add(LocalDate.of(2025, 5, 31));
        h.add(LocalDate.of(2025, 6, 1));
        h.add(LocalDate.of(2025, 6, 2));
        h.add(LocalDate.of(2025, 10, 1));
        h.add(LocalDate.of(2025, 10, 2));
        h.add(LocalDate.of(2025, 10, 3));
        h.add(LocalDate.of(2025, 10, 4));
        h.add(LocalDate.of(2025, 10, 5));
        h.add(LocalDate.of(2025, 10, 6));
        h.add(LocalDate.of(2025, 10, 7));
        h.add(LocalDate.of(2025, 10, 8));
        h.add(LocalDate.of(2026, 1, 1));
        h.add(LocalDate.of(2026, 2, 17));
        h.add(LocalDate.of(2026, 2, 18));
        h.add(LocalDate.of(2026, 2, 19));
        h.add(LocalDate.of(2026, 2, 20));
        h.add(LocalDate.of(2026, 2, 21));
        h.add(LocalDate.of(2026, 2, 22));
        h.add(LocalDate.of(2026, 2, 23));
        h.add(LocalDate.of(2026, 4, 4));
        h.add(LocalDate.of(2026, 4, 5));
        h.add(LocalDate.of(2026, 4, 6));
        h.add(LocalDate.of(2026, 5, 1));
        h.add(LocalDate.of(2026, 5, 2));
        h.add(LocalDate.of(2026, 5, 3));
        h.add(LocalDate.of(2026, 5, 4));
        h.add(LocalDate.of(2026, 5, 5));
        HOLIDAYS = Collections.unmodifiableSet(h);
    }

    @Override
    public List<DailyAttendanceDTO> queryByDateAndEmployee(String employeeUuid, LocalDate date) {
        LambdaQueryWrapper<DailyAttendance> wrapper = Wrappers.<DailyAttendance>lambdaQuery()
                .eq(DailyAttendance::getIsDelete, 1);
        if (employeeUuid != null && !employeeUuid.isEmpty()) {
            wrapper.eq(DailyAttendance::getEmployeeUuid, employeeUuid);
        }
        if (date != null) {
            wrapper.eq(DailyAttendance::getDate, date);
        }
        wrapper.orderByAsc(DailyAttendance::getDate);
        return dailyAttendanceDao.selectList(wrapper).stream()
                .map(record -> BeanUtils.copy(record, DailyAttendanceDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<DailyAttendanceDTO> queryByDateRange(String employeeUuid, LocalDate startDate, LocalDate endDate) {
        LambdaQueryWrapper<DailyAttendance> wrapper = Wrappers.<DailyAttendance>lambdaQuery()
                .eq(DailyAttendance::getIsDelete, 1);
        if (employeeUuid != null && !employeeUuid.isEmpty()) {
            wrapper.eq(DailyAttendance::getEmployeeUuid, employeeUuid);
        }
        if (startDate != null) {
            wrapper.ge(DailyAttendance::getDate, startDate);
        }
        if (endDate != null) {
            wrapper.le(DailyAttendance::getDate, endDate);
        }
        wrapper.orderByAsc(DailyAttendance::getEmployeeUuid, DailyAttendance::getDate);
        return dailyAttendanceDao.selectList(wrapper).stream()
                .map(record -> BeanUtils.copy(record, DailyAttendanceDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public DailyAttendanceDTO getByUuid(String uuid) {
        DailyAttendance record = dailyAttendanceDao.selectByUuid(uuid);
        return record == null ? null : BeanUtils.copy(record, DailyAttendanceDTO.class);
    }

    @Override
    public DailyAttendanceDTO save(DailyAttendanceDTO dto) {
        DailyAttendance record = BeanUtils.copy(dto, DailyAttendance.class);
        record.setUuid(UUID.randomUUID().toString().replace("-", ""));
        record.setIsDelete(1);
        dailyAttendanceDao.insert(record);
        return BeanUtils.copy(record, DailyAttendanceDTO.class);
    }

    @Override
    public DailyAttendanceDTO update(DailyAttendanceDTO dto) {
        DailyAttendance record = BeanUtils.copy(dto, DailyAttendance.class);
        dailyAttendanceDao.update(record, new LambdaQueryWrapper<DailyAttendance>().eq(DailyAttendance::getUuid, record.getUuid()));
        return BeanUtils.copy(record, DailyAttendanceDTO.class);
    }

    @Override
    public boolean removeByUuid(String uuid) {
        return dailyAttendanceDao.update(null,
                Wrappers.<DailyAttendance>lambdaUpdate()
                        .eq(DailyAttendance::getUuid, uuid)
                        .set(DailyAttendance::getIsDelete, 0)) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void calculate(LocalDate startDate, LocalDate endDate, String userUuid) {
        List<SysUser> users;
        if (userUuid != null && !userUuid.isEmpty()) {
            SysUser user = sysUserDao.selectByUuid(userUuid);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            users = List.of(user);
        } else {
            users = sysUserDao.selectList(
                    Wrappers.<SysUser>lambdaQuery().eq(SysUser::getIsDelete, 1));
        }

        List<String> userUuidList = users.stream().map(SysUser::getUuid).collect(Collectors.toList());

        Map<String, Rule> ruleMap = ruleDao.selectList(
                Wrappers.<Rule>lambdaQuery().eq(Rule::getIsDelete, 1))
                .stream().collect(Collectors.toMap(Rule::getUuid, r -> r));

        List<DoorAccess> allAccesses = doorAccessDao.selectList(
                new LambdaQueryWrapper<DoorAccess>()
                        .eq(DoorAccess::getIsDelete, 1)
                        .ge(DoorAccess::getAccessDatetime, startDate.atStartOfDay())
                        .lt(DoorAccess::getAccessDatetime, endDate.plusDays(1).atStartOfDay())
                        .in(userUuid != null, DoorAccess::getEmployeeUuid, userUuidList));

        Map<String, Map<LocalDate, List<DoorAccess>>> accessMap = allAccesses.stream()
                .collect(Collectors.groupingBy(DoorAccess::getEmployeeUuid,
                        Collectors.groupingBy(da -> da.getAccessDatetime().toLocalDate())));

        List<Apply> approvedApplies = applyDao.selectList(
                new LambdaQueryWrapper<Apply>()
                        .eq(Apply::getIsDelete, 1)
                        .eq(Apply::getStatus, 9)
                        .le(Apply::getStartTime, endDate.atTime(LocalTime.MAX))
                        .ge(Apply::getEndTime, startDate.atTime(LocalTime.MIN))
                        .in(userUuid != null, Apply::getApplyUserUuid, userUuidList));

        Map<String, List<Apply>> applyMap = approvedApplies.stream()
                .collect(Collectors.groupingBy(Apply::getApplyUserUuid));

        dailyAttendanceDao.delete(new LambdaQueryWrapper<DailyAttendance>()
                .in(DailyAttendance::getEmployeeUuid, userUuidList)
                .ge(DailyAttendance::getDate, startDate)
                .le(DailyAttendance::getDate, endDate));

        List<DailyAttendance> records = new ArrayList<>();
        for (SysUser user : users) {
            Rule rule = ruleMap.get(user.getRuleUuid());
            Map<LocalDate, List<DoorAccess>> userAccessMap = accessMap.getOrDefault(user.getUuid(), Collections.emptyMap());
            List<Apply> userApplies = applyMap.getOrDefault(user.getUuid(), Collections.emptyList());

            for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                DailyAttendance da = new DailyAttendance();
                da.setUuid(UUID.randomUUID().toString().replace("-", ""));
                da.setEmployeeUuid(user.getUuid());
                da.setEmployeeName(user.getName());
                da.setDate(date);

                if (HOLIDAYS.contains(date)) {
                    da.setDayType(3);
                } else if (isWeekend(date)) {
                    da.setDayType(2);
                } else {
                    da.setDayType(1);
                }

                List<DoorAccess> accesses = userAccessMap.getOrDefault(date, Collections.emptyList());
                if (!accesses.isEmpty()) {
                    LocalTime clockIn = accesses.stream().map(a -> a.getAccessDatetime().toLocalTime())
                            .min(LocalTime::compareTo).orElse(null);
                    LocalTime clockOut = accesses.stream().map(a -> a.getAccessDatetime().toLocalTime())
                            .max(LocalTime::compareTo).orElse(null);
                    da.setClockIn(clockIn);
                    da.setClockOut(clockOut);
                    long minutes = Duration.between(clockIn, clockOut).toMinutes();
                    da.setActualWorkHours(BigDecimal.valueOf(Math.max(0, minutes))
                            .divide(BigDecimal.valueOf(60), 1, RoundingMode.HALF_UP));
                } else {
                    da.setActualWorkHours(BigDecimal.ZERO);
                }

                BigDecimal leaveHours = BigDecimal.ZERO;
                BigDecimal annualLeaveHours = BigDecimal.ZERO;
                BigDecimal compLeaveHours = BigDecimal.ZERO;

                for (Apply apply : userApplies) {
                    BigDecimal dailyHours = calculateDailyLeaveHours(apply, date, rule);
                    if (dailyHours.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }
                    leaveHours = leaveHours.add(dailyHours);
                    if (Integer.valueOf(1).equals(apply.getType())) {
                        annualLeaveHours = annualLeaveHours.add(dailyHours);
                    } else if (Integer.valueOf(4).equals(apply.getType())) {
                        compLeaveHours = compLeaveHours.add(dailyHours);
                    }
                }

                da.setLeaveHours(leaveHours);
                da.setAnnualLeaveHours(annualLeaveHours);
                da.setCompLeaveHours(compLeaveHours);

                BigDecimal stdHours = calculateStdDailyHours(rule);
                boolean noOvertimeApply = rule != null && Integer.valueOf(0).equals(rule.getOvertimeApply());
                if (noOvertimeApply) {
                    da.setRecognizedHours(BigDecimal.ZERO.max(da.getActualWorkHours().subtract(leaveHours))
                            .setScale(1, RoundingMode.HALF_UP));
                } else {
                    da.setRecognizedHours(BigDecimal.ZERO.max(stdHours.subtract(leaveHours))
                            .setScale(1, RoundingMode.HALF_UP));
                }

                da.setStatus(determineStatus(da, rule));

                da.setIsDelete(1);
                records.add(da);
            }
        }

        for (DailyAttendance record : records) {
            dailyAttendanceDao.insert(record);
        }
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek dow = date.getDayOfWeek();
        return dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
    }

    private BigDecimal calculateStdDailyHours(Rule rule) {
        if (rule == null) {
            return BigDecimal.ZERO;
        }
        long dailyMinutes = Duration.between(rule.getStartTime(), rule.getEndTime()).toMinutes();
        if (Integer.valueOf(1).equals(rule.getMiddleRest())
                && rule.getMiddleStart() != null && rule.getMiddleEnd() != null) {
            dailyMinutes -= Duration.between(rule.getMiddleStart(), rule.getMiddleEnd()).toMinutes();
        }
        if (rule.getFlexibility() != null) {
            dailyMinutes += rule.getFlexibility() * 60L;
        }
        return BigDecimal.valueOf(Math.max(0, dailyMinutes))
                .divide(BigDecimal.valueOf(60), 1, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDailyLeaveHours(Apply apply, LocalDate date, Rule rule) {
        LocalDate applyStartDate = apply.getStartTime().toLocalDate();
        LocalDate applyEndDate = apply.getEndTime().toLocalDate();

        if (date.isBefore(applyStartDate) || date.isAfter(applyEndDate)) {
            return BigDecimal.ZERO;
        }

        // 跨天请假：如果时间差小于8h，按实际时长计算；否则按8h计算
        if (!applyStartDate.equals(applyEndDate)) {
            long totalMinutes = Duration.between(apply.getStartTime(), apply.getEndTime()).toMinutes();
            if (totalMinutes <= 480) { // 8h = 480min
                return BigDecimal.valueOf(totalMinutes).divide(BigDecimal.valueOf(60), 1, RoundingMode.HALF_UP);
            } else {
                return BigDecimal.valueOf(8.0);
            }
        }

        // 同一天请假，按精确时间计算
        LocalTime workStart = rule != null ? rule.getStartTime() : LocalTime.of(9, 0);
        LocalTime workEnd = rule != null ? rule.getEndTime() : LocalTime.of(18, 0);

        LocalDateTime dayWorkStart = LocalDateTime.of(date, workStart);
        LocalDateTime dayWorkEnd = LocalDateTime.of(date, workEnd);

        LocalDateTime overlapStart = apply.getStartTime().isAfter(dayWorkStart)
                ? apply.getStartTime() : dayWorkStart;
        LocalDateTime overlapEnd = apply.getEndTime().isBefore(dayWorkEnd)
                ? apply.getEndTime() : dayWorkEnd;

        if (!overlapStart.isBefore(overlapEnd)) {
            return BigDecimal.ZERO;
        }

        long minutes = Duration.between(overlapStart, overlapEnd).toMinutes();

        if (rule != null && Integer.valueOf(1).equals(rule.getMiddleRest())
                && rule.getMiddleStart() != null && rule.getMiddleEnd() != null) {
            LocalDateTime lunchStart = LocalDateTime.of(date, rule.getMiddleStart());
            LocalDateTime lunchEnd = LocalDateTime.of(date, rule.getMiddleEnd());
            LocalDateTime lunchOverlapStart = overlapStart.isBefore(lunchStart) ? lunchStart
                    : (overlapStart.isBefore(lunchEnd) ? overlapStart : lunchEnd);
            LocalDateTime lunchOverlapEnd = overlapEnd.isAfter(lunchEnd) ? lunchEnd
                    : (overlapEnd.isAfter(lunchStart) ? overlapEnd : lunchStart);
            if (lunchOverlapStart.isBefore(lunchOverlapEnd)) {
                minutes -= Duration.between(lunchOverlapStart, lunchOverlapEnd).toMinutes();
            }
        }

        BigDecimal hours = BigDecimal.valueOf(Math.max(0, minutes))
                .divide(BigDecimal.valueOf(60), 10, RoundingMode.HALF_UP);

        if (rule != null && rule.getAccuracy() != null) {
            hours = hours.divide(rule.getAccuracy(), 0, RoundingMode.FLOOR)
                    .multiply(rule.getAccuracy());
        }

        return hours;
    }

    private int determineStatus(DailyAttendance da, Rule rule) {
        if (da.getDayType() != null && da.getDayType() >= 2) {
            return 1;
        }
        if (da.getLeaveHours() != null && da.getLeaveHours().compareTo(BigDecimal.ZERO) > 0) {
            return 5;
        }
        if (da.getClockIn() == null && da.getClockOut() == null) {
            return 4;
        }
        if (rule != null && rule.getStartTime() != null && da.getClockIn() != null) {
            LocalTime lateThreshold = rule.getStartTime();
            if (rule.getFlexibility() != null && rule.getFlexibility() > 0) {
                lateThreshold = lateThreshold.plusHours(rule.getFlexibility());
            }
            if (da.getClockIn().isAfter(lateThreshold)) {
                return 2;
            }
        }
        if (rule != null && rule.getEndTime() != null && da.getClockOut() != null) {
            if (da.getClockOut().isBefore(rule.getEndTime())) {
                return 3;
            }
        }
        return 1;
    }
}
