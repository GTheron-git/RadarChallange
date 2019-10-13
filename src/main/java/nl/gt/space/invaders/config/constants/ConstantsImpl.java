package nl.gt.space.invaders.config.constants;

import lombok.Data;

@Data
public class ConstantsImpl implements Constants {
    String filename;
    float cutoffMatchDensity;
    String invaderSectionIdentifier;
    String radarSectionIdentifier;
    float acceptedMatchRatioForHit;
}
