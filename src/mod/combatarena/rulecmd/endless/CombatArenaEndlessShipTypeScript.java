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

public class CombatArenaEndlessShipTypeScript extends BaseCommandPlugin {

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
                case "warship":{
                    record.setOpponentWarship(!record.getOpponentWarship());
                    break;
                }
                case "carrier":{
                    record.setOpponentCarrier(!record.getOpponentCarrier());
                    break;
                }
                case "phaser":{
                    record.setOpponentPhaser(!record.getOpponentPhaser());
                    break;
                }
            }
        }

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        opts.addOption("Enable Warship: " + record.getOpponentWarship(), "CombatArenaEndlessShipTypeWarshipOption");
        opts.addOption("Enable Carrier: " + record.getOpponentCarrier(), "CombatArenaEndlessShipTypeCarrierOption");
        opts.addOption("Enable Phase ship: " + record.getOpponentPhaser(), "CombatArenaEndlessShipTypePhaserOption");

        opts.addOption("Back", "CombatArenaEndlessOption");
        opts.setShortcut("CombatArenaEndlessOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
