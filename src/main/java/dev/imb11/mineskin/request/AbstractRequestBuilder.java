package dev.imb11.mineskin.request;

import dev.imb11.mineskin.GenerateOptions;
import dev.imb11.mineskin.data.Variant;
import dev.imb11.mineskin.data.Visibility;

public abstract class AbstractRequestBuilder implements GenerateRequest {

    private GenerateOptions options = GenerateOptions.create();

    @Override
    public GenerateRequest options(GenerateOptions options) {
        this.options = options;
        return this;
    }

    @Override
    public GenerateRequest visibility(Visibility visibility) {
        this.options.visibility(visibility);
        return this;
    }

    @Override
    public GenerateRequest variant(Variant variant) {
        this.options.variant(variant);
        return this;
    }

    @Override
    public GenerateRequest name(String name) {
        this.options.name(name);
        return this;
    }

    @Override
    public GenerateOptions options() {
        return options;
    }

}
