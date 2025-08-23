package ru.logonik.pluginBase.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

/**
 * Утилитный класс для отображения прогресса в виде визуального прогресс-бара.
 * Использует Adventure API для генерации {@link Component}.
 *
 * Пример использования:
 * <pre>{@code
 * // Простое использование
 * Component progressBar = ProgressDisplay.render(0.75); // 75%
 *
 * // Отправка в action bar
 * ProgressDisplay.sendActionBar(player, 0.75);
 *
 * // Кастомизированное использование через Builder
 * Component customBar = ProgressDisplay.builder()
 *     .length(10)
 *     .filledSymbol("|")
 *     .emptySymbol(" ")
 *     .filledColor(TextColor.color(0x00FF00)) // Зеленый
 *     .emptyColor(TextColor.color(0x555555)) // Темно-серый
 *     .textColor(TextColor.color(0xFFFFFF)) // Белый
 *     .showPercentage(true)
 *     .build()
 *     .render(0.4);
 * }</pre>
 */
public final class ProgressDisplay {

    private static final ProgressDisplay DEFAULT = ProgressDisplay.builder().build();

    private final int length;
    private final String filledSymbol;
    private final String emptySymbol;
    private final TextColor filledColor;
    private final TextColor emptyColor;
    private final TextColor textColor;
    private final boolean showPercentage;

    private ProgressDisplay(Builder builder) {
        this.length = builder.length;
        this.filledSymbol = builder.filledSymbol;
        this.emptySymbol = builder.emptySymbol;
        this.filledColor = builder.filledColor;
        this.emptyColor = builder.emptyColor;
        this.textColor = builder.textColor;
        this.showPercentage = builder.showPercentage;
    }

    /**
     * Отрисовывает прогресс в виде компонента с использованием текущих настроек.
     *
     * @param progress значение прогресса от 0.0 до 1.0
     * @return компонент прогресс-бара
     */
    public Component render(double progress) {
        if (progress < 0.0) progress = 0.0;
        if (progress > 1.0) progress = 1.0;

        int filled = (int) Math.round(progress * length);
        int empty = length - filled;

        Component bar = Component.empty();
        if (filled > 0) {
            bar = bar.append(Component.text(filledSymbol.repeat(filled)).color(filledColor));
        }
        if (empty > 0) {
            bar = bar.append(Component.text(emptySymbol.repeat(empty)).color(emptyColor));
        }

        if (showPercentage) {
            int percent = (int) Math.round(progress * 100);
            bar = bar.append(Component.space())
                     .append(Component.text(percent + "%").color(textColor));
        }

        return bar;
    }

    /**
     * Рендерит прогресс по умолчанию.
     *
     * @param progress значение прогресса от 0.0 до 1.0
     * @return компонент прогресс-бара
     */
    public static Component renderDef(double progress) {
        return DEFAULT.render(progress);
    }

    /**
     * Отправляет прогресс в action bar игрока.
     *
     * @param player   игрок
     * @param progress значение прогресса от 0.0 до 1.0
     */
    public static void sendActionBar(Player player, double progress) {
        player.sendActionBar(renderDef(progress));
    }

    /**
     * Создает новый Builder для кастомизации прогресс-бара.
     *
     * @return билдер
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder для конфигурации {@link ProgressDisplay}.
     */
    public static final class Builder {
        private int length = 20;
        private String filledSymbol = "|";
        private String emptySymbol = " ";
        private TextColor filledColor = TextColor.color(0x00FF00); // зеленый
        private TextColor emptyColor = TextColor.color(0x555555);  // темно-серый
        private TextColor textColor = TextColor.color(0xFFFFFF);   // белый
        private boolean showPercentage = true;

        private Builder() {}

        /**
         * Устанавливает длину прогресс-бара (в символах).
         */
        public Builder length(int length) {
            this.length = length;
            return this;
        }

        /**
         * Символ для заполненной части.
         */
        public Builder filledSymbol(String filledSymbol) {
            this.filledSymbol = filledSymbol;
            return this;
        }

        /**
         * Символ для пустой части.
         */
        public Builder emptySymbol(String emptySymbol) {
            this.emptySymbol = emptySymbol;
            return this;
        }

        /**
         * Цвет заполненной части.
         */
        public Builder filledColor(TextColor filledColor) {
            this.filledColor = filledColor;
            return this;
        }

        /**
         * Цвет пустой части.
         */
        public Builder emptyColor(TextColor emptyColor) {
            this.emptyColor = emptyColor;
            return this;
        }

        /**
         * Цвет текста процента.
         */
        public Builder textColor(TextColor textColor) {
            this.textColor = textColor;
            return this;
        }

        /**
         * Управляет отображением процентов.
         */
        public Builder showPercentage(boolean showPercentage) {
            this.showPercentage = showPercentage;
            return this;
        }

        /**
         * Собирает новый {@link ProgressDisplay} с указанными настройками.
         */
        public ProgressDisplay build() {
            return new ProgressDisplay(this);
        }
    }
}
