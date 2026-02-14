package eleeter.warden.ui.renderer;

import eleeter.warden.utils.config.Colors;
import eleeter.warden.utils.config.EmbedConfig;
import eleeter.warden.utils.config.RendererConfig;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;

public class CaseStatusRenderer
{



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

        public void drawSuccess(Graphics2D g2)
        {
            g2.setColor(Colors.VERY_TRANSPARENT_GREEN);
            g2.fillRoundRect(x, y, w, h, RendererConfig.C_UNI, RendererConfig.C_UNI);

            /** Green border glow */
            g2.setStroke(new BasicStroke(RendererConfig.C_ARC));
            g2.setColor(Colors.SOFT_GREEN);
            g2.drawRoundRect(x, y, w, h, RendererConfig.C_UNI, RendererConfig.C_UNI);
        }
    }

    public static FileUpload render(String targetName, String targetAvatar, String actionTaken, String targetId, String joinedDate, String resolutionTime, String reason)
    {
        int w = RendererConfig.C_WIDTH;
        int h = RendererConfig.C_HEIGHT;

        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        g2.setColor(Colors.BG_MAIN);
        g2.fillRect(0, 0, w, h);

        drawHeader(g2, RendererConfig.C_UNI_2, RendererConfig.C_UNI_2);

        drawProfileSection(g2, RendererConfig.C_UNI_2, 180, targetName, targetAvatar, joinedDate, targetId, resolutionTime);

        WardenBox verdictBox = new WardenBox(RendererConfig.C_UNI_2, 620, 1760, 300);
        verdictBox.drawSuccess(g2);
        drawVerdictContent(g2, verdictBox, actionTaken);

        g2.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            ImageIO.write(img, "png", baos);
            return FileUpload.fromData(baos.toByteArray(), RendererConfig.CSR_FILE_NAME);
        } catch (Exception e)
        {
            return null;
        }
    }

    private static void drawHeader(Graphics2D g2, int x, int y)
    {
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 52));
        g2.setColor(Colors.ACCENT_GREEN);
        g2.drawString(RendererConfig.CSR_HEADER_TITLE, x, y);

        g2.setStroke(new BasicStroke(4f));
        g2.setColor(Colors.SOFT_GREEN);
        g2.drawLine(x, y + RendererConfig.C_UNI, 1840, y + RendererConfig.C_UNI);
    }

    private static void drawProfileSection(Graphics2D g2, int x, int y, String name, String avatarUrl, String joined, String id, String time)
    {
        int avatarSize = 380;

        try
        {
            BufferedImage av = ImageIO.read(new URL(avatarUrl));
            g2.setClip(new RoundRectangle2D.Double(x, y, avatarSize, avatarSize, RendererConfig.C_UNI_3, RendererConfig.C_UNI_3));
            g2.drawImage(av, x, y, avatarSize, avatarSize, null);
            g2.setClip(null);

            g2.setColor(Colors.ACCENT_GREEN);
            g2.setStroke(new BasicStroke(12f));
            g2.drawRoundRect(x, y, avatarSize, avatarSize, RendererConfig.C_UNI_3, RendererConfig.C_UNI_3);
        } catch (Exception ignored)
        {
        }

        int textX = x + avatarSize + RendererConfig.C_UNI_5;

        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, RendererConfig.C_UNI_4));
        g2.setColor(Colors.TEXT_PRIMARY);
        g2.drawString(name, textX, y + RendererConfig.C_UNI_4);

        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.PLAIN, RendererConfig.C_UNI_4));
        g2.setColor(Colors.TEXT_SECONDARY);
        g2.drawString(RendererConfig.CSR_SERVER_ENTRY + joined, textX, y + 175);
        g2.drawString(RendererConfig.CSR_SUBJECT_ID + id, textX, y + 235);

        g2.setColor(Colors.ACCENT_GREEN);
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, RendererConfig.C_UNI_4));
        g2.drawString(RendererConfig.CSR_RESOLVED_AT + time, textX, y + 300);
    }

    private static void drawVerdictContent(Graphics2D g2, WardenBox box, String action)
    {
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, RendererConfig.C_UNI_4));
        g2.setColor(Colors.ACCENT_GREEN);
        g2.drawString(RendererConfig.CSR_FINAL_VERDICT, box.x + RendererConfig.C_UNI_5, box.y + 100);

        g2.setFont(new Font(RendererConfig.RR_FONT_CONSOLAS, Font.BOLD, 85));
        g2.setColor(Color.WHITE);
        String fullAction = RendererConfig.CSR_VERDICT_PREFIX + action.toUpperCase();
        g2.drawString(fullAction, box.x + RendererConfig.C_UNI_5, box.y + 220);
    }
}