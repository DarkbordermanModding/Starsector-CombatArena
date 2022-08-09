package mod.combatarena.rulecmd.tokenexchange;

import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

public class CombatArenaTokenExchangeMainEntry extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        dialog.getTextPanel().addParagraph("Welcome to the Combat arena, a place for you to spill the blood and test your maximum.");
        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();
        opts.addOption("Redeem token for blueprints", "CombatArenaTokenExchangeBlueprintOption");
        opts.addOption("Redeem token for cores", "CombatArenaTokenExchangeCoreOption");
        opts.addOption("Leave", "CombatArenaMainEntryOption");
        opts.setShortcut("CombatArenaMainEntryOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
