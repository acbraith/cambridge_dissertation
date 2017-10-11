package simulator;

import learner.Settings;

/**
 * Wrapper object for all the data relating to a single simulation relevent to
 * the Optimise Simulation Run Manager.
 * 
 * @author Alex Braithwaite
 *
 */
public class OptimiseSimulationData implements Comparable<OptimiseSimulationData> {

	public Settings settings;
	public double finalReward;
	public double sumSquareDif;
	public double confidenceInterval;

	/**
	 * Store the data associated with a given Setting s.
	 * 
	 * @param s
	 * @param finalReward
	 * @param sumSquareDif
	 * @param confidenceInterval
	 */
	public OptimiseSimulationData(Settings s, double finalReward, double sumSquareDif, double confidenceInterval) {
		this.settings = s;
		this.finalReward = finalReward;
		this.sumSquareDif = sumSquareDif;
		this.confidenceInterval = confidenceInterval;
	}

	@Override
	public int compareTo(OptimiseSimulationData o) {
		double result = (o.finalReward + o.confidenceInterval / 2) - (this.finalReward + this.confidenceInterval / 2);
		return result < 0 ? -1 : result == 0 ? 0 : 1;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(finalReward);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(confidenceInterval);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(sumSquareDif);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((settings == null) ? 0 : settings.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OptimiseSimulationData other = (OptimiseSimulationData) obj;
		if (Double.doubleToLongBits(finalReward) != Double.doubleToLongBits(other.finalReward))
			return false;
		if (Double.doubleToLongBits(confidenceInterval) != Double.doubleToLongBits(other.confidenceInterval))
			return false;
		if (Double.doubleToLongBits(sumSquareDif) != Double.doubleToLongBits(other.sumSquareDif))
			return false;
		if (settings == null) {
			if (other.settings != null)
				return false;
		} else if (!settings.equals(other.settings))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "OptimiseSimulationData \n\t[settings=" + settings + "\n\tfinalReward=" + finalReward + ", sumSquareDif="
				+ sumSquareDif + ", confidenceInterval=" + confidenceInterval + "]";
	}

}
