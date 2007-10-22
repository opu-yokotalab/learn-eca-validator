class StackV
{
    private final int SIZE = 50;
    private Vertex[] st;
    private int top;
    public StackV()           // constructor
    {
	st = new Vertex[SIZE];    // make array
	top = -1;
    }
    public void push(Vertex j)   // put item on stack
    { st[++top] = j; }
    public Vertex pop()          // take item off stack
    { return st[top--]; }
    public Vertex peek()         // peek at top of stack
    { return st[top]; }
    public boolean isEmpty()  // true if nothing on stack
    { return (top == -1); }
    public Vertex[] retStack()  // return stack array pointer
    { return st; }
    public int retTop()
    { return top; }
    public void stackClear()  // stack initialize
    { top = -1; }
} // end class StackV