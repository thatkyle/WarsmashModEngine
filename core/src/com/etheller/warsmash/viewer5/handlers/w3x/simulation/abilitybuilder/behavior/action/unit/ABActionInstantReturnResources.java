package com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.action.unit;

import java.util.Map;

import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilities.harvest.CAbilityHarvest;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.behavior.callback.unitcallbacks.ABUnitCallback;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.abilitybuilder.core.ABAction;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.util.ResourceType;

public class ABActionInstantReturnResources implements ABAction {

	private ABUnitCallback unit;

	@Override
	public void runAction(CSimulation game, CUnit caster, Map<String, Object> localStore, final int castId) {
		CUnit targetUnit = caster;
		if (unit != null) {
			targetUnit = unit.callback(game, caster, localStore, castId);
		}
		
		CAbilityHarvest harv = targetUnit.getFirstAbilityOfType(CAbilityHarvest.class);
		if (harv != null && harv.getCarriedResourceType() != null && harv.getCarriedResourceAmount() > 0) {
			CPlayer pl = game.getPlayer(targetUnit.getPlayerIndex());
			switch(harv.getCarriedResourceType()) {
			case FOOD:
				// This might be a bad idea? Not sure it will ever matter
				pl.setFoodCap(Math.min(pl.getFoodCap() + harv.getCarriedResourceAmount(), pl.getFoodCapCeiling()));
				game.unitGainResourceEvent(targetUnit, pl.getId(),
						harv.getCarriedResourceType(),
						harv.getCarriedResourceAmount());
				harv.setCarriedResources(ResourceType.FOOD, 0);
				break;
			case GOLD:
				pl.addGold(harv.getCarriedResourceAmount());
				game.unitGainResourceEvent(targetUnit, pl.getId(),
						harv.getCarriedResourceType(),
						harv.getCarriedResourceAmount());
				harv.setCarriedResources(ResourceType.GOLD, 0);
				break;
			case LUMBER:
				pl.addLumber(harv.getCarriedResourceAmount());
				game.unitGainResourceEvent(targetUnit, pl.getId(),
						harv.getCarriedResourceType(),
						harv.getCarriedResourceAmount());
				harv.setCarriedResources(ResourceType.LUMBER, 0);
				break;
			case MANA:
				//??
				break;
			default:
				break;
			
			}
		}

	}

}
