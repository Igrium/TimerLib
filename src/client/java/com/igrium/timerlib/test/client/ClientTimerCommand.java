package com.igrium.timerlib.test.client;

import com.igrium.timerlib.ClientTimers;
import com.igrium.timerlib.api.TimerProvider;
import com.igrium.timerlib.api.handle.IntervalHandle;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;

import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class ClientTimerCommand {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(literal("clienttimer").then(
            literal("timeout").then(
                argument("time", IntegerArgumentType.integer()).executes(ClientTimerCommand::doTimeout)
            )
        ).then(
            literal("interval").then(
                literal("start").then(
                    argument("duration", IntegerArgumentType.integer(1)).executes(ClientTimerCommand::doInterval)
                )
            ).then(
                literal("stop").executes(ClientTimerCommand::stopInterval)
            )
        ));
    }

    private static TimerProvider getProvider(FabricClientCommandSource source) {
        return ClientTimers.getClientFrame(source.getClient());
    }
    
    private static int doTimeout(CommandContext<FabricClientCommandSource> context) {
        int duration = IntegerArgumentType.getInteger(context, "time");

        TimerProvider provider = getProvider(context.getSource());

        provider.setTimeout(duration, () -> {
            context.getSource().sendFeedback(Text.literal("The timer has expired!"));
        });

        return 1;
    }

    private static IntervalHandle handle;

    private static int doInterval(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        if (handle != null) {
            handle.cancel();
        }

        TimerProvider provider = getProvider(context.getSource());
        handle = provider.setInterval(IntegerArgumentType.getInteger(context, "duration"), () -> {
            // context.getSource().sendFeedback(Text.literal("The timer has expired!"));
            LogUtils.getLogger().info("The timer has expired!");;
        });

        return 1;
    }

    private static int stopInterval(CommandContext<FabricClientCommandSource> context) throws CommandSyntaxException {
        try {
            if (handle == null)
                return 0;

            handle.cancel();
            handle = null;
            return 1;
        } catch (Exception e) {
            LogUtils.getLogger().error("Error stopping interval", e);
            throw e;
        }
    }
}
