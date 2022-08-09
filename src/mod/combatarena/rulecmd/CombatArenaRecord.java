package mod.combatarena.rulecmd;

import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class CombatArenaRecord {

    public static final String COMBAT_ARENA_PREFIX = "CombatArena";
    public static final String OPPONENT_FACTION = COMBAT_ARENA_PREFIX + "OpponentFaction";
    public static final String OPPONENT_HULLSIZE = COMBAT_ARENA_PREFIX + "OpponentHullsize";
    //public static final String opponentCombatOption;

    public FactionAPI getOpponentFaction(){
        int index = (int)Global.getSettings().getFloat(OPPONENT_FACTION);
        if(index == -1) return Global.getSector().getFaction("combat_arena");
        return Global.getSector().getAllFactions().get(index);
    }
    public void setOpponentFaction(FactionAPI faction){
        if(faction.getId().equals("combat_arena")){
            Global.getSettings().setFloat(OPPONENT_FACTION, -1f);
        }else{
            int index = Global.getSector().getAllFactions().indexOf(faction);
            Global.getSettings().setFloat(OPPONENT_FACTION, (float)index);
        }
    }
    public void randomizeOpponentFaction(){
        Random random = new Random();
        FactionAPI faction = Global.getSector().getAllFactions().get(
            random.nextInt(Global.getSector().getAllFactions().size())
        );
        setOpponentFaction(faction);
    }

    public HullSize getOpponentHullsize(HullSize override){
        if(override != null) return override;
        if (Global.getSettings().getFloat(OPPONENT_HULLSIZE) == 0f) return HullSize.DESTROYER;
        return HullSize.values()[(int)Global.getSettings().getFloat(OPPONENT_HULLSIZE)];
    }
}
