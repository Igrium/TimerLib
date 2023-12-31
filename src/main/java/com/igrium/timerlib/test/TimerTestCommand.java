package com.igrium.timerlib.test;

import com.igrium.timerlib.Timers;
import com.igrium.timerlib.api.TimerProvider;
import com.igrium.timerlib.api.handle.IntervalHandle;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.text.Text;
import net.minecraft.server.command.ServerCommandSource;

import static net.minecraft.server.command.CommandManager.*;

public class TimerTestCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
            RegistrationEnvironment environment) {
        
        dispatcher.register(literal("timerlib").then(
            literal("timeout").then(
                argument("delay", IntegerArgumentType.integer(0)).executes(TimerTestCommand::doTimeout)
            )
        ).then(
            literal("interval").then(
                literal("start").then(
                    argument("delay", IntegerArgumentType.integer(1)).executes(TimerTestCommand::doInterval)
                )
            ).then(
                literal("stop").executes(TimerTestCommand::stopInterval)
            )
        ));
    }

    private static int doTimeout(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        TimerProvider provider = Timers.getWorld(context.getSource().getWorld());
        provider.setTimeout(IntegerArgumentType.getInteger(context, "delay"),
                () -> {
                    context.getSource().sendFeedback(() -> Text.literal("The timeout has timed out!"), false);
                });

        return 1;
    }

    private static IntervalHandle handle;

    private static int doInterval(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (handle != null) {
            handle.cancel();
        }

        TimerProvider provider = Timers.getWorld(context.getSource().getWorld());
        handle = provider.setInterval(IntegerArgumentType.getInteger(context, "delay"),
                () -> {
                    context.getSource().sendFeedback(() -> Text.literal("This is an interval!"), false);
                });
        return 1;
    }

    private static int stopInterval(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
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
