package com.tbfox.jogging;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

/**
 * A small "Jogging" label in the top-left corner of the screen.
 * Built the same way as the game's own "Spectating" label, so no asset pack is needed.
 */
public final class JogHud extends CustomUIHud {
    public static final String KEY = "TBFox_Jogging";

    private static final String DOCUMENT = """
            Group {
              Anchor: (Left: 20, Top: 20, Height: 32);
              LayoutMode: Left;

              Label {
                Text: "Jogging";
                Style: (
                  FontSize: 18,
                  TextColor: #7fd67f,
                  RenderBold: true,
                  RenderUppercase: true,
                  LetterSpacing: 1.2,
                  VerticalAlignment: Center
                );
              }
            }
            """;

    public JogHud(PlayerRef player) {
        super(player, KEY);
    }

    @Override
    protected void build(UICommandBuilder ui) {
        ui.appendInline(null, DOCUMENT);
    }

    public static void show(Player player, PlayerRef playerRef) {
        player.getHudManager().addCustomHud(playerRef, new JogHud(playerRef));
    }

    public static void hide(Player player, PlayerRef playerRef) {
        player.getHudManager().removeCustomHud(playerRef, KEY);
    }
}
