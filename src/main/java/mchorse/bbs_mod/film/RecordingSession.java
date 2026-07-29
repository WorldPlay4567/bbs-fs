package mchorse.bbs_mod.film;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Lightweight recording session storing frames sent from clients.
 * MVP: stores frames in memory. Later can be serialized into Film/Replay.
 */
public class RecordingSession
{
    public final UUID sessionId;
    public final UUID owner;
    public final String target; // target morph id / name
    public final long startedAt;

    public final List<Frame> frames = new ArrayList<>();

    public RecordingSession(UUID owner, String target)
    {
        this.sessionId = UUID.randomUUID();
        this.owner = owner;
        this.target = target;
        this.startedAt = System.currentTimeMillis();
    }

    public void addFrame(int tick, double x, double y, double z, float yaw, float pitch, Map<String, Float> morphWeights)
    {
        this.frames.add(new Frame(tick, x, y, z, yaw, pitch, morphWeights));
    }

    public static class Frame
    {
        public final int tick;
        public final double x, y, z;
        public final float yaw, pitch;
        public final Map<String, Float> morphWeights;

        public Frame(int tick, double x, double y, double z, float yaw, float pitch, Map<String, Float> morphWeights)
        {
            this.tick = tick;
            this.x = x;
            this.y = y;
            this.z = z;
            this.yaw = yaw;
            this.pitch = pitch;
            this.morphWeights = morphWeights;
        }
    }
}
