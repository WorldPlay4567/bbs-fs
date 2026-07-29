package mchorse.bbs_mod.film;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Manager for active recording sessions (in-memory MVP).
 */
public class RecordingManager
{
    private static final Map<UUID, RecordingSession> SESSIONS = new ConcurrentHashMap<>();
    private static final Map<String, UUID> ACTIVE_BY_TARGET = new ConcurrentHashMap<>();

    /**
     * Try to start a recording session for the given player and target.
     * Returns created RecordingSession or null when another session for the same target exists.
     */
    public static RecordingSession startSession(ServerPlayerEntity player, String target)
    {
        synchronized (ACTIVE_BY_TARGET)
        {
            if (ACTIVE_BY_TARGET.containsKey(target))
            {
                return null;
            }

            RecordingSession session = new RecordingSession(player.getUuid(), target);

            SESSIONS.put(session.sessionId, session);
            ACTIVE_BY_TARGET.put(target, session.sessionId);

            return session;
        }
    }

    public static boolean addFrame(UUID sessionId, UUID ownerUuid, int tick, double x, double y, double z, float yaw, float pitch, Map<String, Float> morphWeights)
    {
        RecordingSession session = SESSIONS.get(sessionId);

        if (session == null)
        {
            return false;
        }

        if (!session.owner.equals(ownerUuid))
        {
            return false; // only owner can write
        }

        session.addFrame(tick, x, y, z, yaw, pitch, morphWeights);

        return true;
    }

    /**
     * Stop session and remove it from active map. Returns removed session or null.
     */
    public static RecordingSession stopSession(UUID sessionId)
    {
        RecordingSession session = SESSIONS.remove(sessionId);

        if (session != null)
        {
            ACTIVE_BY_TARGET.remove(session.target);
        }

        return session;
    }

    /**
     * Get owner UUID of an active session for a given target, or null.
     */
    public static UUID getActiveOwner(String target)
    {
        UUID sid = ACTIVE_BY_TARGET.get(target);

        if (sid == null) return null;

        RecordingSession s = SESSIONS.get(sid);

        return s != null ? s.owner : null;
    }
}
