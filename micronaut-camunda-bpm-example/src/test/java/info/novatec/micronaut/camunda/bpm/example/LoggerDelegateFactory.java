package info.novatec.micronaut.camunda.bpm.example;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Replaces;
import org.mockito.Mockito;

import javax.inject.Named;
import javax.inject.Singleton;

@Factory
public class LoggerDelegateFactory {
    @Singleton
    @Replaces(LoggerDelegate.class)
    @Named("loggerDelegate")
    public LoggerDelegate loggerDelegate() {
        return Mockito.mock(LoggerDelegate.class);
    }
}
