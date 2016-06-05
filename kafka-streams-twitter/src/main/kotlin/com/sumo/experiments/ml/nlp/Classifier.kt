package com.sumo.experiments.ml.nlp

import com.aliasi.classify.Classification
import com.aliasi.classify.Classified
import com.aliasi.classify.DynamicLMClassifier
import com.aliasi.lm.NGramBoundaryLM

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.ArrayList
import java.util.HashSet

class Classifier @JvmOverloads constructor(private val trainingDataDelimiter: String = "#") {

    private var classifier: DynamicLMClassifier<NGramBoundaryLM>? = null
    private val maxCharNGram = 3

    fun train(filePath: String) {
        val trainingData = this.javaClass.classLoader.getResourceAsStream(filePath)
        val categorySet = HashSet<String>()
        val annotatedData = ArrayList<Array<String>>()
        fillCategoriesAndAnnotatedData(trainingData, categorySet, annotatedData)
        trainClassifier(categorySet, annotatedData)
    }

    private fun trainClassifier(categorySet: Set<String>, annotatedData: List<Array<String>>) {
        val categories = categorySet.toTypedArray()
        classifier = DynamicLMClassifier.createNGramBoundary(categories, maxCharNGram)
        for (row in annotatedData) {
            val actualClassification = row[0]
            val text = row[1]
            val classification = Classification(actualClassification)
            val classified = Classified<CharSequence>(text, classification)
            classifier!!.handle(classified)
        }
    }


    private fun fillCategoriesAndAnnotatedData(trainingData: InputStream,
                                               categorySet: MutableSet<String>,
                                               annotatedData: MutableList<Array<String>>) {
        try {
            val reader = BufferedReader(InputStreamReader(trainingData))
            var line: String? = reader.readLine()
            while (line != null) {
                val data = line.split(trainingDataDelimiter.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                categorySet.add(data[0])
                annotatedData.add(data)
                line = reader.readLine()
            }

        } catch (e: IOException) {
            throw RuntimeException(e)
        }

    }


    fun classify(text: String): String {
        return classifier!!.classify(text.trim { it <= ' ' }).bestCategory().toLowerCase()
    }

}
