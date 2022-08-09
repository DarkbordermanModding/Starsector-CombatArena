package mod.combatarena.rulecmd.tokenexchange;

import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;


public class CombatArenaTokenExchangeCoreScript extends BaseCommandPlugin{

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String arg = null;
        try{
            arg = params.get(0).getString(memoryMap);
        }catch(IndexOutOfBoundsException e){}

        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        if(arg == null){
            dialog.getTextPanel().addParagraph("View core section");
        }
        else{
            dialog.getTextPanel().addParagraph("Redeem core section using token" + arg);

            switch(arg){
                case "gamma":{
                    cargo.addCommodity(Commodities.GAMMA_CORE, 1f);
                    cargo.removeCommodity("arena_token", 2f);
                    break;
                }
                case "beta":{
                    cargo.addCommodity(Commodities.BETA_CORE, 1f);
                    cargo.removeCommodity("arena_token", 4f);
                    break;
                }
                case "alpha":{
                    cargo.addCommodity(Commodities.ALPHA_CORE, 1f);
                    cargo.removeCommodity("arena_token", 8f);
                    break;
                }
                case "omega":{
                    cargo.addCommodity(Commodities.OMEGA_CORE, 1f);
                    cargo.removeCommodity("arena_token", 16f);
                    break;
                }
            }
        }

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        opts.addOption("Redeem gamma core(2 token)", "CombatArenaTokenExchangeCoreGammaOption");
        if(cargo.getCommodityQuantity("arena_token") < 2f){
            opts.setEnabled("CombatArenaTokenExchangeCoreGammaOption", false);
        }
        opts.addOption("Redeem beta core(4 token)", "CombatArenaTokenExchangeCoreBetaOption");
        if(cargo.getCommodityQuantity("arena_token") < 4f){
            opts.setEnabled("CombatArenaTokenExchangeCoreBetaOption", false);
        }
        opts.addOption("Redeem alpha core(8 token)", "CombatArenaTokenExchangeCoreAlphaOption");
        if(cargo.getCommodityQuantity("arena_token") < 8f){
            opts.setEnabled("CombatArenaTokenExchangeCoreAlphaOption", false);
        }
        opts.addOption("Redeem omega core(16 token)", "CombatArenaTokenExchangeCoreOmegaOption");
        if(cargo.getCommodityQuantity("arena_token") < 16f){
            opts.setEnabled("CombatArenaTokenExchangeCoreOmegaOption", false);
        }
        opts.addOption("Back", "CombatArenaTokenExchangeOption");
        opts.setShortcut("CombatArenaTokenExchangeOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
