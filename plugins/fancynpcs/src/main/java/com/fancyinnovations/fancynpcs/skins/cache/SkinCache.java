package com.fancyinnovations.fancynpcs.skins.cache;

import com.fancyinnovations.fancynpcs.api.skins.SkinData;

public interface SkinCache {

    long CACHE_TIME = 1000 * 60 * 60 * 24 * 7; // 1 week

    SkinCacheData getSkin(String identifier);

    void addSkin(SkinData skin);

    void removeSkin(String identifier);

    void clear();

}
