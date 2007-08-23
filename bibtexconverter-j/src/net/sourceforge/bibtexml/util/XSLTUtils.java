package net.sourceforge.bibtexml.util;
/*
 * $Id: XSLTUtils.java 326 2007-08-23 15:19:05Z ringler $
 *
 * Copyright (c) 2007 Moritz Ringler
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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.TreeSet;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;
import de.mospace.lang.DefaultClassLoaderProvider;
import de.mospace.lang.BrowserLauncher;
import de.mospace.lang.ClassLoaderProvider;
import de.mospace.swing.ExtensionInstaller;
import de.mospace.swing.PathInput;

/**
    XSLT utilities
 **/
public class XSLTUtils extends DefaultClassLoaderProvider{
    public final static String TRANSFORMER_FACTORY_IMPLEMENTATION =
            "net.sf.saxon.TransformerFactoryImpl";

    private static XSLTUtils instance;
    private TransformerFactory tf;

    private XSLTUtils(){
        /* add default library directories to class path if they exist */
        String fs = File.separator;
        List<String> candidates = new ArrayList<String>();
        candidates.add(System.getProperty("user.dir") + fs + "lib");
        try{
            candidates.add(new File(
                DefaultClassLoaderProvider
                .getRepositoryRootDir(XSLTUtils.class),
                "lib").getAbsolutePath());
        } catch(Exception ignore){
            System.err.println(ignore);
            System.err.flush();
        }
        String appdata = System.getenv("APPDATA");
        if(appdata != null){
            candidates.add(appdata + fs + "bibtexconverter");
        }
        candidates.add(
            System.getProperty("user.home")+fs+".bibtexconverter"+fs+"lib");
        candidates.add(
            Preferences.userNodeForPackage(net.sourceforge.bibtexml.BibTeXConverter.class).get("saxon", null));
        for (String cf : candidates){
            if(cf != null){
                File f = new File(cf);
                if(f.isDirectory() && !DefaultClassLoaderProvider.isTemporary(f)){
                    registerLibraryDirectory(f);
                } else if(f.isFile() && f.getName().endsWith(".jar")){
                    registerLibrary(f);
                }
            }
        }

        /* Try to obtain a Saxon transformer factory */
        System.setProperty("javax.xml.transform.TransformerFactory",
                            TRANSFORMER_FACTORY_IMPLEMENTATION);
        tf = tryToGetTransformerFactory();
    }

    public static synchronized XSLTUtils getInstance(){
        if(instance == null){
            instance = new XSLTUtils();
        }
        return instance;
    }


    public String getSaxonVersion(){
        return getSaxonVersion("ProductTitle");
    }

    public String getSaxonVersion(String what){
        String result = null;
        try{
            Class c = Class.forName("net.sf.saxon.Version", true, getClassLoader());
            java.lang.reflect.Method m = c.getMethod("get" + what);
            result = (String) m.invoke(null);
        } catch (Exception ignore){
            System.err.println(ignore);
            System.err.flush();
        }
        return result;
    }

    public Object clone() throws CloneNotSupportedException{
        throw new CloneNotSupportedException("Singleton");
    }


    /** Tries to obtain an instance of a Saxon TransformerFactory.
    * If Saxon is on the application class path or in one of BibTeXConverter's
    * installation-dependent library directories a Saxon transformer factory
    * will be created and returned by calls to this method.<p>
    * If Saxon has not been found in one of the above locations. This method
    * asks the user to install Saxon. If the installation completes
    * successfully, a Saxon transformer factory is created and returned
    * by this and all future calls to this method.
    * @return a Saxon TransformerFactory or null as detailed above
    */
    public synchronized TransformerFactory loadTransformerFactory(final JFrame trig){
        if(tf == null){
            /* We first try to load from the existing class path and library
             * directories.
             */
            tf = tryToGetTransformerFactory();
        }

        if(tf == null){
            /* We've been unsuccessfull so far. We ask the user to install
            * saxon.
            */
            if(SaxonInstaller.INSTANCE.installSaxon(trig, this)){
                /* if the user has indeed installed saxon
                * we update our custom class loader and
                * try to create a TransformerFactory */
                tf = tryToGetTransformerFactory();
            }
        }
        return tf;
    }

    /** Tries to obtain an instance of a Saxon TransformerFactory. If saxon
     * is not found, null is returned.
     **/
    public final synchronized TransformerFactory tryToGetTransformerFactory(){
        if(tf == null){
            Thread.currentThread().setContextClassLoader(getClassLoader());
            try{
                tf = TransformerFactory.newInstance();
                System.out.println("Saxon found in " +
                        DefaultClassLoaderProvider.getRepositoryRoot(
                        tf.getClass()));
                double saxonversion = 0;
                try{
                    String sv = getSaxonVersion("ProductVersion");
                    //use only part up to second dot
                    int dot = sv.indexOf('.');
                    dot = sv.indexOf('.', dot + 1);
                    if(dot > 0){
                        sv = sv.substring(0, dot);
                    }
                    saxonversion = Double.parseDouble(sv);
                } catch (Exception ignore){
                    System.err.println("Cannot parse saxon version.");
                    System.err.println(ignore);
                    System.err.flush();
                }
                System.out.println("Saxon version: " + saxonversion);
                if(saxonversion >= 8.9){
                    System.out.println();
                } else if (saxonversion >= 8.8){
                    //We need to switch the URI resolver to something that
                    //knows how to handle jar files
                    tf.setURIResolver(new JarAwareURIResolver());
                } else {
                    System.out.println();
                    System.err.println("WARNING: This program has been developed" +
                        " and tested with saxon version 8.8 and later.");
                }
                System.out.flush();
                System.err.flush();
            } catch (TransformerFactoryConfigurationError ignore){
            }
        }
        return tf;
    }

    private static final class SaxonInstaller{
        private static SaxonInstaller INSTANCE = new SaxonInstaller();

        private void addDir(final Set<File> set, final File f){
            if(f != null &&
                !DefaultClassLoaderProvider.isTemporary(f) &&
            ExtensionInstaller.canWrite(f) &&
            !f.isFile())
            {
                File file = f;
                try{
                    file = f.getCanonicalFile();
                } catch (IOException ignore) {
                    file = f;
                }
                set.add(file);
            }
        }

        private File[] getUserInstallTargets(){
            final Set<File> userInstallTargets = new TreeSet<File>();
            addDir(userInstallTargets, new File(System.getProperty("user.dir"),
                "lib"));
            try{
                addDir(userInstallTargets, new File(
                    DefaultClassLoaderProvider.getRepositoryRootDir(getClass()),
                    "lib"));
            } catch (Exception ignore){
                System.err.println(ignore.getMessage());
            }
            final String appdata = System.getenv("APPDATA");
            if(appdata != null){
                addDir(userInstallTargets, new File(appdata, "bibtexconverter"));
            }
            addDir(userInstallTargets, new File(System.getProperty("user.home") +
                File.separator + ".bibtexconverter",
                "lib"));
            final String prefTarget =
            Preferences.userNodeForPackage(getClass())
            .get("saxon", null);
            if(prefTarget != null){
                addDir(userInstallTargets,
                    (new File(prefTarget)).getAbsoluteFile().getParentFile());
            }
            return userInstallTargets.toArray(new File[userInstallTargets.size()]);
        }

        public boolean installSaxon(final JFrame trigger, final ClassLoaderProvider clp){
            final String jhjars = "saxon8.jar";
            final String saxonURI =
            "http://sf.net/project/showfiles.php?group_id=29872&package_id=21888";
            final ExtensionInstaller extInst = new ExtensionInstaller(trigger);
            final String systemInstallTarget = extInst.getWritableExtensionDirectory();
            final File[] userInstallTargets = getUserInstallTargets();

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
            final Object value = optionPane.getValue();
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
                        public void actionPerformed(final ActionEvent e){
                            try{
                                BrowserLauncher.openURL(saxonURI);
                            } catch (IOException ex){
                                JOptionPane.showMessageDialog(trigger,
                                    "Can't open browser. Please do so yourself "+
                                    "and visit<p>" + saxonURI);
                            }
                        }
                });
                final JPanel dl = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
            final PathInput pinz = new PathInput("", freshInstall? ".zip" : ".jar");
            dialogPane.add(pinz);//4

            final ButtonGroup targets = new ButtonGroup();
            if(freshInstall){
                text = new JLabel(
                    "<html><br>Press OK to install saxon...</html>");
                dialogPane.add(text);//5
                JRadioButton btarget;
                boolean targetSelected = false;
                final Insets bmargin = new Insets(0,20,0,0);
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
                final Object result = (Integer) optionPane.getValue();
                if(result == null){
                    repeat = false;
                } else if(result == JOptionPane.UNINITIALIZED_VALUE){
                } else if(result instanceof Integer){
                    final int res = ((Integer) result).intValue();
                    if(res == JOptionPane.OK_OPTION){
                        final boolean emptySaxonZip = pinz.getPath().equals("");
                        final boolean emptyTarget = freshInstall && (targets.getSelection() == null);
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
                if("*".equals(starget)){
                    final JFileChooser jfc = new JFileChooser();
                    jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    jfc.setMultiSelectionEnabled(false);
                    final int returnVal = jfc.showOpenDialog(trigger);
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
                    final String saxon_jar = (new File(jhjars)).getName();
                    ftarget = new File(ftarget, saxon_jar);
                    Preferences.userNodeForPackage(net.sourceforge.bibtexml.BibTeXConverter.class)
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
                final File ftarget = new File(pinz.getPath());
                Preferences.userNodeForPackage(net.sourceforge.bibtexml.BibTeXConverter.class)
                .put("saxon", ftarget.getAbsolutePath());
                success  = clp.registerLibrary(ftarget);
            }
            return success;
        }
    }

   private static class JarAwareURIResolver implements URIResolver{
        public JarAwareURIResolver(){
            //sole constructor
        }

        public Source resolve(String href,
                      String base)
                throws TransformerException{
           try{
               URI uri = new URI(href);
               if(uri.isAbsolute()){
                   //leave it as it is
               } else if (base.startsWith("jar:")){
                   int lastslash = base.lastIndexOf('/');
                   if(lastslash >= 0){
                       uri = new URI(base.substring(0, lastslash + 1) + href);
                   } else {
                       uri = new URI(base + "/" + href);
                   }
               } else {
                   URI baseuri = new URI(base);
                   uri = baseuri.resolve(uri);
               }
               URL url = uri.toURL();
               InputStream in = url.openStream();
               Source src = new StreamSource(new BufferedInputStream(in));
               src.setSystemId(url.toString());
               return src;
           } catch (Exception ex){
               throw new TransformerException(ex);
           }
        }
    }

}