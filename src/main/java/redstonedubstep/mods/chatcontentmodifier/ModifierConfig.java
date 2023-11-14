package redstonedubstep.mods.chatcontentmodifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.electronwill.nightconfig.core.AbstractCommentedConfig;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.DEDICATED_SERVER)
public class ModifierConfig {
	public static final ModConfigSpec SERVER_SPEC;
	public static final Config CONFIG;

	static {
		final Pair<Config, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(Config::new);

		SERVER_SPEC = specPair.getRight();
		CONFIG = specPair.getLeft();
	}

	public static class Config {
		public ConfigValue<List<AbstractCommentedConfig>> chatReplacements;
		public HashMap<String, List<String>> replacementMap;

		Config(ModConfigSpec.Builder builder) {
			chatReplacements = builder
					.comment(" --- ChatContentModifier Config File --- ",
							"A list of all chat message replacements that should be applied to player chat messages.",
							"Multiple entries can be added to this config, and regexes are supported.",
							"An individual config entry needs a list of \"keys\", which are one or more strings that should be looked out for, and a \"replacement\" string that replaces one of the keys.",
							"Additionally, the value \"ignore_case\" can be added to each config, which, being false by default, specifies whether the \"keys\" should be matched without case-sensitivity.",
							"A very basic replacement config that automatically applies correct spellings could look like this:",
							"[[chatReplacements]]",
							"	keys = [\"theyre\", \"theire\"]",
							"	replacement = \"they're\"",
							"",
							"[[chatReplacements]]",
							"	keys = [\"Im\"]",
							"	replacement = \"I'm\"",
							"	ignore_case = true")
					.define("chatReplacements", new ArrayList<>());
		}
	}

	@SubscribeEvent
	public static void onConfigUpdate(ModConfigEvent event) {
		List<AbstractCommentedConfig> configList = CONFIG.chatReplacements.get();
		HashMap<String, List<String>> replacementMap = new HashMap<>();

		for (AbstractCommentedConfig config : configList) {
			List<String> keys = config.get("keys");
			String replacement = config.get("replacement");

			if (config.getOrElse("ignore_case", false))
				keys = keys.stream().map(s -> "(?i)" + s).toList();

			replacementMap.put(replacement, keys);
		}

		CONFIG.replacementMap = replacementMap;
	}
}
