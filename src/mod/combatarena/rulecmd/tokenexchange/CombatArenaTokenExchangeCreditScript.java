package mod.combatarena.rulecmd.tokenexchange;

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


public class CombatArenaTokenExchangeCreditScript extends BaseCommandPlugin{

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String arg = null;
        try{
            arg = params.get(0).getString(memoryMap);
        }catch(IndexOutOfBoundsException e){}

        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        int amount = (int)cargo.getCommodityQuantity("arena_token");
        if(arg == null){
            dialog.getTextPanel().addParagraph("You have " + amount + " token and can exchange " + amount * 200000 + " credits.\n" +
                "Do you wish to continue?"
            );
        }
        else{
            switch(arg){
                case "yes":{
                    cargo.getCredits().add(amount * 200000);
                    cargo.removeCommodity("arena_token", amount);
                    break;
                }
            }
        }

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        opts.addOption("Yes", "CombatArenaTokenExchangeCreditYesOption");
        if(cargo.getCommodityQuantity("arena_token") <= 0f){
            opts.setEnabled("CombatArenaTokenExchangeCreditYesOption", false);
        }
        opts.addOption("No/Leave", "CombatArenaTokenExchangeOption");
        opts.setShortcut("CombatArenaTokenExchangeOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
