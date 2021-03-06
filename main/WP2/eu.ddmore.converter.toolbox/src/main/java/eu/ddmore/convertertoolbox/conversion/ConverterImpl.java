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
package eu.ddmore.convertertoolbox.conversion;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import eu.ddmore.convertertoolbox.api.conversion.ConversionListener;
import eu.ddmore.convertertoolbox.api.conversion.Converter;
import eu.ddmore.convertertoolbox.api.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.api.domain.Version;
import eu.ddmore.convertertoolbox.api.response.ConversionReport;
import eu.ddmore.convertertoolbox.api.spi.ConverterProvider;
import eu.ddmore.convertertoolbox.concurrent.ConversionCallbackImpl;

/**
 * Represents a converter available in the toolbox
 */
public class ConverterImpl implements Converter {

    private ConverterProvider provider;
    private static ExecutorService executorService;
    private static final int MAX_THREADS_NUMBER = 10;

    /**
     * Instantiates a thread pool that reuses a fixed number of threads
     * operating off a shared unbounded queue. At any point, at most
     * <tt>MAX_THREADS_NUMBER</tt> threads will be active processing tasks.
     * If additional tasks are submitted when all threads are active,
     * they will wait in the queue until a thread is available.
     * {@link java.util.concurrent.Executors#newFixedThreadPool 'Executors.newFixedThreadPool'}
     */
    public ConverterImpl() {
        executorService = Executors.newFixedThreadPool(MAX_THREADS_NUMBER);
    }
    
    /**
     * Sets the converter service provider associated with this converter.
     * @param provider the converter service provider to be associated with this converter.
     */
    public void setProvider(ConverterProvider provider) {
        this.provider = provider;
    }

    @Override
    public ConversionReport convertToFile(File src, File tgtFile) throws IOException {
        return provider.performConvertToFile(src, tgtFile);
    }

    @Override
    public ConversionReport convert(File src, File outputDirectory) throws IOException {
        return provider.performConvert(src, outputDirectory);
    }

    @Override
    public void convert(final File src, final File outputDirectory, final ConversionListener listener) throws IOException {
        ListeningExecutorService service = MoreExecutors.listeningDecorator(executorService);
        
        ListenableFuture<ConversionReport> conversion = service.submit(new Callable<ConversionReport>() {

            public ConversionReport call() throws IOException {
                return convert(src, outputDirectory);
            }
        });
        Futures.addCallback(conversion, new ConversionCallbackImpl(listener));
    }
    
    @Override
    public ConverterProvider getConverterProvider() {
        return provider;
    }

    @Override
    public LanguageVersion getTarget() {
        return getConverterProvider().getTarget();
    }

    @Override
    public LanguageVersion getSource() {
        return getConverterProvider().getSource();
    }

    @Override
    public Version getConverterVersion() {
        return getConverterProvider().getConverterVersion();
    }

    @Override
    public String toString() {
        return String.format("ConverterImpl [provider=%s]", provider);
    }

}
