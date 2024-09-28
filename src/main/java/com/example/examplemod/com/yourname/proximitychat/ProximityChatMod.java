package com.example.examplemod.com.yourname.proximitychat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.mumblelink.MumbleLink;  // Assuming you have MumbleLink in your mod's dependencies

import java.util.HashMap;
import java.util.UUID;

@Mod(ProximityChatMod.MODID)
public class ProximityChatMod {
    public static final String MODID = "proximitychat";

    // Store last known proximity of players
    private final HashMap<UUID, Float> playerVolumeMap = new HashMap<>();
    private final MumbleLink mumbleLink = new MumbleLink(); // Initialize MumbleLink

    public ProximityChatMod() {
        // Register the setup method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Common setup code can go here
    }

    @SubscribeEvent
    public void onServerStarted(FMLServerStartedEvent event) {
        // Server started event (if you need to initialize something at server start)
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        World world = player.world;

        // Only on the client-side (where MumbleLink works)
        if (world.isRemote) {
            // Update the player’s position and orientation for MumbleLink
            updateMumbleLink(player);

            for (PlayerEntity otherPlayer : world.getPlayers()) {
                if (!player.equals(otherPlayer)) {
                    double distance = player.getDistance(otherPlayer);
                    if (distance <= 50) { // If players are within 50 blocks

                        // Call your voice chat logic here to enable/adjust volume
                        float volume = (float) Math.max(0.1, 1.0 - distance / 50.0);

                        // Update player volume map
                        playerVolumeMap.put(otherPlayer.getUniqueID(), volume);

                        // Enable voice chat for players based on proximity and set volume
                        enableVoiceChatForPlayers(player, otherPlayer, volume);
                    } else {
                        // Disable chat if the distance is too far
                        disableVoiceChatForPlayers(player, otherPlayer);
                    }
                }
            }
        }
    }

    private void updateMumbleLink(PlayerEntity player) {
        // Get the player's position and orientation
        Vector3d playerPosition = player.getPositionVec();
        float playerYaw = player.rotationYaw;
        float playerPitch = player.rotationPitch;

        // Set positional audio data in MumbleLink
        mumbleLink.setPosition(playerPosition.x, playerPosition.y, playerPosition.z);
        mumbleLink.setOrientation(playerYaw, playerPitch);

        // Optionally: set player context and identity
        mumbleLink.setContext("Overworld");  // Example context; could be dynamic
        mumbleLink.setIdentity(player.getGameProfile().getName()); // Set player name as identity

        // Send the updated information to Mumble
        mumbleLink.update();
    }

    private void enableVoiceChatForPlayers(PlayerEntity player, PlayerEntity otherPlayer, float volume) {
        // Example pseudo code for enabling voice chat and setting volume
        // For actual voice integration, connect to Mumble or Simple Voice Chat here
        System.out.println("Voice enabled between " + player.getName().getString() + " and " + otherPlayer.getName().getString() + " with volume: " + volume);
        // Implement logic to notify voice chat service like Mumble or Simple Voice Chat
    }

    private void disableVoiceChatForPlayers(PlayerEntity player, PlayerEntity otherPlayer) {
        // Example pseudo code for disabling voice chat
        System.out.println("Voice disabled between " + player.getName().getString() + " and " + otherPlayer.getName().getString());
        // Implement logic to disable voice chat service like Mumble or Simple Voice Chat
    }
}
