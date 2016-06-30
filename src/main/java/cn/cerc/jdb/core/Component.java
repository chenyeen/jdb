package cn.cerc.jdb.core;

import java.util.ArrayList;

public class Component {
	private ArrayList<Component> components = new ArrayList<Component>();
	private int tag;

	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public final void init(Component owner) {
		// 此函数专供后续对象覆盖使用
		if (owner != null) {
			owner.addComponent(this);
		}
	}

	private void addComponent(Component child) {
		this.components.add(child);
	}

	public ArrayList<Component> getComponents() {
		return components;
	}

	public int getComponentCount() {
		return components.size();
	}

}
