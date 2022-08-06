package src.data.scripts.world;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.combat.ShipAIConfig;
import com.fs.starfarer.api.combat.ShipAIPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import org.apache.log4j.Logger;
import com.thoughtworks.xstream.XStream;
import src.data.utils.GladiatorSociety_Constants;
import src.data.utils.GladiatorSociety_XStreamConfig;

public class GladiatorSociety_ModPlugin extends BaseModPlugin {


    @Override
    public void configureXStream(XStream x) {
        GladiatorSociety_XStreamConfig.configureXStream(x);
    }

    @Override
    public void onNewGameAfterTimePass() {
        SectorAPI sector = Global.getSector();
        FactionAPI player = sector.getFaction(Factions.PLAYER);
        for (FactionAPI faction : Global.getSector().getAllFactions()) {
            if (faction != player) {
                faction.setRelationship(GladiatorSociety_Constants.GSFACTION_ID, RepLevel.SUSPICIOUS);
            }
        }
        player.setRelationship(GladiatorSociety_Constants.GSFACTION_ID, -1f);
    }

    private static Logger LOG = Global.getLogger(GladiatorSociety_ModPlugin.class);

    @Override
    public PluginPick<ShipAIPlugin> pickShipAI(FleetMemberAPI member, ShipAPI ship) {
        final String PREFIX = "GladiatorSociety_";
        ShipAIConfig config = new ShipAIConfig();
        String personality = "steady";

        boolean hasHullMod = false;
        if (ship.getCaptain() != null && ship.getCaptain().isDefault()) {
            for (String hullMod : ship.getVariant().getHullMods()) {
                if (hullMod.startsWith(PREFIX)) {
                    hasHullMod = true;
                    personality = hullMod.split("_")[1];
                    config.personalityOverride = personality;
                    break;
                }
            }
        }
        if (!hasHullMod) {
            return null;
        }

        LOG.info("Applying personality [" + personality + "] to ship [" + ship.getName() + "]");
        return new PluginPick<>(Global.getSettings().createDefaultShipAI(ship, config), CampaignPlugin.PickPriority.MOD_SPECIFIC);
    }

}
