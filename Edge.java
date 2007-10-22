// class Edge
// 2005/12/11 authed by konishi

class Edge
{
    // Edge exist flag
    public boolean exist;
    // Edge visited
    public boolean Visited;
    public boolean toEnd;

    // Condition Variable
    // [[Node:a] AND [Node:b] AND [..]...] OR [[..] AND [...   ]] OR ..
    public String[][] condition;

    // Cost State
    // 0:OK 1:lack(条件不足) 2:overlap(条件重複) 3:両方 4:未チェック
    public int costState;

    // constructor
    public Edge(){
	toEnd = false;
	Visited = false;
	exist = false;
	costState = 4;
    }
}  // end class Edge