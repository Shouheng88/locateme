package me.shouheng.locate.engine.jobs

import me.shouheng.locate.engine.IEngineJob
import me.shouheng.locate.engine.keyword.SearchKeywords
import me.shouheng.locate.engine.source.CodeSources

/** Source locate implementation. */
class SourceLocateJob(
    val keywords: SearchKeywords,
    val sourceCode: CodeSources
): IEngineJob {
    override fun startJob() {

    }
}