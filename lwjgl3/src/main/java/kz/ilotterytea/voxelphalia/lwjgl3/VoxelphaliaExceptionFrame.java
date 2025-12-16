package kz.ilotterytea.voxelphalia.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.GLVersion;
import kz.ilotterytea.voxelphalia.VoxelphaliaConstants;
import kz.ilotterytea.voxelphalia.utils.OSUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VoxelphaliaExceptionFrame extends JFrame {

    public VoxelphaliaExceptionFrame(Exception e) {
        super(String.format("Oops... It looks like %s just crashed...", VoxelphaliaConstants.Metadata.APP_NAME));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(800, 600);
        setMinimumSize(new Dimension(800, 600));

        Color BG_COLOR = new Color(0x87ceeb);

        JPanel contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        setContentPane(contentPanel);

        getContentPane().setBackground(BG_COLOR);

        // --- game logo ---
        JLabel logo = new JLabel(new ImageIcon(getClass().getClassLoader().getResource("textures/gui/voxelphalia.png")));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(new EmptyBorder(0, 0, 16, 0));
        add(logo);

        // --- saving exception ---
        StringWriter out = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        writer.write(String.format("%s has just crashed!\n", VoxelphaliaConstants.Metadata.APP_NAME));
        writer.write("----------------------------------------\n\n");

        writer.write("To report this, copy all of the text below and email it to voxelphalia@alright.party\n");
        writer.write("Include a brief description of the steps you took before the error appeared.\n\n");

        writer.write("--- BEGIN CRASH REPORT ---\n");

        writer.write(String.format("Time: %s\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").format(new Date())));
        writer.write(String.format("Voxelphalia: %s\n", VoxelphaliaConstants.Metadata.APP_VERSION));
        writer.write(String.format("OS: %s %s (%s)\n", System.getProperty("os.name", "N/A"), System.getProperty("os.version", "N/A"), System.getProperty("os.arch", "N/A")));
        writer.write(String.format("Java: %s\n", System.getProperty("java.version", "N/A")));
        writer.write(String.format("VM: %s\n", System.getProperty("java.vm.name", "N/A")));

        GLVersion glv = Gdx.graphics.getGLVersion();
        writer.write(String.format("OpenGL: %s (%s %s)\n\n",
            glv.getRendererString(), glv.getType(), glv.getVersionString()
        ));

        e.printStackTrace(writer);
        writer.write("--- END CRASH REPORT ---");
        writer.flush();

        File directory = new File(OSUtils.getUserDataDirectory(String.format("%s/%s/crashreports",
            VoxelphaliaConstants.Metadata.APP_DEV,
            VoxelphaliaConstants.Metadata.APP_ID
        )));

        if (!directory.exists()) {
            directory.mkdirs();
        }

        File reportFile = new File(String.format("%s/report-%s.log", directory.getAbsolutePath(), "123"));
        boolean reportSaved = false;

        try {
            FileOutputStream fos = new FileOutputStream(reportFile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeUTF(out.toString());
            oos.close();
            fos.close();
            reportSaved = true;
        } catch (IOException ignored) {
        }

        // --- displaying exception ---
        JTextArea exceptionArea = new JTextArea(out.toString());
        exceptionArea.setEditable(false);
        exceptionArea.setCursor(null);
        exceptionArea.setOpaque(false);
        exceptionArea.setFocusable(true);
        exceptionArea.setWrapStyleWord(true);
        exceptionArea.setLineWrap(true);
        exceptionArea.setBackground(Color.gray);

        JScrollPane exceptionScroll = new JScrollPane(exceptionArea);
        exceptionScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        exceptionScroll.setBackground(Color.WHITE);
        exceptionScroll.setBorder(new LineBorder(new Color(0x243238), 1));
        exceptionScroll.setAlignmentX(Component.CENTER_ALIGNMENT);
        exceptionScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        add(exceptionScroll);

        // --- buttons ---
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
        buttonPanel.setLayout(new GridLayout(1, 2, 8, 8));
        buttonPanel.setBackground(BG_COLOR);
        add(buttonPanel);

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(actionEvent -> dispose());
        buttonPanel.add(closeButton);

        JButton copyButton = new JButton("Copy to clipboard");
        copyButton.addActionListener(actionEvent -> Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(out.toString()), null));
        buttonPanel.add(copyButton);

        if (reportSaved) {
            JButton reportButton = new JButton("View crash report");
            reportButton.addActionListener(actionEvent -> {
                try {
                    Desktop.getDesktop().open(reportFile);
                } catch (IOException ignored) {
                }
            });
            buttonPanel.add(reportButton);
        }

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
