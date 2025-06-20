package com.swissre.employee;

import java.util.ArrayList;
import java.util.List;

public class EmployeeHierarchy {
    public int id;
    public String name;
    public String role;
    public List<EmployeeHierarchy> reportees = new ArrayList<>();

    public EmployeeHierarchy(Employee employee ) {
        this.id = employee.id;
        this.name = employee.name;
        this.role = employee.category;
    }
}
