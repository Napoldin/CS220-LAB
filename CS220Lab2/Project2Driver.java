import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// for reference https://www.geeksforgeeks.org/java/java-comparator-interface/

public class Project2Driver{
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

class Employee{
    String name;
    Integer age;
    Integer salary;

    Employee(String name, int age, int salary){
        this.name = name;
        this.age = age;
        this.salary = salary;
    }

    String getInfo(){return this.name + ": " + this.age + ", " + this.salary;}
}

class EmployeeSorter implements Comparator<Employee>{
    public int compare(Employee e1, Employee e2){
        int ageCompare = e1.age.compareTo(e2.age);
        if (ageCompare != 0){return ageCompare;}
        int salaryCompare = e1.salary.compareTo(e2.salary)*-1;
        if (salaryCompare != 0){return salaryCompare;}
        int nameCompare = e1.name.compareTo(e2.name);
        return nameCompare;
    }
}