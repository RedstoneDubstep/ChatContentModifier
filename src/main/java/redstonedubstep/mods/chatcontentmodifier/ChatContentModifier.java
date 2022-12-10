package redstonedubstep.mods.chatcontentmodifier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.IExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.network.NetworkConstants;

@Mod("chatcontentmodifier")
public class ChatContentModifier {
	public static final Logger LOGGER = LogManager.getLogger();

	public ChatContentModifier() {
		ModLoadingContext.get().registerExtensionPoint(IExtensionPoint.DisplayTest.class, () -> new IExtensionPoint.DisplayTest(() -> NetworkConstants.IGNORESERVERONLY, (a, b) -> true));
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ModifierConfig.SERVER_SPEC);
	}
}
