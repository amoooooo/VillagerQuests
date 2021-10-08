package net.villagerquests.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.villagerquests.VillagerQuestsMain;
import net.villagerquests.accessor.PlayerAccessor;
import net.villagerquests.network.QuestServerPacket;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    @Inject(method = "onPlayerConnect", at = @At(value = "TAIL"))
    private void onPlayerConnectMixin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        QuestServerPacket.writeS2CPlayerQuestDataPacket(player);
        QuestServerPacket.writeS2CQuestListPacket(player);
    }

    @Inject(method = "respawnPlayer", at = @At(value = "TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void respawnPlayerMixin(ServerPlayerEntity player, boolean alive, CallbackInfoReturnable<ServerPlayerEntity> info, BlockPos blockPos, float f, boolean bl, ServerWorld serverWorld,
            Optional<Vec3d> optional2, ServerWorld serverWorld2, ServerPlayerEntity serverPlayerEntity) {
        if (alive || !VillagerQuestsMain.CONFIG.hardMode) {
            ((PlayerAccessor) serverPlayerEntity).syncPlayerQuest(((PlayerAccessor) player).getPlayerQuestIdList(), ((PlayerAccessor) player).getPlayerKilledQuestList(),
                    ((PlayerAccessor) player).getPlayerTravelList(), ((PlayerAccessor) player).getPlayerQuestTraderIdList(), ((PlayerAccessor) player).getPlayerFinishedQuestIdList(),
                    ((PlayerAccessor) player).getPlayerQuestTimerList(), ((PlayerAccessor) player).getPlayerQuestRefreshTimerList());
            QuestServerPacket.writeS2CPlayerQuestDataPacket(serverPlayerEntity);
        }
    }

}
