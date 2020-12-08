package com.rastadrian.jpa.jpaexample;

import com.rastadrian.jpa.jpaexample.model.Employee;
import com.rastadrian.jpa.jpaexample.model.Shift;
import com.rastadrian.jpa.jpaexample.model.ShiftDetail;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class EmployeeExtractor implements ResultSetExtractor<Employee> {

    @Override
    public Employee extractData(ResultSet rs) throws SQLException, DataAccessException {

        if (!rs.first()) {
            return null;
        }

        Employee employee = new Employee();
        employee.setShifts(new ArrayList<>());
        Shift currentShift = null;

        do {
            employee.setId(rs.getInt("Employee_Id"));

            // every row has a different shift detail, so we extract it right away
            ShiftDetail currentShiftDetail = new ShiftDetail();
            currentShiftDetail.setId(rs.getInt("ShiftDetail_Id"));
            currentShiftDetail.setMessage(rs.getString("ShiftDetail_Message"));

            // every row might have the same shift or not, so we have to keep track
            int newShiftId = rs.getInt("Shift_Id");
            if (currentShift == null || currentShift.getId() != newShiftId) {

                if (currentShift != null) {
                    // the details on this row are for a new shift, so add the current shift to the employee
                    employee.getShifts().add(currentShift);
                }

                // create new shift
                currentShift = new Shift();
                currentShift.setId(newShiftId);
                currentShift.setStartDate(rs.getDate("Shift_StartDate"));
                currentShift.setShiftDetails(new ArrayList<>());
            }

            // for each row, we add the details to whatever is the current shift
            currentShift.getShiftDetails().add(currentShiftDetail);
        } while (rs.next());

        // add the very last shift on the loop to the employee
        employee.getShifts().add(currentShift);
        return employee;
    }
}
