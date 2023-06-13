/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package com.mineblock11.skinshuffle.client.skin.source;

import com.mineblock11.skinshuffle.client.SkinShuffleClient;
import com.mineblock11.skinshuffle.client.skin.Skin;
import com.mineblock11.skinshuffle.client.skin.UrlSkin;
import com.mineblock11.skinshuffle.client.skin.source.exception.FetchException;
import com.mineblock11.skinshuffle.client.skin.source.exception.ParseException;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import org.jetbrains.annotations.Nullable;
import org.jsoup.nodes.Document;
import org.jsoup.select.Selector;

import java.io.IOException;

public class SkinDexSource implements SkinSource {
    private static final String BASE_URL = "https://www.minecraftskins.com/";
    private static final String PAGE_URL = BASE_URL + "%d/";
    private static SkinDexSource INSTANCE;

    public static SkinDexSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SkinDexSource( // TODO: json these
                    58, ".skin-list .skin a.skin-title",
                    ".skin-previews .skin-previews-wrapper img[src*=\"/uploads/skins\"]",
                    ".skin-preview div.sid-pxarm"
            );
        }
        return INSTANCE;
    }

    private final int pageSize;
    private final String detailsPageSelector;
    private final String downloadButtonSelector;
    private final String skinTypeElementSelector;

    private final IntObjectMap<Page> pages = new IntObjectHashMap<>();

    public SkinDexSource(int pageSize, String detailsPageSelector, String downloadButtonSelector, String skinTypeElementSelector) {
        this.pageSize = pageSize;
        this.detailsPageSelector = detailsPageSelector;
        this.downloadButtonSelector = downloadButtonSelector;
        this.skinTypeElementSelector = skinTypeElementSelector;
    }

    @Override
    public Skin get(int index) { // TODO: this should return a "FutureSkin" that loads the skin in the background
        return pages.computeIfAbsent(index / pageSize, Page::new).get(index % pageSize);
    }

    @Override
    public @Nullable SkinSource getSearchedSource(String query) {
        return null; // TODO
    }

    private class Page {
        private final int index;

        private final Document document;
        private final IntObjectMap<Skin> skins = new IntObjectHashMap<>();

        private Page(int index) {
            this.index = index;
            document = fetchPage();
        }

        private Document fetchPage() {
            try {
                return SkinShuffleClient.jsoupConnection(String.format(PAGE_URL, index + 1)).get();
            } catch (IOException e) {
                throw new FetchException(e);
            }
        }

        public Skin get(int index) {
            return skins.computeIfAbsent(index, this::fetchSkin);
        }

        @SuppressWarnings("DataFlowIssue")
        private Skin fetchSkin(int index) {
            try {
                var skinPage = SkinShuffleClient.jsoupConnection(document.select(detailsPageSelector)
                                .get(index)
                                .attr("abs:href"))
                        .get();
                var skinUrl = skinPage.select(downloadButtonSelector)
                        .first()
                        .attr("src");
                var skinType = skinPage.select(skinTypeElementSelector)
                        .first()
                        .text().toLowerCase().contains("slim") ? "slim" : "default";
                return new UrlSkin(skinUrl, skinType);
            } catch (IOException e) {
                throw new FetchException(e);
            } catch (IndexOutOfBoundsException | Selector.SelectorParseException | NullPointerException e) {
                throw new ParseException(e);
            }
        }
    }
}
