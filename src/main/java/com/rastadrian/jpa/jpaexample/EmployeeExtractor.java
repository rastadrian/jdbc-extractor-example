package com.rastadrian.jpa.jpaexample;

import com.rastadrian.jpa.jpaexample.model.Employee;
import com.rastadrian.jpa.jpaexample.model.Shift;
import com.rastadrian.jpa.jpaexample.model.ShiftDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
public class EmployeeExtractor implements ResultSetExtractor<List<Employee>> {

    private final Map<Integer, Employee> employees = new HashMap<>();
    private final Map<Integer, Shift> shifts = new HashMap<>();

    private final Function<ResultSet, Employee> toEmployee = resultSet -> {
        Employee employee = new Employee();
        employee.setShifts(new ArrayList<>());
        try {
            employee.setId(resultSet.getInt("Employee_Id"));
        } catch (SQLException e) {
            log.error("Unable to retrieve data from ResultSet", e);
        }
        return employee;
    };

    private final Function<ResultSet, Shift> toShift = resultSet -> {
        Shift shift = new Shift();
        shift.setShiftDetails(new ArrayList<>());

        try {
            shift.setId(resultSet.getInt("Shift_Id"));
            shift.setStartDate(resultSet.getDate("Shift_StartDate"));
        } catch (SQLException e) {
            log.error("Unable to retrieve data from ResultSet", e);
        }

        return shift;
    };

    private final Function<ResultSet, ShiftDetail> toShiftDetail = resultSet -> {
        ShiftDetail shiftDetail = new ShiftDetail();
        try {
            shiftDetail.setId(resultSet.getInt("ShiftDetail_Id"));
            shiftDetail.setMessage(resultSet.getString("ShiftDetail_Message"));
        } catch (SQLException e) {
            log.error("Unable to retrieve data from ResultSet", e);
        }
        return shiftDetail;
    };

    @Override
    public List<Employee> extractData(ResultSet resultSet) throws SQLException, DataAccessException {

        while (resultSet.next()) {

            ShiftDetail shiftDetail = toShiftDetail.apply(resultSet);

            int shiftId = resultSet.getInt("Shift_Id");
            Shift shift = shifts.computeIfAbsent(shiftId, id -> toShift.apply(resultSet));
            shift.getShiftDetails().add(shiftDetail);

            int employeeId = resultSet.getInt(resultSet.getInt("Employee_Id"));
            Employee employee = employees.computeIfAbsent(employeeId, id -> toEmployee.apply(resultSet));

            if (!employee.getShifts().contains(shift)) {
                employee.getShifts().add(shift);
            }
        }

        return new ArrayList<>(employees.values());
    }
}
