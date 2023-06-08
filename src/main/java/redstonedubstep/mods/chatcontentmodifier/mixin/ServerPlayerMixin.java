package redstonedubstep.mods.chatcontentmodifier.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.OutgoingChatMessage.Disguised;
import net.minecraft.network.chat.OutgoingChatMessage.Player;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.level.ServerPlayer;
import redstonedubstep.mods.chatcontentmodifier.ChatContentModifier;
import redstonedubstep.mods.chatcontentmodifier.ModifierConfig;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {
	@ModifyVariable(method = "sendChatMessage", at = @At("HEAD"), argsOnly = true)
	private OutgoingChatMessage chatcontentmodifier$modifyChatMessage(OutgoingChatMessage original) {
		String originalMessage = original.content().getString();
		HashMap<String, List<String>> replacements = ModifierConfig.CONFIG.replacementMap;

		for (Map.Entry<String, List<String>> entry : replacements.entrySet()) {
			for (String key : entry.getValue())
				try {
					originalMessage = originalMessage.replaceAll(key, entry.getKey());
				} catch(PatternSyntaxException e) {
					ChatContentModifier.LOGGER.warn(e);
				}
		}

		if (!originalMessage.equals(original.content().getString())) {
			if (original instanceof Player playerChatMessage)
				return new Player(new PlayerChatMessage(playerChatMessage.message().link(), playerChatMessage.message().signature(), playerChatMessage.message().signedBody(), Component.literal(originalMessage), ((Player) original).message().filterMask()));
			else
				return new Disguised(Component.literal(originalMessage));
		}

		return original;
	}
}
