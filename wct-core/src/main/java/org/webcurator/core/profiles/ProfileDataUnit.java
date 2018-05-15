package org.webcurator.core.profiles;

import java.util.ArrayList;
import java.util.List;

/**
 * Enumeration containing data units.
 */
public enum ProfileDataUnit {
    B, KB, MB, GB;

    public static ProfileDataUnit DEFAULT = B;

    public static List<String> getProfileDataUnitNames() {
        List<String> names = new ArrayList<String>();
        for (ProfileDataUnit profileDataUnit : ProfileDataUnit.values()) {
            names.add(profileDataUnit.name());
        }
        return names;
    }

}
