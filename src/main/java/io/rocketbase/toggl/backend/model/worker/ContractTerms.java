package io.rocketbase.toggl.backend.model.worker;

import io.rocketbase.toggl.backend.model.global.Note;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContractTerms {

    private String name;

    private List<DayOfWeek> weeklyWorkingDays;

    private BigDecimal weeklyWorkingHours;

    private int daysOfVacationPerYear;

    private BigDecimal grossMonthlySalary;

    private BigDecimal netMonthlySalary;

    private LocalDate validFrom;

    private LocalDate validTo;

    private List<Note> notes;
}
