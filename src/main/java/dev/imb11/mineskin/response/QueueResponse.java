package dev.imb11.mineskin.response;

import dev.imb11.mineskin.data.JobInfo;
import dev.imb11.mineskin.data.RateLimitInfo;
import dev.imb11.mineskin.data.UsageInfo;

public interface QueueResponse extends MineSkinResponse<JobInfo> {
    JobInfo getJob();

    RateLimitInfo getRateLimit();

    UsageInfo getUsage();
}
