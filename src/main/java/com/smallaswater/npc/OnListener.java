package com.smallaswater.npc;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByChildEntityEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityVehicleEnterEvent;
import cn.nukkit.event.player.PlayerInteractEntityEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.network.protocol.PlayerListPacket;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.entitys.EntityRsNPC;
import com.smallaswater.npc.events.RsNPCInteractEvent;
import com.smallaswater.npc.utils.Utils;
import com.smallaswater.npc.variable.VariableManage;

/**
 * @author lt_name
 */
@SuppressWarnings("unused")
public class OnListener implements Listener {

    private final RsNPC rsNPC;

    public OnListener(RsNPC rsNPC) {
        this.rsNPC = rsNPC;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityVehicleEnter(EntityVehicleEnterEvent event) {
        if (event.getEntity() instanceof EntityRsNPC || event.getVehicle() instanceof EntityRsNPC) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof EntityRsNPC entityRsNPC) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            RsNPCInteractEvent rsNPCInteractEvent = new RsNPCInteractEvent(player, entityRsNPC);

            entity.getServer().getPluginManager().callEvent(rsNPCInteractEvent);

            if (rsNPCInteractEvent.isCancelled()) {
                return;
            }

            RsNpcConfig config = entityRsNPC.getConfig();

            entityRsNPC.setPauseMoveTick(60);

            Utils.executeCommand(player, config);

            for (String message : config.getMessages()) {
                player.sendMessage(VariableManage.stringReplace(player, message, config));
            }

        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof EntityRsNPC entityRsNpc)) {
            return;
        }

        event.setCancelled(true);
        if (event instanceof EntityDamageByEntityEvent) {
            Entity damage = ((EntityDamageByEntityEvent) event).getDamager();

            if (!(damage instanceof Player player)) {
                return;
            }

            RsNPCInteractEvent rsNPCInteractEvent = new RsNPCInteractEvent(player, entityRsNpc);

            entityRsNpc.getServer().getPluginManager().callEvent(rsNPCInteractEvent);

            if (rsNPCInteractEvent.isCancelled()) {
                return;
            }

            RsNpcConfig rsNpcConfig = entityRsNpc.getConfig();

            if (!rsNpcConfig.isCanProjectilesTrigger() && event instanceof EntityDamageByChildEntityEvent) {
                return;
            }

            entityRsNpc.setPauseMoveTick(60);
            Utils.executeCommand(player, rsNpcConfig);

            for (String message : rsNpcConfig.getMessages()) {
                player.sendMessage(VariableManage.stringReplace(player, message, rsNpcConfig));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDataPacketSend(DataPacketSendEvent event) {
        if (event.getPacket() instanceof PlayerListPacket packet) {
            if (Api.isHideCustomSkin(event.getPlayer())) {
                for (PlayerListPacket.Entry entry : packet.entries) {
                    for (RsNpcConfig config : this.rsNPC.getNpcs().values()) {
                        EntityRsNPC entityRsNpc = config.getEntityRsNpc();
                        if (entityRsNpc != null && entityRsNpc.getUniqueId() == entry.uuid) {
                            entry.skin = this.rsNPC.getSkinByName("private_steve");
                            break;
                        }
                    }
                }
            }
        }
    }
}
