package mod.combatarena.rulecmd.tokenexchange;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.loading.HullModSpecAPI;
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
            dialog.getTextPanel().addParagraph("View blueprint section");
        }
        else{
            dialog.getTextPanel().addParagraph("Redeem blueprint section using token" + arg);

            Random random = new Random();
            switch(arg){
                case "weapon":{
                    String weaponId = Global.getSector().getAllWeaponIds().get(
                        random.nextInt(Global.getSector().getAllWeaponIds().size())
                    );
                    cargo.addSpecial(new SpecialItemData(Items.WEAPON_BP, weaponId), 1f);
                    cargo.removeCommodity("arena_token", 2f);
                    break;
                }
                case "fighter":{
                    String fighterId = Global.getSector().getAllFighterWingIds().get(
                        random.nextInt(Global.getSector().getAllFighterWingIds().size())
                    );
                    cargo.addSpecial(new SpecialItemData(Items.FIGHTER_BP, fighterId), 1f);
                    cargo.removeCommodity("arena_token", 4f);
                    break;
                }
                case "hullmod":{
                    HullModSpecAPI hullmod = Global.getSettings().getAllHullModSpecs().get(
                        random.nextInt(Global.getSettings().getAllHullModSpecs().size())
                    );
                    cargo.addSpecial(new SpecialItemData(Items.MODSPEC, hullmod.getId()), 1f);
                    cargo.removeCommodity("arena_token", 4f);
                    break;
                }
                case "hull":{
                    ShipHullSpecAPI hull = Global.getSettings().getAllShipHullSpecs().get(
                        random.nextInt(Global.getSettings().getAllShipHullSpecs().size())
                    );
                    cargo.addSpecial(new SpecialItemData(Items.SHIP_BP, hull.getHullId()), 1f);
                    cargo.removeCommodity("arena_token", 8f);
                    break;
                }
            }
        }

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        opts.addOption("Redeem gamma core(2 token)", "CombatArenaTokenExchangeCoreGammaOption");
        dialog.getTextPanel().addParagraph(cargo.getCommodityQuantity("arena_token") + "token");
        if(cargo.getCommodityQuantity("arena_token") < 2f){
            opts.setEnabled("CombatArenaTokenExchangeCoreGammaOption", false);
        }
        opts.addOption("Redeem beta core(4 token)", "CombatArenaTokenExchangeCoreBetaOption");
        dialog.getTextPanel().addParagraph(cargo.getCommodityQuantity("arena_token") + "token");
        if(cargo.getCommodityQuantity("arena_token") < 4f){
            opts.setEnabled("CombatArenaTokenExchangeCoreBetaOption", false);
        }
        opts.addOption("Redeem alpha core(8 token)", "CombatArenaTokenExchangeCoreAlphaOption");
        dialog.getTextPanel().addParagraph(cargo.getCommodityQuantity("arena_token") + "token");
        if(cargo.getCommodityQuantity("arena_token") < 8f){
            opts.setEnabled("CombatArenaTokenExchangeCoreAlphaOption", false);
        }
        opts.addOption("Redeem omega core(16 token)", "CombatArenaTokenExchangeCoreOmegaOption");
        dialog.getTextPanel().addParagraph(cargo.getCommodityQuantity("arena_token") + "token");
        if(cargo.getCommodityQuantity("arena_token") < 16f){
            opts.setEnabled("CombatArenaTokenExchangeCoreOmegaOption", false);
        }
        opts.addOption("Back", "CombatArenaMainEntryBackOption");
        opts.setShortcut("CombatArenaMainEntryBackOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
