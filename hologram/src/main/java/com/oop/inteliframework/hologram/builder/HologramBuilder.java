package com.oop.inteliframework.hologram.builder;

import com.google.common.base.Preconditions;
import com.oop.inteliframework.hologram.Hologram;
import com.oop.inteliframework.hologram.HologramLine;
import com.oop.inteliframework.hologram.HologramView;
import com.oop.inteliframework.hologram.line.HologramItem;
import com.oop.inteliframework.hologram.line.HologramText;
import com.oop.inteliframework.hologram.rule.HologramRule;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Accessors(chain = true, fluent = true)
public class HologramBuilder {

    @Setter
    private long refreshRate;

    private List<ViewBuilder> views = new ArrayList<>();

    /*
    * Set global refresh rate of views
    * Each view can override this value
     * And it can be changed dynamically
    */
    public HologramBuilder refreshRate(long time, TimeUnit unit) {
        return refreshRate(unit.toMillis(time));
    }

    /*
     * Add a view to the list of views
    */
    public HologramBuilder addView(Consumer<ViewBuilder> consumer) {
        ViewBuilder viewBuilder = new ViewBuilder();
        consumer.accept(viewBuilder);
        views.add(viewBuilder);
        return this;
    }

    public Hologram build() {
        Hologram hologram = new Hologram();
        hologram.setRefreshRate(refreshRate);
        Preconditions.checkArgument(!views.isEmpty(), "Failed to build hologram. 0 Views provided.");

        for (ViewBuilder viewBuilder : views) {
            HologramView view = new HologramView();
            view.addRule(viewBuilder.viewRules.toArray(new HologramRule[0]));
            view.addLines(viewBuilder.lines.toArray(new HologramLine[0]));
            if (viewBuilder.refreshRate != -1)
                view.setRefreshRate(viewBuilder.refreshRate);

            hologram.addView(view);
        }

        return hologram;
    }

    @RequiredArgsConstructor
    @Accessors(chain = true, fluent = true)
    public static class ViewBuilder extends HologramBuilder {
        private List<HologramRule> viewRules = new ArrayList<>();
        private List<HologramLine<?, ?>> lines = new LinkedList<>();

        @Setter
        private long refreshRate = -1;

        /*
        Add view rule to the view
        Which defines if the view can be viewed by the player
        */
        public ViewBuilder addRule(HologramRule... rules) {
            viewRules.addAll(Arrays.asList(rules));
            return this;
        }

        /*
        Set refresh rate of the view
        */
        public ViewBuilder refreshRate(long time, TimeUnit unit) {
            return refreshRate(unit.toMillis(time));
        }

        /**
         * Add new line to the view
         */
        public ViewBuilder addLines(Consumer<LinesBuilder> consumer) {
            LinesBuilder builder = new LinesBuilder(line -> lines.add(line));
            consumer.accept(builder);
            return this;
        }
    }

    @RequiredArgsConstructor
    @Accessors(chain = true, fluent = true)
    public static class LinesBuilder {

        private @NonNull
        final Consumer<HologramLine> whenFinished;

        public LinesBuilder displayItem(Function<Player, ItemStack> itemSupplier) {
            HologramItem item = new HologramItem(itemSupplier);
            whenFinished.accept(item);
            return this;
        }


        public LinesBuilder displayText(Function<Player, String> textSupplier) {
            HologramText text = new HologramText(textSupplier);
            whenFinished.accept(text);
            return this;
        }


        public LinesBuilder displayText(Supplier<String> textSupplier) {
            return displayText(player -> textSupplier.get());
        }

        public LinesBuilder displayText(String text) {
            return displayText(() -> text);
        }

        public LinesBuilder onClick(BiConsumer<Player, HologramLine> lineClick) {
            return this;
        }
    }
}
