package mod.combatarena.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class CombatArenaRecord {

    public static final String COMBAT_ARENA_PREFIX = "CombatArena";
    public static final String OPPONENT_FACTION = COMBAT_ARENA_PREFIX + "OpponentFaction";
    public static final String OPPONENT_HULLSIZE = COMBAT_ARENA_PREFIX + "OpponentHullsize";
    //public static final String opponentCombatOption;

    public String getOpponentFaction(String override){
        if(override != null) return override;
        if(Global.getSettings().getString(OPPONENT_FACTION) == null) return "hegemony";
        return Global.getSettings().getString(OPPONENT_FACTION);
    }

    public HullSize getOpponentHullsize(HullSize override){
        if(override != null) return override;
        if (Global.getSettings().getFloat(OPPONENT_HULLSIZE) == 0f) return HullSize.DESTROYER;
        return HullSize.values()[(int)Global.getSettings().getFloat(OPPONENT_HULLSIZE)];
    }
}
