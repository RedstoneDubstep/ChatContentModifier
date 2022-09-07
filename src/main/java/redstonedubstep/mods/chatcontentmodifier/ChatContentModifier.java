package redstonedubstep.mods.chatcontentmodifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod("chatcontentmodifier")
@EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class ChatContentModifier {
	private static final Logger LOGGER = LogManager.getLogger();

	public ChatContentModifier() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ModifierConfig.SERVER_SPEC);
	}

	@SubscribeEvent
	public static void onServerStarted(ServerStartedEvent event) {
		if (!ServerLifecycleHooks.getCurrentServer().previewsChat() && !(ServerLifecycleHooks.getCurrentServer() instanceof DedicatedServer))
			ForgeMod.enableServerChatPreview();
	}

	@SubscribeEvent
	public static void onChatEvent(ServerChatEvent event) {
		String originalMessage = event.getMessage().getString();
		HashMap<String, List<String>> replacements = ModifierConfig.CONFIG.replacementMap;

		for (Map.Entry<String, List<String>> entry : replacements.entrySet()) {
			for (String key : entry.getValue())
				try {
					originalMessage = originalMessage.replaceAll(key, entry.getKey());
				} catch(PatternSyntaxException e) {
					if (event instanceof ServerChatEvent.Submitted)
						LOGGER.warn(e);
				}
		}

		event.setMessage(Component.literal(originalMessage));
	}
}
