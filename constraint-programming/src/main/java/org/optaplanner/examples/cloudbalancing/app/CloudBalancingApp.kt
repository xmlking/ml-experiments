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

package org.optaplanner.examples.cloudbalancing.app

import org.optaplanner.core.api.solver.Solver
import org.optaplanner.core.api.solver.SolverFactory
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance
import org.optaplanner.examples.cloudbalancing.persistence.CloudBalancingDao
import org.optaplanner.examples.cloudbalancing.swingui.CloudBalancingPanel
import org.optaplanner.examples.common.app.CommonApp
import org.optaplanner.examples.common.persistence.SolutionDao
import org.optaplanner.examples.common.swingui.SolutionPanel

/**
 * For an easy example, look at [CloudBalancingHelloWorld] instead.
 */
class CloudBalancingApp : CommonApp<CloudBalance>("Cloud balancing",
        "Assign processes to computers.\n\n" +
                "Each computer must have enough hardware to run all of its processes.\n" +
                "Each used computer inflicts a maintenance cost.", CloudBalancingApp.SOLVER_CONFIG, CloudBalancingPanel.LOGO_PATH) {

    override fun createSolver(): Solver<CloudBalance> {
        val solverFactory = SolverFactory.createFromXmlResource<CloudBalance>(SOLVER_CONFIG)
        return solverFactory.buildSolver()
    }

    override fun createSolutionPanel(): SolutionPanel {
        return CloudBalancingPanel()
    }

    override fun createSolutionDao(): SolutionDao {
        return CloudBalancingDao()
    }

    companion object {

        val SOLVER_CONFIG = "org/optaplanner/examples/cloudbalancing/solver/cloudBalancingSolverConfig.xml"

        @JvmStatic fun main(args: Array<String>) {
            CommonApp.prepareSwingEnvironment()
            CloudBalancingApp().init()
        }
    }

}
