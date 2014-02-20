package com.taobao.top.pacman;

import java.util.ArrayList;
import java.util.List;

public class ActivityMembers {
	private int lastId;
	private List<Activity> members;

	private ActivityMembers parent;
	private int parentId;

	public ActivityMembers() {
	}

	public ActivityMembers(ActivityMembers parent, int parentId) {
		this.parent = parent;
		this.parentId = parentId;
	}

	public ActivityMembers getParent() {
		return parent;
	}

	public int getParentId() {
		return parentId;
	}

	public int size() {
		return this.members == null ? 0 : this.members.size();
	}

	public Activity getOwner() {
		return this.parent != null ? this.parent.get(this.parentId) : null;
	}

	public Activity get(int id) {
		return this.members != null &&
				id > 0 &&
				id <= this.members.size() ?
				this.members.get(id - 1) : null;
	}

	public void add(Activity member) {
		if (this.members == null)
			this.members = new ArrayList<Activity>();
		this.lastId++;
		member.setId(this.lastId);
		this.members.add(member);
	}
}
