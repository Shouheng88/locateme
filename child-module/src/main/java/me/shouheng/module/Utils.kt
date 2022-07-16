package me.shouheng.module

/** The utils. */
object Utils {

    private const val helloStatic = "Hello There"

    /** Test keyword usage. */
    fun testKeyword(): String {
        println("Hello" + System.currentTimeMillis())
        return helloStatic;
    }
}
