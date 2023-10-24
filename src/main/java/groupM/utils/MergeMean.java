package groupM.utils;

import org.apache.commons.math3.stat.descriptive.moment.Mean;

public class MergeMean {
    /**
     * Combines multiple mean objects together.
     * Note that after using this util, only the getResults() value will be correct.
     * The other first moment values will be incorrect
     * @param means the means to combine
     * @return the combined means
     */
    public static Mean merge(Mean[] means){
        double total = 0.0;
        long totalCount = 0;
        
        for(Mean mean : means){
            total += mean.getResult() * mean.getN();
            totalCount += mean.getN();
        }

        Mean combinedMean = new Mean();
        combinedMean.increment(total / totalCount);
        return combinedMean;
    }
}
