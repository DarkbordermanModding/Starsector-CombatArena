package src.data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.ShipAPI;

//From Autonomous Ship http://fractalsoftworks.com/forum/index.php?topic=13199.0
public abstract class GladiatorSociety_AbstractPersonalityHullMod extends BaseHullMod {
    private static final String PREFIX = "autonomous_personality_";
    private String personality;

    GladiatorSociety_AbstractPersonalityHullMod(String personality) {
        this.personality = personality;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        for (String hullMod : ship.getVariant().getHullMods()) {
            if (hullMod.startsWith(PREFIX) && !hullMod.equals(PREFIX + personality)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        return "Personality hullmods are mutually exclusive";
    }
}
