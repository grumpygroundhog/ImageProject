import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.MouseInputAdapter;


public class GUIDual implements ActionListener {

    /* These are the strings for the menu items */
    private Map<String, Method> methodMap;

    private JFrame top;
    private GVpicture left, right;
    private JMenu operMenu;
    private JMenuItem open, webOpen, quit;
    private JMenuItem operItems[];
    private JFileChooser fc;

    public GUIDual() {
        top = new JFrame();


        methodMap = new TreeMap<String, Method>();

        /* The following loop collects all the methods
         * in ImageProcessor.java that are declared
         * static and takes a GVpicture parameter
         */
        Method[] imgur = ImageProcessor.class.getDeclaredMethods();
        for (Method m : imgur) {
            Class[] params = m.getParameterTypes();
            if (Modifier.isStatic(m.getModifiers()) == false ||
                    params.length != 1 ||
                    params[0] != GVpicture.class /*||
                    params[1] != Object[].class*/) continue;
            methodMap.put(m.getName(), m);
        }

        /* menu items must be set AFTER the method names are collected */
        setupMenus();

        left = new GVpicture(256, 256, GVpicture.GRAY_TYPE);
        right = new GVpicture(256, 256, GVpicture.GRAY_TYPE);
        right.addMouseListener(new MouseHandler());
        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        fc = new JFileChooser(new File("."));
        top.setVisible(true);
        top.pack();
        top.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void setupMenus() {
        JMenuBar mb = new JMenuBar();
        top.setJMenuBar(mb);
        JMenu file = new JMenu("File");
        mb.add(file);

        open = new JMenuItem("Open Local Image");
        open.addActionListener(this);
        file.add(open);

        webOpen = new JMenuItem("Open Web Image");
        webOpen.addActionListener(this);
        file.add(webOpen);

        file.addSeparator();
        quit = new JMenuItem("Quit");
        file.add(quit);
        quit.addActionListener(this);

        operMenu = new JMenu("Operations");
        operMenu.setVisible(false);
        mb.add(operMenu);
        for (String k : methodMap.keySet()) {
            JMenuItem x = new JMenuItem(k);
            operMenu.add(x);
            x.addActionListener(this);
        }
    }


    /**
     * Use an anonymous class below
     */
    public void actionPerformed(ActionEvent ev) {
        Object which;

        which = ev.getSource();
        if (which == open) {
            int response;

            response = fc.showOpenDialog(top);
            if (response == JFileChooser.APPROVE_OPTION) {
                left.setImage(fc.getSelectedFile());
                right.setImage(left);
                operMenu.setVisible(true);
                top.pack();
            }

        } else if (which == webOpen) {
            String userInp = JOptionPane.showInputDialog(top,
                    "Enter URL of image");
            if (userInp != null) {
                try {
                    left.setImage(new URL(userInp));
                    right.setImage(left);
                    operMenu.setVisible(true);
                    top.pack();
                } catch (MalformedURLException e) {
                    JOptionPane.showMessageDialog(top,
                            "Invalid URL");
                }
            }

        } else if (which == quit) {
            System.exit(0);
        } else {
            if (which.getClass() != JMenuItem.class) return;
            JMenuItem mi = (JMenuItem) which;
            /* Use the method name to look up the actual method */
            Method work = methodMap.get(mi.getActionCommand());
            try {
                right.setImage(left); /* copy left to right */
                /* use Java Reflection to invoke the method */
                work.invoke(null, right);
                top.repaint();  /* refresh the UI */
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private class MouseHandler extends MouseInputAdapter {
        /**
         * {@inheritDoc}
         *
         * @param e
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            int b = e.getButton();
            if (b == MouseEvent.BUTTON3) {
                int response = fc.showSaveDialog(top);
                if (response == JFileChooser.APPROVE_OPTION) {
                        GVpicture img = (GVpicture) e.getSource();
                        img.save (fc.getSelectedFile().getAbsolutePath());
                }
            }
            super.mouseClicked(e);
        }
    }

    public static void main(String[] args) {
        new GUIDual();
    }
}
