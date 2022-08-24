package mod.combatarena.rulecmd.tokenexchange;

import java.util.ArrayList;
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
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.util.Misc;


public class CombatArenaTokenExchangeBlueprintScript extends BaseCommandPlugin{

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
            dialog.getTextPanel().addParagraph("Redeem blueprint using token" + arg);

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
                case "hullmod":{
                    List<HullModSpecAPI> hullmods = new ArrayList<HullModSpecAPI>();
                    for (HullModSpecAPI spec: Global.getSettings().getAllHullModSpecs()){
                        if(!spec.isHidden() && !spec.isAlwaysUnlocked()){
                            hullmods.add(spec);
                        }
                    }

                    HullModSpecAPI hullmod = hullmods.get(random.nextInt(hullmods.size()));
                    cargo.addSpecial(new SpecialItemData(Items.MODSPEC, hullmod.getId()), 1f);
                    cargo.removeCommodity("arena_token", 4f);
                    break;
                }
                case "hull":{
                    List<ShipHullSpecAPI> hulls = new ArrayList<ShipHullSpecAPI>();
                    for(ShipHullSpecAPI spec: Global.getSettings().getAllShipHullSpecs()){
                        if(spec.getHullSize() != HullSize.FIGHTER){
                            hulls.add(spec);
                        }
                    }

                    ShipHullSpecAPI hull = hulls.get(random.nextInt(hulls.size()));
                    cargo.addSpecial(new SpecialItemData(Items.SHIP_BP, hull.getHullId()), 1f);
                    cargo.removeCommodity("arena_token", 8f);
                    break;
                }
            }
        }

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        opts.addOption("Redeem weapon blueprint(2 token)", "CombatArenaTokenExchangeBlueprintWeaponOption");
        if(cargo.getCommodityQuantity("arena_token") < 2f){
            opts.setEnabled("CombatArenaTokenExchangeBlueprintWeaponOption", false);
        }
        opts.addOption("Redeem hullmod blueprint(4 token)", "CombatArenaTokenExchangeBlueprintHullmodOption");
        if(cargo.getCommodityQuantity("arena_token") < 4f){
            opts.setEnabled("CombatArenaTokenExchangeBlueprintHullmodOption", false);
        }
        opts.addOption("Redeem hull blueprint(8 token)", "CombatArenaTokenExchangeBlueprintHullOption");
        if(cargo.getCommodityQuantity("arena_token") < 8f){
            opts.setEnabled("CombatArenaTokenExchangeBlueprintHullOption", false);
        }
        opts.addOption("Back", "CombatArenaTokenExchangeOption");
        opts.setShortcut("CombatArenaTokenExchangeOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
