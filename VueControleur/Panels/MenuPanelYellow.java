package VueControleur.Panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuPanelYellow extends JPanel {

    private BufferedImage image;

    public MenuPanelYellow() {

        this.setPreferredSize(new Dimension(750, 325));
        this.setBackground(Color.yellow);
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        try {
            image = ImageIO.read(new File("Images/menu.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(image,100,50,null);

        Font font = new Font("Calibri", Font.BOLD, 70);
        g.setFont(font);
        g.setColor(Color.black);
        g.drawString("BIENVENUE !", 275, 200);
    }
}
