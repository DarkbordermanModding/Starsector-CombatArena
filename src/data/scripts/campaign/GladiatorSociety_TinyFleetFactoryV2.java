package src.data.scripts.campaign;

import java.util.List;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionDoctrineAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import static com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3.*;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

public class GladiatorSociety_TinyFleetFactoryV2 {

    public static final Logger LOG = Global.getLogger(GladiatorSociety_TinyFleetFactoryV2.class);

    public static CampaignFleetAPI createFleet(FleetParamsV3 params) {
        Global.getSettings().profilerBegin("GladiatorSociety_TinyFleetFactoryV2.createFleet()");
        LOG.info("|||   Creating Fleet Begin   |||");
        try {
            params.ignoreMarketFleetSizeMult = true;

            boolean fakeMarket = true;
            MarketAPI market = pickMarket(params);
            if (market == null) {
                market = Global.getFactory().createMarket("fake", "fake", 5);
                market.getStability().modifyFlat("fake", 10000);
                market.setFactionId(params.factionId);
                market.getStats().getDynamic().getMod(Stats.FLEET_QUALITY_MOD).modifyFlat("fake", BASE_QUALITY_WHEN_NO_MARKET);
                market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat("fake", 1f);
            }
            boolean sourceWasNull = params.source == null;
            params.source = market;
            if (sourceWasNull && params.qualityOverride == null) { // we picked a nearby market based on location
                params.updateQualityAndProducerFromSourceMarket();
            }

            String factionId = params.factionId;
            if (factionId == null) {
                factionId = params.source.getFactionId();
            }

            if(factionId.equals("combat_arena")){
                params.mode = ShipPickMode.ALL;
            }else{
                params.mode = ShipPickMode.PRIORITY_THEN_ALL;
            }

            CampaignFleetAPI fleet = createEmptyFleet(factionId, params.fleetType, market);
            fleet.getFleetData().setOnlySyncMemberLists(true);

            FactionDoctrineAPI doctrine = fleet.getFaction().getDoctrine();
            if (params.doctrineOverride != null) {
                doctrine = params.doctrineOverride;
            }

            float numShipsMult = 1f;
            if (params.ignoreMarketFleetSizeMult == null || !params.ignoreMarketFleetSizeMult) {
                numShipsMult = market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).computeEffective(0f);
            }

            Random random = new Random();
            if (params.random != null) {
                random = params.random;
            }

            float combatPts = params.combatPts;//(numShipsMult + 3);

            if (params.onlyApplyFleetSizeToCombatShips != null && params.onlyApplyFleetSizeToCombatShips) {
                numShipsMult = 1f;
            }

            float freighterPts = params.freighterPts * numShipsMult;
            if (combatPts < 10 && combatPts > 0) {
                combatPts = Math.max(combatPts, 5 + random.nextInt(6));
            }

            float dW = (float) doctrine.getWarships() + random.nextInt(3) - 2;
            float dC = (float) doctrine.getCarriers() + random.nextInt(3) - 2;
            float dP = (float) doctrine.getPhaseShips() + random.nextInt(3) - 2;

            float r1 = random.nextFloat();
            float r2 = random.nextFloat();
            float min = Math.min(r1, r2);
            float max = Math.max(r1, r2);

            float mag = 1f;
            float v1 = min;
            float v2 = max - min;
            float v3 = 1f - max;

            v1 *= mag;
            v2 *= mag;
            v3 *= mag;

            v1 -= mag / 3f;
            v2 -= mag / 3f;
            v3 -= mag / 3f;

            dW += v1;
            dC += v2;
            dP += v3;

            boolean banPhaseShipsEtc = !fleet.getFaction().isPlayerFaction()
                    && combatPts < FLEET_POINTS_THRESHOLD_FOR_ANNOYING_SHIPS;
            if (params.forceAllowPhaseShipsEtc != null && params.forceAllowPhaseShipsEtc) {
                banPhaseShipsEtc = !params.forceAllowPhaseShipsEtc;
            }

            params.banPhaseShipsEtc = banPhaseShipsEtc;

            if (banPhaseShipsEtc) {
                dP = 0;
            };

            if (dW < 0) {
                dW = 0;
            }
            if (dC < 0) {
                dC = 0;
            }
            if (dP < 0) {
                dP = 0;
            }

            float extra = 7 - (dC + dP + dW);
            if (extra < 0) {
                extra = 0f;
            }
            if (doctrine.getWarships() > doctrine.getCarriers() && doctrine.getWarships() > doctrine.getPhaseShips()) {
                dW += extra;
            } else if (doctrine.getCarriers() > doctrine.getWarships() && doctrine.getCarriers() > doctrine.getPhaseShips()) {
                dC += extra;
            } else if (doctrine.getPhaseShips() > doctrine.getWarships() && doctrine.getPhaseShips() > doctrine.getCarriers()) {
                dP += extra;
            }

            float doctrineTotal = dW + dC + dP;

            //System.out.println("DW: " + dW + ", DC: " + dC + " DP: " + dP);
            combatPts = (int) combatPts;
            int warships = (int) (combatPts * dW / doctrineTotal);
            int carriers = (int) (combatPts * dC / doctrineTotal);
            int phase = (int) (combatPts * dP / doctrineTotal);

            warships += (combatPts - warships - carriers - phase);
            params.minShipSize = 4;
            params.maxShipSize = 4;

            if (params.treatCombatFreighterSettingAsFraction != null && params.treatCombatFreighterSettingAsFraction) {
                float combatFreighters = (int) Math.min(freighterPts * 1.5f, warships * 1.5f) * doctrine.getCombatFreighterProbability();
                float added = addCombatFreighterFleetPoints(fleet, random, combatFreighters, params);
                freighterPts -= added * 0.5f;
                warships -= added * 0.5f;
            } else if (freighterPts > 0 && random.nextFloat() < doctrine.getCombatFreighterProbability()) {
                float combatFreighters = (int) Math.min(freighterPts * 1.5f, warships * 1.5f);
                float added = addCombatFreighterFleetPoints(fleet, random, combatFreighters, params);
                freighterPts -= added * 0.5f;
                warships -= added * 0.5f;
            }

            addCombatFleetPoints(fleet, random, warships, carriers, phase, params);

            addFreighterFleetPoints(fleet, random, freighterPts, params);
            fleet.getFleetData().sort();
            fleet.forceSync();

            fleet.getFleetData().setOnlySyncMemberLists(false);
            fleet.getFleetData().sort();

            // Do some custom fleet advanced options
            List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();
            for (FleetMemberAPI member : members) {
                member.setCaptain(
                    OfficerManagerEvent.createOfficer(
                        Global.getSector().getFaction(factionId), 1, true
                    )
                );
                member.getVariant().addMod(HullMods.REINFORCEDHULL);
                member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
            }
            fleet.setNoFactionInName(true);
            fleet.setFaction("combat_arena", true);
            fleet.setName("Gladiator fleet");
            fleet.getAI().addAssignment(FleetAssignment.INTERCEPT, Global.getSector().getPlayerFleet(), 1000000f, null);
            fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
            fleet.getMemoryWithoutUpdate().set("$dialog", "The gladiator glares at you briefly before shutting down the comm link.");
            Misc.makeImportant(fleet, "combat_arena", 120);
            return fleet;

        } finally {
            Global.getSettings().profilerEnd();
        }
    }
}
