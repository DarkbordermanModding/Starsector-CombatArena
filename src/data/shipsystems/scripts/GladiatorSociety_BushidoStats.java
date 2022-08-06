package src.data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.EnumSet;

public class GladiatorSociety_BushidoStats extends BaseShipSystemScript {

    public static final float ROF_BONUS = 1f;
    public static final float FLUX_REDUCTION = 50f;
    public static final Color JITTER_UNDER_COLOR = new Color(255,200,0,155);

    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {

        float mult = 1f + ROF_BONUS * effectLevel;
        stats.getBallisticRoFMult().modifyMult(id, mult);
        stats.getBallisticWeaponFluxCostMod().modifyPercent(id, -FLUX_REDUCTION);

        if (state == ShipSystemStatsScript.State.OUT) {
            stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
            stats.getMaxTurnRate().unmodify(id);
        } else {
            stats.getMaxSpeed().modifyFlat(id, 50f);
            stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
            stats.getDeceleration().modifyPercent(id, 200f * effectLevel);
            stats.getTurnAcceleration().modifyFlat(id, 30f * effectLevel);
            stats.getTurnAcceleration().modifyPercent(id, 200f * effectLevel);
            stats.getMaxTurnRate().modifyFlat(id, 15f);
            stats.getMaxTurnRate().modifyPercent(id, 100f);
        }

        if (stats.getEntity() instanceof ShipAPI) {

            ShipAPI ship = (ShipAPI) stats.getEntity();

            if (effectLevel > 0f) {
                ship.setWeaponGlow(effectLevel, Misc.setAlpha(JITTER_UNDER_COLOR, 255), EnumSet.of(WeaponType.BALLISTIC));
            }

            String key = ship.getId() + "_" + id;
            Object test = Global.getCombatEngine().getCustomData().get(key);
            if (state == State.IN) {
                if (test == null && effectLevel > 0.2f) {
                    Global.getCombatEngine().getCustomData().put(key, new Object());

                    ship.getHullSpec().getAllWeaponSlotsCopy().get(0).computePosition(ship);
                    for (WeaponSlotAPI weapon : ship.getHullSpec().getAllWeaponSlotsCopy()) {
                        if (weapon.isSystemSlot()) {
                            Global.getCombatEngine().spawnProjectile(ship, null, "flarelauncher1", weapon.computePosition(ship), weapon.getAngle() + ship.getFacing(), ship.getVelocity());
                        }

                    }

                }
            } else if (state == State.OUT && test != null) {
                Global.getCombatEngine().getCustomData().remove(key);

            }
        }
    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        stats.getBallisticRoFMult().unmodify(id);
        stats.getBallisticWeaponFluxCostMod().unmodify(id);
        stats.getMaxSpeed().unmodify(id);
        stats.getMaxTurnRate().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);
        stats.getAcceleration().unmodify(id);
        stats.getDeceleration().unmodify(id);
    }

    @Override
    public StatusData getStatusData(int index, State state, float effectLevel) {
        float mult = 1f + ROF_BONUS * effectLevel;
        float bonusPercent = (int) ((mult - 1f) * 100f);
        if (index == 0) {
            return new StatusData("ballistic rate of fire +" + (int) bonusPercent + "%", false);
        }
        if (index == 1) {
            return new StatusData("ballistic flux use -" + (int) FLUX_REDUCTION + "%", false);
        }
        if (index == 2) {
            return new StatusData("improved maneuverability", false);
        } else if (index == 3) {
            return new StatusData("+50 top speed", false);
        }
        return null;
    }
}
