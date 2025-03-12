package de.nexusrealms.creaturemod.client.screen;

import de.nexusrealms.creaturemod.network.IncantationPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.StringHelper;
import org.apache.commons.lang3.StringUtils;

public class IncantationScreen extends Screen {
    protected TextFieldWidget textField;

    public IncantationScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        this.textField = new TextFieldWidget(this.client.advanceValidatingTextRenderer, 4, this.height - 26, this.width - 4, 12, Text.literal("Where is this displayed"));
        this.textField.setMaxLength(256);
        this.textField.setDrawsBackground(false);
        this.textField.setText("Vasai scaala");
        this.textField.setFocusUnlocked(false);
        this.addSelectableChild(this.textField);
    }

    protected void setInitialFocus() {
        this.setInitialFocus(this.textField);
    }

    protected void insertText(String text, boolean override) {
        if (override) {
            this.textField.setText(text);
        } else {
            this.textField.write(text);
        }

    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(2, this.height - 28, this.width - 2, this.height - 16, 0x885b0000);
        this.textField.render(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

    }

    public void resize(MinecraftClient client, int width, int height) {
        String string = this.textField.getText();
        this.init(client, width, height);
        this.setText(string);
    }

    private void setText(String text) {
        this.textField.setText(text);
    }

    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        } else if (keyCode == 256) {
            this.client.setScreen(null);
            return true;
        } else if (keyCode == 257 || keyCode == 335) {
            this.sendMessage(this.textField.getText(), false);
            this.client.setScreen(null);
            return true;
        }
        return false;
    }
    public String normalize(String chatText) {
        return StringHelper.truncateChat(StringUtils.normalizeSpace(chatText.trim()));
    }
    public void sendMessage(String chatText, boolean addToHistory) {
        chatText = this.normalize(chatText);
        if (!chatText.isEmpty()) {
            client.player.sendMessage(Text.literal("Invoked " + chatText));
            ClientPlayNetworking.send(new IncantationPacket(chatText));

        }
    }
}
