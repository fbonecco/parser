package com.rocketinsights.core.tree;

/**
 * Auxiliary data structure that can be used while parsing properties files. It
 * builds a N-tree of nodes. Each level of a node represents a part on
 * properties'path. For example: given the property a.b.c and a.b.d, then this
 * tree will build the following structure:
 * 
 * a -> b -> c,d
 * 
 * @author fbonecco
 *
 */
public class PropertiesTree {

	private Node root;

	public PropertiesTree() {
		super();
	}

	public Node getRoot() {
		return this.root;
	}

	public void setRoot(Node root) {
		this.root = root;
	}

	/**
	 * Adds a node to this tree. In case there is a property-collision, the Node is
	 * not added and the method returns false. For example, given the property a.b.c
	 * is already stored, adding the property a.b will fail thus returning false.
	 * 
	 * @param node
	 * @return true if the node was added
	 */
	public boolean add(Node node) {
		return auxAdd(node, root);
	}

	private boolean auxAdd(Node node, Node parent) {
		if (!parent.getChildren().contains(node)) {
			parent.addChild(node);
			return true;
		} else {
			int i = parent.getChildren().indexOf(node);
			parent = parent.getChildren().get(i);
			if (!parent.hasChildren() && !node.hasChildren()) {
				return false;
			} else if (node.hasChildren()) {
				if (parent.hasChildren()) {
					return auxAdd(node.getChildren().get(0), parent);
				} else {
					return false;
				}
			} else if (parent.getChildren().size() != node.getChildren().size()) {
				return false;
			} else {
				return false;
			}
		}
	}
}
