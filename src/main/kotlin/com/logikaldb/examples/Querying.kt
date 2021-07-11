package com.logikaldb.examples

import com.logikaldb.ConstraintFactory.and
import com.logikaldb.ConstraintFactory.eq
import com.logikaldb.ConstraintFactory.or
import com.logikaldb.ConstraintFactory.field
import com.logikaldb.LogikalDB
import com.logikaldb.StdLib.notEq
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import java.math.BigDecimal

private fun main() {
    runBlocking {
        val logikalDB = LogikalDB()

        val firstName = field("firstName", String::class.java)
        val lastName = field("lastName", String::class.java)
        val department = field("department", String::class.java)
        val salary = field("salary", BigDecimal::class.java)

        // Write employee data to the database
        logikalDB.write(
            listOf("example", "query"), "employee",
            or(
                and(eq(firstName, "John"), eq(lastName, "Smith"), eq(department, "HR"), eq(salary, BigDecimal(10000))),
                and(eq(firstName, "Yolanda"), eq(lastName, "Gates"), eq(department, "IT"), eq(salary, BigDecimal(25000))),
                and(eq(firstName, "Bill"), eq(lastName, "Smith"), eq(department, "HR"), eq(salary, BigDecimal(15000))),
                and(eq(firstName, "Gustav"), eq(lastName, "Musk"), eq(department, "IT"), eq(salary, BigDecimal(20000))),
                and(eq(firstName, "Charlie"), eq(lastName, "Fisher"), eq(department, "SE"), eq(salary, BigDecimal(30000))),
            )
        )

        // Query the employee data with lastName==Smith constraint
        logikalDB.read(listOf("example", "query"), "employee")
            .and(eq(lastName, "Smith"))
            .select()
            .forEach { println("Result with lastName==Smith query: $it") }

        // Query the employee data with lastName!=Smith and department==IT constraint
        logikalDB.read(listOf("example", "query"), "employee")
            .and(notEq(lastName, "Smith"), eq(department, "IT"))
            .select()
            .forEach { println("Result with lastName!=Smith and department==IT query: $it") }

        // Same query as before but using selectFlow instead
        logikalDB.read(listOf("example", "query"), "employee")
            .and(notEq(lastName, "Smith"), eq(department, "IT"))
            .selectFlow()
            .collect { println("Flow Result with lastName!=Smith and department==IT query: $it") }
    }
}
