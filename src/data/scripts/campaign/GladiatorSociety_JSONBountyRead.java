/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src.data.scripts.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import src.data.scripts.campaign.dataclass.*;
import src.data.utils.GladiatorSociety_Constants;

public class GladiatorSociety_JSONBountyRead {

    public static final Logger LOG = Global.getLogger(GladiatorSociety_JSONBountyRead.class);

    protected static final String BOUNTYPATH = "data/config/gsounty/Missions.csv";
    protected static final String ENDLESSPATH = "data/config/gsounty/EndlessFaction.csv";
    protected static final String ENDLESSREWARDPATH = "data/config/gsounty/EndlessReward.csv";
    protected static final String MISSIONPATH = "data/config/gsounty/";

    protected static final int SIZE;
    protected static final List<GladiatorSociety_BountyData> BOUNTY_LIST = new ArrayList<>();
    protected static final Set<String> ENDLESS_LIST = new HashSet<>();
    protected static final List<GladiatorSociety_EndlessReward> ENDLESS_REWARD_LIST = new ArrayList<>();

    private static int maxlevel;

    static {
        try {
            OfficerLevelupPlugin plugin = (OfficerLevelupPlugin) Global.getSettings().getPlugin("officerLevelUp");
            maxlevel = plugin.getMaxLevel(null);
            JSONArray bountyCsv = Global.getSettings().getMergedSpreadsheetDataForMod("missionid", BOUNTYPATH, GladiatorSociety_Constants.MOD_ID);
            LOG.info("GS: Get every JSON Bounty");
            for (int x = 0; x < bountyCsv.length(); x++) {
                GladiatorSociety_BountyData bounty = new GladiatorSociety_BountyData();

                JSONObject row = bountyCsv.getJSONObject(x);

                bounty.missionid = row.getString("missionid");
                if (bounty.missionid == null || bounty.missionid.isEmpty()) {
                    continue;
                }
                bounty = loadBounty(bounty, bounty.missionid);

                if (bounty != null) {
                    BOUNTY_LIST.add(bounty);
                    LOG.info("|||   Bounty valid   |||");
                    continue;
                }
                LOG.info("|||   Bounty not valid   |||");

            }
            LOG.info("|||   Check WhiteList for Endless Factions   |||");
            JSONArray endlessCsv = Global.getSettings().getMergedSpreadsheetDataForMod("factionid", ENDLESSPATH, GladiatorSociety_Constants.MOD_ID);
            for (int x = 0; x < endlessCsv.length(); x++) {
                JSONObject row = endlessCsv.getJSONObject(x);
                String fac = row.getString("factionid");
                ENDLESS_LIST.add(fac);
            }
            LOG.info("|||  Done  |||");
            LOG.info("|||   Check reward for Endless Battle  |||");
            JSONArray endlessRewardCsv = Global.getSettings().getMergedSpreadsheetDataForMod("id_reward", ENDLESSREWARDPATH, GladiatorSociety_Constants.MOD_ID);
            for (int x = 0; x < endlessRewardCsv.length(); x++) {
                JSONObject row = endlessRewardCsv.getJSONObject(x);
                String str = row.optString("id_reward", "");
                if (str.isEmpty()) {
                    LOG.info("||| Reward ignored when no idReward tag  |||");
                    continue;
                }
                GladiatorSociety_EndlessReward reward = new GladiatorSociety_EndlessReward();
                reward.id_Reward = str;
                reward.id_Resource = row.optString("id_resource", "");

                if (reward.id_Resource.isEmpty()) {
                    LOG.info("|||   The id_resource is not valid   |||");
                    continue;
                }
                boolean blueprint = "true".equalsIgnoreCase(row.optString("blueprint", ""));
                reward.blueprint = blueprint;

                boolean flag = false;

                try {
                    if (Global.getSettings().getWeaponSpec(reward.id_Resource) != null) {
                        reward.rewardType = 2;//Weapon
                        LOG.info("|||  Weapon " + reward.id_Resource + "   |||");
                        flag = true;
                    }
                } catch (RuntimeException e) {

                }
                if (!flag) {
                    try {
                        if (Global.getSettings().getFighterWingSpec(reward.id_Resource) != null) {
                            reward.rewardType = 3;//Fighter
                            LOG.info("|||  Fighter " + reward.id_Resource + "   |||");
                            flag = true;
                        }
                    } catch (RuntimeException e) {

                    }
                }
                if (!flag) {
                    try {
                        if (Global.getSettings().doesVariantExist(reward.id_Resource)) {
                            reward.rewardType = 4;//Variant
                            LOG.info("|||  Variant " + reward.id_Resource + "   |||");
                            flag = true;
                        }
                    } catch (RuntimeException e) {

                    }
                }
                if (!flag) {
                    try {
                        if (Global.getSettings().getHullModSpec(reward.id_Resource) != null) {
                            reward.rewardType = 5;//Hullmod
                            LOG.info("|||  Hullmod " + reward.id_Resource + "   |||");
                            flag = true;
                        }
                    } catch (RuntimeException e) {

                    }
                }
                if (!flag) {
                    try {
                        if (Global.getSettings().getIndustrySpec(reward.id_Resource) != null) {
                            reward.rewardType = 6;//Industry
                            LOG.info("|||  Industry " + reward.id_Resource + "   |||");
                            flag = true;
                        }
                    } catch (RuntimeException e) {

                    }
                }
                if (!flag) {
                    try {
                        if (Global.getSettings().getHullSpec(reward.id_Resource) != null) {
                            reward.rewardType = 7;//Hull
                            LOG.info("|||  Hull " + reward.id_Resource + "   |||");
                        }
                    } catch (RuntimeException e) {

                    }
                }
                if (reward.rewardType == 0) {
                    LOG.info("||| The id_resource is not a valid weapon, fighter, variant, hull, hullmod, industry id   |||");
                    continue;
                }
                /* 
                // Check if the id_resource is on reality a blueprint.
                
                if (!blueprint.isEmpty() && reward.rewardType!=0) {
                    
                    switch (blueprint) {
                        case "weapon":
                            
                            break;
                        case "fighter":
                            reward.item_blueprint = Items.FIGHTER_BP;
                            break;
                        case "ship":
                            reward.item_blueprint = Items.SHIP_BP;
                            break;
                        case "hullmod":
                            reward.item_blueprint = Items.MODSPEC;
                            break;  
                        case "industry":
                            reward.item_blueprint = Items.INDUSTRY_BP;
                            break; 
                        default:
                            reward.item_blueprint = "";
                    }
                    LOG.info("|||  Blueprint " + reward.item_blueprint + "   |||");
                }    */
                reward.number = row.optInt("number", 1);
                reward.description = row.optString("description", "Description not found");
                reward.roundReward = row.optInt("roundReward", 0);
                ENDLESS_REWARD_LIST.add(reward);

            }
            LOG.info("|||  Done  |||");

        } catch (JSONException ex) {
            LOG.log(Level.INFO, "The row do not exist", ex);
        } catch (IOException ex) {
            LOG.log(Level.WARN, "Not success to load any csv", ex);
        }
        SIZE = BOUNTY_LIST.size();
    }

    public static List<GladiatorSociety_BountyData> getAllBountyCopy() {
        return new ArrayList<>(BOUNTY_LIST);
    }

    public static Set<String> getAllEndlessFactionCopy() {
        return new HashSet<>(ENDLESS_LIST);
    }

    public static List<GladiatorSociety_EndlessReward> getAllEndlessRewardCopy() {
        return new ArrayList<>(ENDLESS_REWARD_LIST);
    }

    public static int getAllBountySize() {
        return SIZE;
    }

    public static GladiatorSociety_BountyData loadBounty(GladiatorSociety_BountyData bounty, String missionid) throws IOException, JSONException {

        JSONObject settings = Global.getSettings().loadJSON(MISSIONPATH + missionid + ".json");
        LOG.info("|||   " + bounty.missionid + "   |||");
        if (settings == null) {
            LOG.info("|||   Error, the mission name is wrong   |||");
            return null;
        }

        bounty.faction = settings.optString("faction", Factions.PIRATES);
        String firstname = settings.optString("firstname", "");
        String lastname = settings.optString("lastname", "");
        Gender gender;

        String gen = settings.optString("gender", "N");
        switch (gen) {
            case "F":
                gender = Gender.FEMALE;
                break;
            case "M":
                gender = Gender.MALE;
                break;
            default:
                gender = Gender.ANY;
                break;
        }
        if(!(firstname.isEmpty() || lastname.isEmpty())){
        bounty.fullname = new FullName(firstname, lastname, gender);
        }
        else{
             bounty.fullname = new FullName("Unnamed", "Person", gender);
        }
        

        JSONArray dependencies = settings.optJSONArray("dependencies");
        if (dependencies != null) {
            int len = dependencies.length();

            String str;

            for (int i = 0; i < len; i++) {
                str = dependencies.getString(i);
                if (!Global.getSettings().getModManager().isModEnabled(str)) {
                    LOG.info("Dependency failed: " + str);
                    return null;
                }
            }
        }

        boolean hasFaction = false;
        for (FactionAPI factionAPI : Global.getSector().getAllFactions()) {
            if (factionAPI.getId().equals(bounty.faction)) {
                hasFaction = true;
                break;
            }
        }
        if (!hasFaction) {
            return null;
        }
        LOG.info("Faction: " + bounty.faction);

        bounty.flagship = new GladiatorSociety_DataShip(null, 0, null, null);

        bounty.flagship.personality = settings.optString("officerPersonality", "steady");
        switch (bounty.flagship.personality) {
            case "timid":
            case "aggressive":
            case "steady":
            case "cautious":
            case "reckless":
                break;
            default:
                bounty.flagship.personality = "steady";
        }
        LOG.info("Officer Personality: " + bounty.flagship.personality);

        bounty.flagship.ship = settings.optString("mainShip", "tempest_Attack");
        if (Global.getSettings().getVariant(bounty.flagship.ship) == null) {
            LOG.error(" --- Null mainShip variant: " + bounty.flagship.ship + " --- ");
            return null;
        }
        JSONArray mainshipcustom = settings.optJSONArray("mainShipCustom");
        if (mainshipcustom != null) {
            bounty.flagship.ship = mainshipcustom.getString(0);
            bounty.flagship.hullid = mainshipcustom.getString(1);
            if (Global.getSettings().getHullSpec(bounty.flagship.hullid) == null) {
                LOG.error(" --- Null mainShip hull: " + bounty.flagship.hullid + " --- ");
                return null;
            }

        }

        LOG.info("Main ship: " + bounty.flagship.ship);

        bounty.description = settings.optString("description", "Lack of information on this opponent.");
        bounty.dialog = settings.optString("dialog", "Ready for the fight!");
        bounty.avatar = settings.optString("avatar", "");
        bounty.bountyvalue = settings.optInt("reward", 50000);
        if (bounty.bountyvalue < 0) {
            bounty.bountyvalue = 0;
        }
        bounty.flagship.number = settings.optInt("officerlevel", maxlevel);
        if (bounty.flagship.number < 0 || bounty.flagship.number > maxlevel) {
            bounty.flagship.number = maxlevel;
        }
        bounty.needBounty = settings.optString("needBounty", null);

        bounty.dsrandom = settings.optInt("dsrandom", 1);
        if (!(bounty.dsrandom > -1 && bounty.dsrandom < 3)) {
            bounty.dsrandom = 1;
        }

        bounty.randomFleet = settings.optBoolean("randomFleet", false);
        bounty.combatPoint = settings.optInt("combatPoints", 0);
        if (bounty.combatPoint < 0 || bounty.combatPoint > 2000) {
            bounty.combatPoint = 0;
        }
        bounty.advships = getShips(settings, bounty);
        bounty.fleetPoints = bounty.combatPoint * 3;

        float n = addFleetPoints(bounty.flagship, true);
        if (n < 0) {
            return null;
        }
        bounty.fleetPoints += n;

        for (GladiatorSociety_DataShip dataship : bounty.advships) {
            n = addFleetPoints(dataship, false);
            if (n < 0) {
                return null;
            }
            bounty.fleetPoints += n;
        }

        //bounty.advships = new GladiatorSociety_DataShip[0];
        LOG.info(" --- Fleet Bounty Empty --- ");
        LOG.info(" --- Fleet Bounty End --- ");
        return bounty;
    }

    private static float addFleetPoints(GladiatorSociety_DataShip dataship, boolean isFlagShip) {
        float fp = 0;
        if (dataship.hullid != null) {
            ShipHullSpecAPI hullspec = Global.getSettings().getHullSpec(dataship.hullid);
            if (hullspec != null) {
                fp = hullspec.getFleetPoints();
            } else {
                LOG.error(" --- Null hull: " + dataship.hullid + " --- ");
                return -1;
            }
        } else {
            ShipVariantAPI variant = Global.getSettings().getVariant(dataship.ship);
            if (variant == null) {
                LOG.error(" --- Null Variant: " + dataship.ship + " --- ");
                return -1;
            }
            ShipHullSpecAPI hullspec = variant.getHullSpec();
            if (hullspec != null) {
                fp = hullspec.getFleetPoints();
            }
        }
        return fp * (isFlagShip ? 1 : dataship.number);
    }

    private static List<GladiatorSociety_DataShip> getShips(JSONObject settings, GladiatorSociety_BountyData bounty) throws JSONException {
        JSONArray jsonships = settings.optJSONArray("ships");
        JSONArray jsonadvships = settings.optJSONArray("advships");

        LOG.info(" --- Fleet Bounty Begin --- ");

        if (jsonadvships != null) {

            //   GladiatorSociety_DataShip[] advships = new GladiatorSociety_DataShip[jsonadvships.length()];
            List<GladiatorSociety_DataShip> advships = new ArrayList<>();

            JSONArray arr;
            GladiatorSociety_DataShip newship;
            for (int i = 0; i < jsonadvships.length(); i++) {
                arr = jsonadvships.getJSONArray(i);

                String perso = arr.getString(2);
                switch (perso) {
                    case "timid":
                    case "aggressive":
                    case "cautious":
                    case "reckless":
                        break;
                    default:
                        perso = "steady";
                }
                int val = arr.optInt(1, 1);
                if (val <= 0) {
                    val = 1;
                }
                newship = new GladiatorSociety_DataShip(arr.getString(0), val, perso, arr.length() == 4 ? arr.getString(3) : null);
                //advships[i] = new GladiatorSociety_DataShip(arr.getString(0), val, perso);
                advships.add(newship);
                LOG.info("Ship: " + newship.ship + ", number: " + newship.number + ", personality: " + newship.personality);

                // LOG.info("Ship: " + advships[i].ship + ", number: " + advships[i].number + ", personality: " + advships[i].personality);
            }
            LOG.info(" --- Fleet Bounty End --- ");

            return advships;
        }
        if (jsonships != null) {
            LOG.info("  -- Deprecated method -- ");

            //  bounty.ships = new String[0];
            /* bounty.advships = new GladiatorSociety_DataShip[0];
            return bounty;*/
            GladiatorSociety_DataShip[] advships;
            List<GladiatorSociety_DataShip> deprecatedships = new ArrayList<>();

            String str;
            int len = jsonships.length();
            int val;

            int flag = -1;
            GladiatorSociety_DataShip dataship = null;
            LOG.info("Deprecated Size: " + len);

            for (int i = 0; i < len; i++) {
                str = jsonships.getString(i);

                val = getInteger(str);
                if (val == 0) {
                    dataship = new GladiatorSociety_DataShip(str, 1, "steady", null);
                    LOG.info("Ship: " + dataship.ship + ", personality: " + dataship.personality);

                } else if (dataship != null) {
                    deprecatedships.get(flag).number = val;
                    LOG.info("Number: " + val);
                    continue;
                } else if (dataship == null) {

                    dataship = new GladiatorSociety_DataShip(bounty.flagship.ship, val, bounty.flagship.personality, bounty.flagship.hullid);
                    LOG.info("Ship: " + dataship.ship + ", personality: " + dataship.personality);
                    LOG.info("Number: " + val);

                }
                deprecatedships.add(dataship);
                flag++;
            }
            LOG.info(" --- Fleet Bounty End --- ");
            return deprecatedships;
        }
        return new ArrayList<>();
    }

    private static int getInteger(String str) {
        int value = 0;
        for (char c : str.toCharArray()) {
            if (c >= '0' && c <= '9') {
                value *= 10;
                value += c - '0';
            } else {
                return 0;
            }
        }
        return value;
    }

}
