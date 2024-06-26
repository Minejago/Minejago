package dev.thomasglasser.minejago.network;

import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.client.animation.definitions.PlayerAnimations;
import dev.thomasglasser.minejago.world.attachment.MinejagoAttachmentTypes;
import dev.thomasglasser.minejago.world.level.storage.SpinjitzuData;
import dev.thomasglasser.tommylib.api.client.ClientUtils;
import dev.thomasglasser.tommylib.api.client.animation.AnimationUtils;
import dev.thomasglasser.tommylib.api.network.ExtendedPacketPayload;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public record ClientboundStopSpinjitzuPayload(UUID uuid, boolean fail) implements ExtendedPacketPayload
{
    public static final Type<ClientboundStopSpinjitzuPayload> TYPE = new Type<>(Minejago.modLoc("clientbound_stop_spinjitzu"));
    public static final StreamCodec<ByteBuf, ClientboundStopSpinjitzuPayload> CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ClientboundStopSpinjitzuPayload::uuid,
            ByteBufCodecs.BOOL, ClientboundStopSpinjitzuPayload::fail,
            ClientboundStopSpinjitzuPayload::new
    );

    // On Client
    public void handle(Player player) {
        AbstractClientPlayer clientPlayer = ClientUtils.getClientPlayerByUUID(uuid);
        clientPlayer.setData(MinejagoAttachmentTypes.SPINJITZU, new SpinjitzuData(clientPlayer.getData(MinejagoAttachmentTypes.SPINJITZU).unlocked(), false));
        if (Minejago.Dependencies.PLAYER_ANIMATOR.isInstalled())
        {
            if (fail)
                AnimationUtils.startAnimation(PlayerAnimations.Spinjitzu.WOBBLE.getAnimation(), null, clientPlayer, FirstPersonMode.VANILLA);
            else
                AnimationUtils.startAnimation(PlayerAnimations.Spinjitzu.FINISH.getAnimation(), null, clientPlayer, FirstPersonMode.VANILLA);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
}
