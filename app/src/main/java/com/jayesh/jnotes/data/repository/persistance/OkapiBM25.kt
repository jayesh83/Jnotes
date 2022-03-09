package com.jayesh.jnotes.data.repository.persistance

import kotlin.math.log

/** One of the search result relevance ranking algorithm **/
class OkapiBM25 {

    companion object {

        fun score(matchInfo: ByteArray, column: Int, b: Double = 0.75, k1: Double = 1.2): Double {
            val pOffset = 0
            val cOffset = 1
            val nOffset = 2
            val aOffset = 3

            val termCount = matchInfo[pOffset]
            val colCount = matchInfo[cOffset]

            val lOffset = aOffset + colCount
            val xOffset = lOffset + colCount

            val totalDocs = matchInfo[nOffset].toDouble()
            val avgLength = matchInfo[aOffset + column].toDouble()
            val docLength = matchInfo[lOffset + column].toDouble()

            var score = 0.0

            for (i in 0 until termCount) {
                val currentX = xOffset + (3 * (column + i * colCount))
                val termFrequency = matchInfo[currentX].toDouble()
                val docsWithTerm = matchInfo[currentX + 2].toDouble()

                val p = totalDocs - docsWithTerm + 0.5
                val q = docsWithTerm + 0.5
                val idf = log(p,q)

                val r = termFrequency * (k1 + 1)
                val s = b * (docLength / avgLength)
                val t = termFrequency + (k1 * (1 - b + s))
                val rightSide = r/t

                score += (idf * rightSide)
            }

            return score

        }
    }
}