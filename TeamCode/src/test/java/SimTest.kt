package org.firstinspires.ftc.teamcode.test

import org.firstinspires.ftc.teamcode.sim.DataAnalyzer
import org.firstinspires.ftc.teamcode.sim.NotActuallyATest
import org.firstinspires.ftc.teamcode.util.json.tokenize
import org.junit.Test
import java.nio.file.Files
import kotlin.io.path.Path

class SimTest {
    @Test
    fun testFileLoader(){
        var analyzer = DataAnalyzer()
        var testData = "{\n" +
                "  \"what is this for\" : \"testing the file loader\",\n" +
                "  \"why\" : \"Avery has this thing where he unit tests random things that don't need testing\",\n" +
                "  \"wait what?\" : [\n" +
                "    \"yeah\",\n" +
                "    \"he tests stuff that obviously works\",\n" +
                "    \"but then doesnt write tests for other things\",\n" +
                "    \"and those are the things that break\",\n" +
                "    {\n" +
                "      \"broken\" : \"litterally every string extension function\",\n" +
                "      \"mood\" : \":(\",\n" +
                "      \"maybe add other data types as well?\" : 12345\n" +
                "    }\n" +
                "  ]\n" +
                "}"

        analyzer.load("src/test/java/testData.json")

        assert(tokenize( analyzer.data ).toString() == testData)
    }
}