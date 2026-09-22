package com.b2cmall.shop.config;
import com.b2cmall.shop.service.event.handler.*;
import com.google.common.eventbus.*;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
@Configuration
public class EventBusConfiguration {
    @Bean
    public ThreadPoolTaskExecutor registrationExecutor() {
        ThreadPoolTaskExecutor executor=new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);executor.setMaxPoolSize(2);executor.setQueueCapacity(256);
        executor.setThreadNamePrefix("registration-");
        executor.setWaitForTasksToCompleteOnShutdown(true);executor.setAwaitTerminationSeconds(15);
        return executor;
    }
    @Bean
    public EventBus registrationEventBus(ThreadPoolTaskExecutor registrationExecutor,
            InitEmployeeEventHandle employee,InitShopMessageEventHandle message) {
        EventBus bus=new AsyncEventBus(registrationExecutor,(exception,context)->
            org.slf4j.LoggerFactory.getLogger(EventBusConfiguration.class).warn("Registration subscriber {} failed; retry with same registration request",context.getSubscriber().getClass().getSimpleName()));
        bus.register(employee);bus.register(message);
        return bus;
    }
}
