package dev.imb11.skinshuffle.api.data;

import org.jetbrains.annotations.Nullable;

public record SkinUploadRequest(String type, @Nullable String url, String model) {
}
