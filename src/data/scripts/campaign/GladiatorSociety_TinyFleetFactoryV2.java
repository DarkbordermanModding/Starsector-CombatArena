package src.data.scripts.campaign;

import java.util.List;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FactionDoctrineAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.FactionAPI.ShipPickMode;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDConfig;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDConfigGen;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl.FIDDelegate;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent.SkillPickPreference;
import static com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3.*;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.loading.WeaponGroupSpec;
import com.fs.starfarer.api.loading.WeaponGroupType;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import java.io.IOException;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GladiatorSociety_TinyFleetFactoryV2 {

    protected static final String CUSTOMVARIANTPATH = "data/config/gsounty/gladiator_variants/";

    public static final Logger LOG = Global.getLogger(GladiatorSociety_TinyFleetFactoryV2.class);

    public static void addGSInteractionConfig(CampaignFleetAPI fleet) {
        fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_INTERACTION_DIALOG_CONFIG_OVERRIDE_GEN,
                new GSFleetInteractionConfigGen());
    }

    public static class GSFleetInteractionConfigGen implements FIDConfigGen {

        @Override
        public FIDConfig createConfig() {
            FIDConfig config = new FIDConfig();
            config.showTransponderStatus = false;
            config.delegate = new FIDDelegate() {

                @Override
                public void postPlayerSalvageGeneration(InteractionDialogAPI dialog, FleetEncounterContext context, CargoAPI salvage) {
                }

                @Override
                public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
                    bcc.aiRetreatAllowed = false;

                }

                @Override
                public void notifyLeave(InteractionDialogAPI dialog) {
                }
            };
            return config;
        }
    }

    public static CampaignFleetAPI createFleet(FleetParamsV3 params,PersonAPI person) {
        Global.getSettings().profilerBegin("GladiatorSociety_TinyFleetFactoryV2.createFleet()");
        try {
            boolean fakeMarket = false;
            MarketAPI market = pickMarket(params);
            if (market == null) {
                market = Global.getFactory().createMarket("fake", "fake", 5);
                market.getStability().modifyFlat("fake", 10000);
                market.setFactionId(params.factionId);
                SectorEntityToken token = Global.getSector().getHyperspace().createToken(0, 0);
                market.setPrimaryEntity(token);
                market.getStats().getDynamic().getMod(Stats.FLEET_QUALITY_MOD).modifyFlat("fake", BASE_QUALITY_WHEN_NO_MARKET);

                market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat("fake", 1f);

                fakeMarket = true;
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

            ShipPickMode mode = Misc.getShipPickMode(market, factionId);
            if (params.modeOverride != null) {
                mode = params.modeOverride;
            }

            CampaignFleetAPI fleet = createEmptyFleet(factionId, params.fleetType, market);
            fleet.getFleetData().setOnlySyncMemberLists(true);
            FleetMemberAPI memb = null;
            ShipVariantAPI variant2;
            int countFleet = 0;
            if (memb != null) {
                fleet.getFleetData().addFleetMember(memb);
                 countFleet++;
                memb.setShipName(person.getName().getFullName() + "'s Ship");
            } else {
                LOG.info("|||   Creating MotherShip failed   |||");
            }
            LOG.info("|||   Creating Custom Fleet Begin   |||");


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

            float combatPts = params.combatPts;
            if(countFleet==0){
                combatPts+=10;
            }
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

            params.mode = mode;
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
            CampaignFleetAPI subfleet = createEmptyFleet(factionId, params.fleetType, market);

                if (params.treatCombatFreighterSettingAsFraction != null && params.treatCombatFreighterSettingAsFraction) {
                    float combatFreighters = (int) Math.min(freighterPts * 1.5f, warships * 1.5f) * doctrine.getCombatFreighterProbability();
                    float added = addCombatFreighterFleetPoints(subfleet, random, combatFreighters, params);
                    freighterPts -= added * 0.5f;
                    warships -= added * 0.5f;
                } else if (freighterPts > 0 && random.nextFloat() < doctrine.getCombatFreighterProbability()) {
                    float combatFreighters = (int) Math.min(freighterPts * 1.5f, warships * 1.5f);
                    float added = addCombatFreighterFleetPoints(subfleet, random, combatFreighters, params);
                    freighterPts -= added * 0.5f;
                    warships -= added * 0.5f;
                }
                params.maxShipSize = 4;

                addCombatFleetPoints(subfleet, random, warships, carriers, phase, params);
                addFreighterFleetPoints(subfleet, random, freighterPts, params);

                subfleet.getFleetData().sort();
            List<FleetMemberAPI> members = subfleet.getFleetData().getMembersListCopy();
            for (FleetMemberAPI mem : members) {
                fleet.getFleetData().addFleetMember(mem);
            }

            if (params.withOfficers) {
                GladiatorSociety_TinyFleetFactoryV2.addCommanderAndOfficers(fleet, params, random);
            }

            fleet.forceSync();

            if (fleet.getFleetData().getNumMembers() <= 0
                    || fleet.getFleetData().getNumMembers() == fleet.getNumFighters()) {
            }

            if (fakeMarket) {
                params.source = null;
            }

            /*  DefaultFleetInflaterParams p = new DefaultFleetInflaterParams();
            p.quality = quality;
            p.persistent = true;
            p.seed = random.nextLong();
            p.mode = mode;
            p.timestamp = params.timestamp;

            FleetInflater inflater = Misc.getInflater(fleet, p);
            fleet.setInflater(inflater);*/
            fleet.getFleetData().setOnlySyncMemberLists(false);
            fleet.getFleetData().sort();

            members = fleet.getFleetData().getMembersListCopy();
            for (FleetMemberAPI member : members) {
                member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
            }
            GladiatorSociety_TinyFleetFactoryV2.addGSInteractionConfig(fleet);

            return fleet;

        } finally {
            Global.getSettings().profilerEnd();
        }
    }

    private static float countParams(CampaignFleetAPI fleet) {

        float count = 0;
        int sizefleet = sizeFleet(fleet);
        if (sizefleet <= 5) {
            count = sizefleet;
        } else {
            count = 3 + (float) Math.pow(sizefleet / 3, 0.77);
        }
        return count;
    }

    private static int sizeFleet(CampaignFleetAPI fleet) {

        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();

        int maxSize = 0;
        for (FleetMemberAPI member : members) {
            if (member.isFighterWing()) {
                continue;
            }
            if (member.isFlagship()) {
                continue;
            }
            if (!member.getCaptain().isDefault()) {
                continue;
            }
            maxSize++;
        }
        return maxSize;
    }

    private static void addCommanderAndOfficers(CampaignFleetAPI fleet, FleetParamsV3 params, Random random) {
        OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");
        int min = 5;
        int max = plugin.getMaxLevel(null);
        if (max > params.officerLevelLimit) {
            max = params.officerLevelLimit;
        }

        FactionAPI faction = fleet.getFaction();

        List<FleetMemberAPI> members = fleet.getFleetData().getMembersListCopy();
        float combatPoints = 0f;
        for (FleetMemberAPI member : members) {
            if (member.isCivilian()) {
                continue;
            }
            combatPoints += member.getFleetPointCost();
        }

        boolean debug = true;
        debug = false;

        FactionDoctrineAPI doctrine = faction.getDoctrine();
        if (params.doctrineOverride != null) {
            doctrine = params.doctrineOverride;
        }

        float doctrineBonus = ((float) doctrine.getOfficerQuality() - 1f) * 0.25f;
        float fleetSizeBonus = combatPoints / 50f * 0.2f;
        if (fleetSizeBonus > 1f) {
            fleetSizeBonus = 1f;
        }

        float officerLevelValue = doctrineBonus * 0.7f + fleetSizeBonus * 0.3f;

        int maxLevel = (int) (min + Math.round((float) (max - min) * officerLevelValue));
        maxLevel += params.officerLevelBonus;
        int minLevel = maxLevel - 4;

        if (maxLevel > max) {
            maxLevel = max;
        }
        if (minLevel > max) {
            minLevel = max;
        }

        if (minLevel < min) {
            minLevel = min;
        }
        if (maxLevel < min) {
            maxLevel = min;
        }

        WeightedRandomPicker<FleetMemberAPI> picker = new WeightedRandomPicker<FleetMemberAPI>(random);
        WeightedRandomPicker<FleetMemberAPI> flagshipPicker = new WeightedRandomPicker<FleetMemberAPI>(random);

        int maxSize = 0;
        for (FleetMemberAPI member : members) {
            if (member.isFighterWing()) {
                continue;
            }
            if (member.isFlagship()) {
                continue;
            }
            if (!member.getCaptain().isDefault()) {
                continue;
            }
            int size = member.getHullSpec().getHullSize().ordinal();
            if (size > maxSize) {
                maxSize = size;
            }
        }
        for (FleetMemberAPI member : members) {
            if (member.isFighterWing()) {
                continue;
            }
            if (member.isFlagship()) {
                continue;
            }
            if (!member.getCaptain().isDefault()) {
                continue;
            }

            float q = 1f;
            if (member.isCivilian()) {
                q *= 0.0001f;
            }
            float weight = (float) member.getFleetPointCost() * q;
            picker.add(member, weight);

            int size = member.getHullSpec().getHullSize().ordinal();
            if (size >= maxSize) {
                flagshipPicker.add(member, weight);
            }
        }

        int baseOfficers = Global.getSettings().getInt("baseNumOfficers") + (int) countParams(fleet);
        int numOfficersIncludingCommander = 1 + random.nextInt(baseOfficers + 1);

        boolean commander = true;
        for (int i = 0; i < numOfficersIncludingCommander; i++) {
            FleetMemberAPI member = null;

            if (commander) {
                member = flagshipPicker.pickAndRemove();
            }
            if (member == null) {
                member = picker.pickAndRemove();
            } else {
                picker.remove(member);
            }

            if (member == null) {
                break; // out of ships that need officers
            }

            int level = (int) Math.min(max, Math.round(minLevel + random.nextFloat() * (maxLevel - minLevel)));
            if (Misc.isEasy()) {
                level = (int) Math.ceil((float) level * Global.getSettings().getFloat("easyOfficerLevelMult"));
            }

            if (level <= 0) {
                continue;
            }

            float weight = getMemberWeight(member);
            float fighters = member.getVariant().getFittedWings().size();
            boolean wantCarrierSkills = weight > 0 && fighters / weight >= 0.5f;
            SkillPickPreference pref = SkillPickPreference.ANY;
            if (wantCarrierSkills) {
                pref = SkillPickPreference.CARRIER;
            }

            
            PersonAPI person = OfficerManagerEvent.createOfficer(fleet.getFaction(), level, pref, random);
            if (person.getPersonalityAPI().getId().equals(Personalities.TIMID)) {
                person.setPersonality(Personalities.CAUTIOUS);
            }

            if (commander) {
                if (params.commander != null) {
                    person = params.commander;
                } else {
                    addCommanderSkills(person, fleet, params, random);
                }
                person.setRankId(Ranks.SPACE_COMMANDER);
                person.setPostId(Ranks.POST_FLEET_COMMANDER);
                fleet.setCommander(person);
                fleet.getFleetData().setFlagship(member);
                commander = false;

                int officerNumLimit = person.getStats().getOfficerNumber().getModifiedInt();
                int aboveBase = officerNumLimit - baseOfficers + params.officerNumberBonus;
                if (aboveBase < 0) {
                    aboveBase = 0;
                }
                numOfficersIncludingCommander += aboveBase;

                numOfficersIncludingCommander *= params.officerNumberMult;
                if (numOfficersIncludingCommander < 1) {
                    numOfficersIncludingCommander = 1;
                }
                if (debug) {
                    System.out.println("Adding " + aboveBase + " extra officers due to commander skill");
                }
            } else {
                member.setCaptain(person);
            }
        }
    }

    public static CampaignFleetAPI createFleet(FleetParamsV3 params) {
        Global.getSettings().profilerBegin("GladiatorSociety_TinyFleetFactoryV2.createFleet()");
        LOG.info("|||   Creating Fleet Begin   |||");
        try {
            boolean fakeMarket = false;
            MarketAPI market = pickMarket(params);
            if (market == null) {
                market = Global.getFactory().createMarket("fake", "fake", 5);
                market.getStability().modifyFlat("fake", 10000);
                market.setFactionId(params.factionId);
                SectorEntityToken token = Global.getSector().getHyperspace().createToken(0, 0);
                market.setPrimaryEntity(token);

                market.getStats().getDynamic().getMod(Stats.FLEET_QUALITY_MOD).modifyFlat("fake", BASE_QUALITY_WHEN_NO_MARKET);

                market.getStats().getDynamic().getMod(Stats.COMBAT_FLEET_SIZE_MULT).modifyFlat("fake", 1f);

                fakeMarket = true;
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

            ShipPickMode mode = Misc.getShipPickMode(market, factionId);
            if(factionId.equals("combat_arena")){
                mode = ShipPickMode.ALL;
            }
            else if (params.modeOverride != null) {
                mode = params.modeOverride;
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

            params.mode = mode;
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
            params.minShipSize = 3;
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

            if (params.withOfficers) {
                GladiatorSociety_TinyFleetFactoryV2.addCommanderAndOfficers(fleet, params, random);
            }

            fleet.forceSync();

            if (fakeMarket) {
                params.source = null;
            }
            fleet.getFleetData().setOnlySyncMemberLists(false);
            fleet.getFleetData().sort();
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
            GladiatorSociety_TinyFleetFactoryV2.addGSInteractionConfig(fleet);

            return fleet;

        } finally {
            Global.getSettings().profilerEnd();
        }
    }

}
