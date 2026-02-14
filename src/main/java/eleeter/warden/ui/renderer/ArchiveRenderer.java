package eleeter.warden.ui.renderer;

import eleeter.warden.utils.config.Colors;
import eleeter.warden.utils.config.RendererConfig;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;

public class ArchiveRenderer
{

    public static FileUpload render(Member suspect, List<Member> reporters, Member admin, String verdict, String reason, String time)
    {
        int w = RendererConfig.A_WIDTH, h = RendererConfig.A_HEIGHT;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        g2.setColor(Colors.BG_MAIN);
        g2.fillRect(0, 0, w, h);

        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 60));
        g2.setColor(new Color(52, 152, 219));
        g2.drawString(RendererConfig.AR_HEADER_TITLE, 80, 110);
        g2.fillRect(80, 145, 1760, 6);

        /** SUSPECT (With Glow) */
        drawSuspectCard(g2, 80, 220, suspect);

        drawReporterGrid(g2, 680, 220, reporters, Colors.GRAY);

        drawSingleCard(g2, 1280, 220, RendererConfig.AR_ADJUDICATOR_LABEL, admin, Colors.GREEN);

        g2.setColor(new Color(20, 20, 20));
        g2.fillRoundRect(80, 780, 1760, 240, 40, 40);

        g2.setFont(new Font(RendererConfig.RR_FONT_CONSOLAS, Font.BOLD, 55));
        g2.setColor(Color.WHITE);
        g2.drawString(RendererConfig.AR_VERDICT_PREFIX + verdict.toUpperCase(), 130, 865);

        g2.setFont(new Font(RendererConfig.RR_FONT_CONSOLAS, Font.PLAIN, 38));
        g2.setColor(Color.LIGHT_GRAY);
        g2.drawString(RendererConfig.AR_REASON_PREFIX + reason, 130, 930);

        g2.setFont(new Font(RendererConfig.RR_FONT_CONSOLAS, Font.PLAIN, 30));
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(RendererConfig.AR_TIMESTAMP_PREFIX + time, 130, 985);

        g2.dispose();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            ImageIO.write(img, "png", baos);
            return FileUpload.fromData(baos.toByteArray(), RendererConfig.AR_FILE_NAME);
        } catch (Exception e)
        {
            return null;
        }
    }

    private static void drawSuspectCard(Graphics2D g2, int x, int y, Member m)
    {
        if (m == null) return;
        int imgX = x + 150, imgY = y + 80, size = 280;

        for (int i = 1; i <= 15; i++)
        {
            g2.setColor(new Color(255, 0, 0, 45 / i));
            g2.setStroke(new BasicStroke(i * 2));
            g2.drawRoundRect(imgX - i, imgY - i, size + (i * 2), size + (i * 2), 65, 65);
        }

        try
        {
            BufferedImage av = ImageIO.read(new URL(m.getUser().getEffectiveAvatarUrl()));
            g2.setClip(new RoundRectangle2D.Double(imgX, imgY, size, size, 60, 60));
            g2.drawImage(av, imgX, imgY, size, size, null);
            g2.setClip(null);

            g2.setStroke(new BasicStroke(8f));
            g2.setColor(Color.RED);
            g2.drawRoundRect(imgX, imgY, size, size, 60, 60);
        } catch (Exception ignored)
        {
            ignored.printStackTrace();
        }

        g2.setColor(Color.RED);
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 35));
        g2.drawString(RendererConfig.AR_SUSPECT_LABEL, imgX, y + 50);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 45));
        g2.drawString(m.getEffectiveName(), imgX, y + 420);
    }

    private static void drawSingleCard(Graphics2D g2, int x, int y, String title, Member m, Color accent)
    {
        if (m == null) return;
        int imgX = x + 150, imgY = y + 80, size = 280;
        try
        {
            BufferedImage av = ImageIO.read(new URL(m.getUser().getEffectiveAvatarUrl()));
            g2.setClip(new RoundRectangle2D.Double(imgX, imgY, size, size, 60, 60));
            g2.drawImage(av, imgX, imgY, size, size, null);
            g2.setClip(null);

            g2.setStroke(new BasicStroke(6f));
            g2.setColor(accent);
            g2.drawRoundRect(imgX, imgY, size, size, 60, 60);
        } catch (Exception ignored)
        {
            ignored.printStackTrace();
        }

        g2.setColor(accent);
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 35));
        g2.drawString(title, imgX, y + 50);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 45));
        g2.drawString(m.getEffectiveName(), imgX, y + 420);
    }

    private static void drawReporterGrid(Graphics2D g2, int x, int y, List<Member> reporters, Color accent)
    {
        if (reporters == null || reporters.isEmpty()) return;

        int startX = x + 150, startY = y + 80, totalArea = 280;
        int count = reporters.size();
        int cols = (int) Math.ceil(Math.sqrt(Math.min(count, 25)));
        if (cols < 2 && count > 1) cols = 2;

        int pfpSize = (totalArea / cols) - 4;
        int maxVisible = cols * cols;

        for (int i = 0; i < Math.min(count, maxVisible); i++)
        {
            int row = i / cols, col = i % cols;
            int curX = startX + (col * (pfpSize + 4)), curY = startY + (row * (pfpSize + 4));

            if (i == maxVisible - 1 && count > maxVisible)
            {
                g2.setColor(new Color(0, 0, 0, 200));
                g2.fillRoundRect(curX, curY, pfpSize, pfpSize, 15, 15);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, pfpSize / 2));
                String text = "+" + (count - maxVisible + 1);
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(text, curX + (pfpSize - fm.stringWidth(text)) / 2, curY + (pfpSize + fm.getAscent() - fm.getDescent()) / 2);
            } else
            {
                try
                {
                    BufferedImage av = ImageIO.read(new URL(reporters.get(i).getUser().getEffectiveAvatarUrl()));
                    g2.setClip(new RoundRectangle2D.Double(curX, curY, pfpSize, pfpSize, 15, 15));
                    g2.drawImage(av, curX, curY, pfpSize, pfpSize, null);
                    g2.setClip(null);
                } catch (Exception ignored)
                {
                    ignored.printStackTrace();
                }
            }
        }

        g2.setColor(accent);
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 35));
        g2.drawString(count > 1 ? RendererConfig.AR_REPORTERS_LABEL : RendererConfig.AR_REPORTER_LABEL, startX, y + 50);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font(RendererConfig.RR_FONT_SEGOE_UI, Font.BOLD, 45));
        String name = reporters.get(0).getEffectiveName() + (count > 1 ? " +" + (count - 1) : "");
        g2.drawString(name, startX, y + 420);
    }
}