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

import org.optaplanner.examples.common.app.CommonBenchmarkApp

class CloudBalancingBenchmarkApp : CommonBenchmarkApp(CommonBenchmarkApp.ArgOption("default",
        "org/optaplanner/examples/cloudbalancing/benchmark/cloudBalancingBenchmarkConfig.xml"), CommonBenchmarkApp.ArgOption("stepLimit",
        "org/optaplanner/examples/cloudbalancing/benchmark/cloudBalancingStepLimitBenchmarkConfig.xml"), CommonBenchmarkApp.ArgOption("scoreDirector",
        "org/optaplanner/examples/cloudbalancing/benchmark/cloudBalancingScoreDirectorBenchmarkConfig.xml"), CommonBenchmarkApp.ArgOption("template",
        "org/optaplanner/examples/cloudbalancing/benchmark/cloudBalancingBenchmarkConfigTemplate.xml.ftl", true)) {
    companion object {

        @JvmStatic fun main(args: Array<String>) {
            CloudBalancingBenchmarkApp().buildAndBenchmark(args)
        }
    }

}
