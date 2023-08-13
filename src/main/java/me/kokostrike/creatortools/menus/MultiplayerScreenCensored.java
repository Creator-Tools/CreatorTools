package me.kokostrike.creatortools.menus;

import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.DirectConnectScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.gui.widget.*;
import net.minecraft.client.network.LanServerQueryManager;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.option.ServerList;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public class MultiplayerScreenCensored extends MultiplayerScreen {
    public MultiplayerScreenCensored(MultiplayerScreen screen) {
        super(screen.parent);
    }

    @Override
    protected void init() {
        if (this.initialized) {
            this.serverListWidget.updateSize(this.width, this.height, 32, this.height - 64);
        } else {
            this.initialized = true;
            this.serverList = new ServerList(this.client);
            this.serverList.loadFile();
            this.lanServers = new LanServerQueryManager.LanServerEntryList();
            try {
                this.lanServerDetector = new LanServerQueryManager.LanServerDetector(this.lanServers);
                this.lanServerDetector.start();
            } catch (Exception exception) {
                LOGGER.warn("Unable to start LAN server detection: {}", (Object)exception.getMessage());
            }
            this.serverListWidget = new MultiplayerServerListWidget(this, this.client, this.width, this.height, 32, this.height - 64, 36);
            this.serverListWidget.setServers(this.serverList);
        }
        this.addSelectableChild(this.serverListWidget);
        this.buttonJoin = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.select"), button -> this.connect()).width(100).build());
        ButtonWidget buttonWidget = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.direct"), button -> {
            this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", false);
            this.client.setScreen(new DirectConnectCensored(this, this::directConnect, this.selectedEntry));
        }).width(100).build());
        ButtonWidget buttonWidget2 = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.add"), button -> {
            this.selectedEntry = new ServerInfo(I18n.translate("selectServer.defaultName", new Object[0]), "", false);
            this.client.setScreen(new AddServerScreen(this, this::addEntry, this.selectedEntry));
        }).width(100).build());
        this.buttonEdit = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.edit"), button -> {
            MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry) {
                ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry)entry).getServer();
                this.selectedEntry = new ServerInfo(serverInfo.name, serverInfo.address, false);
                this.selectedEntry.copyWithSettingsFrom(serverInfo);
                this.client.setScreen(new AddServerScreen(this, this::editEntry, new ServerInfo(this.selectedEntry.name, "Streamer Mode is Enabled!", false)));
            }
        }).width(74).build());
        this.buttonDelete = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.delete"), button -> {
            String string;
            MultiplayerServerListWidget.Entry entry = (MultiplayerServerListWidget.Entry)this.serverListWidget.getSelectedOrNull();
            if (entry instanceof MultiplayerServerListWidget.ServerEntry && (string = ((MultiplayerServerListWidget.ServerEntry)entry).getServer().name) != null) {
                MutableText text = Text.translatable("selectServer.deleteQuestion");
                MutableText text2 = Text.translatable("selectServer.deleteWarning", string);
                MutableText text3 = Text.translatable("selectServer.deleteButton");
                Text text4 = ScreenTexts.CANCEL;
                this.client.setScreen(new ConfirmScreen(this::removeEntry, text, text2, text3, text4));
            }
        }).width(74).build());
        ButtonWidget buttonWidget3 = this.addDrawableChild(ButtonWidget.builder(Text.translatable("selectServer.refresh"), button -> this.refresh()).width(74).build());
        ButtonWidget buttonWidget4 = this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, button -> this.client.setScreen(this.parent)).width(74).build());
        GridWidget gridWidget = new GridWidget();
        GridWidget.Adder adder = gridWidget.createAdder(1);
        AxisGridWidget axisGridWidget = adder.add(new AxisGridWidget(308, 20, AxisGridWidget.DisplayAxis.HORIZONTAL));
        axisGridWidget.add(this.buttonJoin);
        axisGridWidget.add(buttonWidget);
        axisGridWidget.add(buttonWidget2);
        adder.add(EmptyWidget.ofHeight(4));
        AxisGridWidget axisGridWidget2 = adder.add(new AxisGridWidget(308, 20, AxisGridWidget.DisplayAxis.HORIZONTAL));
        axisGridWidget2.add(this.buttonEdit);
        axisGridWidget2.add(this.buttonDelete);
        axisGridWidget2.add(buttonWidget3);
        axisGridWidget2.add(buttonWidget4);
        gridWidget.refreshPositions();
        SimplePositioningWidget.setPos(gridWidget, 0, this.height - 64, this.width, 64);
        this.updateButtonActivationStates();
    }
}
