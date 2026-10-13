package de.oliver.fancyvisuals.config;

public class NametagConfig {

    private int distributionBucketSize;

    public NametagConfig() {
        distributionBucketSize = 10;
    }

    public void load() {

    }

    /**
     * Retrieves the size of the distribution bucket configured.
     *
     * @return The size of the distribution bucket.
     */
    public int getDistributionBucketSize() {
        return distributionBucketSize;
    }
}
