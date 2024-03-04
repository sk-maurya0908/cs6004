class Node {
	Node f;
	Node g;
}

public class Test {
	public static Node global = new Node();
	public static void main(String[] args) {
		foo();
	}
	public static Node foo(){
		Node x = new Node();
		x.f = new Node();
		x.f.g = new Node(); 
		Node y = new Node(); 
		Node z = new Node();
		y.f = z;
		bar(x.f, y);
		return y.f;
	}
	public static void bar(Node p1, Node p2){
		Node w = new Node();
		global.g = p1;
		w.f = new Node(); 
		p2.f = w.f;
		//return p1;		
	}
}
