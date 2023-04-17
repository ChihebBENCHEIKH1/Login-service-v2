package com.spring.social.model;


public class Student {

    public Student(Long id, String name, String age) {
		super();
		this.id = id;
		this.name = name;
		this.age = age;
	}

	private Long id;

    private String name;

    private String age;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

}
