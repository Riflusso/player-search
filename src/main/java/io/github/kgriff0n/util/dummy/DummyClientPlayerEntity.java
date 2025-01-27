package io.github.kgriff0n.util.dummy;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.function.Function;

//Credits: https://github.com/enjarai/cicada-lib
public class DummyClientPlayerEntity extends ClientPlayerEntity {
    private static DummyClientPlayerEntity instance;
    private SkinTextures skinTextures = null;
    private PlayerEntity player = null;
    private Text name = null;
    public Function<EquipmentSlot, ItemStack> equippedStackSupplier = slot -> ItemStack.EMPTY;

    public static DummyClientPlayerEntity getInstance() {
        if (instance == null) instance = new DummyClientPlayerEntity();
        return instance;
    }

    private DummyClientPlayerEntity() {
        super(MinecraftClient.getInstance(), DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance(), null, null,false, false);
        setUuid(UUID.randomUUID());
        MinecraftClient.getInstance().getSkinProvider().fetchSkinTextures(getGameProfile()).thenAccept((textures) -> {
            skinTextures = textures;
        });
    }

    public DummyClientPlayerEntity(Text name) {
        this();
        this.name = name;
    }

    public DummyClientPlayerEntity(@Nullable PlayerEntity player, UUID uuid, SkinTextures skinTextures) {
        this(player, uuid, skinTextures, DummyClientWorld.getInstance(), DummyClientPlayNetworkHandler.getInstance());
    }

    public DummyClientPlayerEntity(@Nullable PlayerEntity player, UUID uuid, SkinTextures skinTextures, ClientWorld world, ClientPlayNetworkHandler networkHandler) {
        super(MinecraftClient.getInstance(), world, networkHandler, null, null, false, false);
        this.player = player;
        setUuid(uuid);
        this.skinTextures = skinTextures;
    }

    @Override
    public boolean isPartVisible(PlayerModelPart modelPart) {
        return true;
    }

    @Override
    public SkinTextures getSkinTextures() {
        return skinTextures == null ? DefaultSkinHelper.getSkinTextures(this.getUuid()) : skinTextures;
    }

    @Nullable
    @Override
    protected PlayerListEntry getPlayerListEntry() {
        return null;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return true;
    }

    @Override
    public ItemStack getEquippedStack(EquipmentSlot slot) {
        if (player != null) {
            return player.getEquippedStack(slot);
        }
        return equippedStackSupplier.apply(slot);
    }

    @Override
    public Text getName() {
        if (name == null) {
            return super.getName();
        } else {
            return name;
        }
    }
}