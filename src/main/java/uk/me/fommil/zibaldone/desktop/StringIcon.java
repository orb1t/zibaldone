/*
 * Created 02-Aug-2012
 * 
 * Copyright Samuel Halliday 2012
 * PROPRIETARY/CONFIDENTIAL. Use is subject to licence terms.
 */
package uk.me.fommil.zibaldone.desktop;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import javax.swing.Icon;
import lombok.Getter;
import lombok.Setter;

/**
 * Shows a {@link AttributedString} as an {@link Icon} while taking care of line wrapping.
 * Note that attributes - such as the font and foreground colour - should be
 * set using {@link AttributedString#addAttribute(Attribute, Object)}, e.g.
 * <code><pre>
 * text.addAttribute(TextAttribute.FONT, font);
 * text.addAttribute(TextAttribute.FOREGROUND, foreground);
 * </pre></code>
 *
 * @author Samuel Halliday
 * @see <a href="http://docs.oracle.com/javase/tutorial/2d/text/drawmulstring.html">Java 2D Multi-Strings</a>
 */
public class StringIcon implements Icon {

    @Getter @Setter
    private AttributedString text;

    @Getter @Setter
    private Color background;

    @Getter @Setter
    private Dimension maximum;

    private static final int DEFAULT_WIDTH = 60;

    private static final int DEFAULT_HEIGHT = 40;

    @Override
    public int getIconHeight() {
        return maximum != null ? maximum.height : DEFAULT_HEIGHT;
    }

    @Override
    public int getIconWidth() {
        return maximum != null ? maximum.width : DEFAULT_WIDTH;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(x, y);

        int width = getIconWidth();
        int height = getIconHeight();

        if (getBackground() != null) {
            g2d.setColor(getBackground());
            g.fillRect(0, 0, width, height);
        }

        FontRenderContext frc = g2d.getFontRenderContext();
        LineBreakMeasurer lineMeasurer = new LineBreakMeasurer(text.getIterator(), frc);

        float drawPosY = 0;
        TextLayout layout;
        while ((layout = lineMeasurer.nextLayout(width)) != null) {
            drawPosY += layout.getAscent();
            if (drawPosY > height) {
                break;
            }
            layout.draw(g2d, 0, drawPosY);
            drawPosY += layout.getDescent() + layout.getLeading();
        }

        g2d.translate(-x, -y);
    }
}
