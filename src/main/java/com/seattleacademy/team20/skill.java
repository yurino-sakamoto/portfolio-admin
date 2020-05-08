package com.seattleacademy.team20;

public class skill{

	private String name;
	private int score;
	private String category;

	public skill(String name, String category, int score) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.name=name;
		this.score=score;
		this.category=category;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
}