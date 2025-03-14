package com.smallaswater.npc.events;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import com.smallaswater.npc.entitys.EntityRsNPC;
import lombok.Getter;

public class RsNPCInteractEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final EntityRsNPC rsNPC;

    public RsNPCInteractEvent(Player player, EntityRsNPC rsNPC) {
        this.player = player;
        this.rsNPC = rsNPC;
    }

    public static HandlerList getHandlers() {
        return handlers;
    }
}
