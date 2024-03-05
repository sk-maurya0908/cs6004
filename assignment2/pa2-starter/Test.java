
class Node {
	Node f;
	Node g;
	void func(){
		g = new Node();
	}
}


public class Test {
	public static Node global = new Node();
	Node memNode;
	void memNodeInit(){
		memNode = new Node();
	}

	public static void main(String[] args) {
		foo();
	}
	public static Node foo(){
		Node x = new Node();
		x.f = new Node();
		x.f.g = new Node(); 
		Node y = new Node(); 
		Node z = new Node();
		z.func();
		y.f = z;
		bar(x.f, y);
		return y.f;
	}
	public static void bar(Node p1, Node p2){
		Node w = new Node();
		w.f = new Node();
		Integer a = new Integer(10);
		if(a < 20){
			global.g = p2;
		}
		p1.f = w.f;
	}
}
