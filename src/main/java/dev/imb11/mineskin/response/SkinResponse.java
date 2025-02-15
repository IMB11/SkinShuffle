package dev.imb11.mineskin.response;

import dev.imb11.mineskin.data.SkinInfo;

public interface SkinResponse extends MineSkinResponse<SkinInfo> {
    SkinInfo getSkin();
}
