package dev.imb11.mineskin;

import com.google.gson.Gson;
import dev.imb11.mineskin.request.RequestHandler;
import dev.imb11.mineskin.request.RequestHandlerConstructor;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;

public class ClientBuilder {

    private String userAgent = "MineSkinClient";
    private String apiKey = null;
    private int timeout = 10000;
    private Gson gson = new Gson();
    private Executor getExecutor = null;
    private Executor generateExecutor = null;
    private ScheduledExecutorService generateRequestScheduler = null;
    private ScheduledExecutorService getRequestScheduler = null;
    private ScheduledExecutorService jobCheckScheduler = null;
    private RequestHandlerConstructor requestHandlerConstructor = null;

    private ClientBuilder() {
    }

    /**
     * Create a new ClientBuilder
     */
    public static ClientBuilder create() {
        return new ClientBuilder();
    }

    /**
     * Set the User-Agent
     */
    public ClientBuilder userAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * Set the API key
     */
    public ClientBuilder apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    /**
     * Set the timeout
     */
    public ClientBuilder timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    /**
     * Set the Gson instance
     */
    public ClientBuilder gson(Gson gson) {
        this.gson = gson;
        return this;
    }

    /**
     * Set the Executor for get requests
     */
    public ClientBuilder getExecutor(Executor getExecutor) {
        this.getExecutor = getExecutor;
        return this;
    }

    /**
     * Set the Executor for generate requests
     */
    public ClientBuilder generateExecutor(Executor generateExecutor) {
        this.generateExecutor = generateExecutor;
        return this;
    }

    /**
     * Set the ScheduledExecutorService for submitting queue jobs
     */
    public ClientBuilder generateRequestScheduler(ScheduledExecutorService scheduledExecutor) {
        this.generateRequestScheduler = scheduledExecutor;
        return this;
    }

    /**
     * Set the ScheduledExecutorService for get requests, e.g. getting skins
     */
    public ClientBuilder getRequestScheduler(ScheduledExecutorService scheduledExecutor) {
        this.getRequestScheduler = scheduledExecutor;
        return this;
    }

    /**
     * Set the ScheduledExecutorService for checking job status
     */
    public ClientBuilder jobCheckScheduler(ScheduledExecutorService scheduledExecutor) {
        this.jobCheckScheduler = scheduledExecutor;
        return this;
    }


    /**
     * Set the constructor for the RequestHandler
     */
    public ClientBuilder requestHandler(RequestHandlerConstructor requestHandlerConstructor) {
        this.requestHandlerConstructor = requestHandlerConstructor;
        return this;
    }

    /**
     * Build the MineSkinClient
     */
    public MineSkinClient build() {
        if (requestHandlerConstructor == null) {
            throw new IllegalStateException("RequestHandlerConstructor is not set");
        }
        if ("MineSkinClient".equals(userAgent)) {
            MineSkinClientImpl.LOGGER.log(Level.WARNING, "Using default User-Agent: MineSkinClient - Please set a custom User-Agent");
        }
        if (apiKey == null) {
            MineSkinClientImpl.LOGGER.log(Level.WARNING, "Creating MineSkinClient without API key");
        }

        if (getExecutor == null) {
            getExecutor = Executors.newSingleThreadExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setName("MineSkinClient/get");
                return thread;
            });
        }
        if (generateExecutor == null) {
            generateExecutor = Executors.newSingleThreadExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setName("MineSkinClient/generate");
                return thread;
            });
        }

        if (generateRequestScheduler == null) {
            generateRequestScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r);
                thread.setName("MineSkinClient/scheduler");
                return thread;
            });
        }
        if (getRequestScheduler == null) {
            getRequestScheduler = generateRequestScheduler;
        }
        if (jobCheckScheduler == null) {
            jobCheckScheduler = generateRequestScheduler;
        }

        RequestHandler requestHandler = requestHandlerConstructor.construct(userAgent, apiKey, timeout, gson);
        RequestExecutors executors = new RequestExecutors(getExecutor, generateExecutor, generateRequestScheduler, getRequestScheduler, jobCheckScheduler);
        return new MineSkinClientImpl(requestHandler, executors);
    }

}
