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

public class NameMcSource implements SkinSource {
    private static final String BASE_URL = "https://namemc.com";
    private static final String PAGE_URL = BASE_URL + "/minecraft-skins?page=%d";
    private static NameMcSource INSTANCE;

    public static NameMcSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NameMcSource( // TODO: json these
                    30, ".row .col-4 .card a", ".right-0 form a",
                    ".card .checkered canvas", "data-model"
            );
        }
        return INSTANCE;
    }

    private final int pageSize;
    private final String detailsPageSelector;
    private final String downloadButtonSelector;
    private final String skinTypeElementSelector;
    private final String skinTypeAttributeSelector;

    private final IntObjectMap<Page> pages = new IntObjectHashMap<>();

    public NameMcSource(int pageSize, String detailsPageSelector, String downloadButtonSelector, String skinTypeElementSelector, String skinTypeAttributeSelector) {
        this.pageSize = pageSize;
        this.detailsPageSelector = detailsPageSelector;
        this.downloadButtonSelector = downloadButtonSelector;
        this.skinTypeElementSelector = skinTypeElementSelector;
        this.skinTypeAttributeSelector = skinTypeAttributeSelector;
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
                return SkinShuffleClient.jsoupConnection(String.format(PAGE_URL, index)).get();
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
                        .attr("href");
                var skinType = skinPage.select(skinTypeElementSelector)
                        .first()
                        .attr(skinTypeAttributeSelector).equals("classic") ? "default" : "slim";
                return new UrlSkin(skinUrl, skinType);
            } catch (IOException e) {
                throw new FetchException(e);
            } catch (IndexOutOfBoundsException | Selector.SelectorParseException | NullPointerException e) {
                throw new ParseException(e);
            }
        }
    }
}
