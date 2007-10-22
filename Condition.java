// class Condition
// authed by konishi 2006/01/23
import java.util.*;
import java.util.regex.*;

class Condition{
    public Edge[] toList;
    
    private int[] judgeTable;
    private String[] costMat;
    private Hashtable costHash;
    private String[] buffPattern;

    // constructor
    public Condition(int edgeMax,Edge[] buffEdge){
	int costHashValue = 0; // Hash Value initialize コスト最大桁数
	int rowNum;  // table 要素数

	StringBuffer[] buffCostMat = new StringBuffer[2];

	// Hash Table Initialize
	costHash = new Hashtable();

	// input Edge
	toList = new Edge[edgeMax];
	for(int i=0;i<edgeMax;i++)
	    toList[i] = buffEdge[i];

	// costHash Key Mapping
	costHashValue = keyMapping(costHashValue);
	rowNum = (int)Math.pow(2.0,(double)costHashValue);

	// create buffPattern
	buffPattern = new String[costHashValue];

	// create judgeTable
	System.out.println("rowNum:"+rowNum);
	judgeTable = new int[rowNum];

	// create costMatrix
	costMat = new String[rowNum];
	for(int i=0;i<costMat.length;i++){
	    for(int n=0;n<2;n++)  // buffCostMat initialize
		buffCostMat[n] = new StringBuffer();
	    buffCostMat[0].append(Integer.toBinaryString(i));
	    for(int j=0;j<costHashValue - buffCostMat[0].length();j++)
		buffCostMat[1].append("0");
	    costMat[i] = buffCostMat[1].append(buffCostMat[0]).toString();
	}

	// output
	for(int i=0;i<costMat.length;i++)
	    System.out.println(costMat[i]);
    }

    // costHash Key Mapping
    private int keyMapping(int costHashValue){
	
	for(int i=0;i<toList.length;i++){
	    if(toList[i].condition != null){
		for(int j=0;j<toList[i].condition.length;j++){
		    for(int k=0;k<toList[i].condition[j].length;k++){
			// Conditionの種類の判定
			String[] buffCost = toList[i].condition[j][k].split(":");

			// Condition がNode のとき
			if(buffCost[0].equals("Node")){
			    if(buffCost[1].charAt(0) == '!'){ // not 符号有りのとき
				if(!costHash.containsKey(buffCost[1].substring(1))){
				    costHash.put(buffCost[1].substring(1),
						 new Integer(costHashValue));
				    costHashValue++;
				}
			    }else{  // not 符号無しのとき
				if(!costHash.containsKey(buffCost[1])){
				    costHash.put(buffCost[1],
						 new Integer(costHashValue));
				    costHashValue++;
				}
			    }
			// ConditionがTestのとき
			}else if(buffCost[0].equals("Test")){
			    // testCondition 0:testKey 1:testPattern
			    String[] testCondition = buffCost[1].split(",");

			    if(testCondition[0].charAt(0) == '!'){ // not 符号有りのとき
				if(!costHash.containsKey(testCondition[0].substring(1))){
				    costHash.put(testCondition[0].substring(1),
						 new Integer(costHashValue));
				    costHashValue += testCondition[1].length();
				}
			    }else{  // not 符号無しのとき
				if(!costHash.containsKey(testCondition[0])){
				    costHash.put(testCondition[0],
						 new Integer(costHashValue));
				    costHashValue += testCondition[1].length();
				}
			    }
			}else if(buffCost[0].equals("Level")){}	
		    }
		}
	    }
	}
	
	// output Hash
	System.out.println();
	for (Enumeration outHash = costHash.keys() ; outHash.hasMoreElements() ;) {
	    String key = (String)outHash.nextElement();
	    System.out.println("Key:" + key + " Value:" + costHash.get(key).toString());
	}
	return costHashValue;
    }

    // Judge Condition
    public void judgeCondition(){
	boolean lack = false;
	boolean overlap =false;
	
	// JudgeTable initialize
	for(int i=0;i<judgeTable.length;i++)
	    judgeTable[i] = 0;
	
	for(int i=0;i<toList.length;i++){
	    if(toList[i].condition != null){  // 条件有りの場合
		for(int j=0;j<toList[i].condition.length;j++){
		    
		    //buffPattern initialize
		    for(int init=0;init<buffPattern.length;init++)
			buffPattern[init] = ".";
		    
		    for(int k=0;k<toList[i].condition[j].length;k++){
			// AND progress
			// Conditionの種類の判定
			String[] buffCost = toList[i].condition[j][k].split(":");

			// Condition がNode のとき
			if(buffCost[0].equals("Node")){
			    if(buffCost[1].charAt(0) == '!'){ // not 符号有りのとき
				String key = buffCost[1].substring(1);
				Integer digit = (Integer)costHash.get(key);
				buffPattern[digit.intValue()] = "0";
			    }else{  // not 符号無しのとき
				String key = buffCost[1];
				Integer digit = (Integer)costHash.get(key);
				buffPattern[digit.intValue()] = "1";
			    }
			// ConditionがTestのとき
			}else if(buffCost[0].equals("Test")){
			    String[] testCondition = buffCost[1].split(",");

			    if(testCondition[0].charAt(0) == '!'){ // not 符号有りのとき
				char[] testPattern = testCondition[1].toCharArray();
				// Pattern 反転
				for(int l=0;l<testPattern.length;l++){
				    if(testPattern[l] == '1')
					testPattern[l] = '0';
				    else if(testPattern[l] == '0')
					testPattern[l] = '1';
				}
				// create regular expression String
				StringBuffer[] buffTestPattern = new StringBuffer[testPattern.length];
				for(int l=0;l<testPattern.length;l++){
				    buffTestPattern[l] = new StringBuffer();
				    for(int m=0;m<testPattern.length;m++){
					if(l == m){
					    buffTestPattern[l].append(testPattern[l]);
					}else{
					    buffTestPattern[l].append(".");
					}
				    }
				}				
				// input to buffPattern
				String key = testCondition[0].substring(1);
				Integer digit = (Integer)costHash.get(key);
				for(int l=0;l<testPattern.length;l++){
				    if(l == 0){
					buffTestPattern[l].insert(0,'(');
					buffPattern[digit.intValue()+l] = buffTestPattern[l].toString();
				    }else if(l == testPattern.length-1){
					buffTestPattern[l].insert(0,'|');
					buffTestPattern[l].append(")");
					buffPattern[digit.intValue()+l] = buffTestPattern[l].toString();
				    }else{
					buffTestPattern[l].insert(0,'|');
					buffPattern[digit.intValue()+l] = buffTestPattern[l].toString();
				    }
				}
			    }else{  // not 符号無しのとき
				char[] testPattern = testCondition[1].toCharArray();

				String key = testCondition[0];
				Integer digit = (Integer)costHash.get(key);
				for(int l=0;l<testPattern.length;l++){
				    buffPattern[digit.intValue()+l] = Character.toString(testPattern[l]);
				}
			    }
			}else if(buffCost[0].equals("Level")){}			
		    }
		    // Cost Pattern Matching
		    // create Regular expression
		    StringBuffer buffReg = new StringBuffer();
		    for(int k=0;k<buffPattern.length;k++)
			buffReg.append(buffPattern[k]);
		    String regPattern = buffReg.toString();
                    System.out.println("Pattern:" + regPattern);
                  
		    Pattern p = Pattern.compile(regPattern);  // create Pattern
		    for(int k=0;k<costMat.length;k++){
			Matcher m = p.matcher(costMat[k]);
			// Match progress
			if(m.matches()){
			    judgeTable[Integer.parseInt(costMat[k],2)] += 1;
			}
                    }
		}
	    }else{  // 条件無しの場合
		for(int j=0;j<judgeTable.length;j++)
		    judgeTable[j]++;
	    }
	}

	// Edge State dicigion
	for(int i=0;i<judgeTable.length;i++){
	    if(judgeTable[i] == 0)
		lack = true;  // lack flag on
	    else if(judgeTable[i] >= 2)
		overlap = true; // overlap on
          System.out.println("JudgeTable[" + i + "] = " + judgeTable[i]);
	}
	for(int i=0;i<toList.length;i++){
	    if( lack && overlap)
		toList[i].costState = 3;
	    else if( lack )
		toList[i].costState = 1;
	    else if( overlap )
		toList[i].costState = 2;
	    else
		toList[i].costState = 0;
	}
    }


}