package src.data.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GladiatorSociety_EndlessContent {

    private int endlessPower;
    private int endlessRound;
    private String nextFaction;
    private FactionAPI currentFaction;

    public GladiatorSociety_EndlessContent() {
        endlessPower = 100;
        endlessRound = 0;
        setRandomFaction();
    }

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
        nextFaction = Factions.PIRATES;
    }

    public int getEndlessReward() {
        return (int) (4.5 * Math.pow(endlessPower, 2)) + endlessPower * 1000;
    }
}
