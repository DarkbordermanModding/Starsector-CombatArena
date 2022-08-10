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


public class CombatArenaEndlessScript extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();
        CombatArenaRecord record = (CombatArenaRecord)Global.getSector().getPersistentData().get(
            CombatArenaRecord.COMBAT_ARENA_DATA_STORAGE_KEY
        );
        dialog.getTextPanel().addParagraph(
            "Opponent information:\n" +
            "Faction" + record.getOpponentFaction().getDisplayName() + "\n"
        );

        opts.addOption("Fight", "CombatArenaTokenExchangeBlueprintWeaponOption");
        opts.addOption("Change opponent faction", "CombatArenaEndlessFactionOption");
        //opts.addOption("Change opponent size", "CombatArenaTokenExchangeBlueprintHullmodOption");
        //opts.addOption("Change opponent combat strength", "CombatArenaTokenExchangeBlueprintHullOption");
        opts.addOption("Back", "CombatArenaMainEntryOption");
        opts.setShortcut("CombatArenaMainEntryOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
