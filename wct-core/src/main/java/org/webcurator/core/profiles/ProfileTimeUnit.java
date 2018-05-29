package org.webcurator.core.profiles;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration containing time units.
 */
public enum ProfileTimeUnit {
    SECOND, MINUTE, HOUR, DAY, WEEK;

    public static ProfileTimeUnit DEFAULT = SECOND;

    public static List<String> getProfileDataTimeNames() {
        List<String> names = new ArrayList<String>();
        for (ProfileTimeUnit profileTimeUnit : ProfileTimeUnit.values()) {
            names.add(profileTimeUnit.name());
        }
        return names;
    }

}
