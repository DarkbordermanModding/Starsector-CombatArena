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

public class CombatArenaEndlessFactionScript extends BaseCommandPlugin {

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
            dialog.getTextPanel().addParagraph("Redeem blueprint using token" + arg);

            CombatArenaRecord record = new CombatArenaRecord();
            switch(arg){
                case "random":{
                    record.randomizeOpponentFaction();
                    break;
                }
                case "mixed":{
                    record.setOpponentFaction(Global.getSector().getFaction("combat_arena"));
                    break;
                }
            }
        }

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        opts.addOption("Randomize faction", "CombatArenaEndlessFactionRandomOption");
        opts.addOption("Play with mixed faction", "CombatArenaEndlessFactionMixedOption");
        opts.addOption("Back", "CombatArenaEndlessOption");
        opts.setShortcut("CombatArenaEndlessOption", Keyboard.KEY_ESCAPE, false, false, false, false);
        return true;
    }
}
