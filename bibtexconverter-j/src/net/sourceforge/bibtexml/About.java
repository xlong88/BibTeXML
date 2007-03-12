package net.sourceforge.bibtexml;

/* Mp3dings - manage mp3 meta-information
 * Copyright (C) 2003 Moritz Ringler
 * $Id: About.java,v 1.9 2007/03/10 23:00:21 Moritz.Ringler Exp $
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.prefs.Preferences;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import de.mospace.lang.*;
import de.mospace.swing.ExtensionInstaller;
import de.mospace.swing.PathInput;

/**
 * A Dialog that shows author and license information, opened
 * with the Help->About menu command.
 * @author Moritz Ringler
 * @version $Revision: 1.9 $ ($Date: 2007/03/10 23:00:21 $)
 */
final class About extends JDialog implements ActionListener {

    public About(JFrame parent, ImageIcon logo, String saxonVersion) {
        super(parent);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        init(parent, logo, saxonVersion);
        pack();
    }

    /**Component initialization*/
    private void init(JFrame parent, ImageIcon logo, String saxonVersion) {
        Container cp = getContentPane();
        setTitle("About BibTeXConverter");
        setResizable(false);

        JLabel image = new JLabel(logo);
        JLabel text = new JLabel(
            "<html>" +
            "<b>BibTeXConverter alpha-1</b><br>" +

            /*
            "<a href=\"http://mp3dings.sourceforge.net\">" +
            "http://mp3dings.sourceforge.net</a><br><br>" +
            */

            "&copy; Moritz Ringler, 2006<br><br>" +
            "<b>BibTeX-Parsers:</b><br>" +
            "<u>texlipse.sf.net</u><br>&copy; Oskar Ojala, 2004-2005<br>" +
            "<u>bibtexml.sf.net</u><br>bibtex2xml.py: &copy; Vidar Bronken Gundersen, Sara Sprenkle<br>" +
            "Java port: &copy; Moritz Ringler, 2006<br><br>" +
            ((saxonVersion == null)? "" : ("<b>XSLT-Processor:</b><br>"  + saxonVersion + "<br><br>"))
            + "Running on <br>" +
            "Java " + System.getProperty("java.version")+
            " (" + System.getProperty("java.vendor") + ")<br></html>"
        );
        text.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 10));

        JTextArea license = new JTextArea(
        "This program is free software; you can redistribute it and/or\n"+
        "modify it under the terms of the GNU General Public License\n"+
        "as published by the Free Software Foundation; either version 2\n"+
        "of the License, or (at your option) any later version.\n"+
        "\n"+
        "This program is distributed in the hope that it will be useful,\n"+
        "but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
        "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
        "GNU General Public License for more details.\n"+
        "\n"+
        "You should have received a copy of the GNU General Public License\n"+
        "along with this program; if not, write to the Free Software\n"+
        "Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA\n"+
        "\n" );
        license.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(5,5,5,5, cp.getBackground()),
            BorderFactory.createEmptyBorder(5,5,5,5)
        ));

        JButton button =
                new JButton(UIManager.getString("OptionPane.okButtonText"));
        button.addActionListener(this);

        cp.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        cp.add(image, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 1;

        cp.add(text, gbc);

        gbc.gridwidth = 2;
        gbc.gridx = 0;
        gbc.gridy = 1;

        cp.add(license, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        cp.add(button, gbc);
    }

    /**Overridden so we can exit when window is closed*/
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            dispose();
        }
        super.processWindowEvent(e);
    }

    /**Close the dialog on a button event*/
    public void actionPerformed(ActionEvent e) {
        dispose();
    }

    private static void addDir(Set<File> set, File f){
        if(f != null &&
           !DefaultClassLoaderProvider.isTemporary(f) &&
           ExtensionInstaller.canWrite(f) &&
           !f.isFile())
        {
            try{
                f = f.getCanonicalFile();
            } catch (IOException ignore) {
            }
            set.add(f);
        }
    }

    private static File[] getUserInstallTargets(){
        Set<File> userInstallTargets = new TreeSet<File>();
        addDir(userInstallTargets, new File(System.getProperty("user.dir"),
                "lib"));
        try{
            addDir(userInstallTargets, new File(
                DefaultClassLoaderProvider.getRepositoryRootDir(BibTeXConverter.class),
                "lib"));
        } catch (Exception ignore){
            System.err.println(ignore.getMessage());
        }
        String appdata = System.getenv("APPDATA");
        if(appdata != null){
            addDir(userInstallTargets, new File(appdata, "bibtexconverter"));
        }
        addDir(userInstallTargets, new File(System.getProperty("user.home") +
                File.separator + ".bibtexconverter",
                "lib"));
        String prefTarget =
                Preferences.userNodeForPackage(BibTeXConverter.class)
                .get("saxon", null);
        if(prefTarget != null){
           addDir(userInstallTargets,
                (new File(prefTarget)).getAbsoluteFile().getParentFile());
        }
        return userInstallTargets.toArray(new File[userInstallTargets.size()]);
    }
    
    public static boolean installSaxon(final JFrame trigger, ClassLoaderProvider clp){
        String jhjars = "saxon8.jar";
        final String saxonURI =
          "http://sf.net/project/showfiles.php?group_id=29872&package_id=21888";
        ExtensionInstaller extInst = new ExtensionInstaller(trigger);
        String systemInstallTarget = extInst.getWritableExtensionDirectory();
        File[] userInstallTargets = getUserInstallTargets();

        Box dialogPane = Box.createVerticalBox();
        JLabel text = new JLabel(
                "<html>BibTeXConverter converts BibTeX to XML and " +
                "derives all its other outputs<br>by applying XSLT stylesheets " +
                "to the intermediary XML data." +
                "To use this<br>XSLT-based functionality you need to download " +
                "and install the free Saxon-B<br>XSLT engine by Michael Kay.<p>" +
                "Saxon is not bundled with BibTeXConverter because "+
                "it is released under a<br>GPL-incompatible open source license.<p>" +
                "</html>");
        dialogPane.add(text);//1
        final String[] options = new String[]{
            "Download and install saxon",
            "Use an existing saxon installation"};
        JOptionPane optionPane = new JOptionPane(
                dialogPane,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.YES_NO_OPTION,
                null,
                options,
                options[0]
        );
        JDialog dialog = optionPane.createDialog(trigger, "Saxon installation");
        dialog.setModal(true);
        dialog.setVisible(true);
        Object value = optionPane.getValue();
        boolean freshInstall = true;
        if(value == null){
            return false;
        } else if (value.equals(options[1])){
            freshInstall = false;
        } else if (value.equals(options[0])){
            //freshInstall = true;
        }
        
        JButton button;
        dialogPane = Box.createVerticalBox();
        if(freshInstall){
            button = new JButton(
                    "Open a web browser at http://sf.net/projects/saxon/files/");
            button.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    try{
                        BrowserLauncher.openURL(saxonURI);
                    } catch (IOException ex){
                        JOptionPane.showMessageDialog(trigger,
                                "Can't open browser. Please do so yourself "+
                                "and visit<p>" + saxonURI);
                    }
                }
            });
            JPanel dl = new JPanel(new FlowLayout(FlowLayout.LEFT));
            dl.add(button);
            dl.add(new JLabel(" and download saxonb(\u22678-8)j.zip."));
            for(Component c : dl.getComponents()){
                ((JComponent) c).setAlignmentX(0.0f);
            }
            dialogPane.add(Box.createVerticalStrut(10));
            dialogPane.add(dl);//2
        }
        
        final String filename = (freshInstall? "downloaded Saxon zip" : jhjars);
        text = new JLabel(
            "<html><br>Please enter the location of the "+
                filename + 
                " file here.</html>");
        dialogPane.add(text);//3
        PathInput pinz = new PathInput("", freshInstall? ".zip" : ".jar");
        dialogPane.add(pinz);//4
        
        ButtonGroup targets = new ButtonGroup();
        if(freshInstall){
            text = new JLabel(
                "<html><br>Press OK to install saxon...</html>");
            dialogPane.add(text);//5
            JRadioButton btarget;
            boolean targetSelected = false;
            Insets bmargin = new Insets(0,20,0,0);
            if(systemInstallTarget != null){
                dialogPane.add(Box.createVerticalStrut(5));
                text = new JLabel("system-wide (may affect other Java applications)");
                btarget = new JRadioButton(systemInstallTarget, false);
                btarget.setMargin(bmargin);
                btarget.setActionCommand(systemInstallTarget);
                targets.add(btarget);
                dialogPane.add(text);
                dialogPane.add(btarget);
            }
            dialogPane.add(Box.createVerticalStrut(5));
            if(userInstallTargets.length != 0){
                text = new JLabel("for BibTeXConverter only");
                dialogPane.add(text);
                for(File f : userInstallTargets){
                    btarget = new JRadioButton(f.getAbsolutePath(), !targetSelected);
                    btarget.setActionCommand(f.getAbsolutePath());
                    btarget.setMargin(bmargin);
                    targets.add(btarget);
                    dialogPane.add(btarget);
                    targetSelected = true;
                }
            }
            btarget = new JRadioButton("to a location of your choice", !targetSelected);
            btarget.setActionCommand("*");
            btarget.setMargin(bmargin);
            targets.add(btarget);
            dialogPane.add(btarget);
    
            for(Component c : dialogPane.getComponents()){
                ((JComponent) c).setAlignmentX(0.0f);
            }
        }
        
        optionPane = new JOptionPane(
                dialogPane,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.OK_CANCEL_OPTION
        );
        dialog = optionPane.createDialog(trigger, "Saxon installation");
        dialog.pack();
        dialog.setModal(true);

        boolean success = false;
        for(boolean repeat = true; repeat;){
            dialog.setVisible(true);
            Object result = (Integer) optionPane.getValue();
            if(result == null){
                repeat = false;
            } else if(result == JOptionPane.UNINITIALIZED_VALUE){
            } else if(result instanceof Integer){
                int res = ((Integer) result).intValue();
                if(res == JOptionPane.OK_OPTION){
                    boolean emptySaxonZip = pinz.getPath().equals("");
                    boolean emptyTarget = freshInstall && (targets.getSelection() == null);
                    repeat = emptySaxonZip || emptyTarget;
                    success = !repeat;
                    if(repeat){
                        JOptionPane.showMessageDialog(trigger,
                            (emptySaxonZip? "Please specify the location of " +
                                "the " + filename + " file.\n" : "") +
                            (emptyTarget? "Please choose a target directory.\n"
                                : ""));
                    }
                } else {
                    repeat = false;
                }
            }
        }

        String starget = null;
        if(freshInstall && success){
            starget = targets.getSelection().getActionCommand();
            if(starget.equals("*")){
                JFileChooser jfc = new JFileChooser();
                jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                jfc.setMultiSelectionEnabled(false);
                int returnVal = jfc.showOpenDialog(trigger);
                if(returnVal == JFileChooser.APPROVE_OPTION){
                    starget = jfc.getSelectedFile().getAbsolutePath();
                } else {
                    starget = null;
                }
            }
        }

        if(freshInstall){
            success = starget != null;
            if(success){
                File ftarget = new File(starget);
                ftarget.mkdirs();
                extInst.setTargetDirectory(ftarget);
                String saxon_jar = (new File(jhjars)).getName();
                ftarget = new File(ftarget, saxon_jar);
                Preferences.userNodeForPackage(BibTeXConverter.class)
                        .put("saxon", ftarget.getAbsolutePath());
                if(extInst.installExtension(new File(pinz.getPath()), jhjars)){
                    JOptionPane.showMessageDialog(trigger,
                            "Saxon has been installed successfully.");
                   clp.registerLibrary(ftarget);
                } else {
                    JOptionPane.showMessageDialog(trigger,
                            "<html>Saxon installation failed. " +
                            "Please extract " + saxon_jar +
                            " from the Saxon zip file<p>" +
                            "put it on your classpath " +
                            "and restart the application.</html>");
                    success = false;
                }
            }
        } else if (success){
            File ftarget = new File(pinz.getPath());
            Preferences.userNodeForPackage(BibTeXConverter.class)
                        .put("saxon", ftarget.getAbsolutePath());
            success  = clp.registerLibrary(ftarget);
        }
        return success;
    }
}