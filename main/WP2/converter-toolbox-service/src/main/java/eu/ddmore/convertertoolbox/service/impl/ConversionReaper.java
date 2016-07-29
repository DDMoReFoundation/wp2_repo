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

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.service.ConversionRepository;


/**
 * Component responsible for cleaning up old Conversions and data
 */
@Component
public class ConversionReaper {

    private static final Logger LOG = Logger.getLogger(ConversionReaper.class);
    
    @Autowired(required=true)
    private ConversionRepository conversionRepository;
    
    @Autowired(required=true)
    private ConversionRemover conversionRemover;
    
    @Value("${cts.conversionResultsAvailabilityTimeout}")
    private long conversionResultsAvailabilityTimeout = TimeUnit.HOURS.toMillis(2);
    
    @Scheduled(fixedRateString = "${cts.cleanupRate:300000}")
    public void performCleanup() {
        Collection<Conversion> forDeletion = conversionRepository.
                getConversionsCompletedEarlierThan(new Date().getTime()-conversionResultsAvailabilityTimeout);
        LOG.info(String.format("There are %s conversion candiates for removal", forDeletion.size()));
        for(Conversion conversion : forDeletion) {
            try {
                conversionRemover.remove(conversion);
            } catch(Exception ex) {
                LOG.error("Error when trying to remove old conversion",ex);
            }
        }
    }

    public void setConversionResultsAvailabilityTimeout(long conversionResultsAvailabilityTimeout) {
        this.conversionResultsAvailabilityTimeout = conversionResultsAvailabilityTimeout;
    }
}
