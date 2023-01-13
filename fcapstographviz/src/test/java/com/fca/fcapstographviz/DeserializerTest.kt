package com.fca.fcapstographviz

import com.fca.fcapstographviz.deserializer.GsonProvider
import com.fca.fcapstographviz.entities.*
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class DeserializerTest {
    val deserializer = GsonProvider.newInstance()

    @Test
    fun arcDeserializationTest() {
        val str = "{\"S\":1,\"D\":2}"
        val expectedArc = Arc(1, 2)
        val actualArc = deserializer.fromJson(str, Arc::class.java)

        assertEquals(expectedArc, actualArc)
    }

    @Test
    fun intentDeserializationTest() {
        val str = "{\"Count\":4,\"Names\":[\"needs water to live\",\"lives on land\",\"needs chlorophyll\",\"monocotyledon\"]}"
        val expectedIntent = Intent(
            count = 4,
            names = listOf("needs water to live", "lives on land", "needs chlorophyll", "monocotyledon")
        )

        val actualIntent = deserializer.fromJson(str, Intent::class.java)

        assertEquals(expectedIntent, actualIntent)
    }

    @Test
    fun NodeDeserializationTest() {
        val str = "{\n" +
                "\t\t\t\"Ext\":{\"Count\":3,\"Inds\":[0,1,2],\"Names\":[\"fish leech\",\"bream\",\"frog\"]},\n" +
                "\t\t\t\"LStab\":1.0,\"UStab\":1,\"Stab\":1.0,\n" +
                "\t\t\t\"Int\":{\"Count\":3,\"Names\":[\"needs water to live\",\"lives in water\",\"can move\"]}\n" +
                "\t\t}"

        val expectedNode = Node(
            extent = Extent(
                count = 3,
                indices = listOf(0, 1, 2),
                names = listOf("fish leech", "bream", "frog")
            ),
            lStab = 1.0,
            uStab = 1,
            stab = 1.0,
            intent = Intent(
                count = 3,
                names = listOf("needs water to live", "lives in water", "can move")
            )
        )

        val actualNode = deserializer.fromJson(str, Node::class.java)

        assertEquals(expectedNode, actualNode)
    }

    @Test
    fun rootNodeDeserializationTest() {
        val str = "{\n" +
                "\t\t\t\"Ext\":{\"Count\":0,\"Inds\":[]},\n" +
                "\t\t\t\"Stab\":\"inf\",\n" +
                "\t\t\t\"Int\":\"BOTTOM\"\n" +
                "\t\t}"

        val expectedNode = Node(
            extent = Extent(
                count = 0,
                indices = emptyList(),
                names = emptyList()
            ),
            lStab = null,
            uStab = 0,
            stab = Node.STAB_INF,
            intent = null
        )

        val actualNode = deserializer.fromJson(str, Node::class.java)

        assertEquals(expectedNode, actualNode)
    }

    @Test
    fun graphInfoDeserializerTest() {
        val str = "{\n" +
                "\t\"NodesCount\":20,\n" +
                "\t\"ArcsCount\":36,\n" +
                "\t\"Bottom\":[0],\n" +
                "\t\"Top\":[4]\n" +
                "}"

        val expectedInfo = GraphInfo(20, 36, 0, 4)
        val actualInfo = deserializer.fromJson(str, GraphInfo::class.java)

        assertEquals(expectedInfo, actualInfo)
    }

    @Test
    fun graphDeserializerTest() {
        val str = "[{\n" +
                "\t\"NodesCount\":2,\n" +
                "\t\"ArcsCount\":1,\n" +
                "\t\"Bottom\":[0],\n" +
                "\t\"Top\":[1]\n" +
                "},{\n" +
                "\t\"Nodes\":[\n" +
                "\t\t{\n" +
                "\t\t\t\"Ext\":{\"Count\":1,\"Inds\":[0],\"Names\":[\"Ext1\"]},\n" +
                "\t\t\t\"LStab\":1.0,\"UStab\":1,\"Stab\":1,\n" +
                "\t\t\t\"Int\":{\"Count\":1,\"Names\":[\"Int1\"]}\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"Ext\":{\"Count\":1,\"Inds\":[1],\"Names\":[\"Ext2\"]},\n" +
                "\t\t\t\"LStab\":1.0,\"UStab\":1,\"Stab\":1,\n" +
                "\t\t\t\"Int\":{\"Count\":1,\"Names\":[\"Int2\"]}\n" +
                "\t\t}\n" +
                "\t]\n" +
                "},{\n" +
                "\t\"Arcs\":[\n" +
                "\t\t{\"S\":1, \"D\":2}\n" +
                "\t]\n" +
                "}]"

        val expectedGraph = Graph(
            graphInfo = GraphInfo(2, 1, 0, 1),
            nodes = listOf(
                Node(
                    extent = Extent(1, listOf(0), listOf("Ext1")),
                    lStab = 1.0,
                    uStab = 1,
                    stab = 1.0,
                    intent = Intent(1, listOf("Int1"))
                ),
                Node(
                    extent = Extent(1, listOf(1), listOf("Ext2")),
                    lStab = 1.0,
                    uStab = 1,
                    stab = 1.0,
                    intent = Intent(1, listOf("Int2"))
                )
            ),
            arcs = listOf(Arc(source = 1, destination = 2))
        )

        val actualGraph = deserializer.fromJson(str, Graph::class.java)

        assertEquals(expectedGraph, actualGraph)
    }
}