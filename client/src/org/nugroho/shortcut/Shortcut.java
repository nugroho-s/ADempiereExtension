package org.nugroho.shortcut;

public class Shortcut {
	private String name;
	private String shortcut;
	public Shortcut(String name, String shortcut) {
		this.name = name;
		this.shortcut = shortcut;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getShortcut() {
		return shortcut;
	}
	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	}
	public String toString(){
		return this.name + "\t: " + this.shortcut;
	}
}
