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

package org.optaplanner.examples.cloudbalancing.app

import org.optaplanner.core.api.solver.Solver
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalancingGenerator

object CloudBalancingHelloWorld {

    @JvmStatic fun main(args: Array<String>) {
        // Build the Solver
        val solverFactory = SolverFactory.createFromXmlResource<CloudBalance>(
                "org/optaplanner/examples/cloudbalancing/solver/cloudBalancingSolverConfig.xml")
        val solver = solverFactory.buildSolver()

        // Load a problem with 400 computers and 1200 processes
        val unsolvedCloudBalance = CloudBalancingGenerator().createCloudBalance(400, 1200)

        // Solve the problem
        val solvedCloudBalance = solver.solve(unsolvedCloudBalance)

        // Display the result
        println("\nSolved cloudBalance with 400 computers and 1200 processes:\n" + toDisplayString(solvedCloudBalance))
    }

    fun toDisplayString(cloudBalance: CloudBalance): String {
        val displayString = StringBuilder()
        for (process in cloudBalance.processList!!) {
            val computer = process.computer
            displayString.append("  ").append(process.label).append(" -> ").append(computer?.label).append("\n")
        }
        return displayString.toString()
    }

}
