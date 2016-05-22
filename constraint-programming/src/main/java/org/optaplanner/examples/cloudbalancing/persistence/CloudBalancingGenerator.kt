/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.cloudbalancing.persistence

import java.io.File
import java.math.BigInteger
import java.util.ArrayList
import java.util.Random

import org.optaplanner.examples.cloudbalancing.domain.CloudBalance
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess
import org.optaplanner.examples.common.app.LoggingMain
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter
import org.optaplanner.examples.common.persistence.SolutionDao

class CloudBalancingGenerator : LoggingMain {

    private class Price constructor(val hardwareValue: Int, val description: String, val cost: Int)

    protected val solutionDao: SolutionDao?
    protected val outputDir: File?
    protected var random: Random = Random(47)

    constructor() {
        checkConfiguration()
        solutionDao = CloudBalancingDao()
        outputDir = File(solutionDao.dataDir, "unsolved")
    }

    constructor(withoutDao: Boolean) {
        if (!withoutDao) {
            throw IllegalArgumentException("The parameter withoutDao ($withoutDao) must be true.")
        }
        checkConfiguration()
        solutionDao = null
        outputDir = null
    }

    fun generate() {
        writeCloudBalance(2, 6)
        writeCloudBalance(3, 9)
        writeCloudBalance(4, 12)
        //        writeCloudBalance(5, 15);
        //        writeCloudBalance(6, 18);
        //        writeCloudBalance(7, 21);
        //        writeCloudBalance(8, 24);
        //        writeCloudBalance(9, 27);
        //        writeCloudBalance(10, 30);
        //        writeCloudBalance(11, 33);
        //        writeCloudBalance(12, 36);
        //        writeCloudBalance(13, 39);
        //        writeCloudBalance(14, 42);
        //        writeCloudBalance(15, 45);
        //        writeCloudBalance(16, 48);
        //        writeCloudBalance(17, 51);
        //        writeCloudBalance(18, 54);
        //        writeCloudBalance(19, 57);
        //        writeCloudBalance(20, 60);
        writeCloudBalance(100, 300)
        writeCloudBalance(200, 600)
        writeCloudBalance(400, 1200)
        writeCloudBalance(800, 2400)
        writeCloudBalance(1600, 4800)
    }

    private fun checkConfiguration() {
        if (CPU_POWER_PRICES.size != MEMORY_PRICES.size || CPU_POWER_PRICES.size != NETWORK_BANDWIDTH_PRICES.size) {
            throw IllegalStateException("All price arrays must be equal in length.")
        }
    }

    private fun writeCloudBalance(computerListSize: Int, processListSize: Int) {
        val fileName = determineFileName(computerListSize, processListSize)
        val outputFile = File(outputDir, fileName + ".xml")
        val cloudBalance = createCloudBalance(fileName, computerListSize, processListSize)
        solutionDao!!.writeSolution(cloudBalance, outputFile)
    }

    fun createCloudBalance(computerListSize: Int, processListSize: Int): CloudBalance {
        return createCloudBalance(determineFileName(computerListSize, processListSize),
                computerListSize, processListSize)
    }

    private fun determineFileName(computerListSize: Int, processListSize: Int): String {
        return "$computerListSize  computers- $processListSize processes"
    }

    fun createCloudBalance(inputId: String, computerListSize: Int, processListSize: Int): CloudBalance {
//        random = Random(47)
        val cloudBalance = CloudBalance()
        cloudBalance.id = 0L
        createComputerList(cloudBalance, computerListSize)
        createProcessList(cloudBalance, processListSize)
        assureComputerCapacityTotalAtLeastProcessRequiredTotal(cloudBalance)
        val possibleSolutionSize = BigInteger.valueOf(cloudBalance.computerList!!.size.toLong()).pow(
                cloudBalance.processList!!.size)
        logger.info("CloudBalance {} has {} computers and {} processes with a search space of {}.",
                inputId, computerListSize, processListSize,
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize))
        return cloudBalance
    }

    private fun createComputerList(cloudBalance: CloudBalance, computerListSize: Int) {
        val computerList = ArrayList<CloudComputer>(computerListSize)
        for (i in 0..computerListSize - 1) {
            val computer = CloudComputer()
            computer.id = i.toLong()
            val cpuPowerPricesIndex = random.nextInt(CPU_POWER_PRICES.size)
            computer.cpuPower = CPU_POWER_PRICES[cpuPowerPricesIndex].hardwareValue
            val memoryPricesIndex = distortIndex(cpuPowerPricesIndex, MEMORY_PRICES.size)
            computer.memory = MEMORY_PRICES[memoryPricesIndex].hardwareValue
            val networkBandwidthPricesIndex = distortIndex(cpuPowerPricesIndex, NETWORK_BANDWIDTH_PRICES.size)
            computer.networkBandwidth = NETWORK_BANDWIDTH_PRICES[networkBandwidthPricesIndex].hardwareValue
            val cost = CPU_POWER_PRICES[cpuPowerPricesIndex].cost
            +MEMORY_PRICES[memoryPricesIndex].cost
            +NETWORK_BANDWIDTH_PRICES[networkBandwidthPricesIndex].cost
            computer.cost = cost
            logger.trace("Created computer with cpuPowerPricesIndex ({}), memoryPricesIndex ({})," + " networkBandwidthPricesIndex ({}).",
                    cpuPowerPricesIndex, memoryPricesIndex, networkBandwidthPricesIndex)
            computerList.add(computer)
        }
        cloudBalance.computerList = computerList
    }

    private fun distortIndex(referenceIndex: Int, length: Int): Int {
        var index = referenceIndex
        val randomDouble = random.nextDouble()
        var loweringThreshold = 0.25
        while (randomDouble < loweringThreshold && index >= 1) {
            index--
            loweringThreshold *= 0.10
        }
        var heighteningThreshold = 0.75
        while (randomDouble >= heighteningThreshold && index <= length - 2) {
            index++
            heighteningThreshold = 1.0 - (1.0 - heighteningThreshold) * 0.10
        }
        return index
    }

    private fun createProcessList(cloudBalance: CloudBalance, processListSize: Int) {
        val processList = ArrayList<CloudProcess>(processListSize)
        for (i in 0..processListSize - 1) {
            val process = CloudProcess()
            process.id = i.toLong()
            val requiredCpuPower = generateRandom(MAXIMUM_REQUIRED_CPU_POWER)
            process.requiredCpuPower = requiredCpuPower
            val requiredMemory = generateRandom(MAXIMUM_REQUIRED_MEMORY)
            process.requiredMemory = requiredMemory
            val requiredNetworkBandwidth = generateRandom(MAXIMUM_REQUIRED_NETWORK_BANDWIDTH)
            process.requiredNetworkBandwidth = requiredNetworkBandwidth
            logger.trace("Created CloudProcess with requiredCpuPower ({}), requiredMemory ({})," + " requiredNetworkBandwidth ({}).",
                    requiredCpuPower, requiredMemory, requiredNetworkBandwidth)
            // Notice that we leave the PlanningVariable properties on null
            processList.add(process)
        }
        cloudBalance.processList = processList
    }

    private fun generateRandom(maximumValue: Int): Int {
        val randomDouble = random.nextDouble()
        val parabolaBase = 2000.0
        val parabolaRandomDouble = (Math.pow(parabolaBase, randomDouble) - 1.0) / (parabolaBase - 1.0)
        if (parabolaRandomDouble < 0.0 || parabolaRandomDouble >= 1.0) {
            throw IllegalArgumentException("Invalid generated parabolaRandomDouble ($parabolaRandomDouble)")
        }
        val value = Math.floor(parabolaRandomDouble * maximumValue.toDouble()).toInt() + 1
        if (value < 1 || value > maximumValue) {
            throw IllegalArgumentException("Invalid generated value ($value)")
        }
        return value
    }

    private fun assureComputerCapacityTotalAtLeastProcessRequiredTotal(cloudBalance: CloudBalance) {
        val computerList = cloudBalance.computerList
        var cpuPowerTotal = 0
        var memoryTotal = 0
        var networkBandwidthTotal = 0
        for (computer in computerList!!) {
            cpuPowerTotal += computer.cpuPower
            memoryTotal += computer.memory
            networkBandwidthTotal += computer.networkBandwidth
        }
        var requiredCpuPowerTotal = 0
        var requiredMemoryTotal = 0
        var requiredNetworkBandwidthTotal = 0
        for (process in cloudBalance.processList!!) {
            requiredCpuPowerTotal += process.requiredCpuPower
            requiredMemoryTotal += process.requiredMemory
            requiredNetworkBandwidthTotal += process.requiredNetworkBandwidth
        }
        var cpuPowerLacking = requiredCpuPowerTotal - cpuPowerTotal
        while (cpuPowerLacking > 0) {
            val computer = computerList[random.nextInt(computerList.size)]
            val upgrade = determineUpgrade(cpuPowerLacking)
            computer.cpuPower = computer.cpuPower + upgrade
            cpuPowerLacking -= upgrade
        }
        var memoryLacking = requiredMemoryTotal - memoryTotal
        while (memoryLacking > 0) {
            val computer = computerList[random.nextInt(computerList.size)]
            val upgrade = determineUpgrade(memoryLacking)
            computer.memory = computer.memory + upgrade
            memoryLacking -= upgrade
        }
        var networkBandwidthLacking = requiredNetworkBandwidthTotal - networkBandwidthTotal
        while (networkBandwidthLacking > 0) {
            val computer = computerList[random.nextInt(computerList.size)]
            val upgrade = determineUpgrade(networkBandwidthLacking)
            computer.networkBandwidth = computer.networkBandwidth + upgrade
            networkBandwidthLacking -= upgrade
        }
    }

    private fun determineUpgrade(lacking: Int): Int {
        for (upgrade in intArrayOf(8, 4, 2, 1)) {
            if (lacking >= upgrade) {
                return upgrade
            }
        }
        throw IllegalStateException("Lacking ($lacking) should be at least 1.")
    }

    companion object {

        private val CPU_POWER_PRICES = arrayOf(// in gigahertz
                Price(3, "single core 3ghz", 110), Price(4, "dual core 2ghz", 140), Price(6, "dual core 3ghz", 180), Price(8, "quad core 2ghz", 270), Price(12, "quad core 3ghz", 400), Price(16, "quad core 4ghz", 1000), Price(24, "eight core 3ghz", 3000))
        private val MEMORY_PRICES = arrayOf(// in gigabyte RAM
                Price(2, "2 gigabyte", 140), Price(4, "4 gigabyte", 180), Price(8, "8 gigabyte", 220), Price(16, "16 gigabyte", 300), Price(32, "32 gigabyte", 400), Price(64, "64 gigabyte", 600), Price(96, "96 gigabyte", 1000))
        private val NETWORK_BANDWIDTH_PRICES = arrayOf(// in gigabyte per hour
                Price(2, "2 gigabyte", 100), Price(4, "4 gigabyte", 200), Price(6, "6 gigabyte", 300), Price(8, "8 gigabyte", 400), Price(12, "12 gigabyte", 600), Price(16, "16 gigabyte", 800), Price(20, "20 gigabyte", 1000))

        private val MAXIMUM_REQUIRED_CPU_POWER = 12 // in gigahertz
        private val MAXIMUM_REQUIRED_MEMORY = 32 // in gigabyte RAM
        private val MAXIMUM_REQUIRED_NETWORK_BANDWIDTH = 12 // in gigabyte per hour

        @JvmStatic fun main(args: Array<String>) {
            CloudBalancingGenerator().generate()
        }
    }

}
