package org.pdfclown.samples.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;
import javax.swing.filechooser.FileFilter;

public class SampleViewer
{
  private class PdfFileFilter
    extends FileFilter
  {
    @Override
    public boolean accept(
      File file
      )
    {
      if(file.isDirectory())
        return true;

      return file.getName().endsWith(".pdf");
    }

    @Override
    public String getDescription(
      )
    {return "PDF files";}
  }

  private static final String ClassName = SampleViewer.class.getName();

  private static final String PropertiesFilePath = "pdfclown-samples-gui.properties";

  private static final String Properties_InputPath = ClassName + ".inputPath";

  public static void main(
    String args[]
    )
  {
    Properties properties = new Properties();
    try
    {
      properties.load(
        new FileInputStream(PropertiesFilePath)
        );
    }
    catch(Exception e)
    {throw new RuntimeException("An exception occurred while loading the properties file (\"" + PropertiesFilePath + "\").",e);}

    try
    {
      SampleViewer sampleViewer = new SampleViewer(
        properties.getProperty(Properties_InputPath)
        );
      sampleViewer.show();
    }
    catch(Exception e)
    {e.printStackTrace();}
  }

  private String inputPath;

  private JFrame window;
  private PdfInspectorSample domInspector;

  public SampleViewer(
    String inputPath
    )
  {
    this.inputPath = new java.io.File(inputPath).getAbsolutePath();

    initialize();

    showOpenFileDialog();
  }

  public void show(
    )
  {window.setVisible(true);}

  private void initialize(
    )
  {
    window = new JFrame("PDF Clown Sample Viewer");
    SpringLayout springLayout = new SpringLayout();

    window.getContentPane().setLayout(springLayout);
    window.setName("main");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setFocusable(true);
    {
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension windowSize = new Dimension(
        (int)(screenSize.getWidth()*.6),
        (int)(screenSize.getHeight()*.6)
        );
      int x = (int)(screenSize.getWidth() - windowSize.width) / 2;
      int y = (int)(screenSize.getHeight() - windowSize.height) / 2;

      window.setBounds(x,y,windowSize.width,windowSize.height);
    }

    {
      final JTabbedPane mainTabbedPane = new JTabbedPane();
      mainTabbedPane.setPreferredSize(new Dimension(0, 400));
      mainTabbedPane.setMinimumSize(new Dimension(0, 300));
      window.getContentPane().add(mainTabbedPane,BorderLayout.CENTER);

      final JMenuBar menuBar = new JMenuBar();
      window.setJMenuBar(menuBar);
      final JMenu fileMenu = new JMenu();
      menuBar.add(fileMenu);
      fileMenu.setName("fileMenu");
      fileMenu.setText("File");
      fileMenu.setMnemonic(KeyEvent.VK_F);
      {
        final JMenuItem openMenuItem = new JMenuItem();
        openMenuItem.setText("Open...");
        fileMenu.add(openMenuItem);
        openMenuItem.addMouseListener(
          new MouseAdapter()
          {
            @Override
            public void mousePressed(
              MouseEvent e
              )
            {showOpenFileDialog();}
          }
          );
        fileMenu.addSeparator();
        final JMenuItem exitMenuItem = new JMenuItem();
        exitMenuItem.setText("Exit");
        fileMenu.add(exitMenuItem);
        exitMenuItem.addMouseListener(
          new MouseAdapter()
          {
            @Override
            public void mousePressed(
              MouseEvent e
              )
            {System.exit(1);}
          }
          );
      }

      springLayout.putConstraint(SpringLayout.SOUTH, mainTabbedPane, 0, SpringLayout.SOUTH, window.getContentPane());
      springLayout.putConstraint(SpringLayout.EAST, mainTabbedPane, 0, SpringLayout.EAST, window.getContentPane());
      springLayout.putConstraint(SpringLayout.NORTH, mainTabbedPane, 0, SpringLayout.NORTH, window.getContentPane());
      springLayout.putConstraint(SpringLayout.WEST, mainTabbedPane, 0, SpringLayout.WEST, window.getContentPane());

      domInspector = new PdfInspectorSample();
      mainTabbedPane.addTab(
        "DOM Inspector",
        null,
        domInspector,
        null
        );
    }
  }

  private void showOpenFileDialog(
    )
  {
    JFileChooser fileChooser = new JFileChooser(
      inputPath + java.io.File.separator + "pdf"
      );
    fileChooser.setDialogTitle("Open PDF file");
    fileChooser.addChoosableFileFilter(new PdfFileFilter());
    switch(fileChooser.showDialog(null, "Open"))
    {
      case JFileChooser.APPROVE_OPTION:
        File file = fileChooser.getSelectedFile();

        domInspector.open(file);
        break;
    }
  }
}
