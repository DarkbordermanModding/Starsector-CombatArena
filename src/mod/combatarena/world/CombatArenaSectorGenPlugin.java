package mod.combatarena.world;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;

import mod.combatarena.utilities.CombatArenaRecord;

public class CombatArenaSectorGenPlugin extends BaseModPlugin {

    private void createCombatArenaFaction(){
        SectorAPI sector = Global.getSector();
        FactionAPI player = sector.getFaction(Factions.PLAYER);
        FactionAPI combat_arena = sector.getFaction(mod.combatarena.world.Factions.COMBAT_ARENA);
        for (FactionAPI faction : Global.getSector().getAllFactions()) {
            if (faction != player) {
                faction.setRelationship(combat_arena.getId(), RepLevel.SUSPICIOUS);
            }
        }
        player.setRelationship(combat_arena.getId(), RepLevel.VENGEFUL);

        for(ShipHullSpecAPI spec: Global.getSettings().getAllShipHullSpecs()){
            combat_arena.addKnownShip(spec.getHullId(), true);
        }
        for(HullModSpecAPI spec: Global.getSettings().getAllHullModSpecs()){
            combat_arena.addKnownHullMod(spec.getId());
        }
        for(WeaponSpecAPI spec: Global.getSettings().getAllWeaponSpecs()){
            combat_arena.addKnownWeapon(spec.getWeaponId(), true);
        }
        combat_arena.setAutoEnableKnownHullmods(true);
        combat_arena.setAutoEnableKnownShips(true);
        combat_arena.setAutoEnableKnownWeapons(true);

        CombatArenaRecord record = new CombatArenaRecord();
        Global.getSector().getPersistentData().put(CombatArenaRecord.COMBAT_ARENA_DATA_STORAGE_KEY, record);
    }

    public void onNewGame() {
        createCombatArenaFaction();
    }

    public void onGameLoad(boolean newGame){
        // Mid-save compatibility
        FactionAPI combat_arena = Global.getSector().getFaction(mod.combatarena.world.Factions.COMBAT_ARENA);
        if(combat_arena == null){
            Global.getSettings().createBaseFaction(mod.combatarena.world.Factions.COMBAT_ARENA);
            createCombatArenaFaction();
        }
    }
}
