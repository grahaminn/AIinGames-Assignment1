package groupM.players.mcts;

import java.util.Random;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.util.FastMath;
import org.json.simple.JSONObject;

import umontreal.ssj.probdist.GammaDist;
import umontreal.ssj.probdist.NormalDist;

class NormalGammaDistribution{
    private double alpha;
    private double beta;
    private Mean mean;
    private int nVisits;

    public NormalGammaDistribution(){
        this.alpha = 0.0;
        this.beta = 0.0;
        this.nVisits = 0;
        this.mean = new Mean();
    }
    
    protected void observeSample(double result) {
        double n = 1;
        double v = this.nVisits;

        double mu = this.mean.getResult();
        if (Double.isNaN(mu)){
            mu = 0;
        }
        
        this.alpha += n / 2;
        this.beta += (n*v/(v+n)) * (FastMath.pow(result - mu, 2) / 2);
        this.nVisits +=1;
        this.mean.increment(result);
    }

    protected double sample(Random rnd) {
        if(this.alpha == 0.0){
            return 0;
        }
        
        // sample precision from gamme dist using inverse sampling trick
        double u = rnd.nextDouble();
        double precision = GammaDist.inverseF(this.alpha, this.beta, 5, u);
        
        if(Double.isNaN(precision) || Double.isInfinite(precision)|| precision == 0 || nVisits == 0){
            precision = 0.001;
        }
        
        // sample value from normal dist using inverse sampling trick
        u = rnd.nextDouble();
        double value = NormalDist.inverseF(this.mean.getResult(), Math.sqrt(1/precision), u);
        return value;
    }
    
    protected double getMean(){
        return this.mean.getResult();
    }

    protected int getNVisits(){
        return this.nVisits;
    }

    /**
     * This function 'merges' two distributions.
     * Note that this doesnt actually merge the distributions, it simply calculates the means of the two distribution means
     * @param other the other distribution to merge
     */
    protected void merge(NormalGammaDistribution other){
        this.nVisits += other.nVisits;

        Mean mergedMean = new Mean();
        mergedMean.increment(this.mean.getResult());
        mergedMean.increment(other.mean.getResult());
        this.mean = mergedMean;
    }

    protected JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("nVisits", this.nVisits);
        json.put("alpha", this.alpha);
        json.put("beta", this.beta);
        json.put("mean", this.mean.getResult());
        return json;
    }
}