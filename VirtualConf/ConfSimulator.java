package VirtualConf;

import java.awt.Color;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;

class Node {
    int id;                           // id for drawing.
    int x,y;                          // x,y location on GUI.
    int role; 	 // 1 attendant, 2 presenter, 3 leader
    int popular; // 1 attendant, 2 presenter, 3 leader
    Color color; 	 // 1 green, 2 yellow, 3 red 
    int session;
    int areaNum;
    int area [];
    int next;  	 // talk to next people
    LinkedList<Integer> groupList; 				//store id info
}

class Session {
	int id;
	int x,y;
	int area [];
	LinkedList<Integer> peopleList;
}
public class ConfSimulator {
	int confWidth = 680, confHeight = 260;
	int sessionWidth, sessionHeight;
	
	double prob = 1;
	int sessionTime = 30, talkTime = 10, resduTime = 30, confTime = 10*(sessionTime + talkTime);
    int numSession = 4, numPaper = 20, numArea = 12, numBreak = 3;
    int paperPerSession = 5, areaPerSession = 3;
    int attendant = 70, presenter = 20, leader = 10, numNodes = attendant + presenter + leader; 
    int attendantArea = 1, presenterArea = 2, leaderArea = 3;   
    
    LinkedList<Integer> attendantList, presenterList, leaderList; 
    Session sessionList[];
    int areaList[];
    
    int goRandVal = 1, goSameVal = 0, goDiffVal = 0; 
    int talkRandVal = 1, talkSameVal = 0, talkDiffVal = 0; 

    // Store node instances.
    Vector<Node> nodes;
    int numEdges = -1; 

    // Radius of circle to draw.
    int radius = 5;
    
    void initNode () {
    	nodes = new Vector<Node> ();
    	attendantList = new LinkedList<Integer> ();
    	presenterList = new LinkedList<Integer> ();
    	leaderList = new LinkedList<Integer> ();
    	
    	for (int i=0; i<numNodes; i++) {
   		 	Node node = new Node ();
   		 	node.id = i;
   		 	node.x = -1;
   		 	node.y = -1;
   		 	node.role = -1; 
   		 	node.popular = -1;
   		 	node.color = Color.black;
   		 	node.areaNum = -1;
   		 	node.session = -1;
   		 	node.next = -1;
   		 	node.groupList = new LinkedList<Integer> (); 
    	    nodes.add (node);
    	}
    }
    void assignRole(Node n) {
		 if (n.id < attendant){
	   	    n.role = 1; 
	   	    n.popular = 1;
	   	    n.color = Color.green; 
	   	    n.areaNum = attendantArea;
	   	    attendantList.add (n.id);
		 }
		 else if (n.id >= attendant && n.id < attendant + presenter){
			n.role = 2; 
    	    n.popular = 2;
    	    n.color = Color.yellow;
    	    n.areaNum = presenterArea;
    	    presenterList.add (n.id);
		 }
		 else {
			n.role = 3; 
	    	n.popular = 3;
	        n.color = Color.red;
	   	    n.areaNum = leaderArea;
	   	    leaderList.add (n.id);
		 }
    }	
    void initArea () {
    	areaList = new int[numArea];
    	for (int i=0, j=0; i<numArea; i++)
    		areaList[i] = -1;
    }
    void assignArea (Node n) {
    	int i, j, r;
    	n.area= new int[n.areaNum];
 	    for (i=0; i<n.areaNum; i++){
 	    	while(true) {
	 	    	r = (int) UniformRandom.uniform(0, numArea-1) ; 
	 	    	for(j=0; j<i; j++)
	 	    		if (n.area[j] == r)
	 	    			break;
	 	    	if (j == i) break;
 	    	}
 	    	n.area[i] = r;
 	    }
    }
    void initSession () {
    	//assign Area
    	sessionList = new Session[numSession];
    	for (int i=0, j=0; i<numSession; i++){
    		sessionList[i] = new Session();
    		sessionList[i].id = i;
    		sessionList[i].area = new int[areaPerSession];
    		for (int k=0; k<areaPerSession; k++){
    			sessionList[i].area[k] = j; //assignArea
        		areaList[j] = i;
    			j++;
    		}
    	}
    	//assign presenter
    	for (int i=0, j=0; i<numSession; i++){
    		sessionList[i].peopleList = new LinkedList();
    		for (int k=0; k<paperPerSession; k++) {
    			Node n = nodes.get(presenterList.get(j));
    			n.session = i;
    			sessionList[i].peopleList.add(n.id);
    			j++;
    		}
    	}
    }
    void assignSessionLocation (Session s){
    	sessionWidth = confWidth*2/numSession;
    	sessionHeight = confHeight/2;
    	int i;
    	for(i=0; i<(numSession+1)/2; i++) {
    		sessionList[i].x = i*sessionWidth;
    		sessionList[i].y = 0;
    	}
    	for(i=(numSession+1)/2; i<numSession; i++) {
    		sessionList[i].x = (i-numSession/2)*sessionWidth;
    		sessionList[i].y = sessionHeight;
    	}
    }
    
    int chooseSession (Node n){
    	int s = -1, i;
    	
    	if (n.role == 2) {//presenter
    		s = n.session;
    		return s;
    	}
    	else { //attendant or leader
    		if (goRandVal == 1){ //chooseSessionRandom
    			s = (int)UniformRandom.uniform(0, numSession-1) ;
    		}
    		else { // chooseSession base on rule
    			double p = UniformRandom.uniform(0, 1);
    			if (p > prob) //don't follow the rule
    				s = (int)UniformRandom.uniform(0, numSession-1) ;
    			else { //follow the rule
    				if (goSameVal == 1){
    					int r = (int)UniformRandom.uniform(0, n.areaNum-1) ;
    					s = areaList[n.area[r]];
    				}
    				else if (goDiffVal == 1){
    					while(true) {
    						int r = (int)UniformRandom.uniform(0, numArea-1);
    						for(i=0; i<n.areaNum; i++)
    							if (n.area[i] == r)
    								break;
    						if (i == n.areaNum) {
    							s = areaList[r];
    							break;
    						}
    					}
    				}
    			}
    		}
    	}
    	sessionList[s].peopleList.add(n.id);
    	n.session = s;
    	return s;
    }
    void assignNodeLocation (Node n, Session s) {
    	int lx, rx, uy, dy, x, y;
    	lx = s.x+radius;
    	rx = s.x+sessionWidth-radius;
    	uy = s.y+radius;
    	dy = s.y+sessionHeight-radius;
    	x = n.x = (int)UniformRandom.uniform(lx, rx);
    	y = n.y = (int)UniformRandom.uniform(uy, dy);
    }
    boolean samearea(Node a, Node b)
    {
    	int i, j;
    	for(i=0; i<a.areaNum; i++) {
    		for(j=0; j<b.areaNum; j++)
    			if (a.area[i] == b.area[j])
    				return true;
    	}
    	return false;
    }
    double distance (int x1, int y1, int x2, int y2)
    {
    	return Math.sqrt ( (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2) );
    }
    
    void choosePeople (Node n, Session s){
    	int id = -1, i, bestid = -1, j;
    	int num = s.peopleList.size();
    	Node target;
       	double mindis = Math.pow(10, 6.0), dis;
    	if (talkRandVal == 1) {
    		int m = 0;
    		while(m < numNodes) {
    			id = (int)UniformRandom.uniform(0, num-1) ;
    			Node tn = nodes.get(s.peopleList.get(id));
    			if (tn.next != -1) continue;
    			if (tn.id != n.id) break;
    			m ++;
    		}
    		if (m == numNodes) bestid = -1;
    		else bestid = s.peopleList.get(id);
    	}
    	else {
    		double p = UniformRandom.uniform(0.0, 1.0);
			if (p > prob) {//don't follow the rule
				int m = 0;
				while (m < numNodes) {
					id = (int)UniformRandom.uniform(0, num-1) ;
					Node tn = nodes.get(s.peopleList.get(id));
	    			if (tn.next != -1) continue;
	    			if (tn.id != n.id) break;
	    			m ++;
				}
				if (m == numNodes) bestid = -1;
	    		else bestid = s.peopleList.get(id);
			}
			else {
				if (talkSameVal == 1){
					for(i=0; i<num; i++) {
						id = s.peopleList.get(i);
						if (n.id == id) continue;
						for(j=0; j<n.groupList.size(); j++)
							if (id == n.groupList.get(j))
								break;
						if (j<n.groupList.size()) continue;
						target = nodes.get(id);
						if (target.next != -1) continue;
						if (!samearea(n, target)) continue;
						dis = distance(n.x, n.y, target.x, target.y);
						if (dis<mindis) {
							mindis = dis;
							bestid = id;
						}
					}
				}
				else if (talkDiffVal == 1){
					for(i=0; i<num; i++) {
						id = s.peopleList.get(i);
						if (n.id == id) continue;
						for(j=0; j<n.groupList.size(); j++)
							if (id == n.groupList.get(j))
								break;
						if (j<n.groupList.size()) continue;
						target = nodes.get(id);
						if (target.next != -1) continue;
						if (samearea(n, target)) continue;
						dis = distance(n.x, n.y, target.x, target.y);
						if (dis<mindis) {
							mindis = dis;
							bestid = id;
						}
					}
				}
			}
    	}
    	if (bestid != -1) {
	    	n.next = bestid;
	    	Node t = nodes.get(bestid);
	    	t.next = n.id;
	    	n.groupList.add(bestid);
    	}
    	else n.next = -1;
    }
    void moveTotalk (Node n1, Node n2, Session s){
    	int blx, brx, buy, bdy;
    	blx = s.x+radius;
    	brx = s.x-radius+sessionWidth;
    	buy = s.y+radius;
    	bdy = s.y-radius+sessionHeight;
    	while (true) {
	    	double theta = UniformRandom.uniform(0, Math.PI*2);
	    	int x, y;
	    	x = n1.x+(int)(2*radius*Math.cos(theta));
	    	y = n1.y+(int)(2*radius*Math.sin(theta));
	    	if (x>=blx && x<=brx && y>=buy && y<=bdy) {
	    		n2.x = x;
	    		n2.y = y;
	    		break;
	    	}
    	}
    }
    
    void init()
    {
    	int i;
    	sessionTime = resduTime = 30;
    	numEdges = 0;
    	initNode();
    	for(i=0; i<numNodes; i++)
    		assignRole(nodes.get(i));
    	initArea();
    	for(i=0; i<numNodes; i++)
    		assignArea(nodes.get(i));
    	initSession();
    	for(i=0; i<numSession; i++)
    		assignSessionLocation(sessionList[i]);
    	for(i=0; i<numNodes; i++) {
			Node n = nodes.get(i);
			n.session = chooseSession(n);
		}
    	for(i=0; i<numNodes; i++) {
    		Node n = nodes.get(i);
    		Session s = sessionList[n.session];
    		assignNodeLocation(n, s);
    	}
    }
    void nextstep()
    {
    	int i;
		if (resduTime>0) {
			for(i=0; i<numNodes; i++) {
				Node n = nodes.get(i);
				n.next = -1;
			}
			for(i=0; i<leaderList.size(); i++) {
				Node n = nodes.get(leaderList.get(i));
				Session s = sessionList[n.session];
				if (n.next < 0)
					choosePeople(n, s);
			}
			for(i=0; i<presenterList.size(); i++) {
				Node n = nodes.get(presenterList.get(i));
				Session s = sessionList[n.session];
				if (n.next < 0)
					choosePeople(n, s);
			}
			for(i=0; i<attendantList.size(); i++) {
				Node n = nodes.get(attendantList.get(i));
				Session s = sessionList[n.session];
				if (n.next < 0)
					choosePeople(n, s);
			}
			for(i=0; i<numNodes; i++) {
				Node n1, n2;
				n1 = nodes.get(i);
				if (n1.next == -1) continue;
				n2 = nodes.get(n1.next);
				Session s = sessionList[n1.session];
				n1.next = n2.next = -1;
				moveTotalk(n1, n2, s);
			}
			resduTime -= talkTime;
			numEdges = calcEdgeNum();
			if (resduTime<0) sessionTime = 0;
		}
		else {
			int j;
			for (i=0, j=0; i<numSession; i++){
				sessionList[i].peopleList.clear();
	    		for (int k=0; k<paperPerSession; k++) {
	    			Node n = nodes.get(presenterList.get(j));
	    			n.session = i;
	    			sessionList[i].peopleList.add(n.id);
	    			j++;
	    		}
	    	}
			for(i=0; i<numNodes; i++) {
				Node n = (Node)nodes.get(i);
				chooseSession(n);
			}
	    	for(i=0; i<numNodes; i++) {
	    		Node n = nodes.get(i);
	    		Session s = sessionList[n.session];
	    		assignNodeLocation(n, s);
	    	}
			resduTime = sessionTime;
		}
    }
    
    int getEdgeNum () {
    	return numEdges; 
    }
    
    int getNodeNum () {
    	return numNodes; 
    }
    
    int getDegree (int nodeID) {
    	int deg = 0;
    	//node.groupList.size();
    	return deg;
    }
    
    int calcEdgeNum()
    {
    	int r = 0, i, j, k, t;
    	for(i=0; i<numNodes; i++) {
    		Node n = nodes.get(i);
    		for(j = 0; j<n.groupList.size(); j++) {
    			t = n.groupList.get(j);
    			for(k=n.groupList.size()-1; k>j; k--)
    				if (n.groupList.get(k) == t)
    					n.groupList.remove(k);
    		}
    		r += n.groupList.size();
    	}
    	return r/2;
    }

    double calcAvgDegree () {
    	return (double)numEdges/numNodes;
    }
    
    void printNode () {
    	for (int i=0; i<numNodes; i++){	
    			Node n = (Node) nodes.get(i);
    			System.out.println("Node " + i  + " area:" + n.area.length +" session:" + n.session+" x:"+n.x+" y:"+n.y+" next "+n.next);
    	}
    }
    
    void printSession (){
    	for (int i=0; i<numSession; i++){
    		System.out.println("Session "+ i +" area:"); 		
    		System.out.println("#people List: " + sessionList[i].peopleList.size());
    		for(int j=0; j<sessionList[i].peopleList.size(); j++) {
    			Node n = nodes.get(sessionList[i].peopleList.get(j));
    			System.out.print(n.id+" ");
    		}
    		System.out.print("\n");
    	}
    }
}
