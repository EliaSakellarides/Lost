package com.lostthesis.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

/**
 * Helper per costruire scene dialog con layout standard:
 * immagine in alto (55%), titolo, testo narrativo e bottoni in basso
 */
public class SceneBuilder {
    private final JFrame parent;
    private final int screenWidth;
    private final int screenHeight;

    public SceneBuilder(JFrame parent, int screenWidth, int screenHeight) {
        this.parent = parent;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public JDialog createFullScreenDialog() {
        JDialog dialog = new JDialog(parent, false);
        dialog.setUndecorated(true);
        dialog.setSize(screenWidth, screenHeight);
        dialog.setLocationRelativeTo(parent);
        return dialog;
    }

    public JPanel createImagePanel(String imageFilename) {
        JPanel imagePanel = new JPanel() {
            private Image image;
            {
                image = loadImageFromClasspath(imageFilename);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (image != null) {
                    g2d.drawImage(image, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(Color.BLACK);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        imagePanel.setPreferredSize(new Dimension(screenWidth, (int)(screenHeight * 0.55)));
        imagePanel.setBackground(Color.BLACK);
        return imagePanel;
    }

    public JPanel createTitlePanel(String title, Color bgColor, Color fgColor) {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(bgColor);
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 26));
        titleLabel.setForeground(fgColor);
        titlePanel.add(titleLabel);
        return titlePanel;
    }

    public JTextArea createSceneText(String text) {
        JTextArea sceneText = new JTextArea();
        sceneText.setEditable(false);
        sceneText.setLineWrap(true);
        sceneText.setWrapStyleWord(true);
        sceneText.setBackground(Color.BLACK);
        sceneText.setForeground(Color.WHITE);
        sceneText.setFont(new Font("Georgia", Font.ITALIC, 17));
        sceneText.setText(text);
        return sceneText;
    }

    public JPanel createButtonPanel(JButton... buttons) {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(Color.BLACK);
        for (JButton btn : buttons) {
            btnPanel.add(btn);
        }
        return btnPanel;
    }

    /**
     * Assembla il layout standard di una scena.
     * @param statusBar opzionale, puo essere null
     */
    public void assembleStandardScene(JDialog dialog, JPanel imagePanel,
                                       JPanel titlePanel, JTextArea sceneText,
                                       JPanel buttonPanel, JPanel statusBar) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.BLACK);

        if (statusBar != null) {
            mainPanel.add(statusBar, BorderLayout.NORTH);
        }

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBackground(Color.BLACK);
        textPanel.setBorder(BorderFactory.createEmptyBorder(15, 50, 15, 50));
        textPanel.add(sceneText, BorderLayout.CENTER);
        textPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(imagePanel, BorderLayout.CENTER);
        if (titlePanel != null) {
            topPanel.add(titlePanel, BorderLayout.SOUTH);
        }

        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(textPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
    }

    public static Image loadImageFromClasspath(String filename) {
        try {
            InputStream is = SceneBuilder.class.getResourceAsStream("/images/" + filename);
            if (is != null) {
                Image img = ImageIO.read(is);
                is.close();
                return img;
            }
        } catch (Exception e) {
            System.out.println("Immagine non trovata: " + filename);
        }
        return null;
    }
}
