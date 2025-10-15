package de.oliver.deployment.hangar;

import java.util.List;
import java.util.Map;

public record HangarVersionUpload(
        String version,
        String channel,
        String description,
        Map<String, List<String>> platformDependencies
) {
    
}
