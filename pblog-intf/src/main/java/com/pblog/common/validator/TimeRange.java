package com.pblog.common.validator;

import com.pblog.common.dto.PageQueryDTO;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;
import java.lang.annotation.*;
import java.time.LocalDateTime;

/**
 * 时间范围校验注解：开始时间 ≤ 结束时间
 */
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimeRangeValidator.class)
@Documented
public @interface TimeRange {
    String message() default "开始时间不能晚于结束时间";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}

/**
 * 时间范围校验器实现
 */
class TimeRangeValidator implements ConstraintValidator<TimeRange, PageQueryDTO> {
    @Override
    public boolean isValid(PageQueryDTO dto, ConstraintValidatorContext context) {
        // 两个时间都为null：校验通过
        if (dto.getStartTime() == null && dto.getEndTime() == null) {
            return true;
        }
        // 仅一个时间不为null：校验通过
        if (dto.getStartTime() == null || dto.getEndTime() == null) {
            return true;
        }
        // 两个时间都不为null：校验开始时间 ≤ 结束时间
        return !dto.getStartTime().isAfter(dto.getEndTime());
    }
}
