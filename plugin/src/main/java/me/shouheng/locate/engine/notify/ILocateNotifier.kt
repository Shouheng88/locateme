package me.shouheng.locate.engine.notify

import me.shouheng.locate.engine.keyword.SearchKeywords

/** The LocateMe notifier. */
interface ILocateNotifier {

    /** Do notify. */
    fun notify(result: SearchKeywords)
}
