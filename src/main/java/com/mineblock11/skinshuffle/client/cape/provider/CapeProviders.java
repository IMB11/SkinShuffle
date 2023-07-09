package com.mineblock11.skinshuffle.client.cape.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public enum CapeProviders implements CapeProvider {
    MOJANG("minecraft"),
    OPTIFINE("optifine"),
    MC_CAPES("minecraftcapes");

    public static final Codec<CapeProviders> CODEC = Codec.STRING.comapFlatMap(CapeProviders::validate, CapeProviders::getProviderID);
    final String providerID;

    CapeProviders(String providerID) {
        this.providerID = providerID;
    }

    private static DataResult<CapeProviders> validate(String id) {
        switch (id) {
            case "minecraft" -> {
                return DataResult.success(CapeProviders.MOJANG);
            }
            case "optifine" -> {
                return DataResult.success(CapeProviders.OPTIFINE);
            }
            case "minecraftcapes" -> {
                return DataResult.success(CapeProviders.MC_CAPES);
            }
        }
        return DataResult.error(() -> "Invalid cape provider id: " + id);
    }

    @Override
    public String getProviderID() {
        return this.providerID;
    }
}
