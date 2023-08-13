package me.kokostrike.creatortools.menus;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class DirectConnectCensored extends DirectConnectScreen {


    public DirectConnectCensored(Screen parent, BooleanConsumer callback, ServerInfo server) {
        super(parent, callback, server);
    }

    @Override
    protected void init() {
        this.addressField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 116, 200, 20, Text.translatable("addServer.enterIp"));
        this.addressField.setMaxLength(128);
        this.addressField.setText("Streamer Mode is Enabled!");
        this.addressField.setChangedListener(text -> this.onAddressFieldChanged());
        this.addSelectableChild(this.addressField);
        this.selectServerButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.select"), button -> this.saveAndClose()).dimensions(this.width / 2 - 100, this.height / 4 + 96 + 12, 200, 20).build());
        this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.callback.accept(false)).dimensions(this.width / 2 - 100, this.height / 4 + 120 + 12, 200, 20).build());
        this.setInitialFocus(this.addressField);
        this.onAddressFieldChanged();
    }

    private void saveAndClose() {
        this.serverEntry.address = MinecraftClient.getInstance().options.lastServer;
        this.callback.accept(true);
    }

    @Override
    public void removed() {

    }
}
