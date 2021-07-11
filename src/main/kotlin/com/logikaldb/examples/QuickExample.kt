package com.logikaldb.examples

import com.logikaldb.ConstraintFactory.and
import com.logikaldb.ConstraintFactory.eq
import com.logikaldb.ConstraintFactory.or
import com.logikaldb.ConstraintFactory.field
import com.logikaldb.LogikalDB
import kotlinx.coroutines.runBlocking

private fun main() {
    runBlocking {
        val logikalDB = LogikalDB()

        val pokemonName = field("name", String::class.java)
        val pokemonType = field("type", String::class.java)

        val dataset = or(
            and(eq(pokemonName, "Bulbasaur"), eq(pokemonType, "Grass")),
            and(eq(pokemonName, "Charmander"), eq(pokemonType, "Fire")),
            and(eq(pokemonName, "Squirtle"), eq(pokemonType, "Water")),
            and(eq(pokemonName, "Vulpix"), eq(pokemonType, "Fire"))
        )
        val query = eq(pokemonType, "Fire")

        // Write the dataset to the database
        logikalDB.write(listOf("example", "quick"), "pokemon", dataset)

        // Query the pokemon, which type is fire and finally print out the name of the found pokemons
        logikalDB.read(listOf("example", "quick"), "pokemon")
            .and(query)
            .select(pokemonName)
            .forEach { println("Result: $it") }
    }
}
