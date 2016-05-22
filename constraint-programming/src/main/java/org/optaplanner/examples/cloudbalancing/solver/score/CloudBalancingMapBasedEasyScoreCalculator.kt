/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.cloudbalancing.solver.score

import java.util.HashMap
import java.util.HashSet

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess

class CloudBalancingMapBasedEasyScoreCalculator : EasyScoreCalculator<CloudBalance> {

    override fun calculateScore(cloudBalance: CloudBalance): HardSoftScore {
        val computerListSize = cloudBalance.computerList!!.size
        val cpuPowerUsageMap = HashMap<CloudComputer, Int>(computerListSize)
        val memoryUsageMap = HashMap<CloudComputer, Int>(computerListSize)
        val networkBandwidthUsageMap = HashMap<CloudComputer, Int>(computerListSize)
        for (computer in cloudBalance.computerList!!) {
            cpuPowerUsageMap.put(computer, 0)
            memoryUsageMap.put(computer, 0)
            networkBandwidthUsageMap.put(computer, 0)
        }
        val usedComputerSet = HashSet<CloudComputer>(computerListSize)

        visitProcessList(cpuPowerUsageMap, memoryUsageMap, networkBandwidthUsageMap,
                usedComputerSet, cloudBalance.processList!!)

        val hardScore = sumHardScore(cpuPowerUsageMap, memoryUsageMap, networkBandwidthUsageMap)
        val softScore = sumSoftScore(usedComputerSet)

        return HardSoftScore.valueOf(hardScore, softScore)
    }

    private fun visitProcessList(cpuPowerUsageMap: MutableMap<CloudComputer, Int>,
                                 memoryUsageMap: MutableMap<CloudComputer, Int>, networkBandwidthUsageMap: MutableMap<CloudComputer, Int>,
                                 usedComputerSet: MutableSet<CloudComputer>, processList: List<CloudProcess>) {
        // We loop through the processList only once for performance
        for (process in processList) {
            val computer = process.computer
            if (computer != null) {
                val cpuPowerUsage = cpuPowerUsageMap[computer]!! + process.requiredCpuPower
                cpuPowerUsageMap.put(computer, cpuPowerUsage)
                val memoryUsage = memoryUsageMap[computer]!! + process.requiredMemory
                memoryUsageMap.put(computer, memoryUsage)
                val networkBandwidthUsage = networkBandwidthUsageMap[computer]!! + process.requiredNetworkBandwidth
                networkBandwidthUsageMap.put(computer, networkBandwidthUsage)
                usedComputerSet.add(computer)
            }
        }
    }

    private fun sumHardScore(cpuPowerUsageMap: Map<CloudComputer, Int>, memoryUsageMap: Map<CloudComputer, Int>,
                             networkBandwidthUsageMap: Map<CloudComputer, Int>): Int {
        var hardScore = 0
        for (usageEntry in cpuPowerUsageMap.entries) {
            val computer = usageEntry.key
            val cpuPowerAvailable = computer.cpuPower - usageEntry.value
            if (cpuPowerAvailable < 0) {
                hardScore += cpuPowerAvailable
            }
        }
        for (usageEntry in memoryUsageMap.entries) {
            val computer = usageEntry.key
            val memoryAvailable = computer.memory - usageEntry.value
            if (memoryAvailable < 0) {
                hardScore += memoryAvailable
            }
        }
        for (usageEntry in networkBandwidthUsageMap.entries) {
            val computer = usageEntry.key
            val networkBandwidthAvailable = computer.networkBandwidth - usageEntry.value
            if (networkBandwidthAvailable < 0) {
                hardScore += networkBandwidthAvailable
            }
        }
        return hardScore
    }

    private fun sumSoftScore(usedComputerSet: Set<CloudComputer>): Int {
        var softScore = 0
        for (usedComputer in usedComputerSet) {
            softScore -= usedComputer.cost
        }
        return softScore
    }

}
