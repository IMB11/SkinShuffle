package dev.imb11.mineskin.request;

import dev.imb11.mineskin.request.source.UploadSource;

public class UploadRequestBuilderImpl extends AbstractRequestBuilder implements UploadRequestBuilder {

    private final UploadSource uploadSource;

    UploadRequestBuilderImpl(UploadSource uploadSource) {
        this.uploadSource = uploadSource;
    }

    @Override
    public UploadSource getUploadSource() {
        return uploadSource;
    }
}
