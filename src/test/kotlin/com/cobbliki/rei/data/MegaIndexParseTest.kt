package com.cobbliki.rei.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class MegaIndexParseTest {
    @Test
    fun `parses array megaEvolves with multiple base formes`() {
        val js = """{ name: "Mewtwonite X", megaStone: "Mewtwo-Mega-X", megaEvolves: ["Mewtwo","Mewtwo-Armored"] }"""
        val e = MegaIndex.parse("mega_showdown", "mewtwonitex", js)!!
        assertEquals("mega_showdown", e.namespace)
        assertEquals("mewtwonitex", e.itemKey)
        assertEquals("Mewtwo-Mega-X", e.megaStone)
        assertEquals(listOf("Mewtwo", "Mewtwo-Armored"), e.baseFormes)
    }

    @Test
    fun `parses single shadow base forme`() {
        val js = """{ name: "Shadow Mewtwonite X", megaStone: "Mewtwo-Mega-SX", megaEvolves: ["Mewtwo-Shadow"] }"""
        val e = MegaIndex.parse("mega_showdown", "shadowmewtwonitex", js)!!
        assertEquals("Mewtwo-Mega-SX", e.megaStone)
        assertEquals(listOf("Mewtwo-Shadow"), e.baseFormes)
    }

    @Test
    fun `parses string megaEvolves`() {
        val js = """{ megaStone: "Charizard-Mega-Y", megaEvolves: "Charizard" }"""
        val e = MegaIndex.parse("mega_showdown", "charizarditey", js)!!
        assertEquals("Charizard-Mega-Y", e.megaStone)
        assertEquals(listOf("Charizard"), e.baseFormes)
    }

    @Test
    fun `returns null when not a mega stone`() {
        assertNull(MegaIndex.parse("mega_showdown", "eviolite", """{ name: "Eviolite", spritenum: 1 }"""))
    }
}
