package com.logikaldb.examples

import com.logikaldb.ConstraintFactory.and
import com.logikaldb.ConstraintFactory.eq
import com.logikaldb.ConstraintFactory.or
import com.logikaldb.ConstraintFactory.field
import com.logikaldb.LogikalDB
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal

private fun main() {
    runBlocking {
        val logikalDB = LogikalDB()

        val empFirstName = field("employee.firstName", String::class.java)
        val empLastName = field("employee.lastName", String::class.java)
        val empDepartment = field("employee.department", String::class.java)
        val empSalary = field("employee.salary", BigDecimal::class.java)

        // Write employee data to the database
        logikalDB.write(
            listOf("example", "join"), "employee",
            or(
                and(eq(empFirstName, "John"), eq(empLastName, "Smith"), eq(empDepartment, "HR"), eq(empSalary, BigDecimal(10000))),
                and(eq(empFirstName, "Yolanda"), eq(empLastName, "Gates"), eq(empDepartment, "IT"), eq(empSalary, BigDecimal(25000))),
                and(eq(empFirstName, "Bill"), eq(empLastName, "Smith"), eq(empDepartment, "HR"), eq(empSalary, BigDecimal(15000))),
                and(eq(empFirstName, "Gustav"), eq(empLastName, "Musk"), eq(empDepartment, "IT"), eq(empSalary, BigDecimal(20000))),
                and(eq(empFirstName, "Charlie"), eq(empLastName, "Fisher"), eq(empDepartment, "SE"), eq(empSalary, BigDecimal(30000))),
            )
        )

        val depDepartmentName = field("employee.departmentName", String::class.java)
        val depManager = field("department.manager", String::class.java)
        val depManagerEmail = field("department.managerEmail", String::class.java)

        // Write department data to the database
        logikalDB.write(
            listOf("example", "join"), "department",
            or(
                and(eq(depDepartmentName, "HR"), eq(depManager, "Chris Harris"), eq(depManagerEmail, "charris@company.com")),
                and(eq(depDepartmentName, "IT"), eq(depManager, "Olivia Jones"), eq(depManagerEmail, "ojones@company.com")),
                and(eq(depDepartmentName, "SE"), eq(depManager, "Bill Milles"), eq(depManagerEmail, "bmilles@company.com"))
            )
        )

        // Read out the data (flow) from the database
        val employees = logikalDB.read(listOf("example", "join"), "employee")
        val departments = logikalDB.read(listOf("example", "join"), "department")

        // This is the constraint used to join the tables based on the department name
        val joinGoal = eq(empDepartment, depDepartmentName)

        // Run the join query and print out the results
        employees.join(joinGoal, departments)
            .select()
            .forEach { println("Result by joining two tables: $it") }
    }
}
