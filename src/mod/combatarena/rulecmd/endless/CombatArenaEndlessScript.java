package mod.combatarena.rulecmd.endless;

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

public class CombatArenaEndlessScript extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        opts.addOption("Fight", "CombatArenaTokenExchangeBlueprintWeaponOption");
        opts.addOption("Change faction", "CombatArenaTokenExchangeBlueprintFighterOption");
        opts.addOption("Redeem hullmod blueprint(4 token)", "CombatArenaTokenExchangeBlueprintHullmodOption");
        opts.addOption("Redeem hull blueprint(8 token)", "CombatArenaTokenExchangeBlueprintHullOption");
        opts.addOption("Back", "CombatArenaTokenExchangeOption");
        opts.setShortcut("CombatArenaTokenExchangeOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
