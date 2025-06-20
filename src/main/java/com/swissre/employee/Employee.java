package com.swissre.employee;

import java.time.LocalDate;

public class Employee {
    public int id;
    public String name;
    public String city;
    public String state;
    public String category;
    public Integer managerId;
    public int salary;
    public LocalDate doj;

    public Employee(int id, String name, String city, String state, String category, Integer managerId, int salary, LocalDate doj) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.state = state;
        this.category = category;
        this.managerId = managerId;
        this.salary = salary;
        this.doj = doj;
    }
}
