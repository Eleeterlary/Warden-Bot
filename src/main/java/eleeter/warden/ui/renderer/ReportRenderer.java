package eleeter.warden.ui.renderer;

import eleeter.warden.utils.config.Colors;
import eleeter.warden.utils.config.Config;
import eleeter.warden.utils.config.RendererConfig;
import net.dv8tion.jda.api.utils.FileUpload;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReportRenderer
{


    /*

      1 <--> 2
      |      |
      3 <--> 4

    */
    private static class WardenBox
    {
        int x, y, w, h;

        public WardenBox(int x, int y, int w, int h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }

        public void drawGlass(Graphics2D g2)
        {
            g2.setColor(Colors.PANEL_BG);
            g2.fillRoundRect(x, y, w, h, RendererConfig.ARC_WIDTH, RendererConfig.ARC_HEIGHT);
            g2.setStroke(new BasicStroke(RendererConfig.GLASS_STROKE));
            g2.setColor(Colors.SOFT_BLACK);
            g2.drawRoundRect(x, y, w, h, RendererConfig.ARC_WIDTH, RendererConfig.ARC_HEIGHT);
        }

        public void drawDanger(Graphics2D g2)
        {
            g2.setColor(Colors.VERY_TRANSPARENT_RED);
            g2.fillRoundRect(x, y, w, h, RendererConfig.ARC_WIDTH, RendererConfig.ARC_HEIGHT);
            g2.setStroke(new BasicStroke(RendererConfig.GLASS_STROKE));
            g2.setColor(Colors.FAINT_RED);
            g2.drawRoundRect(x, y, w, h, RendererConfig.ARC_WIDTH, RendererConfig.ARC_HEIGHT);
        }
    }

    public static FileUpload render(String targetName, String targetAvatar, List<String> reporterAvatars, String reason, int unique, int total, String joinedDate, String reportTime)
    {
        int w = 1920;
        int baseHeight = 880;

        BufferedImage tempImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gMeasure = tempImg.createGraphics();
        Font evidenceFont = new Font(RendererConfig.RR_FONT_CONSOLAS, Font.PLAIN, 42);

        List<String> wrappedLines = wrapText(reason, evidenceFont, 1660, gMeasure);
        gMeasure.dispose();

        int lineHeight = 55;
        int evidenceBoxHeight = Math.max(220, (wrappedLines.size() * lineHeight) + 110);
        int finalHeight = baseHeight + evidenceBoxHeight + 80;

        BufferedImage img = new BufferedImage(w, finalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        g2.setColor(Colors.BG_MAIN);
        g2.fillRect(0, 0, w, finalHeight);

        drawHeader(g2, 80, 80);

        drawProfileSection(g2, 80, 180, targetName, targetAvatar, joinedDate, reporterAvatars, reportTime);

        WardenBox uniqueBox = new WardenBox(80, 620, 850, 220);
        uniqueBox.drawGlass(g2);
        drawMetricContent(g2, uniqueBox, RendererConfig.RR_UNIQUE_REPORTERS, String.valueOf(unique));

        WardenBox totalBox = new WardenBox(990, 620, 850, 220);
        totalBox.drawGlass(g2);
        drawMetricContent(g2, totalBox, RendererConfig.RR_TOTAL_ATTEMPTS, String.valueOf(total));

        WardenBox evidenceBox = new WardenBox(80, 880, 1760, evidenceBoxHeight);
        evidenceBox.drawDanger(g2);
        drawEvidenceContent(g2, evidenceBox, wrappedLines, evidenceFont);

        g2.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            ImageIO.write(img, Config.IMAGE_FORMAT, baos);
            return FileUpload.fromData(baos.toByteArray(), RendererConfig.RR_FILE_NAME);
        } catch (Exception e)
        {
            return null;
        }
    }

    private static void drawHeader(Graphics2D g2, int x, int y)
    {
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 52));
        g2.setColor(Colors.ACCENT_RED);
        g2.drawString(RendererConfig.RR_HEADER_TITLE, x, y);

        g2.setStroke(new BasicStroke(4f));
        g2.setColor(Colors.SOFT_RED);
        g2.drawLine(x, y + 35, 1840, y + 35);
    }

    private static void drawProfileSection(Graphics2D g2, int x, int y, String name, String avatarUrl, String joined, List<String> reporters, String time)
    {
        int avatarSize = 380;

        try
        {
            BufferedImage av = ImageIO.read(new URL(avatarUrl));
            g2.setClip(new RoundRectangle2D.Double(x, y, avatarSize, avatarSize, 65, 65));
            g2.drawImage(av, x, y, avatarSize, avatarSize, null);
            g2.setClip(null);

            g2.setColor(Colors.ACCENT_RED);
            g2.setStroke(new BasicStroke(12f));
            g2.drawRoundRect(x, y, avatarSize, avatarSize, 65, 65);
        } catch (Exception ignored)
        {
        }

        int textX = x + avatarSize + 60;

        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 95));
        g2.setColor(Colors.TEXT_PRIMARY);
        g2.drawString(name, textX, y + 95);

        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.PLAIN, 42));
        g2.setColor(Colors.TEXT_SECONDARY);
        g2.drawString(RendererConfig.RR_SERVER_ENTRY + joined, textX, y + 175);

        String label = (reporters.size() > 1) ? RendererConfig.RR_REPORTERS_PLURAL : RendererConfig.RR_REPORTERS_SINGULAR;
        g2.setColor(Colors.ACCENT_RED);
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 42));
        g2.drawString(label, textX, y + 250);

        int gridX = textX;
        int gridY = y + 270;
        int maxRowWidth = 1200;

        int pfpSize = (reporters.size() <= 6) ? 100 : Math.max(50, (maxRowWidth / reporters.size()) - 10);
        int gap = 12;
        int maxVisible = 15;

        for (int i = 0; i < Math.min(reporters.size(), maxVisible); i++)
        {
            try
            {
                BufferedImage rAv = ImageIO.read(new URL(reporters.get(i)));
                int curX = gridX + (i * (pfpSize + gap));

                g2.setClip(new RoundRectangle2D.Double(curX, gridY, pfpSize, pfpSize, 25, 25));
                g2.drawImage(rAv, curX, gridY, pfpSize, pfpSize, null);
                g2.setClip(null);

                g2.setStroke(new BasicStroke(3f));
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawRoundRect(curX, gridY, pfpSize, pfpSize, 25, 25);
            } catch (Exception ignored)
            {
            }
        }

        if (reporters.size() > maxVisible)
        {
            g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 35));
            g2.setColor(Colors.TEXT_SECONDARY);
            g2.drawString("+" + (reporters.size() - maxVisible), gridX + (maxVisible * (pfpSize + gap)) + 10, gridY + (pfpSize / 2) + 12);
        }

        g2.setColor(Colors.ACCENT_RED);
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 42));
        g2.drawString(RendererConfig.RR_REPORTED_AT + time, textX, y + 250 + pfpSize + 80);
    }

    private static void drawMetricContent(Graphics2D g2, WardenBox box, String label, String value)
    {
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 36));
        g2.setColor(Colors.ACCENT_RED);
        g2.drawString(label, box.x + 45, box.y + 75);

        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 105));
        g2.setColor(Colors.TEXT_PRIMARY);
        g2.drawString(value, box.x + 45, box.y + 185);
    }

    private static void drawEvidenceContent(Graphics2D g2, WardenBox box, List<String> lines, Font font)
    {
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 36));
        g2.setColor(Colors.ACCENT_RED);
        g2.drawString(RendererConfig.RR_SUBMITTED_EVIDENCE, box.x + 45, box.y + 65);

        g2.setFont(font);
        g2.setColor(new Color(235, 235, 245));
        int currentY = box.y + 135;
        for (String line : lines)
        {
            g2.drawString(RendererConfig.RR_EVIDENCE_PREFIX + line.trim(), box.x + 55, currentY);
            currentY += 55;
        }
    }

    private static List<String> wrapText(String text, Font font, int maxWidth, Graphics2D g2)
    {
        List<String> lines = new ArrayList<>();
        FontMetrics fm = g2.getFontMetrics(font);
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words)
        {
            if (fm.stringWidth(currentLine + " " + word) < maxWidth)
            {
                if (currentLine.length() > 0) currentLine.append(" ");
                currentLine.append(word);
            } else
            {
                lines.add(currentLine.toString());
                currentLine = new StringBuilder(word);
            }
        }
        if (currentLine.length() > 0) lines.add(currentLine.toString());
        return lines;
    }
}