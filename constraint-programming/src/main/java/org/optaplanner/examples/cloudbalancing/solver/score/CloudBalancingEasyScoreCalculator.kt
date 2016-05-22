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

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess

class CloudBalancingEasyScoreCalculator : EasyScoreCalculator<CloudBalance> {

    /**
     * A very simple implementation. The double loop can easily be removed by using Maps as shown in
     * [CloudBalancingMapBasedEasyScoreCalculator.calculateScore].
     */
    override fun calculateScore(cloudBalance: CloudBalance): HardSoftScore {
        var hardScore = 0
        var softScore = 0
        for (computer in cloudBalance.computerList!!) {
            var cpuPowerUsage = 0
            var memoryUsage = 0
            var networkBandwidthUsage = 0
            var used = false

            // Calculate usage
            for (process in cloudBalance.processList!!) {
                if (computer == process.computer) {
                    cpuPowerUsage += process.requiredCpuPower
                    memoryUsage += process.requiredMemory
                    networkBandwidthUsage += process.requiredNetworkBandwidth
                    used = true
                }
            }

            // Hard constraints
            val cpuPowerAvailable = computer.cpuPower - cpuPowerUsage
            if (cpuPowerAvailable < 0) {
                hardScore += cpuPowerAvailable
            }
            val memoryAvailable = computer.memory - memoryUsage
            if (memoryAvailable < 0) {
                hardScore += memoryAvailable
            }
            val networkBandwidthAvailable = computer.networkBandwidth - networkBandwidthUsage
            if (networkBandwidthAvailable < 0) {
                hardScore += networkBandwidthAvailable
            }

            // Soft constraints
            if (used) {
                softScore -= computer.cost
            }
        }
        return HardSoftScore.valueOf(hardScore, softScore)
    }

}
