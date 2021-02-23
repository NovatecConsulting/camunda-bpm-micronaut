/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package info.novatec.micronaut.camunda.bpm.feature.test

import io.micronaut.context.ApplicationContext
import org.camunda.bpm.engine.ProcessEngine
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * @author Tobias Schäfer
 */
class MnProcessEngineConfigurationJobExecutorTest {

    @ParameterizedTest
    @ValueSource(strings = ["true", "false"])
    fun `job executor is enabled for production but not for tests`(test : Boolean) {
        ApplicationContext.builder().deduceEnvironment(test).build().start().use { applicationContext ->
            val pec = applicationContext.getBean(ProcessEngine::class.java).processEngineConfiguration as ProcessEngineConfigurationImpl
            assertEquals(!test, pec.isJobExecutorActivate)
        }
    }
}
