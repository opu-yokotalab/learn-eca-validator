// class Graph
// 2005/12/12 authed by konishi

////////////////////////////////////////////////////////////////////
// Create Graph Progress:
// addVertex,addEdge,displayVertex,verNo
// Breadth-First-Search(Check Cost) and Depth-First-Search(Find Loop)
////////////////////////////////////////////////////////////////////
class Graph
{
    private final int MAX_LISTS = 100;  // pathListROw MaxSize
    private Vertex verList[];           // list of vertices
    public int nVerts;           // current number of vertices
    private Edge adjMat[][];      // adjacency matrix
    private Condition[] condList = new Condition[MAX_LISTS]; // edge condition object 
    public int nConds;    // current number of conditions

    private StackX stack;
    private StackV stackV;
    private Queue queue;

    
    public Graph()           // constructor
    {
	nVerts = 0;          // nVerts Initialize
	nConds = 0;          // nConds Initialize

	stack = new StackX();     // int Stack
	stackV = new StackV();    // Object Stack
	queue = new Queue();
    }  // end constructor
    
    // add Vertex Method
    public void addVertex(String[] buffVerList)
    {
	int i;
	verList = new Vertex[buffVerList.length];
	for(i=0;i<buffVerList.length;i++)
	    verList[i] = new Vertex(buffVerList[i]);
	nVerts = i;

	// adjacency matrix
	adjMat = new Edge[nVerts][nVerts];
	for(int j=0; j<nVerts; j++)       // set adjacency
	    for(int k=0; k<nVerts; k++)   // matrix to 0
		adjMat[j][k] = new Edge();
    }
    ////////////////////////////////////////////////////
    // addEdge Method Over Load
    ////////////////////////////////////////////////////
    // No Condition
    public void addEdge(int start, int end)
    {
	adjMat[start][end].exist = true;
    }
    // Condition exist
    public void addEdge(int start, int end ,int orNo ,String[][] cond)
    {
	adjMat[start][end].exist = true;
	// input condition
	adjMat[start][end].condition = new String[orNo][];
	for(int i=0;i<orNo;i++){
	    adjMat[start][end].condition[i] = new String[cond[i].length];
	    for(int j=0;j<cond[i].length;j++){
		adjMat[start][end].condition[i][j] = cond[i][j];
	    }
	}
    }
    ////////////////////////////////////////////////////////////////
    // GraphValidation on Warshall method
    ////////////////////////////////////////////////////////////////
    public void warshall(){
	int[][] transCl = new int[nVerts][nVerts]; // 推移閉包 有向グラフ
	int[][] transCl2 = new int[nVerts][nVerts]; // 推移閉包 無向グラフ
	int startIndex = verNo("start");
	int endIndex = verNo("end");
	int seqNo = 1;    // separate graph No

	// transCl initialize
	for(int i=0;i<nVerts;i++){
	    for(int j=0;j<nVerts;j++){
		transCl2[i][j] = 0;
	    }
	}

	for(int i=0;i<nVerts;i++){
	    for(int j=0;j<nVerts;j++){
		if(adjMat[i][j].exist){
		    transCl[i][j] = 1;
		    transCl2[i][j] = 1;
		    transCl2[j][i] = 1;
		}else{
		    transCl[i][j] = 0;
		}
	    }
	}

	// caliculation Transitive Closure (有向グラフ)
	for(int y=0;y<nVerts;y++){
	    for(int x=0;x<nVerts;x++){
		if(transCl[x][y] == 1){
		    for(int n=0;n<nVerts;n++){
			if(transCl[y][n] == 1)
			    transCl[x][n] = 1;
		    }
		}
	    }
	}

	// 終了地点へ到達不能、開始地点から到達不能なノードを判定
	for(int i=0;i<nVerts;i++){
	    if(transCl[i][endIndex] == 1)
		verList[i].toEnd = true;
	    if(transCl[startIndex][i] == 1)
		verList[i].fromStart = true;
	}


	// caliculation Transitive Closure 2 (無向グラフ)
	for(int y=0;y<nVerts;y++){
	    for(int x=0;x<nVerts;x++){
		if(transCl2[x][y] == 1){
		    for(int n=0;n<nVerts;n++){
			if(transCl2[y][n] == 1)
			    transCl2[x][n] = 1;
		    }
		}
	    }
	}

	// 分割グラフの判定
	for(int i=0;i<nVerts;i++){
	    if(verList[i].graphNo == 0){
		for(int j=0;j<nVerts;j++){
		    if(transCl2[i][j] == 1)
			verList[j].graphNo = seqNo;
		}
		verList[i].graphNo = seqNo++;
	    }
	}
	
	// debug print
	System.out.println("\n");
	for(int y=0;y<nVerts;y++){
	    for(int x=0;x<nVerts;x++){
		System.out.printf("%d",transCl2[y][x]);
	    }
	    System.out.println();
	}

	System.out.printf("graph No = %d \n",seqNo-1);
    }
    ////////////////////////////////////////////////////////////////
    // GraphValidation on Depth-First-Search version 2nd
    ////////////////////////////////////////////////////////////////
    public void dfs2()  // depth-first search
    {                                 // begin at vertex "start"
	int startIndex = verNo("start");
	int[] stackP;    // stack Pointer
	displayVertex(startIndex);                 // display it
	stack.push(startIndex);                 // push it
	
	while( !stack.isEmpty() )      // until stack empty,
	    {
		// get an unvisited vertex adjacent to stack top
		int f = stack.peek();
		int t = getUnvisitedAdjDFS2( stack.peek());
		
		// 頂点が終了地点に到達可能であればこれまでの経路の
		// 頂点を｢到達可能｣とする
		if(judgeToEndEdge(f)){
		    stackP = stack.retStack();
		    for(int i=0;i<stack.retTop();i++){
			adjMat[stackP[i]][stackP[i+1]].toEnd = true;
		    }
		}

		// v=-1:Path not found
		if(t == -1){
		    stack.pop();
		}else if(verList[t].label.equals("end")){
		    adjMat[f][t].Visited = true;
		    displayVertex(t);
		    stack.push(t);
		    stackP = stack.retStack();
		    for(int i=0;i<stack.retTop();i++){
			adjMat[stackP[i]][stackP[i+1]].toEnd = true;
		    }
		}else{
		    adjMat[f][t].Visited = true;  // mark it
		    displayVertex(t);                 // display it
		    stack.push(t);                 // push it
		}
	    }  // end while
	
	// stack is empty, so we're done
	for(int j=0; j<nVerts; j++){          // reset flags
            verList[j].Visited = false;
	}
	for(int i=0;i<nVerts;i++){
	    for(int j=0;j<nVerts;j++){
		if(adjMat[i][j].toEnd == true){
		    verList[i].toEnd=true;
		    verList[j].toEnd=true;
		}
	    }
	}
	stack.stackClear();
    }  // end dfs
    
    // returns an unvisited vertex adj to v For DFS
    public int getUnvisitedAdjDFS2(int v)
    {
	for(int j=0; j<nVerts; j++)
	    if(adjMat[v][j].exist == true){
		if(adjMat[v][j].Visited == false)
		    return j;
	    }
	return -1; // path not found
    }  // end getUnvisitedAdjDFS()
    //  辺が可達であるかの判断
    private boolean judgeToEndEdge(int v){
	for(int j=0; j<nVerts;j++){
	    if(adjMat[v][j].toEnd == true){
		return true;
	    }
	}
	return false;
    }
    ////////////////////////////////////////////////////////////////
    // Breadth-First-Search 
    ////////////////////////////////////////////////////////////////
    public void bfs()                   // breadth-first search
    {
	Edge[] buffEdge = new Edge[50];	

	int startIndex = verNo("start");
	verList[startIndex].Visited = true;  // mark it
	displayVertex(startIndex);           // display it
	queue.insert(startIndex);            // insert at tail
	int v2;

	while( !queue.isEmpty() )     // until queue empty,
	    {
		int v1 = queue.remove();   // remove vertex at head

		int buffIndex = 0;
		// until it has no unvisited neighbors
		while( (v2=getUnvisitedAdjBFS(v1)) != -1 )
		    {                                  // get one,
			adjMat[v1][v2].Visited = true;  // mark it
			buffEdge[buffIndex]=adjMat[v1][v2];
			buffIndex++;
			
			displayVertex(v2);           // display it
			queue.insert(v2);            // insert it
		    }   // end while
		if(!verList[v1].label.equals("end"))
		    condList[nConds++] = new Condition(buffIndex,buffEdge);
	    }  // end while(queue not empty)
	
	// queue is empty, so we're done
	for(int j=0; j<nVerts; j++)             // reset flags
	    for(int k =0; k<nVerts; k++)
		adjMat[j][k].Visited = false;
    }  // end bfs()
    
    // returns an unvisited vertex adj to v For BFS
    public int getUnvisitedAdjBFS(int v)
      {
	  for(int j=0; j<nVerts; j++)
	      if(adjMat[v][j].exist==true && adjMat[v][j].Visited==false)
		  return j;
	  return -1;
      }  // end getUnvisitedAdjBFS()

    public int getUnvisitedAdjBFS2(int v)
      {
	  for(int j=0; j<nVerts; j++)
	      if(adjMat[v][j].exist==true && verList[j].Visited==false)
		  return j;
	  return -1;
      }  // end getUnvisitedAdjBFS()
    ////////////////////////////////////////////////////////////////
    // Check Condition Method
    ////////////////////////////////////////////////////////////////
    public void condv(){
	for(int i=0;i<nConds;i++){
	    condList[i].judgeCondition();
	}
    }
    ////////////////////////////////////////////////////////////////
    //  Utility
    ////////////////////////////////////////////////////////////////
    // Return No of index VertexList
    public int verNo(String label){
	for(int i=0;i< nVerts;i++){
	    if(verList[i].label.equals(label))
		return i;
	}
	return -1; // label missing
    }
    // Return true or false   value in array?
    public boolean isValue(int[] array,int value){
	for(int i=0;i<array.length;i++){
	    if(array[i] == value)
		return true;
	}
	return false;
    }
    // Return index value in index
    public int isIndex(int[] array,int value){
	for(int i=0;i<array.length;i++){
	    if(array[i] == value)
		return i;
	}
	return -1;
    }
    ///////////////////////////////////////////////////
    // Output Method
    ///////////////////////////////////////////////////
    public void displayVertex(int v)
    {
	System.out.print(verList[v].label + " ");
    }
    public void displayBadVertex(){

	System.out.println("\nCan not reach to End:");
	for(int i=0;i<nVerts;i++){
	    if(verList[i].toEnd == false){
		if(!verList[i].label.equals("end"))
		    System.out.println(verList[i].label);
	    }
	}
	System.out.println("\nCan not reach from Start:");
	for(int i=0;i<nVerts;i++){
	    if(verList[i].fromStart == false)
		if(!verList[i].label.equals("start")){
		    System.out.println(verList[i].label);
		}
	}

	System.out.println("\nOutput graph No:");
	for(int i=0;i<nVerts;i++){
	    System.out.printf("%s is No : %d\n",verList[i].label,verList[i].graphNo);
	}
    }
    public void displayBadEdge(){

	System.out.println("Output Edges");

	for(int i=0;i<nVerts;i++){
	    for(int j=0;j<nVerts;j++){
		if(adjMat[i][j].exist){
		    System.out.print(verList[i].label + " -> " + 
				     verList[j].label + " Condition:");
		    if(adjMat[i][j].costState == 0)
			System.out.println("OK!");
		    if(adjMat[i][j].costState == 1)
			System.out.println("条件が不足しています");
		    if(adjMat[i][j].costState == 2)
			System.out.println("条件が重複しています");
		    if(adjMat[i][j].costState == 3)
			System.out.println("条件が不足かつ重複しています");
		}
	    }
	}
    }
    // X-Y coordinates calculation
    public void calCoordinates(Vertex[] outList,Edge[][] outMat)
    {
	int X=0,Y=1;  // horizontal:X vertical:Y Coordinate
	
	// bread-fisrt search
	int startIndex = verNo("start");
	verList[startIndex].Visited = true;  // mark it
	verList[startIndex].horizontal=X;    // set Coordinates
	verList[startIndex].vertical=Y;
	queue.insert(startIndex);            // insert at tail
	int v2;

	X++;
	while(!queue.isEmpty()){     // until queue empty,
	    int v1 = queue.remove();   // remove vertex at head
	    // until it has no unvisited neighbors
	    if( verList[v1].horizontal == X ){
		Y = 1;  // Y-coordinate initialize
		X = verList[v1].horizontal + 1;  // X-coordinate initialize
	    }
	    while( (v2=getUnvisitedAdjBFS2(v1)) != -1 ){
		verList[v2].Visited = true;         // mark it
		verList[v2].horizontal=X;   // set Coordinates
		verList[v2].vertical=Y;
		Y++;
		queue.insert(v2);            // insert it
	    }   // end while
	}  // end while(queue not empty)
	
	// queue is empty, so we're done
	for(int j=0; j<nVerts; j++)// reset flags
	    verList[j].Visited = false;

	// Copy to output verList and adjMat
	for(int i=0;i<nVerts;i++){
	    outList[i].label = verList[i].label;
	    outList[i].toEnd = verList[i].toEnd;
	    outList[i].horizontal = verList[i].horizontal;
	    outList[i].vertical = verList[i].vertical;
	    for(int j=0;j<nVerts;j++){
		outMat[i][j].exist = adjMat[i][j].exist;
		outMat[i][j].costState = adjMat[i][j].costState;
		outMat[i][j].condition = adjMat[i][j].condition;
	    }
	}
    }
}  // end class Graph