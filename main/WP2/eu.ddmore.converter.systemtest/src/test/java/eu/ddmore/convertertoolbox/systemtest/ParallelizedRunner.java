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
package eu.ddmore.convertertoolbox.systemtest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;


/**
 * A JUnit Runner which performs {@link Parameterized} tests run in parallel.
 */
public class ParallelizedRunner extends Parameterized {
        private static class RunnerSchedulerWithThreadPool implements RunnerScheduler
        {
            private ExecutorService executor; 
            private long shutdownTimeout;
            
            public RunnerSchedulerWithThreadPool(int numThreads, long shutdownTimeout)
            {
                executor = Executors.newFixedThreadPool(numThreads);
                this.shutdownTimeout = shutdownTimeout;
            }
            
            @Override
            public void finished()
            {
                executor.shutdown();
                try
                {
                    executor.awaitTermination(shutdownTimeout, TimeUnit.MILLISECONDS);
                }
                catch (InterruptedException exc)
                {
                    throw new RuntimeException(exc);
                }
            }

            @Override
            public void schedule(Runnable childStatement)
            {
                executor.submit(childStatement);
            }
        }

        public ParallelizedRunner(Class<?> klass) throws Throwable
        {
            super(klass);
            int numThreads = 4;
            long shutdownTimeout = TimeUnit.MINUTES.toMillis(10);
            if(klass.isAnnotationPresent(Workers.class)) {
                Workers annotation = klass.getAnnotation(Workers.class);
                numThreads = annotation.value();
            }
            setScheduler(new RunnerSchedulerWithThreadPool(numThreads, shutdownTimeout));
        }
}
