package cl.architeq.displayParking.service;

import cl.architeq.displayParking.gpio.GpioControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;


@Service
public class GpioService implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private ApplicationContext appContext;

    private static final Logger _logger = LoggerFactory.getLogger(GpioService.class);

    private GpioControl gpioControl;

    public void executeAsync() {

        this.gpioControl = appContext.getBean(GpioControl.class);
        this.taskExecutor.execute(this.gpioControl);

    }


    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        _logger.info("context closed event in GPIO Service .. ");
    }
}
