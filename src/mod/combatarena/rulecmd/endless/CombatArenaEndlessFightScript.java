package mod.combatarena.rulecmd.endless;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.InteractionDialogPlugin;
import com.fs.starfarer.api.campaign.OptionPanelAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.MemFlags;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import mod.combatarena.utilities.CombatArenaFactory;
import mod.combatarena.utilities.CombatArenaRecord;

public class CombatArenaEndlessFightScript extends BaseCommandPlugin{

    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        accept(dialog, memoryMap.get(MemKeys.LOCAL));
        return true;
    }
    public void accept(InteractionDialogAPI dialog, MemoryAPI memory) {
        OptionPanelAPI opts = dialog.getOptionPanel();
        opts.clearOptions();
        dialog.getTextPanel().addParagraph("The mission have be accepted");
        final SectorEntityToken entity = dialog.getInteractionTarget();
        final CombatArenaRecord record = (CombatArenaRecord)Global.getSector().getPersistentData().get(
            CombatArenaRecord.COMBAT_ARENA_DATA_STORAGE_KEY
        );
        final CampaignFleetAPI endlessfleet = spawnFleet(record);

        dialog.setInteractionTarget(endlessfleet);

        final FleetInteractionDialogPluginImpl.FIDConfig config = new FleetInteractionDialogPluginImpl.FIDConfig();
        config.leaveAlwaysAvailable = true;
        config.showCommLinkOption = false;
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
        config.firstTimeEngageOptionText = "Engage the opponent fleet";
        config.afterFirstTimeEngageOptionText = "Re-engage the opponent fleet";
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
                endlessfleet.getMemoryWithoutUpdate().clear();
                // there's a "standing down" assignment given after a battle is finished that we don't care about
                endlessfleet.clearAssignments();
                endlessfleet.deflate();

                dialog.setPlugin(originalPlugin);
                dialog.setInteractionTarget(entity);
                //Global.getSector().getCampaignUI().clearMessages();
                if (plugin.getContext() instanceof FleetEncounterContext) {
                    FleetEncounterContext context = (FleetEncounterContext) plugin.getContext();
                    if(context.didPlayerWinEncounterOutright())
                    {
                        Global.getSector().getPlayerFleet().getCargo().getCredits().add(record.getCreditRewardAmount(record));
                        Global.getSector().getPlayerFleet().getCargo().addCommodity("arena_token", record.getArenaTokenRewardAmount(record));
                        record.increaseOpponentFleetPoint();
                        record.randomizeOpponentFaction();
                    } else {
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                }
            }
        };
        dialog.setPlugin(plugin);
        plugin.init(dialog);
    }

    private CampaignFleetAPI spawnFleet(CombatArenaRecord record) {

        FleetParamsV3 params = new FleetParamsV3(
                null,
                null,
                record.getOpponentFaction().getId(),
                null,
                FleetTypes.PERSON_BOUNTY_FLEET,
                record.getOpponentFleetPoint(), // combatPts
                0, // freighterPts
                0, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                1f // qualityMod
        );
        params.minShipSize = record.opponentMinShipSize;
        params.maxShipSize = record.opponentMaxShipSize;

        if(record.getOpponentMode().equals("fleetparam")){
            return CombatArenaFactory.createFleetByFleetParam(record);
        }
        else if(record.getOpponentMode().equals("fleetpoints")){
            return CombatArenaFactory.createFleetByFleetPoints(record);
        }
        //default
        return CombatArenaFactory.createFleetByFleetParam(record);
    }
}
