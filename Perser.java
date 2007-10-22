// Perser Module For Java
// authed by konishi 2006/02/01

import java.io.*;
import java.util.regex.*;
import java.awt.*;

class Perser{
    Graph graph;
    File inputProlog,inputDG;
    
    // constructor
    public Perser(String inputFileName){
	if(inputFileName.indexOf(".dg") == -1)
	    inputProlog = new File(inputFileName);
	else{
	    inputDG = new File(inputFileName);
	    inputProlog = null;
	}
    }
    public Perser(File inputFileName){
	if(inputFileName.toString().indexOf(".dg") == -1)
	    inputProlog = inputFileName;
	else{
	    inputDG = inputFileName;
	    inputProlog = null;
	}
    }

    ///////////////////////////////////////////////////
    // ECA Rule Perser
    ///////////////////////////////////////////////////
    public Graph pers(){
	if(inputProlog != null){
	    String buffDG = analysis(inputProlog);
	    inputDG = new File(buffDG);
	}
	graph = new Graph();
	inputGraph(inputDG,graph);

	return graph;
    }

    ///////////////////////////////////////////////////
    // Prolog analysis pl -> dg   return of dg FileName
    ///////////////////////////////////////////////////    
    private String analysis(File inputProlog){
	String str;
	List verList = new List();
	List edgeList = new List();
	List[] costList = new List[100];
	int costNo=-1;

	// ListArray initialize
	for(int i=0;i<100;i++)
	    costList[i] = new List();

	// Regular expression
	// match of next("id1","id2",...
	String nextReg = "next\\(\\s*\"(.+?)\"\\s*,\\s*\"(.+?)\"";
	// match of conditions
	String testReg = "testMatch\\(.*\"(.+?)\"\\s*,\\s*\"(.+?)\"";
	String modReg = "(|not)\\s*moduleMember\\(LI,\"(.+?)\"\\)";

	Pattern nextPat = Pattern.compile(nextReg);
	Pattern testPat = Pattern.compile(testReg);
	Pattern modPat = Pattern.compile(modReg);

	// Prolog input
	try{
	    BufferedReader input = new BufferedReader(new FileReader(inputProlog));
	    
	    while((str = input.readLine()) != null){
		// next Matching
		Matcher nextMat = nextPat.matcher(str);
		if(nextMat.find()){
		    System.out.println(str);
		    // vertex input
		    for(int i=1;i<=nextMat.groupCount();i++){
			if(!memberList(verList,nextMat.group(i))){
			    //System.out.println(nextMat.group(i));
			    verList.add(nextMat.group(i));
			}
		    }
		    // Edge input
		    String buffEdge = nextMat.group(1) + " " + nextMat.group(2);
		    if(!memberList(edgeList,buffEdge)){
			costNo++;
			edgeList.add(buffEdge);
		    }
		}

		// Condition Matching
		// testMatch
		/*Matcher testMat = testPat.matcher(str2[1]);
		if(testMat.find()){
		    for(int i=1;i<=testMat.groupCount();i++){
			System.out.println(" test:" + testMat.group(i));
		    }
		}
		*/
		// moduelMember
		Matcher modMat = modPat.matcher(str);
		StringBuffer modBuff2 = new StringBuffer();
		while(modMat.find()){
		    StringBuffer modBuff = new StringBuffer();
		    modBuff.append("Node:");
		    for(int i=1;i<=modMat.groupCount();i++){
			if(modMat.group(i).equals("not"))
			    modBuff.append("!");
			else
			    modBuff.append(modMat.group(i));
		    }
		    modBuff2.append(modBuff + " ");
		}
		if(modBuff2.length() >= 1){
		    costList[costNo].add(modBuff2.toString());
		    System.out.println(" Module:" + modBuff2.toString());
		}
	    }
	    input.close();
	}catch(Exception e){
	    System.out.println("Error Message!!" + e.getMessage());
	}

	// Directed Graph output
	StringBuffer buffPath = new StringBuffer();
	String outputPathName;
	
	// pl -> dg
	buffPath.append(inputProlog.toString()).toString();
	buffPath.replace(buffPath.indexOf(".pl"),buffPath.length(),".dg");
	outputPathName = buffPath.toString();
	try{
	    BufferedWriter output = new BufferedWriter(new FileWriter(outputPathName));
	    
	    output.write("Vertex:\n");
	    for(int i=0;i<verList.getItemCount();i++){
		output.write(verList.getItem(i) + " ");
	    }
	    output.write("\nEdge:\n");
	    for(int i=0;i<edgeList.getItemCount();i++){
		output.write(edgeList.getItem(i));
		output.write("\nCost:\n");
		for(int j=0;j<costList[i].getItemCount();j++){
		    output.write(costList[i].getItem(j) + "\n");
		}
		output.write("CostEnd:\n");
	    }
	    output.close();
	}catch(Exception e){
	    System.out.println(e.getMessage());
	}

	return outputPathName;
    }

    ///////////////////////////////////////////////////
    // Input Graph File dg -> Graph class
    ///////////////////////////////////////////////////
    private void inputGraph(File inputFileName,Graph graph){
	String str;
	String[] buffVertex,buffEdge;

	int orNo;
	String[][] buffCondition = new String[50][];

	try{
	    BufferedReader input = new BufferedReader(new FileReader(inputFileName));
	    
	    while((str = input.readLine()) != null){
		// Vertex Class ê∂ê¨
		if(str.equals("Vertex:")){
		    str = input.readLine();
		    buffVertex = str.split(" ");
		    graph.addVertex(buffVertex);    // add Vertex
		    for(int i=0;i<buffVertex.length;i++)
			System.out.println(buffVertex[i]);
		}
		// Edge Class ê∂ê¨
		if(str.equals("Edge:")){		    
		    while((str = input.readLine()) != null){
			buffEdge = str.split(" ");

			// input Condition
			orNo = 0;
			str = input.readLine();
			if(str.equals("Cost:")){
			    while(!(str = input.readLine()).equals("CostEnd:")){
				buffCondition[orNo] = str.split(" ");
				orNo++;
			    }
			}

			if(orNo == 0){  // No Condition
			    graph.addEdge(graph.verNo(buffEdge[0]),  // From Vertex
					  graph.verNo(buffEdge[1])); // To Vertex
			}else{  // Condition exist
			    graph.addEdge(graph.verNo(buffEdge[0]),  // From Vertex
					  graph.verNo(buffEdge[1]),  // To Vertex
					  orNo,                      // or No
					  buffCondition);            // Condition
			}
		    }
		}
	    }
	    input.close();
	}catch(Exception e){
	    System.out.println(e.getMessage());
	}
    }

    /*** Utility ***/
    private boolean memberList(List list,String item){
	for(int i=0;i<list.getItemCount();i++){
	    if(list.getItem(i).equals(item))
		return true;
	}
	return false;
    }
}
