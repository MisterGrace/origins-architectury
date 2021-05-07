package io.github.apace100.origins.origin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import io.github.apace100.origins.Origins;
import io.github.apace100.origins.integration.OriginEventsArchitectury;
import io.github.apace100.origins.util.MultiJsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.List;
import java.util.Map;

public class OriginManager extends MultiJsonDataLoader {
	
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

	public OriginManager() {
		super(GSON, "origins");
	}

	private static void fireLoadingEvent(Origin origin) {
		OriginEventsArchitectury.ORIGIN_LOADING.invoker().onLoad(origin);
	}

	@Override
	protected void apply(Map<Identifier, List<JsonElement>> loader, ResourceManager manager, Profiler profiler) {
		OriginRegistry.reset();
		loader.forEach((id, jel) -> jel.forEach(je -> {
			try {
				Origin origin = Origin.fromJson(id, je.getAsJsonObject());
				fireLoadingEvent(origin);
				if(!OriginRegistry.contains(id)) {
					OriginRegistry.register(id, origin);
				} else {
					if(OriginRegistry.get(id).getLoadingPriority() < origin.getLoadingPriority()) {
						OriginRegistry.update(id, origin);
					}
				}
			} catch(Exception e) {
				Origins.LOGGER.error("There was a problem reading Origin file " + id.toString() + " (skipping): " + e.getMessage());
			}
		}));
		Origins.LOGGER.info("Finished loading origins from data files. Registry contains " + OriginRegistry.size() + " origins.");
		OriginEventsArchitectury.ORIGINS_LOADED.invoker().onDataLoaded(false);
	}

	/*@Override*/
	public Identifier getFabricId() {
		return new Identifier(Origins.MODID, "origins");
	}
}
