package mod.combatarena.utilities;

import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;

public class CombatArenaRecord {

    public String opponentFaction = "hegemony";
    public int opponentShipSize = 2;
    public float opponentFleetPoint = 100f;
    public static final String COMBAT_ARENA_DATA_STORAGE_KEY = "$CombatArenaStorageKey";
    //public static final String opponentCombatOption;

    public FactionAPI getOpponentFaction(){
        return Global.getSector().getFaction(opponentFaction);
    }
    public void setOpponentFaction(FactionAPI faction){
        opponentFaction = faction.getId();
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }
    public void randomizeOpponentFaction(){
        Random random = new Random();
        FactionAPI faction = Global.getSector().getAllFactions().get(
            random.nextInt(Global.getSector().getAllFactions().size())
        );
        setOpponentFaction(faction);
    }

    public int getOpponentShipSize(int override){
        return opponentShipSize;
    }
    public void setOpponentShipSize(int shipSize){
        opponentShipSize = shipSize;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public float getOpponentFleetPoint(){
        return opponentFleetPoint;
    }
    public void setOpponentFleetPoint(float fleetPoint){
        opponentFleetPoint = fleetPoint;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public float getCreditRewardAmountWithoutArenaToken(CombatArenaRecord record){
        float credits = opponentFleetPoint * 1000;
        return (int)credits;
    }
    public float getArenaTokenRewardAmount(CombatArenaRecord record){
        return (int)getCreditRewardAmountWithoutArenaToken(record)/250000;
    }
    public float getCreditRewardAmount(CombatArenaRecord record){
        return (int)getCreditRewardAmountWithoutArenaToken(record)%250000;
    }
}
