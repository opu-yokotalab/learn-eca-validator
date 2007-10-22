/// Validate Program GUI Side (ProtType)
// authed by konishi 2005/12/11

import javax.swing.*;
import javax.swing.table.*;
import java.io.*;
import java.lang.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.*;

public class ValiGui extends JFrame implements ActionListener{
    public JCanvas canvas;  // graph 表示用　canvas
    public JFileChooser fc; // FileChooser
    public Graph graph;   // Graph Class
    public JTable table;  // cost Table
    private DefaultTableColumnModel columnModel;
    private DefaultTableModel tableModel;
    private TableColumn column = null;
    private JScrollPane scrPane;

    public Vertex[] verList;  // Vertex Class 描画用
    public Edge[][] adjMat;     // Edge Class 描画用

    public static void main(String[] args){
        /* 自分自身を作成 */
        ValiGui valiFrame = new ValiGui("教材妥当性検証器");

        /* 実際に表示する */
        valiFrame.setVisible(true);
    }

    // Constructor
    ValiGui(String title){
	/* サイズと位置を指定 */
        setTitle(title);
        setBounds( 10, 10, 600 , 450);

	/* 終了処理を追加 */
        addWindowListener(new WindowAdapter(){
		public void windowClosing(WindowEvent e){System.exit(0);}
	    });

	// Menu Bar create
	// Menu File create
	JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
	menuFile.setMnemonic(KeyEvent.VK_F);
	
        JMenuItem itemLoad = new JMenuItem("Load ECA-Rule");
	itemLoad.addActionListener(this);
        itemLoad.setActionCommand("Load");

        JMenuItem itemExit = new JMenuItem("Exit");
	itemExit.addActionListener(this);
        itemExit.setActionCommand("Exit");

        menuFile.add(itemLoad);
	menuFile.addSeparator();
        menuFile.add(itemExit);
	
        menuBar.add(menuFile);
        setJMenuBar(menuBar);

	// create FileChooser
	fc = new JFileChooser("./graphs");
	FileFilterEx filter[] = {
	    new FileFilterEx(".dg" , "graph ファイル(*.dg)"),
	    new FileFilterEx(".pl" , "pl ファイル(*.pl)")
	};
	for(int i = 0 ; i < filter.length ; i++)
	    fc.addChoosableFileFilter(filter[i]);
	// Button create
	JButton btnGraphValidation = new JButton("Graph Validation");
	btnGraphValidation.addActionListener(this);
        btnGraphValidation.setActionCommand("GraphValidation");

	JButton btnCostValidation = new JButton("Cost Validation");
	btnCostValidation.addActionListener(this);
        btnCostValidation.setActionCommand("CostValidation");
	
	// Button Panel create
	JPanel btnPanel = new JPanel();
	btnPanel.add(btnGraphValidation);
	btnPanel.add(btnCostValidation);

	// Canvas create
	canvas = new JCanvas(this);
	
	//Cost Table create
	String[][] tabledata = null;
	String[] colNames = {"From","To","Cost"};
	tableModel = new DefaultTableModel(tabledata, colNames);
	table = new JTable(tableModel);

	// def column size
	columnModel = (DefaultTableColumnModel)table.getColumnModel();
	column = null;
	for (int i = 0 ; i < columnModel.getColumnCount() ; i++){
	    column = columnModel.getColumn(i);
	    if(i < 2)
		column.setPreferredWidth(30);
	}
	// scroll panel create
	scrPane = new JScrollPane();
	scrPane.getViewport().setView(table);
	scrPane.setPreferredSize(new Dimension(230,100));
	
	// BasePanel create
        JPanel basePanel = new JPanel(new BorderLayout());
	basePanel.add(scrPane,BorderLayout.LINE_END);
        basePanel.add(btnPanel, BorderLayout.PAGE_START);
        basePanel.add(canvas, BorderLayout.CENTER);
	getContentPane().add(basePanel);
    }
    
    // insert value of Cost Table
    public void createTable(String[][] tabledata,int rowNum){
	
	int Num = tableModel.getRowCount();
	//remove all row
	for(int i=0;i<Num;i++){
	    tableModel.removeRow(0);
	}

	//create new table
	for(int i=0;i<rowNum;i++){
	    tableModel.addRow(tabledata[i]);
	}
	
    }

    // Action Progress
    public void actionPerformed(ActionEvent e){
        String cmd = e.getActionCommand();  // menu Command
	File inputFileName;  // input Graph File
	int selected; // Selected File Name

	// menu Action Progress
	// Load menu Progress
        if (cmd.equals("Load")){
	    
	    selected = fc.showOpenDialog(this);
	    if(selected == JFileChooser.APPROVE_OPTION){
		canvas.setText(fc.getSelectedFile().getName());
		// graph File Load
		inputFileName = fc.getSelectedFile();
		Perser per = new Perser(inputFileName);
		graph = per.pers();
		
		// 描画用　Vertex , Edge Class create
		verList = new Vertex[graph.nVerts];
		adjMat = new Edge[graph.nVerts][graph.nVerts];
		for(int i=0;i<graph.nVerts;i++){
		    verList[i] = new Vertex();
		    for(int j=0;j<graph.nVerts;j++){
			adjMat[i][j] = new Edge();
		    }
		}

		// graph Output
		Output(graph);
	    }

	// Exit menu Progress
        }else if (cmd.equals("Exit")){
	    System.exit(0);
	// GraphValidation Progress
        }else if (cmd.equals("GraphValidation")){
	    if(canvas.paintFlag){
		graph.dfs2();
		Output(graph);
	    }else{
		System.out.println("Please! Load ECA");
	    }
	}else if (cmd.equals("CostValidation")){
	    if(canvas.paintFlag){
		graph.bfs();
		graph.condv();
		Output(graph);
	    }else{
		System.out.println("Please! Load ECA");
	    }
	}
    }
    ///////////////////////////////////////////////////
    // Output Graph File
    ///////////////////////////////////////////////////
    public void Output(Graph outGraph){
	// X-Y coordinates calculation
	outGraph.calCoordinates(verList,adjMat);
	
	// CostTable Output
	int dataNum = 0;
	String[][] tabledata = new String[100][3];
	StringBuffer[] dataBuff = new StringBuffer[30];
	for(int i=0;i<graph.nVerts;i++){
	    for(int j=0;j<graph.nVerts;j++){
		
		// condition data
		if(adjMat[i][j].condition != null){
		    tabledata[dataNum][0] = verList[i].label;
		    tabledata[dataNum][1] = verList[j].label;

		    for(int k=0;k<adjMat[i][j].condition.length;k++){
			dataBuff[k] = new StringBuffer();
			for(int l=0;l<adjMat[i][j].condition[k].length;l++){
			    dataBuff[k].append(adjMat[i][j].condition[k][l] + " ");
			}
			tabledata[dataNum][2] = dataBuff[k].toString();
			if(k<adjMat[i][j].condition.length)
			    dataNum++;
		    }
		    dataNum++;
		}
	    }
	}

	createTable(tabledata,dataNum);
	canvas.paintFlag = true;	
	canvas.repaint();
    }
}
// Difinition JCanvas Class
class JCanvas extends JLabel{
    private final int N = 90;  // 座標変倍定数
    private final int RADIUS = 17; // 頂点の半径
    public ValiGui valiGui;
    public boolean paintFlag;  // paint Method Flag
    
    // Constructor
    JCanvas(ValiGui classBuff){
	valiGui = classBuff;

	paintFlag = false;     // flag initialize
        setForeground(Color.black);   // 文字の色を黒に
	setOpaque(true);              // ラベルの背景を非透明に
    }

    // paint Method
    public void paint(Graphics g){
	// Graphics 2D class
	Graphics2D g2 = (Graphics2D)g;
	
	// RenderingHint レンダリング方式の設定
	g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
			    RenderingHints.VALUE_ANTIALIAS_ON);
	// BasicStroke 線の太さの設定
	BasicStroke wide = new BasicStroke(3.0f);
	BasicStroke thin = new BasicStroke(1.5f);
	BasicStroke mark = new BasicStroke(8.0f);

	// Background Color
	g2.setColor(new Color(240,235,255));
	g2.fillRect(0,0,getWidth(),getHeight());
	
	if(paintFlag){
	    // start and end color patint
	    g2.setStroke(wide);
	    //start mark
	    g2.setColor(Color.cyan);
	    g2.draw(new Ellipse2D.Double(10,5,20,20));
	    //end mark
	    g2.setColor(new Color(25,25,112));
	    g2.draw(new Ellipse2D.Double(100,5,20,20));
	    // draw string
	    g2.setColor(Color.black);
	    g2.drawString("start",35,20);
	    g2.drawString("end",125,20);


	    // Edge 描画
	    for(int i=0;i<valiGui.graph.nVerts;i++){
		for(int j=0;j<valiGui.graph.nVerts;j++){
		    int Xfrom = valiGui.verList[i].horizontal*N;
		    int Yfrom = valiGui.verList[i].vertical*N;
		    int Xto = valiGui.verList[j].horizontal*N;
		    int Yto = valiGui.verList[j].vertical*N;
		    
		    // Difinition Arrow diagram
		    int pt = Xto - RADIUS;
		    GeneralPath arrow = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		    arrow.moveTo(pt,Yto);
		    arrow.lineTo(pt-10,Yto-6);
		    arrow.lineTo(pt-10,Yto+10);
		    arrow.lineTo(pt,Yto);

		    if(valiGui.adjMat[i][j].exist){
			g2.setStroke(mark);
			if(valiGui.verList[i].label.equals("start")){
			    // start Vertex marking
			    g2.setColor(Color.cyan);
			    g2.draw(new Ellipse2D.Double(Xto-RADIUS,Yto-RADIUS,
							 2*RADIUS,2*RADIUS));
			}else if(valiGui.verList[j].label.equals("end")){
			    // end Vertex marking
			    g2.setColor(new Color(25,25,112));
			    g2.draw(new Ellipse2D.Double(Xfrom-RADIUS,Yfrom-RADIUS,
							 2*RADIUS,2*RADIUS));
			}else{
			    // Edge Stroke and Color
			    g2.setStroke(wide);
			    if(valiGui.adjMat[i][j].costState == 0)
				g2.setColor(Color.darkGray);
			    else{
				g2.setColor(Color.red);
				if(valiGui.adjMat[i][j].costState == 1)
				    g2.drawString("条件不足",Xfrom - 20,Yfrom + 40);
				else if(valiGui.adjMat[i][j].costState == 2)
				    g2.drawString("条件重複",Xfrom - 20,Yfrom + 40);
				else if(valiGui.adjMat[i][j].costState == 3)
				    g2.drawString("条件不足かつ重複",Xfrom - 20,Yfrom + 40);
				else if(valiGui.adjMat[i][j].costState == 4)
				    g2.drawString("未チェック",Xfrom - 20,Yfrom + 40);
			    }				
			    // Edge draw
			    if((Xfrom > Xto) || (Yfrom > Yto)){
				// reverse line draw
				if((Xfrom != Xto ) && (Yfrom != Yto)){
				    g2.draw(new CubicCurve2D.Double(Xfrom,Yfrom,Xfrom,Yto,
								    Xto,Yfrom,Xto,Yto));
                                }else if(Yfrom != Yto){
				    int Xc = Math.abs(Yto-Yfrom) + 100;
				    int Yc = (Yfrom + Yto)/2;
				    g2.draw(new QuadCurve2D.Double(Xfrom,Yfrom,Xc,Yc,Xto,Yto));
                                }else{
				    int Xc = (Xfrom + Xto)/2;
				    int Yc = Math.abs(Xto-Xfrom)/N + 
					10 * Math.abs(Xto-Xfrom)/N;
				    g2.draw(new QuadCurve2D.Double(Xfrom,Yfrom,Xc,Yc,Xto,Yto));
				}
			    }else if((Xfrom != Xto ) && (Yfrom != Yto)){
				g2.fill(arrow);
				g2.draw(new CubicCurve2D.Double(Xfrom,Yfrom,Xto,Yfrom,
								Xfrom-30,Yto,Xto,Yto));
			    }
			    else if(((Xto - Xfrom) > N) || ((Yto - Yfrom) > N)){
				int Xc = (Xfrom + Xto)/2;
				int Yc = -(Yfrom + 50);
				g2.draw(new QuadCurve2D.Double(Xfrom,Yfrom,Xc,Yc,Xto,Yto));
			    }else{
				g2.fill(arrow);
				g2.draw(new Line2D.Double(Xfrom,Yfrom,Xto,Yto));
			    }
			}
		    }
		}
	    }
	    
	    // Vertex 配置
	    for(int i=0;i<valiGui.graph.nVerts;i++){
		// 座標代入
		int X = valiGui.verList[i].horizontal*N;
		int Y = valiGui.verList[i].vertical*N;
		
		if(valiGui.verList[i].toEnd)
		    g2.setColor(new Color(96,149,237));
		else
		    g2.setColor(new Color(208,20,60));
		if( !valiGui.verList[i].label.equals("start") &&
		    !valiGui.verList[i].label.equals("end")){
		    // 円描画
		    g2.fill(new Ellipse2D.Double(X-RADIUS,Y-RADIUS,2*RADIUS,2*RADIUS));
		    // ラベル描画
		    g2.setColor(Color.black);
		    g2.setStroke(wide);
		    g2.drawString(valiGui.verList[i].label,X-5,Y+2);
		}
	    }
	}
    }
    
    public void update(Graphics g){
	paint(g);
    }
}
// FileFilter OverRide
class FileFilterEx extends javax.swing.filechooser.FileFilter {
	private String extension , msg;
	public FileFilterEx(String extension , String msg) {
		this.extension = extension;
		this.msg = msg;
	}
	public boolean accept(File f) {
		return f.getName().endsWith(extension);
	}
	public String getDescription() { return msg; }
}