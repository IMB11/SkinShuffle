package dev.imb11.skinshuffle.api;

import com.google.gson.Gson;
import dev.imb11.skinshuffle.api.data.SkinQueryResult;
import dev.imb11.skinshuffle.api.data.SkinUploadRequest;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SkinShuffleAPI {
    private final String websocketHost;
    private final Gson gson = new Gson();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Constructs a SkinShuffleClient.
     *
     * @param websocketHost the host of the websocket server (e.g., "localhost:58372" or "skinshuffle.imb11.dev")
     */
    public SkinShuffleAPI(String websocketHost) {
        this.websocketHost = websocketHost;
    }

    /**
     * Builds the full WebSocket URI.
     */
    private URI buildUri() {
        return URI.create("wss://" + websocketHost + "/skin-gateway");
    }

    /**
     * Establishes a WebSocket connection, sends binary data if provided, then sends a JSON message,
     * awaits the SkinQueryResult response, closes the connection, and returns the result.
     *
     * @param jsonMessage the JSON message payload
     * @param binaryData  optional binary data to send (for file skin uploads); may be null
     * @return a CompletableFuture resolving to the SkinQueryResult
     */
    private CompletableFuture<SkinQueryResult> connectAndSend(String jsonMessage, byte[] binaryData) {
        URI uri = buildUri();
        CompletableFuture<SkinQueryResult> futureResult = new CompletableFuture<>();

        WebSocket.Listener listener = new WebSocket.Listener() {
            @Override
            public void onOpen(WebSocket webSocket) {
                // If binary data is provided, send it first.
                if (binaryData != null) {
                    webSocket.sendBinary(ByteBuffer.wrap(binaryData), true);
                }
                // Then send JSON message.
                webSocket.sendText(jsonMessage, true);
                WebSocket.Listener.super.onOpen(webSocket);
            }

            @Override
            public CompletableFuture<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
                String message = data.toString();
                try {
                    SkinQueryResult result = gson.fromJson(message, SkinQueryResult.class);
                    futureResult.complete(result);
                } catch (Exception e) {
                    futureResult.completeExceptionally(e);
                }
                return (CompletableFuture<?>) WebSocket.Listener.super.onText(webSocket, data, last);
            }

            @Override
            public void onError(WebSocket webSocket, Throwable error) {
                futureResult.completeExceptionally(error);
                WebSocket.Listener.super.onError(webSocket, error);
            }

            @Override
            public CompletableFuture<?> onClose(WebSocket webSocket, int statusCode, String reason) {
                if (!futureResult.isDone()) {
                    futureResult.completeExceptionally(new RuntimeException("Connection closed prematurely: " + reason));
                }
                return (CompletableFuture<?>) WebSocket.Listener.super.onClose(webSocket, statusCode, reason);
            }
        };

        // Build websocket and attach a callback to close it once the response is received.
        CompletableFuture<WebSocket> wsFuture = httpClient.newWebSocketBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .buildAsync(uri, listener);

        wsFuture.thenAccept(ws ->
                futureResult.whenComplete((result, error) ->
                        ws.sendClose(WebSocket.NORMAL_CLOSURE, "Done").join()
                )
        );

        return futureResult;
    }

    /**
     * Uploads a URL-based skin. This function connects to the WebSocket, sends the URL skin upload request,
     * awaits the server response, closes the connection, and returns the resulting SkinQueryResult.
     *
     * @param skinUrl the URL of the skin to upload
     * @param model   the skin model to use (for example, "default")
     * @return the SkinQueryResult from the server
     * @throws Exception if an error occurs during the process
     */
    public SkinQueryResult uploadUrlSkin(String skinUrl, String model) throws Exception {
        SkinUploadRequest request = new SkinUploadRequest("url", skinUrl, model);
        String jsonMessage = gson.toJson(request);
        return connectAndSend(jsonMessage, null).get(30, TimeUnit.SECONDS);
    }

    /**
     * Uploads a file-based skin. This function reads the skin file from a given path, connects to the WebSocket,
     * sends the binary data followed by the JSON file upload request, awaits the server response, closes the connection,
     * and returns the resulting SkinQueryResult.
     *
     * @param filePath the path to the skin file
     * @param model    the skin model to use (for example, "default")
     * @return the SkinQueryResult from the server
     * @throws IOException if there is an error reading the file
     * @throws Exception   if an error occurs during the process
     */
    public SkinQueryResult uploadFileSkin(Path filePath, String model) throws Exception {
        byte[] fileBytes = Files.readAllBytes(filePath);
        SkinUploadRequest request = new SkinUploadRequest("file", null, model);
        String jsonMessage = gson.toJson(request);
        return connectAndSend(jsonMessage, fileBytes).get(30, TimeUnit.SECONDS);
    }
}