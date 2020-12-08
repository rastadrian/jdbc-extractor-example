package com.rastadrian.jpa.jpaexample;

import com.rastadrian.jpa.jpaexample.model.Employee;
import com.rastadrian.jpa.jpaexample.model.Shift;
import com.rastadrian.jpa.jpaexample.model.ShiftDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@SpringBootApplication
public class JpaExampleApplication {

    @Value("${queries.employee_by_id_shifts_filtered}")
    private String query;

    @Bean
    public CommandLineRunner runner(EmployeeRepository repository, JdbcTemplate template, EmployeeExtractor employeeExtractor) {
        return args -> {
            // create shifts
            Shift oldShift = createShift(LocalDate.now().minusYears(1), "Old Shift"); // 1 year ago
            Shift recentShift = createShift(LocalDate.now().minusDays(5), "More Recent Shift"); // 5 days ago
            Shift mostRecentShift = createShift(LocalDate.now().minusDays(1), "Most Recent Shift"); // 1 day ago

            // create employee
            Employee employee = createEmployee(Arrays.asList(oldShift, recentShift, mostRecentShift));

            // save employee
            Employee savedEmployee = repository.save(employee);
            Integer id = savedEmployee.getId();

            // fetch employee by id, but only fetch the shifts from 6 days ago
            Optional<Employee> result = Optional.ofNullable(
                    template.query(query, employeeExtractor, id, Date.valueOf(LocalDate.now().minusDays(6))));

            result.ifPresentOrElse(printEmployeeDetails(),
                    () -> log.warn("Couldn't find employee!"));
        };
    }

    private Consumer<Employee> printEmployeeDetails() {
        return employee -> {
            log.info("Found employee {}", employee.getId());

            employee.getShifts().forEach(shift -> {
                log.info("Found shift {} on {} with messages:", shift.getId(), shift.getStartDate());

                shift.getShiftDetails().forEach(detail -> log.info("{}", detail.getMessage()));
            });
        };
    }

    private Employee createEmployee(List<Shift> shifts) {
        Employee employee = new Employee();
        employee.setShifts(shifts);
        shifts.forEach(shift -> shift.setEmployee(employee)); // reverse reference for cascade.
        return employee;
    }

    private Shift createShift(LocalDate date, String message) {
        ShiftDetail shiftDetail = new ShiftDetail();
        shiftDetail.setMessage(message);

        Shift shift = new Shift();
        shift.setStartDate(Date.valueOf(date));
        shift.setShiftDetails(Collections.singletonList(shiftDetail));
        shiftDetail.setShift(Collections.singletonList(shift)); // reverse reference for cascade.
        return shift;
    }

    public static void main(String[] args) {
        SpringApplication.run(JpaExampleApplication.class, args);
    }

}
