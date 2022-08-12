package mod.combatarena.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;

public class CombatArenaRecord {

    public static final String COMBAT_ARENA_DATA_STORAGE_KEY = "$CombatArenaStorageKey";
    public String opponentFaction = "hegemony";
    public int opponentMinShipSize = 2;
    public int opponentMaxShipSize = 2;
    public float opponentFleetPoint = 100f;
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
        List<FactionAPI> factions = new ArrayList<>();
        for(FactionAPI faction: Global.getSector().getAllFactions()){
            if(faction.isShowInIntelTab() == true){
                factions.add(faction);
            }
        }
        FactionAPI faction = factions.get(random.nextInt(factions.size()));
        setOpponentFaction(faction);
    }

    public int getOpponentMinShipSize(){
        return opponentMinShipSize;
    }
    public void setOpponentMinShipSize(int shipSize){
        opponentMinShipSize = shipSize;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public int getOpponentMaxShipSize(){
        return opponentMaxShipSize;
    }
    public void setOpponentMaxShipSize(int shipSize){
        opponentMaxShipSize = shipSize;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public float getOpponentFleetPoint(){
        return opponentFleetPoint;
    }
    public void setOpponentFleetPoint(float fleetPoint){
        opponentFleetPoint = fleetPoint;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }
    public void increaseOpponentFleetPoint(){
        Random rand = new Random();
        float fleetPoint = getOpponentFleetPoint() + 40 + rand.nextInt(20);
        setOpponentFleetPoint(fleetPoint);
    }

    public float getCreditRewardAmountWithoutArenaToken(CombatArenaRecord record){
        float shipSizeVary = (opponentMaxShipSize + opponentMinShipSize)/2 + (opponentMaxShipSize - opponentMinShipSize)/1.5f;
        float credits = (opponentFleetPoint * shipSizeVary/3) * 1500;
        return (int)credits;
    }
    public float getArenaTokenRewardAmount(CombatArenaRecord record){
        return (int)getCreditRewardAmountWithoutArenaToken(record)/200000;
    }
    public float getCreditRewardAmount(CombatArenaRecord record){
        return (int)getCreditRewardAmountWithoutArenaToken(record)%200000;
    }
}
