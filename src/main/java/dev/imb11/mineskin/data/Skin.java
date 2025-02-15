package dev.imb11.mineskin.data;

public interface Skin {

    String uuid();

    String name();

    Variant variant();

    Visibility visibility();

    TextureInfo texture();

    int views();

    boolean duplicate();

}
