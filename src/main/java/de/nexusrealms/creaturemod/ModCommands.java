package de.nexusrealms.creaturemod;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import de.nexusrealms.creaturemod.curses.Curse;
import de.nexusrealms.creaturemod.curses.CurseInstance;
import de.nexusrealms.creaturemod.curses.Curses;
import de.nexusrealms.creaturemod.curses.TherianthropyCurse;
import de.nexusrealms.creaturemod.items.ModItemComponents;
import de.nexusrealms.creaturemod.magic.flow.FlowStorage;
import de.nexusrealms.creaturemod.magic.flow.FlowUnit;
import de.nexusrealms.creaturemod.magic.spell.Incantation;
import de.nexusrealms.creaturemod.magic.spell.Spell;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryReferenceArgumentType;
import net.minecraft.command.argument.RegistryKeyArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
                            commandContext.getSource().sendFeedback(() -> Text.literal(FlowStorage.getFlowStorage(commandContext.getSource().getPlayer()).dumpFlow()), false);
                            return  1;
                        }));
            }
            commandDispatcher.register(literal("flow")
                    .then(literal("add")
                            .then(literal("item")
                                    .then(argument("element", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, ModRegistries.Keys.ELEMENTS))
                                            .then(argument("amount", IntegerArgumentType.integer(0))
                                                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4) && serverCommandSource.isExecutedByPlayer())
                                                    .executes(commandContext -> {
                                                        FlowUnit flowUnit = FlowUnit.of(RegistryEntryReferenceArgumentType.getRegistryEntry(commandContext, "element",
                                                                ModRegistries.Keys.ELEMENTS), IntegerArgumentType.getInteger(commandContext, "amount"));
                                                        ItemStack stack = commandContext.getSource().getPlayer().getStackInHand(Hand.MAIN_HAND);
                                                        if(stack.contains(ModItemComponents.FLOW_CAPACITY)){
                                                            stack.set(ModItemComponents.STORED_FLOW, flowUnit);
                                                        }
                                                        commandContext.getSource().sendFeedback(() -> Text.translatable("message.creature-mod.flow.add.item", flowUnit.getElement().getIdAsString(), flowUnit.getValue()).append(stack.getName()), false);
                                                        return 1;
                                                    }))))
                            .then(argument("players", EntityArgumentType.players())
                                    .then(argument("element", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, ModRegistries.Keys.ELEMENTS))
                                            .then(argument("amount", IntegerArgumentType.integer(0))
                                                    .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                                                    .executes(commandContext -> {
                                                        Collection<ServerPlayerEntity> players = EntityArgumentType.getPlayers(commandContext, "players");
                                                        FlowUnit flowUnit = FlowUnit.of(RegistryEntryReferenceArgumentType.getRegistryEntry(commandContext, "element",
                                                                ModRegistries.Keys.ELEMENTS), IntegerArgumentType.getInteger(commandContext, "amount"));
                                                        players.stream().map(FlowStorage::getFlowStorage).forEach(flowStorage -> flowStorage.addFlow(flowUnit));
                                                        commandContext.getSource().sendFeedback(() -> Text.translatable("message.creature-mod.flow.add", flowUnit.getElement().getIdAsString(), flowUnit.getValue(), players.size()), false);
                                                        return 1;
                                                    }))))));
            commandDispatcher.register(literal("castspell")
                    .then(argument("spell", RegistryEntryReferenceArgumentType.registryEntry(commandRegistryAccess, ModRegistries.Keys.SPELLS))
                            .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4) && serverCommandSource.isExecutedByPlayer())
                            .executes(commandContext -> {
                                RegistryEntry<Spell> spell = RegistryEntryReferenceArgumentType.getRegistryEntry(commandContext, "spell", ModRegistries.Keys.SPELLS);
                                Spell.castDirect(commandContext.getSource().getPlayer(), spell);
                                commandContext.getSource().sendFeedback(() -> Text.translatable("message.creature-mod.spell.cast", spell.getIdAsString()), false);
                                return 1;
                            })));
            commandDispatcher.register(literal("incantation")
                    .then(argument("words", StringArgumentType.string())
                            .suggests((commandContext, suggestionsBuilder) -> {
                                commandRegistryAccess.getWrapperOrThrow(ModRegistries.Keys.SPELLS).streamEntries().map(r -> '"' + r.value().incantation().words() + '"').forEach(suggestionsBuilder::suggest);
                                return suggestionsBuilder.buildFuture();
                            })
                            .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4) && serverCommandSource.isExecutedByPlayer())
                            .executes(commandContext -> {
                                String words = StringArgumentType.getString(commandContext, "words");
                                Optional<RegistryEntry<Spell>> optionalSpell = Incantation.lookup(commandRegistryAccess, words);
                                if(optionalSpell.isPresent()){
                                    Spell.castDirect(commandContext.getSource().getPlayer(), optionalSpell.get());
                                    commandContext.getSource().sendFeedback(() -> Text.translatable("message.creature-mod.spell.cast", optionalSpell.get().getIdAsString()), false);
                                } else {
                                    commandContext.getSource().sendError(Text.translatable("message.creature-mod.spell.notfound", words));
                                }
                                return 1;
                            })));
        });
    }
}
