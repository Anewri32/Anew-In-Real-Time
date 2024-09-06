package com.anewrealtime;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.ZoneId;

public class AnewInRealTime implements ModInitializer {
	public static final String MOD_ID = "anew-in-real-time";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private int tickCounter = 0;
	private static final int TICKS_PER_30_SECONDS = 600;  // 30 seconds = 600 ticks

	@Override
	public void onInitialize() {
		LOGGER.info("AnewInRealTime Mod initialized!");
		ServerTickEvents.START_SERVER_TICK.register(this::onServerTick);
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
		ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);
	}

	private void onServerStarted(MinecraftServer server) {
		// LOGGER.info("Server started. Setting gamerule doDaylightCycle to false.");
		server.getCommandManager().executeWithPrefix(server.getCommandSource(), "gamerule doDaylightCycle false");
	}

	private void onServerStopped(MinecraftServer server) {
		// LOGGER.info("Server stopped. Restoring gamerule doDaylightCycle to true.");
		server.getCommandManager().executeWithPrefix(server.getCommandSource(), "gamerule doDaylightCycle true");
	}

	// This method is called on each server tick
	private void onServerTick(MinecraftServer server) {
		tickCounter++;
		// Only update the time every 30 seconds (600 ticks)
		if (tickCounter >= TICKS_PER_30_SECONDS) {
			tickCounter = 0;  // Reset the counter
			LocalTime currentTime = LocalTime.now(ZoneId.systemDefault());
			long minecraftTime = convertRealTimeToMinecraft(currentTime);
			server.getOverworld().setTimeOfDay(minecraftTime);
			// LOGGER.info("Synchronized Minecraft time with real-world time.");
		}
	}

	private long convertRealTimeToMinecraft(LocalTime time) {
		int hours = time.getHour();
		int minutes = time.getMinute();

		// In Minecraft, 24,000 ticks = 24 hours. 1 hour = 1000 ticks.
		long minecraftTime = (hours * 1000) + (minutes * (1000 / 60));
		return minecraftTime - 6000;  // 00:00 time in minecraft is 6:00 in real time.
	}
}
