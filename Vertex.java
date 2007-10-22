// class Vertex
// 2005/12/11 authed by konishi

class Vertex
{
    public String label;        // label (e.g. 'A')
    public boolean Visited;
    public int graphNo;         // separate graph No
    public boolean fromStart;   // reach from start flag
    public boolean toEnd;       // reach to end flag
    

    public int horizontal,vertical;  // horizontal:X vertical:Y coordinates

    public Vertex(){
	// no progress
    }

    public Vertex(String lab)   // constructor
    {
	graphNo = 0;            // separate graph No
	label = lab;            // Vertex label
	Visited = false;        // Visited flag
	fromStart = false;      // can reach from start Vertex flag
	toEnd = false;          // can reach to end Vertex flag
    }
    
}  // end class Vertex