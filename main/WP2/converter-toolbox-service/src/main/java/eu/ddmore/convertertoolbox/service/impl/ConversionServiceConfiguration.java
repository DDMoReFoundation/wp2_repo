/*******************************************************************************
 * Copyright (C) 2016 Mango Business Solutions Ltd, http://www.mango-solutions.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/agpl-3.0.html>.
 *******************************************************************************/
package eu.ddmore.convertertoolbox.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.google.common.base.Preconditions;


/**
 * Holds context configuration for Conversion Service beans
 */
@Configuration
public class ConversionServiceConfiguration {
    
    @Bean
    public TaskExecutor conversionTaskExecutor(@Value("${cts.serviceCapacity}") int capacity,
            @Value("${cts.parallelConversions}") int threadPool) {
        Preconditions.checkArgument(capacity>threadPool, "service capacity is smaller than thread pool, it doesn't make sense. Please fix application configuration.");
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setDaemon(true);
        taskExecutor.setMaxPoolSize(threadPool);
        taskExecutor.setCorePoolSize(threadPool);
        taskExecutor.setQueueCapacity(capacity-threadPool);
        taskExecutor.initialize();
        return taskExecutor;
    }
}
