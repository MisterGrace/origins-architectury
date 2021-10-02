package io.github.apace100.origins.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.edwinmindcraft.origins.api.capabilities.IOriginContainer;
import io.github.edwinmindcraft.origins.api.origin.OriginLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;

import java.util.List;

public class WaitForNextLayerScreen extends Screen {

	private final List<OriginLayer> layerList;
	private final int currentLayerIndex;
	private final boolean showDirtBackground;
	private final int maxSelection;

	protected WaitForNextLayerScreen(List<OriginLayer> layerList, int currentLayerIndex, boolean showDirtBackground) {
		super(TextComponent.EMPTY);
		this.layerList = layerList;
		this.currentLayerIndex = currentLayerIndex;
		this.showDirtBackground = showDirtBackground;
		Player player = Minecraft.getInstance().player;
		OriginLayer currentLayer = layerList.get(currentLayerIndex);
		this.maxSelection = currentLayer.getOriginOptionCount(player);
	}

	public void openSelection() {
		int index = this.currentLayerIndex + 1;
		Player player = Minecraft.getInstance().player;
		LazyOptional<IOriginContainer> iOriginContainerLazyOptional = IOriginContainer.get(player);
		if (!iOriginContainerLazyOptional.isPresent()) {
			Minecraft.getInstance().setScreen(null);
			return;
		}
		IOriginContainer component = iOriginContainerLazyOptional.orElseThrow(RuntimeException::new);
		while (index < this.layerList.size()) {
			if (!component.hasOrigin(this.layerList.get(index)) && this.layerList.get(index).origins(player).size() > 0) {
				Minecraft.getInstance().setScreen(new ChooseOriginScreen(this.layerList, index, this.showDirtBackground));
				return;
			}
			index++;
		}
		Minecraft.getInstance().setScreen(null);
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		if (this.maxSelection == 0) {
			this.openSelection();
			return;
		}
		this.renderBackground(matrices);
	}

	@Override
	public void renderBackground(PoseStack matrices, int vOffset) {
		if (this.showDirtBackground) {
			super.renderDirtBackground(vOffset);
		} else {
			super.renderBackground(matrices, vOffset);
		}
	}
}
