package redstonedubstep.mods.chatcontentmodifier.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.PatternSyntaxException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingPlayerChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import redstonedubstep.mods.chatcontentmodifier.ChatContentModifier;
import redstonedubstep.mods.chatcontentmodifier.ModifierConfig;

@Mixin({OutgoingPlayerChatMessage.Tracked.class, OutgoingPlayerChatMessage.NotTracked.class})
public class OutgoingPlayerChatMessageMixin {
	@ModifyVariable(method = "<init>", at = @At("HEAD"), argsOnly = true)
	private static PlayerChatMessage modifyChatMessage(PlayerChatMessage original) {
		String originalMessage = original.serverContent().getString();
		HashMap<String, List<String>> replacements = ModifierConfig.CONFIG.replacementMap;

		for (Map.Entry<String, List<String>> entry : replacements.entrySet()) {
			for (String key : entry.getValue())
				try {
					originalMessage = originalMessage.replaceAll(key, entry.getKey());
				} catch(PatternSyntaxException e) {
					ChatContentModifier.LOGGER.warn(e);
				}
		}

		if (!originalMessage.equals(original.serverContent().getString()))
			return new PlayerChatMessage(original.signedHeader(), original.headerSignature(), original.signedBody(), Optional.of(Component.literal(originalMessage)), original.filterMask());

		return original;
	}
}
