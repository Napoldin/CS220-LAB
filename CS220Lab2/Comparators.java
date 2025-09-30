import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// [1] Geeks for Geeks. java comparator interface. https://www.geeksforgeeks.org/java/java-comparator-interface/. 31 Jul, 2025


/**
 * Driver class
 */
public class Comparators{
    public static void main(String[] args) {
        List<Employee> employees = new ArrayList<>();

        employees.add(new Employee("John", 24, 50000));
        employees.add(new Employee("Bohn", 25, 90000));
        employees.add(new Employee("Fohn", 22, 50000));
        employees.add(new Employee("Aohn", 22, 50000));
        employees.add(new Employee("Eohn", 21, 50000));
        employees.add(new Employee("Dohn", 22, 30000));

        System.out.println("Before sorting:");
        for (Employee employee : employees) {System.out.println(employee.getInfo());}
        System.out.println();
        employees.sort(new EmployeeSorter());
        System.out.println("After sorting:");
        for (Employee employee : employees) {System.out.println(employee.getInfo());}
    }
}

/**
 * The class representing Employees
 * Contains name, age, and salary and a "to string" method
 */
class Employee{
    String name;
    Integer age;
    Integer salary;

    /**
     * Just a constructor method for the class
     *
     * @param name Employee name
     * @param age Employee Age
     * @param salary Employee Salary
     */
    Employee(String name, int age, int salary){
        this.name = name;
        this.age = age;
        this.salary = salary;
    }

    /**
     * Gives a string representation of the object
     *
     * @return a String representation of the object
     */
    String getInfo(){return this.name + ": " + this.age + ", " + this.salary;}
}


/**
 * Class for sorting through Employees
 */
class EmployeeSorter implements Comparator<Employee>{

    /**
     * Compares two employees and moves them accordingly for
     * age in ascending order, then by salary in descending order,
     * then by name in alphabetical order.
     *
     * @param e1 Employee 1
     * @param e2 Employee 2
     * @return int of -1, 0, or 1 representing where we're moving our employees
     */
    public int compare(Employee e1, Employee e2){
        int ageCompare = e1.age.compareTo(e2.age);
        if (ageCompare != 0){return ageCompare;}
        int salaryCompare = e1.salary.compareTo(e2.salary)*-1;
        if (salaryCompare != 0){return salaryCompare;}
        int nameCompare = e1.name.compareTo(e2.name);
        return nameCompare;
    }
}