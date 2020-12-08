package com.rastadrian.jpa.jpaexample.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.sql.Date;
import java.util.List;

@Getter
@Setter
@Entity
public class Shift {

    @Id
    @GeneratedValue
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "Employee_Id", nullable = false)
    private Employee employee;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(
            name = "ShiftDetails",
            joinColumns = @JoinColumn(name = "Shift_id"),
            inverseJoinColumns = @JoinColumn(name = "ShiftDetail_Id"))
    private List<ShiftDetail> shiftDetails;

    @Column(name = "StartDate")
    private Date startDate;

}
