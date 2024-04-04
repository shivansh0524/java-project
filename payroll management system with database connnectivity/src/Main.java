import java.sql.*;
import java.util.*;

abstract class employee {
    private String emp_name;
    private int emp_id;

    public employee(String name, int id) {
        this.emp_id = id;
        this.emp_name = name;
    }

    public String getName() {
        return emp_name;
    }

    public int getId() {
        return emp_id;
    }

    public abstract double calculateSalary();

    @Override
    public String toString() {
        return "Employee[name = " + emp_name + ", id =" + emp_id + ", salary =" + calculateSalary() + "]";
    }
}

class fullTimeEmployee extends employee {
    private double monthlySalary;

    public fullTimeEmployee(String name, int id, double salary) {
        super(name, id);
        this.monthlySalary = salary;
    }

    @Override
    public double calculateSalary() {
        return monthlySalary;
    }
}

class partTimeEmployee extends employee {
    private int hoursWorked;
    private double hourlyRate;

    public partTimeEmployee(String name, int id, int hoursWorked, double hourlyRate) {
        super(name, id);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double calculateSalary() {
        return hoursWorked * hourlyRate;
    }
}

class payrollSystem {
    private Connection conn;
    private ArrayList<employee> employeeList;
    public payrollSystem() {
        employeeList = new ArrayList<employee>();
        try {

            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javaa", "root", "1234567");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void fetchEmployees() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM employees where emp_id>8");
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                String name = rs.getString("emp_name");
                int id = rs.getInt("emp_id");
                double salary = rs.getDouble("salary");
                employee emp;
                // Determine if employee is full-time or part-time based on salary
                if (salary >= 0) {
                    emp = new fullTimeEmployee(name, id, salary);
                } else {
                    emp = new partTimeEmployee(name, id, (int) -salary, -salary);
                }
                addEmployee(emp);
            }
            System.out.println("Number of rows retrieved: " + rowCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void addEmployee(employee employee)
    {
        employeeList.add(employee);
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO employees (emp_name, emp_id, salary) VALUES (?, ?, ?)");
            pstmt.setString(1, employee.getName());
            pstmt.setInt(2, employee.getId());
            pstmt.setDouble(3, employee.calculateSalary());
            pstmt.executeUpdate();
            System.out.println("Employee added successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeEmployee(int id) {
        // Remove employee from the database
        employee employeeToRemove = null;
        for (employee employee:employeeList) {
            if (employee.getId() == id) {

                employeeToRemove = employee;
                break;
            }
        }
        if(employeeToRemove != null)
        {
            employeeList.remove(employeeToRemove);
        }
    }


    public void displayEmployee() {
        // Display employees
        for (employee employee:employeeList)
        {
            System.out.println(employee);

        }
    }
}

public class Main {
    public static void main(String[] args) {
        payrollSystem pt = new payrollSystem();
//        pt.fetchEmployees(); // Fetch employees from database
        fullTimeEmployee emp1 = new fullTimeEmployee("aryan",12, 220000);
        fullTimeEmployee emp2 = new fullTimeEmployee("kamalKant",19, 50000);

        pt.addEmployee(emp1);
        pt.addEmployee(emp2);
        pt.displayEmployee();
        pt.removeEmployee(5);

        System.out.println();
        System.out.println(" After removing a element:");
        pt.displayEmployee();
    }
}
