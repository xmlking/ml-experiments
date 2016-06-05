package com.sumo.experiments.ml.nlp

import com.aliasi.classify.Classification
import com.aliasi.classify.Classified
import com.aliasi.classify.DynamicLMClassifier
import com.aliasi.lm.NGramBoundaryLM

import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.HashSet

object LingPipeTester {

    @Throws(Exception::class)
    @JvmStatic fun main(args: Array<String>) {

        val trainingData = LingPipeTester::class.java.classLoader.getResourceAsStream("twitterTrainingData_clean.csv")
        val maxCharNGram = 3

        var reader = BufferedReader(InputStreamReader(trainingData))
        val categorySet = HashSet<String>()
        val annotatedData = ArrayList<Array<String>>()
        var line: String? = reader.readLine()
        while (line != null) {
            val data = line.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            categorySet.add(data[0])
            annotatedData.add(data)
            line = reader.readLine()
        }
        println("read in all data")
        reader.close()
        val categories = categorySet.toTypedArray()

        val classifier = DynamicLMClassifier.createNGramBoundary(categories, maxCharNGram)
        for (row in annotatedData) {
            val truth = row[0]
            val text = row[1]
            val classification = Classification(truth)
            val classified = Classified<CharSequence>(text, classification)
            classifier.handle(classified)
        }
        println("training complete")

        reader = BufferedReader(InputStreamReader(System.`in`))

        println("enter text, I'll tell you the language")
        var text: String? = null
        while ({text = reader.readLine(); text}() != "quit") {
            val classification = classifier.classify(text)
            println("Entered -> " + text)
            println("lang -> " + classification.bestCategory())
        }
    }
}
