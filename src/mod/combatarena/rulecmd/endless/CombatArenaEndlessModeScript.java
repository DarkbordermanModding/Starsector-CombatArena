package mod.combatarena.rulecmd.endless;

import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import mod.combatarena.utilities.CombatArenaRecord;

public class CombatArenaEndlessModeScript extends BaseCommandPlugin{

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String arg = null;
        try{
            arg = params.get(0).getString(memoryMap);
        }catch(IndexOutOfBoundsException e){}

        if(arg == null){}
        else{
            CombatArenaRecord record = (CombatArenaRecord)Global.getSector().getPersistentData().get(
                CombatArenaRecord.COMBAT_ARENA_DATA_STORAGE_KEY
            );
            switch(arg){
                case "fleetparam":{
                    record.setOpponentMode("fleetparam");
                    break;
                }
                case "fleetpoints":{
                    record.setOpponentMode("fleetpoints");
                    break;
                }
            }
        }

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        opts.addOption("Fleet parameter mode", "CombatArenaEndlessModeFleetParamOption");
        opts.addOption("Fleet point mode", "CombatArenaEndlessModeFleetPointsOption");
        opts.addOption("Back", "CombatArenaEndlessOption");
        opts.setShortcut("CombatArenaEndlessOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
