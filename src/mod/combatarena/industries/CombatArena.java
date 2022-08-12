package mod.combatarena.industries;

import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD.RaidDangerLevel;
import com.fs.starfarer.api.util.Pair;


public class CombatArena extends BaseIndustry {

	public void apply() {
		super.apply(true);

		int size = market.getSize();
		demand(Commodities.CREW, size);
		demand(Commodities.SHIPS, size-2);
		supply(Commodities.ORGANS, size-1);
		supply(Commodities.SHIP_WEAPONS, size-1);
		supply(Commodities.METALS, size);
		supply(Commodities.HEAVY_MACHINERY, size-1);

		Pair<String, Integer> deficit = getMaxDeficit(Commodities.SHIPS);
		applyDeficitToProduction(1, deficit,Commodities.ORGANS);
		applyDeficitToProduction(1, deficit,Commodities.SHIP_WEAPONS);
		applyDeficitToProduction(1, deficit,Commodities.METALS);
		applyDeficitToProduction(1, deficit,Commodities.HEAVY_MACHINERY);

		if (!isFunctional()) {
			supply.clear();
		}
	}

	@Override
	public void unapply() {
		super.unapply();
	}

	@Override
	public RaidDangerLevel adjustCommodityDangerLevel(String commodityId, RaidDangerLevel level) {
		return level.next();
	}

	@Override
	public RaidDangerLevel adjustItemDangerLevel(String itemId, String data, RaidDangerLevel level) {
		return level.next();
	}

	@Override
	protected boolean canImproveToIncreaseProduction() {
		return true;
	}
}
