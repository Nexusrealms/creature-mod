package de.nexusrealms.creaturemod;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import de.nexusrealms.creaturemod.curses.Curse;
import de.nexusrealms.creaturemod.curses.CurseInstance;
import de.nexusrealms.creaturemod.curses.Curses;
import de.nexusrealms.creaturemod.curses.TherianthropyCurse;
import de.nexusrealms.creaturemod.magic.MagicChecks;
import de.nexusrealms.creaturemod.magic.flow.FlowUnit;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.Collection;
import java.util.List;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;
public class ModCommands {
    public static void init(){
        CommandRegistrationCallback.EVENT.register((commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
            commandDispatcher.register(literal("curse")
                    .then(literal("apply")
                            .then(argument("entities", EntityArgumentType.entities())
                                    .then(argument("curse", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, ModRegistries.Keys.CURSES))
                                            .then(argument("hidden", BoolArgumentType.bool())
                                                    .then(argument("shouldTick", BoolArgumentType.bool())
                                                            .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                                                            .executes(context -> {
                                                                Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                                                                for (Entity entity : entities) {
                                                                    if (entity instanceof LivingEntity living) {
                                                                        RegistryEntry.Reference<Curse> curse = RegistryEntryReferenceArgumentType.getRegistryEntry(context, "curse", ModRegistries.Keys.CURSES);
                                                                        CurseInstance curseInstance = new CurseInstance(curse, BoolArgumentType.getBool(context, "hidden"), BoolArgumentType.getBool(context, "shouldTick"));
                                                                        Curses.applyCurse(curseInstance, living);
                                                                        context.getSource().sendFeedback(() -> Text.translatable("message.creature-mod.curse.apply", curse.registryKey().getValue(), living.getName()), false);
                                                                    } else {
                                                                        context.getSource().sendError(Text.translatable("message.creature-mod.curse.nonliving"));
                                                                    }
                                                                }
                                                                return 1;
                                                            }))))))
                    .then(literal("clear")
                            .then(argument("entities", EntityArgumentType.entities())
                                    .then(argument("curse", RegistryKeyArgumentType.registryKey(ModRegistries.Keys.CURSES))
                                            .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                                            .executes(context -> {
                                                Collection<? extends Entity> entities = EntityArgumentType.getEntities(context, "entities");
                                                for (Entity entity : entities) {
                                                    if (entity instanceof LivingEntity living) {
                                                        RegistryKey<Curse> curse = context.getArgument("curse", RegistryKey.class);
                                                        Curses.removeAllCurses(curseInstance -> curseInstance.getType().matchesKey(curse), living);
                                                        context.getSource().sendFeedback(() -> Text.translatable("message.creature-mod.curse.remove", curse.getValue()).append(living.getName()), false);
                                                    } else {
                                                        context.getSource().sendError(Text.translatable("message.creature-mod.curse.nonliving"));
                                                    }
                                                }
                                                return 1;
                                            }))))
                    .then(literal("list")
                            .then(argument("entity", EntityArgumentType.entity())
                                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                                    .executes(context -> {
                                        if(EntityArgumentType.getEntity(context, "entity") instanceof LivingEntity living){
                                            List<CurseInstance> curseInstances = Curses.getCurses(living);
                                            if(curseInstances.isEmpty()){
                                                context.getSource().sendError(Text.translatable("message.creature-mod.curse.none"));
                                            }
                                            curseInstances.forEach(curseInstance -> context.getSource().sendFeedback(() -> Text.literal(curseInstance.getType().getIdAsString()), false));
                                        } else {
                                            context.getSource().sendError(Text.translatable("message.creature-mod.curse.nonliving"));
                                        }
                                        return 1;
                                    }))));
            if(FabricLoader.getInstance().isDevelopmentEnvironment()){
                commandDispatcher.register(literal("therianthropyon")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .executes(commandContext -> {
                            TherianthropyCurse.transformPlayer(commandContext.getSource().getPlayer());
                            return 1;
                        }));
                commandDispatcher.register(literal("therianthropyoff")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .executes(commandContext -> {
                            TherianthropyCurse.untransformPlayer(commandContext.getSource().getPlayer());
                            return 1;
                        }));
                commandDispatcher.register(literal("flowdump")
                        .requires(ServerCommandSource::isExecutedByPlayer)
                        .executes(commandContext -> {
                            commandContext.getSource().sendFeedback(() -> Text.literal(commandContext.getSource().getPlayer().getComponent(ModEntityComponents.PLAYER_FLOW_STORAGE).dumpFlow()), false);
                            return 1;
                        }));
            }
            commandDispatcher.register(literal("flow")
                    .then(literal("add")
                            .then(argument("players", EntityArgumentType.players())
                                    .then(argument("element", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, ModRegistries.Keys.ELEMENTS))
                                            .then(argument("amount", IntegerArgumentType.integer(0))
                                                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                                                    .executes(commandContext -> {
                                                        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(commandContext, "players");
                                                        FlowUnit flowUnit = FlowUnit.of(RegistryEntryReferenceArgumentType.getRegistryEntry(commandContext, "element",
                                                                ModRegistries.Keys.ELEMENTS), IntegerArgumentType.getInteger(commandContext, "amount"));
                                                        MagicChecks.doIfDoesSorcery(players, player -> player.getComponent(ModEntityComponents.PLAYER_FLOW_STORAGE).addFlow(flowUnit));
                                                        commandContext.getSource().sendFeedback(() -> Text.translatable("message.creature-mod.flow.add", flowUnit.getElement().getIdAsString(), flowUnit.getValue(), players.size()), false);
                                                        return 1;
                                                    }))))));
        });
    }
}
