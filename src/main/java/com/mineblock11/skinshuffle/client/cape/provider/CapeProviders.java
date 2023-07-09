package com.mineblock11.skinshuffle.client.cape.provider;

public enum CapeProviders implements CapeProvider {
    MOJANG("minecraft"),
    OPTIFINE("optifine"),
    MC_CAPES("minecraftcapes");

    final String providerID;

    CapeProviders(String providerID) {
        this.providerID = providerID;
    }

    @Override
    public String getProviderID() {
        return this.providerID;
    }
}
