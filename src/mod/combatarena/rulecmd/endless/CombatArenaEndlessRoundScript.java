package mod.combatarena.rulecmd.endless;

import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import mod.combatarena.utilities.CombatArenaRecord;
public class CombatArenaEndlessRoundScript extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        CombatArenaRecord record = (CombatArenaRecord)Global.getSector().getPersistentData().get(
            CombatArenaRecord.COMBAT_ARENA_DATA_STORAGE_KEY
        );

        String arg = null;
        try{
            arg = params.get(0).getString(memoryMap);
        }catch(IndexOutOfBoundsException e){}

        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        if(arg == null){}
        else{
            switch(arg){
                case "increase":{
                    cargo.removeCommodity("arena_token", 1f);
                    record.increaseOpponentFleetPoint();
                    break;
                }
                case "reset":{
                    record.setOpponentFleetPoint(60f);
                    break;
                }
            }
        }

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        opts.addOption("Increase fleet difficulties(1 token)", "CombatArenaEndlessRoundIncreaseOption");
        if(cargo.getCommodityQuantity("arena_token") < 1f){
            opts.setEnabled("CombatArenaEndlessRoundIncreaseOption", false);
        }
        opts.addOption("Reset fleet difficulties", "CombatArenaEndlessRoundResetOption");
        opts.addOption("Back", "CombatArenaEndlessOption");
        opts.setShortcut("CombatArenaEndlessOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
