package quoridor;

import java.awt.Color;

/**
 * Defines a set of UI colors for the game. Provides factory methods for light and dark themes
 * so components can switch palettes at runtime without duplicating configuration.
 */
public final class ThemePalette {

    public enum Mode {
        LIGHT,
        DARK
    }

    private final Mode mode;

    // Core frame / panel colors
    private final Color frameBackground;
    private final Color boardBackground;

    // Board specific colors
    private final Color boardGradientTop;
    private final Color boardGradientBottom;
    private final Color boardBorderColor;
    private final Color gridLineColor;
    private final Color validMoveFill;
    private final Color validMoveStroke;
    private final Color currentPlayerGlow;
    private final Color playerOutlineColor;
    private final Color wallBorderColor;

    // Status panel colors
    private final Color statusPanelBackground;
    private final Color statusPanelBorder;
    private final Color statusSectionBackground;
    private final Color statusCurrentTurnBackground;
    private final Color statusCurrentTurnBorder;
    private final Color statusTextPrimary;
    private final Color statusTextSecondary;
    private final Color statusMessageColor;
    private final Color statusDividerColor;
    private final Color themeToggleBackground;
    private final Color themeToggleForeground;

    private ThemePalette(Builder builder) {
        this.mode = builder.mode;
        this.frameBackground = builder.frameBackground;
        this.boardBackground = builder.boardBackground;
        this.boardGradientTop = builder.boardGradientTop;
        this.boardGradientBottom = builder.boardGradientBottom;
        this.boardBorderColor = builder.boardBorderColor;
        this.gridLineColor = builder.gridLineColor;
        this.validMoveFill = builder.validMoveFill;
        this.validMoveStroke = builder.validMoveStroke;
        this.currentPlayerGlow = builder.currentPlayerGlow;
        this.playerOutlineColor = builder.playerOutlineColor;
        this.wallBorderColor = builder.wallBorderColor;
        this.statusPanelBackground = builder.statusPanelBackground;
        this.statusPanelBorder = builder.statusPanelBorder;
        this.statusSectionBackground = builder.statusSectionBackground;
        this.statusCurrentTurnBackground = builder.statusCurrentTurnBackground;
        this.statusCurrentTurnBorder = builder.statusCurrentTurnBorder;
        this.statusTextPrimary = builder.statusTextPrimary;
        this.statusTextSecondary = builder.statusTextSecondary;
        this.statusMessageColor = builder.statusMessageColor;
        this.statusDividerColor = builder.statusDividerColor;
        this.themeToggleBackground = builder.themeToggleBackground;
        this.themeToggleForeground = builder.themeToggleForeground;
    }

    public Mode mode() {
        return mode;
    }

    public Color frameBackground() {
        return frameBackground;
    }

    public Color boardBackground() {
        return boardBackground;
    }

    public Color boardGradientTop() {
        return boardGradientTop;
    }

    public Color boardGradientBottom() {
        return boardGradientBottom;
    }

    public Color boardBorderColor() {
        return boardBorderColor;
    }

    public Color gridLineColor() {
        return gridLineColor;
    }

    public Color validMoveFill() {
        return validMoveFill;
    }

    public Color validMoveStroke() {
        return validMoveStroke;
    }

    public Color currentPlayerGlow() {
        return currentPlayerGlow;
    }

    public Color playerOutlineColor() {
        return playerOutlineColor;
    }

    public Color wallBorderColor() {
        return wallBorderColor;
    }

    public Color statusPanelBackground() {
        return statusPanelBackground;
    }

    public Color statusPanelBorder() {
        return statusPanelBorder;
    }

    public Color statusSectionBackground() {
        return statusSectionBackground;
    }

    public Color statusCurrentTurnBackground() {
        return statusCurrentTurnBackground;
    }

    public Color statusCurrentTurnBorder() {
        return statusCurrentTurnBorder;
    }

    public Color statusTextPrimary() {
        return statusTextPrimary;
    }

    public Color statusTextSecondary() {
        return statusTextSecondary;
    }

    public Color statusMessageColor() {
        return statusMessageColor;
    }

    public Color statusDividerColor() {
        return statusDividerColor;
    }

    public Color themeToggleBackground() {
        return themeToggleBackground;
    }

    public Color themeToggleForeground() {
        return themeToggleForeground;
    }

    public boolean isDark() {
        return mode == Mode.DARK;
    }

    public static ThemePalette light() {
        return new Builder(Mode.LIGHT)
                .frameBackground(new Color(0xfaf6ed))
                .boardBackground(new Color(240, 236, 227))
                .boardGradientTop(new Color(246, 229, 192))
                .boardGradientBottom(new Color(223, 190, 142))
                .boardBorderColor(new Color(154, 123, 91))
                .gridLineColor(new Color(72, 59, 48))
                .validMoveFill(new Color(46, 204, 113, 160))
                .validMoveStroke(new Color(27, 156, 88, 200))
                .currentPlayerGlow(new Color(255, 255, 255, 160))
                .playerOutlineColor(new Color(45, 45, 45, 160))
                .wallBorderColor(new Color(54, 47, 43, 140))
                .statusPanelBackground(new Color(0xf5f0e6))
                .statusPanelBorder(new Color(0xb8a490))
                .statusSectionBackground(new Color(0xeae2d6))
                .statusCurrentTurnBackground(new Color(0xd4f1f4))
                .statusCurrentTurnBorder(new Color(0x75b9be))
                .statusTextPrimary(new Color(0x2f4858))
                .statusTextSecondary(new Color(0x5d554f))
                .statusMessageColor(new Color(0x4a535a))
                .statusDividerColor(new Color(0xcdbcaa))
                .themeToggleBackground(new Color(0xd7cfc1))
                .themeToggleForeground(new Color(0x2f2f2f))
                .build();
    }

    public static ThemePalette dark() {
        return new Builder(Mode.DARK)
                .frameBackground(new Color(0x1a1f24))
                .boardBackground(new Color(0x1f262d))
                .boardGradientTop(new Color(0x27313b))
                .boardGradientBottom(new Color(0x13191f))
                .boardBorderColor(new Color(0x3a4752))
                .gridLineColor(new Color(0x6f7c87))
                .validMoveFill(new Color(52, 160, 164, 170))
                .validMoveStroke(new Color(46, 204, 201, 210))
                .currentPlayerGlow(new Color(255, 255, 255, 110))
                .playerOutlineColor(new Color(220, 220, 220, 180))
                .wallBorderColor(new Color(30, 30, 30, 180))
                .statusPanelBackground(new Color(0x232a31))
                .statusPanelBorder(new Color(0x3c4a55))
                .statusSectionBackground(new Color(0x2b343d))
                .statusCurrentTurnBackground(new Color(0x31414f))
                .statusCurrentTurnBorder(new Color(0x4f93a8))
                .statusTextPrimary(new Color(0xe8eef4))
                .statusTextSecondary(new Color(0xbac4ce))
                .statusMessageColor(new Color(0x9bb0c2))
                .statusDividerColor(new Color(0x3f4a54))
                .themeToggleBackground(new Color(0x34404a))
                .themeToggleForeground(new Color(0xe8eef4))
                .build();
    }

    private static final class Builder {
        private final Mode mode;
        private Color frameBackground;
        private Color boardBackground;
        private Color boardGradientTop;
        private Color boardGradientBottom;
        private Color boardBorderColor;
        private Color gridLineColor;
        private Color validMoveFill;
        private Color validMoveStroke;
        private Color currentPlayerGlow;
        private Color playerOutlineColor;
        private Color wallBorderColor;
        private Color statusPanelBackground;
        private Color statusPanelBorder;
        private Color statusSectionBackground;
        private Color statusCurrentTurnBackground;
        private Color statusCurrentTurnBorder;
        private Color statusTextPrimary;
        private Color statusTextSecondary;
        private Color statusMessageColor;
        private Color statusDividerColor;
        private Color themeToggleBackground;
        private Color themeToggleForeground;

        private Builder(Mode mode) {
            this.mode = mode;
        }

        private Builder frameBackground(Color value) { this.frameBackground = value; return this; }
        private Builder boardBackground(Color value) { this.boardBackground = value; return this; }
        private Builder boardGradientTop(Color value) { this.boardGradientTop = value; return this; }
        private Builder boardGradientBottom(Color value) { this.boardGradientBottom = value; return this; }
        private Builder boardBorderColor(Color value) { this.boardBorderColor = value; return this; }
        private Builder gridLineColor(Color value) { this.gridLineColor = value; return this; }
        private Builder validMoveFill(Color value) { this.validMoveFill = value; return this; }
        private Builder validMoveStroke(Color value) { this.validMoveStroke = value; return this; }
        private Builder currentPlayerGlow(Color value) { this.currentPlayerGlow = value; return this; }
        private Builder playerOutlineColor(Color value) { this.playerOutlineColor = value; return this; }
        private Builder wallBorderColor(Color value) { this.wallBorderColor = value; return this; }
        private Builder statusPanelBackground(Color value) { this.statusPanelBackground = value; return this; }
        private Builder statusPanelBorder(Color value) { this.statusPanelBorder = value; return this; }
        private Builder statusSectionBackground(Color value) { this.statusSectionBackground = value; return this; }
        private Builder statusCurrentTurnBackground(Color value) { this.statusCurrentTurnBackground = value; return this; }
        private Builder statusCurrentTurnBorder(Color value) { this.statusCurrentTurnBorder = value; return this; }
        private Builder statusTextPrimary(Color value) { this.statusTextPrimary = value; return this; }
        private Builder statusTextSecondary(Color value) { this.statusTextSecondary = value; return this; }
        private Builder statusMessageColor(Color value) { this.statusMessageColor = value; return this; }
        private Builder statusDividerColor(Color value) { this.statusDividerColor = value; return this; }
        private Builder themeToggleBackground(Color value) { this.themeToggleBackground = value; return this; }
        private Builder themeToggleForeground(Color value) { this.themeToggleForeground = value; return this; }

        private ThemePalette build() {
            return new ThemePalette(this);
        }
    }
}

