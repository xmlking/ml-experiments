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

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.impl.score.director.incremental.AbstractIncrementalScoreCalculator
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess

class CloudBalancingIncrementalScoreCalculator : AbstractIncrementalScoreCalculator<CloudBalance>() {

    private var cpuPowerUsageMap: MutableMap<CloudComputer, Int>? = null
    private var memoryUsageMap: MutableMap<CloudComputer, Int>? = null
    private var networkBandwidthUsageMap: MutableMap<CloudComputer, Int>? = null
    private var processCountMap: MutableMap<CloudComputer, Int>? = null

    private var hardScore: Int = 0
    private var softScore: Int = 0

    override fun resetWorkingSolution(cloudBalance: CloudBalance) {
        val computerListSize = cloudBalance.computerList!!.size
        cpuPowerUsageMap = HashMap<CloudComputer, Int>(computerListSize)
        memoryUsageMap = HashMap<CloudComputer, Int>(computerListSize)
        networkBandwidthUsageMap = HashMap<CloudComputer, Int>(computerListSize)
        processCountMap = HashMap<CloudComputer, Int>(computerListSize)
        for (computer in cloudBalance.computerList!!) {
            cpuPowerUsageMap!!.put(computer, 0)
            memoryUsageMap!!.put(computer, 0)
            networkBandwidthUsageMap!!.put(computer, 0)
            processCountMap!!.put(computer, 0)
        }
        hardScore = 0
        softScore = 0
        for (process in cloudBalance.processList!!) {
            insert(process)
        }
    }

    override fun beforeEntityAdded(entity: Any) {
        // Do nothing
    }

    override fun afterEntityAdded(entity: Any) {
        // TODO the maps should probably be adjusted
        insert(entity as CloudProcess)
    }

    override fun beforeVariableChanged(entity: Any, variableName: String) {
        retract(entity as CloudProcess)
    }

    override fun afterVariableChanged(entity: Any, variableName: String) {
        insert(entity as CloudProcess)
    }

    override fun beforeEntityRemoved(entity: Any) {
        retract(entity as CloudProcess)
    }

    override fun afterEntityRemoved(entity: Any) {
        // Do nothing
        // TODO the maps should probably be adjusted
    }

    private fun insert(process: CloudProcess) {
        val computer = process.computer
        if (computer != null) {
            val cpuPower = computer.cpuPower
            val oldCpuPowerUsage = cpuPowerUsageMap!![computer]
            val oldCpuPowerAvailable = cpuPower - oldCpuPowerUsage!!
            val newCpuPowerUsage = oldCpuPowerUsage + process.requiredCpuPower
            val newCpuPowerAvailable = cpuPower - newCpuPowerUsage
            hardScore += Math.min(newCpuPowerAvailable, 0) - Math.min(oldCpuPowerAvailable, 0)
            cpuPowerUsageMap!!.put(computer, newCpuPowerUsage)

            val memory = computer.memory
            val oldMemoryUsage = memoryUsageMap!![computer]
            val oldMemoryAvailable = memory - oldMemoryUsage!!
            val newMemoryUsage = oldMemoryUsage + process.requiredMemory
            val newMemoryAvailable = memory - newMemoryUsage
            hardScore += Math.min(newMemoryAvailable, 0) - Math.min(oldMemoryAvailable, 0)
            memoryUsageMap!!.put(computer, newMemoryUsage)

            val networkBandwidth = computer.networkBandwidth
            val oldNetworkBandwidthUsage = networkBandwidthUsageMap!![computer]
            val oldNetworkBandwidthAvailable = networkBandwidth - oldNetworkBandwidthUsage!!
            val newNetworkBandwidthUsage = oldNetworkBandwidthUsage + process.requiredNetworkBandwidth
            val newNetworkBandwidthAvailable = networkBandwidth - newNetworkBandwidthUsage
            hardScore += Math.min(newNetworkBandwidthAvailable, 0) - Math.min(oldNetworkBandwidthAvailable, 0)
            networkBandwidthUsageMap!!.put(computer, newNetworkBandwidthUsage)

            val oldProcessCount = processCountMap!![computer]
            if (oldProcessCount == 0) {
                softScore -= computer.cost
            }
            val newProcessCount = oldProcessCount!! + 1
            processCountMap!!.put(computer, newProcessCount)
        }
    }

    private fun retract(process: CloudProcess) {
        val computer = process.computer
        if (computer != null) {
            val cpuPower = computer.cpuPower
            val oldCpuPowerUsage = cpuPowerUsageMap!![computer]
            val oldCpuPowerAvailable = cpuPower - oldCpuPowerUsage!!
            val newCpuPowerUsage = oldCpuPowerUsage - process.requiredCpuPower
            val newCpuPowerAvailable = cpuPower - newCpuPowerUsage
            hardScore += Math.min(newCpuPowerAvailable, 0) - Math.min(oldCpuPowerAvailable, 0)
            cpuPowerUsageMap!!.put(computer, newCpuPowerUsage)

            val memory = computer.memory
            val oldMemoryUsage = memoryUsageMap!![computer]
            val oldMemoryAvailable = memory - oldMemoryUsage!!
            val newMemoryUsage = oldMemoryUsage - process.requiredMemory
            val newMemoryAvailable = memory - newMemoryUsage
            hardScore += Math.min(newMemoryAvailable, 0) - Math.min(oldMemoryAvailable, 0)
            memoryUsageMap!!.put(computer, newMemoryUsage)

            val networkBandwidth = computer.networkBandwidth
            val oldNetworkBandwidthUsage = networkBandwidthUsageMap!![computer]
            val oldNetworkBandwidthAvailable = networkBandwidth - oldNetworkBandwidthUsage!!
            val newNetworkBandwidthUsage = oldNetworkBandwidthUsage - process.requiredNetworkBandwidth
            val newNetworkBandwidthAvailable = networkBandwidth - newNetworkBandwidthUsage
            hardScore += Math.min(newNetworkBandwidthAvailable, 0) - Math.min(oldNetworkBandwidthAvailable, 0)
            networkBandwidthUsageMap!!.put(computer, newNetworkBandwidthUsage)

            val oldProcessCount = processCountMap!![computer]
            val newProcessCount = oldProcessCount!! - 1
            if (newProcessCount == 0) {
                softScore += computer.cost
            }
            processCountMap!!.put(computer, newProcessCount)
        }
    }

    override fun calculateScore(): HardSoftScore {
        return HardSoftScore.valueOf(hardScore, softScore)
    }

}
