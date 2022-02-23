/*
 * Copyright 2020-2021 original authors
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
package info.novatec.micronaut.camunda.bpm.feature;

import info.novatec.micronaut.camunda.bpm.feature.initialization.ParallelInitializationService;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.jdbc.BasicJdbcConfiguration;
import io.micronaut.transaction.SynchronousTransactionManager;
import jakarta.inject.Singleton;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @author Tobias Schäfer
 */
@Factory
public class ProcessEngineFactory {

    private static final Logger log = LoggerFactory.getLogger(ProcessEngineFactory.class);

    @Singleton
    @Bean(preDestroy = "close")
    public ProcessEngine processEngine(ProcessEngineConfiguration processEngineConfiguration, CamundaVersion camundaVersion, SynchronousTransactionManager<Connection> transactionManager, BasicJdbcConfiguration basicJdbcConfiguration, ParallelInitializationService parallelInitializationService) {
        if (camundaVersion.getVersion().isPresent()) {
            log.info("Camunda version: {}", camundaVersion.getVersion().get());
        } else {
            log.warn("The Camunda version cannot be determined. If you created a Fat/Uber/Shadow JAR then please consider using the Micronaut Application Plugin's 'dockerBuild' task to create a Docker image.");
        }

        log.info("Building process engine connected to {}", basicJdbcConfiguration.getUrl());
        Instant start = Instant.now();

        ProcessEngine processEngine = transactionManager.executeWrite(
                transactionStatus -> processEngineConfiguration.buildProcessEngine()
        );

        log.info("Started process engine in {}ms", ChronoUnit.MILLIS.between(start, Instant.now()));

        log.debug("Starting Camunda related services...");
        parallelInitializationService.process(processEngine);
        log.debug("Camunda related services started in {}ms", ChronoUnit.MILLIS.between(start, Instant.now()));

        return processEngine;
    }
}
