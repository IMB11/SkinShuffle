package com.mineblock11.skinshuffle.client;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import net.fabricmc.api.ClientModInitializer;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class SkinShuffleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        SkinShuffleConfig.setup();
    }

    public static Connection jsoupConnection(String url) {
        return Jsoup.connect(url)
                .ignoreContentType(true)
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                .header("Accept-Encoding", "gzip,deflate,sdch")
                .header("Accept-Language", "en")
                .header("Connection", "keep-alive")
                .header("Host", "namemc.com")
                .referrer("http://www.google.com")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/113.0")
                .maxBodySize(0)
                .timeout(12000)
                .followRedirects(true);
    }
}
