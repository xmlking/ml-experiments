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

package org.optaplanner.examples.cloudbalancing.solver.move

import java.util.Collections

import org.apache.commons.lang3.ObjectUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.optaplanner.core.impl.heuristic.move.AbstractMove
import org.optaplanner.core.impl.heuristic.move.Move
import org.optaplanner.core.impl.score.director.ScoreDirector
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess

class CloudComputerChangeMove(private val cloudProcess: CloudProcess, private val toCloudComputer: CloudComputer) : AbstractMove() {

    override fun isMoveDoable(scoreDirector: ScoreDirector): Boolean {
        return !ObjectUtils.equals(cloudProcess.computer, toCloudComputer)
    }

    override fun createUndoMove(scoreDirector: ScoreDirector): Move {
        return CloudComputerChangeMove(cloudProcess, cloudProcess.computer!!)
    }

    override fun doMoveOnGenuineVariables(scoreDirector: ScoreDirector) {
        CloudBalancingMoveHelper.moveCloudComputer(scoreDirector, cloudProcess, toCloudComputer)
    }

    override fun getPlanningEntities(): Collection<Any> {
        return listOf(cloudProcess)
    }

    override fun getPlanningValues(): Collection<Any> {
        return listOf(toCloudComputer)
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) {
            return true
        } else if (o is CloudComputerChangeMove) {
            return EqualsBuilder().append(cloudProcess, o.cloudProcess).append(toCloudComputer, o.toCloudComputer).isEquals
        } else {
            return false
        }
    }

    override fun hashCode(): Int {
        return HashCodeBuilder().append(cloudProcess).append(toCloudComputer).toHashCode()
    }

    override fun toString(): String {
        return "{ $cloudProcess $cloudProcess.computer  -> $toCloudComputer }"
    }

}
