package com.logikaldb.examples

import com.logikaldb.ConstraintFactory.and
import com.logikaldb.ConstraintFactory.eq
import com.logikaldb.ConstraintFactory.or
import com.logikaldb.ConstraintFactory.field
import com.logikaldb.LogikalDB
import com.logikaldb.StdLib.inSet
import com.logikaldb.StdLib.notEq
import com.logikaldb.entity.Constraint
import com.logikaldb.logikal.Field
import kotlinx.coroutines.runBlocking

/*
* Implemented the murder mystery from this website: https://xmonader.github.io/prolog/2018/12/21/solving-murder-prolog.html
* */

private val manSet = setOf("George", "John", "Robert")
private val womanSet = setOf("Barbara", "Christine", "Yolanda")

private val bathroom = field("bathroom", String::class.java)
private val diningRoom = field("diningRoom", String::class.java)
private val kitchen = field("kitchen", String::class.java)
private val livingRoom = field("livingRoom", String::class.java)
private val pantry = field("pantry", String::class.java)
private val study = field("study", String::class.java)

private val bag = field("bag", String::class.java)
private val firearm = field("firearm", String::class.java)
private val gas = field("gas", String::class.java)
private val knife = field("knife", String::class.java)
private val poison = field("poison", String::class.java)
private val rope = field("rope", String::class.java)

private val murder = field("murder", String::class.java)

private fun people(variable: Field<String>): Constraint = or(inSet(variable, manSet), inSet(variable, womanSet))

private fun uniquePeople(peopleVariables: List<Field<String>>): Constraint {
    val peopleGoals = peopleVariables.map(::people)
    val uniquePeopleGoals = mutableListOf<Constraint>()
    val numberOfPeople = peopleVariables.size - 1
    for (i in 0..numberOfPeople) {
        for (j in i + 1..numberOfPeople) {
            uniquePeopleGoals.add(notEq(peopleVariables[i], peopleVariables[j]))
        }
    }
    return and(peopleGoals + uniquePeopleGoals)
}

private fun murderer(): Constraint {
    val entry = and(
        uniquePeople(listOf(bathroom, diningRoom, kitchen, livingRoom, pantry, study)),
        uniquePeople(listOf(bag, firearm, gas, knife, poison, rope))
    )
    val clue1 = and(
        inSet(kitchen, manSet), notEq(kitchen, rope), notEq(kitchen, knife), notEq(kitchen, bag),
        notEq(kitchen, firearm)
    )
    val clue2 = and(inSet(bathroom, setOf("Barbara", "Yolanda")), inSet(study, setOf("Barbara", "Yolanda")))
    val clue3 = and(notEq(bag, "Barbara"), notEq(bag, "George"), notEq(bag, bathroom), notEq(bag, diningRoom))
    val clue4 = and(inSet(rope, womanSet), eq(rope, study))
    val clue5 = inSet(livingRoom, setOf("John", "George"))
    val clue6 = notEq(knife, diningRoom)
    val clue7 = and(notEq(pantry, "Yolanda"), notEq(study, "Yolanda"))
    val clue8 = eq(firearm, "George")
    val clue9 = and(eq(pantry, gas), eq(pantry, murder))
    return and(entry, clue1, clue2, clue3, clue4, clue5, clue6, clue7, clue8, clue9)
}

private fun main() {
    runBlocking {
        val logikalDB = LogikalDB()

        // Save the murder mystery query to the database
        logikalDB.write(listOf("example", "murderMystery"), "murderer", murderer())

        // Read out the query from the db and evaluate it, but only ask for the murder as the result
        logikalDB.read(listOf("example", "murderMystery"), "murderer")
            .select(murder)
            .forEach { println("Result: $it") }
    }
}
