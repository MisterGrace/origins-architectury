package io.github.edwinmindcraft.origins.api.origin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record OriginUpgrade(ResourceLocation advancement, ResourceLocation origin, String announcement) {
	public static final MapCodec<OriginUpgrade> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			ResourceLocation.CODEC.fieldOf("condition").forGetter(OriginUpgrade::advancement),
			ResourceLocation.CODEC.fieldOf("origin").forGetter(OriginUpgrade::origin),
			Codec.STRING.optionalFieldOf("announcement", "").forGetter(OriginUpgrade::announcement)
	).apply(instance, OriginUpgrade::new));

	public static final Codec<OriginUpgrade> CODEC = MAP_CODEC.codec();
}
