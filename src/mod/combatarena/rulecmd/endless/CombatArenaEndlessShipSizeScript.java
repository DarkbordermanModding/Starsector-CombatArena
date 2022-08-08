package mod.combatarena.rulecmd.endless;

import java.util.HashMap;
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

public class CombatArenaEndlessShipSizeScript extends BaseCommandPlugin {

    private static HashMap<Integer, String> sizeMap = new HashMap<Integer, String>();
    static {
        sizeMap.put(1, "Frigate");
        sizeMap.put(2, "Destroyer");
        sizeMap.put(3, "Cruiser");
        sizeMap.put(4, "Capital ship");
    };

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        CombatArenaRecord record = (CombatArenaRecord)Global.getSector().getPersistentData().get(
            CombatArenaRecord.COMBAT_ARENA_DATA_STORAGE_KEY
        );

        String arg = null;
        try{
            arg = params.get(0).getString(memoryMap);
        }catch(IndexOutOfBoundsException e){}

        if(arg == null){}
        else{
            switch(arg){
                case "max":{
                    int max = record.opponentMaxShipSize + 1;
                    if(max > 4) max = record.opponentMinShipSize;
                    record.setOpponentMaxShipSize(max);
                    break;
                }
                case "min":{
                    int min = record.opponentMinShipSize + 1;
                    if(min > record.opponentMaxShipSize) min = 1;
                    record.setOpponentMinShipSize(min);
                    break;
                }
            }
        }

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        opts.addOption("Change with max size: " + sizeMap.get(record.opponentMaxShipSize), "CombatArenaEndlessMaxShipSizeOption");
        opts.addOption("Change with min size: " + sizeMap.get(record.opponentMinShipSize), "CombatArenaEndlessMinShipSizeOption");
        opts.addOption("Back", "CombatArenaEndlessOption");
        opts.setShortcut("CombatArenaEndlessOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
