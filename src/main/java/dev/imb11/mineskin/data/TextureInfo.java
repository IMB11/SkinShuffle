package dev.imb11.mineskin.data;

public record TextureInfo(
        ValueAndSignature data,
        SkinHashes hash,
        SkinUrls url
) {
}
