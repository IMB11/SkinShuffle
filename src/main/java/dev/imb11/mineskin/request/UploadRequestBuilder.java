package dev.imb11.mineskin.request;

import dev.imb11.mineskin.request.source.UploadSource;

public interface UploadRequestBuilder extends GenerateRequest {

    UploadSource getUploadSource();

}
