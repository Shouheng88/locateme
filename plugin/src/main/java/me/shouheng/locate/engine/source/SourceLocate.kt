package me.shouheng.locate.engine.source

import me.shouheng.locate.engine.keyword.SearchKeywords

/** Source locate implementation. */
class SourceLocate(
    val keywords: SearchKeywords,
    val sourceCode: CodeSources
): ISourceLocate {
    override fun doLocate() {

    }
}