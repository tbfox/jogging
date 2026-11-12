package com.tbfox.jogging;

import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

/**
 * A small status label in the top-left corner of the screen: "Jogging" or "Winded".
 * Built the same way as the game's own "Spectating" label.
 */
public final class JogHud extends CustomUIHud {
    public static final String KEY = "TBFox_Jogging";

    /** What the label can show. */
    public enum Label {
        JOGGING("Jogging", "#7fd67f"),
        WINDED("Winded", "#e8a04c");

        final String text;
        final String color;

        Label(String text, String color) {
            this.text = text;
            this.color = color;
        }
    }

    /**
     * Same layout as the game's own "time left" HUD (Common/UI/Custom/Hud/TimeLeft.ui):
     * a row, holding a dark see-through box that sizes itself to the text.
     * Background is black at 55% opacity; raise the number for a darker box.
     */
    private static final String DOCUMENT = """
            Group {
              LayoutMode: Left;
              Anchor: (Top: 20, Height: 36);

              Group {
                Background: #000000(0.55);
                Anchor: (Left: 20);
                Padding: (Horizontal: 12, Vertical: 6);
                LayoutMode: Left;

                Label {
                  Text: "%s";
                  Style: (
                    FontSize: 18,
                    TextColor: %s,
                    RenderBold: true,
                    RenderUppercase: true,
                    LetterSpacing: 1.2,
                    VerticalAlignment: Center
                  );
                }
              }
            }
            """;

    private final Label label;

    private JogHud(PlayerRef player, Label label) {
        super(player, KEY);
        this.label = label;
    }

    @Override
    protected void build(UICommandBuilder ui) {
        ui.appendInline(null, DOCUMENT.formatted(label.text, label.color));
    }

    /** Shows the given label, replacing whatever was there. Null removes it. */
    public static void show(Player player, PlayerRef playerRef, Label label) {
        if (label == null) {
            player.getHudManager().removeCustomHud(playerRef, KEY);
        } else {
            player.getHudManager().addCustomHud(playerRef, new JogHud(playerRef, label));
        }
    }
}
