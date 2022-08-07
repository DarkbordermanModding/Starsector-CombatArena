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


public class CombatArenaTokenExchangeBlueprintScript extends BaseCommandPlugin{

    public boolean purchase_hullmod(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        dialog.getTextPanel().addParagraph("WTF blueprint section");
        return true;
    }
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String arg = null;
        try{
            arg = params.get(0).getString(memoryMap);
        }catch(IndexOutOfBoundsException e){}
        if(arg == null){
            dialog.getTextPanel().addParagraph("View blueprint section");
        }
        else{
            dialog.getTextPanel().addParagraph("Redeem blueprint section using token" + arg);
        }

        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();
        opts.addOption("Redeem weapon blueprint", "CombatArenaTokenExchangeBlueprintWeaponOption");
        dialog.getTextPanel().addParagraph(cargo.getCommodityQuantity("arena_token") + "token");
        if(cargo.getCommodityQuantity("arena_token") < 2f){
            opts.setEnabled("CombatArenaTokenExchangeBlueprintWeaponOption", false);
        }
        //opts.addOption("Redeem weapon blueprint", "CombatArenaTokenExchangeBlueprintOption 4", "Go to token exchange to redeem your reward");
        //opts.addOption("Redeem hull blueprint", "CombatArenaTokenExchangeBlueprintOption 8", "Go to token exchange to redeem your reward");
        opts.addOption("Back", "CombatArenaMainEntryBackOption");
        opts.setShortcut("CombatArenaMainEntryBackOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
