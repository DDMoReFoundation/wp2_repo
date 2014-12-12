/*******************************************************************************
 * Copyright (C) 2002 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.convertertoolbox.service.impl.conversion;



/**
 * Represents a step performed by { @link ConversionTask }
 */
public interface ConversionStep {
    /**
     * Performs business logic 
     * @param conversion
     */
    void execute(ConversionContext conversionContext);
}