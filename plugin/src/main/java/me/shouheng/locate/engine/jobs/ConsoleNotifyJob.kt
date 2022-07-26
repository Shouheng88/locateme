package me.shouheng.locate.engine.jobs

import me.shouheng.locate.engine.IEngineJob
import me.shouheng.locate.engine.keyword.SearchKeywords

/** The default implementation of notifier by output to console. */
class ConsoleNotifyJob(keywords: SearchKeywords): IEngineJob {
    override fun startJob() {

    }
}
