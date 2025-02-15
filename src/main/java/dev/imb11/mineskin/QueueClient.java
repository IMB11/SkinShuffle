package dev.imb11.mineskin;

import dev.imb11.mineskin.data.JobInfo;
import dev.imb11.mineskin.data.JobReference;
import dev.imb11.mineskin.request.GenerateRequest;
import dev.imb11.mineskin.response.JobResponse;
import dev.imb11.mineskin.response.QueueResponse;

import java.util.concurrent.CompletableFuture;

public interface QueueClient {

    /**
     * Submit a skin generation request
     * @see <a href="https://docs.mineskin.org/docs/mineskin-api/queue-skin-generation">Queue skin generation</a>
     */
    CompletableFuture<QueueResponse> submit(GenerateRequest request);

    /**
     * Get the status of a job
     * @see <a href="https://docs.mineskin.org/docs/mineskin-api/get-job-status">Get Job Status</a>
     */
    CompletableFuture<JobResponse> get(JobInfo jobInfo);

    /**
     * Get the status of a job
     * @see <a href="https://docs.mineskin.org/docs/mineskin-api/get-job-status">Get Job Status</a>
     */
    CompletableFuture<JobResponse> get(String id);

    /**
     * Wait for a job to complete
     */
    CompletableFuture<JobReference> waitForCompletion(JobInfo jobInfo);

}
