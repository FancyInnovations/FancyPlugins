package com.fancyinnovations.fancynpcs.skins;

import com.fancyinnovations.fancynpcs.api.skins.SkinData;
import org.mineskin.request.GenerateRequest;

import java.util.Objects;

public class SkinGenerationRequest {

    private final String id;
    private final SkinData.SkinVariant variant;
    private final GenerateRequest mineskinRequest;

    public SkinGenerationRequest(String id, SkinData.SkinVariant variant, GenerateRequest mineskinRequest) {
        this.id = id;
        this.variant = variant;
        this.mineskinRequest = mineskinRequest;
    }

    public SkinGenerationRequest(String id, SkinData.SkinVariant variant) {
        this.id = id;
        this.variant = variant;
        this.mineskinRequest = null;
    }

    public String getID() {
        return id;
    }

    public SkinData.SkinVariant getVariant() {
        return variant;
    }

    public GenerateRequest getMineskinRequest() {
        return mineskinRequest;
    }

    @Override
    public String toString() {
        return "SkinGenerationRequest{" +
                "id='" + id + '\'' +
                ", variant=" + variant +
                ", mineskinRequest=" + mineskinRequest +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        SkinGenerationRequest that = (SkinGenerationRequest) o;
        return Objects.equals(id, that.id) &&
                variant == that.variant &&
                Objects.equals(mineskinRequest, that.mineskinRequest);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, variant, mineskinRequest);
    }
}
