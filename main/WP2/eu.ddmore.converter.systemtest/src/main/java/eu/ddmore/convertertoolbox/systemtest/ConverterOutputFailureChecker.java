package eu.ddmore.convertertoolbox.systemtest;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


/**
 * Class to heuristically and generically check the output from a converter execution,
 * i.e. the generated model file, the standard output file and the standard error file,
 * to attempt to determine if the conversion process failed.
 * <p>
 * At the very least, this checks that,
 * <ol>
 * <li>the expected generated model file exists
 * <li>the expected generated model file is larger than a specified threshhold size (number of bytes)
 * <li>the standard error output is empty
 * </ol>
 * <p>
 * <b>Note that even if this checking determines that the process didn't fail,
 * it doesn't mean that the conversion was actually successful, since garbage
 * could have been written out to the generated model file with no errors
 * produced, and this wouldn't know any different!</b>
 * <p>
 * Subclasses could extend this class to provide additional checks.
 */
public class ConverterOutputFailureChecker {
    Logger LOGGER = Logger.getLogger(ConverterOutputFailureChecker.class);

    // We'll consider a conversion to have failed if the converted output file has a size that is less than this number of bytes
    private final int outputFileSizeThreshhold;
    
    /**
     * Constructor providing suitable parameters to check against.
     * <p>
     * @param outputFileSizeThreshhold - We'll consider a conversion to have failed if the converted 
     *                                   output file has a size that is less than this number of bytes
     */
    ConverterOutputFailureChecker(final int outputFileSizeThreshhold) {
        this.outputFileSizeThreshhold = outputFileSizeThreshhold;
    }
    
    /**
     * Heuristically check if the conversion did not fail.
     * <p>
     * @param expectedOutputFile - the model {@link File} that should be created from the conversion process
     * @param stdoutFile - {@link File} to which the stdout was piped
     * @param stderrFile - {@link File} to which the stderr was piped
     * @return
     */
    boolean isConversionErrorFree(final File expectedOutputFile, final File stdoutFile, final File stderrFile) {
        if (! expectedOutputFile.exists()) {
            LOGGER.error("Model file wasn't created: " + expectedOutputFile);
            return false;
        }
        if (FileUtils.sizeOf(expectedOutputFile) < this.outputFileSizeThreshhold) {
            LOGGER.error("Expected created model file too small to be valid: " + expectedOutputFile);
            return false;
        }
        if (FileUtils.sizeOf(stderrFile) > 0) {
            // Errors were produced onto the standard error stream
            LOGGER.error("Standard error output not empty after creation of model file: " + expectedOutputFile);
            return false;
        }
        return true;
    }
    
}
