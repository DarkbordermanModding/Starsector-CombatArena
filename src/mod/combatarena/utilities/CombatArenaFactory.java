package mod.combatarena.utilities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;

import static com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3.*;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

public class CombatArenaFactory {

    public static final Logger LOG = Global.getLogger(CombatArenaFactory.class);

    public static CampaignFleetAPI createFleetByFleetParam(CombatArenaRecord record) {
        // create mock market
        MarketAPI market = Global.getFactory().createMarket("fake", "fake", 5);
        Random random = new Random();

        // Create parameter for picking doctrine only
        FleetParamsV3 params = new FleetParamsV3(market, null, null, null, null, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
        params.factionId = record.getOpponentFaction().getId();
        params.minShipSize = record.opponentMinShipSize;
        params.maxShipSize = record.opponentMaxShipSize;
        if(params.factionId.equals("combat_arena")) params.mode = ShipPickMode.ALL;
        else params.mode = ShipPickMode.PRIORITY_THEN_ALL;

        CampaignFleetAPI fleet = createEmptyFleet(record.getOpponentFaction().getId(), FleetTypes.PERSON_BOUNTY_FLEET, market);
        float averageFleetPoint = record.getOpponentFleetPoint() / record.getDistributionDenominator();
        // prevent too small fleet and no spawn
        if(averageFleetPoint < 10f) averageFleetPoint = 10f;

        // fleet creation
        if(record.getOpponentWarship()) addCombatFleetPoints(fleet, random, averageFleetPoint, 0f, 0f, params);
        if(record.getOpponentCarrier()) addCombatFleetPoints(fleet, random, 0f, averageFleetPoint, 0f, params);
        if(record.getOpponentPhaser()) addCombatFleetPoints(fleet, random, 0f, 0f, averageFleetPoint, params);
        if(record.getOpponentFreighter()) addFreighterFleetPoints(fleet, random, averageFleetPoint, params);
        if(record.getOpponentTanker()) addTankerFleetPoints(fleet, random, averageFleetPoint, params);
        if(record.getOpponentLiner()) addLinerFleetPoints(fleet, random, averageFleetPoint, params);
        if(record.getOpponentTransport()) addTransportFleetPoints(fleet, random, averageFleetPoint, params);
        if(record.getOpponentUtilities()) addUtilityFleetPoints(fleet, random, averageFleetPoint, params);

        // if result fleet combat point is too small, will generate default ship size to compensate it.
        if(record.getOpponentFleetPoint() - fleet.getFleetPoints() > 10f){
            LOG.info("||| Fleet to small, try to regen" + fleet.getFleetPoints());
            float diff = record.getOpponentFleetPoint() - fleet.getFleetPoints();
            params.minShipSize = 1;
            params.maxShipSize = record.opponentMaxShipSize;
            addCombatFleetPoints(fleet, random, diff, 0f, 0f, params);
        }
        LOG.info("||| " + fleet.getFleetPoints());

        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();
        for (FleetMemberAPI member : members) {
            member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
            // member.setCaptain(
            //     OfficerManagerEvent.createOfficer(
            //         Global.getSector().getFaction(factionId), 1, true
            //     )
            // );
            // member.getVariant().addMod(HullMods.REINFORCEDHULL);
            //member.getVariant().addPermaMod(HullMods.REINFORCEDHULL, true);
        }
        fleet.setNoFactionInName(true);
        fleet.setFaction(record.opponentFaction, true);
        fleet.setName("Opponent fleet");
        fleet.getAI().addAssignment(FleetAssignment.INTERCEPT, Global.getSector().getPlayerFleet(), 1000000f, null);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        fleet.getMemoryWithoutUpdate().set("$dialog", "The opponent glares at you briefly before shutting down the comm link.");
        Misc.makeImportant(fleet, "combat_arena", 120);
        return fleet;
    }

    public static CampaignFleetAPI createFleetByFleetPoints(CombatArenaRecord record) {
        // stub: not yet implemented
        MarketAPI market = Global.getFactory().createMarket("fake", "fake", 5);
        Random random = new Random();

        CampaignFleetAPI fleet = createEmptyFleet(record.getOpponentFaction().getId(), FleetTypes.PERSON_BOUNTY_FLEET, market);

        List<ShipVariantAPI> variants = new ArrayList<ShipVariantAPI>();
        for(String variantId: Global.getSettings().getAllVariantIds()){
            ShipVariantAPI variant = Global.getSettings().getVariant(variantId);
            HullSize size = variant.getHullSize();
            if(size == HullSize.FRIGATE || size == HullSize.DESTROYER || size == HullSize.CRUISER || size == HullSize.CAPITAL_SHIP){
                if(
                    !variant.isStation() &&
                    !variant.getHullVariantId().contains("module_") &&
                    !variant.getHullVariantId().contains("remnant_") &&
                    !variant.getHullVariantId().contains("derelict_mothership_") &&
                    !variant.getHullVariantId().contains("astral1") //bugged variants
                ) variants.add(variant);
            }
        }

        Collections.sort(variants, (new Comparator<ShipVariantAPI>() {
            @Override
            public int compare(ShipVariantAPI a, ShipVariantAPI b){
                return a.getHullSpec().getFleetPoints() - b.getHullSpec().getFleetPoints();
            }
        }));

        int quartile = variants.size() / 4;
        int multi = record.getOpponentQuantile();
        List<ShipVariantAPI> variantQuartiles = variants.subList(quartile * (multi - 1), quartile * multi);

        int currentFleetPoints = 0;
        while(currentFleetPoints < record.opponentFleetPoint){
            ShipVariantAPI variant = variantQuartiles.get(random.nextInt(variantQuartiles.size()));
            LOG.info(variant.getHullVariantId());
            fleet.getFleetData().addFleetMember(
                Global.getFactory().createFleetMember(FleetMemberType.SHIP, variant.getHullVariantId())
            );
            currentFleetPoints += variant.getHullSpec().getFleetPoints();
        }
        fleet.getFleetData().setOnlySyncMemberLists(false);
        fleet.getFleetData().sort();
        fleet.forceSync();
        fleet.setNoFactionInName(true);
        fleet.setFaction("combat_arena", true);
        fleet.setName("Opponent fleet");
        fleet.getAI().addAssignment(FleetAssignment.INTERCEPT, Global.getSector().getPlayerFleet(), 1000000f, null);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        fleet.getMemoryWithoutUpdate().set("$dialog", "The opponent glares at you briefly before shutting down the comm link.");
        Misc.makeImportant(fleet, "combat_arena", 120);

        return fleet;
    }
}
