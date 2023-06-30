package mod.combatarena.rulecmd.combatarena;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

public class ShouldShowCombatArena extends BaseCommandPlugin {

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (Global.getSettings().isDevMode()) return true;

        MarketAPI market = dialog.getInteractionTarget().getMarket();
        if (market == null) return false;
        if (!market.hasIndustry("combatarena")) return false;
        if (!market.getIndustry("combatarena").isFunctional()) return false;

        return true;
    }
}
