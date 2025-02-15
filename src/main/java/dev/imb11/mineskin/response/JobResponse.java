package dev.imb11.mineskin.response;

import dev.imb11.mineskin.MineSkinClient;
import dev.imb11.mineskin.data.JobReference;
import dev.imb11.mineskin.data.JobInfo;
import dev.imb11.mineskin.data.SkinInfo;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface JobResponse extends MineSkinResponse<JobInfo>, JobReference {
    JobInfo getJob();

    Optional<SkinInfo> getSkin();

    CompletableFuture<SkinInfo> getOrLoadSkin(MineSkinClient client);
}
