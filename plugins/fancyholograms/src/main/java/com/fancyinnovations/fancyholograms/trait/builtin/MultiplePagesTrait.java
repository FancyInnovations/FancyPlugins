package com.fancyinnovations.fancyholograms.trait.builtin;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitClass;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@ApiStatus.Experimental
@HologramTraitClass(traitName = "multiple_pages_trait")
public class MultiplePagesTrait extends HologramTrait {

    private static final Configuration DEFAULT_CONFIG = new Configuration(
            Mode.CYCLE,
            1000,
            List.of(
                    new Page(List.of("Page 1", "Line 1", "Line 2")),
                    new Page(List.of("Page 2", "Line 1", "Line 2"))
            ));

    private Configuration config;
    private int currentPageIdx;
    private ScheduledFuture<?> updateTask;

    public MultiplePagesTrait() {
        this.currentPageIdx = 0;
    }

    @Override
    public void onAttach() {
        if (!(hologram.getData() instanceof TextHologramData td)) {
            throw new IllegalStateException("Hologram must be text hologram to use MultiplePagesTrait");
        }

        load();

        this.updateTask = hologramThread.scheduleWithFixedDelay(
                this::run,
                0,
                config.cycleDelay(),
                TimeUnit.MILLISECONDS
        );
    }

    @Override
    public void save() {
        try {
            storage.set(hologram.getData().getName(), config);
        } catch (IOException e) {
            logger.error("Failed to save configuration for MultiplePagesTrait", ThrowableProperty.of(e));
        }
    }

    @Override
    public void load() {
        try {
            config = storage.get(hologram.getData().getName(), Configuration.class);
        } catch (IOException e) {
            logger.error("Failed to load configuration for MultiplePagesTrait", ThrowableProperty.of(e));
        }

        if (config == null) {
            config = DEFAULT_CONFIG;
            save();
        }
    }

    /**
     * Runs the page update logic based on the current configuration.
     * This method updates the text of the hologram to display the lines of the current page and then determines the next page index based on the configured mode (CYCLE, RANDOM, or MANUAL).
     */
    private void run() {
        TextHologramData td = (TextHologramData) hologram.getData();
        Page currentPage = config.pages().get(currentPageIdx);

        td.setText(new ArrayList<>(currentPage.lines()));

        currentPageIdx = switch (config.mode()) {
            case CYCLE -> (currentPageIdx + 1) % config.pages().size();
            case RANDOM -> (int) (Math.random() * config.pages().size());
            default -> currentPageIdx;
        };
    }

    public int getCurrentPageIndex() {
        return this.currentPageIdx;
    }

    public void setCurrentPageIndex(int currentPageIdx) {
        this.currentPageIdx = currentPageIdx;
    }

    public Mode getMode() {
        return config.mode();
    }

    public void setMode(Mode mode) {
        this.config = new Configuration(mode, config.cycleDelay(), config.pages());
        save();
    }

    public long getCycleDelay() {
        return config.cycleDelay();
    }

    public void setCycleDelay(long cycleDelay) {
        this.config = new Configuration(config.mode(), cycleDelay, config.pages());
        save();


        if (!this.updateTask.cancel(true)) {
            logger.warn("Failed to cancel existing update task for MultiplePagesTrait");
        }

        this.updateTask = hologramThread.scheduleWithFixedDelay(
                this::run,
                0,
                config.cycleDelay(),
                TimeUnit.MILLISECONDS
        );
    }

    public void setLines(int pageIndex, List<String> lines) {
        if (pageIndex < 0 || pageIndex >= config.pages().size()) {
            throw new IndexOutOfBoundsException("Page index out of bounds");
        }

        List<Page> pages = new ArrayList<>(config.pages());
        pages.set(pageIndex, new Page(lines));

        this.config = new Configuration(config.mode(), config.cycleDelay(), pages);
        save();
    }

    public List<Page> getPages() {
        return config.pages();
    }

    public enum Mode {
        MANUAL,
        CYCLE,
        RANDOM,
    }

    public record Configuration(
            Mode mode,
            long cycleDelay,
            List<Page> pages
    ) {

    }

    public record Page(List<String> lines) {
        
    }
}
