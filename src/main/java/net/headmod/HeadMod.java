package net.headmod;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.UUID;

public class HeadMod implements ModInitializer {
    @Override
    public void onInitialize() {
        // Команда открытия меню (через чат)
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("headmenu")
                .executes(context -> {
                    Text msg = Text.literal("\n [ ПОИСК ГОЛОВЫ: НАЖМИ ТУТ ] \n")
                        .formatted(Formatting.AQUA, Formatting.BOLD)
                        .styled(s -> s.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gethead "))
                                     .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Вставить Base64"))));
                    context.getSource().sendFeedback(() -> msg, false);
                    return 1;
                }));

            // Команда выдачи
            dispatcher.register(CommandManager.literal("gethead")
                .then(CommandManager.argument("b64", StringArgumentType.greedyString())
                .executes(context -> {
                    String b64 = StringArgumentType.getString(context, "b64");
                    var player = context.getSource().getPlayer();
                    if (player != null) {
                        ItemStack head = new ItemStack(Items.PLAYER_HEAD);
                        UUID id = UUID.nameUUIDFromBytes(b64.getBytes());
                        GameProfile profile = new GameProfile(id, "CustomHead");
                        profile.getProperties().put("textures", new Property("textures", b64));
                        head.set(DataComponentTypes.PROFILE, new ProfileComponent(profile));
                        player.getInventory().insertStack(head);
                    }
                    return 1;
                })));
        });
    }
}