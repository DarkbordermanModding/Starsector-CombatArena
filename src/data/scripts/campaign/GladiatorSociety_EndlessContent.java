package src.data.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import src.data.scripts.campaign.dataclass.*;

public class GladiatorSociety_EndlessContent {

    // public static final String GSTAG = "$GladiatorSociety_ER";
    private int endlessPower;
    private int endlessRound;
    private String nextFaction;
    private FactionAPI currentFaction;

    public GladiatorSociety_EndlessContent() {
        endlessPower = 100;
        endlessRound = 0;
        setRandomFaction();
    }
    private final List<GladiatorSociety_EndlessReward> currentRewardList = new ArrayList<>();
    private final Set<String> rewardTaken = new HashSet<>();

    public int getEndlessPower() {
        return (int)(endlessPower*1.5f);
    }

    private void addEndlessPower() {
        if (endlessPower > 400) {
            endlessPower += (endlessPower * 0.05f);
        } else {
            endlessPower += (endlessPower * 0.1f + 10);
        }
    }

    public int getEndlessRound() {
        return endlessRound;
    }

    public void setEndlessRound(int value) {
        this.endlessRound = value;
    }

    public FactionAPI shuffleFaction(){
        WeightedRandomPicker<FactionAPI> picker = new WeightedRandomPicker<>();
        
        for (FactionAPI faction: Global.getSector().getAllFactions()){
            picker.add(faction, 1);
        }
        if (picker.isEmpty()) {
            return Global.getSector().getFaction(Factions.PIRATES);
        }
        while(true){
            currentFaction = picker.pick();
            if(currentFaction != null){
                break;
            }
        }
        return currentFaction;
    }

    public FactionAPI getEndlessFaction() {
        while(currentFaction == null){
            currentFaction = Global.getSector().getFaction(Factions.PIRATES);
        }
        return currentFaction;
    }

    public void setEndlessFaction(FactionAPI faction){
        currentFaction = faction;
    }

    public void incEndlessRound() {
        endlessRound++;
        addEndlessPower();
        setRandomFaction();
    }
    public void resetEndless(){
        endlessRound = 1;
        endlessPower = 100;
        setRandomFaction();
    }

    private void setRandomFaction() {
        WeightedRandomPicker<String> picker = new WeightedRandomPicker<>();
        Set<String> endlessfaction = GladiatorSociety_JSONBountyRead.getAllEndlessFactionCopy();
        
        for (FactionAPI faction: Global.getSector().getAllFactions()){
            picker.add(faction.getId(), 1);
        }
        if (picker.isEmpty()) {
            nextFaction = Factions.PIRATES;
            return;
        }
        nextFaction = picker.pick();
        if (nextFaction == null) {
            nextFaction = Factions.PIRATES;
        }
    }

    public int getEndlessReward() {
        return (int) (4.5 * Math.pow(endlessPower, 2)) + endlessPower * 1000;
    }

    public boolean canHaveReward() {

        updateAvailableReward();

        return !currentRewardList.isEmpty() && Global.getSector().getPlayerFleet().getFleetData().getNumMembers() < Global.getSettings().getMaxShipsInFleet();
    }

    public void updateAvailableReward() {
        List<GladiatorSociety_EndlessReward> list = GladiatorSociety_JSONBountyRead.getAllEndlessRewardCopy();
        currentRewardList.clear();
        int max = 4;
        int inc = 0;
        for (GladiatorSociety_EndlessReward reward : list) {
            if (rewardTaken.contains(reward.id_Reward)) {

            } else {
                if (this.endlessRound >= reward.roundReward) {
                    currentRewardList.add(reward);
                    inc++;
                    if (inc >= max) {
                        return;
                    }
                }
            }
        }
    }

    public List<GladiatorSociety_EndlessReward> getCurrentRewardList() {
        return currentRewardList;
    }

    public Set<String> getRewardTakenList() {
        return this.rewardTaken;
    }

    public void addTakenReward(String reward) {
        rewardTaken.add(reward);
    }


}
