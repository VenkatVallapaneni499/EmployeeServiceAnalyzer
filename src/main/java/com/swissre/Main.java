package com.swissre;

import com.swissre.employee.Employee;
import com.swissre.employee.EmployeeHierarchy;
import com.swissre.employee.EmployeeService;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        String excelPath = "employees.xlsx";
        String jsonPath = "employeehierarchy.json";

        // 1. Generate Excel
        EmployeeService.generateExcel(excelPath);
        System.out.println("Generated excel with employee data successfully");
        // 2. Read Employees
        List<Employee> employees = EmployeeService.readExcel(excelPath);

        System.out.println("Read Excel employee size::"+employees.size());
        // 3. Gratuity Eligible
        List<Employee> gratuityList = EmployeeService.getGratuityEligible(employees);
        System.out.println("Eligible for Gratuity:: " + gratuityList.size());

        // 4. Salary > Manager
        List<Employee> overpaid = EmployeeService.salaryGreaterThanManager(employees);
        System.out.println("Employees earning more than manager:: " + overpaid.size());

        // 5. Build and Write Hierarchy
        EmployeeHierarchy root = EmployeeService.buildEmployeeHierarchy(employees);
        EmployeeService.writeHierarchyToJson(root, jsonPath);

        System.out.println("Hierarchy JSON written to:: " + jsonPath);


    }
}