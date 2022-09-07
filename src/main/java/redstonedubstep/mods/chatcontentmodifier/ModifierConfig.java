package redstonedubstep.mods.chatcontentmodifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@EventBusSubscriber(bus = Bus.MOD, value = Dist.DEDICATED_SERVER)
public class ModifierConfig {
	public static final ForgeConfigSpec SERVER_SPEC;
	public static final Config CONFIG;

	static {
		final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);

		SERVER_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	public static class Config {
		public ConfigValue<List<AbstractCommentedConfig>> chatReplacements;
		public HashMap<String, List<String>> replacementMap;

		Config(ForgeConfigSpec.Builder builder) {
			chatReplacements = builder
					.comment(" --- ChatContentModifier Config File --- ",
							"A list of all chat message replacements that should be applied to player chat messages.",
							"Multiple entries can be added to this config, and regexes are supported.",
							"An individual config entry needs a list of \"keys\", which are one or more strings that should be looked out for, and a \"replacement\" string that replaces one of the keys.",
							"A very basic replacement config that automatically applies correct spellings could look like this:",
							"[[chatReplacements]]",
							"	keys = [\"theyre\", \"theire\"]",
							"	replacement = \"they're\"",
							"",
							"[[chatReplacements]]",
							"	keys = [\"Im\"]",
							"	replacement = \"I'm\"")
					.define("chatReplacements", new ArrayList<>());
		}
	}

	@SubscribeEvent
	public static void onConfigUpdate(ModConfigEvent event) {
		List<AbstractCommentedConfig> configList = CONFIG.chatReplacements.get();
		HashMap<String, List<String>> replacementMap = new HashMap<>();

		for (AbstractCommentedConfig config : configList) {
			String replacement = config.get("replacement");
			ArrayList<String> keys = config.get("keys");

			replacementMap.put(replacement, keys);
		}

		CONFIG.replacementMap = replacementMap;
	}
}
