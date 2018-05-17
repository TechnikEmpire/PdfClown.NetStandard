package org.pdfclown.samples.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.pdfclown.bytes.IBuffer;
import org.pdfclown.documents.Page;
import org.pdfclown.documents.contents.Contents;
import org.pdfclown.documents.contents.objects.CompositeObject;
import org.pdfclown.documents.contents.objects.ContentObject;
import org.pdfclown.documents.contents.objects.GenericOperation;
import org.pdfclown.documents.contents.objects.Operation;
import org.pdfclown.files.File;
import org.pdfclown.objects.PdfArray;
import org.pdfclown.objects.PdfDataObject;
import org.pdfclown.objects.PdfDictionary;
import org.pdfclown.objects.PdfDirectObject;
import org.pdfclown.objects.PdfName;
import org.pdfclown.objects.PdfStream;
import org.pdfclown.util.parsers.ParseException;

/**
  PDF content stream's object model inspector.
  <p>This is a proof of concept that exploits the object-oriented content stream model
  implemented by PDF Clown.</p>
*/
public class PdfInspectorSample
  extends JPanel
{
  private static final long serialVersionUID = 1L;

  // <class>
  // <classes>
  private static class CellRenderer
    extends JTextArea
    implements TableCellRenderer
  {
    private static final long serialVersionUID = 1L;

    @Override
    public Component getTableCellRendererComponent(
      JTable table,
      Object value,
      boolean isSelected,
      boolean hasFocus,
      int row,
      int column
      )
    {
      setWrapStyleWord(false);
      setLineWrap(true);
      setText((String)value);
      return this;
    }
  }

  private static class ContentNodeValue
    extends NodeValue
  {
    private ContentObject content;

    private static String getName(
      ContentObject content
      )
    {
      if(content instanceof GenericOperation)
        return ((GenericOperation)content).getOperator();
      else
        return content.getClass().getSimpleName();
    }

    public ContentNodeValue(
      ContentObject content
      )
    {
      super(getName(content));

      this.content = content;
    }

    public ContentObject getContent(
      )
    {return content;}
  }

  private static class NodeValue
  {
    private String name;

    public NodeValue(
      String name
      )
    {this.name = name;}

    @Override
    public String toString(
      )
    {return name;}
  }

  private static class PageNodeValue
    extends NodeValue
  {
    private Page page;

    public PageNodeValue(
      Page page
      )
    {
      super("Page " + page.getNumber());

      this.page = page;
    }

    public Page getPage(
      )
    {return page;}
  }
  // </classes>

  // <fields>
  private File file;

  private final JTable table;
  private final JTree tree;
  // </fields>

  // <constructors>
  public PdfInspectorSample(
    )
  {
    super(new GridLayout(1,0));

    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    {
      Dimension minimumSize = new Dimension(200, 100);

      JScrollPane treeScrollPane;
      {
        tree = new JTree();
        {
          tree.setModel(new DefaultTreeModel(null));
          tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION
            );
          tree.addTreeSelectionListener(
            new TreeSelectionListener()
            {
              @Override
              public void valueChanged(TreeSelectionEvent event)
              {
                // Clear the attributes table!
                DefaultTableModel model = (DefaultTableModel)table.getModel();
                model.setRowCount(0);

                DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
                if(node == null)
                  return;

                // Show the current node attributes into the table!
                Object nodeValue = node.getUserObject();
                if(nodeValue instanceof ContentNodeValue)
                {showContentAttributes(((ContentNodeValue)nodeValue).getContent(), model);}
                else if(nodeValue instanceof PageNodeValue)
                {showPageAttributes(((PageNodeValue)nodeValue).getPage().getContents(), model);}

                pack(table);
              }
            }
            );
          tree.addTreeWillExpandListener(
            new TreeWillExpandListener()
            {
              @Override
              public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException
              {/* NOOP */}
              @Override
              public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException
              {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)event.getPath().getLastPathComponent();
                Object nodeValue = node.getUserObject();
                if(nodeValue instanceof PageNodeValue)
                {
                  // Content placeholder?
                  if(((DefaultMutableTreeNode)node.getFirstChild()).getUserObject() == null)
                  {
                    // Remove the content placeholder!
                    node.removeAllChildren();

                    // Create the page content nodes!
                    createContentNodes(
                      ((PageNodeValue)nodeValue).getPage().getContents(),
                      node
                      );
                  }
                }
              }
            }
            );
        }
        treeScrollPane = new JScrollPane(tree);
        treeScrollPane.setMinimumSize(minimumSize);
      }
      splitPane.setLeftComponent(treeScrollPane);

      JScrollPane tableScrollPane;
      {
        table = new JTable(new DefaultTableModel(null, new String[]{"Attribute","Value"}));
        {
          table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
          TableColumn column0 = table.getColumnModel().getColumn(0);
          {
            column0.setCellRenderer(new CellRenderer());
            column0.setMinWidth(200);
            column0.setMaxWidth(200);
          }
          TableColumn column1 = table.getColumnModel().getColumn(1);
          {
            column1.setCellRenderer(new CellRenderer());
          }
          table.getColumnModel().addColumnModelListener(
            new TableColumnModelListener()
            {
              @Override
              public void columnAdded(TableColumnModelEvent event)
              {/* NOOP */}
              @Override
              public void columnMarginChanged(ChangeEvent event)
              {pack(table);}
              @Override
              public void columnMoved(TableColumnModelEvent event)
              {/* NOOP */}
              @Override
              public void columnRemoved(TableColumnModelEvent event)
              {/* NOOP */}
              @Override
              public void columnSelectionChanged(ListSelectionEvent event)
              {/* NOOP */}
            }
            );
        }
        tableScrollPane = new JScrollPane(table);
        tableScrollPane.setMinimumSize(minimumSize);
      }
      splitPane.setRightComponent(tableScrollPane);
    }
    add(splitPane);
  }
  // </constructors>

  // <interface>
  // <public>
  public void open(
    java.io.File file
    )
  {
    // Open the PDF file!
    try
    {this.file = new File(file.getAbsolutePath());}
    catch(ParseException e)
    {throw new RuntimeException(file.getAbsolutePath() + " file parsing failed.",e);}
    catch(Exception e)
    {throw new RuntimeException(file.getAbsolutePath() + " file access error.",e);}

    // Create the root node!
    DefaultMutableTreeNode fileNode = new DefaultMutableTreeNode(file.getName());
    ((DefaultTreeModel)tree.getModel()).setRoot(fileNode);

    // Create the page nodes!
    createPageNodes(fileNode);

    // Expand the tree to show the page nodes!
    tree.expandPath(new TreePath(fileNode));
  }
  // </public>

  // <private>
  private void createContentNodes(
    List<ContentObject> contents,
    DefaultMutableTreeNode parentNode
    )
  {
    for(ContentObject content : contents)
    {
      DefaultMutableTreeNode contentNode = new DefaultMutableTreeNode(
        new ContentNodeValue(content)
        );
      parentNode.add(contentNode);

      if(content instanceof CompositeObject)
      {
        // Create inner content nodes!
        createContentNodes(
          ((CompositeObject)content).getObjects(),
          contentNode
          );
      }
    }
  }

  private void createPageNodes(
    DefaultMutableTreeNode fileNode
    )
  {
    for(Page page : file.getDocument().getPages())
    {
      DefaultMutableTreeNode pageNode = new DefaultMutableTreeNode(
        new PageNodeValue(page)
        );
      fileNode.add(pageNode);

      // Add a placeholder node to postpone content parsing!
      DefaultMutableTreeNode contentPlaceholderNode = new DefaultMutableTreeNode();
      pageNode.add(contentPlaceholderNode);
    }
  }

  private void pack(
    JTable table
    )
  {
    for(
      int rowIndex = 0,
        rowCount = table.getRowCount();
      rowIndex < rowCount;
      rowIndex++
      )
    {
      int height = 0;
      for(
        int columnIndex = 0,
          columnCount = table.getColumnCount();
        columnIndex < columnCount;
        columnIndex++
        )
      {
        height = Math.max(
          height,
          table.prepareRenderer(
            table.getCellRenderer(rowIndex, columnIndex),
            rowIndex,
            columnIndex
            ).getPreferredSize().height
          );
      }
      if(table.getRowHeight(rowIndex) < height)
      {table.setRowHeight(rowIndex, height);}
    }
  }

  private void showContentAttributes(
    ContentObject content,
    DefaultTableModel model
    )
  {
    Operation operation;
    if(content instanceof Operation)
    {operation = (Operation)content;}
    else if(content instanceof CompositeObject)
    {operation = ((CompositeObject)content).getHeader();}
    else
    {operation = null;}
    if(operation == null)
      return;

    model.addRow(
      new Object[]
      {
        "(operator)",
        operation.getOperator()
      }
      );

    List<PdfDirectObject> operands = operation.getOperands();
    if(operands != null)
    {
      for(int index = 0, length = operands.size(); index < length; index++)
      {
        PdfDirectObject operand = operands.get(index);
        if(operand instanceof PdfArray)
        {
          PdfArray operandElements = (PdfArray)operand;
          int operandElementIndex = -1;
          for(PdfDirectObject operandElement : operandElements)
          {
            model.addRow(
              new Object[]
              {
                "(operand " + index + "." + (++operandElementIndex) + ")",
                operandElement.toString()
              }
              );
          }
        }
        else if(operand instanceof PdfDictionary)
        {
          PdfDictionary operandEntries = (PdfDictionary)operand;
          int operandEntryIndex = -1;
          for(Map.Entry<PdfName,PdfDirectObject> operandEntry : operandEntries.entrySet())
          {
            model.addRow(
              new Object[]
              {
                "(operand " + index + "." + (++operandEntryIndex) + ") " + operandEntry.getKey().toString(),
                operandEntry.getValue().toString()
              }
              );
          }
        }
        else
        {
          model.addRow(
            new Object[]
            {
              "(operand " + index + ")",
              operand.toString()
            }
            );
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void showPageAttributes(
    Contents contents,
    DefaultTableModel model
    )
  {
    List<PdfDataObject> streamObjects = new ArrayList<PdfDataObject>();
    PdfDataObject contentsDataObject = contents.getBaseDataObject();
    if(contentsDataObject instanceof PdfArray)
    {streamObjects.addAll((List<PdfDirectObject>)contentsDataObject);}
    else
    {streamObjects.add(contentsDataObject);}

    int streamIndex = -1;
    for(PdfDataObject streamObject : streamObjects)
    {
      PdfStream stream = (PdfStream)streamObject.resolve();
      IBuffer streamBody = stream.getBody();
      model.addRow(
        new Object[]
        {
          "(stream " + (++streamIndex) + ")",
          streamBody.getString(0, (int)streamBody.getLength())
        }
        );
    }
  }
  // </private>
  // </interface>
  // </class>
}
