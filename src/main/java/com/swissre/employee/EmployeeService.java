package com.swissre.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EmployeeService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d-MMM-yyyy", Locale.ENGLISH);

    public static void generateExcel(String path) throws Exception {
        List<Employee> list = new ArrayList<>();
        list.add(new Employee(789, "Rama", "Chennai", "Tamilnadu", "Director", null, 150000, LocalDate.of(2022, 10, 25)));
        list.add(new Employee(456, "Shivam", "Bangalore", "Karnataka", "manager", 789, 75000, LocalDate.of(2022, 7, 5)));
        list.add(new Employee(123, "Ravi", "Hyderabad", "Telangana", "employee", 456, 45000, LocalDate.of(2023, 6, 4)));
        list.add(new Employee(1011, "Krishna", "Hyderabad", "Telangana", "employee", 456, 50000, LocalDate.of(2021, 3, 7)));
        list.add(new Employee(1213, "Sreekanth", "Mumbai", "Maharashtra", "employee", 789, 60000, LocalDate.of(2019, 8, 8)));
        list.add(new Employee(1415, "Manoj", "Mangalore", "Karnataka", "employee", 456, 95000, LocalDate.of(2018, 6, 9)));

        // Generate dummy employees
        for (int i = 2000; i < 2050; i++) {
            String name = "Emp" + i;
            String category = "employee";
            Integer managerId = (i % 2 == 0) ? 456 : 789;
            LocalDate doj = LocalDate.now().minusMonths((i % 80) + 1);
            list.add(new Employee(i, name, "City" + i, "State" + i, category, managerId, 40000 + (i % 10000), doj));
        }

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Employees");
        String[] headers = {"id", "name", "city", "state", "category", "manager_id", "salary", "DOJ"};

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) headerRow.createCell(i).setCellValue(headers[i]);

        int rowNum = 1;
        for (Employee e : list) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(e.id);
            row.createCell(1).setCellValue(e.name);
            row.createCell(2).setCellValue(e.city);
            row.createCell(3).setCellValue(e.state);
            row.createCell(4).setCellValue(e.category);
            //row.createCell(5).setCellValue(e.managerId != null ? e.managerId.toString() : "");
            Cell managerCell = row.createCell(5);
            if (e.managerId != null) {
                managerCell.setCellValue(e.managerId);
            } else {
                managerCell.setBlank();
            }
            row.createCell(6).setCellValue(e.salary);
            row.createCell(7).setCellValue(e.doj.format(FORMATTER));
        }

        try (FileOutputStream fos = new FileOutputStream(path)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    public static List<Employee> readExcel(String filePath) throws Exception {
        List<Employee> employees = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row r = sheet.getRow(i);
                if (r == null) continue;
                int id = (int) r.getCell(0).getNumericCellValue();
                String name = r.getCell(1).getStringCellValue();
                String city = r.getCell(2).getStringCellValue();
                String state = r.getCell(3).getStringCellValue();
                String category = r.getCell(4).getStringCellValue();
                Integer managerId = null;
                if (r.getCell(5).getCellType() == CellType.NUMERIC)
                    managerId = (int) r.getCell(5).getNumericCellValue();
                int salary = (int) r.getCell(6).getNumericCellValue();
                LocalDate doj = LocalDate.parse(r.getCell(7).getStringCellValue(), FORMATTER);
                employees.add(new Employee(id, name, city, state, category, managerId, salary, doj));
            }
        }
        return employees;
    }


    public static List<Employee> getGratuityEligible(List<Employee> list) {
        return list.stream()
                .filter(e -> e.doj.isBefore(LocalDate.now().minusMonths(60)))
                .collect(Collectors.toList());
    }

    public static List<Employee> salaryGreaterThanManager(List<Employee> list) {
        Map<Integer, Employee> empMap = list.stream().collect(Collectors.toMap(e -> e.id, e -> e));
        return list.stream()
                .filter(e -> e.managerId != null)
                .filter(e -> {
                    Employee m = empMap.get(e.managerId);
                    return m != null && e.salary > m.salary;
                })
                .collect(Collectors.toList());
    }

    public static EmployeeHierarchy buildEmployeeHierarchy(List<Employee> empList) {
        Map<Integer, EmployeeHierarchy> nodeMap = new HashMap<>();
        for (Employee emp : empList) nodeMap.put(emp.id, new EmployeeHierarchy(emp));

        EmployeeHierarchy root = null;
        for (Employee employee : empList) {
            //System.out.println("employee manager id::"+employee.managerId);
            if (employee.managerId == null && root==null) {
                root = nodeMap.get(employee.id);
            } else {
                EmployeeHierarchy m = nodeMap.get(employee.managerId);
                if (m != null) m.reportees.add(nodeMap.get(employee.id));
            }
        }
        return root;
    }


    public static void writeHierarchyToJson(EmployeeHierarchy root, String path) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(path), root);
    }
}
