package VueControleur.Panels;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuPanelNoir extends JPanel {

    private BufferedImage image;
    private BufferedImage imageRobot;

    public MenuPanelNoir(){
        this.setBackground(Color.black);
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);

        try {
            image = ImageIO.read(new File("Images/Gyromite_Smick.png"));
            imageRobot = ImageIO.read(new File("Images/Robot-Series.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }


        Font fontGyro = new Font("Calibri", Font.BOLD, 80);
        g.setFont(fontGyro);
        g.setColor(Color.white);
        g.drawString("GYROMITE", 100, 65);

        Font fontNom = new Font("Times New Roman", Font.ITALIC, 20);
        g.setFont(fontNom);
        g.setColor(Color.white);
        g.drawString("Votre Nom Ici ", 70, 210);
        
        g.drawRoundRect(45, 180, 170,50,30,30);

        

    }
}
