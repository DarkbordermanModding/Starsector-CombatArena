package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.lwjgl.input.Keyboard;
import src.data.scripts.campaign.GladiatorSociety_Content;
import src.data.scripts.campaign.GladiatorSociety_TinyFleetFactoryV2;
import src.data.scripts.campaign.dataclass.GladiatorSociety_BountyData;
import src.data.utils.GladiatorSociety_Constants;

public class GladiatorSociety_GladiatorAvailable extends BaseCommandPlugin {

    public static final String GLADIATORSOCIETY_CONTENTKEY = "$GladiatorSociety_ContentKey";

    public GladiatorSociety_Content content;

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        if (content == null) {
            if (Global.getSector().getPersistentData().containsKey(GLADIATORSOCIETY_CONTENTKEY)) {
                content = (GladiatorSociety_Content) Global.getSector().getPersistentData().get(GLADIATORSOCIETY_CONTENTKEY);
            } else {
                content = new GladiatorSociety_Content();
                Global.getSector().getPersistentData().put(GLADIATORSOCIETY_CONTENTKEY, content);
            }
        }
        if (content == null) {
            return false;
        }

        String arg = params.get(0).getString(memoryMap);
        String exitOpt = "continueCutComm";
        int num = -1;
        if (params.size() > 1) {
            num = (int) params.get(1).getFloat(memoryMap);
        }
        switch (arg) {
            case "bountyAvailable":
                bountyAvailable(dialog, memoryMap.get(MemKeys.LOCAL));
                return true;
            case "bountyDisplay":
                if (num == -1) {
                    return true;
                }
                bountyDisplay(dialog, memoryMap.get(MemKeys.LOCAL), num);
                return true;
            case "Next":
                next();
                bountyAvailable(dialog, memoryMap.get(MemKeys.LOCAL));
                return true;
            case "Accept":
                if (num == -1) {
                    return true;
                }
                accept(dialog, memoryMap.get(MemKeys.LOCAL), num);
                return true;

        }

        return false;
    }

    public void next() {
        content.incrementNext();
        if (content.getNextSet() * content.getMaxConcurrent() + 1 > content.missions.size()) {
            content.resetNext();
        }
    }

    public void bountyAvailable(InteractionDialogAPI dialog, MemoryAPI memory) {

        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();

        content.initPicker();
        int maxconcurrent = content.getMaxConcurrent();
        int next = content.getNextSet();
        // dialog.getTextPanel().addParagraph("next"+next);
        //GladiatorSociety_BountyMission_V2 mis;
      //  content.missions.clear();
      /*  while ((content.createMission()) != null) {
            //   dialog.getTextPanel().addParagraph(""+mis.getMissionID());
        }*/
        // dialog.getTextPanel().addParagraph(""+content.missions.size());
        Iterator<GladiatorSociety_BountyData> iter = content.missions.iterator();
        int i = 0;
        while (next > i) {
            int n = 0;
            while (iter.hasNext() && n < maxconcurrent) {
                iter.next();
                n++;
                // dialog.getTextPanel().addParagraph("n"+n);
            }
            i++;
            //  dialog.getTextPanel().addParagraph("i"+i);

        }
        int groupNum = 0;
        while (iter.hasNext() && groupNum < maxconcurrent) {
            String optionId = "gladiator_Bounty" + groupNum;
            GladiatorSociety_BountyData dara=iter.next();
            opts.addOption(dara.fullname.getFullName()+" ("+dara.fleetPoints+")",
                    optionId, "Display the bounty");
            groupNum++;
        }

        opts.addOption("Next", "gladiator_Next", "Display the bounty");
        /*  if (content.getMaxConcurrent() <= content.missions.size()) {
                    opts.setEnabled("gladiator_Next", false);
        }*/
        String exitOpt = "gladiatorComRelay";

        opts.addOption(Misc.ucFirst("back"), exitOpt);
        opts.setShortcut(exitOpt, Keyboard.KEY_ESCAPE,
                false, false, false, false);

    }

    public void bountyDisplay(InteractionDialogAPI dialog, MemoryAPI memory, int num) {

        OptionPanelAPI opts = dialog.getOptionPanel();

        opts.clearOptions();

        GladiatorSociety_BountyData mission = content.missions.get(num + content.getMaxConcurrent() * content.getNextSet());

        dialog.getTextPanel().addParagraph("Reward: " + mission.bountyvalue + " credits");
        float point = Global.getSector().getPlayerFleet().getFleetPoints();
        float pointGS = mission.fleetPoints;
        
        dialog.getTextPanel().addParagraph("Estimated Comparaison force : You(" + point + ") Ennemi(" + pointGS + ")", Color.RED);
        //dialog.getTextPanel().highlightInLastPara(dangercolor, difmes);

        dialog.getTextPanel().addParagraph(mission.description);
        FactionAPI faction = Global.getSector().getFaction(mission.faction);
        if (faction != null) {
            dialog.getTextPanel().addParagraph("Faction: " + faction.getDisplayNameLongWithArticle());
        }
        dialog.getTextPanel().addParagraph("WARNING: The fleet will appear near you.", Color.RED);
        opts.addOption("Accept", "Accept" + num, "Accept");
        opts.setShortcut("Accept" + num, Keyboard.KEY_G,
                false, false, false, false);

        String exitOpt = "gladiatorDirectoryMain";

        opts.addOption(Misc.ucFirst("back"), exitOpt);
        opts.setShortcut(exitOpt, Keyboard.KEY_ESCAPE,
                false, false, false, false);
    }

    public void accept(InteractionDialogAPI dialog, MemoryAPI memory, int num) {
        OptionPanelAPI opts = dialog.getOptionPanel();

        opts.clearOptions();

        dialog.getTextPanel().addParagraph("The mission have be accepted");
        // Global.getSector().getCampaignUI().addMessage("GladiatorSociety_BountyMission playerAccept");
        GladiatorSociety_BountyData mission = content.missions.get(num + content.getMaxConcurrent() * content.getNextSet());
        //content.missions.get(num+content.getMaxConcurrent()*content.getNextSet()).playerAccept(Global.getSector().getPlayerFleet().getInteractionTarget());

        final SectorEntityToken entity = dialog.getInteractionTarget();
        final CampaignFleetAPI gladiatorfleet = spawnFleet(mission);
        content.missions.remove(mission);//num + content.getMaxConcurrent() * content.getNextSet());
        content.resetNext();
        content.currentMission = mission;
        dialog.setInteractionTarget(gladiatorfleet);

        final FleetInteractionDialogPluginImpl.FIDConfig config = new FleetInteractionDialogPluginImpl.FIDConfig();
        config.leaveAlwaysAvailable = false;
        config.showCommLinkOption = true;
        config.showEngageText = true;

        config.showFleetAttitude = false;
        config.showTransponderStatus = false;
        config.showWarningDialogWhenNotHostile = false;
        config.alwaysAttackVsAttack = true;
        config.impactsAllyReputation = false;
        config.impactsEnemyReputation = false;
        config.pullInAllies = false;
        config.pullInEnemies = false;
        config.pullInStations = false;
        config.lootCredits = false;
        config.firstTimeEngageOptionText = "Engage the gladiator fleet";
        config.afterFirstTimeEngageOptionText = "Re-engage the gladiator fleet";
        config.noSalvageLeaveOptionText = "Continue";
        config.dismissOnLeave = true;
        config.printXPToDialog = true;

        long seed = memory.getLong(MemFlags.SALVAGE_SEED);
        config.salvageRandom = Misc.getRandom(seed, 75);

        final FleetInteractionDialogPluginImpl plugin = new FleetInteractionDialogPluginImpl(config);
        final InteractionDialogPlugin originalPlugin = dialog.getPlugin();
        config.delegate = new FleetInteractionDialogPluginImpl.BaseFIDDelegate() {
            @Override
            public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
                bcc.aiRetreatAllowed = false;
                bcc.enemyDeployAll = true;
            }

            @Override
            public void notifyLeave(InteractionDialogAPI dialog) {
                // nothing in there we care about keeping; clearing to reduce savefile size
                gladiatorfleet.getMemoryWithoutUpdate().clear();
                // there's a "standing down" assignment given after a battle is finished that we don't care about
                gladiatorfleet.clearAssignments();
                gladiatorfleet.deflate();

                dialog.setPlugin(originalPlugin);
                dialog.setInteractionTarget(entity);

                //Global.getSector().getCampaignUI().clearMessages();
                if (plugin.getContext() instanceof FleetEncounterContext) {
                    FleetEncounterContext context = (FleetEncounterContext) plugin.getContext();
                    if (context.didPlayerWinEncounterOutright()) {
                       
                        int payment = (int) (content.currentMission.bountyvalue * context.getBattle().getPlayerInvolvementFraction());
                        Global.getSector().getPlayerFleet().getCargo().getCredits().add(payment);
                        content.addBountyDone(content.currentMission.missionid);
                    } else {
                        content.missions.add(content.currentMission);
                        dialog.dismiss();
                    }
                } else {
                    content.missions.add(content.currentMission);
                    dialog.dismiss();
                }
            }
        };
        dialog.setPlugin(plugin);
        plugin.init(dialog);

    }
    public static final String CUSTOMPORTRAIT = "customportrait";

    private PersonAPI initPerson(GladiatorSociety_BountyData bountydata) {
        PersonAPI person = OfficerManagerEvent.createOfficer(Global.getSector().getFaction(bountydata.faction), bountydata.flagship.number, true);
        // person = OfficerManagerEvent.createOfficer(Global.getSector().getFaction(GladiatorSociety_Constants.GSFACTION_ID), personLevel);
        if (!bountydata.avatar.isEmpty()) {
            person.setPortraitSprite(bountydata.avatar);

            if (person.getPortraitSprite() == null) {
                person.setFaction(CUSTOMPORTRAIT);
                person.setPortraitSprite(bountydata.avatar);
                person.setFaction(bountydata.faction);
            }

        }
        person.setPersonality(bountydata.flagship.personality);

        if (bountydata.fullname == null) {
            return person;
        }
        person.setName(bountydata.fullname);
        return person;
    }

    private CampaignFleetAPI spawnFleet(GladiatorSociety_BountyData bountydata) {
        PersonAPI person = initPerson(bountydata);

        String fleetName = person.getName().getLast() + "'s" + " Fleet";
        FleetParamsV3 params = new FleetParamsV3(
                null,
                null,
                bountydata.faction, // quality will always be reduced by non-market-faction penalty, which is what we want 
                null,
                FleetTypes.PERSON_BOUNTY_FLEET,
                bountydata.combatPoint, // combatPts
                0, // freighterPts 
                0, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                1f // qualityMod
        );
        params.ignoreMarketFleetSizeMult = true;

        CampaignFleetAPI fleet = GladiatorSociety_TinyFleetFactoryV2.createFleet(params, bountydata, person);
        if (fleet == null || fleet.isEmpty()) {
            return null;
        }

        fleet.setCommander(person);
        fleet.getFlagship().setCaptain(person);
        FleetFactoryV3.addCommanderSkills(person, fleet, null);

        Misc.makeImportant(fleet, GladiatorSociety_Constants.GSFACTION_ID, 120);
        //fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PIRATE, true);
        //fleet.getMemoryWithoutUpdate().set(MemFlags.FLEET_NO_MILITARY_RESPONSE, true);
        fleet.setNoFactionInName(true);
        fleet.setFaction(GladiatorSociety_Constants.GSFACTION_ID, true);
        fleet.setName(fleetName);
        //fleet.removeAbility(Abilities.INTERDICTION_PULSE);
        fleet.getAI().addAssignment(FleetAssignment.INTERCEPT, Global.getSector().getPlayerFleet(), 1000000f, null);
        fleet.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, true);
        fleet.getMemoryWithoutUpdate().set("$dialog", bountydata.dialog);
       // fleet.getInflater().setRemoveAfterInflating(false);

        
        return fleet;
    }
}
