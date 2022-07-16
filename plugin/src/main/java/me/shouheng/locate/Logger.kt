package me.shouheng.locate

/** Log printer. */
object Logger {

    private const val LEVEL_V = 0
    private const val LEVEL_D = 1
    private const val LEVEL_I = 2
    private const val LEVEL_W = 3
    private const val LEVEL_E = 4

    private var logLevel: Int = LEVEL_D

    /** Set log level. */
    fun setLevel(level: Int) {
        this.logLevel = level
    }

    /** Show debug log. */
    fun verbose(msg: String) {
        if (logLevel < LEVEL_V) {
            return
        }
        println(">>> Locate [V] $msg")
    }

    /** Show debug log. */
    fun debug(msg: String) {
        if (logLevel < LEVEL_D) {
            return
        }
        println(">>> Locate [D] $msg")
    }

    /** Show debug log. */
    fun info(msg: String) {
        if (logLevel < LEVEL_I) {
            return
        }
        println(">>> Locate [I] $msg")
    }

    /** Show error log. */
    fun warn(msg: String) {
        if (logLevel < LEVEL_W) {
            return
        }
        println(">>> Locate [W] $msg")
    }

    /** Show error log. */
    fun error(msg: String) {
        if (logLevel < LEVEL_E) {
            return
        }
        println(">>> Locate [E] $msg")
    }
}