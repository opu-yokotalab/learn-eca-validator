/// Seaquence Validater Program For Java (ProtType)
// 2005/12/11 authed by konishi
 
import java.io.*;
import java.awt.*;
import java.util.regex.*;

public class Validater 
{
    public static void main(String[] args)
    {
	int n;

	Perser per = new Perser(args[0]);
	Graph graph = new Graph();

	if(args.length > 1)
	    n = Integer.parseInt(args[1]);
	else
	    n = -1;

	// persing
	graph = per.pers();

	System.out.println("\n Search Start!");

	switch(n){
	case 1:
	    // Graph Validation on Warshall method
	    System.out.print("Warshall method: ");
	    graph.warshall();
	    graph.displayBadVertex();  // output Bad Vertices
	    break;
	    
	case 2:
	    // Cost Validation
	    graph.bfs();  // bread-first search
	    graph.condv();
	    graph.displayBadEdge();
	    break;
	case 3:
	    // Graph Validation 2nd
	    System.out.print("DFS 2nd: ");
	    graph.dfs2();
	    System.out.print("\n");
	    graph.displayBadVertex();
	default :
	}
    }  // end main()
}
