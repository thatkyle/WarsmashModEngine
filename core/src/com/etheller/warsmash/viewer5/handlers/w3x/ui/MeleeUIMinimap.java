package com.etheller.warsmash.viewer5.handlers.w3x.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.etheller.warsmash.viewer5.handlers.w3x.environment.PathingGrid;
import com.etheller.warsmash.viewer5.handlers.w3x.rendersim.RenderUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CSimulation;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.CUnit;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CAllianceType;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.CPlayer;
import com.etheller.warsmash.viewer5.handlers.w3x.simulation.players.vision.CPlayerFogOfWar;

public class MeleeUIMinimap {
	private static final float HERO_STEP = 0.01f;
	private final Rectangle minimap;
	private final Rectangle minimapFilledArea;
	private final Texture minimapTexture;
	private final Rectangle playableMapArea;
	private final Texture[] teamColors;
	private Texture[] specialIcons;
	private float heroAlpha = 0.90f;
	private byte polarity = -1;

	public MeleeUIMinimap(final Rectangle displayArea, final Rectangle playableMapArea, final Texture minimapTexture,
			final Texture[] teamColors, final Texture[] specialIcons) {
		this.playableMapArea = playableMapArea;
		this.minimapTexture = minimapTexture;
		this.teamColors = teamColors;
		this.minimap = displayArea;
		this.specialIcons = specialIcons;
		final float worldWidth = playableMapArea.getWidth();
		final float worldHeight = playableMapArea.getHeight();
		final float worldSize = Math.max(worldWidth, worldHeight);
		final float minimapFilledWidth = (worldWidth / worldSize) * this.minimap.width;
		final float minimapFilledHeight = (worldHeight / worldSize) * this.minimap.height;

		this.minimapFilledArea = new Rectangle(this.minimap.x + ((this.minimap.width - minimapFilledWidth) / 2),
				this.minimap.y + ((this.minimap.height - minimapFilledHeight) / 2), minimapFilledWidth,
				minimapFilledHeight);
	}

	public void render(final CSimulation game, final SpriteBatch batch, final Iterable<RenderUnit> units, final PathingGrid pathingGrid, final CPlayerFogOfWar fogOfWar, final CPlayer player) {
		batch.draw(this.minimapTexture, this.minimap.x, this.minimap.y, this.minimap.width, this.minimap.height);
		Color og = batch.getColor();
		
		final int minX = pathingGrid.getFogOfWarIndexX(playableMapArea.getX());
		final int minY = pathingGrid.getFogOfWarIndexY(playableMapArea.getY());
		final int maxX = pathingGrid.getFogOfWarIndexX(playableMapArea.getX() + playableMapArea.getWidth());
		final int maxY = pathingGrid.getFogOfWarIndexY(playableMapArea.getY() + playableMapArea.getHeight());
		float mapXMod = this.minimapFilledArea.width / (maxX - minX);
		float mapYMod = this.minimapFilledArea.height / (maxY - minY);
		
		for (int y = 0; y < (maxY - minY); y++) {
			for (int x =  0; x < (maxX - minX); x++) {
				byte state = fogOfWar.getState(x+minX, y+minY);
				if (state > 0) {
					batch.setColor(0f,0f,0f,0.5f);
					batch.draw(this.teamColors[0],
							this.minimapFilledArea.x + x * mapXMod,
							this.minimapFilledArea.y + y * mapYMod,
							mapXMod, mapYMod);
				} else if (state < 0) {
					batch.setColor(0f,0f,0f,1f);
					batch.draw(this.teamColors[0],
							this.minimapFilledArea.x + x * mapXMod,
							this.minimapFilledArea.y + y * mapYMod,
							mapXMod, mapYMod);
				}
			}
		}
		
		heroAlpha += HERO_STEP * polarity;
		if (heroAlpha <= 0.5 || heroAlpha >= 0.95) {
			polarity *= -1;
		}

		for (final RenderUnit unit : units) {
			CUnit simUnit = unit.getSimulationUnit();
			int dimensions = 4;
			if (!simUnit.isHidden() && !simUnit.isDead() && simUnit.isVisible(game, player.getId())) {
				batch.setColor(1,1,1,1);
				final Texture minimapIcon;
				if (simUnit.getGoldMineData() != null) {
					minimapIcon = this.specialIcons[0];
					dimensions = 21;
				} else if (simUnit.getNeutralBuildingData() != null) {
					minimapIcon = this.specialIcons[1];
					dimensions = 21;
				} else if (simUnit.isHero()) {
					if (player.hasAlliance(simUnit.getPlayerIndex(), CAllianceType.PASSIVE)) {
						batch.setColor(1f, 1f, 1f, heroAlpha);
					} else {
//						Color pc = new Color(game.getPlayer(simUnit.getPlayerIndex()).getColor());
						batch.setColor(1f, 0.2f, 0.2f, heroAlpha);
					}
					minimapIcon = this.specialIcons[2];
					dimensions = 28;
				} else {
					if (simUnit.isBuilding()) {
						dimensions = 10;
					}
					minimapIcon = this.teamColors[unit.getSimulationUnit().getPlayerIndex()];
				}
				int offset = dimensions / 2;
				batch.draw(minimapIcon,
						this.minimapFilledArea.x + (((unit.location[0] - this.playableMapArea.getX())
								/ (this.playableMapArea.getWidth())) * this.minimapFilledArea.width) - offset,
						this.minimapFilledArea.y + (((unit.location[1] - this.playableMapArea.getY())
								/ (this.playableMapArea.getHeight())) * this.minimapFilledArea.height) - offset,
						dimensions, dimensions);
				batch.setColor(og);
			}
		}
	}

	public Vector2 getWorldPointFromScreen(final float screenX, final float screenY) {
		final Rectangle filledArea = this.minimapFilledArea;
		final float clickX = (screenX - filledArea.x) / filledArea.width;
		final float clickY = (screenY - filledArea.y) / filledArea.height;
		final float worldX = (clickX * this.playableMapArea.width) + this.playableMapArea.x;
		final float worldY = (clickY * this.playableMapArea.height) + this.playableMapArea.y;
		return new Vector2(worldX, worldY);

	}

	public boolean containsMouse(final float x, final float y) {
		return this.minimapFilledArea.contains(x, y);
	}
}
