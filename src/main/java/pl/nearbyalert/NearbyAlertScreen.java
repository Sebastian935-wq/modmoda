package pl.nearbyalert;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

public class NearbyAlertScreen extends Screen {
    private final Screen parent;

    public NearbyAlertScreen(Screen parent) {
        super(Text.literal("Nearby Alert"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int w = 200, h = 20, x = this.width / 2 - w / 2, y = this.height / 2 - 45;

        this.addDrawableChild(new RadiusSlider(x, y, w, h));

        this.addDrawableChild(ButtonWidget.builder(enabledText(), b -> {
            Config.INSTANCE.enabled = !Config.INSTANCE.enabled;
            b.setMessage(enabledText());
        }).dimensions(x, y + 25, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(soundText(), b -> {
            Config.INSTANCE.sound = !Config.INSTANCE.sound;
            b.setMessage(soundText());
        }).dimensions(x, y + 50, w, h).build());

        this.addDrawableChild(ButtonWidget.builder(Text.literal("Gotowe"), b -> close())
                .dimensions(x, y + 85, w, h).build());
    }

    private Text enabledText() {
        return Text.literal("Powiadomienia: " + (Config.INSTANCE.enabled ? "§aWŁ" : "§cWYŁ"));
    }

    private Text soundText() {
        return Text.literal("Dźwięk: " + (Config.INSTANCE.sound ? "§aWŁ" : "§cWYŁ"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFF);
    }

    @Override
    public void close() {
        Config.save();
        if (this.client != null) this.client.setScreen(parent);
    }

    private static class RadiusSlider extends SliderWidget {
        private static final int MIN = 1, MAX = 128;

        RadiusSlider(int x, int y, int w, int h) {
            super(x, y, w, h, Text.empty(), (Config.INSTANCE.radius - MIN) / (double) (MAX - MIN));
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal("Zasięg: " + (MIN + (int) Math.round(value * (MAX - MIN))) + " kratek"));
        }

        @Override
        protected void applyValue() {
            Config.INSTANCE.radius = MIN + (int) Math.round(value * (MAX - MIN));
        }
    }
}
