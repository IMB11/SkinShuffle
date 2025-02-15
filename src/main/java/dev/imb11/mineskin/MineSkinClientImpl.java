package dev.imb11.mineskin;

import com.google.gson.JsonObject;
import dev.imb11.mineskin.data.JobInfo;
import dev.imb11.mineskin.data.JobReference;
import dev.imb11.mineskin.data.NullJobReference;
import dev.imb11.mineskin.data.RateLimitInfo;
import dev.imb11.mineskin.data.SkinInfo;
import dev.imb11.mineskin.exception.MineSkinRequestException;
import dev.imb11.mineskin.exception.MineskinException;
import dev.imb11.mineskin.request.GenerateRequest;
import dev.imb11.mineskin.request.RequestHandler;
import dev.imb11.mineskin.request.UploadRequestBuilder;
import dev.imb11.mineskin.request.UrlRequestBuilder;
import dev.imb11.mineskin.request.UserRequestBuilder;
import dev.imb11.mineskin.request.source.UploadSource;
import dev.imb11.mineskin.response.JobResponse;
import dev.imb11.mineskin.response.JobResponseImpl;
import dev.imb11.mineskin.response.MineSkinResponse;
import dev.imb11.mineskin.response.QueueResponse;
import dev.imb11.mineskin.response.QueueResponseImpl;
import dev.imb11.mineskin.response.SkinResponse;
import dev.imb11.mineskin.response.SkinResponseImpl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static com.google.common.base.Preconditions.checkNotNull;

public class MineSkinClientImpl implements MineSkinClient {

    public static final Logger LOGGER = Logger.getLogger(MineSkinClient.class.getName());

    private static final String API_BASE = "https://api.mineskin.org";

    private final RequestExecutors executors;

    private final RequestHandler requestHandler;
    private final RequestQueue generateQueue;
    private final RequestQueue getQueue;

    private final QueueClient queueClient = new QueueClientImpl();
    private final SkinsClient skinsClient = new SkinsClientImpl();

    public MineSkinClientImpl(RequestHandler requestHandler, RequestExecutors executors) {
        this.requestHandler = checkNotNull(requestHandler);
        this.executors = checkNotNull(executors);

        this.generateQueue = new RequestQueue(executors.generateRequestScheduler(), 200, 1);
        this.getQueue = new RequestQueue(executors.jobCheckScheduler(), 100, 5);
    }

    /////


    @Override
    public QueueClient queue() {
        return queueClient;
    }

    @Override
    public SkinsClient skins() {
        return skinsClient;
    }

    class QueueClientImpl implements QueueClient {

        @Override
        public CompletableFuture<QueueResponse> submit(GenerateRequest request) {
            if (request instanceof UploadRequestBuilder uploadRequestBuilder) {
                return queueUpload(uploadRequestBuilder);
            } else if (request instanceof UrlRequestBuilder urlRequestBuilder) {
                return queueUrl(urlRequestBuilder);
            } else if (request instanceof UserRequestBuilder userRequestBuilder) {
                return queueUser(userRequestBuilder);
            }
            throw new MineskinException("Unknown request builder type: " + request.getClass());
        }

        CompletableFuture<QueueResponse> queueUpload(UploadRequestBuilder builder) {
            return generateQueue.submit(() -> {
                try {
                    Map<String, String> data = builder.options().toMap();
                    UploadSource source = builder.getUploadSource();
                    checkNotNull(source);
                    try (InputStream inputStream = source.getInputStream()) {
                        QueueResponseImpl res = requestHandler.postFormDataFile(API_BASE + "/v2/queue", "file", "mineskinjava", inputStream, data, JobInfo.class, QueueResponseImpl::new);
                        handleGenerateResponse(res);
                        return res;
                    }
                } catch (IOException e) {
                    throw new MineskinException(e);
                } catch (MineSkinRequestException e) {
                    handleGenerateResponse(e.getResponse());
                    throw e;
                }
            }, executors.generateExecutor());
        }

        CompletableFuture<QueueResponse> queueUrl(UrlRequestBuilder builder) {
            return generateQueue.submit(() -> {
                try {
                    JsonObject body = builder.options().toJson();
                    URL url = builder.getUrl();
                    checkNotNull(url);
                    body.addProperty("url", url.toString());
                    QueueResponseImpl res = requestHandler.postJson(API_BASE + "/v2/queue", body, JobInfo.class, QueueResponseImpl::new);
                    handleGenerateResponse(res);
                    return res;
                } catch (IOException e) {
                    throw new MineskinException(e);
                } catch (MineSkinRequestException e) {
                    handleGenerateResponse(e.getResponse());
                    throw e;
                }
            }, executors.generateExecutor());
        }

        CompletableFuture<QueueResponse> queueUser(UserRequestBuilder builder) {
            return generateQueue.submit(() -> {
                try {
                    JsonObject body = builder.options().toJson();
                    UUID uuid = builder.getUuid();
                    checkNotNull(uuid);
                    body.addProperty("user", uuid.toString());
                    QueueResponseImpl res = requestHandler.postJson(API_BASE + "/v2/queue", body, JobInfo.class, QueueResponseImpl::new);
                    handleGenerateResponse(res);
                    return res;
                } catch (IOException e) {
                    throw new MineskinException(e);
                } catch (MineSkinRequestException e) {
                    handleGenerateResponse(e.getResponse());
                    throw e;
                }
            }, executors.generateExecutor());
        }

        private void handleGenerateResponse(MineSkinResponse<?> response0) {
            if (!(response0 instanceof QueueResponse response)) return;
            RateLimitInfo rateLimit = response.getRateLimit();
            if (rateLimit == null) return;
            long nextRelative = rateLimit.next().relative();
            if (nextRelative > 0) {
                generateQueue.setNextRequest(Math.max(generateQueue.getNextRequest(), System.currentTimeMillis() + nextRelative));
            }
        }

        @Override
        public CompletableFuture<JobResponse> get(JobInfo jobInfo) {
            checkNotNull(jobInfo);
            return get(jobInfo.id());
        }

        @Override
        public CompletableFuture<JobResponse> get(String id) {
            checkNotNull(id);
            return CompletableFuture.supplyAsync(() -> {
                try {
                    return requestHandler.getJson(API_BASE + "/v2/queue/" + id, JobInfo.class, JobResponseImpl::new);
                } catch (IOException e) {
                    throw new MineskinException(e);
                }
            }, executors.getExecutor());
        }

        @Override
        public CompletableFuture<JobReference> waitForCompletion(JobInfo jobInfo) {
            checkNotNull(jobInfo);
            if (jobInfo.id() == null) {
                return CompletableFuture.completedFuture(new NullJobReference(jobInfo));
            }
            return new JobChecker(MineSkinClientImpl.this, jobInfo, executors.jobCheckScheduler(), 10, 2, 1).check();
        }


    }

    class SkinsClientImpl implements SkinsClient {

        /**
         * Get an existing skin by UUID (Note: not the player's UUID)
         */
        @Override
        public CompletableFuture<SkinResponse> get(UUID uuid) {
            checkNotNull(uuid);
            return get(uuid.toString());
        }

        /**
         * Get an existing skin by UUID (Note: not the player's UUID)
         */
        @Override
        public CompletableFuture<SkinResponse> get(String uuid) {
            checkNotNull(uuid);
            return getQueue.submit(() -> {
                try {
                    return requestHandler.getJson(API_BASE + "/v2/skins/" + uuid, SkinInfo.class, SkinResponseImpl::new);
                } catch (IOException e) {
                    throw new MineskinException(e);
                }
            }, executors.getExecutor());
        }

    }

}
