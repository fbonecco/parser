package com.rocketinsights.core.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Node {
	private String value;
	private List<Node> children;
	private Node parent;

	public Node() {
		super();
		children = new ArrayList<>();
	}

	public Node(String value) {
		this();
		this.value = value;
	}

	public Node getParent() {
		return this.parent;
	}

	public List<Node> getChildren() {
		return this.children;
	}

	public int getNumberOfChildren() {
		return getChildren().size();
	}

	public boolean hasChildren() {
		return (getNumberOfChildren() > 0);
	}

	public void addChild(Node child) {
		child.parent = this;
		children.add(child);
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public boolean equals(Object o) {

		if (o == null)
			return false;
		if (this == o)
			return true;
		if (!o.getClass().equals(getClass()))
			return false;
		Node obj = (Node) o;
		return Objects.equals(value, obj.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

}
