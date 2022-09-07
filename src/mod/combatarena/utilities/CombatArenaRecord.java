package mod.combatarena.utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;

public class CombatArenaRecord {

    public static final String COMBAT_ARENA_DATA_STORAGE_KEY = "$CombatArenaStorageKey";
    public String opponentMode = "fleetparam";
    public String opponentFaction = "hegemony";
    // Used by fleet params
    public int opponentMinShipSize = 2;
    public int opponentMaxShipSize = 2;
    public float opponentFleetPoint = 60f;
    public boolean opponentWarship = true;
    public boolean opponentCarrier = true;
    public boolean opponentPhaser = true;
    public boolean opponentFreighter = false;
    public boolean opponentTanker = false;
    public boolean opponentLiner = false;
    public boolean opponentTransport = false;
    public boolean opponentUtilities = false;
    // used by fleetpoints
    public int opponentQuantile = 1;

    //public static final String opponentCombatOption;

    public String getOpponentMode(){
        return opponentMode;
    }
    public void setOpponentMode(String mode){
        opponentMode = mode;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public int getOpponentQuantile(){
        return opponentQuantile;
    }
    public void setOpponentQuantile(int quantile){
        opponentQuantile = quantile;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

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

        float shipTypeWeight = 0f;
        if(opponentWarship) shipTypeWeight += 1.1f;
        if(opponentCarrier) shipTypeWeight += 1.0f;
        if(opponentPhaser) shipTypeWeight += 1.2f;
        if(opponentFreighter) shipTypeWeight += 0.1f;
        if(opponentTanker) shipTypeWeight += 0.1f;
        if(opponentLiner) shipTypeWeight += 0.1f;
        if(opponentTransport) shipTypeWeight += 0.1f;
        if(opponentUtilities) shipTypeWeight += 0.1f;
        if(shipTypeWeight == 0f) shipTypeWeight = 1.1f;

        float credits = (
            opponentFleetPoint *
            shipSizeVary/3 *
            shipTypeWeight/record.getDistributionDenominator()
        ) * 1500;
        return (int)credits;
    }
    public float getArenaTokenRewardAmount(CombatArenaRecord record){
        return (int)getCreditRewardAmountWithoutArenaToken(record)/200000;
    }
    public float getCreditRewardAmount(CombatArenaRecord record){
        return (int)getCreditRewardAmountWithoutArenaToken(record)%200000;
    }

    public boolean getOpponentWarship(){
        return opponentWarship;
    }
    public void setOpponentWarship(boolean useWarship){
        opponentWarship = useWarship;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public boolean getOpponentCarrier(){
        return opponentCarrier;
    }
    public void setOpponentCarrier(boolean useCarrier){
        opponentCarrier = useCarrier;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public boolean getOpponentPhaser(){
        return opponentPhaser;
    }
    public void setOpponentPhaser(boolean usePhaser){
        opponentPhaser = usePhaser;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public boolean getOpponentFreighter(){
        return opponentFreighter;
    }
    public void setOpponentFreighter(boolean useFreighter){
        opponentFreighter = useFreighter;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public boolean getOpponentTanker(){
        return opponentTanker;
    }
    public void setOpponentTanker(boolean useTanker){
        opponentTanker = useTanker;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public boolean getOpponentLiner(){
        return opponentLiner;
    }
    public void setOpponentLiner(boolean useLiner){
        opponentLiner = useLiner;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public boolean getOpponentTransport(){
        return opponentTransport;
    }
    public void setOpponentTransport(boolean useTransport){
        opponentTransport = useTransport;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public boolean getOpponentUtilities(){
        return opponentUtilities;
    }
    public void setOpponentUtilities(boolean useUtilities){
        opponentUtilities = useUtilities;
        Global.getSector().getPersistentData().put(COMBAT_ARENA_DATA_STORAGE_KEY, this);
    }

    public int getDistributionDenominator(){
        int denominator = 0;
        if(opponentWarship) denominator++;
        if(opponentCarrier) denominator++;
        if(opponentPhaser) denominator++;
        if(opponentFreighter) denominator++;
        if(opponentTanker) denominator++;
        if(opponentLiner) denominator++;
        if(opponentTransport) denominator++;
        if(opponentUtilities) denominator++;
        if(denominator == 0) return 1;
        return denominator;
    }
}
