package com.logikaldb.examples

import com.logikaldb.Constraint.and
import com.logikaldb.Constraint.eq
import com.logikaldb.Constraint.or
import com.logikaldb.Constraint.vr
import com.logikaldb.LogikalDB
import com.logikaldb.StdLib.inSet
import com.logikaldb.StdLib.notEq
import com.logikaldb.entity.Goal
import com.logikaldb.logikal.Variable
import kotlinx.coroutines.runBlocking

/*
* Implemented the murder mystery from this website: https://xmonader.github.io/prolog/2018/12/21/solving-murder-prolog.html
* */

private val manSet = setOf("George", "John", "Robert")
private val womanSet = setOf("Barbara", "Christine", "Yolanda")

private val bathroom = vr("bathroom", String::class.java)
private val diningRoom = vr("diningRoom", String::class.java)
private val kitchen = vr("kitchen", String::class.java)
private val livingRoom = vr("livingRoom", String::class.java)
private val pantry = vr("pantry", String::class.java)
private val study = vr("study", String::class.java)

private val bag = vr("bag", String::class.java)
private val firearm = vr("firearm", String::class.java)
private val gas = vr("gas", String::class.java)
private val knife = vr("knife", String::class.java)
private val poison = vr("poison", String::class.java)
private val rope = vr("rope", String::class.java)

private val murder = vr("murder", String::class.java)

private fun people(variable: Variable<String>): Goal = or(inSet(variable, manSet), inSet(variable, womanSet))

private fun uniquePeople(peopleVariables: List<Variable<String>>): Goal {
    val peopleGoals = peopleVariables.map(::people)
    val uniquePeopleGoals = mutableListOf<Goal>()
    val numberOfPeople = peopleVariables.size - 1
    for (i in 0..numberOfPeople) {
        for (j in i + 1..numberOfPeople) {
            uniquePeopleGoals.add(notEq(peopleVariables[i], peopleVariables[j]))
        }
    }
    return and(peopleGoals + uniquePeopleGoals)
}

private fun murderer(): Goal {
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
