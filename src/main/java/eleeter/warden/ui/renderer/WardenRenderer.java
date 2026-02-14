package eleeter.warden.ui.renderer;

import eleeter.warden.utils.config.Colors;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;

public class WardenRenderer
{

    /**
     * TODO: Lazy to add color class and I'm too tired
     * But the main thing is that this is working!
     * Same Rendering Concept From Eimi Bot
     **/
    public static FileUpload render(String user, String avatarUrl, String content, String time, String violationDetail)
    {
        int w = 800;
        int h = 200;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(Colors.BG_DARK);
        g2.fillRoundRect(10, 10, w - 20, h - 20, 20, 20);

        for (int i = 1; i <= 5; i++)
        {
            g2.setColor(new Color(0, 150, 255, 25 - (i * 4)));
            g2.drawRoundRect(10 - i, 10 - i, (w - 20) + (i * 2), (h - 20) + (i * 2), 20 + i, 20 + i);
        }

        try
        {
            BufferedImage av = ImageIO.read(new URL(avatarUrl));
            g2.setClip(new Ellipse2D.Double(40, 50, 80, 80));
            g2.drawImage(av, 40, 50, 80, 80, null);
            g2.setClip(null);
            g2.setColor(Colors.ACCENT_BLUE);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new Ellipse2D.Double(40, 50, 80, 80));
        } catch (Exception ignored)
        {
        }

        g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
        g2.setColor(Colors.ACCENT_BLUE);
        g2.drawString("SEC-LOG // " + violationDetail.toUpperCase(), 150, 55);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g2.setColor(Color.WHITE);
        g2.drawString(user, 150, 90);

        g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g2.setColor(new Color(150, 150, 160));
        g2.drawString("Timestamp: " + time, 150, 110);

        g2.setColor(Colors.GLASS_BG);
        g2.fillRoundRect(150, 125, 600, 45, 10, 10);
        g2.setColor(Colors.BORDER_COLOR);
        g2.drawRoundRect(150, 125, 600, 45, 10, 10);

        g2.setFont(new Font("Consolas", Font.PLAIN, 18));
        g2.setColor(new Color(220, 220, 230));

        String displayContent = content.replace("\n", " ");
        String msg = displayContent.length() > 50 ? displayContent.substring(0, 47) + "..." : displayContent;
        g2.drawString("> " + msg, 165, 155);

        g2.dispose();

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream())
        {
            ImageIO.write(img, "png", baos);
            return FileUpload.fromData(baos.toByteArray(), "proof.png");
        } catch (Exception e)
        {
            return null;
        }
    }


    /**
     * I was a victim of ghost-pinging — not anymore :)
     * I've been using Discord since I was 11,
     * so I know exactly how annoying it is.
     */

}