package com.mineblock11.skinshuffle.client.skin.source;

import com.mineblock11.skinshuffle.client.skin.Skin;
import org.jetbrains.annotations.Nullable;

public interface SkinSource {
    @Nullable Skin get(int index);

    @Nullable SkinSource getSearchedSource(String query);
}
