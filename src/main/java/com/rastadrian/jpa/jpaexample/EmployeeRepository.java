package com.rastadrian.jpa.jpaexample;

import com.rastadrian.jpa.jpaexample.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

}
